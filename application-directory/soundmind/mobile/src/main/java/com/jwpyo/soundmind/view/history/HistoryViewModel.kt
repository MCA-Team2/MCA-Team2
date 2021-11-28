package com.jwpyo.soundmind.view.history

import android.util.Log
import androidx.lifecycle.*
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayInputStream


@Suppress("JoinDeclarationAndAssignment")
class HistoryViewModel(
    private val voiceRepository: VoiceRepository,
    private val stressRepository: StressRepository,
) : ViewModel() {
    val historyDate: Flow<LocalDate>
    val volume: Flow<Flow<List<VolumeItem>>>
    val stress: Flow<Flow<List<Stress>>>

    val hourScope: Flow<Pair<Int, Int>>

    val historyDateLiveData: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    val volumeChartInfo: MutableLiveData<List<VolumeItem>> = MutableLiveData()
    val stressChartInfo: MutableLiveData<List<Stress>> = MutableLiveData()

    val audioStartPosition: MutableLiveData<LocalTime>
    val audioEndPosition: MutableLiveData<LocalTime>
    val currentPosition: MutableLiveData<LocalTime>
    val audioIsPlaying: MutableLiveData<Boolean>
    val audioScopeRange: LiveData<String>

    val getAudioByteArrayLiveDate: LiveData<() -> ByteArray>

    val soundPlayer: SoundPlayer = SoundPlayer()
    var playingJob: Job? = null

    fun play() {
        audioIsPlaying.postValue(true)
        playingJob = CoroutineScope(Dispatchers.IO).launch {
            val byteArray = getAudioByteArrayLiveDate.value?.let { it() }
            Log.e("hello", "hello $byteArray")
            if (byteArray != null) {
                val inputStream = ByteArrayInputStream(byteArray)
                soundPlayer.play(inputStream)
                stop()
            }
        }
        playingJob?.invokeOnCompletion {
            audioIsPlaying.postValue(false)
        }
    }

    fun stop() {
        audioIsPlaying.postValue(false)
        playingJob?.cancel()
        playingJob = null
    }

    init {
        historyDate = historyDateLiveData.asFlow()

        volume = historyDate.map { date ->
            voiceRepository.getVoices(date).map { voiceList ->
                voiceList.map { VolumeItem(it.startLDT, it.endLDT, it.volume) }
            }
        }

        stress = historyDate.map { date ->
            stressRepository.getStresses(date)
        }

        hourScope = combine(
            volumeChartInfo.asFlow(), stressChartInfo.asFlow()
        ) { vChart, sChart ->
            val s1 = sChart.firstOrNull()?.ldt
            val s2 = vChart.firstOrNull()?.startLDT
            val e1 = sChart.lastOrNull()?.ldt
            val e2 = vChart.lastOrNull()?.endLDT

            when {
                s1 != null && s2 != null && e1 != null && e2 != null ->
                    Pair(Integer.min(s1.hour, s2.hour), Integer.max(e1.hour, e2.hour) + 1)
                else -> Pair(0, 24)
            }
        }

        val now = LocalTime.now()
        audioStartPosition = MutableLiveData(now.minusMinutes(now.minute.toLong()))
        audioEndPosition = MutableLiveData(now.minusMinutes(now.minute.toLong()).plusHours(1L))
        currentPosition = MutableLiveData()
        audioIsPlaying = MutableLiveData(false)

        audioScopeRange = combine(
            audioStartPosition.asFlow(), audioEndPosition.asFlow()
        ) { start, end ->
            val sTime = start.format(DateTimeFormatter.ofPattern("HH:mm"))
            val eTime = end.format(DateTimeFormatter.ofPattern("HH:mm"))
            "$sTime - $eTime"
        }.asLiveData()

        getAudioByteArrayLiveDate = combine(
            historyDate, audioStartPosition.asFlow(), audioEndPosition.asFlow()
        ) { date, sTime, eTime ->
            stop()
            return@combine {
                val voiceList = voiceRepository.getVoices(date.atTime(sTime), date.atTime(eTime))
                var merged = ByteArray(0)
                voiceList.onEach { voice ->
                    merged += voice.array
                }
                merged
            }
        }.asLiveData()
    }
}
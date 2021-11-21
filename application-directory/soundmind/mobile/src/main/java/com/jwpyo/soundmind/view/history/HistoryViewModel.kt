package com.jwpyo.soundmind.view.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


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

        audioStartPosition = MutableLiveData()
        audioEndPosition = MutableLiveData()
        currentPosition = MutableLiveData()
    }
}
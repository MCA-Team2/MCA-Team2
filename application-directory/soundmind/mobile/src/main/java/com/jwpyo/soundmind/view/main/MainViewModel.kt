package com.jwpyo.soundmind.view.main

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.getVolume
import com.jwpyo.soundmind.utils.getSTT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Integer.max
import java.lang.Integer.min

@Suppress("JoinDeclarationAndAssignment")
class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository,
    private val stressRepository: StressRepository,
    private val context: Context,
) : ViewModel() {
    val historyDate: Flow<LocalDate>
    val volume: Flow<Flow<List<VolumeItem>>>
    val stress: Flow<Flow<List<Stress>>>

    val hourScope: Flow<Pair<Int, Int>>

    val historyDateLiveData: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    val volumeChartInfo: MutableLiveData<List<VolumeItem>> = MutableLiveData()
    val stressChartInfo: MutableLiveData<List<Stress>> = MutableLiveData()

    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            Log.d("Data", "Voice Data Received ")
            val array = dataClient.getByteArrayFromAsset(asset)
            voiceRepository.insertVoice(
                Voice(startLDT, endLDT, array, getSTT(array), getVolume(array))
            )
        }
    }

    fun insertStress(stress: Stress): Long {
        return stressRepository.insertStress(stress)
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
                    Pair(min(s1.hour, s2.hour), max(e1.hour, e2.hour) + 1)
                else -> Pair(0, 24)
            }
        }
    }
}
package com.jwpyo.soundmind.view.main

import androidx.lifecycle.*
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.model.ppg.PPG.Companion.HEART_RATE_PPG_RAW_DATA
import com.jwpyo.soundmind.model.ui.StressItem
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.PPGRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.view.history.StressLineChart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@Suppress("JoinDeclarationAndAssignment")
class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository,
    private val ppgRepository: PPGRepository,
) : ViewModel() {
    val ppgLiveData: LiveData<List<PPG>>

    val historyDate: Flow<LocalDate>
    val ppg: Flow<List<PPG>>
    val stress: Flow<List<StressItem>>

    val historyDateLiveData: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            voiceRepository.insertVoice(
                Voice(startLDT, endLDT, dataClient.getByteArrayFromAsset(asset))
            )
        }
    }

    fun insertPPG(ppgList: List<PPG>) {
        viewModelScope.launch {
            ppgRepository.insertPPG(ppgList)
        }
    }

    init {
        historyDate = historyDateLiveData.asFlow()

        ppg = combine(
            ppgRepository.getPPGs(), historyDate
        ) { ppgList, date ->
            ppgList.filter { it.sensorName == HEART_RATE_PPG_RAW_DATA && it.ldt.toLocalDate() == date }
        }

        stress = ppg.map { ppgList ->
            //TODO : calc. ppg -> stress
            StressLineChart.defaultInfo
        }

        ppgLiveData = ppg.asLiveData()
    }
}
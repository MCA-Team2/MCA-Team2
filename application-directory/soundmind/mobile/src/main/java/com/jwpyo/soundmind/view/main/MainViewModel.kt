package com.jwpyo.soundmind.view.main

import androidx.lifecycle.*
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.model.ppg.PPG.Companion.HEART_RATE_PPG_RAW_DATA
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.PPGRepository
import com.jwpyo.soundmind.repository.VoiceRepository
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
    val historyDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    val heartRatePPGRawData: LiveData<List<PPG>>

    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            voiceRepository.insertVoice(
                Voice(
                    null,
                    startLDT,
                    endLDT,
                    LocalDateTime.now(),
                    dataClient.getByteArrayFromAsset(asset)
                )
            )
        }
    }

    fun insertPPG(ppgList: List<PPG>) {
        viewModelScope.launch {
            ppgRepository.insertPPG(ppgList)
        }
    }

    init {
        heartRatePPGRawData = ppgRepository.getPPGs().map { ppgList ->
            ppgList.filter { it.sensorName == HEART_RATE_PPG_RAW_DATA }
        }.asLiveData()
    }
}
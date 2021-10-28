package com.jwpyo.soundmind.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.PPGRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository,
    private val ppgRepository: PPGRepository,
) : ViewModel() {
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
}
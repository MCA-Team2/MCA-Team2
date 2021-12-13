package com.jwpyo.soundmind.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.getVolume
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@Suppress("JoinDeclarationAndAssignment")
class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository,
    private val stressRepository: StressRepository,
) : ViewModel() {
    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            val array = dataClient.getByteArrayFromAsset(asset)
            voiceRepository.insertVoice(
               // Voice(startLDT, endLDT, array, getSTT(array), getVolume(array), checkBell(this, array))
                Voice(startLDT, endLDT, array, "", getVolume(array), true)
            )
        }
    }

    fun insertStress(stress: Stress): Long {
        return stressRepository.insertStress(stress)
    }
}
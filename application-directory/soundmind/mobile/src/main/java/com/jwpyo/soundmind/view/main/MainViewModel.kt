package com.jwpyo.soundmind.view.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.getSTT
import com.jwpyo.soundmind.utils.getVolume
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@Suppress("JoinDeclarationAndAssignment")
class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository,
    private val stressRepository: StressRepository,
) : ViewModel() {
    val VOLUME_THRESHOLD = 13.5f

    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            val array = dataClient.getByteArrayFromAsset(asset)

            val volume = getVolume(array)
            Log.d("STT", "Volume of the file: " + volume.toString())

            var text = ""
            if(volume >= VOLUME_THRESHOLD)
                text = getSTT(array)

            voiceRepository.insertVoice(
                Voice(startLDT, endLDT, array, text, volume, true)
            )
        }
    }

    fun insertStress(stress: Stress): Long {
        return stressRepository.insertStress(stress)
    }
}
package com.jwpyo.soundmind.view.main

import androidx.lifecycle.*
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.ui.VoiceItem
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.VoiceRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

class MainViewModel(
    private val dataClient: DataClient,
    private val voiceRepository: VoiceRepository
) : ViewModel() {
    val voiceItems: LiveData<List<VoiceItem>> =
        voiceRepository.getVoices().map {
            it.map { voice -> VoiceItem(voice) }
        }.asLiveData()

    val isPlayingMap: MutableMap<Long, MutableLiveData<Boolean>> = mutableMapOf()

    fun insertVoice(asset: Asset, ldt: LocalDateTime) {
        viewModelScope.launch {
            voiceRepository.insertVoice(
                Voice(null, ldt, dataClient.getByteArrayFromAsset(asset))
            )
        }
    }

    fun deleteVoice(voice: Voice) {
        voiceRepository.deleteVoice(voice)
    }
}
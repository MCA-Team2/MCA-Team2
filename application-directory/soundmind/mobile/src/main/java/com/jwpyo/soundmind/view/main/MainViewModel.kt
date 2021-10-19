package com.jwpyo.soundmind.view.main

import androidx.lifecycle.*
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.ui.VoiceItem
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import java.io.ByteArrayInputStream

class MainViewModel(
    private val dataClient: DataClient,
    private val soundPlayer: SoundPlayer,
    private val voiceRepository: VoiceRepository
) : ViewModel() {
    val voiceItems: LiveData<List<VoiceItem>> =
        voiceRepository.getVoices().map {
            it.map { voice -> VoiceItem(voice) }
        }.asLiveData()

    private var playingJob: Job? = null
    var isPlaying: Long? = null

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

    fun deleteVoice(voice: Voice) {
        voiceRepository.deleteVoice(voice)
    }

    fun play(voiceItem: VoiceItem) {
        voiceItem.isPlaying.postValue(true)
        isPlaying = voiceItem.voice.id

        playingJob = CoroutineScope(Dispatchers.IO).launch {
            val byteArray = voiceItem.voice.array
            val inputStream = ByteArrayInputStream(byteArray)
            soundPlayer.play(inputStream)
            stop(voiceItem)
        }
        playingJob?.invokeOnCompletion {
            voiceItem.isPlaying.postValue(false)
        }
    }

    fun stop(voiceItem: VoiceItem) {
        if (voiceItem.voice.id == isPlaying) {
            voiceItem.isPlaying.postValue(false)

            playingJob?.cancel()
            playingJob = null
            isPlaying = null
        }
    }
}
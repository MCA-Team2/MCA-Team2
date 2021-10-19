package com.jwpyo.soundmind.model.ui

import androidx.lifecycle.MutableLiveData
import com.jwpyo.soundmind.model.voice.Voice

data class VoiceItem(
    val voice: Voice,
    val isPlaying: MutableLiveData<Boolean> = MutableLiveData(false)
)
package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.voice.Voice
import kotlinx.coroutines.flow.Flow

interface VoiceRepository {
    fun getVoices(): Flow<List<Voice>>
    fun insertVoice(voice: Voice): Long
    fun deleteVoice(voice: Voice)
}
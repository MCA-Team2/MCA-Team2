package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.persistence.dao.VoiceDao
import kotlinx.coroutines.flow.Flow

class VoiceRepositoryImpl(
    private val voiceDao: VoiceDao
) : VoiceRepository {
    override fun getVoices(): Flow<List<Voice>> {
        return voiceDao.getVoices()
    }

    override fun insertVoice(voice: Voice): Long {
        return voiceDao.insert(voice)
    }

    override fun deleteVoice(voice: Voice) {
        return voiceDao.delete(voice)
    }
}
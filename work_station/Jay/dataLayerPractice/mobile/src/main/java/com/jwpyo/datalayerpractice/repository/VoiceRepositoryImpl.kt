package com.jwpyo.datalayerpractice.repository

import com.jwpyo.datalayerpractice.model.voice.Voice
import com.jwpyo.datalayerpractice.persistence.dao.VoiceDao
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
package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.persistence.dao.VoiceDao
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class VoiceRepositoryImpl(
    private val voiceDao: VoiceDao
) : VoiceRepository {
    override fun getVoices(): Flow<List<Voice>> {
        return voiceDao.getVoices()
    }

    override fun getVoices(date: LocalDate): Flow<List<Voice>> {
        return voiceDao.getVoices(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        )
    }

    override fun insertVoice(voice: Voice): Long {
        return voiceDao.insert(voice)
    }

    override fun deleteVoice(voice: Voice) {
        return voiceDao.delete(voice)
    }
}
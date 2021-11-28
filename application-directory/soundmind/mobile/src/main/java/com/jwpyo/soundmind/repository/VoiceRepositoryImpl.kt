package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.persistence.dao.VoiceDao
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class VoiceRepositoryImpl(
    private val voiceDao: VoiceDao
) : VoiceRepository {
    override fun getVoices(): Flow<List<Voice>> {
        return voiceDao.getVoicesFlow()
    }

    override fun getVoices(date: LocalDate): Flow<List<Voice>> {
        return voiceDao.getVoicesFlow(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        )
    }

    override fun getVoices(ldt1: LocalDateTime, ldt2: LocalDateTime): List<Voice> {
        return voiceDao.getVoices(ldt1, ldt2)
    }

    override fun insertVoice(voice: Voice): Long {
        return voiceDao.insert(voice)
    }

    override fun deleteVoice(voice: Voice) {
        return voiceDao.delete(voice)
    }
}
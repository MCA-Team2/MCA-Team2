package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.voice.Voice
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

interface VoiceRepository {
    fun getVoices(): Flow<List<Voice>>
    fun getVoices(date: LocalDate): Flow<List<Voice>>
    fun getVoices(ldt1: LocalDateTime, ldt2: LocalDateTime): List<Voice>
    fun insertVoice(voice: Voice): Long
    fun deleteVoice(voice: Voice)
}
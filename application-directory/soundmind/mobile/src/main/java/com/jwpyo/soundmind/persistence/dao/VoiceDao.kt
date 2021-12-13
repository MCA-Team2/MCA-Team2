package com.jwpyo.soundmind.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.voice.Voice
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime

@Dao
interface VoiceDao {
    @Insert
    fun insert(voice: Voice): Long

    @Delete
    fun delete(voice: Voice)

    @Query("SELECT * FROM Voice")
    fun getVoicesFlow(): Flow<List<Voice>>

    @Query("SELECT * FROM Voice WHERE startLDT > :ldt1 AND endLDT < :ldt2")
    fun getVoices(ldt1: LocalDateTime, ldt2: LocalDateTime): List<Voice>

    @Query("SELECT * FROM Voice WHERE startLDT > :ldt1 AND endLDT < :ldt2")
    fun getVoicesFlow(ldt1: LocalDateTime, ldt2: LocalDateTime): Flow<List<Voice>>
}

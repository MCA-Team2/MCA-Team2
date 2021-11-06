package com.jwpyo.soundmind.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jwpyo.soundmind.model.voice.Voice
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Insert
    fun insert(voice: Voice): Long

    @Delete
    fun delete(voice: Voice)

    @Query("SELECT * FROM Voice")
    fun getVoices(): Flow<List<Voice>>
}

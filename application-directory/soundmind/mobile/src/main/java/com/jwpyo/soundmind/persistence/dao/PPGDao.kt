package com.jwpyo.soundmind.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jwpyo.soundmind.model.ppg.PPG
import kotlinx.coroutines.flow.Flow

@Dao
interface PPGDao {
    @Insert
    fun insert(ppg: PPG): Long

    @Insert
    fun insert(ppg: List<PPG>)

    @Query("SELECT * FROM PPG")
    fun getPPGs(): Flow<List<PPG>>
}
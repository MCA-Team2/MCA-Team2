package com.jwpyo.soundmind.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jwpyo.soundmind.model.stress.Stress
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime

@Dao
interface StressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stress: Stress): Long

    @Query("SELECT * FROM Stress WHERE ldt BETWEEN :ldt1 AND :ldt2")
    fun getStresses(ldt1: LocalDateTime, ldt2: LocalDateTime): Flow<List<Stress>>
}
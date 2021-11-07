package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.stress.Stress
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface StressRepository {
    fun insertStress(stress: Stress): Long
    fun getStresses(date: LocalDate): Flow<List<Stress>>
}
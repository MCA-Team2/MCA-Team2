package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.persistence.dao.StressDao
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class StressRepositoryImpl(
    private val stressDao: StressDao
) : StressRepository {
    override fun insertStress(stress: Stress) {
        stressDao.insert(stress)
    }

    override fun getStresses(date: LocalDate): Flow<List<Stress>> {
        return stressDao.getStresses(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
    }
}
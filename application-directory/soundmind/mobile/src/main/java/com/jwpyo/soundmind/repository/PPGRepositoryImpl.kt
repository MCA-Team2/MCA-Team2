package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.persistence.dao.PPGDao
import kotlinx.coroutines.flow.Flow

class PPGRepositoryImpl(
    private val ppgDao: PPGDao
) : PPGRepository {
    override fun getPPGs(): Flow<List<PPG>> {
        return ppgDao.getPPGs()
    }

    override fun insertPPG(ppgList: List<PPG>) {
        ppgDao.insert(ppgList)
    }
}
package com.jwpyo.soundmind.repository

import com.jwpyo.soundmind.model.ppg.PPG
import kotlinx.coroutines.flow.Flow

interface PPGRepository {
    fun getPPGs(): Flow<List<PPG>>
    fun insertPPG(ppgList: List<PPG>)
}
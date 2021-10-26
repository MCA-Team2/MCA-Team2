package com.jwpyo.soundmind.model

data class PPG(
    val sensorValue: Float,
    val accuracy: Int,
    val timestamp: Long,
)
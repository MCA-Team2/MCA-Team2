package com.jwpyo.soundmind.model

import org.threeten.bp.LocalDateTime

data class PPG(
    val sensorValue: Float,
    val accuracy: Int,
    val ldt: LocalDateTime,
)
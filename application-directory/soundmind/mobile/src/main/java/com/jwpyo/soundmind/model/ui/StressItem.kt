package com.jwpyo.soundmind.model.ui

import java.time.LocalDateTime

data class StressItem(
    val ldt: LocalDateTime,
    val value: Float,
) {
    val x: Float get() = ldt.toLocalTime().toSecondOfDay().toFloat()
}
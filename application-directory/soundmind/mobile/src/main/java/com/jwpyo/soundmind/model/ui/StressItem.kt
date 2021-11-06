package com.jwpyo.soundmind.model.ui

import com.jwpyo.soundmind.extensions.clamp
import org.threeten.bp.LocalDateTime
import kotlin.math.log2


data class StressItem(
    val ldt: LocalDateTime,
    val value: Float,
) {
    val x: Float get() = ldt.toLocalTime().toSecondOfDay().toFloat() / 60 / 24
    val y: Float get() = log2(value.clamp(0.25f, 4f))
}
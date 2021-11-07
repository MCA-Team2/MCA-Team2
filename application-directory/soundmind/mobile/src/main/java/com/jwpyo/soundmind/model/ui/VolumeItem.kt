package com.jwpyo.soundmind.model.ui

import com.jwpyo.soundmind.extensions.clamp
import org.threeten.bp.LocalDateTime

data class VolumeItem(
    val startLDT: LocalDateTime,
    val endLDT: LocalDateTime,
    val value: Float?,
) {
    val y get(): Float? = value?.clamp(13f, 15f)?.minus(13f)?.div(2f)
}
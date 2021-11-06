package com.jwpyo.soundmind.model.ui

import org.threeten.bp.LocalDateTime

data class VolumeItem(
    val startLDT: LocalDateTime,
    val endLDT: LocalDateTime,
    val value: Float?,
)
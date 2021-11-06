package com.jwpyo.soundmind.model.ui

import org.threeten.bp.LocalDateTime

data class PPG(
    val sensorName: String,
    val sensorValue: Float,
    val accuracy: Int,
    val ldt: LocalDateTime,
) {
    companion object {
        const val HEART_RATE_PPG_RAW_DATA = "Heart Rate PPG Raw Data"
        const val HEART_RATE_HD_PPG_RAW_DATA = "Heart Rate HD PPG Raw Data"
    }
}
package com.jwpyo.soundmind.model.ppg

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class PPG(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val sensorName: String,
    val sensorValue: Float,
    val accuracy: Int,
    val timestamp: Long,
) : Parcelable {
    constructor(
        sensorName: String,
        sensorValue: Float,
        accuracy: Int,
        timestamp: Long
    ) : this(null, sensorName, sensorValue, accuracy, timestamp)

    companion object {
        const val HEART_RATE_PPG_RAW_DATA = "Heart Rate PPG Raw Data"
    }
}
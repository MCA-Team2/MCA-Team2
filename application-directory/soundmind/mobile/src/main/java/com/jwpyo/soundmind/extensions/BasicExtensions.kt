package com.jwpyo.soundmind.extensions

import android.util.Log
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.sql.Timestamp

fun Float.clamp(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

fun LocalDateTime.toTimestamp(): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    return Timestamp.valueOf(formatter.format(this)).time
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(
        this / 1000,
        (this % 1000).toInt(),
        OffsetDateTime.now().offset
    )
}
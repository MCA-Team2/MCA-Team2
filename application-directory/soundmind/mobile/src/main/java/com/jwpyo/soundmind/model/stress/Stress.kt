package com.jwpyo.soundmind.model.stress

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jwpyo.soundmind.extensions.clamp
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime
import kotlin.math.log2

@Entity
@Parcelize
data class Stress(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val ldt: LocalDateTime,
    val value: Float,
) : Parcelable {
    constructor(ldt: LocalDateTime, value: Float) : this(null, ldt, value)

    val x: Float get() = ldt.toLocalTime().toSecondOfDay().toFloat() / 60 / 60
    val y: Float get() = log2(value.clamp(0.25f, 4f))
}
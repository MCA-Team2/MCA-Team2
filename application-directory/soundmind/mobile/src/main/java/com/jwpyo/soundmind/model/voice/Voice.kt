package com.jwpyo.soundmind.model.voice

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Entity
@Parcelize
data class Voice(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val startLDT: LocalDateTime,
    val endLDT: LocalDateTime,
    val receiveLDT: LocalDateTime,
    val array: ByteArray,
) : Parcelable {
    constructor(startLDT: LocalDateTime, endLDT: LocalDateTime, array: ByteArray) :
            this(null, startLDT, endLDT, LocalDateTime.now(), array)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Voice

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
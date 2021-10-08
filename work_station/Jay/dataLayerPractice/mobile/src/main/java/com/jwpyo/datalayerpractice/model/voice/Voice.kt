package com.jwpyo.datalayerpractice.model.voice

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Voice(
    @PrimaryKey(autoGenerate = true) val id: Long?
) : Parcelable
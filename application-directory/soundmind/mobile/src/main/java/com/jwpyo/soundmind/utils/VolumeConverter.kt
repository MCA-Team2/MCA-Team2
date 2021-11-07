package com.jwpyo.soundmind.utils

import com.jwpyo.soundmind.extensions.clamp
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.model.voice.Voice
import java.nio.ByteBuffer
import kotlin.math.log2
import kotlin.math.sqrt

fun convertToShortArray(byteArray: ByteArray): ShortArray {
    val shortArray = ShortArray(byteArray.size / 2)
    val bb = ByteBuffer.wrap(byteArray)
    shortArray.indices.forEach { i ->
        shortArray[i] = bb.getShort()
    }

    return shortArray
}

fun getVolume(byteArray: ByteArray): Float {
    val rms = sqrt(convertToShortArray(byteArray).map { short -> short * short }.average())
    return log2(rms.toFloat())
}
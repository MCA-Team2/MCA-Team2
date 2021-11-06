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
    val dB = log2(rms.toFloat())
    return (dB.clamp(10f, 15f) - 10f) / 5
}

fun getVolumeItems(voiceList: List<Voice>): List<VolumeItem> {
    return voiceList.map {
        val startLDT = it.startLDT
        val endLDT = it.endLDT
        val value = getVolume(it.array)

        VolumeItem(startLDT, endLDT, value)
    }
}
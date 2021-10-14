package com.jwpyo.soundmind.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.InputStream

class SoundPlayer {
    private var state = State.IDLE

    private enum class State {
        IDLE, RECORDING, PLAYING
    }

    /**
     * Plays the recorded file, if any.
     *
     * Returns when playing the file is finished.
     *
     * This is cancellable, and cancelling it will stop playback.
     */
    suspend fun play(inputStream: InputStream) {
        if (state != State.IDLE) {
            // Requesting to play while state was not IDLE
            return
        }

        state = State.PLAYING

        val intSize = AudioTrack.getMinBufferSize(RECORDING_RATE, CHANNELS_OUT, FORMAT)

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .setBufferSizeInBytes(intSize)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(RECORDING_RATE)
                    .setChannelMask(CHANNELS_OUT)
                    .setEncoding(FORMAT)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack.setVolume(AudioTrack.getMaxVolume())
        audioTrack.play()

        try {
            withContext(Dispatchers.IO) {
                val buffer = ByteArray(intSize * 2)
                while (isActive) {
                    // TODO> Inappropriate blocking method call
                    val read = inputStream.read(buffer, 0, buffer.size)
                    if (read < 0) break
                    audioTrack.write(buffer, 0, read)
                }
            }
        } finally {
            audioTrack.release()
            state = State.IDLE
        }
    }

    companion object {
        private const val RECORDING_RATE = 8000 // can go up to 44K, if needed
        private const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
        private const val CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO
        private const val FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}
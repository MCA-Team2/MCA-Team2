package com.jwpyo.datalayerpractice.utils

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.jwpyo.datalayerpractice.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class SoundRecorder {
    private var state = State.IDLE

    private enum class State {
        IDLE, RECORDING, PLAYING
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    suspend fun record(
        onFinish: (ByteArray) -> Unit = {}
    ) {
        if (state != State.IDLE) {
            //Requesting to start recording while state was not IDLE
            return
        }

        state = State.RECORDING

        val intSize = AudioRecord.getMinBufferSize(RECORDING_RATE, CHANNEL_IN, FORMAT)

        val audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(RECORDING_RATE)
                    .setChannelMask(CHANNEL_IN)
                    .setEncoding(FORMAT)
                    .build()
            )
            .setBufferSizeInBytes(intSize * 3)
            .build()

        audioRecord.startRecording()

        var result = ByteArray(0)
        try {
            withContext(Dispatchers.IO) {
                val buffer = ByteArray(intSize)
                while (isActive) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    Log.d(MainActivity.TAG, "hello $read, ${result.size}, ${buffer.size}")
                    result += buffer.copyOfRange(0, read)
                }
            }
        } finally {
            onFinish(result)
            audioRecord.release()
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
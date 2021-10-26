package com.jwpyo.soundmind.utils

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.jwpyo.soundmind.utils.Constant.AUDIO_CHUNK_SIZE
import com.jwpyo.soundmind.utils.Constant.RECORDING_RATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class SoundRecorder {
    private var state = State.IDLE

    private enum class State {
        IDLE, RECORDING, PLAYING
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    suspend fun record(
        onSend: (ByteArray, LocalDateTime, LocalDateTime) -> Unit = { _, _, _ -> }
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
        var startTime: LocalDateTime = LocalDateTime.now()
        try {
            withContext(Dispatchers.IO) {
                val buffer = ByteArray(intSize)
                while (isActive) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    result += buffer.copyOfRange(0, read)

                    if (result.size > AUDIO_CHUNK_SIZE) {
                        onSend(
                            result.copyOfRange(0, AUDIO_CHUNK_SIZE),
                            startTime,
                            LocalDateTime.now().also { startTime = it }
                        )
                        result = result.copyOfRange(AUDIO_CHUNK_SIZE, result.size)
                    }
                }
            }
        } finally {
            onSend(result, startTime, LocalDateTime.now())
            audioRecord.release()
            state = State.IDLE
        }
    }

    companion object {
        private const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
        private const val CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO
        private const val FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}
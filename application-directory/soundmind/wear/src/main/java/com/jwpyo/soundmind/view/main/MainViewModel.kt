package com.jwpyo.soundmind.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.jwpyo.soundmind.utils.Constant
import org.threeten.bp.LocalDateTime

class MainViewModel(
    private val dataClient: DataClient
) : ViewModel() {
    val isRecording = MutableLiveData(false)
    val statusText = MutableLiveData("")

    fun sendVoice(
        voice: ByteArray,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Task<DataItem> {
        val putDataMapRequest = PutDataMapRequest.create(Constant.AUDIO_PATH).apply {
            val asset = Asset.createFromBytes(voice)
            dataMap.putAsset(Constant.AUDIO_KEY, asset)
            dataMap.putString(Constant.START_TIME_KEY, startTime.toString())
            dataMap.putString(Constant.END_TIME_KEY, endTime.toString())
        }

        val request = putDataMapRequest.asPutDataRequest().apply {
            setUrgent()
        }

        return dataClient.putDataItem(request)
    }
}
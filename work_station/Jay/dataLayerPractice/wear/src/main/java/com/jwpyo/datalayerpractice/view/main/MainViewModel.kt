package com.jwpyo.datalayerpractice.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.jwpyo.datalayerpractice.utils.Constant
import org.threeten.bp.LocalDateTime
import java.util.*

class MainViewModel(
    private val dataClient: DataClient
) : ViewModel() {
    val isRecording = MutableLiveData(false)

    fun sendVoice(voice: ByteArray): Task<DataItem> {
        val putDataMapRequest = PutDataMapRequest.create(Constant.AUDIO_PATH).apply {
            val asset = Asset.createFromBytes(voice)
            dataMap.putAsset(Constant.AUDIO_KEY, asset)
            dataMap.putString(Constant.TIME_KEY, LocalDateTime.now().toString())
        }

        val request = putDataMapRequest.asPutDataRequest().apply {
            setUrgent()
        }

        return dataClient.putDataItem(request)
    }
}
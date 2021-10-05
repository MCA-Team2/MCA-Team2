package com.jwpyo.datalayerpractice.view.main

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.jwpyo.datalayerpractice.utils.Constant

class MainViewModel(
    private val dataClient: DataClient
) : ViewModel() {
    var count = 0

    fun sendCount(): Task<DataItem> {
        val putDataMapRequest = PutDataMapRequest.create(Constant.COUNT_PATH)
        putDataMapRequest.dataMap.putInt(Constant.COUNT_KEY, count++)

        val request = putDataMapRequest.asPutDataRequest()
        request.setUrgent()

        return dataClient.putDataItem(request)
    }
}
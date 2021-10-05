package com.jwpyo.datalayerpractice.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.jwpyo.datalayerpractice.utils.Constant

class MainViewModel(
    private val dataClient: DataClient
) : ViewModel() {
    val count = MutableLiveData(0)

    fun increaseCount(): Task<DataItem> {
        count.value = count.value?.plus(1)

        val putDataMapRequest = PutDataMapRequest.create(Constant.COUNT_PATH).apply {
            dataMap.putInt(Constant.COUNT_KEY, count.value!!)
        }

        val request = putDataMapRequest.asPutDataRequest().apply {
            setUrgent()
        }

        return dataClient.putDataItem(request)
    }
}
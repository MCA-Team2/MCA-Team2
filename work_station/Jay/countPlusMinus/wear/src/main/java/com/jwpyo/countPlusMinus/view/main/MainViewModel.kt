package com.jwpyo.countPlusMinus.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.jwpyo.countPlusMinus.utils.Constant

class MainViewModel(
    private val dataClient: DataClient
) : ViewModel() {
    val count = MutableLiveData<Int>()

    fun minusCount(): Task<DataItem> {
        val putDataMapRequest = PutDataMapRequest.create(Constant.COUNT_PATH).apply {
            val newCount = count.value?.minus(1) ?: 0
            dataMap.putInt(Constant.COUNT_KEY, newCount)
        }

        val request = putDataMapRequest.asPutDataRequest().apply {
            setUrgent()
        }

        return dataClient.putDataItem(request)
    }
}
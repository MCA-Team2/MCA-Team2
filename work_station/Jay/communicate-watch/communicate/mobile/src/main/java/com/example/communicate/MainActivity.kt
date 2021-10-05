package com.example.communicate

import android.os.Bundle
import com.example.communicate.base.DatabindingActivity
import com.example.communicate.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*

class MainActivity : DatabindingActivity() {
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private var dataClient: DataClient? = null
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MainActivity

            sendButton.setOnClickListener {
                increaseCounter()
            }
        }

        dataClient = Wearable.getDataClient(this)
    }

    private fun increaseCounter() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/count").run {
            dataMap.putInt(COUNT_KEY, count++)
            asPutDataRequest()
        }.apply {
            setUrgent()
        }

        val putDataTask: Task<DataItem> =
            Wearable.getDataClient(applicationContext).putDataItem(putDataReq)

        putDataTask.addOnSuccessListener {
            binding.sendButton.text = count.toString()
        }
    }

    companion object {
        const val COUNT_KEY = "COUNT"
    }
}
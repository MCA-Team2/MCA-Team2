package com.jwpyo.datalayerpractice

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity(), OnDataChangedListener {
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text_view).setOnClickListener {
            sendCount()
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        Toast.makeText(this, "hello!", Toast.LENGTH_SHORT).show()
    }

    private fun sendCount() {
        Toast.makeText(this, "count = $count", Toast.LENGTH_SHORT).show()

        val putDataMapRequest = PutDataMapRequest.create("/count")
        putDataMapRequest.dataMap.putInt("count", count++)

        val request = putDataMapRequest.asPutDataRequest()
        request.setUrgent()

        val dataItemTask = Wearable.getDataClient(applicationContext).putDataItem(request)
    }
}
package com.example.wearfirstexample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wearfirstexample.databinding.ActivityMainBinding

class MainActivity : Activity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var mPPG: Sensor? = null
    private val TYPE_PPG_RAW = 65572
    private val TYPE_PPG = 21
    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Heart Rate PPG
        // mPPG = sensorManager.getDefaultSensor(TYPE_PPG)

        // Heart Rate PPG RAW Data
        mPPG = sensorManager.getDefaultSensor(TYPE_PPG_RAW)

        textView = findViewById(R.id.text)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BODY_SENSORS), 99)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value = event?.values?.get(0)
        if (value != null) {
            textView?.text = value.toString()
            Log.d("MCA", value.toString())
        }
        else {
            textView?.text = "null"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onResume() {
        super.onResume()
        mPPG?.also { heart ->
            sensorManager.registerListener(this, heart, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
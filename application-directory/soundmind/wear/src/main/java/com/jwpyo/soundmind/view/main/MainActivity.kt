package com.jwpyo.soundmind.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.utils.PermissionManager
import com.jwpyo.soundmind.utils.SoundRecorder
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), DataClient.OnDataChangedListener, SensorEventListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    // Variable for PPG Sensing
    private val permissionManager: PermissionManager by lazy { PermissionManager(this) }
    private lateinit var mSensorManager: SensorManager
    private val mHeartSensors: ArrayList<Sensor> by lazy { checkAllSensors() } // PPG > PPG_Raw > PPG_SPO2 > PPG_SDNN > PPG_HD > PPG_Static > PPG_BG > PPG_SPO2_BG > PPG_SDNN_BG

    // Variable for Vocie Recording
    private var recordingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        AmbientModeSupport.attach(this)

        checkPermissions()
        setEventListeners()
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)

        for(mSensor in mHeartSensors){
            mSensor.also { heart ->
                mSensorManager.registerListener(this, heart, SensorManager.SENSOR_DELAY_FASTEST)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
        mSensorManager.unregisterListener(this)
    }

    private fun checkPermissions() {
        permissionManager.assertPermissionOrRequest(Manifest.permission.RECORD_AUDIO)
        permissionManager.assertPermissionOrRequest(Manifest.permission.BODY_SENSORS)
    }

    private fun checkAllSensors(): ArrayList<Sensor> {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val heartSensors: ArrayList<Sensor> = ArrayList<Sensor>()
        mSensorManager.getSensorList(Sensor.TYPE_ALL).toList().forEach { sensor ->
            if(sensor.name.contains("PPG")) {
                if(sensor.name != "Heart Rate HD PPG Raw Data" && sensor.name != "Heart Rate PPG Raw Data"){
                    Log.d("hello", "Added name = ${sensor.name} / type = ${sensor.type}")
                    heartSensors.add(sensor)
                }
            }
        }
        return heartSensors
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // TODO : Accuracy가 낮을 때 (0~2)이면 특수한 조치?
    }

    override fun onSensorChanged(event: SensorEvent) {
        // TODO : 감지된 센서 데이터를 voice와 함께 기록한다.
        if(event.sensor in mHeartSensors){
            val value = event?.values?.get(0)
            if (value != null)
                Log.d("MCA", event.timestamp.toString() + " [" + event.sensor.name.toString() + "] : " + value.toString() + " (Accuracy: " + event.accuracy + ")")
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("hello", "hello $dataEvents")

//        dataEvents.forEach { dataEvent ->
//            when (dataEvent.type) {
//                TYPE_CHANGED -> {
//                    when (dataEvent.dataItem.uri.path) {
//                        Constant.COUNT_PATH -> {
//                            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
//                            val count = dataMapItem.dataMap.getInt(Constant.COUNT_KEY)
//
//                            mainViewModel.count.postValue(count)
//                        }
//                        else -> Unit
//                    }
//                }
//                TYPE_DELETED -> Unit
//                else -> Unit
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    private fun setEventListeners() {
        binding.recordButton.setOnClickListener {
            mainViewModel.isRecording.postValue(true)
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                SoundRecorder().record {
                    val nodeList =
                        Tasks.await(Wearable.getNodeClient(this@MainActivity).connectedNodes)
                    if (nodeList.size > 0) {
                        mainViewModel.statusText.postValue("connected")
                        mainViewModel.sendVoice(it)
                    }
                    else {
                        mainViewModel.statusText.postValue("disconnected")
                    }
                }
            }
        }
        binding.stopButton.setOnClickListener {
            mainViewModel.isRecording.postValue(false)
            recordingJob?.cancel()
        }
    }

}
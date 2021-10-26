package com.jwpyo.soundmind.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.os.Bundle
import android.util.Log
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.model.PPG
import com.jwpyo.soundmind.utils.AppState
import com.jwpyo.soundmind.utils.Constant.PPG_CHUNK_SIZE
import com.jwpyo.soundmind.utils.PermissionManager
import com.jwpyo.soundmind.utils.SoundRecorder
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity :
    BaseActivity(),
    AmbientModeSupport.AmbientCallbackProvider,
    DataClient.OnDataChangedListener,
    SensorEventListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()
    private val appState: AppState by inject()

    private lateinit var ambientController: AmbientModeSupport.AmbientController

    //region variables for PPG Sensing
    /**
     * PPG > PPG_Raw > PPG_SPO2 > PPG_SDNN > PPG_HD > PPG_Static > PPG_BG > PPG_SPO2_BG > PPG_SDNN_BG
     */
    private val permissionManager: PermissionManager by lazy { PermissionManager(this) }
    private val sensorManager: SensorManager by lazy { getSystemService(SENSOR_SERVICE) as SensorManager }
    private val heartSensors: List<Sensor> by lazy {
        sensorManager.getSensorList(Sensor.TYPE_ALL).toList()
            .filter { it.name.contains("PPG") }
            .onEach { Log.d("hello", "name = ${it.name} / type = ${it.type}") }
    }

    private val ppgStorageMap: MutableMap<String, List<PPG>> = mutableMapOf()
    //endregion

    //region variables for Voice Recording
    private var recordingJob: Job? = null
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        ambientController = AmbientModeSupport.attach(this)

        checkPermissions()
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDestroy() {
        mainViewModel.isRecording.postValue(false)
        stopRecord()
        super.onDestroy()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object : AmbientModeSupport.AmbientCallback() {
            override fun onUpdateAmbient() {
                super.onUpdateAmbient()
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("hello", "dataEvents = $dataEvents")
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d("hello", "accuracy = $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorName = event.sensor.name
        val ppg = event.run {
            val sensorValue = values?.get(0) ?: return@run null
            if (sensorValue < 0) return@run null
            PPG(sensorValue, accuracy, timestamp)
        } ?: return

        val ppgList = (ppgStorageMap[sensorName] ?: emptyList()) + ppg

        if (ppgList.size >= PPG_CHUNK_SIZE) {
            val sensorValue = ppgList.map { it.sensorValue }.toFloatArray()
            val accuracy = ppgList.map { it.accuracy.toLong() }.toLongArray()
            val timestamp = ppgList.map { it.timestamp }.toLongArray()
            mainViewModel.sendPPG(sensorName, sensorValue, accuracy, timestamp)
            ppgStorageMap[sensorName] = emptyList()
        } else {
            ppgStorageMap[sensorName] = ppgList
        }
    }

    private fun checkPermissions() {
        permissionManager.assertPermissionOrRequest(Manifest.permission.RECORD_AUDIO)
        permissionManager.assertPermissionOrRequest(Manifest.permission.BODY_SENSORS)
    }

    private fun setObservers() {
        appState.recordState.observe(this) {
            when (it) {
                true -> {
                    if (mainViewModel.isRecording.value != true) {
                        mainViewModel.isRecording.postValue(true)
                        startRecord()
                        heartSensors.forEach { sensor ->
                            sensorManager.registerListener(this, sensor, SENSOR_DELAY_FASTEST)
                        }
                    }
                }
                false -> {
                    mainViewModel.isRecording.postValue(false)
                    stopRecord()
                    sensorManager.unregisterListener(this)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecord() {
        stopRecord()
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            SoundRecorder().record { byteArray, startTime, endTime ->
                mainViewModel.sendVoice(byteArray, startTime, endTime)
            }
        }
    }

    private fun stopRecord() {
        recordingJob?.cancel()
    }

    companion object {
        const val HEART_RATE_HD_PPG_RAW = "Heart Rate HD PPG Raw Data"
        const val HEART_RATE_PPG_RAW = "Heart Rate PPG Raw Data"
    }
}
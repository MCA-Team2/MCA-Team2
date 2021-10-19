package com.jwpyo.soundmind.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.utils.AppState
import com.jwpyo.soundmind.utils.PermissionManager
import com.jwpyo.soundmind.utils.SoundRecorder
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), DataClient.OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()
    private val appState: AppState by inject()

    private val permissionManager: PermissionManager by lazy { PermissionManager(this) }

    private var recordingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        AmbientModeSupport.attach(this)

        checkPermissions()
        setObservers()

        printAllSensors()
        getGoogleFitClient()
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("hello", "hello $dataEvents")
    }

    private fun checkPermissions() {
        permissionManager.assertPermissionOrRequest(Manifest.permission.RECORD_AUDIO)
    }

    private fun setObservers() {
        appState.recordState.observe(this) {
            binding.logText.text = "state -> $it"
            when (it) {
                true -> {
                    if (mainViewModel.isRecording.value != true) {
                        mainViewModel.isRecording.postValue(true)
                        startRecord()
                    }
                }
                false -> {
                    mainViewModel.isRecording.postValue(false)
                    stopRecord()
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


    /**
     * Experimental works ...
     */

    private fun printAllSensors() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getSensorList(Sensor.TYPE_ALL).toList().forEach { sensor ->
            Log.d("hello", "hello name = ${sensor.name} / type = ${sensor.type}")
        }
    }

    private fun getGoogleFitClient() {
        val fitnessOptions =
            FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_DELTA).build()

        // Note: Fitness.SensorsApi.findDataSources() requires the
        // ACCESS_FINE_LOCATION permission.

        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .findDataSources(
                DataSourcesRequest.Builder()
                    .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                    .setDataSourceTypes(DataSource.TYPE_RAW)
                    .build()
            )
            .addOnSuccessListener { dataSources ->
                dataSources.forEach {
                    Log.i("hello", "hello Data source found: ${it.streamIdentifier}")
                    Log.i("hello", "hello Data Source type: ${it.dataType.name}")

                    if (it.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        Log.i("hello", "hello Data source for STEP_COUNT_DELTA found!")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("hello", "hello Find data sources request failed", e)
            }
    }
}
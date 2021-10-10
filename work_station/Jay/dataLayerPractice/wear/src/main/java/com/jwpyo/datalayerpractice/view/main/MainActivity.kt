package com.jwpyo.datalayerpractice.view.main

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
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
import com.jwpyo.datalayerpractice.utils.PermissionManager
import com.jwpyo.datalayerpractice.utils.SoundRecorder
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), DataClient.OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

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

        setEventListeners()

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

    private fun checkPermissions() {
        permissionManager.assertPermissionOrRequest(Manifest.permission.RECORD_AUDIO)
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
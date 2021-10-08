package com.jwpyo.datalayerpractice.view.main

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEvent.TYPE_DELETED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
import com.jwpyo.datalayerpractice.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), DataClient.OnDataChangedListener, SensorEventListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    lateinit var sensorManager: SensorManager
    lateinit var sensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        AmbientModeSupport.attach(this)

        setEventListeners()
        setSensors()
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
        /**
         * @author Jay
         * when count data from mobile/wear is received,
         * update count data in view model
         *
         * exactly same with mobile/MainActivity
         */
        Log.d("MainActivity::onDataChanged", "$dataEvents")

        dataEvents.forEach { dataEvent ->
            when (dataEvent.type) {
                TYPE_CHANGED -> {
                    when (dataEvent.dataItem.uri.path) {
                        Constant.COUNT_PATH -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                            val count = dataMapItem.dataMap.getInt(Constant.COUNT_KEY)

                            mainViewModel.count.postValue(count)
                        }
                        else -> {
                            TODO()
                        }
                    }
                }
                TYPE_DELETED -> {
                    TODO()
                }
                else -> {
                    TODO()
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    private fun setEventListeners() {
        binding.decreaseButton.setOnClickListener {
            mainViewModel.minusCount()
        }
    }

    private fun setSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getSensorList(Sensor.TYPE_ALL).toList().forEach { sensor ->
            Log.d(TAG, "hello name = ${sensor.name} / type = ${sensor.type}")
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
                    Log.i(TAG, "hello Data source found: ${it.streamIdentifier}")
                    Log.i(TAG, "hello Data Source type: ${it.dataType.name}")

                    if (it.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        Log.i(TAG, "hello Data source for STEP_COUNT_DELTA found!")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "hello Find data sources request failed", e)
            }
    }

    companion object {
        const val TAG = "JAEWOO TEST"
    }
}
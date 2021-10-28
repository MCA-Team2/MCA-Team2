package com.jwpyo.soundmind.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.service.MainService
import com.jwpyo.soundmind.utils.Constant.ACCURACY_KEY
import com.jwpyo.soundmind.utils.Constant.AUDIO_KEY
import com.jwpyo.soundmind.utils.Constant.AUDIO_PATH
import com.jwpyo.soundmind.utils.Constant.END_TIME_KEY
import com.jwpyo.soundmind.utils.Constant.PPG_PATH_PREFIX
import com.jwpyo.soundmind.utils.Constant.SENSOR_VALUE_KEY
import com.jwpyo.soundmind.utils.Constant.START_TIME_KEY
import com.jwpyo.soundmind.utils.Constant.TIME_STAMP_KEY
import com.jwpyo.soundmind.view.adapter.MainStateAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime

class MainActivity : BaseActivity(), OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            stateAdapter = MainStateAdapter(this@MainActivity)
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        setEventListeners()
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
        dataEvents
            .filter { it.type == TYPE_CHANGED }
            .forEach { dataEvent ->
                val path = dataEvent.dataItem.uri.path ?: return@forEach
                Log.d("hello", "hello $dataEvent")

                when {
                    path == AUDIO_PATH -> {
                        onAudioChangedEvent(dataEvent)
                    }
                    path.substring(0, PPG_PATH_PREFIX.length) == PPG_PATH_PREFIX -> {
                        onPPGChangeEvent(dataEvent, path.substring(PPG_PATH_PREFIX.length))
                    }
                }
            }
    }

    private fun setEventListeners() {
        findViewById<View>(R.id.nav_voice).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_voice
        }
        findViewById<View>(R.id.nav_ppg).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_ppg
        }
        findViewById<View>(R.id.nav_setting).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_setting
        }
    }

    private fun onAudioChangedEvent(dataEvent: DataEvent) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val asset = dataMapItem.dataMap.getAsset(AUDIO_KEY)
            val startLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(START_TIME_KEY)
            )
            val endLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(END_TIME_KEY)
            )
            mainViewModel.insertVoice(asset!!, startLDT, endLDT)
        }.onFailure {
            Log.e("hello", "error: $it")
        }

    private fun onPPGChangeEvent(dataEvent: DataEvent, sensorName: String) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val sensorValue = dataMapItem.dataMap.getFloatArray(SENSOR_VALUE_KEY)!!
            val accuracy = dataMapItem.dataMap.getLongArray(ACCURACY_KEY)!!
            val timestamp = dataMapItem.dataMap.getLongArray(TIME_STAMP_KEY)!!

            val ppgList = sensorValue.indices.map { i ->
                PPG(sensorName, sensorValue[i], accuracy[i].toInt(), timestamp[i])
            }
            mainViewModel.insertPPG(ppgList)
        }.onFailure {
            Log.e("hello", "error: $it")
        }
}
package com.jwpyo.soundmind.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.extensions.showToast
import com.jwpyo.soundmind.utils.Constant
import com.jwpyo.soundmind.view.adapter.MainStateAdapter
import com.jwpyo.soundmind.view.adapter.VoiceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
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
        dataEvents.forEach { dataEvent ->
            when (dataEvent.type) {
                DataEvent.TYPE_CHANGED -> {
                    when (dataEvent.dataItem.uri.path) {
                        Constant.AUDIO_PATH -> {
                            Log.d("hello", "hello $dataEvent")
                            onAudioChangedEvent(dataEvent)
                        }
                    }
                }
                DataEvent.TYPE_DELETED -> Unit
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
            val asset = dataMapItem.dataMap.getAsset(Constant.AUDIO_KEY)
            val startLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(Constant.START_TIME_KEY)
            )
            val endLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(Constant.END_TIME_KEY)
            )
            mainViewModel.insertVoice(asset!!, startLDT, endLDT)
        }.onFailure {
            showToast("$it")
        }
}
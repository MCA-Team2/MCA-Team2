package com.jwpyo.soundmind.view.main

import android.os.Bundle
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.extensions.showToast
import com.jwpyo.soundmind.utils.Constant
import com.jwpyo.soundmind.view.adapter.VoiceAdapter
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime

class MainActivity : BaseActivity(), OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            adapter = VoiceAdapter(mainViewModel, get())
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
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

    private fun onAudioChangedEvent(dataEvent: DataEvent) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val asset = dataMapItem.dataMap.getAsset(Constant.AUDIO_KEY)
            val ldt = LocalDateTime.parse(
                dataMapItem.dataMap.getString(Constant.TIME_KEY)
            )
            mainViewModel.insertVoice(asset!!, ldt)
        }.onFailure {
            showToast("$it")
        }
}
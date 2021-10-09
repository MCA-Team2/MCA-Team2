package com.jwpyo.datalayerpractice.view.main

import android.os.Bundle
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
import com.jwpyo.datalayerpractice.extensions.showToast
import com.jwpyo.datalayerpractice.utils.Constant
import com.jwpyo.datalayerpractice.view.adapter.VoiceAdapter
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime

class MainActivity : BaseActivity(), OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            adapter = VoiceAdapter(get())
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
package com.jwpyo.datalayerpractice.view.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
import com.jwpyo.datalayerpractice.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), OnDataChangedListener {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
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
                DataEvent.TYPE_CHANGED -> {
                    when (dataEvent.dataItem.uri.path) {
                        Constant.COUNT_PATH -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                            val count = dataMapItem.dataMap.getInt(Constant.COUNT_KEY)

                            mainViewModel.count.postValue(count)
                        }
                        Constant.AUDIO_PATH -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                            val asset = dataMapItem.dataMap.getAsset(Constant.AUDIO_KEY)
                        }
                        else -> {
                            TODO()
                        }
                    }
                }
                DataEvent.TYPE_DELETED -> {
                    TODO()
                }
                else -> {
                    TODO()
                }
            }
        }
    }

    private fun setEventListeners() {
        binding.increaseButton.setOnClickListener {
            mainViewModel.plusCount()
        }
    }

    private fun loadJob(activity: Activity, asset: Asset): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val assetInputStream = Tasks.await(
                    Wearable.getDataClient(activity).getFdForAsset(asset)
                ).inputStream

                // TODO> how to manage input stream?
            }
        }
    }
}
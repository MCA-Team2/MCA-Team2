package com.jwpyo.countPlusMinus.view.main

import android.os.Bundle
import android.util.Log
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.jwpyo.countPlusMinus.R
import com.jwpyo.countPlusMinus.base.BaseActivity
import com.jwpyo.countPlusMinus.databinding.ActivityMainBinding
import com.jwpyo.countPlusMinus.utils.Constant
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
}
package com.jwpyo.datalayerpractice.view.main

import android.os.Bundle
import android.util.Log
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
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

    override fun onDataChanged(dataEvent: DataEventBuffer) {
        // TODO : Declare what to do when notifying that the data layer has changed.
        /**
         * @author Jay
         * mobile 에서 data layer 에 보낸 putInt 요청으로,
         * data layer 에 수정이 일어나면,
         * wear 도 당연히 이벤트[dataEvent]를 수신하지만,
         * mobile 또한 그 이벤트[dataEvent]를 수신합니다.
         */
    }

    private fun setEventListeners() {
        binding.sendButton.setOnClickListener {
            Log.d(TAG, "count=${mainViewModel.count}")
            mainViewModel.sendCount()
        }
    }

    companion object {
        const val TAG = "MainActivity::Logging"
    }
}
package com.jwpyo.soundmind.view.log

import android.os.Bundle
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityLogBinding
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.view.adapter.SensorTypeAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogActivity : BaseActivity() {

    private val binding: ActivityLogBinding by binding(R.layout.activity_log)
    private val logViewModel: LogViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            adapter = SensorTypeAdapter()
            vm = logViewModel
            lifecycleOwner = this@LogActivity
        }

        setEventListeners()
    }

    private fun setEventListeners() {
        binding.voiceHistory.enterButton.setOnClickListener {
            VoiceDetailDialog().show(this)
        }
    }
}
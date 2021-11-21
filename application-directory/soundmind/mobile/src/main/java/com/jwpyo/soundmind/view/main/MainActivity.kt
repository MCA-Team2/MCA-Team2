package com.jwpyo.soundmind.view.main

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.BaseActivity
import com.jwpyo.soundmind.databinding.ActivityMainBinding
import com.jwpyo.soundmind.view.adapter.MainStateAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), 3)

        binding.apply {
            stateAdapter = MainStateAdapter(this@MainActivity)
            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        setEventListeners()
    }

    private fun setEventListeners() {
        findViewById<View>(R.id.nav_record).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_record
        }
        findViewById<View>(R.id.nav_history).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_history
        }
        findViewById<View>(R.id.nav_setting).setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.nav_setting
        }
    }
}
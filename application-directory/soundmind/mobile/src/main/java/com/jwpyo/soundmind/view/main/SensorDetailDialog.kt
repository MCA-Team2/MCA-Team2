package com.jwpyo.soundmind.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogSensorDetailBinding
import com.jwpyo.soundmind.extensions.applyWindowTransparent

class SensorDetailDialog(
    private val data: List<Float> = listOf()
) : DatabindingDialog() {
    private lateinit var binding: DialogSensorDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding<DialogSensorDetailBinding>(
            inflater,
            R.layout.dialog_sensor_detail,
            container
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this@SensorDetailDialog.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowTransparent()
    }
}
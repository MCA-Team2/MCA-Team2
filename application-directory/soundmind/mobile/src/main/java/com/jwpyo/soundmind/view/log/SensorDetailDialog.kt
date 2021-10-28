package com.jwpyo.soundmind.view.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogSensorDetailBinding
import com.jwpyo.soundmind.extensions.applyWindowSize
import com.jwpyo.soundmind.extensions.applyWindowTransparent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SensorDetailDialog(
    private val sensorName: String? = null,
) : DatabindingDialog() {
    private lateinit var binding: DialogSensorDetailBinding
    val viewModel: LogViewModel by sharedViewModel()

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
        applyWindowSize(0.9f, 0.5f)

        binding.titleText.text = sensorName

        viewModel.ppgList.observe(this) { ppgAllList ->
            val ppgList = ppgAllList
                .filter { it.sensorName == sensorName }
                .sortedBy { it.timestamp }
            binding.detailText.text = "#${ppgList.size} of data"
        }
    }
}
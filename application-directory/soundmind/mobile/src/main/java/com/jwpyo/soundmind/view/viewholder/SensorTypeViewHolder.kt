package com.jwpyo.soundmind.view.viewholder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jwpyo.soundmind.base.BaseBindingViewHolder
import com.jwpyo.soundmind.databinding.ItemSensorTypeBinding
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.view.log.LogViewModel
import com.jwpyo.soundmind.view.log.SensorDetailDialog

class SensorTypeViewHolder(
    view: View,
    private val viewModel: LogViewModel,
) : BaseBindingViewHolder<ItemSensorTypeBinding>(view) {
    override fun bindData(data: Any) {
        if (data is String) {
            binding.apply {
                item = data
                lifecycleOwner = view.context as LifecycleOwner
            }

            setEventListeners(data)
        }
    }

    private fun setEventListeners(sensorName: String) {
        binding.enterButton.setOnClickListener {
            SensorDetailDialog(sensorName).show(view.context)
        }
    }
}
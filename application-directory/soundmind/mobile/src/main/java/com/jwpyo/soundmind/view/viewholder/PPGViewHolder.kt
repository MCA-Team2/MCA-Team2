package com.jwpyo.soundmind.view.viewholder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jwpyo.soundmind.base.BaseBindingViewHolder
import com.jwpyo.soundmind.databinding.ItemPpgTypeBinding
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.view.main.MainViewModel
import com.jwpyo.soundmind.view.ppg.PPGGraphDialog

class PPGViewHolder(
    view: View,
    private val viewModel: MainViewModel,
) : BaseBindingViewHolder<ItemPpgTypeBinding>(view) {
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

            PPGGraphDialog(sensorName).show(view.context)
        }
    }
}
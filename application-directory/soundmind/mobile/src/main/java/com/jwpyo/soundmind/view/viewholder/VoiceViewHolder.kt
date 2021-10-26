package com.jwpyo.soundmind.view.viewholder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jwpyo.soundmind.base.BaseBindingViewHolder
import com.jwpyo.soundmind.databinding.ItemVoiceBinding
import com.jwpyo.soundmind.model.ui.VoiceItem
import com.jwpyo.soundmind.view.main.MainViewModel

class VoiceViewHolder(
    view: View,
    private val viewModel: MainViewModel,
) : BaseBindingViewHolder<ItemVoiceBinding>(view) {
    override fun bindData(data: Any) {
        if (data is VoiceItem) {
            binding.apply {
                item = data
                lifecycleOwner = view.context as LifecycleOwner
            }

            if (viewModel.isPlaying == data.voice.id)
                data.isPlaying.value = true

            setEventListeners(data)
        }
    }

    private fun setEventListeners(voiceItem: VoiceItem) {
        binding.mediaButton.setOnClickListener {
            when (voiceItem.isPlaying.value) {
                null -> Unit
                true -> {
                    viewModel.stop(voiceItem)
                }
                false -> {
                    viewModel.play(voiceItem)
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteVoice(voiceItem.voice)
        }
    }
}
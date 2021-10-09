package com.jwpyo.datalayerpractice.view.viewholder

import android.view.View
import com.jwpyo.datalayerpractice.base.BaseBindingViewHolder
import com.jwpyo.datalayerpractice.databinding.ItemVoiceBinding
import com.jwpyo.datalayerpractice.model.ui.VoiceItem

class VoiceViewHolder(
    view: View,
) : BaseBindingViewHolder<ItemVoiceBinding>(view) {
    private var isPlaying = false

    override fun bindData(data: Any) {
        if (data is VoiceItem) {
            binding.apply {
                item = data
            }

            binding.mediaButton.setOnClickListener {
                isPlaying = !isPlaying

                if (isPlaying) {
                    play(data)
                    binding.mediaButton.text = "stop"
                } else {
                    stop(data)
                    binding.mediaButton.text = "play"
                }
            }
        }
    }

    private fun play(voiceItem: VoiceItem) {

    }

    private fun stop(voiceItem: VoiceItem) {

    }
}
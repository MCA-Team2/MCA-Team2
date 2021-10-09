package com.jwpyo.datalayerpractice.view.viewholder

import android.view.View
import com.jwpyo.datalayerpractice.base.BaseBindingViewHolder
import com.jwpyo.datalayerpractice.databinding.ItemVoiceBinding
import com.jwpyo.datalayerpractice.model.ui.VoiceItem
import com.jwpyo.datalayerpractice.utils.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class VoiceViewHolder(
    view: View,
    private val soundPlayer: SoundPlayer
) : BaseBindingViewHolder<ItemVoiceBinding>(view) {
    private var playingJob: Job? = null
    private var isPlaying = false

    override fun bindData(data: Any) {
        if (data is VoiceItem) {
            binding.apply {
                item = data
            }

            binding.mediaButton.setOnClickListener {
                isPlaying = !isPlaying

                if (isPlaying) {
                    binding.mediaButton.text = "stop"
                    playingJob = CoroutineScope(Dispatchers.IO).launch { play(data) }
                    playingJob?.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.mediaButton.text = "play"
                            isPlaying = false
                        }
                    }
                } else stop()
            }

            binding.deleteButton.setOnClickListener {

            }
        }
    }

    private suspend fun play(voiceItem: VoiceItem) {
        val byteArray = voiceItem.voice.array
        val inputStream = ByteArrayInputStream(byteArray)
        soundPlayer.play(inputStream)
        stop()
    }

    private fun stop() {
        playingJob?.cancel()
        playingJob = null
    }
}
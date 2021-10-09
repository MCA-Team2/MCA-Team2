package com.jwpyo.datalayerpractice.view.viewholder

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.jwpyo.datalayerpractice.base.BaseBindingViewHolder
import com.jwpyo.datalayerpractice.databinding.ItemVoiceBinding
import com.jwpyo.datalayerpractice.model.ui.VoiceItem
import com.jwpyo.datalayerpractice.utils.SoundPlayer
import com.jwpyo.datalayerpractice.view.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class VoiceViewHolder(
    view: View,
    private val viewModel: MainViewModel,
    private val soundPlayer: SoundPlayer,
) : BaseBindingViewHolder<ItemVoiceBinding>(view) {
    private var playingJob: Job? = null
    private var isPlayingLiveData: MutableLiveData<Boolean>? = null

    override fun bindData(data: Any) {
        if (data is VoiceItem) {
            binding.apply {
                item = data
            }

            isPlayingLiveData = viewModel.isPlayingMap[data.voice.id]
            if (isPlayingLiveData == null) {
                viewModel.isPlayingMap[data.voice.id!!] = MutableLiveData(false).also {
                    isPlayingLiveData = it
                }
            }

            setEventListeners(data)
            setObservers(data)
        }
    }

    private fun setEventListeners(voiceItem: VoiceItem) {
        binding.mediaButton.setOnClickListener {
            isPlayingLiveData?.postValue(isPlayingLiveData?.value == false)
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteVoice(voiceItem.voice)
        }
    }

    private fun setObservers(voiceItem: VoiceItem) {
        Log.d("hello", "hello 11")
        val lifecycleOwner = view.context as LifecycleOwner

        Log.d("hello", "hello 22 $isPlayingLiveData")
        isPlayingLiveData?.let { isPlayingLiveData ->
            isPlayingLiveData.observe(lifecycleOwner) { isPlaying ->
                Log.d("hello", "hello 33")
                if (isPlaying) {
                    binding.mediaButton.text = "stop"
                    playingJob = CoroutineScope(Dispatchers.IO).launch { play(voiceItem) }
                    playingJob?.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.mediaButton.text = "play"
                            isPlayingLiveData.postValue(false)
                        }
                    }
                } else stop()
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
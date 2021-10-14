package com.jwpyo.soundmind.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jwpyo.soundmind.model.ui.VoiceItem
import com.jwpyo.soundmind.view.adapter.VoiceAdapter

@BindingAdapter("voiceItems")
fun bindVoiceItems(view: RecyclerView, items: List<VoiceItem>?) {
    view.recycledViewPool.clear()
    if (items != null) {
        (view.adapter as? VoiceAdapter)?.addItems(items)
    }
}
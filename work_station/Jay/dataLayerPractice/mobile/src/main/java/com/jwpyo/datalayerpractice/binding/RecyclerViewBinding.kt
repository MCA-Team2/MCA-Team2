package com.jwpyo.datalayerpractice.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jwpyo.datalayerpractice.model.ui.VoiceItem
import com.jwpyo.datalayerpractice.view.adapter.VoiceAdapter

@BindingAdapter("voiceItems")
fun bindVoiceItems(view: RecyclerView, items: List<VoiceItem>?) {
    view.recycledViewPool.clear()
    if (items != null) {
        (view.adapter as? VoiceAdapter)?.addItems(items)
    }
}
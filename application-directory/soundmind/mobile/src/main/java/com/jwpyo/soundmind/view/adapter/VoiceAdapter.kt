package com.jwpyo.soundmind.view.adapter

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.model.ui.VoiceItem
import com.jwpyo.soundmind.utils.SoundPlayer
import com.jwpyo.soundmind.view.log.LogViewModel
import com.jwpyo.soundmind.view.main.MainViewModel
import com.jwpyo.soundmind.view.viewholder.VoiceViewHolder
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow

class VoiceAdapter(
    private val viewModel: LogViewModel,
    private val lifecycleOwner: LifecycleOwner
) : BaseAdapter() {
    init {
        addSection(arrayListOf<Any>())
    }

    fun addItems(items: List<VoiceItem>) {
        sections()[0].clear()
        addItemListOnSection(0, items)

        // TODO> apply DiffUtil
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int = R.layout.item_voice

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when (layout) {
            R.layout.item_voice -> return VoiceViewHolder(view, viewModel, lifecycleOwner)
            else -> throw Resources.NotFoundException("not founded layout")
        }

    }
}
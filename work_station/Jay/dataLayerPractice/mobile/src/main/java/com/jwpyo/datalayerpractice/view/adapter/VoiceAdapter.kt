package com.jwpyo.datalayerpractice.view.adapter

import android.content.res.Resources
import android.view.View
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.model.ui.VoiceItem
import com.jwpyo.datalayerpractice.utils.SoundPlayer
import com.jwpyo.datalayerpractice.view.viewholder.VoiceViewHolder
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow

class VoiceAdapter(
    private val soundPlayer: SoundPlayer
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
            R.layout.item_voice -> return VoiceViewHolder(view, soundPlayer)
            else -> throw Resources.NotFoundException("not founded layout")
        }

    }
}
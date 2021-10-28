package com.jwpyo.soundmind.view.adapter

import android.content.res.Resources
import android.view.View
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.view.log.LogViewModel
import com.jwpyo.soundmind.view.main.MainViewModel
import com.jwpyo.soundmind.view.viewholder.SensorTypeViewHolder
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow

class SensorTypeAdapter(
    private val viewModel: LogViewModel
) : BaseAdapter() {
    init {
        addSection(arrayListOf<Any>())
    }

    fun addItems(items: List<String>) {
        sections()[0].clear()
        addItemListOnSection(0, items)

        // TODO> apply DiffUtil
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int = R.layout.item_sensor_type

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when (layout) {
            R.layout.item_sensor_type -> return SensorTypeViewHolder(view, viewModel)
            else -> throw Resources.NotFoundException("not founded layout")
        }

    }
}
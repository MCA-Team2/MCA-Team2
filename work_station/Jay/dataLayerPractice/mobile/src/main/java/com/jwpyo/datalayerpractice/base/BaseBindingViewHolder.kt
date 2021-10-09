package com.jwpyo.datalayerpractice.base

import android.view.View
import com.jwpyo.datalayerpractice.binding.bindings
import com.skydoves.baserecyclerviewadapter.BaseViewHolder

abstract class BaseBindingViewHolder<T>(val view: View) : BaseViewHolder(view) {

    protected val binding: T by bindings(view)

    override fun onClick(v: View?) = Unit

    override fun onLongClick(v: View?): Boolean = true
}
package com.jwpyo.datalayerpractice.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("count_text")
fun bindingCountText(view: TextView, count: Int?) {
    view.text =
        if (count != null) "count = $count"
        else "not yet received"
}
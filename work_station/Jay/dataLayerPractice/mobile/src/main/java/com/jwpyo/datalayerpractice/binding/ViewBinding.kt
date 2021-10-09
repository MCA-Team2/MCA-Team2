package com.jwpyo.datalayerpractice.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@BindingAdapter("ldt_text")
fun bindingLDTText(view: TextView, ldt: LocalDateTime?) {
    view.text =
        if (ldt == null) "invalid time"
        else ldt.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss"))
}
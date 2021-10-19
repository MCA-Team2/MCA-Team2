package com.jwpyo.soundmind.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment

fun DialogFragment.show(context: Context?) {
    val fm = context?.getAutoFragmentManager()
    if (fm != null && fm.findFragmentByTag(this::class.java.simpleName) == null) {
        fm.beginTransaction().add(this, this::class.java.simpleName).commitAllowingStateLoss()
    }
}

fun DialogFragment.applyWindowTransparent() {
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}
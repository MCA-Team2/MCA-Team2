package com.jwpyo.soundmind.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
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

fun DialogFragment.applyWindowSize(widthRatio: Float, heightRatio: Float) {
    val params: ViewGroup.LayoutParams? = dialog?.window?.attributes

    val metrics = requireContext().resources.displayMetrics

    params?.width = metrics.widthPixels.times(widthRatio).toInt()
    params?.height = metrics.heightPixels.times(heightRatio).toInt()

    dialog?.window?.attributes = params as WindowManager.LayoutParams
    dialog?.window?.setGravity(Gravity.CENTER)
}
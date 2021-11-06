package com.jwpyo.soundmind.view.history

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.VolumeItem
import org.threeten.bp.LocalDateTime

class VoiceVolumeChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var source: List<VolumeItem>? = null
    private var x1: Int = 0
    private var x2: Int = 24
    private var radius = 5f

    fun setData(_source: List<VolumeItem>) {
        if (_source.isEmpty()) return
        source = _source

        invalidate()
    }

    fun setScope(left: LocalDateTime, right: LocalDateTime) {
        layoutParams.width = WIDTH_PER_HOUR * (right.hour - left.hour + 1)

        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (source == null) return

        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()

        canvas?.apply {
            source?.forEach {
//                it.startLDT - x1
            }
        }
    }

    private fun Canvas.drawRoundRect(c1: Pair<Float, Float>, c2: Pair<Float, Float>, paint: Paint) =
        drawRoundRect(
            RectF(c1.first - radius, c1.second - radius, c2.first + radius, c2.second + radius),
            radius, radius, paint
        )

    companion object {
        const val WIDTH_PER_HOUR = 300
    }
}
package com.jwpyo.soundmind.view.history

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.model.ui.VolumeItem
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class VoiceVolumeChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var source: List<VolumeItem>? = null
    private var x1: Int = 0
    private var x2: Int = 24

    private val gridPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grid)
        style = Paint.Style.STROKE
    }

    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.audio)
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val textPaint = TextPaint().apply {
        color = ContextCompat.getColor(context, R.color.font)
        textSize = 25f
        textAlign = Paint.Align.LEFT
    }

    fun setData(_source: List<VolumeItem>) {
        if (_source.isEmpty()) return
        source = _source

        invalidate()
    }

    fun setScope(left: Int, right: Int) {
        layoutParams = layoutParams.apply {
            width = VoiceVolumeChart.WIDTH_PER_HOUR * (right - left)
        }
        x1 = left
        x2 = right

        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        Log.e("hello", "hello voice chart on draw")
        super.onDraw(canvas)
        if (source == null) return

        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val unitMaxHeight = h * 0.6f

        canvas?.apply {
            (x1..x2).forEach { x ->
                drawLine(
                    (x - x1).toFloat() * StressLineChart.WIDTH_PER_HOUR,
                    0f,
                    (x - x1).toFloat() * StressLineChart.WIDTH_PER_HOUR,
                    h,
                    gridPaint
                )
                drawText(
                    "$x:00",
                    (x - x1).toFloat() * StressLineChart.WIDTH_PER_HOUR,
                    h - textPaint.textSize,
                    textPaint
                )
            }

            source?.forEach {
                if (it.value == null) return@forEach

                val l = Duration.between(
                    LocalTime.of(x1, 0),
                    it.startLDT.toLocalTime()
                ).toMinutes().toFloat() / 60 * WIDTH_PER_HOUR
                val r = Duration.between(
                    LocalTime.of(x1, 0),
                    it.endLDT.toLocalTime()
                ).toMinutes().toFloat() / 60 * WIDTH_PER_HOUR

                val vol = it.value * unitMaxHeight
                drawLine((l+r) / 2, h / 2 - vol / 2, (l+r) / 2, h / 2 + vol / 2, linePaint)
            }
        }
    }

    init {
        setWillNotDraw(false)
    }

    companion object {
        const val WIDTH_PER_HOUR = 300

        val defaultInfo: List<VolumeItem> = listOf(
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 0),
                LocalDateTime.now().toLocalDate().atTime(9, 5),
                0.5f
            ),
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 5),
                LocalDateTime.now().toLocalDate().atTime(9, 10),
                0.1f
            ),
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 10),
                LocalDateTime.now().toLocalDate().atTime(9, 15),
                0.2f
            ),
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 15),
                LocalDateTime.now().toLocalDate().atTime(9, 20),
                0.8f
            ),
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 20),
                LocalDateTime.now().toLocalDate().atTime(9, 25),
                0.9f
            ),
            VolumeItem(
                LocalDateTime.now().toLocalDate().atTime(9, 25),
                LocalDateTime.now().toLocalDate().atTime(9, 30),
                0.5f
            ),
        )
    }
}
package com.jwpyo.soundmind.view.history

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.utils.Constant.WIDTH_PER_HOUR
import org.threeten.bp.LocalDateTime

class StressLineChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val gridPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grid)
        style = Paint.Style.STROKE
    }

    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.stress)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val dotPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.stress)
        style = Paint.Style.FILL
    }

    private var source: List<Stress>? = null
    private var x1: Int = 0
    private var x2: Int = 24
    private var dotRadius: Float = 2f

    fun setData(_source: List<Stress>) {
        if (_source.isEmpty()) return
        source = _source

        invalidate()
    }

    fun setScope(left: Int, right: Int) {
        layoutParams = layoutParams.apply {
            width = WIDTH_PER_HOUR * (right - left)
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
        val marginTop = 0.1f
        val marginBot = 0.1f

        canvas?.apply {
            Log.e("hello", "hello $source")
            if (source == null) return@apply

            (x1..x2).forEach { x ->
                drawLine(
                    (x - x1).toFloat() * WIDTH_PER_HOUR,
                    0f,
                    (x - x1).toFloat() * WIDTH_PER_HOUR,
                    h,
                    gridPaint
                )
            }

            source!!.indices.forEach { i ->
                val p1 = Pair(
                    (source!![i].x - x1) * WIDTH_PER_HOUR,
                    (1 - source!![i].y) * h * (1 - marginTop - marginBot) + h * marginTop
                )
                if (i < source!!.size - 1) {
                    val p2 = Pair(
                        (source!![i + 1].x - x1) * WIDTH_PER_HOUR,
                        (1 - source!![i + 1].y) * h * (1 - marginTop - marginBot) + h * marginTop
                    )
                    drawLine(p1.first, p1.second, p2.first, p2.second, linePaint)
                }
                drawCircle(p1.first, p1.second, dotRadius, dotPaint)
            }
        }
    }

    init {
        setWillNotDraw(false)
    }

    companion object {
        val defaultInfo: List<Stress> = listOf(
            Stress(LocalDateTime.now(), 0.5f),
        )
    }
}
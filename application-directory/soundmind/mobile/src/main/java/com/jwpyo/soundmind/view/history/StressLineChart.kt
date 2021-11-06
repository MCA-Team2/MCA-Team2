package com.jwpyo.soundmind.view.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jwpyo.soundmind.model.stress.Stress
import org.threeten.bp.LocalDateTime

class StressLineChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LineChart(context, attrs, defStyleAttr) {
    private val colorGray = Color.parseColor("#EEEEEE")
    private val colorBackground = Color.parseColor("#F1F1F1")
    private val colorText = Color.parseColor("#707070")

    fun setData(source: List<Stress>) {
        if (source.isEmpty()) return

        val lineSet = LineDataSet(
            source.map { Entry(it.x, it.y) },
            "line data set"
        ).also { it.initDefaultAttributes() }

        data = LineData(lineSet)
        invalidate()
    }

    fun setScope(left: LocalDateTime, right: LocalDateTime) {
        xAxis.axisMaximum = left.hour.toFloat() + 1
        xAxis.axisMinimum = right.hour.toFloat()
        layoutParams.width = WIDTH_PER_HOUR * (right.hour - left.hour + 1)
    }

    private fun initDefaultAttributes() {
        axisLeft.axisMaximum = 2.5f
        axisLeft.axisMinimum = -2.5f
        axisLeft.isEnabled = false
        axisRight.isEnabled = false
        description.isEnabled = false
        isDragEnabled = false
        isScaleXEnabled = false
        isScaleYEnabled = false
        legend.isEnabled = false
        xAxis.granularity = 1f
        xAxis.axisMaximum = 24f
        xAxis.axisMinimum = 0f
        xAxis.gridColor = colorBackground
        xAxis.gridLineWidth = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = colorText

        setBackgroundColor(Color.TRANSPARENT)
        setExtraOffsets(0f, 0f, 0f, 0f)
        setTouchEnabled(false)
        setViewPortOffsets(0f, 0f, 0f, 2f)
    }

    private fun LineDataSet.initDefaultAttributes() {
        setDrawValues(false)
        setDrawCircleHole(true)
        setDrawCircles(true)
        circleColors = mutableListOf(colorGray)
        circleRadius = 4f
        circleHoleRadius = 2f
        color = colorGray
        lineWidth = 3f
    }

    init {
        initDefaultAttributes()
    }

    companion object {
        const val WIDTH_PER_HOUR = 300

        val defaultInfo: List<Stress> = listOf(
            Stress(LocalDateTime.now().toLocalDate().atTime(9, 50), 0.2f),
            Stress(LocalDateTime.now().toLocalDate().atTime(9, 55), 0.75f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 0), 0.6f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 5), 1.5f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 10), 2.2f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 15), 3.5f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 20), 4.9f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 25), 3.5f),
            Stress(LocalDateTime.now().toLocalDate().atTime(10, 30), 2.2f),
        )
    }
}
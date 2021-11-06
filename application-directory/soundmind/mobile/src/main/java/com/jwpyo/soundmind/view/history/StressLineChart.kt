package com.jwpyo.soundmind.view.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jwpyo.soundmind.model.ui.StressItem
import org.threeten.bp.LocalDate

class StressLineChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LineChart(context, attrs, defStyleAttr) {
    private val colorGray = Color.parseColor("#EEEEEE")
    private val colorBackground = Color.parseColor("#F1F1F1")
    private val colorText = Color.parseColor("#707070")

    fun setData(source: List<StressItem>) {
        if (source.isEmpty()) return

        val lineSet = LineDataSet(
            source.map { Entry(it.x, it.y) },
            "line data set"
        ).also { it.initDefaultAttributes() }

        xAxis.axisMaximum = source.last().ldt.hour.toFloat() + 1
        xAxis.axisMinimum = source.first().ldt.hour.toFloat()

        data = LineData(lineSet)
        invalidate()
    }

    private fun initDefaultAttributes() {
        axisLeft.axisMaximum = 2f
        axisLeft.axisMinimum = -2f
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
}
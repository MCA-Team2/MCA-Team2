package com.example.heartratefeature

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.heartratefeature.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import uk.me.berndporr.iirj.Butterworth
import java.lang.Double.max

class MainActivity : Activity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var mPPG: Sensor? = null
    private var textView: TextView? = null

    private val TYPE_PPG_RAW = 65572
    private val TAG = "MCA"

    // PPG related
    private var timestampStart: Long = 0
    private val ppgData: MutableList<Double> = mutableListOf()

    private val ANALYSIS_INTERVAL: Long = 300 // in seconds
    private val CENTER_FREQUENCY: Double = 2.1 // Hz
    private val WIDTH_FREQUENCY : Double = 2.8 // Hz
    private val BPM_MIN : Double = 40.0
    private val BPM_MAX : Double = 180.0

    // Temporary sampling freq
    // based on Mobvoi TicWatch Pro
    // TODO: need to be calculated dynamically
    private val SAMPLING_FREQUENCY: Double = 208.4

    // Chart (Debug)
    private var chartView: LineChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Heart Rate PPG RAW Data
        mPPG = sensorManager.getDefaultSensor(TYPE_PPG_RAW)

        textView = findViewById(R.id.text)

        // Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BODY_SENSORS), 99)
        }

        // Start foreground service (preventing from sensor stop)
        val intent = Intent(this, HeartRateService::class.java).apply {
            putExtra("ServiceSignal", true)
        }
        startForegroundService(intent)

        // listening PPG
        mPPG?.also { heart ->
            sensorManager.registerListener(this, heart, SensorManager.SENSOR_DELAY_FASTEST)
        }

        // Chart (Debug)
        chartView = findViewById(R.id.lineChart)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value = event?.values?.get(0)
        val timestamp = event?.timestamp
        if (value != null && timestamp != null) {
            when {
                ppgData.size == 0 -> {
                    timestampStart = timestamp
                }
                timestamp - timestampStart < ANALYSIS_INTERVAL * 1000000000 -> { }
                else -> {
                    analyzeRR(analyzePPG(ppgData))
                    ppgData.clear()
                    timestampStart = timestamp
                }
            }
            ppgData.add(value.toDouble())
        }
    }

    private fun analyzePPG(ppgRaw : MutableList<Double>): MutableList<Double> {
        val ppgFiltered = mutableListOf<Double>()
        ppgFiltered.addAll(ppgRaw)

        // Forward filtering
        var offset = ppgFiltered[0]
        ppgFiltered.replaceAll{ value -> value - offset }

        val butterworth = Butterworth()
        butterworth.bandPass(2, SAMPLING_FREQUENCY, CENTER_FREQUENCY, WIDTH_FREQUENCY)
        ppgFiltered.replaceAll { value -> butterworth.filter(value) }

        // Backward filtering (to remove phase shift)
        ppgFiltered.reverse()
        offset = ppgFiltered[0]
        ppgFiltered.replaceAll{ value -> value - offset}

        val butterworth2 = Butterworth()
        butterworth2.bandPass(2, SAMPLING_FREQUENCY, CENTER_FREQUENCY, WIDTH_FREQUENCY)
        ppgFiltered.replaceAll { value -> butterworth2.filter(value) }
        ppgFiltered.reverse()

        // Baseline for moving average algorithm to work
        val ppgCopy = mutableListOf<Double>()
        ppgCopy.addAll(ppgFiltered)
        ppgCopy.sort()
        val bias = ppgCopy[(ppgFiltered.size * 0.9).toInt()]
        if (bias > 0) {
            ppgFiltered.replaceAll { value -> value - bias }
        }

        // Rolling mean calculation
        val slidingWindowHalfSize = (0.375 * SAMPLING_FREQUENCY).toInt()
        val ppgRollingMean = mutableListOf<Double>()
        var ppgRollingMeanTotal: Double = 0.0
        for (index in slidingWindowHalfSize until ppgFiltered.size - slidingWindowHalfSize) {
            var sum: Double = 0.0
            for (addIndex in index - slidingWindowHalfSize until index + slidingWindowHalfSize + 1) {
                sum += ppgFiltered[addIndex]
            }
            sum /= (slidingWindowHalfSize * 2 + 1)
            if (index == slidingWindowHalfSize || index == ppgFiltered.size - slidingWindowHalfSize - 1) {
                for (k in 0 until slidingWindowHalfSize) {
                    ppgRollingMean.add(sum)
                }
            }
            ppgRollingMean.add(sum)
        }
        ppgRollingMean.forEach { value -> ppgRollingMeanTotal += value }
        ppgRollingMeanTotal /= ppgRollingMean.size

        // Detect peaks
        val thresholdLevel = mutableListOf<Double>(
            0.05, 0.1, 0.15, 0.2, 0.25,
            0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.5, 2.0, 3.0
        )
        val optimalPPGPeak = mutableListOf<Int>()
        var optimalPPGRR = mutableListOf<Double>()
        var optimalRRStd = Double.MAX_VALUE

        for (threshold in thresholdLevel) {
            val ppgPeak = mutableListOf<Int>()
            val ppgRR = mutableListOf<Double>()
            var ignore: Boolean = true
            var findPeak: Boolean = false
            var indexPeak: Int = 0

            for (index in 0 until ppgFiltered.size) {
                val currentThreshold =
                    ppgRollingMean[index] + ppgRollingMeanTotal * threshold
                if (ppgFiltered[index] < currentThreshold) {
                    if (!ignore) {
                        if (!findPeak || (ppgFiltered[indexPeak] > ppgFiltered[index])) {
                            indexPeak = index
                        }
                        findPeak = true
                    }
                } else if (findPeak) {
                    ppgPeak.add(indexPeak)
                    findPeak = false
                } else {
                    ignore = false
                }
            }

            val bpm: Double = ppgPeak.size * 60.0 / ANALYSIS_INTERVAL
            var rrSquared: Double = 0.0
            var rrSum: Double = 0.0
            for (index in 0 until ppgPeak.size - 1) {
                val rr = (ppgPeak[index + 1] - ppgPeak[index]) / SAMPLING_FREQUENCY
                ppgRR.add(rr)
                rrSum += rr
                rrSquared += rr * rr
            }
            rrSquared /= (ppgRR.size - 1)
            rrSum /= (ppgRR.size - 1)
            val rrStd = kotlin.math.sqrt(rrSquared - rrSum * rrSum)

            if (rrStd < optimalRRStd && bpm <= BPM_MAX && BPM_MIN <= bpm) {
                optimalRRStd = rrStd
                optimalPPGPeak.clear()
                optimalPPGPeak.addAll(ppgPeak)
            }
        }

        if (optimalRRStd == Double.MAX_VALUE) {
            // Failed to find optimal solution
            return mutableListOf<Double>()
        }

        // TODO : Interpolate peaks for higher precision

        // Calculate RR interval
        for (index in 0 until optimalPPGPeak.size - 1) {
            val rr = (optimalPPGPeak[index + 1] - optimalPPGPeak[index]) / SAMPLING_FREQUENCY
            optimalPPGRR.add(rr)
        }

        // Define possible RR range and reject outliers
        var meanRR = 0.0
        var rangeRR = 0.0
        optimalPPGRR.forEach { value -> meanRR += value }
        meanRR /= optimalPPGRR.size
        rangeRR = max(0.3 * meanRR, 0.3)
        optimalPPGRR = optimalPPGRR.filter{
            value -> (meanRR - rangeRR < value && meanRR + rangeRR > value)
        } as MutableList<Double>

        // Reject outlier once more, use IQR
        val optimalPPGDataRRCopy = mutableListOf<Double>()
        optimalPPGDataRRCopy.addAll(optimalPPGRR)
        optimalPPGDataRRCopy.sort()
        val q1RR = optimalPPGDataRRCopy[(optimalPPGDataRRCopy.size * 0.25).toInt()]
        val q3RR = optimalPPGDataRRCopy[(optimalPPGDataRRCopy.size * 0.75).toInt()]
        optimalPPGRR = optimalPPGRR.filter {
            value ->  (value > q1RR * 1.5 - q3RR * 0.5) && (value < q3RR * 1.5 - q1RR * 0.5)
        } as MutableList<Double>

        optimalPPGRR.forEach{ value -> Log.d(TAG, value.toString()) }

        /*
        // Visualize Result
        val dataSets = ArrayList<ILineDataSet>()

        // Draw PPG and Peaks
        val entries = mutableListOf<Entry>()
        ppgFiltered.forEachIndexed { index, value ->
            if (index % 10 == 0 && index < ppgFiltered.size / 3) {
                entries.add(Entry(index.toFloat(), value.toFloat()))
            }
        }
        val lineDataSet = LineDataSet(entries, "HR")
        lineDataSet.color = Color.RED
        lineDataSet.setDrawCircles(false)
        dataSets.add(lineDataSet)

        val peakEntries = mutableListOf<Entry>()
        optimalPPGPeak.forEach { value ->
            if (value < ppgFiltered.size / 3)
                peakEntries.add(Entry(value.toFloat(), ppgFiltered[value].toFloat()))
        }
        val ppgPeakDataSet = LineDataSet(peakEntries, "HR")
        ppgPeakDataSet.color = Color.TRANSPARENT
        ppgPeakDataSet.circleSize = 2F
        dataSets.add(ppgPeakDataSet)

        val data = LineData(dataSets)
        chartView?.description = null
        chartView?.setViewPortOffsets(0.0F, 0.0F, 0.0F, 0.0F)
        chartView?.data = data
        chartView?.notifyDataSetChanged()
        chartView?.invalidate()
        chartView?.setTouchEnabled(false)
        chartView?.axisLeft?.setDrawGridLines(false)
        chartView?.axisLeft?.setDrawAxisLine(false)
        chartView?.axisRight?.setDrawGridLines(false)
        chartView?.axisRight?.setDrawAxisLine(false)
        chartView?.xAxis?.setDrawGridLines(false)
        chartView?.xAxis?.setDrawAxisLine(false)
        chartView?.legend?.isEnabled = false
         */
        return optimalPPGRR
    }

    private fun analyzeRR(rrY : MutableList<Double>) {
        if (rrY.isEmpty()) return

        val resampleFactor = 4
        val dataLength = (rrY.size - 1) * resampleFactor
        val rrX = mutableListOf<Double>()
        var rrXAccumulator = 0.0
        rrY.forEach { value ->
            rrXAccumulator += value
            rrX.add(rrXAccumulator)
        }
        val rrXNew = mutableListOf<Double>()
        val rrXInterval = (rrX.last() - rrX.first()) / (dataLength - 1)
        for (i in 0 until dataLength) {
            rrXNew.add(rrXInterval * i + rrX.first())
        }

        // Resampling with Cubic Hermite spline
        val rrTangent = mutableListOf<Double>()
        rrTangent.add((rrY[1] - rrY[0]) / (rrX[1] - rrX[0]))
        for (i in 1 until rrY.size - 1) {
            rrTangent.add((rrY[i+1] - rrY[i-1]) / (rrX[i+1] - rrX[i-1]))
        }
        rrTangent.add((rrY[rrY.size - 1] - rrY[rrY.size - 2]) / (rrX[rrY.size - 1] / rrX[rrY.size - 2]))
        val rrYNew = mutableListOf<Double>()
        var index = 1
        rrXNew.forEach { value ->
            while (rrX[index] < value && index < rrX.size - 1) {
                index++
            }
            val t = (value - rrX[index - 1]) / (rrX[index] - rrX[index - 1])
            val t2 = t*t
            val t3 = t2*t
            rrYNew.add((2*t3-3*t2+1) * rrY[index - 1] + (-2*t3+3*t2) * rrY[index]
                    + (t3-2*t2+t) * rrTangent[index-1] + (t3-t2) * rrTangent[index])
        }

        // TODO : Implement Welch's method

        // This time just DFT

        // RR interval Visualization
        val dataSets = ArrayList<ILineDataSet>()

        val rrEntries = mutableListOf<Entry>()
        for (i in 0 until rrXNew.size) {
            rrEntries.add(Entry(rrXNew[i].toFloat(), rrYNew[i].toFloat()))
        }
        val rrDataSet = LineDataSet(rrEntries, "RR Interval")
        rrDataSet.valueTextSize = 0.0F
        rrDataSet.setDrawCircles(false)
        dataSets.add(rrDataSet)

        /*
        val rrLineEntries = mutableListOf<Entry>()
        for (i in 0 until rrX.size) {
            rrLineEntries.add(Entry(rrX[i].toFloat(), rrY[i].toFloat()))
        }
        val rrLineDataSet = LineDataSet(rrLineEntries, "RR Interval")
        rrLineDataSet.valueTextSize = 0.0F
        rrLineDataSet.color = Color.RED
        rrLineDataSet.setDrawCircles(false)
        dataSets.add(rrLineDataSet)
        */

        val data = LineData(dataSets)
        chartView?.description = null
        chartView?.setViewPortOffsets(0.0F, 0.0F, 0.0F, 0.0F)
        chartView?.data = data
        chartView?.notifyDataSetChanged()
        chartView?.invalidate()
        chartView?.setTouchEnabled(false)
        chartView?.axisLeft?.setDrawGridLines(false)
        chartView?.axisLeft?.setDrawAxisLine(false)
        chartView?.axisRight?.setDrawGridLines(false)
        chartView?.axisRight?.setDrawAxisLine(false)
        chartView?.xAxis?.setDrawGridLines(false)
        chartView?.xAxis?.setDrawAxisLine(false)
        chartView?.legend?.isEnabled = false
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        val intent = Intent(this, HeartRateService::class.java).apply {
            putExtra("ServiceSignal", false)
        }
        startForegroundService(intent)
    }
}
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
    private val butterworth: Butterworth = Butterworth()

    private val ANALYSIS_INTERVAL: Long = 30 // in seconds
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

        // Initialize band-pass filter, [0.75 - 2.5]Hz
        butterworth.bandPass(2, SAMPLING_FREQUENCY, CENTER_FREQUENCY, WIDTH_FREQUENCY)

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
                    // TODO : Initialize properly...
                    for (i in 1..50) {
                        butterworth.filter(value.toDouble())
                    }
                }
                timestamp - timestampStart < ANALYSIS_INTERVAL * 1000000000 -> { }
                else -> {
                    analyzePPG()
                    ppgData.clear()
                    timestampStart = timestamp
                }
            }
            val filteredValue = butterworth.filter(value.toDouble())
            ppgData.add(filteredValue)
        }
    }

    private fun analyzePPG() {
        // Backward filtering (to remove phase shift)
        ppgData.reverse()
        val butterworth2 = Butterworth()
        butterworth2.bandPass(2, SAMPLING_FREQUENCY, CENTER_FREQUENCY, WIDTH_FREQUENCY)

        // TODO : Initialize properly...
        for (i in 1..50) {
            butterworth2.filter(ppgData[0])
        }

        ppgData.replaceAll { value -> butterworth2.filter(value) }
        ppgData.reverse()

        // Simple peak candidate : local minima
        /*
        val ppgPeakRange = 3
        val ppgPeakCandidate = mutableListOf<Int>()
        for (index in ppgPeakRange until ppgData.size - ppgPeakRange) {
            var isMinimum = true
            for (searchIndex in index - ppgPeakRange until index + ppgPeakRange + 1) {
                if (ppgData[searchIndex] < ppgData[index]) {
                    isMinimum = false
                    break
                }
            }
            if (isMinimum) {
                ppgPeakCandidate.add(index)
            }
        }
        */

        // Baseline for moving average algorithm to work
        val ppgDataCopy = mutableListOf<Double>()
        ppgDataCopy.addAll(ppgData)
        ppgDataCopy.sort()
        val bias = ppgDataCopy[(ppgData.size * 0.9).toInt()]
        if (bias > 0) {
            ppgData.replaceAll { value -> value - bias }
        }

        // Rolling mean calculation
        val slidingWindowHalfSize = (0.375 * SAMPLING_FREQUENCY).toInt()
        val ppgDataRollingMean = mutableListOf<Double>()
        var ppgDataRollingMeanTotal: Double = 0.0
        for (index in slidingWindowHalfSize until ppgData.size - slidingWindowHalfSize) {
            var sum: Double = 0.0
            for (addIndex in index - slidingWindowHalfSize until index + slidingWindowHalfSize + 1) {
                sum += ppgData[addIndex]
            }
            sum /= (slidingWindowHalfSize * 2 + 1)
            if (index == slidingWindowHalfSize || index == ppgData.size - slidingWindowHalfSize - 1) {
                for (k in 0 until slidingWindowHalfSize) {
                    ppgDataRollingMean.add(sum)
                }
            }
            ppgDataRollingMean.add(sum)
        }
        ppgDataRollingMean.forEach { value -> ppgDataRollingMeanTotal += value }
        ppgDataRollingMeanTotal /= ppgDataRollingMean.size

        // Draw graph
        val entries = mutableListOf<Entry>()
        ppgData.forEachIndexed { index, value ->
            if (index % 10 == 0 && index < SAMPLING_FREQUENCY * ANALYSIS_INTERVAL / 3) {
                entries.add(Entry(index.toFloat(), value.toFloat()))
            }
        }
        val lineDataSet = LineDataSet(entries, "HR")
        lineDataSet.color = Color.RED
        lineDataSet.setDrawCircles(false)

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)

        // Detect peaks
        val thresholdLevel = mutableListOf<Double>(
            0.05, 0.1, 0.15, 0.2, 0.25,
            0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.5, 2.0, 3.0
        )
        val optimalPPGDataPeak = mutableListOf<Int>()
        val optimalPPGDataRR = mutableListOf<Double>()
        var optimalRRStd = Double.MAX_VALUE
        var optimalBPM = 0.0

        for (threshold in thresholdLevel) {
            val ppgDataPeak = mutableListOf<Int>()
            val ppgDataRR = mutableListOf<Double>()
            var ignore: Boolean = true
            var findPeak: Boolean = false
            var indexPeak: Int = 0

            for (index in 0 until ppgData.size) {
                val currentThreshold =
                    ppgDataRollingMean[index] + ppgDataRollingMeanTotal * threshold
                if (ppgData[index] < currentThreshold) {
                    if (!ignore) {
                        if (!findPeak || (ppgData[indexPeak] > ppgData[index])) {
                            indexPeak = index
                        }
                        findPeak = true
                    }
                } else if (findPeak) {
                    ppgDataPeak.add(indexPeak)
                    findPeak = false
                } else {
                    ignore = false
                }
            }

            val bpm: Double = ppgDataPeak.size * 60.0 / ANALYSIS_INTERVAL
            var rrSquared: Double = 0.0
            var rrSum: Double = 0.0
            for (index in 0 until ppgDataPeak.size - 1) {
                val rr = (ppgDataPeak[index + 1] - ppgDataPeak[index]) / SAMPLING_FREQUENCY
                ppgDataRR.add(rr)
                rrSum += rr
                rrSquared += rr * rr
            }
            rrSquared /= (ppgDataRR.size - 1)
            rrSum /= (ppgDataRR.size - 1)
            val rrStd = kotlin.math.sqrt(rrSquared - rrSum * rrSum)

            if (rrStd < optimalRRStd && bpm <= BPM_MAX && BPM_MIN <= bpm) {
                optimalRRStd = rrStd
                optimalBPM = bpm
                optimalPPGDataPeak.clear()
                optimalPPGDataPeak.addAll(ppgDataPeak)
            }
        }

        if (optimalRRStd == Double.MAX_VALUE) {
            // Failed to find optimal solution
            return
        }

        // TODO : Interpolate peaks for higher precision

        // Calculate RR interval
        for (index in 0 until optimalPPGDataPeak.size - 1) {
            val rr = (optimalPPGDataPeak[index + 1] - optimalPPGDataPeak[index]) / SAMPLING_FREQUENCY
            optimalPPGDataRR.add(rr)
        }

        // Define RR range and reject outliers
        var meanRR = 0.0
        var rangeRR = 0.0
        val optimalPPGDataRR2 = mutableListOf<Double>()
        optimalPPGDataRR.forEach { value -> meanRR += value }
        meanRR /= optimalPPGDataRR.size
        rangeRR = max(0.3 * meanRR, 0.3)
        optimalPPGDataRR2.addAll(optimalPPGDataRR.filter{ value -> (meanRR - rangeRR < value && meanRR + rangeRR > value) })
        optimalPPGDataRR2.forEach {
            Log.d(TAG, it.toString())
        }

        // Visualize Result
        val peakEntries = mutableListOf<Entry>()
        optimalPPGDataPeak.forEach { value ->
            if (value < SAMPLING_FREQUENCY * ANALYSIS_INTERVAL / 3)
                peakEntries.add(Entry(value.toFloat(), ppgData[value].toFloat()))
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
        textView?.text = "BPM " + optimalBPM.toInt().toString()
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
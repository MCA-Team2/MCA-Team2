package com.jwpyo.soundmind.utils

import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.PPG
import org.jtransforms.fft.DoubleFFT_1D
import uk.me.berndporr.iirj.Butterworth

class PPGConverter {

    fun getStress(ppgList: Array<PPG>): Stress? {
        if (ppgList.size < REQUIRE_SAMPLE_NUMBER) return null
        val ldt = ppgList.maxOf { it.ldt }
        val stressValue = analyzeRR(
            analyzePPG(ppgList.map { ppg -> ppg.sensorValue.toDouble() }.toMutableList())
        ).toFloat()
        return Stress(ldt, stressValue)
    }

    private fun analyzePPG(ppgRaw: MutableList<Double>): MutableList<Double> {
        val samplingFrequency: Double = ppgRaw.size / ANALYSIS_INTERVAL.toDouble()
        val ppgFiltered = mutableListOf<Double>()
        ppgFiltered.addAll(ppgRaw)

        // Forward filtering
        var offset = ppgFiltered[0]
        ppgFiltered.replaceAll { value -> value - offset }

        val butterworth = Butterworth()
        butterworth.bandPass(2, samplingFrequency, CENTER_FREQUENCY, WIDTH_FREQUENCY)
        ppgFiltered.replaceAll { value -> butterworth.filter(value) }

        // Backward filtering (to remove phase shift)
        ppgFiltered.reverse()
        offset = ppgFiltered[0]
        ppgFiltered.replaceAll { value -> value - offset }

        val butterworth2 = Butterworth()
        butterworth2.bandPass(2, samplingFrequency, CENTER_FREQUENCY, WIDTH_FREQUENCY)
        ppgFiltered.replaceAll { value -> butterworth2.filter(value) }
        ppgFiltered.reverse()

        // Baseline for moving average algorithm to work
        val ppgCopy = mutableListOf<Double>()
        ppgCopy.addAll(ppgFiltered)
        ppgCopy.sort()
        val bias = ppgCopy[(ppgFiltered.size * 0.1).toInt()]
        if (bias < 0) {
            ppgFiltered.replaceAll { value -> value - bias }
        }

        // Rolling mean calculation
        val slidingWindowHalfSize = (0.375 * samplingFrequency).toInt()
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
        val optimalPPGRR = mutableListOf<Double>()
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
                if (ppgFiltered[index] > currentThreshold) {
                    if (!ignore) {
                        if (!findPeak || (ppgFiltered[indexPeak] < ppgFiltered[index])) {
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
            var rrSquared = 0.0
            var rrSum = 0.0
            for (index in 0 until ppgPeak.size - 1) {
                val rr = (ppgPeak[index + 1] - ppgPeak[index]) / samplingFrequency
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
            val rr = (optimalPPGPeak[index + 1] - optimalPPGPeak[index]) / samplingFrequency
            optimalPPGRR.add(rr)
        }

        // Define RR range and reject peak anomaly
        var meanRR = 0.0
        var rangeRR = 0.0
        var optimalPPGRRCorrection = mutableListOf<Double>()
        optimalPPGRR.forEach { value -> meanRR += value }
        meanRR /= optimalPPGRR.size
        rangeRR = java.lang.Double.max(0.3 * meanRR, 0.3)
        for (i in 0 until optimalPPGRR.size - 1) {
            val val1 = optimalPPGRR[i]
            val val2 = optimalPPGRR[i + 1]
            if (meanRR - rangeRR < val1 && meanRR + rangeRR > val1 &&
                meanRR - rangeRR < val2 && meanRR + rangeRR > val2
            ) {
                optimalPPGRRCorrection.add(val1)
            }
        }
        if (meanRR - rangeRR < optimalPPGRR.last() && meanRR + rangeRR > optimalPPGRR.last()) {
            optimalPPGRRCorrection.add(optimalPPGRR.last())
        }

        // Reject strange RR interval, use IQR
        val optimalPPGRRCopy = mutableListOf<Double>()
        optimalPPGRRCopy.addAll(optimalPPGRRCorrection)
        optimalPPGRRCopy.sort()
        val q1RR = optimalPPGRRCopy[(optimalPPGRRCopy.size * 0.25).toInt()]
        val q3RR = optimalPPGRRCopy[(optimalPPGRRCopy.size * 0.75).toInt()]
        optimalPPGRRCorrection = optimalPPGRRCorrection.filter { value ->
            (value > q1RR * 2.5 - q3RR * 1.5) && (value < q3RR * 2.5 - q1RR * 1.5)
        } as MutableList<Double>

        return optimalPPGRRCorrection
    }

    private fun analyzeRR(rrY: MutableList<Double>): Double {
        if (rrY.isEmpty()) return Double.NaN

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
            rrTangent.add((rrY[i + 1] - rrY[i - 1]) / (rrX[i + 1] - rrX[i - 1]))
        }
        rrTangent.add((rrY[rrY.size - 1] - rrY[rrY.size - 2]) / (rrX[rrY.size - 1] / rrX[rrY.size - 2]))
        val rrYNew = mutableListOf<Double>()
        var index = 1
        rrXNew.forEach { value ->
            while (rrX[index] < value && index < rrX.size - 1) {
                index++
            }
            val t = (value - rrX[index - 1]) / (rrX[index] - rrX[index - 1])
            val t2 = t * t
            val t3 = t2 * t
            rrYNew.add(
                (2 * t3 - 3 * t2 + 1) * rrY[index - 1] + (-2 * t3 + 3 * t2) * rrY[index]
                        + (t3 - 2 * t2 + t) * rrTangent[index - 1] + (t3 - t2) * rrTangent[index]
            )
        }

        // Just FFT
        val binFrequency = 1 / (rrXNew.last() - rrXNew.first())
        val input = DoubleArray(rrXNew.size * 2)
        val rrMagnitude = mutableListOf<Double>()
        for (i in 0 until rrXNew.size) {
            input[i] = rrYNew[i]
        }
        val fft = DoubleFFT_1D(rrXNew.size.toLong())
        fft.realForward(input)
        for (i in 0 until rrXNew.size / 2) {
            // Unit : s^2/Hz
            rrMagnitude.add((input[i * 2] * input[i * 2] + input[i * 2 + 1] * input[i * 2 + 1]) / binFrequency)
        }

        var lf = 0.0
        var hf = 0.0
        for (i in 0 until rrMagnitude.size) {
            lf += java.lang.Double.max(
                java.lang.Double.min(
                    java.lang.Double.min(
                        (i + 0.5) * binFrequency - 0.04,
                        0.15 - (i - 0.5) * binFrequency
                    ), binFrequency
                ), 0.0
            ) * rrMagnitude[i]
            hf += java.lang.Double.max(
                java.lang.Double.min(
                    java.lang.Double.min(
                        (i + 0.5) * binFrequency - 0.15,
                        0.40 - (i - 0.5) * binFrequency
                    ), binFrequency
                ), 0.0
            ) * rrMagnitude[i]
        }

        return lf / hf
    }

    companion object {
        const val REQUIRE_SAMPLE_NUMBER: Int = 50000
        const val ANALYSIS_INTERVAL: Long = 300L // in seconds
        const val CENTER_FREQUENCY: Double = 2.1 // Hz
        const val WIDTH_FREQUENCY: Double = 2.8 // Hz
        const val BPM_MIN: Double = 40.0
        const val BPM_MAX: Double = 180.0
    }
}
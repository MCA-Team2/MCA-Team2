package com.jwpyo.soundmind.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.io.File

fun checkBell(context: Context, byteArray: ByteArray) : Boolean {
    val REQUEST_RECORD_AUDIO = 1337
    val TAG = "AudioDemo"
    val MODEL_FILE = "yamnet.tflite"
    val MINIMUM_DISPLAY_THRESHOLD: Float = 0.3f

    var audioClassifier: AudioClassifier? = null
    var classificationInterval = 500L // how often should classification run in milli-secs
    lateinit var handler: Handler // background thread handler to run classification

    // Create a handler to run classification in a background thread
    val handlerThread = HandlerThread("backgroundThread")
    handlerThread.start()
    handler = HandlerCompat.createAsync(handlerThread.looper)

    // Initialize the audio classifier
    val classifier = AudioClassifier.createFromFile(context, MODEL_FILE)
    val audioTensor = classifier.createInputTensorAudio()

    // Define the classification runnable
    val run = object : Runnable {
        override fun run() {
            val startTime = System.currentTimeMillis()

            // TODO : Load the latest audio sample (ByteArray --> Short[] or float[])
            // audioTensor.load()
            val output = classifier.classify(audioTensor)

            // Filter out results above a certain threshold, and sort them descendingly
            val filteredModelOutput = output[0].categories.filter {
                it.score > MINIMUM_DISPLAY_THRESHOLD
            }.sortedBy {
                -it.score
            }

            val finishTime = System.currentTimeMillis()

            Log.d(TAG, "Latency = ${finishTime - startTime}ms")
            Log.d(TAG, filteredModelOutput.toString())

            // Rerun the classification after a certain interval
            handler.postDelayed(this, classificationInterval)
        }
    }

    // Start the classification process
    handler.post(run)

    // Save the instances we just created for use later
    handler.removeCallbacksAndMessages(null)
    audioClassifier = null

    return true
}
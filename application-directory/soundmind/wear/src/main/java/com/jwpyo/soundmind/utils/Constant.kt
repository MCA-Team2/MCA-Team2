package com.jwpyo.soundmind.utils

object Constant {
    const val COUNT_PATH = "/count"
    const val COUNT_KEY = "count"

    const val START_ACTIVITY_PATH = "/start-activity"
    const val START_RECORD_PATH = "/start-record"
    const val STOP_RECORD_PATH = "/stop-record"

    const val AUDIO_PATH = "/audio"
    const val AUDIO_KEY = "record"
    const val START_TIME_KEY = "start-time"
    const val END_TIME_KEY = "end-time"

    const val PPG_PATH = "/ppg-%s"
    const val SENSOR_VALUE_KEY = "sensor-value"
    const val ACCURACY_KEY = "accuracy"
    const val TIME_STAMP_KEY = "time-stamp"

    const val RECORDING_RATE = 8000 // can go up to 44K, if needed
    const val AUDIO_CHUNK_SIZE = 160000 // 16000 per sec
    const val PPG_CHUNK_SIZE = 1000 // almost 100 per sec

    const val REQUEST_PERMISSION_CODE = 2
}
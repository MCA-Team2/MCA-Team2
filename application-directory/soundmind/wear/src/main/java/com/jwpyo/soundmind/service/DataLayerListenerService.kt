package com.jwpyo.soundmind.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.jwpyo.soundmind.utils.AppState
import com.jwpyo.soundmind.utils.Constant.START_ACTIVITY_PATH
import com.jwpyo.soundmind.utils.Constant.START_RECORD_PATH
import com.jwpyo.soundmind.utils.Constant.STOP_RECORD_PATH
import com.jwpyo.soundmind.view.main.MainActivity
import org.koin.android.ext.android.inject

class DataLayerListenerService: WearableListenerService() {
    private val appState: AppState by inject()

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Check to see if the message is to start an activity
        when(messageEvent.path) {
            START_ACTIVITY_PATH -> {
                wakeup()
            }
            START_RECORD_PATH -> {
                wakeup()
                appState.recordState.postValue(true)
            }
            STOP_RECORD_PATH -> {
                wakeup()
                appState.recordState.postValue(false)
            }
        }
    }

    private fun wakeup() {
        val startIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(startIntent)
    }
}
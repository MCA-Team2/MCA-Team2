package com.jwpyo.soundmind.service

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.jwpyo.soundmind.utils.Constant.START_ACTIVITY_PATH
import com.jwpyo.soundmind.utils.Constant.STOP_ACTIVITY_PATH
import com.jwpyo.soundmind.view.main.MainActivity

class DataLayerListenerService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Check to see if the message is to start an activity
        when(messageEvent.path) {
            START_ACTIVITY_PATH -> {
                val startIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("keep", true)
                }

                startActivity(startIntent)
            }
            STOP_ACTIVITY_PATH -> {
                val startIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("keep", false)
                }
                startActivity(startIntent)
            }
        }
    }
}
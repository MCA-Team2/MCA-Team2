package com.jwpyo.soundmind.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.jwpyo.soundmind.R
import kotlinx.coroutines.*

class MainService : Service() {
    var job: Job? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)

        val pendingIntent: PendingIntent =
            Intent(this, MainService::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_heart_signal)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)

        startRecording()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopRecording()

        super.onDestroy()
    }

    private fun startRecording() {
        job = CoroutineScope(Dispatchers.IO). launch {
            while (true) {
                Log.d("hello", "hello-service i'm alive")
                delay(1000L)
            }
        }
    }

    private fun stopRecording() {
        job?.cancel()
        Log.d("hello", "hello-service i'm removed")
    }

    companion object {
        const val CHANNEL_ID = "MAIN_SERVICE_ID"
        const val CHANNEL_NAME = "MAIN_SERVICE_NAME"
        const val ONGOING_NOTIFICATION_ID = 1

        fun getIntentForStart(
            context: Context? = null,
        ): Intent {
            return Intent(context, MainService::class.java).apply {
                // TODO> maybe we will need some extras
            }
        }
    }
}
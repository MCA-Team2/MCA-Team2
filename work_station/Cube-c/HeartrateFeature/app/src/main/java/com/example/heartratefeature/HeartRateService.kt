package com.example.heartratefeature

import android.content.Intent
import android.os.IBinder
import android.R
import android.app.*
import android.content.Context

import android.os.Build


class HeartRateService : Service() {

    private val CHANNEL_ID = "HRSERVICE"
    private val ONGOING_NOTIFICATION_ID = 11

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        val serviceSignal = intent?.getBooleanExtra("ServiceSignal", false)

        if (serviceSignal == true) {
            val notification: Notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Custom HR")
                .setContentText("mTE UR")
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "HI"
                val descriptionText = "Channel Description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Notification ID cannot be 0.
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        }
        else {
            stopForeground(true)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}
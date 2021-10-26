package com.jwpyo.soundmind.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jwpyo.soundmind.utils.RecordAlarmManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val recordAlarmManager: RecordAlarmManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            recordAlarmManager.scheduleDailyRecords()
        }
    }
}

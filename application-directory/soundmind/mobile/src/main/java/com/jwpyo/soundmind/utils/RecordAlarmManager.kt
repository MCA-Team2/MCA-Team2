package com.jwpyo.soundmind.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jwpyo.soundmind.service.RecordStartService
import org.threeten.bp.LocalTime
import java.util.*

class RecordAlarmManager(
    private val context: Context
) {
    companion object {
        private const val RECORD_SERVICE_ID = 2
    }

    fun scheduleDailyRecords() {
        scheduleRecordStart()
//        scheduleRecordStop()
    }

    private var am: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private fun scheduleRecordStart() {
        val intent = Intent(context, RecordStartService::class.java)
        cancelPendingIntent(intent, RECORD_SERVICE_ID)

        val sender = PendingIntent.getBroadcast(
            context,
            RECORD_SERVICE_ID,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        LocalTime.of(9, 0).also { triggerTime ->
            setAlarm(triggerTime, pendingIntent = sender)
        }
    }

    private fun setAlarm(
        triggerTime: LocalTime,
        pendingIntent: PendingIntent?
    ) {
        if (pendingIntent == null) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, triggerTime.hour)
            set(Calendar.MINUTE, triggerTime.minute)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelPendingIntent(intent: Intent, id: Int) {
        PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE)
            ?.let { previousSender ->
                previousSender.cancel()
                am?.cancel(previousSender)
            }
    }
}
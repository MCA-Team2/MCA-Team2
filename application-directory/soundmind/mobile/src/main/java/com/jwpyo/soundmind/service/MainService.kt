package com.jwpyo.soundmind.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.utils.Constant
import com.jwpyo.soundmind.view.main.MainViewModel
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.threeten.bp.LocalDateTime

class MainService : Service(), DataClient.OnDataChangedListener, KoinComponent {
    val mainViewModel: MainViewModel = get()
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter { it.type == DataEvent.TYPE_CHANGED }
            .forEach { dataEvent ->
                val path = dataEvent.dataItem.uri.path ?: return@forEach
                Log.d("hello", "hello $dataEvent")

                when {
                    path == Constant.AUDIO_PATH -> {
                        onAudioChangedEvent(dataEvent)
                    }
                    path.substring(
                        0,
                        Constant.PPG_PATH_PREFIX.length
                    ) == Constant.PPG_PATH_PREFIX -> {
                        onPPGChangeEvent(dataEvent, path.substring(Constant.PPG_PATH_PREFIX.length))
                    }
                }
            }
    }

    private fun startRecording() {
        Wearable.getDataClient(this).addListener(this)
    }

    private fun stopRecording() {
        Wearable.getDataClient(this).removeListener(this)
    }

    private fun onAudioChangedEvent(dataEvent: DataEvent) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val asset = dataMapItem.dataMap.getAsset(Constant.AUDIO_KEY)
            val startLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(Constant.START_TIME_KEY)
            )
            val endLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(Constant.END_TIME_KEY)
            )
            mainViewModel.insertVoice(asset!!, startLDT, endLDT)
        }.onFailure {
            Log.e("hello", "error: $it")
        }

    private fun onPPGChangeEvent(dataEvent: DataEvent, sensorName: String) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val sensorValue = dataMapItem.dataMap.getFloatArray(Constant.SENSOR_VALUE_KEY)!!
            val accuracy = dataMapItem.dataMap.getLongArray(Constant.ACCURACY_KEY)!!
            val timestamp = dataMapItem.dataMap.getLongArray(Constant.TIME_STAMP_KEY)!!

            val ppgList = sensorValue.indices.map { i ->
                PPG(sensorName, sensorValue[i], accuracy[i].toInt(), timestamp[i])
            }
            mainViewModel.insertPPG(ppgList)
        }.onFailure {
            Log.e("hello", "error: $it")
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
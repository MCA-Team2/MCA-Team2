package com.jwpyo.soundmind.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.speech.SpeechRecognizer
import android.util.Log
import com.google.android.gms.wearable.*
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.PPG
import com.jwpyo.soundmind.model.ui.PPG.Companion.HEART_RATE_PPG_RAW_DATA
import com.jwpyo.soundmind.utils.Constant.ACCURACY_KEY
import com.jwpyo.soundmind.utils.Constant.AUDIO_KEY
import com.jwpyo.soundmind.utils.Constant.AUDIO_PATH
import com.jwpyo.soundmind.utils.Constant.END_TIME_KEY
import com.jwpyo.soundmind.utils.Constant.PPG_PATH_PREFIX
import com.jwpyo.soundmind.utils.Constant.SENSOR_VALUE_KEY
import com.jwpyo.soundmind.utils.Constant.START_TIME_KEY
import com.jwpyo.soundmind.utils.Constant.TIME_STAMP_KEY
import com.jwpyo.soundmind.utils.PPGConverter
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.threeten.bp.LocalDateTime

class MainService : Service(), DataClient.OnDataChangedListener, KoinComponent {
    private val mainViewModel: MainViewModel = get()
    private val ppgConverter: PPGConverter = get()
    private val ppgCache: MutableList<List<PPG>> = mutableListOf()

    private var currentStressId: Long? = null

    // Parameters for STT
    private lateinit var intent: Intent
    private var mRecognizer: SpeechRecognizer? = null

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

//        startSTT()
        startRecording()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
//        stopSTT()
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
                    path == AUDIO_PATH -> {
                        onAudioChangedEvent(dataEvent)
                    }
                    path.substring(0, PPG_PATH_PREFIX.length) == PPG_PATH_PREFIX -> {
                        onPPGChangeEvent(dataEvent, path.substring(PPG_PATH_PREFIX.length))
                    }
                }
            }
    }


//    private val listener: RecognitionListener = object : RecognitionListener {
//        override fun onReadyForSpeech(params: Bundle) {
//            Toast.makeText(applicationContext, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onBeginningOfSpeech() {}
//        override fun onRmsChanged(rmsdB: Float) {}
//        override fun onBufferReceived(buffer: ByteArray) {}
//        override fun onEndOfSpeech() {}
//        override fun onError(error: Int) {
//            val message: String
//            message = when (error) {
//                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
//                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
//                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
//                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
//                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
//                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
//                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER가 바쁨"
//                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
//                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
//                else -> "알 수 없는 오류임"
//            }
//            Toast.makeText(applicationContext, "에러가 발생하였습니다. : $message", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onResults(results: Bundle) {
//            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
//            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//            Log.d("STT", "STT working")
//            for (i in matches!!.indices) {
//                Log.d("STT", "STT: " + matches[i])
//            }
//        }
//
//        override fun onPartialResults(partialResults: Bundle) {}
//        override fun onEvent(eventType: Int, params: Bundle) {}
//    }
//
//    private fun startSTT(){
//        Log.d("STT", "STT started")
//        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName())
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")
//        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//        mRecognizer?.setRecognitionListener(listener)
//        mRecognizer?.startListening(intent)
//    }
//
//    private fun stopSTT() {
//        if (mRecognizer != null) {
//            mRecognizer?.destroy()
//            mRecognizer?.cancel()
//            mRecognizer = null
//        }
//    }

    private fun startRecording() {
        Wearable.getDataClient(this).addListener(this)

    }

    private fun stopRecording() {
        Wearable.getDataClient(this).removeListener(this)
    }

    private fun onAudioChangedEvent(dataEvent: DataEvent) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val asset = dataMapItem.dataMap.getAsset(AUDIO_KEY)
            val startLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(START_TIME_KEY)
            )
            val endLDT = LocalDateTime.parse(
                dataMapItem.dataMap.getString(END_TIME_KEY)
            )
            mainViewModel.insertVoice(asset!!, startLDT, endLDT)
        }.onFailure {
            Log.e("hello", "error: $it")
        }

    private fun onPPGChangeEvent(dataEvent: DataEvent, sensorName: String) =
        runCatching {
            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
            val sensorValue = dataMapItem.dataMap.getFloatArray(SENSOR_VALUE_KEY)!!
            val accuracy = dataMapItem.dataMap.getLongArray(ACCURACY_KEY)!!
            val ldt = dataMapItem.dataMap.getStringArray(TIME_STAMP_KEY)!!
                .map { LocalDateTime.parse(it) }

            if (sensorName == HEART_RATE_PPG_RAW_DATA) {
                val ppgList = sensorValue.indices.map { i ->
                    PPG(sensorName, sensorValue[i], accuracy[i].toInt(), ldt[i])
                }
                ppgCache.add(ppgList)
                Log.e("hello", "hello ${ppgCache.size}")

                runCatching {
                    val stress = ppgConverter.getStress(ppgCache.flatten().toTypedArray())!!
                    mainViewModel.insertStress(stress.apply { id = currentStressId })
                }.onSuccess {
                    Log.e("hello", "hello success!!!")
                    currentStressId = null
                    ppgCache.removeAt(0)
                    Log.e("hello", "hello?? ${ppgCache.size}")
                }.onFailure {
                    currentStressId = mainViewModel.insertStress(
                        Stress(currentStressId, LocalDateTime.now(), Math.random().toFloat())
                    )
                    Log.e("hello", "hello error $it")
                }
            }
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
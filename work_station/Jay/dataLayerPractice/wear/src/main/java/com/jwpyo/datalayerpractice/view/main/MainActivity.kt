package com.jwpyo.datalayerpractice.view.main

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEvent.TYPE_DELETED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.base.BaseActivity
import com.jwpyo.datalayerpractice.databinding.ActivityMainBinding
import com.jwpyo.datalayerpractice.databinding.ActivityRecordBinding
import com.jwpyo.datalayerpractice.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity(), DataClient.OnDataChangedListener {

    private val binding: ActivityRecordBinding by binding(R.layout.activity_record)
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var btnStartRecord: Button
    private lateinit var btnStopRecord: Button
    private lateinit var btnFilePlay: Button
    private lateinit var btnFileStop: Button


    private val REQUEST_PERMISSION_CODE: Int = 1000;
    private val pathSave =
        Environment.getExternalStorageDirectory().absolutePath + "/" + UUID.randomUUID()
            .toString() + "_audio_record.3gp"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
//            vm = mainViewModel
            lifecycleOwner = this@MainActivity
        }

        AmbientModeSupport.attach(this)

        setEventListeners()

        // 지상
        btnStartRecord = findViewById(R.id.record_start_button)
        btnStopRecord = findViewById(R.id.record_stop_button)
        btnFilePlay = findViewById(R.id.file_play_button)
        btnFileStop = findViewById(R.id.file_stop_button)

        if(!checkPermissionFromDevice())
            requestPermission()

        btnStartRecord.setOnClickListener {
                Log.d(TAG, "Record Start")
                setupMediaRecorder()
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                btnFilePlay.setEnabled(false)
                btnFileStop.setEnabled(false)

                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
        }

        btnStopRecord.setOnClickListener {
            Log.d(TAG, "Record Stop")

            mediaRecorder.stop()
                btnStopRecord.setEnabled(false)
                btnFilePlay.setEnabled(true)
                btnStartRecord.setEnabled(true)
                btnFileStop.setEnabled(false)
        }

        btnFilePlay.setOnClickListener {
            Log.d(TAG, "File Play")

            btnFileStop.setEnabled(true)
            btnStopRecord.setEnabled(false)
            btnStartRecord.setEnabled(false)

            mediaPlayer = MediaPlayer();
            try{
                mediaPlayer.setDataSource(pathSave)
                mediaPlayer.prepare()
            } catch(e: IOException){
                e.printStackTrace()
            }

            mediaPlayer.start()
            Toast.makeText(this, "Playing...", Toast.LENGTH_SHORT).show()
        }

        btnFileStop.setOnClickListener {
            Log.d(TAG, "File Stop")

            btnFileStop.setEnabled(false)
            btnStopRecord.setEnabled(false)
            btnStartRecord.setEnabled(true)
            btnFilePlay.setEnabled(true)

            if(mediaPlayer != null){
                mediaPlayer.stop()
                mediaPlayer.release()
                setupMediaRecorder()
            }
        }
    }

    fun setupMediaRecorder(){
        mediaRecorder = MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.setOutputFile(pathSave)

    }

    fun checkPermissionFromDevice(): Boolean {
        var write_external_storage_result : Int? = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var record_audio_result: Int? = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        ), REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }




    override fun onDataChanged(dataEvents: DataEventBuffer) {
        /**
         * @author Jay
         * when count data from mobile/wear is received,
         * update count data in view model
         *
         * exactly same with mobile/MainActivity
         */
        Log.d("MainActivity::onDataChanged", "$dataEvents")

        dataEvents.forEach { dataEvent ->
            when (dataEvent.type) {
                TYPE_CHANGED -> {
                    when (dataEvent.dataItem.uri.path) {
                        Constant.COUNT_PATH -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                            val count = dataMapItem.dataMap.getInt(Constant.COUNT_KEY)

                            mainViewModel.count.postValue(count)
                        }
                        else -> {
                            TODO()
                        }
                    }
                }
                TYPE_DELETED -> {
                    TODO()
                }
                else -> {
                    TODO()
                }
            }
        }
    }

    private fun setEventListeners() {
//        binding.decreaseButton.setOnClickListener {
//            mainViewModel.minusCount()
//        }
    }
}
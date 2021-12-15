package com.jwpyo.soundmind.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import net.sourceforge.lame.lowlevel.LameEncoder
import net.sourceforge.lame.mp3.Lame
import net.sourceforge.lame.mp3.MPEGMode
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.sound.sampled.AudioFormat

suspend fun getSTT(byteArray: ByteArray): String = coroutineScope {
    withContext(Dispatchers.IO) {
        lateinit var response: StringBuffer
        var text = ""
        val clientId = "4qxmevcefs" // Application Client ID";
        val clientSecret = "ql01xMsx8ZSkk34LQNAI8b1OKsB105NU9wYDckyp" // Application Client Secret";

        val language = "Kor" // 언어 코드 ( Kor, Jpn, Eng, Chn )
        val apiURL =
            "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=$language"
        val url = URL(apiURL)

        try {
            val conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/octet-stream")
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId)
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret)

            Log.d("STT", "Encoding ByteArray to Mp3 File")
            val voiceFile = encodePcmToMp3(byteArray)
            Log.d("STT", "Thread Started on the size " + voiceFile.size)

            val outputStream = conn.outputStream
            val inputStream = ByteArrayInputStream(voiceFile)
            val buffer = ByteArray(4096)
            var bytesRead = -1
            while (inputStream.read(buffer, 0, buffer.size).also { bytesRead = it } > 0) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputStream.close()
            Log.d("STT", "STT Finished transmitting data to server")

            var br: BufferedReader? = null
            val responseCode = conn.responseCode
            br = if (responseCode == 200) { // 정상 호출
                BufferedReader(InputStreamReader(conn.inputStream))
            } else {  // 오류 발생
                println("error!!!!!!! responseCode= $responseCode")
                BufferedReader(InputStreamReader(conn.inputStream))
            }
            var inputLine: String?
            if (br != null) {
                response = StringBuffer()
                while (br.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                Log.d("STT", "Response (before parsing): $response")
                text = response.toString().split(":")[1].replace("\"", "").replace("}", "")
                Log.d("STT", "Response: $text")
                br.close()
            } else {
                println("error !!!")
            }
        } catch (e: Exception) {
            Log.e("STT", "Exception?" + e.toString())
        }

        text
    }
}


fun encodePcmToMp3(pcm: ByteArray): ByteArray {
    val encoder = LameEncoder(
        AudioFormat(8000.0f, 16, 1, true, false),
        160,
        MPEGMode.MONO,
        Lame.QUALITY_HIGHEST,
        false
    )
    val mp3 = ByteArrayOutputStream()
    val buffer = ByteArray(encoder.getPCMBufferSize())
    var bytesToTransfer = Math.min(buffer.size, pcm.size)
    var bytesWritten: Int
    var currentPcmPosition = 0
    while (0 < encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer).also {
            bytesWritten = it
        }) {
        currentPcmPosition += bytesToTransfer
        bytesToTransfer = Math.min(buffer.size, pcm.size - currentPcmPosition)
        Log.e("logmessage", "current position: $currentPcmPosition")
        mp3.write(buffer, 0, bytesWritten)
    }
    encoder.close()

    return mp3.toByteArray()
}

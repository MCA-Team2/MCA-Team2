package com.jwpyo.soundmind.utils

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


fun getSTT(byteArray: ByteArray) : String {
    lateinit var response : StringBuffer
    val clientId = "4qxmevcefs" // Application Client ID";
    val clientSecret = "ql01xMsx8ZSkk34LQNAI8b1OKsB105NU9wYDckyp" // Application Client Secret";

    val language = "Kor" // 언어 코드 ( Kor, Jpn, Eng, Chn )
    val apiURL =
        "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=$language"
    val url = URL(apiURL)
    val conn = url.openConnection() as HttpURLConnection
    conn.useCaches = false
    conn.doOutput = true
    conn.doInput = true
    conn.setRequestProperty("Content-Type", "application/octet-stream")
    conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId)
    conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret)

    Log.d("STT", "Let's start" )

    val thread = Thread {
        try {
            val outputStream = conn.outputStream
            Log.d("STT", "Insert voiceFile" )
            val inputStream = ByteArrayInputStream(byteArray)
            Log.d("STT", "voiceFile insert")
            val buffer = ByteArray(4096)
            var bytesRead = -1
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputStream.close()

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
                br.close()
                Log.d("STT", "Text: " + response.toString())
            } else {
                println("error !!!")
            }
        } catch (e: Exception) {
            Log.e("STT", "Exception?" + e.toString() )
        }
    }

    Log.d("STT", "Thread start" )
    thread.start()
    Log.d("STT", "Thread ended" )
    return ""
}


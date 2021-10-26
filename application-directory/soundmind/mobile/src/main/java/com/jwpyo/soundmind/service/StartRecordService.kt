package com.jwpyo.soundmind.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.jwpyo.soundmind.utils.Constant.START_RECORD_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartRecordService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        CoroutineScope(Dispatchers.IO).launch {
            val nodeListTask = Wearable.getNodeClient(applicationContext).connectedNodes
            val nodes = Tasks.await(nodeListTask)

            nodes.forEach { sendStartRecordMessage(it.id) }
        }

        return null
    }

    private fun sendStartRecordMessage(node: String) {
        val sendMessageTask = Wearable.getMessageClient(this).sendMessage(
            node, START_RECORD_PATH, ByteArray(0)
        )

        runCatching {
            Tasks.await(sendMessageTask)
        }.onFailure {
            Log.e("hello", "$it")
        }
    }
}
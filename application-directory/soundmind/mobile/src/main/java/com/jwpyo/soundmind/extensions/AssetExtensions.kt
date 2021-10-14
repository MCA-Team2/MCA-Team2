package com.jwpyo.soundmind.extensions

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

suspend fun DataClient.getByteArrayFromAsset(asset: Asset): ByteArray = coroutineScope {
    withContext(Dispatchers.IO) {
        // TODO> Inappropriate blocking method call
        val task = getFdForAsset(asset)
        Tasks.await(task).inputStream.readBytes()
    }
}
package com.example.communicate

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import timber.log.Timber

private const val TAG = "DataLayerSample"
private const val START_ACTIVITY_PATH = "/start-activity"
private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.d("hello onDataChanged: $dataEvents")

        // Loop through the events and send a message
        // to the node that created the data item.
        dataEvents.map { it.dataItem.uri }
            .forEach { uri ->
                // Get the node id from the host value of the URI
                val nodeId: String = uri.host!!
                // Set the data of the message to be the bytes of the URI
                val payload: ByteArray = uri.toString().toByteArray()

                // Send the RPC
                Wearable.getMessageClient(this)
                    .sendMessage(nodeId, DATA_ITEM_RECEIVED_PATH, payload)
            }
    }
}
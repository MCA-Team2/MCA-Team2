package com.jwpyo.soundmind.view.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentRecordBinding
import com.jwpyo.soundmind.service.MainService
import com.jwpyo.soundmind.utils.Constant
import com.jwpyo.soundmind.view.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RecordFragment : DatabindingFragment() {
    private lateinit var binding: FragmentRecordBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding<FragmentRecordBinding>(
            inflater,
            R.layout.fragment_record,
            container
        ).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@RecordFragment.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
    }

    private fun setEventListeners() {
        binding.appStartButton.setOnClickListener {
            sendMessageToNodes { messageClient, node ->
                messageClient.sendMessage(node, Constant.START_ACTIVITY_PATH, ByteArray(0))
            }
        }

        binding.recordStartButton.setOnClickListener {
            context?.startForegroundService(MainService.getIntentForStart(context))
            sendMessageToNodes { messageClient, node ->
                messageClient.sendMessage(node, Constant.START_RECORD_PATH, ByteArray(0))
            }
        }

        binding.recordStopButton.setOnClickListener {
            context?.stopService(MainService.getIntentForStart(context))
            sendMessageToNodes { messageClient, node ->
                messageClient.sendMessage(node, Constant.STOP_RECORD_PATH, ByteArray(0))
            }
        }
    }

    private fun sendMessageToNodes(lambda: (MessageClient, String) -> Task<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            val ctx = context
            val activity = activity

            if (ctx != null && activity != null) {
                val nodeListTask = Wearable.getNodeClient(ctx).connectedNodes
                val nodes = Tasks.await(nodeListTask)
                nodes.forEach { node ->
                    val sendMessageTask = lambda(
                        Wearable.getMessageClient(activity),
                        node.id
                    )
                    runCatching {
                        Tasks.await(sendMessageTask)
                    }.onFailure {
                        Log.e("hello", "$it")
                    }
                }
            }
        }
    }
}
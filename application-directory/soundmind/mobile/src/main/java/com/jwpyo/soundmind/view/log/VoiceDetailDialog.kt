package com.jwpyo.soundmind.view.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogVoiceDetailBinding
import com.jwpyo.soundmind.extensions.applyWindowSize
import com.jwpyo.soundmind.extensions.applyWindowTransparent
import com.jwpyo.soundmind.view.adapter.VoiceAdapter
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VoiceDetailDialog : DatabindingDialog() {
    private lateinit var binding: DialogVoiceDetailBinding
    val viewModel: LogViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding<DialogVoiceDetailBinding>(
            inflater,
            R.layout.dialog_voice_detail,
            container
        ).apply {
            adapter = VoiceAdapter(viewModel, viewLifecycleOwner)
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@VoiceDetailDialog.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowTransparent()
        applyWindowSize(0.9f, 0.5f)
    }
}
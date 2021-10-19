package com.jwpyo.soundmind.view.voice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentVoiceBinding
import com.jwpyo.soundmind.view.adapter.VoiceAdapter
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VoiceFragment : DatabindingFragment() {
    private lateinit var binding: FragmentVoiceBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding<FragmentVoiceBinding>(
            inflater,
            R.layout.fragment_voice,
            container
        ).apply {
            adapter = VoiceAdapter(viewModel, get())
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@VoiceFragment.binding = this
        }.root
    }
}
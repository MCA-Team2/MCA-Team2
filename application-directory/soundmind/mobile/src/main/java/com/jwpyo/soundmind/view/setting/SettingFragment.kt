package com.jwpyo.soundmind.view.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentSettingBinding
import com.jwpyo.soundmind.view.log.LogActivity
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingFragment : DatabindingFragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding<FragmentSettingBinding>(
            inflater,
            R.layout.fragment_setting,
            container
        ).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@SettingFragment.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
    }

    private fun setEventListeners() {
        binding.openLogButton.setOnClickListener {
            startActivity(Intent(context, LogActivity::class.java))
        }
    }
}
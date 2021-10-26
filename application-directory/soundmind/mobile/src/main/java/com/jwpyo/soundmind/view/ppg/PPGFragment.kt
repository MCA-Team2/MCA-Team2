package com.jwpyo.soundmind.view.ppg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentPpgBinding
import com.jwpyo.soundmind.view.adapter.PPGAdapter
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PPGFragment : DatabindingFragment() {
    private lateinit var binding: FragmentPpgBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding<FragmentPpgBinding>(
            inflater,
            R.layout.fragment_ppg,
            container
        ).apply {
            adapter = PPGAdapter(viewModel)
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@PPGFragment.binding = this
        }.root
    }
}
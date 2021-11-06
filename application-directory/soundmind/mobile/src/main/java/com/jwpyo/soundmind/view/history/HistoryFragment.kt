package com.jwpyo.soundmind.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentHistoryBinding
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryFragment : DatabindingFragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding<FragmentHistoryBinding>(
            inflater,
            R.layout.fragment_history,
            container
        ).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@HistoryFragment.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
        initStressChart()
    }

    private fun setEventListeners() {
        binding.dateDropDown.setOnClickListener {
            DatePickerDialog(viewModel.historyDateLiveData).show(context)
        }

        // TODO> ...
    }

    private fun setObservers() {
        // TODO> ...
    }

    private fun initStressChart() {
        binding.chartStress.setData(StressLineChart.defaultInfo)
    }
}
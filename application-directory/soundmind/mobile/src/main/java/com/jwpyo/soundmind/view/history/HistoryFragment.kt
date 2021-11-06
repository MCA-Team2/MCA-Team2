package com.jwpyo.soundmind.view.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentHistoryBinding
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@SuppressLint("ClickableViewAccessibility")
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
        setObservers()
    }

    private fun setEventListeners() {
        binding.dateDropDown.setOnClickListener {
            DatePickerDialog(viewModel.historyDateLiveData).show(context)
        }

        binding.chartScrollView.setOnTouchListener { _, event ->
            // TODO> magnetic UX
            false
        }
    }

    private var liveData: LiveData<List<Stress>>? = null
    private var observer: Observer<List<Stress>>? = null
    private fun setObservers() {
        viewModel.stress.asLiveData().observe(viewLifecycleOwner) { stressListFlow ->
            if (liveData != null && observer != null)
                liveData!!.removeObserver(observer!!)

            liveData = stressListFlow.asLiveData()
            observer = Observer {
                if (it.isEmpty()) binding.chartStress.setData(StressLineChart.defaultInfo)
                else binding.chartStress.setData(it)
            }
            liveData!!.observe(viewLifecycleOwner, observer!!)
        }
    }
}
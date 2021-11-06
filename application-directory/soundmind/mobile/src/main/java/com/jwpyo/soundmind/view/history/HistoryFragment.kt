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
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@SuppressLint("ClickableViewAccessibility")
class HistoryFragment : DatabindingFragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    private var volumeLiveData: LiveData<List<VolumeItem>>? = null
    private var volumeObserver: Observer<List<VolumeItem>>? = null

    private var stressLiveData: LiveData<List<Stress>>? = null
    private var stressObserver: Observer<List<Stress>>? = null

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

    private fun setObservers() {
        viewModel.hourScope.asLiveData().observe(viewLifecycleOwner) { (min, max) ->
            Log.e("hello", "hello $min, $max")
            binding.volumeChart.setScope(min, max)
            binding.stressChart.setScope(min, max)
        }

        viewModel.volume.asLiveData().observe(viewLifecycleOwner) { volumeListFlow ->
            if (volumeLiveData != null && volumeObserver != null)
                volumeLiveData!!.removeObserver(volumeObserver!!)

            volumeLiveData = volumeListFlow.asLiveData()
            volumeObserver = Observer {
                viewModel.volumeChartInfo.postValue(
                    if (it.isEmpty()) VoiceVolumeChart.defaultInfo else it
                )
            }
            volumeLiveData!!.observe(viewLifecycleOwner, volumeObserver!!)
        }

        viewModel.stress.asLiveData().observe(viewLifecycleOwner) { stressListFlow ->
            if (stressLiveData != null && stressObserver != null)
                stressLiveData!!.removeObserver(stressObserver!!)

            stressLiveData = stressListFlow.asLiveData()
            stressObserver = Observer {
                viewModel.stressChartInfo.postValue(
                    if (it.isEmpty()) StressLineChart.defaultInfo else it
                )
            }
            stressLiveData!!.observe(viewLifecycleOwner, stressObserver!!)
        }

        viewModel.volumeChartInfo.observe(viewLifecycleOwner) { volumeInfo ->
            binding.volumeChart.setData(volumeInfo)
        }

        viewModel.stressChartInfo.observe(viewLifecycleOwner) { stressInfo ->
            binding.stressChart.setData(stressInfo)
        }
    }
}
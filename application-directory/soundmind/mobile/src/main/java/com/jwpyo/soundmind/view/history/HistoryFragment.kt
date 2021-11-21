package com.jwpyo.soundmind.view.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentHistoryBinding
import com.jwpyo.soundmind.extensions.StickyDirection
import com.jwpyo.soundmind.extensions.TouchState
import com.jwpyo.soundmind.extensions.setHorizontalDragListenerOnHolder
import com.jwpyo.soundmind.extensions.show
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.ui.VolumeItem
import com.jwpyo.soundmind.utils.Constant.WIDTH_PER_HOUR
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("ClickableViewAccessibility")
class HistoryFragment : DatabindingFragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModel<HistoryViewModel>()

    private var volumeLiveData: LiveData<List<VolumeItem>>? = null
    private var volumeObserver: Observer<List<VolumeItem>>? = null

    private var stressLiveData: LiveData<List<Stress>>? = null
    private var stressObserver: Observer<List<Stress>>? = null

    private val startHolderTouchState: MutableLiveData<TouchState> = MutableLiveData()
    private val endHolderTouchState: MutableLiveData<TouchState> = MutableLiveData()

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
            when (event.action) {
                ACTION_MOVE -> {
                    if (event.x > binding.chartScrollView.width * 0.9f) {
                        binding.chartScrollView.smoothScrollBy(20, 0)
                    }
                    if (event.x < binding.chartScrollView.width * 0.1f) {
                        binding.chartScrollView.smoothScrollBy(-20, 0)
                    }

                    when {
                        startHolderTouchState.value?.isFocused == true -> {
                            binding.startLineHolder.dispatchTouchEvent(event)
                            true
                        }
                        endHolderTouchState.value?.isFocused == true -> {
                            binding.endLineHolder.dispatchTouchEvent(event)
                            true
                        }
                        else -> false
                    }
                }
                ACTION_UP -> {
                    when {
                        startHolderTouchState.value?.isFocused == true -> {
                            binding.startLineHolder.dispatchTouchEvent(event)
                            startHolderTouchState.value = TouchState(false)
                            true
                        }
                        endHolderTouchState.value?.isFocused == true -> {
                            binding.endLineHolder.dispatchTouchEvent(event)
                            endHolderTouchState.value = TouchState(false)
                            true
                        }
                        else -> false
                    }
                }
                else -> false
            }
        }

        binding.startLineHolder.setHorizontalDragListenerOnHolder(
            binding.chartScrollView,
            binding.startLine,
            StickyDirection.LEFT,
            WIDTH_PER_HOUR / 60f,
            viewModel.audioStartPosition,
            startHolderTouchState
        )

        binding.endLineHolder.setHorizontalDragListenerOnHolder(
            binding.chartScrollView,
            binding.endLine,
            StickyDirection.RIGHT,
            WIDTH_PER_HOUR / 60f,
            viewModel.audioEndPosition,
            endHolderTouchState
        )
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
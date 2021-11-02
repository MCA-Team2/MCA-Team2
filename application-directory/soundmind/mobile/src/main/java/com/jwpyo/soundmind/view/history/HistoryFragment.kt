package com.jwpyo.soundmind.view.history

// Module for MPAndroidChart
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.chibde.visualizer.LineBarVisualizer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingFragment
import com.jwpyo.soundmind.databinding.FragmentHistoryBinding
import com.jwpyo.soundmind.view.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HistoryFragment : DatabindingFragment(), OnChartValueSelectedListener {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    // Variables for MPAndroidChart
    private var chart: LineChart? = null

    // Variables for Audio Visualization
    private var playButton: ImageView? = null
    private var mediaPlayer: MediaPlayer? = null

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
        setChart()
        setAudioVisualizer()
    }

    private fun setAudioVisualizer(){
        playButton = getView()?.findViewById(R.id.playButton)
        playButton!!.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    if(!mediaPlayer?.isPlaying()!!){
                        mediaPlayer?.start()
                        // TODO: 재생버튼 아이콘 설정하기
                        playButton!!.setImageResource(R.drawable.common_full_open_on_phone)
                    } else{
                        mediaPlayer?.pause()
                        // TODO: 정지버튼 아이콘 설정하기
                        playButton!!.setImageResource(R.drawable.common_full_open_on_phone)
                    }
                }
            }
        );
        mediaPlayer = MediaPlayer();
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource("TODO");

        val lineBarVisualizer: LineBarVisualizer? = getView()?.findViewById(R.id.visualizerLine)

        // set custom color to the line.
        // lineBarVisualizer.setColor(ContextCompat.getColor(this, R.color.custom))

        // define custom number of bars you want in the visualizer between (10 - 256).
        lineBarVisualizer?.setDensity(90f)

        // Set your media player to the visualizer.
        lineBarVisualizer?.setPlayer(mediaPlayer!!.audioSessionId)
    }


    private fun setChart() {
        chart = getView()?.findViewById(R.id.chart1)
        chart!!.setOnChartValueSelectedListener(this)

        // enable description text
        chart!!.description.isEnabled = true

        // enable touch gestures
        chart!!.setTouchEnabled(true)

        // enable scaling and dragging
        chart!!.isDragEnabled = true
        chart!!.setDrawGridBackground(false)

        // set an alternative background color
        chart!!.setBackgroundColor(Color.LTGRAY)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        chart!!.data = data

        // TODO : legend 설정을 원할 경우 (val l = chart!!.legend)
        // TODO : 좌표축 설정을 원할 경우 (var xl = chart!!.xAxis / var leftAxis = chart!!.axisLeft)
    }

    private fun addEntry() {
        val data = chart!!.data
        if (data != null) {
            var set = data.getDataSetByIndex(0)
            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }
            // TODO : Data 연결
            data.addEntry(Entry(set.entryCount.toFloat(), (Math.random() * 40).toFloat() + 30f), 0)
            data.notifyDataChanged()

            // let the chart know it's data has changed
            chart!!.notifyDataSetChanged()

            // limit the number of visible entries
            chart!!.setVisibleXRangeMaximum(120f)
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart!!.moveViewToX(data.entryCount.toFloat())

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "Dynamic Data")
        set.axisDependency = AxisDependency.LEFT
        set.color = ColorTemplate.getHoloBlue()
        set.setCircleColor(Color.WHITE)
        set.lineWidth = 2f
        set.circleRadius = 4f
        set.fillAlpha = 65
        set.fillColor = ColorTemplate.getHoloBlue()
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.WHITE
        set.valueTextSize = 9f
        set.setDrawValues(false)
        return set
    }

    private var thread: Thread? = null

    private fun feedMultiple() {
        if (thread != null) thread!!.interrupt()
        val runnable = Runnable { addEntry() }
        thread = Thread {
            for (i in 0..999) {

                // TODO: Thread를 통한 UI update관
//                // Don't generate garbage runnables inside the loop.
//                runOnUiThread(runnable)
//                try {
//                    Thread.sleep(25)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
            }
        }
        thread!!.start()
    }

    override fun onValueSelected(e: Entry, h: Highlight?) {
        Log.i("Entry selected", e.toString())
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }

    override fun onPause() {
        super.onPause()
        if (thread != null) {
            thread!!.interrupt()
        }
    }

    private fun setEventListeners() {
        // TODO> ...
    }
}
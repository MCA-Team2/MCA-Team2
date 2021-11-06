package com.jwpyo.soundmind.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogDatePickerBinding
import com.jwpyo.soundmind.extensions.applyWindowTransparent
import org.threeten.bp.LocalDate
import java.util.*

class DatePickerDialog(
    private val dateLivedata: MutableLiveData<LocalDate>? = null
) : DatabindingDialog() {
    private lateinit var binding: DialogDatePickerBinding

    private var y: Int? = null
    private var m: Int? = null
    private var d: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding<DialogDatePickerBinding>(
            inflater,
            R.layout.dialog_date_picker,
            container
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this@DatePickerDialog.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowTransparent()

        binding.calendarView.setOnDateChangeListener { _, y, m, d ->
            this.y = y
            this.m = m + 1
            this.d = d
        }

        binding.selectButton.setOnClickListener {
            val localDate =
                if (y == null || m == null || d == null) LocalDate.now()
                else LocalDate.of(y!!, m!!, d!!)

            dateLivedata?.postValue(localDate)
            dismissAllowingStateLoss()
        }
    }
}
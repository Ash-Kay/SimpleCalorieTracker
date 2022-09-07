package com.example.simplecalorietracker.ui.addEntry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplecalorietracker.databinding.FragmentAddFoodEntryBinding
import com.example.simplecalorietracker.utils.CalendarRangeValidator
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import java.text.SimpleDateFormat
import java.util.*

class AddFoodEntryFragment : Fragment() {

    private var _binding: FragmentAddFoodEntryBinding? = null
    private val binding get() = _binding!!
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var viewModel: AddFoodEntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AddFoodEntryViewModel::class.java]
        _binding = FragmentAddFoodEntryBinding.inflate(inflater, container, false)

        setCurrentDateTime()
        setupTimePicker()
        setupDatePicker()

        viewModel.dateTime.observe(viewLifecycleOwner) {
            val date = sdf.format(it)
            binding.etDateTime.setText(date)
        }

        datePicker.addOnPositiveButtonClickListener {
            viewModel.updateDateTime(it)
            timePicker.show(parentFragmentManager, "TIME_PICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.timeInMillis = viewModel.dateTime.value ?: MaterialDatePicker.todayInUtcMilliseconds()
            cal.set(Calendar.HOUR, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            viewModel.updateDateTime(cal.timeInMillis)
        }

        binding.etDateTime.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                datePicker.show(parentFragmentManager, "DATE_PICKER")
                v.clearFocus()
            }
        }

        binding.btnSubmit.setOnClickListener {
            //if success
            findNavController().popBackStack()
            //else show error toast
        }

        return binding.root
    }

    private fun setupDatePicker() {
        val (today, constraints) = setupCalenderConstraint()

        datePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setCalendarConstraints(constraints)
            .setSelection(today)
            .setTitleText("Select a date")
            .build()
    }

    private fun setupTimePicker() {
        timePicker = MaterialTimePicker
            .Builder()
            .setTimeFormat(CLOCK_24H)
            .setTitleText("Select a time")
            .build()
    }

    private fun setupCalenderConstraint(): Pair<Long, CalendarConstraints> {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val last3year = MaterialDatePicker.todayInUtcMilliseconds() - 3 * 31_556_952_000

        val constraints = CalendarConstraints.Builder()
            .setOpenAt(today)
            .setStart(last3year)
            .setEnd(today)
            .setValidator(CalendarRangeValidator(last3year, today))
            .build()
        return Pair(today, constraints)
    }

    private fun setCurrentDateTime() {
        val currTimestamp = System.currentTimeMillis()
        val date = sdf.format(currTimestamp)
        viewModel.updateDateTime(currTimestamp)
        binding.etDateTime.setText(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
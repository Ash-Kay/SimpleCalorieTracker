package com.example.simplecalorietracker.ui.addEntry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.simplecalorietracker.databinding.FragmentAddFoodEntryBinding
import com.example.simplecalorietracker.ui.SharedViewModel
import com.example.simplecalorietracker.utils.CalendarRangeValidator
import com.example.simplecalorietracker.utils.Constants
import com.example.simplecalorietracker.utils.NetworkHandler
import com.example.simplecalorietracker.utils.toHumanDateTime
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toLongOrDefault
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

@AndroidEntryPoint
class AddFoodEntryFragment : Fragment() {

    @Inject
    lateinit var networkHandler: NetworkHandler

    private var _binding: FragmentAddFoodEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var datePicker: MaterialDatePicker<Long>

    private val viewModel: AddFoodEntryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var itemId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFoodEntryBinding.inflate(inflater, container, false)

        setupTimePicker()
        setupDatePicker()

        viewModel.dateTime.observe(viewLifecycleOwner) {
            binding.etDateTime.setText(it.toHumanDateTime())
        }

        datePicker.addOnPositiveButtonClickListener {
            viewModel.updateDateTime(it)
            timePicker.show(parentFragmentManager, "TIME_PICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.timeZone = TimeZone.getTimeZone("GMT")
            cal.timeInMillis =
                viewModel.dateTime.value ?: MaterialDatePicker.todayInUtcMilliseconds()
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

        binding.etFoodName.addTextChangedListener {
            if (binding.inputFoodName.isErrorEnabled) {
                binding.inputFoodName.isErrorEnabled = false
            }
        }

        binding.etFoodCalorie.addTextChangedListener {
            if (binding.inputFoodCalorie.isErrorEnabled) {
                binding.inputFoodCalorie.isErrorEnabled = false
            }
        }

        binding.btnSubmit.setOnClickListener {
            with(binding.etFoodName.text.toString()) {
                if (isBlank()) {
                    binding.inputFoodName.error = "Food Name can't be blank"
                    return@setOnClickListener
                }
                if (length > 30) {
                    binding.inputFoodName.error = "Input too long, max length 30"
                    return@setOnClickListener
                }
            }

            with(binding.etFoodCalorie.text.toString()) {
                if (isBlank()) {
                    binding.inputFoodCalorie.error = "Calories can't be blank"
                    return@setOnClickListener
                }
                if (toLongOrNull() == null) {
                    binding.inputFoodCalorie.error = "Not valid calorie input"
                    return@setOnClickListener
                }
                if (toLongOrDefault(0).absoluteValue == 0L) {
                    binding.inputFoodCalorie.error = "Calorie can't be 0"
                    return@setOnClickListener
                }
                if (toLongOrDefault(0).absoluteValue >= Int.MAX_VALUE) {
                    binding.inputFoodCalorie.error = "Input too long, max length 9 digit"
                    return@setOnClickListener
                }
            }

            with(binding.etDateTime.text.toString()) {
                if (isBlank()) {
                    binding.inputDateTime.error = "Date Time can't be blank"
                    return@setOnClickListener
                }
            }

            if (networkHandler.isNetworkAvailable().not()) {
                Toast.makeText(context, "No Internet!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = itemId
            if(id == null) {
                viewModel.submit(
                    binding.etFoodName.text.toString(),
                    binding.etFoodCalorie.text.toString().toLongOrDefault(0).absoluteValue,
                    //TODO: check how to handle
                    viewModel.dateTime.value ?: 0,
                    {
                        findNavController().popBackStack()
                    }, {
                        Toast.makeText(context, "Error Adding Food Entry!", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
            else {
                viewModel.update(
                    id,
                    binding.etFoodName.text.toString(),
                    binding.etFoodCalorie.text.toString().toLongOrDefault(0).absoluteValue,
                    //TODO: check how to handle
                    viewModel.dateTime.value ?: 0,
                    {
                        findNavController().popBackStack()
                    }, {
                        Toast.makeText(context, "Error Updating Food Entry!", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }

        sharedViewModel.itemToUpdate.observe(viewLifecycleOwner) {
            viewModel.setEditState(it)
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        return binding.root
    }

    private fun renderViewState(state: AddFoodEntryViewState) {
        when (state) {
            is AddFoodEntryViewState.Edit -> {
                binding.etFoodName.setText(state.foodEntry.name)
                binding.etFoodCalorie.setText(state.foodEntry.calorie.toString())
                binding.etDateTime.setText(state.foodEntry.timestamp.toHumanDateTime())
                viewModel.updateDateTime(state.foodEntry.timestamp)
                itemId = state.foodEntry.id
            }
            AddFoodEntryViewState.Normal -> {
                setCurrentDateTime()
                itemId = null
            }
        }
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
        val last3year = MaterialDatePicker.todayInUtcMilliseconds() - Constants.threeYearsInMillis

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
        viewModel.updateDateTime(currTimestamp)
        binding.etDateTime.setText(currTimestamp.toHumanDateTime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
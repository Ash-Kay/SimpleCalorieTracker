package com.example.simplecalorietracker.ui.base

import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.utils.setupCalenderConstraint
import com.google.android.material.datepicker.MaterialDatePicker

abstract class BaseHomeFragment<STATE> : Fragment() {

    val dateRangePicker: MaterialDatePicker<Pair<Long, Long>> by lazy {
        MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setCalendarConstraints(setupCalenderConstraint())
            .setTitleText("Select a date range")
            .build()
    }

    abstract val viewModel: ViewModel
    abstract val sharedViewModel: ViewModel

    abstract fun renderViewState(state: STATE)

    abstract fun itemUpdateClicked(foodEntryEntity: FoodEntryEntity)

    abstract fun itemDeleteClicked(foodEntryEntity: FoodEntryEntity)

    abstract fun clearFilterAndFetch()

    abstract fun checkInternetAndGetFoodEntries(start: Long = 0, end: Long = 0)
}
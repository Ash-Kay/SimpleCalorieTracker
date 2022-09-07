package com.example.simplecalorietracker.ui.addEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddFoodEntryViewModel : ViewModel() {

    private val _dateTime = MutableLiveData<Long>()
    val dateTime: LiveData<Long> = _dateTime

    fun updateDateTime(dateTime: Long) {
        _dateTime.postValue(dateTime)
    }
}
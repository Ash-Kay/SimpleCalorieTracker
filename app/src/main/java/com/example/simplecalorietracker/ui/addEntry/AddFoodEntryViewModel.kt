package com.example.simplecalorietracker.ui.addEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddFoodEntryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is UserHome Fragment"
    }
    val text: LiveData<String> = _text
}
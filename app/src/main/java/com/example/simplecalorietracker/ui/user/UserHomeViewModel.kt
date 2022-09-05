package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserHomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is UserHome Fragment"
    }
    val text: LiveData<String> = _text
}
package com.example.simplecalorietracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity

class SharedViewModel : ViewModel() {
    private val _itemToUpdate = MutableLiveData<FoodEntryEntity?>()
    val itemToUpdate: LiveData<FoodEntryEntity?> = _itemToUpdate

    fun updateItem(foodEntity: FoodEntryEntity?) {
        _itemToUpdate.postValue(foodEntity)
    }
}

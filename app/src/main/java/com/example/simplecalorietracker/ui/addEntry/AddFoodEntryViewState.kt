package com.example.simplecalorietracker.ui.addEntry

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.remote.CreateFoodEntryRequest

sealed class AddFoodEntryViewState {
    object Normal : AddFoodEntryViewState()
    data class Edit(val foodEntry: FoodEntryEntity) : AddFoodEntryViewState()
}
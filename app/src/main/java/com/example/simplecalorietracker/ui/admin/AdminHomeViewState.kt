package com.example.simplecalorietracker.ui.admin

import com.example.simplecalorietracker.data.entity.FoodEntryEntity

sealed class AdminHomeViewState {
    object Idle: AdminHomeViewState()
    object Loading : AdminHomeViewState()
    data class Error(val message: String) : AdminHomeViewState()
    data class DataFetchSuccess(val foodEntries: List<FoodEntryEntity>) : AdminHomeViewState()
    data class CacheDataFetchSuccess(val foodEntries: List<FoodEntryEntity>) : AdminHomeViewState()
}
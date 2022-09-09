package com.example.simplecalorietracker.ui.user

import com.example.simplecalorietracker.data.entity.FoodEntryEntity

sealed class UserHomeViewState {
    object Idle: UserHomeViewState()
    object Loading : UserHomeViewState()
    data class Error(val message: String) : UserHomeViewState()
    data class DataFetchSuccess(val foodEntries: List<FoodEntryEntity>) : UserHomeViewState()
    data class CacheDataFetchSuccess(val foodEntries: List<FoodEntryEntity>) : UserHomeViewState()
}
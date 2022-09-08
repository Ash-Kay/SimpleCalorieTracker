package com.example.simplecalorietracker.data.remote

data class CreateFoodEntryRequest(val name: String, val calorie: Long, val timestamp: Long)
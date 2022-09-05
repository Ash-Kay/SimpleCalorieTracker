package com.example.simplecalorietracker.model.entity

import com.google.gson.annotations.SerializedName

data class FoodEntry(
    @SerializedName("id")
    val foodEntryId: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("foodName")
    val foodName: String,

    @SerializedName("calorieCount")
    val calorieCount: Int,
)
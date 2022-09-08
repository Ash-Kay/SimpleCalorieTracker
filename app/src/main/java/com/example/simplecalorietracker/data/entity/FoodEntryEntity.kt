package com.example.simplecalorietracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "foodEntry")
data class FoodEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "calorie")
    @SerializedName("calorie")
    val calorie: Long,
)
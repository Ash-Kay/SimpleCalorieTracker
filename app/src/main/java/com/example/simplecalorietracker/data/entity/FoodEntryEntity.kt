package com.example.simplecalorietracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foodEntry")
data class FoodEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "foodName")
    val foodName: String,

    @ColumnInfo(name = "calorieCount")
    val calorieCount: Long,
)

//@Entity(tableName = "foodEntry")
//data class FoodEntryEntity(
//    @PrimaryKey(autoGenerate = true)
//    @SerializedName("id")
//    val id: Int,
//
//    @SerializedName("date")
//    val date: Long,
//
//    @SerializedName("foodName")
//    val foodName: String,
//
//    @SerializedName("calorieCount")
//    val calorieCount: Long,
//)
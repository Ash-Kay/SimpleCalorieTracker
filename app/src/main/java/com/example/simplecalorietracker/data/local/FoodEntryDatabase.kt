package com.example.simplecalorietracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplecalorietracker.data.entity.FoodEntryEntity

@Database(
    entities = [FoodEntryEntity::class],
    version = 1
)
abstract class FoodEntryDatabase : RoomDatabase() {

    abstract val foodEntryDao: FoodEntryDao

    companion object {
        const val DATABASE_NAME = "food_entries_db"
    }
}
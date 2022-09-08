package com.example.simplecalorietracker.data.local;

import androidx.room.*
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM foodEntry")
    fun getFoodEntries(): Flowable<List<FoodEntryEntity>>

    @Query("SELECT * FROM foodEntry WHERE id = :id")
    fun getFoodEntryById(id: Int): Single<FoodEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFoodEntry(foodEntry: FoodEntryEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFoodEntries(foodEntries: List<FoodEntryEntity>): Completable

    @Delete
    fun deleteFoodEntry(foodEntry: FoodEntryEntity): Completable

    @Query("DELETE FROM foodEntry")
    fun clearFoodEntries(): Completable
}

package com.example.simplecalorietracker.domain.repository

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.remote.GetReportResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface FoodEntryRepository {
    fun getFoodEntriesLocal(): Flowable<List<FoodEntryEntity>>

    fun getFoodEntriesRemote(start: Long, end: Long): Single<List<FoodEntryEntity>>

    fun getFoodEntryById(id: Int): Single<FoodEntryEntity>

    fun insertFoodEntryLocal(foodEntry: FoodEntryEntity): Completable

    fun createFoodEntryRemote(foodName: String, foodCalorie: Long, timestamp: Long) : Single<FoodEntryEntity>

    fun insertFoodEntries(foodEntries: List<FoodEntryEntity>): Completable

    fun updateFoodEntryRemote(id: Int, foodName: String, foodCalorie: Long, timestamp: Long) : Completable

    fun deleteFoodEntryRemote(foodEntry: FoodEntryEntity) : Completable

    fun deleteFoodEntry(foodEntry: FoodEntryEntity): Completable

    fun clearFoodEntries(): Completable

    fun getReport() : Single<GetReportResponse>
}
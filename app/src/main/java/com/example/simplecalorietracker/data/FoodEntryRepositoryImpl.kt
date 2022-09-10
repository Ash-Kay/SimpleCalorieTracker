package com.example.simplecalorietracker.data

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.remote.CreateFoodEntryRequest
import com.example.simplecalorietracker.data.remote.GetReportResponse
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val retrofitService: RetrofitService
) : FoodEntryRepository {

    override fun getFoodEntriesLocal(): Flowable<List<FoodEntryEntity>> {
        return foodEntryDao.getFoodEntries()
    }

    override fun getFoodEntriesRemote(start: Long, end: Long): Single<List<FoodEntryEntity>> {
        return retrofitService.getFoodEntries(1, start, end)
    }

    override fun getFoodEntryById(id: Int): Single<FoodEntryEntity> {
        return foodEntryDao.getFoodEntryById(id)
    }

    override fun insertFoodEntryLocal(foodEntry: FoodEntryEntity): Completable {
        return foodEntryDao.insertFoodEntry(foodEntry)
    }

    override fun createFoodEntryRemote(
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Single<FoodEntryEntity> {
        return retrofitService.createFoodEntry(
            CreateFoodEntryRequest(
                foodName,
                foodCalorie,
                timestamp
            )
        )
    }

    override fun insertFoodEntries(foodEntries: List<FoodEntryEntity>): Completable {
        return foodEntryDao.insertFoodEntries(foodEntries)
    }

    override fun updateFoodEntryRemote(
        id: Int,
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Completable {
        return retrofitService.updateFoodEntry(
            id,
            CreateFoodEntryRequest(
                foodName,
                foodCalorie,
                timestamp
            )
        )
    }

    override fun deleteFoodEntryRemote(foodEntry: FoodEntryEntity): Completable {
        return retrofitService.deleteFoodEntry(foodEntry.id)
    }

    override fun deleteFoodEntry(foodEntry: FoodEntryEntity): Completable {
        return foodEntryDao.deleteFoodEntry(foodEntry)
    }

    override fun clearFoodEntries(): Completable {
        return foodEntryDao.clearFoodEntries()
    }

    override fun getReport(): Single<GetReportResponse> {
        return retrofitService.getReport()
    }
}
package com.example.simplecalorietracker.data

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.remote.CreateFoodEntryRequest
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import com.example.simplecalorietracker.utils.NetworkHandler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val retrofitService: RetrofitService,
    private val networkHandler: NetworkHandler
) : FoodEntryRepository {
    override fun getFoodEntries(): Flowable<List<FoodEntryEntity>> {
        return if (networkHandler.isNetworkAvailable()) {
            //TODO: FIX
            retrofitService.getFoodEntries("", 1)
        } else {
            return foodEntryDao.getFoodEntries()
        }
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
            //TODO: FIX
            "",
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

    override fun deleteFoodEntry(foodEntry: FoodEntryEntity): Completable {
        return foodEntryDao.deleteFoodEntry(foodEntry)
    }

    override fun clearFoodEntries(): Completable {
        return foodEntryDao.clearFoodEntries()
    }
}
package com.example.simplecalorietracker.data

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.remote.CreateFoodEntryRequest
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import com.example.simplecalorietracker.utils.AuthUtils
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

    override fun getFoodEntriesLocal(): Flowable<List<FoodEntryEntity>> {
        return foodEntryDao.getFoodEntries()
    }

    override fun getFoodEntriesRemote(): Single<List<FoodEntryEntity>> {
        //TODO: Check
        return retrofitService.getFoodEntries(AuthUtils.AUTH_TOKEN ?: "", 1)
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
            AuthUtils.AUTH_TOKEN ?: "",
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
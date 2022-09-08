package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetFoodEntriesUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(): Flowable<List<FoodEntryEntity>> {
        return repository.getFoodEntries()
    }
}

class GetFoodEntryUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(id: Int): Single<FoodEntryEntity> {
        return repository.getFoodEntryById(id)
    }
}

class UpdateLocalFoodEntriesUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(foodEntriesList: List<FoodEntryEntity>): Completable {
        return repository.insertFoodEntries(foodEntriesList)
    }
}

class CreateFoodEntryUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Single<FoodEntryEntity> {
        return repository.createFoodEntryRemote(
            foodName = foodName,
            foodCalorie = foodCalorie,
            timestamp = timestamp
        )
    }
}

class AddFoodEntryToLocalUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(foodEntryEntity: FoodEntryEntity): Completable {
        return repository.insertFoodEntryLocal(foodEntryEntity)
    }
}
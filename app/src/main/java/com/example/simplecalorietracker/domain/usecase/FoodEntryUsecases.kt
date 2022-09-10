package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetFoodEntriesRemoteUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(start: Long, end: Long): Single<List<FoodEntryEntity>> {
        return repository.getFoodEntriesRemote(start, end)
    }
}

class GetFoodEntriesLocalUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(): Flowable<List<FoodEntryEntity>> {
        return repository.getFoodEntriesLocal()
    }
}

class GetFoodEntryUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(id: Int): Single<FoodEntryEntity> {
        return repository.getFoodEntryById(id)
    }
}

class UpdateLocalFoodEntriesUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(foodEntriesList: List<FoodEntryEntity>): Completable {
        return repository.clearFoodEntries().andThen(repository.insertFoodEntries(foodEntriesList))
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

class UpdateFoodEntryUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(
        id: Int,
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Completable {
        return repository.updateFoodEntryRemote(
            id = id,
            foodName = foodName,
            foodCalorie = foodCalorie,
            timestamp = timestamp
        )
    }
}

class DeleteFoodEntryUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(
        foodEntry: FoodEntryEntity
    ): Completable {
        return repository.deleteFoodEntryRemote(foodEntry)
            .andThen(repository.deleteFoodEntry(foodEntry))
    }
}

class AddFoodEntryToLocalUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(foodEntryEntity: FoodEntryEntity): Completable {
        return repository.insertFoodEntryLocal(foodEntryEntity)
    }
}
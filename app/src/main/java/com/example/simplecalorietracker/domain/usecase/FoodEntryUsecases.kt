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

class AddFoodEntryUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(foodEntry: FoodEntryEntity): Completable {
        return repository.insertFoodEntry(foodEntry)
    }
}
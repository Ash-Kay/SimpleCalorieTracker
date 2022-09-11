package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.remote.GetReportResponse
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
        return repository.getFoodEntriesLocal().map { it.sortedBy { it.timestamp } }
    }
}

class UpdateLocalFoodEntriesUsecase @Inject constructor(private val repository: FoodEntryRepository) {
    operator fun invoke(foodEntriesList: List<FoodEntryEntity>): Completable {
        return repository.clearFoodEntries().andThen(repository.insertFoodEntries(foodEntriesList))
    }
}

class GetFoodEntriesRemoteAndUpdateCacheUsecase @Inject constructor(
    private val getFoodEntriesRemote: GetFoodEntriesRemoteUsecase,
    private val updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase
) {
    operator fun invoke(start: Long, end: Long): Single<List<FoodEntryEntity>> {
        return getFoodEntriesRemote(start, end)
            .flatMap {
                //Only cache if no filter applied
                if (start == 0L && end == 0L) {
                    updateLocalFoodEntriesUsecase(it).andThen(Single.just(it))
                } else {
                    Single.just(it)
                }
            }.map { list -> list.sortedBy { it.timestamp } }
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

class GetReportUsecase @Inject constructor(
    private val repository: FoodEntryRepository
) {
    operator fun invoke(): Single<GetReportResponse> {
        return repository.getReport()
    }
}
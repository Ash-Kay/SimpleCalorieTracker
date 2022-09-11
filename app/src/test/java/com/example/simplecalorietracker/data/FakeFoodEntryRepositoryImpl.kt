package com.example.simplecalorietracker.data

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.data.remote.GetReportResponse
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class FakeFoodEntryRepositoryImpl : FoodEntryRepository {

    private val foodEntriesRemote = mutableListOf<FoodEntryEntity>()
    private val foodEntriesLocal = mutableListOf<FoodEntryEntity>()
    private val repostResponse = GetReportResponse(0, 1, 1.1F)
    private var id = 0

    init {
        val entryToInsert = mutableListOf<FoodEntryEntity>()
        ('a'..'z').forEachIndexed { index, c ->
            entryToInsert.add(
                FoodEntryEntity(
                    id = index,
                    name = c.toString(),
                    calorie = index.toLong(),
                    timestamp = index.toLong(),
                )
            )
        }
        entryToInsert.shuffle()
        foodEntriesRemote.addAll(entryToInsert)
    }

    override fun getFoodEntriesLocal(): Flowable<List<FoodEntryEntity>> {
        return Flowable.just(foodEntriesLocal)
    }

    override fun getFoodEntriesRemote(start: Long, end: Long): Single<List<FoodEntryEntity>> {
        return Single.just(foodEntriesRemote)
    }

    override fun insertFoodEntryLocal(foodEntry: FoodEntryEntity): Completable {
        foodEntriesLocal.add(foodEntry)
        return Completable.complete()
    }

    override fun createFoodEntryRemote(
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Single<FoodEntryEntity> {
        val entry = FoodEntryEntity(
            id = ++id,
            timestamp = timestamp,
            name = foodName,
            calorie = foodCalorie
        )
        foodEntriesRemote.add(entry)
        return Single.just(entry)
    }

    override fun insertFoodEntries(foodEntries: List<FoodEntryEntity>): Completable {
        foodEntriesLocal.addAll(foodEntries)
        return Completable.complete()
    }

    override fun updateFoodEntryRemote(
        id: Int,
        foodName: String,
        foodCalorie: Long,
        timestamp: Long
    ): Completable {
        val entry = FoodEntryEntity(
            id = id,
            timestamp = timestamp,
            name = foodName,
            calorie = foodCalorie
        )
        val index = foodEntriesRemote.indexOfFirst {
            it.id == id
        }
        foodEntriesRemote[index] = entry
        return Completable.complete()
    }

    override fun deleteFoodEntryRemote(foodEntry: FoodEntryEntity): Completable {
        val index = foodEntriesRemote.indexOfFirst {
            it.id == id
        }
        foodEntriesRemote.removeAt(index)
        return Completable.complete()
    }

    override fun deleteFoodEntry(foodEntry: FoodEntryEntity): Completable {
        val index = foodEntriesLocal.indexOfFirst {
            it.id == id
        }
        foodEntriesLocal.removeAt(index)
        return Completable.complete()
    }

    override fun clearFoodEntries(): Completable {
        foodEntriesLocal.clear()
        return Completable.complete()
    }

    override fun getReport(): Single<GetReportResponse> {
        return Single.just(repostResponse)
    }
}
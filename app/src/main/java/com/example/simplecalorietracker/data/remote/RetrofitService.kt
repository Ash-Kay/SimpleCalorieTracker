package com.example.simplecalorietracker.data.remote

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RetrofitService {
    @GET("/api/v1/foodentries")
    fun getFoodEntries(
        @Header("Authorization") authToken: String,
        @Query("page") pageNo: Int
    ): Flowable<List<FoodEntryEntity>>

    @POST("/api/v1/foodentries")
    fun createFoodEntry(
        @Header("Authorization") authToken: String,
        @Body foodEntry: FoodEntryEntity
    ): Single<FoodEntryEntity>
}
package com.example.simplecalorietracker.data.remote

import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RetrofitService {
    @GET("/api/v1/auth/login")
    fun getAuthToken(): Single<BaseResponse<AuthTokenResponse>>

    @GET("/api/v1/users")
    fun getUserDetails(
        @Header("Authorization") authToken: String
    ): Single<BaseResponse<GetUserDetailsResponse>>

    @GET("/api/v1/foodentries")
    fun getFoodEntries(
        @Header("Authorization") authToken: String,
        @Query("page") pageNo: Int,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): Single<List<FoodEntryEntity>>

    @POST("/api/v1/foodentries")
    fun createFoodEntry(
        @Header("Authorization") authToken: String,
        @Body foodEntryRequest: CreateFoodEntryRequest
    ): Single<FoodEntryEntity>

    @PATCH("/api/v1/foodentries/{id}")
    fun updateFoodEntry(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int,
        @Body foodEntryRequest: CreateFoodEntryRequest
    ): Completable

    @DELETE("/api/v1/foodentries/{id}")
    fun deleteFoodEntry(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int
    ): Completable
}
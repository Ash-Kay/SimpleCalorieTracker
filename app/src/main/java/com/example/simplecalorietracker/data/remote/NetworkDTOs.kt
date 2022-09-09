package com.example.simplecalorietracker.data.remote

import com.google.gson.annotations.SerializedName

data class CreateFoodEntryRequest(val name: String, val calorie: Long, val timestamp: Long)

data class BaseResponse<T>(
    @SerializedName("success")
    val isSuccess: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T,

    @SerializedName("errorCode")
    val errorCode: String?
)

data class AuthTokenResponse(val token: String)

data class GetUserDetailsResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: Role
)

enum class Role(val roleName: String) {
    USER("USER"),
    ADMIN("ADMIN")
}
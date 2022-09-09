package com.example.simplecalorietracker.domain.repository

import com.example.simplecalorietracker.data.remote.AuthTokenResponse
import com.example.simplecalorietracker.data.remote.BaseResponse
import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse
import io.reactivex.rxjava3.core.Single

interface AuthRepository {
    fun getAuthToken(): Single<BaseResponse<AuthTokenResponse>>
    fun getUserDetails(token: String): Single<BaseResponse<GetUserDetailsResponse>>
}
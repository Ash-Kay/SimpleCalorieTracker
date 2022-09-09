package com.example.simplecalorietracker.data

import com.example.simplecalorietracker.data.remote.AuthTokenResponse
import com.example.simplecalorietracker.data.remote.BaseResponse
import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
) : AuthRepository {
    override fun getAuthToken(): Single<BaseResponse<AuthTokenResponse>> {
        return retrofitService.getAuthToken()
    }

    override fun getUserDetails(token: String): Single<BaseResponse<GetUserDetailsResponse>> {
        return retrofitService.getUserDetails(token)
    }
}
package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.remote.AuthTokenResponse
import com.example.simplecalorietracker.data.remote.BaseResponse
import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse
import com.example.simplecalorietracker.domain.repository.AuthRepository
import com.example.simplecalorietracker.utils.AuthUtils
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAuthTokenUsecase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Single<BaseResponse<AuthTokenResponse>> {
        return repository.getAuthToken()
    }
}

class GetAuthTokenAndUserDetailsUsecase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Single<BaseResponse<GetUserDetailsResponse>> {
        return repository.getAuthToken().flatMap {
            AuthUtils.AUTH_TOKEN = it.data.token
            repository.getUserDetails(it.data.token)
        }
    }
}
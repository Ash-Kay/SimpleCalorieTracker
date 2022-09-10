package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.remote.AuthTokenResponse
import com.example.simplecalorietracker.data.remote.BaseResponse
import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse
import com.example.simplecalorietracker.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAuthTokenUsecase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Single<BaseResponse<AuthTokenResponse>> {
        return repository.getAuthToken()
    }
}

class GetUserDetailsUsecase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(token: String): Single<BaseResponse<GetUserDetailsResponse>> {
        return repository.getUserDetails(token)
    }
}

class GetAuthTokenAndUserDetailsUsecase @Inject constructor(
    private val getAuthToken: GetAuthTokenUsecase,
    private val getUserDetails: GetUserDetailsUsecase
) {
    operator fun invoke(): Single<Pair<BaseResponse<AuthTokenResponse>, BaseResponse<GetUserDetailsResponse>>> {
        return getAuthToken().flatMap({ tokenResponse ->
            getUserDetails(tokenResponse.data.token)
        }, { tokenResponse, useDetailsResponse ->
            Pair(tokenResponse, useDetailsResponse)
        })
    }
}
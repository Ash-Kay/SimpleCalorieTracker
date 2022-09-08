package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.remote.AuthTokenResponse
import com.example.simplecalorietracker.data.remote.BaseResponse
import com.example.simplecalorietracker.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAuthTokenUsecase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Single<BaseResponse<AuthTokenResponse>> {
        return repository.getAuthToken()
    }
}
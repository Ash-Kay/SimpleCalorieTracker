package com.example.simplecalorietracker.ui

import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse

sealed class MainViewState {
    object Loading : MainViewState()
    data class Error(val message: String) : MainViewState()
    object AuthCheck : MainViewState()
    data class  AuthCheckSuccess(val userDetails: GetUserDetailsResponse) : MainViewState()
    object ShowUserHome : MainViewState()
    object ShowAdminHome : MainViewState()
}
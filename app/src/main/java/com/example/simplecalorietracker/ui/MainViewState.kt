package com.example.simplecalorietracker.ui

import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse

sealed class MainViewState {
    object Loading : MainViewState()
    data class Error(val message: String, val isCacheCorrupt: Boolean = false) : MainViewState()
    object AuthCheck : MainViewState()
    data class AuthCheckSuccess(
        val userDetails: GetUserDetailsResponse,
        val isServerLogin: Boolean = false
    ) : MainViewState()

    object ShowUserHome : MainViewState()
    object ShowAdminHome : MainViewState()
}
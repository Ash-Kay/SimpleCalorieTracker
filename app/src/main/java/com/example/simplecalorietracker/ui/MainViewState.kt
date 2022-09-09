package com.example.simplecalorietracker.ui

sealed class MainViewState {
    object Loading : MainViewState()
    data class Error(val message: String) : MainViewState()
    object AuthCheck : MainViewState()
    data class  AuthCheckSuccess(val authToken: String) : MainViewState()
    object ShowUserHome : MainViewState()
    object ShowAdminHome : MainViewState()
}
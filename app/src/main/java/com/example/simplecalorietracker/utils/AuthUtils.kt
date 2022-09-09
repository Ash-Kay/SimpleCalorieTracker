package com.example.simplecalorietracker.utils

import android.content.Context
import com.example.simplecalorietracker.data.remote.GetUserDetailsResponse
import com.google.gson.Gson

object AuthUtils {
    var AUTH_TOKEN: String? = null
    private const val AUTH_TOKEN_KEY = "AUTH_TOKEN"
    private const val USER_DETAILS_KEY = "USER_DETAILS"

    fun getAuthToken(context: Context): String? {
        val authToken = AUTH_TOKEN ?: SharedPreferences.getStringData(context, AUTH_TOKEN_KEY)
        if (authToken.isNullOrBlank()) {
            return null
        }
        AUTH_TOKEN = authToken
        return authToken
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val authToken = AUTH_TOKEN ?: SharedPreferences.getStringData(context, AUTH_TOKEN_KEY)
        if (authToken.isNullOrBlank()) {
            return false
        }
        AUTH_TOKEN = authToken
        return true
    }

    fun getUserDetailsFromCache(context: Context): GetUserDetailsResponse? {
        return runCatching {
            val userDetailsJson = SharedPreferences.getStringData(context, USER_DETAILS_KEY)
            Gson().fromJson(userDetailsJson, GetUserDetailsResponse::class.java)
        }.getOrNull()
    }

    fun saveAuthToken(context: Context, authToken: String) {
        SharedPreferences.saveData(context, AUTH_TOKEN_KEY, authToken)
    }

    fun saveUserDetails(context: Context, userDetails: GetUserDetailsResponse) {
        val userDetailsJson = Gson().toJson(userDetails)
        SharedPreferences.saveData(context, USER_DETAILS_KEY, userDetailsJson)
    }

    fun clearAuthDetails(context: Context) {
        AUTH_TOKEN = null
        SharedPreferences.clearData(context, AUTH_TOKEN_KEY)
        SharedPreferences.clearData(context, USER_DETAILS_KEY)
    }
}
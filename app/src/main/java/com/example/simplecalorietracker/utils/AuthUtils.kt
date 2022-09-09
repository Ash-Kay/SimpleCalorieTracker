package com.example.simplecalorietracker.utils

import android.content.Context

object AuthUtils {
    var AUTH_TOKEN: String? = null
    const val AUTH_TOKEN_KEY = "AUTH_TOKEN"

    fun checkAuth(context: Context): String? {
        val authToken = AUTH_TOKEN ?: SharedPreferences.getStringData(context, AUTH_TOKEN_KEY)
        if (authToken.isNullOrBlank()) {
            return null
        }
        AUTH_TOKEN = authToken
        return authToken
    }

    fun clearAuthDetails(context: Context) {
        AUTH_TOKEN = null
        SharedPreferences.clearData(context, AUTH_TOKEN_KEY)
    }
}
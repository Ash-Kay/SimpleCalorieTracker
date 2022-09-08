package com.example.simplecalorietracker.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferences {
    private const val PREF_APP = "com.example.simplecalorietracker.prefs"

    fun getStringData(context: Context, key: String?): String? {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
            .getString(key, null)
    }

    fun saveData(context: Context, key: String?, value: String?) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit()
            .putString(key, value).apply()
    }

    fun getSharedPrefEditor(context: Context, pref: String?): SharedPreferences.Editor? {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit()
    }

    fun saveData(editor: SharedPreferences.Editor) {
        editor.apply()
    }

    fun clearData(context: Context, key: String?) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().remove(key).apply()
    }
}

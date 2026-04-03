package com.example.myapplication.data.session

import android.content.Context


class SessionManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLogin(userId: Long, username: String, fullName: String) {
        sharedPreferences.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_FULL_NAME, fullName)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getCurrentUserId(): Long? {
        if (!isLoggedIn()) return null
        val userId = sharedPreferences.getLong(KEY_USER_ID, -1L)
        return if (userId > 0) userId else null
    }
    fun getCurrentFullName(): String = sharedPreferences.getString(KEY_FULL_NAME, "") ?: ""

    companion object {
        private const val PREF_NAME = "shopping_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}
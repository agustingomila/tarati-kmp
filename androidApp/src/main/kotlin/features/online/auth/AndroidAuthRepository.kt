package com.agustin.tarati.features.online.auth

import android.content.Context
import androidx.core.content.edit

class AndroidAuthRepository(context: Context) : AuthRepository {

    private val prefs = context.getSharedPreferences("tarati_auth", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit { putString(KEY_ACCESS_TOKEN, token) }
    }

    override fun getToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    override fun clearToken() {
        prefs.edit { remove(KEY_ACCESS_TOKEN) }
    }

    override fun saveRefreshToken(refreshToken: String) {
        prefs.edit { putString(KEY_REFRESH_TOKEN, refreshToken) }
    }

    override fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    override fun clearRefreshToken() {
        prefs.edit { remove(KEY_REFRESH_TOKEN) }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}

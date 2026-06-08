package com.agustin.tarati.features.online.auth

import java.util.prefs.Preferences

class DesktopAuthRepository : AuthRepository {

    private val prefs: Preferences = Preferences.userRoot().node("com/agustin/tarati/auth")

    override fun saveToken(token: String) {
        prefs.put(KEY_ACCESS_TOKEN, token)
        prefs.flush()
    }

    override fun getToken(): String? = prefs.get(KEY_ACCESS_TOKEN, null)

    override fun clearToken() {
        prefs.remove(KEY_ACCESS_TOKEN)
        prefs.flush()
    }

    override fun saveRefreshToken(refreshToken: String) {
        prefs.put(KEY_REFRESH_TOKEN, refreshToken)
        prefs.flush()
    }

    override fun getRefreshToken(): String? = prefs.get(KEY_REFRESH_TOKEN, null)

    override fun clearRefreshToken() {
        prefs.remove(KEY_REFRESH_TOKEN)
        prefs.flush()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}

package com.agustin.tarati.web

import com.agustin.tarati.features.online.auth.AuthRepository
import kotlinx.browser.window

/**
 * Implementación web de [AuthRepository] usando [window.localStorage].
 *
 * Los tokens persisten entre sesiones del navegador en el mismo origen.
 * Para mayor seguridad en producción considerar sessionStorage o cookies HttpOnly.
 */
class WasmAuthRepository : AuthRepository {

    override fun saveToken(token: String) =
        window.localStorage.setItem(KEY_ACCESS_TOKEN, token)

    override fun getToken(): String? =
        window.localStorage.getItem(KEY_ACCESS_TOKEN)

    override fun clearToken() =
        window.localStorage.removeItem(KEY_ACCESS_TOKEN)

    override fun saveRefreshToken(refreshToken: String) =
        window.localStorage.setItem(KEY_REFRESH_TOKEN, refreshToken)

    override fun getRefreshToken(): String? =
        window.localStorage.getItem(KEY_REFRESH_TOKEN)

    override fun clearRefreshToken() =
        window.localStorage.removeItem(KEY_REFRESH_TOKEN)

    companion object {
        private const val KEY_ACCESS_TOKEN = "tarati_access_token"
        private const val KEY_REFRESH_TOKEN = "tarati_refresh_token"
    }
}

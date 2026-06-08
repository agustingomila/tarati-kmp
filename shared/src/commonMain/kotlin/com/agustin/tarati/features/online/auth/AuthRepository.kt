package com.agustin.tarati.features.online.auth

/**
 * Repositorio para almacenamiento de tokens de autenticación
 * 
 * ## Versión Simplificada para Fase 7
 * 
 * Esta interfaz define un contrato mínimo para guardar/recuperar tokens.
 * Las implementaciones específicas de plataforma se encargan del
 * almacenamiento seguro real.
 * 
 * ## Implementaciones por plataforma:
 * 
 * ### Android (androidMain):
 * ```kotlin
 * class AndroidAuthRepository(
 *     private val context: Context
 * ) : AuthRepository {
 *     private val prefs = context.getSharedPreferences(
 *         "auth_prefs",
 *         Context.MODE_PRIVATE
 *     )
 *     
 *     override fun saveToken(token: String) {
 *         prefs.edit().putString("access_token", token).apply()
 *     }
 *     
 *     override fun getToken(): String? {
 *         return prefs.getString("access_token", null)
 *     }
 *     
 *     override fun clearToken() {
 *         prefs.edit().remove("access_token").apply()
 *     }
 * }
 * ```
 * 
 * ### Desktop (jvmMain):
 * ```kotlin
 * class DesktopAuthRepository : AuthRepository {
 *     private val prefs = Preferences.userRoot()
 *         .node("com/agustin/tarati/auth")
 *     
 *     override fun saveToken(token: String) {
 *         prefs.put("access_token", token)
 *         prefs.flush()
 *     }
 *     
 *     override fun getToken(): String? {
 *         return prefs.get("access_token", null)
 *     }
 *     
 *     override fun clearToken() {
 *         prefs.remove("access_token")
 *         prefs.flush()
 *     }
 * }
 * ```
 * 
 * ## Seguridad:
 * 
 * Para producción, los tokens deben ser cifrados:
 * - Android: usar EncryptedSharedPreferences
 * - Desktop: usar KeyStore del sistema operativo
 * 
 * Para Fase 7 (desarrollo), almacenamiento simple es suficiente.
 */
interface AuthRepository {

    /**
     * Guardar token de acceso
     * 
     * @param token Token JWT a guardar
     */
    fun saveToken(token: String)

    /**
     * Obtener token guardado
     * 
     * @return Token JWT, o null si no hay ninguno guardado
     */
    fun getToken(): String?

    /**
     * Limpiar token guardado
     * 
     * Elimina el token del almacenamiento persistente.
     */
    fun clearToken()

    /**
     * Guardar refresh token
     * 
     * El refresh token se usa para renovar el access token
     * cuando expira (sin requerir login del usuario).
     * 
     * @param refreshToken Token de renovación
     */
    fun saveRefreshToken(refreshToken: String)

    /**
     * Obtener refresh token guardado
     * 
     * @return Refresh token, o null si no hay ninguno
     */
    fun getRefreshToken(): String?

    /**
     * Limpiar refresh token
     */
    fun clearRefreshToken()

    /**
     * Limpiar todos los tokens
     * 
     * Equivalente a llamar clearToken() y clearRefreshToken().
     */
    fun clearAll() {
        clearToken()
        clearRefreshToken()
    }
}

/**
 * Implementación en memoria del AuthRepository
 * 
 * Solo para desarrollo y testing. Los tokens NO persisten
 * entre reinicios de la aplicación.
 */
class InMemoryAuthRepository : AuthRepository {
    private var token: String? = null
    private var refreshToken: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun clearToken() {
        token = null
    }

    override fun saveRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override fun getRefreshToken(): String? = refreshToken

    override fun clearRefreshToken() {
        refreshToken = null
    }
}
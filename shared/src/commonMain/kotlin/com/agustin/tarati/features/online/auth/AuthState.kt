package com.agustin.tarati.features.online.auth

import kotlinx.serialization.Serializable

/**
 * Estado de autenticación del usuario
 * 
 * Representa si el usuario tiene credenciales válidas para
 * conectarse al servidor online.
 */
@Serializable
sealed class AuthState {
    /**
     * No autenticado - sin token guardado
     */
    @Serializable
    data object Unauthenticated : AuthState()

    /**
     * Autenticando - validando token con servidor
     */
    @Serializable
    data object Authenticating : AuthState()

    /**
     * Autenticado - token válido y activo
     * 
     * @property userInfo Información del usuario autenticado
     * @property tokenExpiry Timestamp de expiración del token (epoch millis)
     */
    @Serializable
    data class Authenticated(
        val userInfo: UserInfo,
        val tokenExpiry: Long
    ) : AuthState()

    /**
     * Error de autenticación
     * 
     * @property message Mensaje de error
     * @property canRetry Si el usuario puede reintentar
     */
    @Serializable
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : AuthState()
}

/**
 * Información del usuario autenticado
 * 
 * @property userId ID único del usuario en el servidor
 * @property username Nombre de usuario único
 * @property email Email del usuario
 * @property displayName Nombre visible (puede ser diferente del username)
 * @property rating Rating actual del usuario
 * @property isGuest Si es un usuario guest (cuenta temporal)
 */
@Serializable
data class UserInfo(
    val userId: String,
    val username: String,
    val email: String? = null,
    val displayName: String = username,
    val rating: Int = 1500,
    val isGuest: Boolean = false
)

/**
 * Credenciales de login
 * 
 * @property username Nombre de usuario o email
 * @property password Contraseña
 */
data class LoginCredentials(
    val username: String,
    val password: String
)

/**
 * Datos para registro de nuevo usuario
 * 
 * @property username Nombre de usuario único
 * @property email Email único
 * @property password Contraseña
 * @property displayName Nombre visible (opcional)
 */
data class RegistrationData(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String? = null
)

/**
 * Respuesta de autenticación del servidor
 * 
 * @property accessToken Token JWT de acceso (corta duración ~15min)
 * @property refreshToken Token para renovar el accessToken (larga duración ~7 días)
 * @property userInfo Información del usuario
 * @property expiresIn Segundos hasta que expire el accessToken
 */
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userInfo: UserInfo,
    val expiresIn: Int = 900 // 15 minutos por defecto
)
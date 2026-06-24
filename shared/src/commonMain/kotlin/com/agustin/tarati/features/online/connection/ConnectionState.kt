package com.agustin.tarati.features.online.connection


import kotlinx.serialization.Serializable

/**
 * Estado global de conexión del sistema online.
 *
 * Solo describe el estado de la conexión WebSocket y del usuario autenticado.
 * La ubicación en la UI (lobby, pantalla de juego, etc.) es responsabilidad
 * del NavController y no forma parte de este estado.
 */
@Serializable
sealed class ConnectionState {
    /** Sin conexión al servidor. El usuario juega en modo local. */
    @Serializable
    data object Offline : ConnectionState()

    /** Conectado y autenticado. */
    @Serializable
    data class Online(
        val userInfo: OnlineUserInfo,
    ) : ConnectionState()

    /** Conectando al servidor (estado transitorio). */
    @Serializable
    data object Connecting : ConnectionState()

    /**
     * Reconectando automáticamente tras pérdida inesperada de conexión.
     *
     * @property userInfo  Datos del usuario de la sesión que se está recuperando.
     * @property attempt   Número de intento actual (1-based).
     */
    @Serializable
    data class Reconnecting(
        val userInfo: OnlineUserInfo,
        val attempt: Int = 1,
    ) : ConnectionState()

    /**
     * Error de conexión.
     *
     * @property message       Mensaje para mostrar al usuario.
     * @property isRecoverable Si el usuario puede reintentar la conexión.
     */
    @Serializable
    data class Error(
        val message: String,
        val isRecoverable: Boolean = true,
    ) : ConnectionState()
}

/**
 * Ubicación del usuario en la experiencia online.
 * @deprecated No se usa — la navegación la gestiona el NavController.
 */
@Serializable
sealed class Location {
    @Serializable
    data object PrivateRoom : Location()

    @Serializable
    data object Lobby : Location()
}

/**
 * Información del usuario online.
 *
 * @property userId      ID único del usuario en el servidor.
 * @property username    Nombre de usuario.
 * @property displayName Nombre visible (puede diferir del username).
 * @property rating      Rating actual del usuario.
 * @property avatarUrl   URL del avatar (opcional).
 */
@Serializable
data class OnlineUserInfo(
    val userId: String,
    val username: String,
    val displayName: String = username,
    val rating: Int = 1500,
    private val avatarUrl: String? = null,
)

/**
 * Visibilidad de la sala privada.
 * @deprecated No se usa en la implementación actual.
 */
enum class RoomVisibility { PRIVATE, PUBLIC, FRIENDS_ONLY }
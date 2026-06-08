package com.agustin.tarati.features.online.connection


import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato público del ViewModel de conexión online.
 *
 * Gestiona exclusivamente la conexión/desconexión del WebSocket y el estado
 * del usuario autenticado. La navegación entre pantallas (lobby, juego, etc.)
 * es responsabilidad del NavController y no forma parte de este contrato.
 *
 * ## Estados de conexión
 * ```
 * Offline ──connect──► Connecting ──success──► Online
 *                           │                    │
 *                           └──error──► Error    └──disconnect──► Offline
 * ```
 */
interface IConnectionViewModel {

    /**
     * Estado actual de la conexión WebSocket.
     * Emite [ConnectionState.Offline], [ConnectionState.Connecting],
     * [ConnectionState.Online] y [ConnectionState.Error].
     */
    val connectionState: StateFlow<ConnectionState>

    /**
     * Token JWT de la sesión activa. Null si no hay conexión.
     * Disponible para que otros componentes (OnlineLobbyViewModel, etc.)
     * puedan hacer llamadas REST autenticadas sin depender de AuthViewModel.
     */
    val accessToken: String?

    /** True si hay una conexión activa al servidor. */
    val isConnected: Boolean
        get() = connectionState.value is ConnectionState.Online

    /** True si la conexión o reconexión automática está en curso. */
    val isConnecting: Boolean
        get() = connectionState.value is ConnectionState.Connecting ||
                connectionState.value is ConnectionState.Reconnecting

    /**
     * Inicia la conexión WebSocket y autentica al usuario con [authToken].
     *
     * Emite [ConnectionState.Connecting] mientras conecta, luego
     * [ConnectionState.Online] si exitoso o [ConnectionState.Error] si falla.
     *
     * @param serverUrl URL del servidor WebSocket.
     * @param authToken Token JWT del usuario autenticado.
     */
    suspend fun connectToServer(serverUrl: String, authToken: String): Result<OnlineUserInfo>

    /**
     * Cierra la conexión WebSocket y vuelve a [ConnectionState.Offline].
     * Limpia toda la información de sesión.
     */
    suspend fun disconnect()

    /**
     * Reintenta la conexión usando las credenciales de la última sesión.
     * Solo válido cuando [connectionState] es [ConnectionState.Error] con
     * `isRecoverable = true`.
     */
    suspend fun retryConnection()
}
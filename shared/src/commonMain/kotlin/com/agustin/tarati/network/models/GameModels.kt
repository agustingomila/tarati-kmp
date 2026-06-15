package com.agustin.tarati.network.models


import com.agustin.tarati.core.domain.game.play.GameEndReason
import com.agustin.tarati.core.domain.game.play.GameResult
import com.agustin.tarati.core.domain.game.time.TimeControl
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Game - Modelo de dominio para partidas completas
 *
 * Representa una partida finalizada con toda su información:
 * - Jugadores y sus ratings
 * - Time control y tiempos usados
 * - Resultado y método de finalización
 * - PGN de la partida
 *
 * @property id ID único de la partida (UUID)
 * @property whitePlayer Información del jugador blanco
 * @property blackPlayer Información del jugador negro
 * @property timeControl Time control de la partida
 * @property result Resultado de la partida
 * @property endMethod Cómo terminó la partida
 * @property pgn Notación PGN de la partida
 * @property moves Número de movimientos
 * @property startedAt Cuándo comenzó
 * @property endedAt Cuándo terminó
 * @property isRated Si es partida rated
 */
@Serializable
data class Game(
    val id: String,
    val whitePlayer: GamePlayerInfo,
    val blackPlayer: GamePlayerInfo,
    val timeControl: GameTimeControl,
    val result: GameResult,
    val endMethod: GameEndReason,
    val pgn: String,
    val moves: Int,
    val startedAt: Instant,
    val endedAt: Instant,
    val isRated: Boolean = true
)

/**
 * GamePlayerInfo - Información de un jugador en una partida
 *
 * @property userId ID del usuario
 * @property username Username del jugador
 * @property ratingBefore Rating antes de la partida
 * @property ratingAfter Rating después de la partida
 * @property ratingChange Cambio de rating (puede ser negativo)
 * @property timeUsed Tiempo usado en milisegundos
 */
@Serializable
data class GamePlayerInfo(
    val userId: String,
    val username: String,
    val ratingBefore: Int,
    val ratingAfter: Int,
    val ratingChange: Int,
    val timeUsed: Long
)

/**
 * GameTimeControl - Configuración de tiempo de la partida
 *
 * @property type Tipo de time control
 * @property initialTime Tiempo inicial en segundos
 * @property increment Incremento por movimiento en segundos
 */
@Serializable
data class GameTimeControl(
    val type: TimeControl,
    val initialTime: Int,
    val increment: Int
) {
    /**
     * Devuelve una representación en string del time control
     * Ejemplo: "5+3" (5 minutos + 3 segundos de incremento)
     */
    fun toDisplayString(): String {
        val minutes = initialTime / 60
        return if (increment > 0) {
            "$minutes+$increment"
        } else {
            "$minutes"
        }
    }
}

/**
 * Entrada interna de cola de matchmaking — usada por [MatchmakingEngine.getOpenSearches]
 * y [BotAgent.decideNextStrategy]. No se serializa directamente.
 */
data class OpenSearchEntry(
    val userId: String,
    val rating: Int,
    val timeControl: String,   // key: "blitz", "rapid", etc.
    val rated: Boolean,
    val waitingSinceMs: Long,
)

/**
 * Búsqueda abierta visible en el lobby del cliente.
 * Respuesta del endpoint GET /api/lobby/open-searches.
 *
 * @property searchId        ticketId Redis del jugador en cola.
 * @property playerUsername  displayName del jugador buscando rival.
 * @property playerRating    ELO del jugador.
 * @property timeControl     Time control de la búsqueda.
 * @property rated           Si la partida será rated.
 * @property waitingSinceMs  Epoch ms desde que se creó la búsqueda.
 * @property isBot           True si el jugador es un bot (para UI diferenciada).
 */
@Serializable
data class OpenSearchDto(
    val searchId: String,
    /** ID del jugador que publicó la búsqueda. Requerido para [ClientMessage.JoinOpenSearch]. */
    val userId: String,
    val playerUsername: String,
    val playerRating: Int,
    val timeControl: GameTimeControl,
    val rated: Boolean,
    val waitingSinceMs: Long,
    val isBot: Boolean,
)


/**
 * Usado para listar partidas sin toda la información completa.
 *
 * @property id ID de la partida
 * @property whiteUsername Username del blanco
 * @property blackUsername Username del negro
 * @property whiteRating Rating del blanco
 * @property blackRating Rating del negro
 * @property timeControl Time control
 * @property result Resultado
 * @property moves Número de movimientos
 * @property endedAt Cuándo terminó
 */
@Serializable
data class GameSummary(
    val id: String,
    val whiteUsername: String,
    val blackUsername: String,
    val whiteRating: Int,
    val blackRating: Int,
    val timeControl: GameTimeControl,
    val result: GameResult,
    val moves: Int,
    val endedAt: Instant
)

// ── Endpoint response DTOs ────────────────────────────────────────────────────

/**
 * Snapshot de una partida en curso para el endpoint GET /api/live-games.
 *
 * Los tiempos [whiteTimeMs]/[blackTimeMs] son el instante de la consulta;
 * el cliente los interpola localmente igual que hace con los GameStateUpdates
 * del WebSocket.
 *
 * @property gameId              ID único de la sesión.
 * @property whiteUsername       Username del jugador blanco.
 * @property blackUsername       Username del jugador negro.
 * @property whiteRating         ELO blancas al inicio de la partida.
 * @property blackRating         ELO negras al inicio de la partida.
 * @property timeControl         Etiqueta + tiempos del time control.
 * @property rated               Si la partida afecta el rating.
 * @property moveCount           Cantidad de movimientos jugados hasta ahora.
 * @property whiteTimeMs         Tiempo restante de blancas en ms (snapshot).
 * @property blackTimeMs         Tiempo restante de negras en ms (snapshot).
 * @property currentTurn         "white" o "black" — quién tiene el turno ahora.
 * @property startedAtMs         Epoch ms del inicio de la partida (para mostrar duración).
 * @property positionNotation    Notación FEN-like del estado actual del tablero.
 * @property spectatingAllowed   Si los espectadores pueden unirse a observar.
 * @property tournamentId        ID del torneo al que pertenece la partida, null si no es de torneo.
 * @property tournamentName      Nombre del torneo, null si no es de torneo.
 * @property tournamentRound     Ronda actual del torneo, null si no es de torneo.
 * @property tournamentTotalRounds Total de rondas del torneo, null si no es de torneo.
 */
@Serializable
data class LiveGameDto(
    val gameId: String,
    val whiteUsername: String,
    val blackUsername: String,
    val whiteRating: Int,
    val blackRating: Int,
    val timeControl: GameTimeControl,
    val rated: Boolean,
    val moveCount: Int,
    val whiteTimeMs: Long,
    val blackTimeMs: Long,
    val currentTurn: String,
    val startedAtMs: Long,
    /** Notación FEN-like del estado actual del tablero. Permite renderizar una miniatura. */
    val positionNotation: String = "",
    /** Si los espectadores pueden unirse a observar esta partida. */
    val spectatingAllowed: Boolean = false,
    /** ID del torneo al que pertenece esta partida. Null para partidas regulares. */
    val tournamentId: String? = null,
    /** Nombre visible del torneo. Null para partidas regulares. */
    val tournamentName: String? = null,
    /** Número de ronda actual dentro del torneo. Null para partidas regulares. */
    val tournamentRound: Int? = null,
    /** Total de rondas del torneo. Null para partidas regulares. */
    val tournamentTotalRounds: Int? = null,
)

/**
 * Entrada del historial de partidas del jugador autenticado.
 * Respuesta del endpoint GET /api/games.
 *
 * Orientado a "mis partidas": el resultado ([result]) ya está expresado
 * desde la perspectiva del jugador que consulta ("win" / "loss" / "draw"),
 * y [myColor] indica con qué color jugó.
 *
 * @property gameId           ID de la partida.
 * @property opponentUsername Username del oponente.
 * @property opponentRating   ELO del oponente al inicio de la partida.
 * @property myColor          "white" o "black".
 * @property result           "win" | "loss" | "draw" desde la perspectiva del jugador.
 * @property endMethod        Cómo terminó la partida (clave de [GameEndReason]).
 * @property timeControl      Etiqueta + tiempos del time control.
 * @property rated            Si la partida era rated.
 * @property moveCount        Número de movimientos.
 * @property ratingChange     Cambio de ELO (positivo = ganó, negativo = perdió).
 * @property ratingAfter      ELO después de la partida.
 * @property endedAtMs        Epoch ms de fin de partida (para mostrar fecha).
 */
@Serializable
data class GameHistoryDto(
    val gameId: String,
    val opponentUsername: String,
    val opponentRating: Int,
    val myColor: String,
    val result: String,
    val endMethod: String,
    val timeControl: GameTimeControl,
    val rated: Boolean,
    val moveCount: Int,
    val ratingChange: Int,
    val ratingAfter: Int,
    val endedAtMs: Long,
    /** Nombre del jugador cuya perspectiva se usa. Null en "Mis Partidas" (el propio usuario),
     *  presente en el feed (partida de un jugador seguido). */
    val playerUsername: String? = null,
)

/**
 * Respuesta paginada genérica.
 *
 * @property items   Lista de elementos de la página actual.
 * @property total   Total de elementos (sin paginar), para calcular el número de páginas en el cliente.
 * @property page    Página actual (0-based).
 * @property limit   Tamaño de página solicitado.
 */
@Serializable
data class PagedResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val limit: Int,
)

// ── Usuarios en línea ─────────────────────────────────────────────────────────

@Serializable
enum class OnlineUserStatus {
    /** Tiene una sesión de juego activa o iniciándose. */
    PLAYING,

    /** Conectado al WebSocket pero sin partida activa. */
    IN_LOBBY,
}

/**
 * Usuario conectado actualmente visible en el lobby.
 *
 * @property userId     ID del usuario.
 * @property displayName Nombre para mostrar (displayName si tiene, username si no).
 * @property isGuest    True para cuentas temporales sin registro.
 * @property status     Estado de actividad actual.
 * @property ratingBlitz Rating blitz (null para invitados).
 */
@Serializable
data class OnlineUserDto(
    val userId: String,
    val displayName: String,
    val isGuest: Boolean,
    val isBot: Boolean = false,
    val status: OnlineUserStatus,
    val ratingBlitz: Int? = null,
)

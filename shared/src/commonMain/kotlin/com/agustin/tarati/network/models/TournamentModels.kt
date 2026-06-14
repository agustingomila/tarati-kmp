package com.agustin.tarati.network.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.time.Instant

// ── Enumerados ────────────────────────────────────────────────────────────────

/**
 * Formato del torneo.
 * ROUND_ROBIN: todos contra todos, emparejamientos generados al inicio.
 * SWISS: emparejamiento por puntos por ronda, evitando repetir rivales.
 *
 * [key] es el valor almacenado en la columna tournaments.type.
 */
@Serializable
enum class TournamentType {
    ROUND_ROBIN, SWISS;

    val key: String get() = name.lowercase()
}

/**
 * Estado del ciclo de vida de un torneo.
 * REGISTERING → ACTIVE → FINISHED (o CANCELLED en cualquier punto antes de FINISHED)
 *
 * [key] es el valor almacenado en la columna tournaments.status.
 */
@Serializable
enum class TournamentStatus {
    REGISTERING, ACTIVE, FINISHED, CANCELLED;

    val key: String get() = name.lowercase()
}

/**
 * Estado de un emparejamiento individual dentro de un torneo.
 *
 * PENDING   — emparejamiento creado por el motor, partida aún no iniciada.
 * ACTIVE    — partida en curso (gameId asignado en GameSessionManager).
 * COMPLETED — partida finalizada (result asignado).
 *
 * [key] es el valor almacenado en la columna tournament_games.status.
 */
@Serializable
enum class TournamentGameStatus {
    PENDING, ACTIVE, COMPLETED;

    val key: String get() = name.lowercase()
}

// ── Request ───────────────────────────────────────────────────────────────────

/**
 * Request para crear un nuevo torneo.
 *
 * @property name              Nombre visible del torneo (1–100 chars)
 * @property type              Formato del torneo
 * @property timeControl       Tipo de control de tiempo: "bullet" | "blitz" | "rapid" | "classical"
 * @property initialTime       Tiempo inicial en segundos
 * @property increment         Incremento por movimiento en segundos
 * @property isRated           Si las partidas del torneo afectan el rating Glicko-2
 * @property minPlayers        Mínimo de jugadores para poder iniciar
 * @property maxPlayers        Máximo de inscritos
 * @property spectatingAllowed Si los espectadores externos pueden ver las partidas del torneo
 */
@Serializable
data class CreateTournamentRequest(
    val name: String,
    val type: TournamentType,
    val timeControl: String,
    val initialTime: Int,
    val increment: Int = 0,
    val isRated: Boolean = true,
    val minPlayers: Int = 4,
    val maxPlayers: Int = 16,
    val spectatingAllowed: Boolean = true,
)

// ── DTOs de respuesta ─────────────────────────────────────────────────────────

/**
 * Resumen de torneo para listas.
 * Respuesta de GET /api/tournaments.
 */
@Immutable
@Serializable
data class TournamentSummaryDto(
    val id: String,
    val name: String,
    val type: TournamentType,
    val status: TournamentStatus,
    val timeControl: String,
    val initialTime: Int,
    val increment: Int,
    val isRated: Boolean,
    val minPlayers: Int,
    val maxPlayers: Int,
    val participantCount: Int,
    val creatorId: String,
    val creatorUsername: String,
    val spectatingAllowed: Boolean = true,
    val createdAt: Instant,
    val startsAt: Instant?,
    val finishedAt: Instant?,
)

/**
 * Detalle completo de un torneo, incluyendo standings y rondas.
 * Respuesta de GET /api/tournaments/:id.
 */
@Immutable
@Serializable
data class TournamentDetailDto(
    val id: String,
    val name: String,
    val type: TournamentType,
    val status: TournamentStatus,
    val timeControl: String,
    val initialTime: Int,
    val increment: Int,
    val isRated: Boolean,
    val minPlayers: Int,
    val maxPlayers: Int,
    val creatorId: String,
    val creatorUsername: String,
    val currentRound: Int,
    val totalRounds: Int,
    val spectatingAllowed: Boolean = true,
    val standings: List<TournamentStandingDto>,
    val rounds: List<TournamentRoundDto>,
    val createdAt: Instant,
    val startsAt: Instant?,
    val finishedAt: Instant?,
)

/**
 * Posición de un jugador en la tabla de clasificación del torneo.
 *
 * score: medios puntos de notación estándar 1-½-0 (vic=2, emp=1, der=0).
 * buchholz: suma de scores (en medios puntos) de los oponentes enfrentados (desempate en Swiss).
 */
@Immutable
@Serializable
data class TournamentStandingDto(
    val rank: Int,
    val userId: String,
    val username: String,
    val displayName: String?,
    val country: String?,
    val rating: Int,
    val score: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val buchholz: Int,
)

/**
 * Una ronda del torneo con todos sus enfrentamientos.
 */
@Immutable
@Serializable
data class TournamentRoundDto(
    val roundNumber: Int,
    val pairings: List<TournamentPairingDto>,
)

/**
 * Enfrentamiento individual dentro de una ronda.
 *
 * gameId es null hasta que el GameSessionManager crea la partida al iniciar la ronda.
 * result es null hasta que la partida termina: "white_wins" | "black_wins" | "draw".
 */
@Immutable
@Serializable
data class TournamentPairingDto(
    val pairingId: String,
    val whiteId: String,
    val whiteUsername: String,
    val blackId: String,
    val blackUsername: String,
    val gameId: String?,
    val result: String?,
    val status: TournamentGameStatus,
)

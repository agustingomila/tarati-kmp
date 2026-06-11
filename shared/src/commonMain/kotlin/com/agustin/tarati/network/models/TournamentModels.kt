package com.agustin.tarati.network.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

// ── Enumerados ────────────────────────────────────────────────────────────────

/**
 * Formato del torneo.
 * ROUND_ROBIN: todos contra todos, emparejamientos generados al inicio.
 * SWISS: emparejamiento por puntos por ronda, evitando repetir rivales.
 */
@Serializable
enum class TournamentType { ROUND_ROBIN, SWISS }

/**
 * Estado del ciclo de vida de un torneo.
 * REGISTERING → ACTIVE → FINISHED (o CANCELLED en cualquier punto antes de FINISHED)
 */
@Serializable
enum class TournamentStatus { REGISTERING, ACTIVE, FINISHED, CANCELLED }

// ── Request ───────────────────────────────────────────────────────────────────

/**
 * Request para crear un nuevo torneo.
 *
 * @property name Nombre visible del torneo (1–100 chars)
 * @property type Formato del torneo
 * @property timeControl Tipo de control de tiempo: "bullet" | "blitz" | "rapid" | "classical"
 * @property initialTime Tiempo inicial en segundos
 * @property increment Incremento por movimiento en segundos
 * @property isRated Si las partidas del torneo afectan el rating Glicko-2
 * @property minPlayers Mínimo de jugadores para poder iniciar
 * @property maxPlayers Máximo de inscritos
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
)

// ── DTOs de respuesta ─────────────────────────────────────────────────────────

/**
 * Resumen de torneo para listas.
 * Respuesta de GET /api/tournaments.
 */
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
    val createdAt: Instant,
    val startsAt: Instant?,
    val finishedAt: Instant?,
)

/**
 * Detalle completo de un torneo, incluyendo standings y rondas.
 * Respuesta de GET /api/tournaments/:id.
 */
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
    val standings: List<TournamentStandingDto>,
    val rounds: List<TournamentRoundDto>,
    val createdAt: Instant,
    val startsAt: Instant?,
    val finishedAt: Instant?,
)

/**
 * Posición de un jugador en la tabla de clasificación del torneo.
 *
 * score: sistema 2-1-0 (victoria=2, empate=1, derrota=0).
 * buchholz: suma de scores de todos los oponentes enfrentados (desempate en Swiss).
 */
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
 * status: "pending" | "active" | "completed"
 */
@Serializable
data class TournamentPairingDto(
    val pairingId: String,
    val whiteId: String,
    val whiteUsername: String,
    val blackId: String,
    val blackUsername: String,
    val gameId: String?,
    val result: String?,
    val status: String,
)

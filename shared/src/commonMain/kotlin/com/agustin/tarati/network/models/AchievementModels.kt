package com.agustin.tarati.network.models

import kotlinx.serialization.Serializable

/** Cuerpo de POST /api/achievements/unlock */
@Serializable
data class UnlockAchievementRequest(
    val achievementId: String,
)

/** Cuerpo de POST /api/achievements/progress */
@Serializable
data class AchievementProgressRequest(
    val achievementId: String,
    val steps: Int,
)

/**
 * Representación de un logro desde el servidor.
 *
 * - [unlockedAt]: timestamp ISO-8601, o null si el logro incremental aún
 *   no se completó (solo tiene progreso de pasos registrado).
 * - [currentSteps]: relevante únicamente para logros incrementales.
 */
@Serializable
data class ServerAchievementDto(
    val achievementId: String,
    val unlockedAt: String?,
    val currentSteps: Int,
)

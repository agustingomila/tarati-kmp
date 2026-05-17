package com.agustin.tarati.services.achievements

import kotlinx.coroutines.flow.StateFlow

/**
 * Repositorio de logros/achievements del juego.
 *
 * Mantiene el estado de progreso de logros y proporciona
 * funcionalidad para desbloquear achievements.
 *
 * ## Implementaciones
 * - **Android**: Usa Google Play Games Services para sincronizar logros
 * - **Desktop**: Implementación en memoria (solo local, sin sincronización)
 * - **iOS**: Puede usar Game Center en el futuro
 *
 * ## Paletas Desbloqueables
 * Las paletas estacionales y especiales se desbloquean permanentemente:
 * - **Halloween**: Ganando en dificultad Champion el 31 de octubre
 * - **Christmas**: Ganando en dificultad Champion el 25 de diciembre
 * - **Aurora**: Ganando un logro específico
 * - **Ember**: Ganando un logro específico
 *
 * ## Uso
 * ```kotlin
 * class SettingsViewModel(
 *     private val achievementsRepository: IAchievementsRepository
 * ) {
 *     val halloweenAvailable = achievementsRepository.halloweenUnlocked
 * }
 * ```
 */
interface IAchievementsRepository {
    /**
     * Mapa de achievement IDs a su progreso actual.
     * - Key: Achievement ID (ej: "first_win", "play_10_games")
     * - Value: Progreso actual (0.0 a 1.0, donde 1.0 = completado)
     */
    val achievements: StateFlow<Map<String, Float>>

    /**
     * True si la paleta Halloween fue desbloqueada permanentemente.
     * Se desbloquea ganando en Champion el 31 de octubre.
     */
    val halloweenUnlocked: StateFlow<Boolean>

    /**
     * True si la paleta Christmas fue desbloqueada permanentemente.
     * Se desbloquea ganando en Champion el 25 de diciembre.
     */
    val christmasUnlocked: StateFlow<Boolean>

    /**
     * True si la paleta Aurora fue desbloqueada permanentemente.
     */
    val auroraUnlocked: StateFlow<Boolean>

    /**
     * True si la paleta Ember fue desbloqueada permanentemente.
     */
    val emberUnlocked: StateFlow<Boolean>

    /**
     * Desbloquea un achievement específico.
     *
     * @param achievementId ID del achievement a desbloquear
     */
    suspend fun unlockAchievement(achievementId: String)

    /**
     * Actualiza el progreso de un achievement incremental.
     *
     * @param achievementId ID del achievement
     * @param progress Progreso actual (0.0 a 1.0)
     */
    suspend fun updateProgress(achievementId: String, progress: Float)

    /**
     * Sincroniza los achievements con el servicio de la plataforma.
     * En Android esto sincroniza con Google Play Games.
     * En Desktop puede ser no-op.
     */
    suspend fun sync()
}
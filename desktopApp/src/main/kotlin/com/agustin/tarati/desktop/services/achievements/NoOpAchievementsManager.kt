package com.agustin.tarati.desktop.services.achievements

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.services.achievements.IAchievementsManager

/**
 * Implementación no-op de [IAchievementsManager] para Desktop.
 *
 * Los logros son una característica específica de Android con Google Play Games.
 * En Desktop no existe un sistema de logros nativo equivalente, por lo que todas
 * las notificaciones se ignoran silenciosamente.
 *
 * ## Posible evolución futura
 *
 * Si en el futuro se desea agregar un sistema de logros local en Desktop:
 * - Crear una base de datos local (SQLite) para rastrear progreso
 * - Mostrar notificaciones del sistema cuando se desbloquea un logro
 * - Agregar una pantalla de logros en la UI de settings
 *
 * Por ahora, esta implementación permite que el código compartido (GameEvents)
 * funcione sin modificaciones en ambas plataformas.
 */
class NoOpAchievementsManager : IAchievementsManager {

    /**
     * No-op: Desktop no rastrea movimientos individuales para logros.
     */
    override suspend fun onMoveApplied(move: Move, previousState: GameState, newState: GameState) = Unit

    /**
     * No-op: Desktop no desbloquea logros al terminar partidas.
     */
    override suspend fun onGameOver(matchState: MatchState, playerSide: CobColor, difficulty: Difficulty) = Unit

    /**
     * No-op: Desktop no desbloquea el logro de completar el tutorial.
     */
    override suspend fun onTutorialCompleted() = Unit

    /**
     * No-op: Desktop no tiene UI de logros para mostrar.
     */
    override fun showAchievementsUI() = Unit
}
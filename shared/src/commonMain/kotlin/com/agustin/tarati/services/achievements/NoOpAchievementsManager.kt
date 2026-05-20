package com.agustin.tarati.services.achievements

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move

/**
 * Implementación no-op de [IAchievementsManager] para Desktop y Web.
 *
 * Los logros son una característica Android-only (Google Play Games).
 * En otras plataformas todas las notificaciones se ignoran silenciosamente.
 */
class NoOpAchievementsManager : IAchievementsManager {
    override suspend fun onMoveApplied(move: Move, previousState: GameState, newState: GameState) = Unit
    override suspend fun onGameOver(matchState: MatchState, playerSide: CobColor, difficulty: Difficulty) = Unit
    override suspend fun onTutorialCompleted() = Unit
    override fun showAchievementsUI() = Unit
}

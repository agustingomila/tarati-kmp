package com.agustin.tarati.services.achievements

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementación no-op de [IAchievementsManager].
 *
 * Usada en plataformas sin sistema de logros activo (por ejemplo, iOS en el futuro).
 * Desktop y Web usan [ServerAchievementsManager] en su lugar.
 */
class NoOpAchievementsManager : IAchievementsManager {
    override val unlockedPaletteAchievements: StateFlow<Set<AchievementId>> =
        MutableStateFlow(emptySet())

    override suspend fun onMoveApplied(move: Move, previousState: GameState, newState: GameState) = Unit
    override suspend fun onGameOver(matchState: MatchState, playerSide: CobColor, difficulty: Difficulty?) = Unit
    override suspend fun onTutorialCompleted() = Unit
    override fun showAchievementsUI(onNavigateToScreen: () -> Unit) = Unit
}

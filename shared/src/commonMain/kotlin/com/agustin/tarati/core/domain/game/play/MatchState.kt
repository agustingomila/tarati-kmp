package com.agustin.tarati.core.domain.game.play

import androidx.compose.runtime.Stable
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState

@Stable
data class MatchState(
    val gameState: GameState,
    val gameEndReason: GameEndReason,
    val winner: CobColor?,
    val moveHistory: Map<String, Int>,
) {
    companion object {
        fun createInitialMatchState(): MatchState =
            MatchState(
                gameState = initialGameState(),
                gameEndReason = GameEndReason.UNDETERMINED,
                winner = null,
                moveHistory = mapOf(),
            )
    }
}

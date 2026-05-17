package com.agustin.tarati.core.domain.game.play

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState

data class MatchState(
    val gameState: GameState,
    val gameResult: GameResult,
    val winner: CobColor?,
    val moveHistory: Map<String, Int>,
) {
    companion object {
        fun createInitialMatchState(): MatchState =
            MatchState(
                gameState = initialGameState(),
                gameResult = GameResult.UNDETERMINED,
                winner = null,
                moveHistory = mapOf(),
            )
    }
}

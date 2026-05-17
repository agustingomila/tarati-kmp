package com.agustin.tarati.features.game

import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameStatus

data class AiThinkingDependencies(
    val gameStatus: GameStatus,
    val currentTurn: CobColor,
    val whiteIsAI: Boolean,
    val blackIsAI: Boolean,
    val isEditing: Boolean,
    /**
     * The [EvaluationConfig] that should be applied to the engine before the
     * next move is calculated. Derived from the difficulty of whichever side
     * is currently to move, so AI vs AI games can use different depths per side.
     */
    val currentTurnConfig: EvaluationConfig,
    /**
     * FEN-like hash of the current board position. Included so that
     * [GameEffects]'s LaunchedEffect re-triggers whenever the board
     * changes even if [currentTurn] stays the same — which happens after
     * a forced in-place promotion (patent §6.3): the promoting player
     * keeps their turn but the board hash is different, allowing the AI
     * to receive a new calculateAIMove call for the post-promotion state.
     */
    val boardHash: String,
) {
    /** True when the current turn belongs to the AI engine. */
    val isAITurn: Boolean
        get() = (currentTurn == CobColor.WHITE && whiteIsAI) ||
                (currentTurn == CobColor.BLACK && blackIsAI)
}
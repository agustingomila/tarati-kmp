package com.agustin.tarati.ui.components.game

import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.features.settings.BoardVisualState
import kotlinx.serialization.Serializable

@Serializable
data class BoardState(
    val gameState: GameState,
    val boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
    val boardVisualState: BoardVisualState,
    val newGame: Boolean = false,
    val aiEnabled: Boolean = true,
    /** True when the White band is controlled by the AI engine. */
    val whiteIsAI: Boolean = false,
    /** True when the Black band is controlled by the AI engine. */
    val blackIsAI: Boolean = true,
    val isEditing: Boolean = false,
) {
    companion object {
        fun createInitialBoardState(): BoardState =
            BoardState(
                gameState = initialGameState(),
                boardVisualState = BoardVisualState(),
                boardOrientation = BoardOrientation.PORTRAIT_WHITE,
                aiEnabled = false,
                blackIsAI = false,
                newGame = true,
            )
    }
}
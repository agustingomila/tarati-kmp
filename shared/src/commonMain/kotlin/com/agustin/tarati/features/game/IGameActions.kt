package com.agustin.tarati.features.game

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import kotlinx.coroutines.flow.StateFlow

interface IGameActions {
    fun gameOver()

    fun stopGame()

    fun resumeGame()

    fun endEditing()

    /** Prevents the logo transition animation on the next GameScreen entry. */
    fun suppressLogoTransition()

    fun startGame(playerSide: CobColor)

    fun updateGameState(gameState: GameState)

    /**
     * True if the current game was started from an edited board position.
     * Achievements are disabled for edited games to prevent manipulation.
     */
    val startedFromEditedBoard: StateFlow<Boolean>

    /**
     * True if the current game was loaded from a previously saved game
     * (via [importGameFromMatchDto]).
     * Achievements are disabled to prevent loading a winning position
     * and claiming credit for a fresh game.
     */
    val startedFromImportedGame: StateFlow<Boolean>
}
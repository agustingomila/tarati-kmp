package com.agustin.tarati.features.game

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.ui.components.editor.IEditBoard
import kotlinx.coroutines.flow.StateFlow

interface IGameService :
    IGameActions,
    IEditBoard {

    // ── Readable game state ───────────────────────────────────────────────
    // Exposed here (not only in IGameModel) so non-Compose consumers like
    // GameEvents can read current state without depending on GameManager directly.

    val gameState: StateFlow<GameState>
    val history: StateFlow<StableHistoryList>
    val moveIndex: StateFlow<Int>
    val gameStatus: StateFlow<GameStatus>

    // ── Operations ───────────────────────────────────────────────────────

    fun saveGameState()

    fun updateUserName(name: String)

    fun boardPositionCopied()

    fun updateBoardOrientation(newOrientation: BoardOrientation)

    /**
     * Rotates the board 90° clockwise and marks the orientation as manually overridden,
     * so that automatic portrait/landscape recalculations no longer override it.
     */
    fun rotateBoardManually()

    /**
     * Clears the manual rotation flag, allowing [GameEffects] to resume computing
     * the orientation automatically from the screen configuration and player side.
     * Should be called whenever a new game starts.
     */
    fun resetManualRotation()

    fun updateAIEnabled(newAIEnabled: Boolean)

    /**
     * Sets the search depth/difficulty for the AI controlling [color].
     * Both bands have independent difficulty settings so that AI vs AI games
     * can pit different strength levels against each other.
     */
    fun updateDifficulty(color: CobColor, difficulty: Difficulty)

    fun addMove(
        move: Move,
        nextState: GameState,
        onMoveRecord: () -> Unit,
    )

    fun redoMove()

    fun undoMove()

    fun moveToCurrentState()

    /**
     * Navega directamente al estado [index] del historial.
     * [index] = -1 restaura la posición inicial.
     * @see com.agustin.tarati.core.domain.game.manager.GameManager.moveToIndex
     */
    fun moveToIndex(index: Int)
}
package com.agustin.tarati.services.clipboard

import com.agustin.tarati.core.data.database.dto.PGNHeader.Companion.generatePGNMoveHistory
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.utils.logging.LoggingFactory

class GameClipboardHelper(
    private val iClipboardService: IClipboardService,
) {
    private val logger by lazy { LoggingFactory.getLogger() }

    suspend fun copyBoardPosition(position: String): Boolean = iClipboardService.copyText("tarati-pos", position)

    suspend fun copyMoveHistory(
        moves: List<Move>,
        gameState: GameState,
        playerSide: CobColor,
        aiEnabled: Boolean,
        positionHistory: Map<String, Int> = emptyMap(),
    ): Boolean {
        val pgnText =
            moves.generatePGNMoveHistory(
                gameState = gameState,
                playerSide = playerSide,
                aiEnabled = aiEnabled,
                appendFen = true,
                positionHistory = positionHistory,
            )
        return iClipboardService.copyText("tarati-pgn", pgnText)
    }

    suspend fun pasteBoardPosition(): GameState? {
        val text = iClipboardService.getText() ?: return null
        return try {
            GameState.parseBoardNotation(text)
        } catch (e: Exception) {
            logger.error("An error occurred while pasting position ($text) from the clipboard")
            null
        }
    }
}
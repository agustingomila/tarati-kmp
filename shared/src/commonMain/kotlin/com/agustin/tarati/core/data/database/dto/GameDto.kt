package com.agustin.tarati.core.data.database.dto

import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.Move

data class GameDto(
    /** Posición del tablero al inicio de la partida (antes del primer movimiento). */
    val initialBoardPosition: String = initialGameState().toPositionNotation(),
    /** Posición final del tablero al guardar (útil para la miniatura). */
    val boardPosition: String = initialGameState().toPositionNotation(),
    val matchResult: MatchResult = MatchResult.UNDEFINED,
    val moveHistory: List<Move> = emptyList(),
) {
    companion object
}
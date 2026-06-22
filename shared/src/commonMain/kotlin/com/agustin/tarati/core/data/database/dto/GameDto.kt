package com.agustin.tarati.core.data.database.dto

import androidx.compose.runtime.Immutable
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.Move

/**
 * DTO de solo lectura de una partida. Anotado [@Immutable] para Compose: aunque
 * [moveHistory] es un `List<Move>` (que el compilador infiere como inestable),
 * el DTO nunca muta su contenido tras construirse, por lo que la promesa de
 * inmutabilidad es válida y habilita el skip de recomposición en los composables
 * que lo reciben (ver [MatchDto]).
 */
@Immutable
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
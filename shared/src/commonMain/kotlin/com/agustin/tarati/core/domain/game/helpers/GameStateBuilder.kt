package com.agustin.tarati.core.domain.game.helpers

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.cleanGameState
import com.agustin.tarati.core.domain.game.play.Move

// Builder para crear estados de juego complejos
class GameStateBuilder(
    initialState: GameState = cleanGameState(),
) {
    private var state = initialState

    fun clearCobs(): GameStateBuilder {
        state = cleanGameState()
        return this
    }

    fun setTurn(turn: CobColor): GameStateBuilder {
        state = state.copy(currentTurn = turn)
        return this
    }

    fun setCob(
        position: Vertex,
        color: CobColor,
        isUpgraded: Boolean = false,
    ): GameStateBuilder {
        state = state.modifyCob(position, color, isUpgraded)
        return this
    }

    fun setCob(
        position: Vertex,
        cob: Cob,
    ): GameStateBuilder {
        state = state.modifyCob(position, cob)
        return this
    }

    fun removeCob(position: Vertex): GameStateBuilder {
        state = state.modifyCob(position)
        return this
    }

    fun moveCob(move: Move): GameStateBuilder {
        state = state.moveCob(move)
        return this
    }

    fun build(): GameState = state
}

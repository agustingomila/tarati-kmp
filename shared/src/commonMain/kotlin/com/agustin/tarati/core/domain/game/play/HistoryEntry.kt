package com.agustin.tarati.core.domain.game.play

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val move: Move,
    val gameState: GameState,
) {
    companion object {
        fun fromPair(newEntry: Pair<Move, GameState>): HistoryEntry = HistoryEntry(newEntry.first, newEntry.second)
    }
}

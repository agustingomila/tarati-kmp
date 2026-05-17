package com.agustin.tarati.core.domain.game.pieces

import com.agustin.tarati.core.domain.game.board.GameBoard.adjacencyMap
import com.agustin.tarati.core.domain.game.board.GameBoard.edges
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.MatchResult.BLACK_WON
import com.agustin.tarati.core.domain.game.play.MatchResult.UNDEFINED
import com.agustin.tarati.core.domain.game.play.MatchResult.WHITE_WON
import kotlinx.serialization.Serializable

@Serializable
enum class CobColor { WHITE, BLACK }

val CobColor.opponent: CobColor get() = if (this == BLACK) WHITE else BLACK

fun CobColor.getName(): Char =
    when (this) {
        WHITE -> 'w'
        else -> 'b'
    }

fun CobColor.isMaximizingPlayer(): Boolean = this == WHITE

/**
 * Flips all opponent pieces adjacent to [to] that were NOT already adjacent to [from]
 * before the move (pre-adjacency rule: a piece cannot be captured by a piece that was
 * already adjacent to it before the move).
 *
 * Captured pieces are flipped to the new color but are never auto-promoted — promotion
 * only occurs when a cob is actively advanced onto an upgrade vertex via a move.
 */
fun CobColor.flipAdjacentCobs(
    mutable: MutableMap<Vertex, Cob>,
    to: Vertex,
    from: Vertex,
) {
    val originAdjacents = adjacencyMap[from]?.toSet() ?: emptySet()
    edges.forEach { edge ->
        val adjacent =
            when {
                edge.from == to -> edge.to
                edge.to == to -> edge.from
                else -> null
            } ?: return@forEach

        if (adjacent !in originAdjacents) {
            mutable[adjacent]?.takeIf { it.color != this }?.let { adjCob ->
                mutable[adjacent] = adjCob.copy(color = this)
            }
        }
    }
}

fun CobColor.getMatchResult(): MatchResult =
    when {
        this == WHITE -> WHITE_WON
        this == BLACK -> BLACK_WON
        else -> UNDEFINED
    }
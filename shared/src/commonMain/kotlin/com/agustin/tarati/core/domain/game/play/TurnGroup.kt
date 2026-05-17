package com.agustin.tarati.core.domain.game.play

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.opponent

/**
 * Represents a single player turn in the move history, which may consist of
 * one or two actions:
 *
 * - **Normal turn**: one [Move] (forward move, rok move, or home-base capture).
 * - **Promotion turn**: two [Move]s — a forced in-place promotion ([Move.isPromotion] == true)
 *   followed immediately by a normal move with the newly-created rok.
 *
 * This model is the correct unit for display in the move-history list and PGN
 * generation: it avoids the desynchronisation caused by treating every [Move]
 * as an independent half-move when a promotion does not consume the turn.
 *
 * Example rendering:
 * ```
 *   1.  B6→B1     C12=R C12→B6
 *   2.  A1→B2     D3→C8
 * ```
 *
 * @property color  The player whose turn this group represents.
 * @property moves  1 or 2 moves, always non-empty. When size == 2, moves[0] is the promotion.
 */
data class TurnGroup(
    val color: CobColor,
    val moves: List<Move>,
) {
    init {
        require(moves.isNotEmpty()) { "TurnGroup must contain at least one move" }
        require(moves.size <= 2) { "TurnGroup cannot contain more than two moves" }
    }

    /**
     * Human-readable notation for this turn.
     * - Single move:      "B6→B1"
     * - Promotion + move: "C12=R C12→B6"
     */
    val notation: String get() = moves.joinToString(" ") { it.name }

    /** True when this turn includes a forced in-place promotion. */
    val hasPromotion: Boolean get() = moves.size == 2 && moves[0].isPromotion()
}

/**
 * Groups a flat [List<Move>] into a [List<TurnGroup>], correctly handling
 * forced-promotion turns where the same player makes two consecutive actions
 * (promotion + subsequent move) within a single turn.
 *
 * A forced promotion ([Move.isPromotion] == true) and the move that immediately
 * follows it are merged into a single [TurnGroup] for the same player.
 *
 * The resulting list maintains stable positional indices for undo/redo
 * highlighting: [moveIndexToGroupIndex] can map a flat move index back to its
 * [TurnGroup] row.
 */
fun List<Move>.groupByTurns(): List<TurnGroup> {
    val groups = mutableListOf<TurnGroup>()
    var i = 0

    // Reconstruct which color made each move by replaying the turn sequence.
    // Turns alternate WHITE→BLACK→WHITE… except when a promotion keeps the turn.
    var currentColor = CobColor.WHITE

    while (i < size) {
        val move = this[i]

        if (move.isPromotion() && i + 1 < size) {
            // Promotion + following move belong to the same player turn.
            val followUp = this[i + 1]
            groups.add(TurnGroup(color = currentColor, moves = listOf(move, followUp)))
            i += 2
        } else {
            groups.add(TurnGroup(color = currentColor, moves = listOf(move)))
            i += 1
        }

        // Only flip the color after a complete turn (i.e. not after the promotion alone).
        currentColor = currentColor.opponent
    }

    return groups
}

/**
 * Maps a flat move-list index (as stored in [StableHistoryList]) to its
 * corresponding [TurnGroup] row index, so undo/redo highlighting stays accurate
 * even when some rows contain two moves.
 *
 * Returns -1 if [moveIndex] is out of range.
 */
fun List<Move>.moveIndexToGroupIndex(moveIndex: Int): Int {
    if (moveIndex !in indices) return -1
    val groups = groupByTurns()
    var flat = 0
    groups.forEachIndexed { groupIdx, group ->
        repeat(group.moves.size) {
            if (flat == moveIndex) return groupIdx
            flat++
        }
    }
    return -1
}
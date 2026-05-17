package com.agustin.tarati.game.logic

import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C9
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.getBoardStateAtMove
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.Move
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests for [GameState.getBoardStateAtMove].
 *
 * Covers the standard path (default [initialGameState]) and the custom
 * [initialState] path introduced to support games started from an edited board,
 * where move history is relative to a non-standard starting position.
 *
 * ## Board topology reference
 *
 *             D3    D4
 *
 *             C7    C8
 *        C6              C9
 *                B4
 *     C5    B3        B5    C10
 *                A1
 *     C4    B2        B6    C11
 *                B1
 *        C3              C12
 *             C2    C1
 *
 *             D2    D1
 *
 * Moves in these tests are chosen to avoid captures:
 *   - WHITE C3→B1: forward move within white half; BLACK at C9 is not adjacent to B1.
 *   - BLACK C9→B4: forward move within black half; WHITE at B1 is not adjacent to B4.
 */
class GetBoardStateAtMoveTest {

    // ── Custom initial state (edited board) ───────────────────────────────────
    //
    // WHITE at C3 (white half, circumference), BLACK at C9 (black half, circumference).
    // These vertices are far enough apart that the chosen moves produce no captures.

    private val editedInitialState = GameState(
        cobs = mapOf(
            C3 to Cob(WHITE, false),
            C9 to Cob(BLACK, false),
        ),
        currentTurn = WHITE,
    )

    // ── Standard path ─────────────────────────────────────────────────────────

    @Test
    fun `default initialState applies all moves from standard opening`() {
        val moves = listOf(Move(C1 to B1))

        val result = getBoardStateAtMove(moves)

        assertEquals("Piece moved to B1", WHITE, result.cobs[B1]?.color)
        assertNull("Origin C1 empty", result.cobs[C1])
    }

    @Test
    fun `moveIndex null applies all moves in the list`() {
        val moves = listOf(Move(C1 to B1), Move(C7 to B4))

        val result = getBoardStateAtMove(moves, moveIndex = null)

        assertEquals("B1 has white", WHITE, result.cobs[B1]?.color)
        assertEquals("B4 has black", BLACK, result.cobs[B4]?.color)
    }

    @Test
    fun `moveIndex zero applies only the first move`() {
        val moves = listOf(Move(C1 to B1), Move(C7 to B4))

        val result = getBoardStateAtMove(moves, moveIndex = 0)

        assertEquals("B1 has white", WHITE, result.cobs[B1]?.color)
        assertEquals("C7 still has black — second move not applied", BLACK, result.cobs[C7]?.color)
        assertNull("B4 untouched", result.cobs[B4])
    }

    @Test
    fun `moveIndex minus one returns initialGameState without applying any move`() {
        val moves = listOf(Move(C1 to B1))

        // take(-1 + 1) = take(0) → no moves applied
        val result = getBoardStateAtMove(moves, moveIndex = -1)

        assertEquals("No moves applied — equals initialGameState", initialGameState(), result)
    }

    @Test
    fun `empty move history returns initialGameState`() {
        val result = getBoardStateAtMove(emptyList())

        assertEquals("Empty history returns initialGameState", initialGameState(), result)
    }

    // ── Custom initialState path ───────────────────────────────────────────────

    @Test
    fun `custom initialState is used as base instead of standard opening`() {
        val moves = listOf(Move(C3 to B1))

        val result = getBoardStateAtMove(
            moveHistory = moves,
            initialState = editedInitialState,
        )

        assertEquals("B1 has white — moved from C3", WHITE, result.cobs[B1]?.color)
        assertNull("C3 empty after move", result.cobs[C3])
        assertEquals("C9 still has black — not involved in move", BLACK, result.cobs[C9]?.color)
        // Standard opening pieces must not be present
        assertNull("D1 empty — custom start has no standard pieces", result.cobs[C1])
    }

    @Test
    fun `custom initialState with empty history returns the custom state unchanged`() {
        val result = getBoardStateAtMove(
            moveHistory = emptyList(),
            initialState = editedInitialState,
        )

        assertEquals("Empty history from custom start returns custom state", editedInitialState, result)
    }

    @Test
    fun `custom initialState respects moveIndex for partial replay`() {
        val moves = listOf(
            Move(C3 to B1),  // move 0: WHITE C3→B1 (forward, no captures)
            Move(C9 to B4),  // move 1: BLACK C9→B4 (forward, no captures)
        )

        val afterFirstMove = getBoardStateAtMove(
            moveHistory = moves,
            moveIndex = 0,
            initialState = editedInitialState,
        )
        val afterBothMoves = getBoardStateAtMove(
            moveHistory = moves,
            moveIndex = 1,
            initialState = editedInitialState,
        )

        // State after move 0: only WHITE moved
        assertEquals("B1 has white after move 0", WHITE, afterFirstMove.cobs[B1]?.color)
        assertEquals("C9 still has black after move 0", BLACK, afterFirstMove.cobs[C9]?.color)
        assertNull("C3 empty after move 0", afterFirstMove.cobs[C3])

        // State after move 1: both moved
        assertEquals("B1 still has white after move 1", WHITE, afterBothMoves.cobs[B1]?.color)
        assertEquals("B4 has black after move 1", BLACK, afterBothMoves.cobs[B4]?.color)
        assertNull("C3 empty after move 1", afterBothMoves.cobs[C3])
        assertNull("C9 empty after move 1", afterBothMoves.cobs[C9])
    }

    @Test
    fun `custom and default initialState produce different results for same moves`() {
        // C3→B1 is a valid forward move from the edited state (C3 has WHITE).
        // From the standard opening, C3 is empty — applyMove is a no-op.
        val moves = listOf(Move(C3 to B1))

        val fromStandard = getBoardStateAtMove(moves)
        val fromCustom = getBoardStateAtMove(moves, initialState = editedInitialState)

        // Standard: no piece at C3 in the standard opening, board unchanged
        assertNull(
            "Standard: C3 move is no-op — B1 unchanged by this move",
            fromStandard.cobs[C3]
        )
        assertEquals("Standard: 8 pieces from opening", 8, fromStandard.cobs.size)

        // Custom: C3 had WHITE, moved to B1
        assertEquals("Custom: B1 has white after C3→B1", WHITE, fromCustom.cobs[B1]?.color)
        assertNull("Custom: C3 empty after move", fromCustom.cobs[C3])
    }
}
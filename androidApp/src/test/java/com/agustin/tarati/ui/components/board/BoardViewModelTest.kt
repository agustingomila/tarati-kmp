package com.agustin.tarati.ui.components.board

import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.ui.components.game.behaviors.BoardSelectionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardViewModelTest {
    @Test
    fun initialState_hasNullSelectedVertex() {
        val viewModel = BoardSelectionViewModel()
        assertNull("Initial selected piece should be null", viewModel.selectedVertex.value)
    }

    @Test
    fun initialState_hasEmptyValidAdjacentVertexes() {
        val viewModel = BoardSelectionViewModel()
        assertTrue(
            "Initial highlighted moves should be empty",
            viewModel.validAdjacentVertexes.value.isEmpty(),
        )
    }

    @Test
    fun updateSelectedVertex_setsNewValue() {
        val viewModel = BoardSelectionViewModel()

        viewModel.updateSelectedVertex(C1)
        assertEquals("Selected piece should be C1", C1, viewModel.selectedVertex.value)

        viewModel.updateSelectedVertex(B2)
        assertEquals("Selected piece should be B2", B2, viewModel.selectedVertex.value)
    }

    @Test
    fun updateSelectedVertex_withNull_clearsSelection() {
        val viewModel = BoardSelectionViewModel()
        viewModel.updateSelectedVertex(C1)

        viewModel.updateSelectedVertex(null)
        assertNull(
            "Selected piece should be null after setting to null",
            viewModel.selectedVertex.value,
        )
    }

    @Test
    fun updateValidAdjacentVertexes_setsNewValidVertexes() {
        val viewModel = BoardSelectionViewModel()
        val moves = listOf(C2, B1, A1)

        viewModel.updateValidAdjacentVertexes(moves)
        assertEquals(
            "Highlighted moves should match input",
            moves,
            viewModel.validAdjacentVertexes.value,
        )
    }

    @Test
    fun updateValidAdjacentVertexes_withEmptyList_clearsValidVertexes() {
        val viewModel = BoardSelectionViewModel()
        viewModel.updateValidAdjacentVertexes(listOf(C2, B1))

        viewModel.updateValidAdjacentVertexes(emptyList())
        assertTrue(
            "Highlighted moves should be empty",
            viewModel.validAdjacentVertexes.value.isEmpty(),
        )
    }

    @Test
    fun resetSelection_clearsBothProperties() {
        val viewModel = BoardSelectionViewModel()
        viewModel.updateSelectedVertex(C1)
        viewModel.updateValidAdjacentVertexes(listOf(C2, B1))

        viewModel.resetSelection()

        assertNull(
            "Selected piece should be null after reset",
            viewModel.selectedVertex.value,
        )
        assertTrue(
            "Highlighted moves should be empty after reset",
            viewModel.validAdjacentVertexes.value.isEmpty(),
        )
    }

    @Test
    fun stateFlow_emitsUpdates() {
        val viewModel = BoardSelectionViewModel()
        val selectedPieceValues = mutableListOf<Vertex?>()
        val highlightedMovesValues = mutableListOf<List<Vertex>>()

        // Collect initial values
        selectedPieceValues.add(viewModel.selectedVertex.value)
        highlightedMovesValues.add(viewModel.validAdjacentVertexes.value)

        // Update and collect new values
        viewModel.updateSelectedVertex(C1)
        viewModel.updateValidAdjacentVertexes(listOf(C2))

        selectedPieceValues.add(viewModel.selectedVertex.value)
        highlightedMovesValues.add(viewModel.validAdjacentVertexes.value)

        // Verify state changes
        assertNull("First selected piece should be null", selectedPieceValues[0])
        assertEquals("Second selected piece should be C1", C1, selectedPieceValues[1])

        assertTrue(
            "First highlighted moves should be empty",
            highlightedMovesValues[0].isEmpty(),
        )
        assertEquals(
            "Second highlighted moves should have one item",
            listOf(C2),
            highlightedMovesValues[1],
        )
    }

    // ── Pre-move: initial state ──────────────────────────────────────────────

    @Test
    fun initialState_hasNullPreMoveFromVertex() {
        val viewModel = BoardSelectionViewModel()
        assertNull(
            "Initial preMoveFromVertex should be null",
            viewModel.preMoveFromVertex.value,
        )
    }

    @Test
    fun initialState_hasEmptyPreMoveValidTargets() {
        val viewModel = BoardSelectionViewModel()
        assertTrue(
            "Initial preMoveValidTargets should be empty",
            viewModel.preMoveValidTargets.value.isEmpty(),
        )
    }

    @Test
    fun initialState_hasNullPendingPreMove() {
        val viewModel = BoardSelectionViewModel()
        assertNull(
            "Initial pendingPreMove should be null",
            viewModel.pendingPreMove.value,
        )
    }

    // ── Pre-move: pre-selection phase ────────────────────────────────────────

    @Test
    fun updatePreMoveFrom_setsNewValue() {
        val viewModel = BoardSelectionViewModel()

        viewModel.updatePreMoveFrom(C1)
        assertEquals(C1, viewModel.preMoveFromVertex.value)

        viewModel.updatePreMoveFrom(B2)
        assertEquals(B2, viewModel.preMoveFromVertex.value)
    }

    @Test
    fun updatePreMoveFrom_withNull_clearsPreSelection() {
        val viewModel = BoardSelectionViewModel()
        viewModel.updatePreMoveFrom(C1)

        viewModel.updatePreMoveFrom(null)
        assertNull(viewModel.preMoveFromVertex.value)
    }

    @Test
    fun updatePreMoveValidTargets_setsList() {
        val viewModel = BoardSelectionViewModel()
        val targets = listOf(C2, B1, A1)

        viewModel.updatePreMoveValidTargets(targets)
        assertEquals(targets, viewModel.preMoveValidTargets.value)
    }

    // ── Pre-move: confirmed pre-move ─────────────────────────────────────────

    @Test
    fun setPendingPreMove_storesMove() {
        val viewModel = BoardSelectionViewModel()
        val move = Move(C1 to B1)

        viewModel.setPendingPreMove(move)
        assertEquals(move, viewModel.pendingPreMove.value)
    }

    @Test
    fun setPendingPreMove_withNull_clears() {
        val viewModel = BoardSelectionViewModel()
        viewModel.setPendingPreMove(Move(C1 to B1))

        viewModel.setPendingPreMove(null)
        assertNull(viewModel.pendingPreMove.value)
    }

    // ── Pre-move: reset ──────────────────────────────────────────────────────

    @Test
    fun resetPreMove_clearsAllThreeFields() {
        val viewModel = BoardSelectionViewModel()
        viewModel.updatePreMoveFrom(C1)
        viewModel.updatePreMoveValidTargets(listOf(C2, B1))
        viewModel.setPendingPreMove(Move(C1 to B1))

        viewModel.resetPreMove()

        assertNull(viewModel.preMoveFromVertex.value)
        assertTrue(viewModel.preMoveValidTargets.value.isEmpty())
        assertNull(viewModel.pendingPreMove.value)
    }

    // ── Pre-move: channel independence from normal selection ─────────────────

    @Test
    fun preMove_and_normalSelection_areIndependent() {
        val viewModel = BoardSelectionViewModel()

        // Setup normal selection
        viewModel.updateSelectedVertex(C1)
        viewModel.updateValidAdjacentVertexes(listOf(C2, B1))

        // Setup pre-move
        viewModel.updatePreMoveFrom(B2)
        viewModel.updatePreMoveValidTargets(listOf(A1))

        // Normal selection unchanged by pre-move updates
        assertEquals(C1, viewModel.selectedVertex.value)
        assertEquals(listOf(C2, B1), viewModel.validAdjacentVertexes.value)

        // Pre-move unchanged by normal selection updates
        assertEquals(B2, viewModel.preMoveFromVertex.value)
        assertEquals(listOf(A1), viewModel.preMoveValidTargets.value)

        // resetSelection touches only the normal channel
        viewModel.resetSelection()
        assertNull(viewModel.selectedVertex.value)
        assertTrue(viewModel.validAdjacentVertexes.value.isEmpty())
        assertEquals(B2, viewModel.preMoveFromVertex.value)
        assertEquals(listOf(A1), viewModel.preMoveValidTargets.value)

        // resetPreMove touches only the pre-move channel — restore normal first
        viewModel.updateSelectedVertex(C1)
        viewModel.updateValidAdjacentVertexes(listOf(C2, B1))
        viewModel.resetPreMove()
        assertEquals(C1, viewModel.selectedVertex.value)
        assertEquals(listOf(C2, B1), viewModel.validAdjacentVertexes.value)
        assertNull(viewModel.preMoveFromVertex.value)
    }
}

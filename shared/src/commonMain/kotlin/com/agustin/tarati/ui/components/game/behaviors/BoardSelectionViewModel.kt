package com.agustin.tarati.ui.components.game.behaviors

import androidx.lifecycle.ViewModel
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.Move
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BoardSelectionViewModel :
    ViewModel(),
    IBoardSelectionViewModel {

    // ── Selección normal ──────────────────────────────────────────────────────

    private val _selectedVertex = MutableStateFlow(null as Vertex?)
    override val selectedVertex: StateFlow<Vertex?> = _selectedVertex.asStateFlow()

    override fun updateSelectedVertex(vertex: Vertex?) {
        _selectedVertex.update { vertex }
    }

    private val _validAdjacentVertexes = MutableStateFlow(listOf<Vertex>())
    override val validAdjacentVertexes: StateFlow<List<Vertex>> = _validAdjacentVertexes.asStateFlow()

    override fun updateValidAdjacentVertexes(vertices: List<Vertex>) {
        _validAdjacentVertexes.update { vertices }
    }

    override fun resetSelection() {
        _selectedVertex.update { null }
        _validAdjacentVertexes.update { emptyList() }
    }

    // ── Pre-movimiento ────────────────────────────────────────────────────────

    private val _preMoveFromVertex = MutableStateFlow(null as Vertex?)
    override val preMoveFromVertex: StateFlow<Vertex?> = _preMoveFromVertex.asStateFlow()

    private val _preMoveValidTargets = MutableStateFlow(listOf<Vertex>())
    override val preMoveValidTargets: StateFlow<List<Vertex>> = _preMoveValidTargets.asStateFlow()

    private val _pendingPreMove = MutableStateFlow(null as Move?)
    override val pendingPreMove: StateFlow<Move?> = _pendingPreMove.asStateFlow()

    override fun updatePreMoveFrom(vertex: Vertex?) {
        _preMoveFromVertex.update { vertex }
    }

    override fun updatePreMoveValidTargets(vertices: List<Vertex>) {
        _preMoveValidTargets.update { vertices }
    }

    override fun setPendingPreMove(move: Move?) {
        _pendingPreMove.update { move }
    }

    override fun resetPreMove() {
        _preMoveFromVertex.update { null }
        _preMoveValidTargets.update { emptyList() }
        _pendingPreMove.update { null }
    }
}
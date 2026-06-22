package com.agustin.tarati.ui.components.game.behaviors

import androidx.compose.runtime.Stable
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.Move
import kotlinx.coroutines.flow.StateFlow

@Stable
interface IBoardSelectionViewModel {

    // ── Selección normal (turno del humano) ──────────────────────────────────

    val selectedVertex: StateFlow<Vertex?>
    val validAdjacentVertexes: StateFlow<List<Vertex>>

    fun resetSelection()
    fun updateSelectedVertex(vertex: Vertex?)
    fun updateValidAdjacentVertexes(vertices: List<Vertex>)

    // ── Pre-movimiento (durante el turno de la IA) ───────────────────────────
    //
    // Dos sub-fases: "pre-selección" (preMoveFromVertex != null, pendingPreMove == null)
    // y "confirmado" (pendingPreMove != null, preMoveFromVertex == null).
    // Canal paralelo a selectedVertex/validAdjacentVertexes para no interferir
    // con el flujo normal cuando ambos pueden estar activos a la vez.

    val preMoveFromVertex: StateFlow<Vertex?>
    val preMoveValidTargets: StateFlow<List<Vertex>>

    /** Pre-movimiento pendiente de ejecución. Se descarta si ya no es legal al volver el turno humano. */
    val pendingPreMove: StateFlow<Move?>

    fun updatePreMoveFrom(vertex: Vertex?)
    fun updatePreMoveValidTargets(vertices: List<Vertex>)
    fun setPendingPreMove(move: Move?)

    /** Limpia pre-selección y pre-move confirmado. */
    fun resetPreMove()
}
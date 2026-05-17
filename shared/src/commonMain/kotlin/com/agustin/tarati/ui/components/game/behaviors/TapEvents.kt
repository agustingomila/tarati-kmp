package com.agustin.tarati.ui.components.game.behaviors

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.Move

interface TapEvents {

    // ── Flujo normal (turno humano) ──────────────────────────────────────────

    fun onSelected(
        from: Vertex,
        valid: List<Vertex>,
    )

    fun onMove(move: Move)

    fun onInvalid(
        from: Vertex,
        valid: List<Vertex>,
    )

    fun onEditPieceRequested(from: Vertex)

    fun onCancel()

    // ── Flujo pre-move (mientras la IA piensa y preMovesEnabled=true) ────────

    /**
     * El usuario tocó una pieza humana durante el turno de la IA para iniciar
     * un pre-movimiento. [valid] se proyecta sobre el estado actual (antes del
     * movimiento de la IA) y se revalida al momento de ejecutar.
     */
    fun onPreMoveSelected(
        from: Vertex,
        valid: List<Vertex>,
    )

    /** El usuario confirmó el pre-movimiento tocando un target válido. */
    fun onPreMoveSet(move: Move)

    /** Cancela pre-selección y pre-movimiento pendiente. */
    fun onPreMoveCancel()
}
package com.agustin.tarati.ui.components.game.draw.board

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.ui.components.game.animation.AnimatedCob
import com.agustin.tarati.ui.components.game.animation.VisualGameState
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation

data class BoardRenderData(
    val gameState: GameState,
    val selectedVertex: Vertex?,
    val validAdjacentVertexes: List<Vertex>,
    val animatedPieces: Map<Vertex, AnimatedCob>,
    val currentHighlights: List<HighlightAnimation>,
    val visualState: VisualGameState,

    // Estado de pre-movimiento — canal paralelo a selectedVertex/validAdjacentVertexes,
    // activo durante el turno de la IA. pendingPreMove persiste hasta que se
    // ejecuta o descarta al volver el turno humano.
    val preMoveFromVertex: Vertex? = null,
    val preMoveValidTargets: List<Vertex> = emptyList(),
    val pendingPreMove: Move? = null,
)

fun createEmptyBoardRenderData(): BoardRenderData =
    BoardRenderData(
        gameState = GameState(mapOf(), WHITE),
        selectedVertex = null,
        validAdjacentVertexes = listOf(),
        animatedPieces = mapOf(),
        currentHighlights = listOf(),
        visualState = VisualGameState(),
    )
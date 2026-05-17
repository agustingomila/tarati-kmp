package com.agustin.tarati.features.library

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.board.buildPositionCache
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.components.game.BoardState
import com.agustin.tarati.ui.components.game.draw.board.drawAllPieces
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
import com.agustin.tarati.ui.components.game.draw.board.drawEdges
import com.agustin.tarati.ui.components.game.draw.board.drawVertices
import com.agustin.tarati.ui.theme.getBoardColors


/**
 * Renderiza un tablero estático (sin animaciones) en un único [Canvas].
 *
 * A diferencia del tablero de juego en vivo, que usa dos Canvas independientes
 * (uno estático para aristas/vértices y otro dinámico para piezas/highlights),
 * aquí toda la escena se dibuja en un solo pase. Esto elimina el desfase
 * de 1-2 frames entre el tablero y las piezas que ocurría al animar el
 * contenedor padre (expansión/colapso de paneles en Detalle de Partida).
 */
@Composable
fun StaticBoardRenderer(
    modifier: Modifier,
    gameState: GameState,
) {
    val boardState =
        BoardState(
            gameState = gameState,
            boardVisualState = BoardVisualState().copy(animateEffects = false),
            aiEnabled = false,
        )

    val boardColors = getBoardColors()
    val density = LocalDensity.current
    val vWidth = with(density) { 60.dp.toPx() }
    val orientation = boardState.boardOrientation

    Canvas(modifier = modifier) {
        val positionCache = buildPositionCache(size, orientation)

        drawBoardBackground(
            canvasSize = size,
            orientation = orientation,
            edgesVisible = boardState.boardVisualState.edgesVisibles,
            regionsVisible = boardState.boardVisualState.regionsVisibles,
            perimeterVisible = boardState.boardVisualState.perimeterVisible,
            colors = boardColors,
        )

        drawEdges(
            canvasSize = size,
            orientation = orientation,
            boardState = boardState,
            colors = boardColors,
        )

        drawVertices(
            canvasSize = size,
            vWidth = vWidth,
            selectedVertex = null,
            adjacentVertexes = emptyList(),
            boardState = boardState,
            colors = boardColors,
        )

        drawAllPieces(
            staticCobs = gameState.cobs,
            animatedPieces = emptyMap(),
            positionCache = positionCache,
            orientation = orientation,
            selectedPiece = null,
            colors = boardColors,
        )
    }
}
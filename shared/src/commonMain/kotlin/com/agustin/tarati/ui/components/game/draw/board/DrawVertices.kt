package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.ui.components.game.BoardState
import com.agustin.tarati.ui.components.game.highlights.HighlightAction
import com.agustin.tarati.ui.components.game.highlights.base.VertexHighlight
import com.agustin.tarati.ui.theme.BoardColors
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Clock

fun DrawScope.drawVertices(
    canvasSize: Size,
    vWidth: Float,
    selectedVertex: Vertex?,
    adjacentVertexes: List<Vertex>,
    boardState: BoardState,
    colors: BoardColors,
) {
    val gameState = boardState.gameState
    val orientation = boardState.boardOrientation
    val labelsVisible = boardState.boardVisualState.labelsVisibles
    val verticesVisible = boardState.boardVisualState.verticesVisibles
    val radius = minOf(canvasSize.width, canvasSize.height) * 0.015f

    if (verticesVisible) {
        vertices.forEach { vertex ->
            val pos = getVisualPosition(vertex, canvasSize, orientation)
            val cob = gameState.cobs[vertex]

            val vertexColor =
                when {
                    vertex == selectedVertex -> colors.vertexSelectedColor
                    adjacentVertexes.contains(vertex) -> colors.vertexAdjacentColor
                    cob != null -> colors.vertexOccupiedColor
                    else -> colors.boardVertexColor
                }

            drawCircle(color = vertexColor, center = pos, radius = radius)

            // Borde del vértice
            drawCircle(
                color = colors.textColor.copy(alpha = 0.3f),
                center = pos,
                radius = radius,
                style = Stroke(width = 1f),
            )

            if (labelsVisible) {
                // Etiqueta del vértice
                drawVertexLabel(
                    label = vertex.name,
                    position = pos,
                    textSize = vWidth / 4,
                    color = colors.textColor,
                )
            }
        }
    }
}

fun DrawScope.drawVertexHighlight(
    highlight: VertexHighlight,
    canvasSize: Size,
    orientation: BoardOrientation,
    colors: BoardColors,
) {
    val pos = getVisualPosition(highlight.vertex, canvasSize, orientation)
    val baseRadius = minOf(canvasSize.width, canvasSize.height) * 0.03f

    // Efecto de pulso si está activado
    val pulseFactor =
        if (highlight.pulse) {
            val pulseTime = Clock.System.now().toEpochMilliseconds() % 1000L / 1000f
            (0.7f + 0.3f * sin(pulseTime * 2 * PI).toFloat())
        } else {
            1f
        }

    val pulseRadius = baseRadius * pulseFactor

    when (highlight.action) {
        HighlightAction.CAPTURE -> {
            val ringRadius = pulseRadius * 3.0f
            val glowRadius = pulseRadius * 3.8f

            drawCircle(
                color = colors.highlightVertexCapture1Color.copy(alpha = 0.25f),
                center = pos,
                radius = glowRadius,
            )
            drawCircle(
                color = colors.highlightVertexCapture2Color.copy(alpha = 0.9f),
                center = pos,
                radius = ringRadius,
                style = Stroke(width = 4f),
            )
            drawCircle(
                color = colors.highlightVertexCapture3Color.copy(alpha = 0.6f),
                center = pos,
                radius = ringRadius * 0.85f,
                style = Stroke(width = 2f),
            )
        }

        HighlightAction.UPGRADE -> {
            drawCircle(
                color = colors.highlightVertexUpgrade1Color.copy(alpha = 0.3f),
                center = pos,
                radius = pulseRadius * 2f,
            )
            drawCircle(
                color = colors.highlightVertexUpgrade1Color,
                center = pos,
                radius = pulseRadius * 1.6f,
                style = Stroke(width = 4f),
            )
            drawCircle(
                color = colors.highlightVertexUpgrade2Color,
                center = pos,
                radius = pulseRadius * 0.6f,
            )
        }

        else -> {
            // Movimiento común, iluminar vértices adyacentes.
            drawCircle(
                color = colors.highlightVertexAdjacent1Color.copy(alpha = 0.6f),
                center = pos,
                radius = pulseRadius * 0.8f,
                style = Stroke(width = 3f),
            )
            drawCircle(
                color = colors.highlightVertexAdjacent2Color,
                center = pos,
                radius = pulseRadius * 0.4f,
            )
        }
    }

    // TODO: Si hay mensaje, dibujar texto (opcional)
    highlight.messageResId?.let {
        // drawContext.canvas.nativeCanvas.drawText(...)
    }
}

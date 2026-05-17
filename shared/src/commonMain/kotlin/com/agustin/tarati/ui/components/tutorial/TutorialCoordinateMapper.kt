package com.agustin.tarati.ui.components.tutorial

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.core.domain.tutorial.BasicMovesStep
import com.agustin.tarati.core.domain.tutorial.BridgeStep
import com.agustin.tarati.core.domain.tutorial.CapturesStep
import com.agustin.tarati.core.domain.tutorial.CenterStep
import com.agustin.tarati.core.domain.tutorial.CircumferenceStep
import com.agustin.tarati.core.domain.tutorial.CobsStep
import com.agustin.tarati.core.domain.tutorial.CompletedStep
import com.agustin.tarati.core.domain.tutorial.DomesticBasesStep
import com.agustin.tarati.core.domain.tutorial.IntroductionStep
import com.agustin.tarati.core.domain.tutorial.TutorialStep
import com.agustin.tarati.core.domain.tutorial.UpgradeStep
import kotlin.math.abs

class TutorialCoordinateMapper(
    private val boardSize: Size,
    private val orientation: BoardOrientation,
) {
    fun getBubblePositionForVertex(
        vertex: Vertex,
        size: IntSize = IntSize(320, 280),
    ): BubbleConfig {
        val vertexPosition =
            getVisualPosition(
                vertex = vertex,
                size = Size(boardSize.width, boardSize.height),
                orientation = orientation,
            )

        // Posición inteligente que usa CENTER cuando es apropiado
        val bubblePosition =
            calculateBubblePosition(
                vertexX = vertexPosition.x,
                vertexY = vertexPosition.y,
            )

        return BubbleConfig(
            position = bubblePosition,
            targetVertex = vertex,
            size = size,
        )
    }

    private fun calculateBubblePosition(
        vertexX: Float,
        vertexY: Float,
    ): BubblePosition {
        val screenCenterX = boardSize.width / 2
        val screenCenterY = boardSize.height / 2

        // Umbrales para considerar proximidad al centro de la pantalla
        val centerThresholdX = boardSize.width * 0.3f
        val centerThresholdY = boardSize.height * 0.3f

        val isNearCenterX = abs(vertexX - screenCenterX) < centerThresholdX
        val isNearCenterY = abs(vertexY - screenCenterY) < centerThresholdY

        // Si está cerca del centro en ambos ejes, usar posición central
        if (isNearCenterX && isNearCenterY) {
            return if (vertexY < screenCenterY) BubblePosition.BOTTOM_CENTER else BubblePosition.TOP_CENTER
        }

        // Si está cerca del centro horizontalmente pero no verticalmente
        if (isNearCenterX) {
            return if (vertexY < screenCenterY) BubblePosition.BOTTOM_CENTER else BubblePosition.TOP_CENTER
        }

        // Si está cerca del centro verticalmente pero no horizontalmente
        if (isNearCenterY) {
            return if (vertexX < screenCenterX) BubblePosition.CENTER_RIGHT else BubblePosition.CENTER_LEFT
        }

        // Para vértices lejos del centro, usar lógica de posición opuesta
        return when {
            // Vértice en parte superior izquierda -> Burbuja en inferior derecha
            vertexX < screenCenterX && vertexY < screenCenterY -> BubblePosition.BOTTOM_RIGHT

            // Vértice en parte superior derecha -> Burbuja en inferior izquierda
            vertexX >= screenCenterX && vertexY < screenCenterY -> BubblePosition.BOTTOM_LEFT

            // Vértice en parte inferior izquierda -> Burbuja en superior derecha
            vertexX < screenCenterX && vertexY >= screenCenterY -> BubblePosition.TOP_RIGHT

            // Vértice en parte inferior derecha -> Burbuja en superior izquierda
            else -> BubblePosition.TOP_LEFT
        }
    }
}

fun getDefaultBubbleConfigForStep(step: TutorialStep): BubbleConfig =
    when (step) {
        is IntroductionStep -> BubbleConfig(BubblePosition.TOP_CENTER)
        is CompletedStep -> BubbleConfig(BubblePosition.CENTER_CENTER)
        is CenterStep -> BubbleConfig(BubblePosition.BOTTOM_CENTER)
        is BridgeStep -> BubbleConfig(BubblePosition.BOTTOM_CENTER)
        is CircumferenceStep -> BubbleConfig(BubblePosition.BOTTOM_CENTER)
        is DomesticBasesStep -> BubbleConfig(BubblePosition.CENTER_CENTER)
        is CobsStep -> BubbleConfig(BubblePosition.TOP_CENTER)
        is BasicMovesStep -> BubbleConfig(BubblePosition.TOP_CENTER)
        is CapturesStep -> BubbleConfig(BubblePosition.TOP_CENTER)
        is UpgradeStep -> BubbleConfig(BubblePosition.BOTTOM_CENTER)
        else -> BubbleConfig(BubblePosition.TOP_CENTER)
    }

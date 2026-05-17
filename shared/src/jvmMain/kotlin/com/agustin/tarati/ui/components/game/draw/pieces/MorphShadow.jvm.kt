package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Implementación Desktop de morphShadow sin blur (fallback simplificado).
 *
 * Dibuja la sombra como una copia del path desplazada y con color semitransparente.
 * No aplica blur gaussiano ya que no está disponible BlurMaskFilter en JVM.
 *
 * El resultado es visualmente aceptable aunque menos refinado que la versión Android.
 * Para una sombra con blur en Desktop sería necesario implementar un algoritmo
 * personalizado o usar una librería de procesamiento de imagen.
 */
actual fun Modifier.morphShadow(
    shape: MorphShape,
    elevation: Dp,
    color: Color,
    offsetX: Dp,
    offsetY: Dp,
): Modifier = drawBehind {
    val path = shape.createPath(size)

    translate(left = offsetX.toPx(), top = offsetY.toPx()) {
        // Dibuja múltiples capas con alpha decreciente para simular blur suave
        val layers = 3
        val baseAlpha = color.alpha / layers

        repeat(layers) { layer ->
            val scale = 1f + (layer * 0.02f) // Ligera expansión por capa
            val layerAlpha = baseAlpha * (1f - layer * 0.25f)

            drawPath(
                path = path,
                color = color.copy(alpha = layerAlpha),
            )
        }
    }
}
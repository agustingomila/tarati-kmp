package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate

/**
 * Implementación Desktop de drawMorphFlipShadow simplificada.
 *
 * No usa Matrix transformations ni BlurMaskFilter. En su lugar:
 * - Dibuja el path sin transformar (o con offset simple)
 * - Usa múltiples capas con alpha decreciente para simular blur
 * - Calidad inferior a Android pero suficiente para gameplay
 *
 * ## Trade-offs
 * - No hay compresión direccional de la sombra
 * - Blur simulado es menos suave
 * - Suficiente para identificar la pieza y jugar
 */
actual fun DrawScope.drawMorphFlipShadow(params: MorphFlipShadowParams) {
    // Sombra simplificada: múltiples capas con offset
    translate(left = params.position.x - params.radius, top = params.position.y - params.radius) {
        // Simulamos blur con 3 capas de diferente alpha
        val layers = 3
        val baseAlpha = params.umbraAlpha / layers

        repeat(layers) { layer ->
            val layerOffset = layer * 1f  // Offset incremental para simular blur
            val layerAlpha = baseAlpha * (1f - layer * 0.2f)

            translate(left = layerOffset, top = layerOffset) {
                drawPath(
                    path = params.shadowPath,
                    color = params.shadowColor.copy(alpha = layerAlpha),
                )
            }
        }

        // Penumbra simplificada (si está habilitada)
        if (params.showPenumbra) {
            translate(left = 3f, top = 3f) {
                drawPath(
                    path = params.shadowPath,
                    color = params.shadowColor.copy(alpha = params.penumbraAlpha * 0.5f),
                )
            }
        }
    }
}
package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Implementación Desktop de [drawBorderPattern].
 *
 * Solo dibuja el borde sólido — sin guardas decorativas por ahora.
 * Las guardas (Meander, Chevron, etc.) requieren PathMeasure de Android.
 * En el futuro se puede implementar con alternativas KMP como la librería
 * de geometría de Compose o una implementación propia de PathMeasure.
 */
actual fun DrawScope.drawBorderPattern(
    projectedFacePath: Path,
    pattern: BorderPattern,
    borderWidth: Float,
    borderColor: Color,
    accentColor: Color,
    projectionScale: Float,
    projectionAxisAngleDeg: Float,
    flatFacePath: Path,
    projectionShift: Offset,
    shape: MorphShape?,
) {
    // Borde sólido para todos los patrones en Desktop
    clipPath(flatFacePath) {
        drawPath(
            path = flatFacePath,
            color = borderColor,
            style = Stroke(width = borderWidth * 2f),
        )
    }

    // DoubleRing: agregar segundo anillo en accentColor
    if (pattern == BorderPattern.DoubleRing) {
        clipPath(flatFacePath) {
            drawPath(
                path = flatFacePath,
                color = accentColor,
                style = Stroke(width = borderWidth * 0.5f),
            )
        }
    }
}
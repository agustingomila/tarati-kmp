package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Dibuja la [BorderPattern] de una pieza poligonal.
 *
 * ## expect/actual
 * La implementación Android usa android.graphics.PathMeasure y asAndroidPath()
 * para muestrear puntos a lo largo del perímetro. La implementación Desktop
 * usa solo el borde sólido (sin guardas decorativas por ahora).
 */
expect fun DrawScope.drawBorderPattern(
    projectedFacePath: Path,
    pattern: BorderPattern,
    borderWidth: Float,
    borderColor: Color,
    accentColor: Color,
    projectionScale: Float = 1f,
    projectionAxisAngleDeg: Float = 90f,
    flatFacePath: Path = projectedFacePath,
    projectionShift: Offset = Offset.Zero,
    shape: MorphShape? = null,
)
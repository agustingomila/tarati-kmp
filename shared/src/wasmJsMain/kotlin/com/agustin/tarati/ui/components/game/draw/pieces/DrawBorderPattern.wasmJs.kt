package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

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
    clipPath(flatFacePath) {
        drawPath(path = flatFacePath, color = borderColor, style = Stroke(width = borderWidth * 2f))
    }
    if (pattern == BorderPattern.DoubleRing) {
        clipPath(flatFacePath) {
            drawPath(path = flatFacePath, color = accentColor, style = Stroke(width = borderWidth * 0.5f))
        }
    }
}

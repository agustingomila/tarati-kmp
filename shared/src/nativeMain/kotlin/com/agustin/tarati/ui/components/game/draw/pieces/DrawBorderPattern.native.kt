package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    shape: MorphShape?
) {
}
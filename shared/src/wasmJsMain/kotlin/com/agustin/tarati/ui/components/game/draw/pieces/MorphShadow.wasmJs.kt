package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

actual fun Modifier.morphShadow(
    shape: MorphShape,
    elevation: Dp,
    color: Color,
    offsetX: Dp,
    offsetY: Dp,
): Modifier = drawBehind {
    val path = shape.createPath(size)
    translate(left = offsetX.toPx(), top = offsetY.toPx()) {
        val layers = 3
        val baseAlpha = color.alpha / layers
        repeat(layers) { layer ->
            val layerAlpha = baseAlpha * (1f - layer * 0.25f)
            drawPath(path = path, color = color.copy(alpha = layerAlpha))
        }
    }
}

package com.agustin.tarati.ui.components.game.draw.board

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

actual fun DrawScope.drawVertexLabel(
    label: String,
    position: Offset,
    textSize: Float,
    color: Color
) {
    drawContext.canvas.nativeCanvas.drawText(
        label,
        position.x - textSize * 0.8f,
        position.y - textSize * 0.8f,
        Paint().apply {
            this.color = color.toArgb()
            this.textSize = textSize
            isAntiAlias = true
        },
    )
}
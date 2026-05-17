package com.agustin.tarati.ui.components.game.draw.pieces

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Implementación Android de morphShadow con BlurMaskFilter de alta calidad.
 *
 * Utiliza el canvas nativo de Android para aplicar blur gaussiano a la sombra,
 * siguiendo el contorno exacto del polígono. La calidad del blur es superior
 * a la implementación de Compose puro.
 */
actual fun Modifier.morphShadow(
    shape: MorphShape,
    elevation: Dp,
    color: Color,
    offsetX: Dp,
    offsetY: Dp,
): Modifier = drawBehind {
    val blurRadius = elevation.toPx()
    val path = shape.createPath(size).asAndroidPath()
        .also { it.offset(offsetX.toPx(), offsetY.toPx()) }

    drawContext.canvas.nativeCanvas.drawPath(path, Paint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        maskFilter = BlurMaskFilter(blurRadius * 1.5f, BlurMaskFilter.Blur.NORMAL)
    })
}
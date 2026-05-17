package com.agustin.tarati.ui.components.game.draw.pieces

import android.graphics.BlurMaskFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

/**
 * Implementación Android de drawMorphFlipShadow con calidad premium.
 *
 * Usa Matrix para transformación afín direccional y BlurMaskFilter para
 * umbra + penumbra con blur gaussiano de alta calidad.
 *
 * ## Capas de renderizado
 * 1. **Umbra** (sombra principal): blur pequeño, alpha base
 * 2. **Penumbra** (halo exterior): blur grande, alpha menor (solo si sinA > 0.05)
 *
 * La transformación Matrix comprime la sombra en dirección perpendicular a la luz
 * conforme la pieza se acerca al canto, simulando la geometría 3D real.
 */
actual fun DrawScope.drawMorphFlipShadow(params: MorphFlipShadowParams) {
    // Crear Matrix de transformación Android
    val shadowMatrix = Matrix()
    shadowMatrix.setValues(params.transformMatrix)

    // Transformar el path
    val shadowAndroid = android.graphics.Path(params.shadowPath.asAndroidPath())
        .also { it.transform(shadowMatrix) }

    translate(left = params.position.x - params.radius, top = params.position.y - params.radius) {
        // Capa 1: Umbra (sombra principal)
        drawContext.canvas.nativeCanvas.drawPath(shadowAndroid, Paint().apply {
            isAntiAlias = true
            color = params.shadowColor.copy(alpha = params.umbraAlpha).toArgb()
            maskFilter = BlurMaskFilter(params.umbraBlur, BlurMaskFilter.Blur.NORMAL)
        })

        // Capa 2: Penumbra (halo exterior, solo visible cerca del canto)
        if (params.showPenumbra) {
            drawContext.canvas.nativeCanvas.drawPath(shadowAndroid, Paint().apply {
                isAntiAlias = true
                color = params.shadowColor.copy(alpha = params.penumbraAlpha).toArgb()
                maskFilter = BlurMaskFilter(params.penumbraBlur, BlurMaskFilter.Blur.NORMAL)
            })
        }
    }
}
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.agustin.tarati.ui.components.game.draw.common

import android.graphics.BitmapShader
import android.graphics.Shader
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.core.graphics.createBitmap

actual object NoiseTexture {
    private const val SIZE = 128
    private const val SEED = 0x93D765DDL
    private val brush: ShaderBrush by lazy { buildBrush() }

    actual fun DrawScope.applyNoise(path: Path, alpha: Float) {
        drawPath(path, brush = brush, alpha = alpha, blendMode = BlendMode.Overlay)
    }

    actual fun DrawScope.applyNoise(center: Offset, radius: Float, alpha: Float) {
        drawCircle(brush = brush, center = center, radius = radius, alpha = alpha, blendMode = BlendMode.Overlay)
    }

    actual fun DrawScope.applyNoise(topLeft: Offset, size: Size, cornerRadius: CornerRadius, alpha: Float) {
        drawRoundRect(
            brush = brush,
            topLeft = topLeft,
            size = size,
            cornerRadius = cornerRadius,
            alpha = alpha,
            blendMode = BlendMode.Overlay
        )
    }

    private fun buildBrush(): ShaderBrush {
        val pixels = IntArray(SIZE * SIZE)
        var state = SEED
        for (i in pixels.indices) {
            state = state xor (state shl 13)
            state = state ushr 7
            state = state xor (state shl 17)
            val v = (state and 0xFFL).toInt()
            pixels[i] = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
        }
        val bitmap = createBitmap(SIZE, SIZE)
            .also { it.setPixels(pixels, 0, SIZE, 0, 0, SIZE, SIZE) }
        return ShaderBrush(BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT))
    }
}
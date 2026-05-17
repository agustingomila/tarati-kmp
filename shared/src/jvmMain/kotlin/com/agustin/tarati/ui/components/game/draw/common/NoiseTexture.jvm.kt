@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

// No-op en Desktop — el efecto de ruido es al 7% de opacidad,
// visualmente imperceptible su ausencia.
actual object NoiseTexture {
    actual fun DrawScope.applyNoise(path: Path, alpha: Float) = Unit
    actual fun DrawScope.applyNoise(center: Offset, radius: Float, alpha: Float) = Unit
    actual fun DrawScope.applyNoise(topLeft: Offset, size: Size, cornerRadius: CornerRadius, alpha: Float) = Unit
}
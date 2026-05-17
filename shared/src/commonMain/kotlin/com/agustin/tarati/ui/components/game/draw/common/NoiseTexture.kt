@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

expect object NoiseTexture {
    fun DrawScope.applyNoise(path: Path, alpha: Float = 0.10f)
    fun DrawScope.applyNoise(center: Offset, radius: Float, alpha: Float = 0.10f)
    fun DrawScope.applyNoise(topLeft: Offset, size: Size, cornerRadius: CornerRadius, alpha: Float = 0.10f)
}
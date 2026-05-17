package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay

actual fun DrawScope.drawFlipShadow(
    position: Offset,
    radius: Float,
    sinA: Float,
    lightOfDay: LightOfDay,
    cosFlip: Float,
    sinFlip: Float,
    shadowColor: Color
) {
}
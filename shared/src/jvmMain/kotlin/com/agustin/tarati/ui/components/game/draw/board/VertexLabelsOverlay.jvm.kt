package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.agustin.tarati.core.domain.game.board.BoardOrientation

@Composable
actual fun VertexLabelsOverlay(
    occupiedVertex: VertexListWrapper,
    labelsVisible: Boolean,
    boardOrientation: BoardOrientation,
    containerSize: Size,
    textSize: Float,
    textColor: Color,
) = Unit

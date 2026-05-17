package com.agustin.tarati.ui.components.tutorial

import androidx.compose.ui.unit.IntSize
import com.agustin.tarati.core.domain.game.board.Vertex

data class BubbleConfig(
    val position: BubblePosition,
    val targetVertex: Vertex? = null,
    val size: IntSize = IntSize(320, 280),
)
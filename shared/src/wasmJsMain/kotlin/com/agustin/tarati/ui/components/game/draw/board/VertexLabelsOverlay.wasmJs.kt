package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.getVisualPosition

@Composable
actual fun VertexLabelsOverlay(
    occupiedVertex: VertexListWrapper,
    labelsVisible: Boolean,
    boardOrientation: BoardOrientation,
    containerSize: Size,
    textSize: Float,
    textColor: Color,
) {
    if (!labelsVisible || containerSize == Size.Zero) return
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        vertices.filterNot { it in occupiedVertex.items }.forEach { vertex ->
            val pos = getVisualPosition(vertex, containerSize, boardOrientation)
            val offsetX = with(density) { (pos.x - textSize * 1.8f).toDp() }
            val offsetY = with(density) { (pos.y - textSize * 1.8f).toDp() }
            val fontSize = with(density) { textSize.toSp() }

            Text(
                text = vertex.name,
                color = textColor,
                fontSize = fontSize,
                lineHeight = fontSize,
                modifier = Modifier.absoluteOffset(x = offsetX, y = offsetY),
            )
        }
    }
}

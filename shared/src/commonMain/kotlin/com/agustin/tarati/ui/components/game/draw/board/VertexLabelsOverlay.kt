package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.Vertex

/**
 * Overlay composable para las etiquetas de vértice del tablero.
 *
 * En Android, JVM y native las etiquetas se dibujan directamente en el Canvas
 * mediante [drawVertexLabel], por lo que este composable es un no-op.
 * En wasmJs, donde el renderizado de texto dentro de DrawScope no funciona,
 * este composable posiciona composables [Text] sobre el tablero.
 */
@Composable
expect fun VertexLabelsOverlay(
    occupiedVertex: VertexListWrapper,
    labelsVisible: Boolean,
    boardOrientation: BoardOrientation,
    containerSize: Size,
    textSize: Float,
    textColor: Color,
)

@Immutable
data class VertexListWrapper(
    val items: List<Vertex>
)

package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// Las etiquetas en WASM se renderizan con VertexLabelsOverlay (composable Text),
// ya que los mecanismos de texto dentro de DrawScope no funcionan en esta plataforma.
actual fun DrawScope.drawVertexLabel(
    label: String,
    position: Offset,
    textSize: Float,
    color: Color,
) = Unit

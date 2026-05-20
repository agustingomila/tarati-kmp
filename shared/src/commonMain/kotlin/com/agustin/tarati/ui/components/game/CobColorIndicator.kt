package com.agustin.tarati.ui.components.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.board.drawIndicatorPiece
import com.agustin.tarati.ui.theme.getBoardColors

/**
 * Indicador circular de color de banda, tematizado con la paleta actual.
 *
 * Usa el mismo renderer que el sidebar y los indicadores de esquina del tablero.
 *
 * @param color  Banda a representar (blancas o negras).
 * @param size   Tamaño del canvas cuadrado. Default: 18.dp.
 */
@Composable
fun CobColorIndicator(
    color: CobColor,
    size: Dp = 18.dp,
) {
    val boardColors = getBoardColors()
    Canvas(modifier = Modifier.size(size)) {
        val r = this.size.minDimension / 2f
        drawIndicatorPiece(
            position = Offset(r, r),
            radius = r,
            cobColor = color,
            colors = boardColors,
        )
    }
}

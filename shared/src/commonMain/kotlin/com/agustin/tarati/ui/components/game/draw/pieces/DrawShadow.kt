package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay
import com.agustin.tarati.ui.theme.BoardColors

/**
 * Dibuja una sombra simple desplazada según [LightOfDay].
 *
 * Versión KMP que reemplaza la transformación Matrix por translate() de DrawScope,
 * que es portable a todas las plataformas.
 *
 * @param morphPath Path de la pieza a sombrear
 * @param lightOfDay Configuración de iluminación (offset y intensidad)
 * @param boardColors Colores del tablero (para color de sombra)
 */
fun DrawScope.drawShadow(
    morphPath: Path,
    lightOfDay: LightOfDay,
    boardColors: BoardColors
) {
    translate(left = lightOfDay.shadowOffsetX, top = lightOfDay.shadowOffsetY) {
        drawPath(
            path = morphPath,
            color = boardColors.boardVertexColor.copy(
                alpha = 0.28f * lightOfDay.shadowIntensity
            )
        )
    }
}

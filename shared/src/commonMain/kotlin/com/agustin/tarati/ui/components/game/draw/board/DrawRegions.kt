package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.ui.components.game.highlights.base.RegionHighlight
import com.agustin.tarati.ui.theme.BoardColors
import kotlin.math.pow
import kotlin.time.Clock

fun DrawScope.drawRegionHighlight(
    highlight: RegionHighlight,
    canvasSize: Size,
    orientation: BoardOrientation,
    colors: BoardColors,
) {
    val region = highlight.region

    // Crear path para la región
    val path = createBoundaryPath(canvasSize, orientation, region)

    // Efecto de pulso para el destello
    val pulseFactor =
        if (highlight.pulse) {
            val pulseTime = Clock.System.now().toEpochMilliseconds() % 300L / 300f
            // Usar función cuadrática para un destello más rápido
            if (pulseTime < 0.5f) {
                (0.3f + 0.7f * (pulseTime * 2).pow(2))
            } else {
                (0.3f + 0.7f * (1 - (pulseTime - 0.5f) * 2).pow(2))
            }
        } else {
            1f
        }

    // Dibujar fondo de la región con efecto de pulso
    drawPath(
        path = path,
        color = colors.highlightRegion1Color,
        style = Fill,
        alpha = 0.6f * pulseFactor,
    )

    // Dibujar borde resaltado con efecto de pulso
    drawPath(
        path = path,
        color = colors.highlightRegion2Color,
        style = Stroke(width = 5f * pulseFactor),
        alpha = 0.8f * pulseFactor,
    )
}

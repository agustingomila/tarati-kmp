package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay
import com.agustin.tarati.ui.components.game.draw.board.getLightOfDay
import com.agustin.tarati.ui.components.game.draw.common.NoiseTexture
import com.agustin.tarati.ui.theme.BoardColors

// ─────────────────────────────────────────────────────────────────────────────
// drawMorphCob — pieza poligonal estática
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Dibuja una pieza poligonal estática con sombra según [LightOfDay].
 *
 * @param borderInsetWidth Anchura visible del borde inset. Default `radius × 0.22`.
 * @param centerRadiusFrac Escala de la forma central respecto al polígono. Default 0.20.
 */
fun DrawScope.drawMorphCob(
    position: Offset,
    radius: Float,
    cobShape: CobShape,
    cobColor: CobColor,
    boardColors: BoardColors,
    hourOfDay: Float = 12f,
    borderInsetWidth: Float = radius * 0.22f,
    centerRadiusFrac: Float = 0.20f,
) {
    val shapeColors = cobShape.colorScheme.resolve(cobColor, boardColors)
    val lightOfDay = getLightOfDay(hourOfDay, radius)
    val pathSize = Size(radius * 2f, radius * 2f)
    val morphPath = cobShape.shape.createPath(pathSize)

    // Para polígonos con vértice asimétrico (triángulo) el centroide no coincide
    // con el bounding box center — lo usamos como pivot del motivo central.
    val centroid = cobShape.shape.computeCentroid(
        cx = radius, cy = radius,
        rx = radius * cobShape.shape.sizeFrac,
        ry = radius * cobShape.shape.sizeFrac,
    )

    val organicFill: Color = if (shapeColors.lightColor != null && shapeColors.shadowColor != null) {
        createOrganicColor(
            pieceColor = PieceColor(
                baseColor = shapeColors.fill,
                borderColor = shapeColors.border,
                lightColor = shapeColors.lightColor,
                shadowColor = shapeColors.shadowColor,
            ),
            hourOfDay = hourOfDay,
            colors = boardColors,
        )
    } else {
        shapeColors.fill
    }

    translate(left = position.x - radius, top = position.y - radius) {
        drawShadow(
            morphPath = morphPath,
            lightOfDay = lightOfDay,
            boardColors = boardColors
        )

        drawPath(morphPath, color = organicFill)

        drawBorderPattern(
            projectedFacePath = morphPath,
            pattern = cobShape.borderPattern,
            borderWidth = borderInsetWidth,
            borderColor = shapeColors.border,
            accentColor = organicFill,
            shape = cobShape.shape,
        )

        drawCenterMotif(
            motif = cobShape.centerMotif,
            facePath = morphPath,
            centroid = centroid,
            radius = radius,
            centerRadiusFrac = centerRadiusFrac,
            fillColor = shapeColors.center,
            accentColor = organicFill,
            rotationDeg = cobShape.shape.rotationDeg,
        )

        with(NoiseTexture) { applyNoise(morphPath) }
    }
}
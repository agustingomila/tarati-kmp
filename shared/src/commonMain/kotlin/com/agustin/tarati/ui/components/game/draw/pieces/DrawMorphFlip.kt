package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.ui.components.game.draw.board.getLightOfDay
import com.agustin.tarati.ui.components.game.draw.common.NoiseTexture
import com.agustin.tarati.ui.theme.BoardColors
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// drawMorphFlip — pieza poligonal en animación de volteo
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Dibuja un frame del volteo de una pieza poligonal.
 *
 * La sombra se dibuja con escala direccional: mantiene su tamaño en la
 * dirección de la luz y se comprime en la perpendicular conforme la pieza se
 * acerca al canto (sinA → 1). Usa umbra + penumbra con blur creciente.
 *
 * @param hourOfDay         Hora del día (0–24) para [LightOfDay]. Default 12.
 * @param borderInsetWidth  Anchura visible del borde inset. Default `radius × 0.22`.
 * @param centerRadiusFrac  Escala de la forma central. Default 0.20.
 */
fun DrawScope.drawMorphFlip(
    position: Offset,
    radius: Float,
    projection: MorphShapeProjection,
    flipProgress: Float,
    rimFrac: Float = 0.22f,
    cobShape: CobShape,
    cobColor: CobColor,
    boardColors: BoardColors,
    hourOfDay: Float = 12f,
    borderInsetWidth: Float = radius * 0.22f,
    centerRadiusFrac: Float = 0.20f,
) {
    val angle = flipProgress * PI.toFloat()
    val scale = cos(angle)
    // 0 cuando la pieza está plana (|scale|=1), 1 cuando está de canto (scale=0).
    val sinA = sqrt(max(0f, 1f - scale * scale))
    val isFront = projection.isFrontFace(scale)

    val lightOfDay = getLightOfDay(hourOfDay, radius)
    val frontColors = cobShape.colorScheme.resolve(cobColor, boardColors)
    val backColors = cobShape.colorScheme.resolve(cobColor.opponent, boardColors)
    val visibleColors = if (isFront) frontColors else backColors

    val organicFill: Color = if (visibleColors.lightColor != null && visibleColors.shadowColor != null) {
        createOrganicColor(
            pieceColor = PieceColor(
                baseColor = visibleColors.fill,
                borderColor = visibleColors.border,
                lightColor = visibleColors.lightColor,
                shadowColor = visibleColors.shadowColor,
            ),
            hourOfDay = hourOfDay,
            colors = boardColors,
        )
    } else {
        visibleColors.fill
    }

    // Canto: blend de bordes frontal+trasero.
    val edgeColor = blendColors(frontColors.border, backColors.border, 0.5f).copy(alpha = 0.90f)

    val pathSize = Size(radius * 2f, radius * 2f)
    val paths = projection.flipPaths(pathSize, scale, rimFrac)
    val centroid = projection.centroidInPathSpace(pathSize)

    // ── Sombra compleja ────────────────────────────────────────────────────────
    // Calculamos parámetros en Kotlin puro, renderizado es expect/actual
    val shadowParams = computeMorphFlipShadowParams(
        position = position,
        radius = radius,
        projection = projection,
        flipProgress = flipProgress,
        lightOfDay = lightOfDay,
        shadowColor = boardColors.boardVertexColor,
        rimFrac = rimFrac,
    )
    drawMorphFlipShadow(shadowParams)

    // ── Pieza: canto + fill + borde + textura + motivo central ──────────────
    val tx = position.x - radius + paths.shift.x
    val ty = position.y - radius + paths.shift.y

    translate(left = tx, top = ty) {
        // Dibujar edge con rotación si es necesario
        // Aplicamos la rotación usando DrawScope.rotate() en lugar de pre-rotar el Path
        if (paths.edge != null) {
            if (abs(paths.edgeRotationDeg) > 0.1f) {
                rotate(
                    degrees = paths.edgeRotationDeg,
                    pivot = Offset(radius, radius)  // Centro del path (size = radius*2)
                ) {
                    drawPath(paths.edge, color = edgeColor)
                }
            } else {
                drawPath(paths.edge, color = edgeColor)
            }
        }

        // Dibujar face con proyección 3D si es necesario
        // Aplicamos la matriz de transformación exacta (equivalente a faceMatrix)
        if (abs(paths.faceScale - 1f) > 0.01f) {
            // Calcular componentes de la matriz de transformación afín
            // Esta es la matriz exacta que usa Android en path.transform(faceMatrix)
            val axR = paths.faceAxisAngleDeg * PI.toFloat() / 180f
            val cosAx = cos(axR)
            val sinAx = sin(axR)
            val fmA = cosAx * cosAx + paths.faceScale * sinAx * sinAx
            val fmB = (1f - paths.faceScale) * sinAx * cosAx
            val fmD = sinAx * sinAx + paths.faceScale * cosAx * cosAx

            // Centro del path (cx, cy) en coordenadas del path (no de pantalla)
            val cx = radius
            val cy = radius

            // Componentes de traslación que mantienen (cx, cy) fijo
            val tx = cx * (1f - fmA) - cy * fmB
            val ty = cy * (1f - fmD) - cx * fmB

            // Aplicar transformación usando Matrix de Compose (column-major)
            // Transformación 2D: [ fmA  fmB  tx ]
            //                    [ fmB  fmD  ty ]
            //                    [ 0    0    1  ]
            withTransform({
                transform(
                    Matrix(
                        floatArrayOf(
                            fmA, fmB, 0f, 0f,   // Columna 0
                            fmB, fmD, 0f, 0f,   // Columna 1
                            0f, 0f, 1f, 0f,   // Columna 2
                            tx, ty, 0f, 1f    // Columna 3 (traslación)
                        )
                    )
                )
            }) {
                drawPath(paths.face, color = organicFill)

                drawBorderPattern(
                    projectedFacePath = paths.face,
                    pattern = cobShape.borderPattern,
                    borderWidth = borderInsetWidth,
                    borderColor = visibleColors.border,
                    accentColor = organicFill,
                    flatFacePath = projection.createPath(pathSize, scale = 1f),
                    shape = cobShape.shape,
                )

                drawCenterMotif(
                    motif = cobShape.centerMotif,
                    facePath = paths.face,
                    centroid = centroid,
                    radius = radius,
                    centerRadiusFrac = centerRadiusFrac,
                    fillColor = visibleColors.center,
                    accentColor = organicFill,
                    absScale = abs(scale),
                    rotationDeg = cobShape.shape.rotationDeg,
                )
            }
        } else {
            // Sin proyección (scale == 1.0), dibujar directamente
            drawPath(paths.face, color = organicFill)

            drawBorderPattern(
                projectedFacePath = paths.face,
                pattern = cobShape.borderPattern,
                borderWidth = borderInsetWidth,
                borderColor = visibleColors.border,
                accentColor = organicFill,
                projectionScale = scale,
                projectionAxisAngleDeg = projection.axisAngleDeg,
                flatFacePath = projection.createPath(pathSize, scale = 1f),
                shape = cobShape.shape,
            )

            drawCenterMotif(
                motif = cobShape.centerMotif,
                facePath = paths.face,
                centroid = centroid,
                radius = radius,
                centerRadiusFrac = centerRadiusFrac,
                fillColor = visibleColors.center,
                accentColor = organicFill,
                absScale = abs(scale),
                rotationDeg = cobShape.shape.rotationDeg,
                projectionScale = scale,  // signo preservado para espejo de cara trasera
                projectionAxisAngleDeg = projection.axisAngleDeg,
            )
        }

        with(NoiseTexture) { applyNoise(paths.face) }
    }
}

/**
 * Mezcla dos colores por interpolación lineal.
 */
fun blendColors(a: Color, b: Color, t: Float): Color {
    val mt = 1f - t
    return Color(
        red = a.red * mt + b.red * t,
        green = a.green * mt + b.green * t,
        blue = a.blue * mt + b.blue * t,
        alpha = a.alpha * mt + b.alpha * t,
    )
}
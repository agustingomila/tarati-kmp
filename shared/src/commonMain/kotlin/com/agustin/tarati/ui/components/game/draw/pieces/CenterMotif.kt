package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// CenterMotif — catálogo de centros
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Motivo central para piezas mejoradas (Roks).
 *
 * Migración KMP: la implementación anterior de [drawDefaultCenter] usaba
 * android.graphics.Matrix + nativeCanvas. Ahora usa withTransform de Compose,
 * que es equivalente y 100% multiplataforma.
 */
sealed class CenterMotif {
    object None : CenterMotif()
    object Default : CenterMotif()
    object Cross : CenterMotif()
    object Trefoil : CenterMotif()
    object Ring : CenterMotif()
    object Compass : CenterMotif()
    object DiamondCross : CenterMotif()
    object Star5 : CenterMotif()
}

// ─────────────────────────────────────────────────────────────────────────────
// drawCenterMotif — dispatcher
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawCenterMotif(
    motif: CenterMotif,
    facePath: Path,
    centroid: Offset,
    radius: Float,
    centerRadiusFrac: Float = 0.20f,
    fillColor: Color?,
    accentColor: Color,
    absScale: Float = 1f,
    rotationDeg: Float = 0f,
    projectionScale: Float = 1f,
    projectionAxisAngleDeg: Float = 90f,
    sizeFactor: Float = 1.7f,
    projectionShift: Offset = Offset.Zero,
) {
    if (motif == CenterMotif.None) return
    if (fillColor == null) return
    if (absScale <= 0.05f) return

    val centerR = radius * centerRadiusFrac * sizeFactor

    // Default: usa withTransform (KMP) en lugar de android.graphics.Matrix + nativeCanvas
    if (motif == CenterMotif.Default) {
        drawDefaultCenter(facePath, centroid, centerRadiusFrac, fillColor)
        return
    }

    val scalePivot = Offset(centroid.x + projectionShift.x, centroid.y + projectionShift.y)
    val projRotDeg = 90f - projectionAxisAngleDeg

    withTransform({
        if (projRotDeg != 0f) rotate(-projRotDeg, scalePivot)
        scale(projectionScale, 1f, scalePivot)
        if (projRotDeg != 0f) rotate(projRotDeg, scalePivot)
    }) {
        when (motif) {
            CenterMotif.None, CenterMotif.Default -> Unit

            CenterMotif.Ring ->
                drawRingCenter(centroid, centerR, fillColor)

            CenterMotif.Cross ->
                withTransform({ rotate(rotationDeg, centroid) }) {
                    drawCrossCenter(centroid, centerR, fillColor, accentColor)
                }

            CenterMotif.Trefoil ->
                withTransform({ rotate(rotationDeg, centroid) }) {
                    drawTrefoilCenter(centroid, centerR, fillColor)
                }

            CenterMotif.Compass ->
                withTransform({ rotate(rotationDeg, centroid) }) {
                    drawCompassCenter(centroid, centerR, fillColor, accentColor)
                }

            CenterMotif.DiamondCross ->
                withTransform({ rotate(rotationDeg, centroid) }) {
                    drawDiamondCrossCenter(centroid, centerR, fillColor, accentColor)
                }

            CenterMotif.Star5 ->
                withTransform({ rotate(rotationDeg, centroid) }) {
                    drawStar5Center(centroid, centerR, fillColor)
                }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Default — polígono escalado (KMP: withTransform en lugar de Matrix + nativeCanvas)
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawDefaultCenter(
    facePath: Path,
    centroid: Offset,
    centerRadiusFrac: Float,
    fillColor: Color,
) {
    withTransform({
        scale(centerRadiusFrac, centerRadiusFrac, centroid)
    }) {
        drawPath(facePath, color = fillColor)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Cross — cruz de brazos iguales
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawCrossCenter(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
    accentColor: Color,
) {
    val arm = centerR * 0.90f
    val hw = centerR * 0.20f

    val path = Path().apply {
        moveTo(centroid.x - arm, centroid.y - hw)
        lineTo(centroid.x + arm, centroid.y - hw)
        lineTo(centroid.x + arm, centroid.y + hw)
        lineTo(centroid.x - arm, centroid.y + hw)
        close()
        moveTo(centroid.x - hw, centroid.y - arm)
        lineTo(centroid.x + hw, centroid.y - arm)
        lineTo(centroid.x + hw, centroid.y + arm)
        lineTo(centroid.x - hw, centroid.y + arm)
        close()
    }
    drawPath(path, color = fillColor)

    val sq = hw * 0.6f
    val accentPath = Path().apply {
        moveTo(centroid.x - sq, centroid.y - sq)
        lineTo(centroid.x + sq, centroid.y - sq)
        lineTo(centroid.x + sq, centroid.y + sq)
        lineTo(centroid.x - sq, centroid.y + sq)
        close()
    }
    drawPath(accentPath, color = accentColor)
}

// ─────────────────────────────────────────────────────────────────────────────
// Trefoil — trébol de tres pétalos
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawTrefoilCenter(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
) {
    val petalLen = centerR * 0.9f
    val petalWidth = centerR * 0.45f
    val arcRadius = (petalLen * petalLen + petalWidth * petalWidth) / (4f * petalWidth)
    val arcCenterOffset = arcRadius - petalWidth / 2f

    val halfSweepRad = asin((petalLen / 2f) / arcRadius)
    val halfSweepDeg = (halfSweepRad * 180.0 / PI).toFloat()

    for (i in 0 until 3) {
        val petalDirDeg = i * 120.0
        val petalDirRad = (petalDirDeg * PI / 180.0).toFloat()
        val dirX = cos(petalDirRad)
        val dirY = sin(petalDirRad)
        val perpX = -dirY
        val perpY = dirX

        val midX = centroid.x + dirX * (petalLen / 2f)
        val midY = centroid.y + dirY * (petalLen / 2f)

        val path = Path().apply {
            val leftCx = midX - perpX * arcCenterOffset
            val leftCy = midY - perpY * arcCenterOffset
            val rightCx = midX + perpX * arcCenterOffset
            val rightCy = midY + perpY * arcCenterOffset

            val baseAngleLeft = (atan2(perpY, perpX) * 180.0 / PI).toFloat()
            val baseAngleRight = (atan2(-perpY, -perpX) * 180.0 / PI).toFloat()

            arcTo(
                rect = Rect(
                    leftCx - arcRadius, leftCy - arcRadius,
                    leftCx + arcRadius, leftCy + arcRadius,
                ),
                startAngleDegrees = baseAngleLeft - halfSweepDeg,
                sweepAngleDegrees = 2f * halfSweepDeg,
                forceMoveTo = true,
            )
            arcTo(
                rect = Rect(
                    rightCx - arcRadius, rightCy - arcRadius,
                    rightCx + arcRadius, rightCy + arcRadius,
                ),
                startAngleDegrees = baseAngleRight - halfSweepDeg,
                sweepAngleDegrees = 2f * halfSweepDeg,
                forceMoveTo = false,
            )
            close()
        }
        drawPath(path, color = fillColor)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Ring — anillo concéntrico
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRingCenter(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
) {
    drawCircle(
        color = fillColor,
        center = centroid,
        radius = centerR * 0.72f,
        style = Stroke(width = centerR * 0.40f),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Compass — rosa de los vientos de 8 puntas
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawCompassCenter(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
    accentColor: Color,
) {
    val outerR = centerR * 1.05f
    val shortR = centerR * 0.80f
    val innerR = centerR * 0.45f

    val path = Path().apply {
        for (i in 0 until 16) {
            val r = when {
                i % 2 == 1 -> innerR
                (i / 2) % 2 == 0 -> outerR
                else -> shortR
            }
            val rad = ((i * 22.5 - 90.0) * PI / 180.0).toFloat()
            val x = centroid.x + r * cos(rad)
            val y = centroid.y + r * sin(rad)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color = fillColor)
    drawCircle(color = accentColor, center = centroid, radius = centerR * 0.14f)
}

// ─────────────────────────────────────────────────────────────────────────────
// DiamondCross — rombo relleno con cruz de acento
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawDiamondCrossCenter(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
    accentColor: Color,
) {
    val dR = centerR * 0.90f
    val rhombus = Path().apply {
        moveTo(centroid.x + dR, centroid.y)
        lineTo(centroid.x, centroid.y + dR)
        lineTo(centroid.x - dR, centroid.y)
        lineTo(centroid.x, centroid.y - dR)
        close()
    }
    drawPath(rhombus, color = fillColor)

    val arm = centerR * 0.70f
    val hw = centerR * 0.18f
    val cross = Path().apply {
        for (i in 0 until 8) {
            val r = if (i % 2 == 0) arm else hw
            val rad = (i * 45.0 * PI / 180.0).toFloat()
            val x = centroid.x + r * cos(rad)
            val y = centroid.y + r * sin(rad)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(cross, color = accentColor)
}

// ─────────────────────────────────────────────────────────────────────────────
// Star5 — estrella de 5 puntas clásica
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawStar5Center(
    centroid: Offset,
    centerR: Float,
    fillColor: Color,
) {
    val outer = centerR * 0.95f
    val inner = centerR * 0.40f

    val path = Path().apply {
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outer else inner
            val rad = (i * 36.0 * PI / 180.0).toFloat()
            val x = centroid.x + r * cos(rad)
            val y = centroid.y + r * sin(rad)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color = fillColor)
}
package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// DoubleRing
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawDoubleRingBorder(
    facePath: Path,
    borderWidth: Float,
    borderColor: Color,
    accentColor: Color,
) {
    clipPath(facePath) {
        drawPath(facePath, color = borderColor, style = Stroke(width = borderWidth * 2f))
        drawPath(facePath, color = accentColor, style = Stroke(width = borderWidth * 1.5f))
        drawPath(facePath, color = borderColor, style = Stroke(width = borderWidth * 0.8f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Fishtail
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawFishtailStamp(
    pos: Offset,
    tangentDeg: Float,
    borderWidth: Float,
    color: Color,
    cellWidth: Float? = null,
) {
    val nominalCellWidth = borderWidth * 2.4f
    val effectiveCellWidth = cellWidth ?: nominalCellWidth
    val scaleX = effectiveCellWidth / nominalCellWidth

    val w = (borderWidth * 1.22f) * scaleX
    val h = borderWidth * 0.75f
    val elevation = borderWidth * 0.55f
    val armLen = (borderWidth * 0.85f) * scaleX
    val topLen = (borderWidth * 1.6f) * scaleX
    val t = borderWidth * 0.20f

    val rad = (tangentDeg * PI / 180.0).toFloat()
    val perp = rad + (PI / 2f).toFloat()
    val cosR = cos(rad)
    val sinR = sin(rad)
    val cosP = cos(perp)
    val sinP = sin(perp)

    fun p(along: Float, across: Float): Offset = Offset(
        pos.x + along * cosR + across * cosP,
        pos.y + along * sinR + across * sinP,
    )

    val path = Path().apply {
        val a0 = p(-w, +h)
        val a1 = p(-w + armLen, +h)
        val b0 = p(-topLen / 2f, +h - elevation)
        val b1 = p(topLen / 2f, +h - elevation)
        val c0 = p(w - armLen, +h)
        val c1 = p(w, +h)

        moveTo(a0.x, a0.y)
        lineTo(a1.x, a1.y)
        lineTo(b0.x, b0.y)
        lineTo(b1.x, b1.y)
        lineTo(c0.x, c0.y)
        lineTo(c1.x, c1.y)
    }

    drawPath(path, color = color, style = Stroke(width = t, cap = StrokeCap.Butt, join = StrokeJoin.Miter))
}

// ─────────────────────────────────────────────────────────────────────────────
// Diamond
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawDiamondStamp(
    pos: Offset,
    tangentDeg: Float,
    borderWidth: Float,
    color: Color,
) {
    val half = borderWidth * 0.85f
    val rad = (tangentDeg * PI / 180.0).toFloat()
    val perp = rad + (PI / 3f).toFloat()

    val cosR = cos(rad)
    val sinR = sin(rad)
    val cosP = cos(perp)
    val sinP = sin(perp)

    val path = Path().apply {
        moveTo(pos.x + half * cosR + half * cosP, pos.y + half * sinR + half * sinP)
        lineTo(pos.x - half * cosR + half * cosP, pos.y - half * sinR + half * sinP)
        lineTo(pos.x - half * cosR - half * cosP, pos.y - half * sinR - half * sinP)
        lineTo(pos.x + half * cosR - half * cosP, pos.y + half * sinR - half * sinP)
        close()
    }
    drawPath(path, color = color)
}

// ─────────────────────────────────────────────────────────────────────────────
// Chevron
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawChevronStamp(
    pos: Offset,
    tangentDeg: Float,
    borderWidth: Float,
    color: Color,
) {
    val halfBase = borderWidth * 0.90f
    val height = borderWidth * 1.50f
    val rad = (tangentDeg * PI / 180.0).toFloat()
    val perp = rad + (PI / 2f).toFloat()

    val cosR = cos(rad)
    val sinR = sin(rad)
    val cosP = cos(perp)
    val sinP = sin(perp)

    val path = Path().apply {
        moveTo(
            pos.x - halfBase * cosR - borderWidth * 0.5f * cosP,
            pos.y - halfBase * sinR - borderWidth * 0.5f * sinP,
        )
        lineTo(
            pos.x + halfBase * cosR - borderWidth * 0.5f * cosP,
            pos.y + halfBase * sinR - borderWidth * 0.5f * sinP,
        )
        lineTo(
            pos.x + (height - borderWidth * 0.5f) * cosP,
            pos.y + (height - borderWidth * 0.5f) * sinP,
        )
        close()
    }
    drawPath(path, color = color)
}

// ─────────────────────────────────────────────────────────────────────────────
// Meander
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawMeanderStamp(
    pos: Offset,
    tangentDeg: Float,
    borderWidth: Float,
    color: Color,
    cellWidth: Float? = null,
) {
    val nominalCellWidth = borderWidth * 2.4f
    val effectiveCellWidth = cellWidth ?: nominalCellWidth
    val scaleX = effectiveCellWidth / nominalCellWidth

    val w = (borderWidth * 1.2f) * scaleX
    val h = borderWidth * 1.1f
    val t = borderWidth * 0.2f
    val step = (h * 1.5f - t) / 3.5f

    val rad = (tangentDeg * PI / 180.0).toFloat()
    val perp = rad + (PI / 2f).toFloat()
    val cosR = cos(rad)
    val sinR = sin(rad)
    val cosP = cos(perp)
    val sinP = sin(perp)

    fun p(along: Float, across: Float): Offset = Offset(
        pos.x + along * cosR + across * cosP,
        pos.y + along * sinR + across * sinP,
    )

    val topLine = Path().apply {
        val a = p(-w, -h + t * 0.5f)
        val b = p(w, -h + t * 0.5f)
        moveTo(a.x, a.y); lineTo(b.x, b.y)
    }

    val spiral = Path().apply {
        val p0 = p(-w + step * 0.6f, -h + t * 0.5f)
        val p1 = p(-w + step * 0.6f, h - step)
        val p2 = p(w - step * 0.6f, h - step)
        val p3 = p(w - step * 0.6f, -h + t * 0.5f + step)
        val p4 = p(-w + step * 1.8f, -h + t * 0.5f + step)
        val p5 = p(-w + step * 1.8f, h - step * 2f)
        val p6 = p(w - step * 1.8f, h - step * 2f)
        val p7 = p(w - step * 1.8f, 0f)

        moveTo(p0.x, p0.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        lineTo(p4.x, p4.y)
        lineTo(p5.x, p5.y)
        lineTo(p6.x, p6.y)
        lineTo(p7.x, p7.y)
    }

    val stroke = Stroke(width = t, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    drawPath(topLine, color = color, style = stroke)
    drawPath(spiral, color = color, style = stroke)
}
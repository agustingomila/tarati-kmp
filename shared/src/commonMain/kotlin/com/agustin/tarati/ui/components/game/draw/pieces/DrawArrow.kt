package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

fun DrawScope.drawArcWithArrowHead(
    rect: Rect,
    brush: Brush,
    startAngle: Float,
    sweepAngle: Float,
    useCenter: Boolean,
    style: Stroke,
    arrowSize: Float = 20f,
    arrowWidth: Float = arrowSize * 0.35f,
    arrowAtStart: Boolean = false,
    arrowAtEnd: Boolean = true,
    arrowStyle: ArrowTipStyle = ArrowTipStyle.PENCIL,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    drawArc(
        brush = brush,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = useCenter,
        topLeft = rect.topLeft,
        size = rect.size,
        style = style,
        alpha = alpha,
        colorFilter = colorFilter,
        blendMode = blendMode
    )

    val cx = rect.center.x
    val cy = rect.center.y
    val rx = rect.width / 2f
    val ry = rect.height / 2f

    val startRad = (startAngle * PI / 180.0).toFloat()
    val sweepRad = (sweepAngle * PI / 180.0).toFloat()
    val endRad = startRad + sweepRad

    fun drawArrowAt(angleRad: Float, isStart: Boolean) {
        val x = cx + rx * cos(angleRad)
        val y = cy + ry * sin(angleRad)
        val point = Offset(x, y)

        val tx = -rx * sin(angleRad)
        val ty = ry * cos(angleRad)
        val length = hypot(tx, ty)
        if (length == 0f) return

        val dirX = tx / length
        val dirY = ty / length

        val baseAngle = atan2(dirY, dirX) * 180f / PI.toFloat()
        val angleDeg = if (isStart) baseAngle + 180f else baseAngle

        createArrowPath(arrowStyle, arrowSize, arrowWidth)?.let { path ->
            drawArrowPath(
                path = path,
                point = point,
                rotation = angleDeg,
                brush = brush,
                arrowStyle = arrowStyle,
                arrowWidth = arrowWidth,
                alpha = alpha,
                colorFilter = colorFilter,
                blendMode = blendMode
            )
        }
    }

    if (arrowAtEnd) drawArrowAt(endRad, isStart = false)
    if (arrowAtStart) drawArrowAt(startRad, isStart = true)
}

fun DrawScope.drawLineWithArrowHead(
    start: Offset,
    end: Offset,
    brush: Brush,
    strokeWidth: Float = 4f,
    arrowSize: Float = 20f,
    arrowWidth: Float = arrowSize * 0.35f,
    arrowAtStart: Boolean = false,
    arrowAtEnd: Boolean = true,
    arrowStyle: ArrowTipStyle = ArrowTipStyle.PENCIL,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    val lineVector = end - start
    val lineLength = lineVector.getDistance()

    if (lineLength > 0f) {
        drawLine(
            brush = brush,
            start = start,
            end = end,
            strokeWidth = strokeWidth,
            alpha = alpha,
            colorFilter = colorFilter,
            blendMode = blendMode,
            cap = StrokeCap.Butt
        )
    }

    val angleDeg = atan2(lineVector.y, lineVector.x) * 180f / PI.toFloat()

    fun drawArrowAt(point: Offset, isStart: Boolean) {
        val rotation = if (isStart) angleDeg + 180f else angleDeg

        // Solapamiento para eliminar gap visual al zoom (1% del grosor de línea)
        val overlap = strokeWidth * 0.01f + 0.6f  // Mínimo 0.5px para cubrir antialiasing

        createArrowPath(arrowStyle, arrowSize, arrowWidth, overlap)?.let { path ->
            drawArrowPath(
                path = path,
                point = point,
                rotation = rotation,
                brush = brush,
                arrowStyle = arrowStyle,
                arrowWidth = arrowWidth,
                alpha = alpha,
                colorFilter = colorFilter,
                blendMode = blendMode
            )
        }
    }

    if (arrowAtStart) drawArrowAt(start, isStart = true)
    if (arrowAtEnd) drawArrowAt(end, isStart = false)
}

// Función helper reutilizable
private fun DrawScope.drawArrowPath(
    path: Path,
    point: Offset,
    rotation: Float,
    brush: Brush,
    arrowStyle: ArrowTipStyle,
    arrowWidth: Float,
    alpha: Float,
    colorFilter: ColorFilter?,
    blendMode: BlendMode
) {
    withTransform({
        translate(point.x, point.y)
        rotate(rotation, pivot = Offset.Zero)
    }) {
        val drawStyle = when (arrowStyle) {
            ArrowTipStyle.CHEVRON -> Stroke(width = arrowWidth * 0.2f)
            else -> Fill
        }
        drawPath(
            path,
            brush,
            style = drawStyle,
            alpha = alpha,
            colorFilter = colorFilter,
            blendMode = blendMode
        )
    }
}

// Función helper para crear paths
private fun createArrowPath(
    style: ArrowTipStyle,
    size: Float,
    width: Float,
    overlap: Float = 0f
): Path? = when (style) {
    ArrowTipStyle.NONE -> null
    ArrowTipStyle.ARROW -> Path().apply {
        moveTo(-overlap, -width / 2)
        lineTo(size, 0f)
        lineTo(-overlap, width / 2)
        close()
    }

    ArrowTipStyle.PENCIL -> Path().apply {
        moveTo(-overlap, -width / 2)
        lineTo(size * 0.7f, -width * 0.15f)
        lineTo(size, 0f)
        lineTo(size * 0.7f, width * 0.15f)
        lineTo(-overlap, width / 2)
        close()
    }

    ArrowTipStyle.CHEVRON -> Path().apply {
        moveTo(0f, -width / 2)
        lineTo(size * 0.6f, 0f)
        lineTo(0f, width / 2)
    }

    ArrowTipStyle.DIAMOND -> Path().apply {
        moveTo(size * 0.3f, 0f)
        lineTo(-overlap, -width / 2)
        lineTo(-size * 0.3f - overlap, 0f)
        lineTo(-overlap, width / 2)
        close()
    }
}
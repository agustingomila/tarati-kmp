package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.ui.components.game.draw.pieces.ArrowTipStyle
import com.agustin.tarati.ui.components.game.draw.pieces.drawArcWithArrowHead
import com.agustin.tarati.ui.components.game.draw.pieces.drawLineWithArrowHead
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview(name = "Pencil Tip - Default", group = "Arrow Tips")
@Composable
fun PreviewPencilTip() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 60.dp.toPx()

            // Anillos base de referencia
            drawCircle(
                color = Color.Gray,
                center = center,
                radius = radius,
                style = Stroke(width = 2f),
                alpha = 0.3f
            )

            // Arco con punta de lápiz
            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                ),
                brush = SolidColor(Color.Cyan),
                startAngle = 45f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = 4f),
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )
        }
    }
}

@Preview(name = "Arrow Tip - Classic", group = "Arrow Tips")
@Composable
fun PreviewArrowTip() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 60.dp.toPx()

            drawCircle(
                color = Color.Gray,
                center = center,
                radius = radius,
                style = Stroke(width = 2f),
                alpha = 0.3f
            )

            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                ),
                brush = SolidColor(Color.Magenta),
                startAngle = 0f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = 4f),
                arrowSize = 24f,
                arrowWidth = 16f,
                arrowStyle = ArrowTipStyle.ARROW,
                arrowAtStart = true
            )
        }
    }
}

@Preview(name = "Chevron Tip - Open", group = "Arrow Tips")
@Composable
fun PreviewChevronTip() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 60.dp.toPx()

            drawCircle(
                color = Color.Gray,
                center = center,
                radius = radius,
                style = Stroke(width = 2f),
                alpha = 0.3f
            )

            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                ),
                brush = SolidColor(Color.Cyan),
                startAngle = 180f,
                sweepAngle = 60f,
                useCenter = false,
                style = Stroke(width = 4f),
                arrowSize = 18f,
                arrowWidth = 14f,
                arrowStyle = ArrowTipStyle.CHEVRON,
                arrowAtStart = true
            )
        }
    }
}

@Preview(name = "Diamond Tip - Marker", group = "Arrow Tips")
@Composable
fun PreviewDiamondTip() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 60.dp.toPx()

            drawCircle(
                color = Color.Gray,
                center = center,
                radius = radius,
                style = Stroke(width = 2f),
                alpha = 0.3f
            )

            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                ),
                brush = SolidColor(Color.Yellow),
                startAngle = 270f,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = 4f),
                arrowSize = 16f,
                arrowWidth = 12f,
                arrowStyle = ArrowTipStyle.DIAMOND,
                arrowAtStart = true
            )
        }
    }
}

@Preview(name = "All Tips Comparison", group = "Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewAllTipsComparison() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Configuración de 4 cuadrantes
            val configs = listOf(
                // Superior izquierdo: PENCIL
                Triple(
                    Offset(center.x - 138f, center.y - 138f),
                    ArrowTipStyle.PENCIL,
                    Color.Cyan
                ),
                // Superior derecho: ARROW
                Triple(
                    Offset(center.x + 138f, center.y - 138f),
                    ArrowTipStyle.ARROW,
                    Color.Magenta
                ),
                // Inferior izquierdo: CHEVRON
                Triple(
                    Offset(center.x - 138f, center.y + 138f),
                    ArrowTipStyle.CHEVRON,
                    Color.Cyan
                ),
                // Inferior derecho: DIAMOND
                Triple(
                    Offset(center.x + 138f, center.y + 138f),
                    ArrowTipStyle.DIAMOND,
                    Color.Yellow
                )
            )

            configs.forEach { (pos, style, color) ->
                val radius = 100f

                // Círculo guía
                drawCircle(
                    color = Color.Gray,
                    center = pos,
                    radius = radius,
                    style = Stroke(width = 1f),
                    alpha = 0.3f
                )

                // Etiqueta
                // Nota: En Canvas puro no hay texto fácil,
                // pero podrías agregar Overlay con Text()

                // Arco con punta específica
                drawArcWithArrowHead(
                    rect = Rect(
                        offset = Offset(pos.x - radius, pos.y - radius),
                        size = Size(radius * 2, radius * 2)
                    ),
                    brush = SolidColor(color),
                    startAngle = when (style) {
                        ArrowTipStyle.PENCIL -> 30f
                        ArrowTipStyle.ARROW -> 0f
                        ArrowTipStyle.CHEVRON -> 180f
                        else -> 270f
                    },
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 3f),
                    arrowSize = when (style) {
                        ArrowTipStyle.PENCIL -> 18f
                        ArrowTipStyle.ARROW -> 20f
                        ArrowTipStyle.CHEVRON -> 16f
                        else -> 14f
                    },
                    arrowWidth = when (style) {
                        ArrowTipStyle.PENCIL -> 5f
                        ArrowTipStyle.ARROW -> 12f
                        ArrowTipStyle.CHEVRON -> 10f
                        else -> 9f
                    },
                    arrowStyle = style,
                    arrowAtStart = true
                )
            }

            // Líneas divisorias
            drawLine(
                color = Color.Gray,
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth = 1f,
                alpha = 0.2f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 1f,
                alpha = 0.2f
            )
        }
    }
}

@Preview(name = "Animated Pencil - Selection Style", group = "Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewAnimatedSelectionStyle() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val timeMs by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Box(
        modifier = Modifier
            .size(250.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val position = Offset(size.width / 2, size.height / 2)
            val radius = 40.dp.toPx()
            val highlightRadius = radius * 1.4f

            // Anillos base (simplificados)
            drawCircle(
                color = Color.White,
                center = position,
                radius = highlightRadius,
                style = Stroke(width = radius * 0.08f),
                alpha = 0.6f
            )

            val angleRad = (timeMs % 2000f) / 2000f * 2f * PI.toFloat()
            val glowAngleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()

            // Configuración exacta de tu drawSelection
            val innerArcSweep = 80f
            val extraSweep = 40f
            val diffuseArcSweep = innerArcSweep + (extraSweep * 2)
            val diffuseStartAngle = glowAngleDeg - extraSweep

            val diffuseRadius = highlightRadius * 1f
            val diffuseStrokeWidth = radius * 0.3f
            val innerRadius = highlightRadius * 1f
            val innerStrokeWidth = radius * 0.12f

            // Arco difuso
            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(position.x - diffuseRadius, position.y - diffuseRadius),
                    size = Size(diffuseRadius * 2f, diffuseRadius * 2f)
                ),
                brush = SolidColor(Color.Cyan.copy(alpha = 0.15f)),
                startAngle = diffuseStartAngle,
                sweepAngle = diffuseArcSweep,
                useCenter = false,
                style = Stroke(width = diffuseStrokeWidth),
                arrowSize = diffuseStrokeWidth * 2f,
                arrowWidth = diffuseStrokeWidth * 1f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )

            // Arco principal
            drawArcWithArrowHead(
                rect = Rect(
                    offset = Offset(position.x - innerRadius, position.y - innerRadius),
                    size = Size(innerRadius * 2f, innerRadius * 2f)
                ),
                brush = SolidColor(Color.Cyan.copy(alpha = 0.7f)),
                startAngle = glowAngleDeg,
                sweepAngle = innerArcSweep,
                useCenter = false,
                style = Stroke(width = innerStrokeWidth),
                arrowSize = innerStrokeWidth * 3f,
                arrowWidth = innerStrokeWidth * 1f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )

            // Punto de brillo
            val headAngleRad = angleRad + Math.toRadians(80 * 0.5).toFloat()
            val headX = position.x + highlightRadius * cos(headAngleRad)
            val headY = position.y + highlightRadius * sin(headAngleRad)
            drawCircle(
                color = Color.Cyan.copy(alpha = 0.9f),
                center = Offset(headX, headY),
                radius = radius * 0.12f
            )
        }
    }
}

@Preview(name = "Pencil Tip - Horizontal Line", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLinePencilHorizontal() {
    Box(
        modifier = Modifier
            .size(300.dp, 150.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(50f, size.height / 2)
            val end = Offset(size.width - 50f, size.height / 2)

            // Línea guía punteada
            drawLine(
                color = Color.Gray,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1f,
                alpha = 0.3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            drawLineWithArrowHead(
                start = start,
                end = end,
                brush = SolidColor(Color.Cyan),
                strokeWidth = 6f,
                arrowSize = 24f,
                arrowWidth = 6f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )
        }
    }
}

@Preview(name = "All Tips - Vertical Comparison", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineAllTipsVertical() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val lineLength = 120f
            val spacing = 80f

            val configs = listOf(
                Triple(ArrowTipStyle.PENCIL, Color.Cyan, "PENCIL"),
                Triple(ArrowTipStyle.ARROW, Color.Magenta, "ARROW"),
                Triple(ArrowTipStyle.CHEVRON, Color.Cyan, "CHEVRON"),
                Triple(ArrowTipStyle.DIAMOND, Color.Yellow, "DIAMOND")
            )

            configs.forEachIndexed { index, (style, color, _) ->
                val y = 60f + index * spacing

                // Línea horizontal para cada estilo
                val start = Offset(centerX - lineLength, y)
                val end = Offset(centerX + lineLength, y)

                // Etiqueta (simulada con líneas, en Compose real usarías Text)
                drawLine(
                    color = color,
                    start = Offset(20f, y),
                    end = Offset(20f, y),
                    strokeWidth = 8f
                )

                drawLineWithArrowHead(
                    start = start,
                    end = end,
                    brush = SolidColor(color),
                    arrowWidth = when (style) {
                        ArrowTipStyle.PENCIL -> 4f
                        ArrowTipStyle.ARROW -> 12f
                        ArrowTipStyle.CHEVRON -> 10f
                        else -> 9f
                    },
                    arrowStyle = style,
                    arrowAtStart = true
                )
            }
        }
    }
}

@Preview(name = "Diagonal Lines - All Directions", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineDiagonalDirections() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = 180f

            // 8 direcciones cardinales e intermedias
            val angles = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)

            angles.forEach { angleDeg ->
                val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
                val dirX = cos(angleRad)
                val dirY = sin(angleRad)

                val start = Offset(
                    center.x - dirX * radius * 0.3f,
                    center.y - dirY * radius * 0.3f
                )
                val end = Offset(
                    center.x + dirX * radius * 0.7f,
                    center.y + dirY * radius * 0.7f
                )

                // Color basado en ángulo para distinguir direcciones
                val hue = angleDeg / 360f
                val color = Color.hsv(hue * 360f, 0.8f, 1.0f)

                drawLineWithArrowHead(
                    start = start,
                    end = end,
                    brush = SolidColor(color),
                    strokeWidth = 3f,
                    arrowSize = 16f,
                    arrowWidth = 8f,
                    arrowStyle = ArrowTipStyle.ARROW
                )
            }
        }
    }
}

@Preview(name = "Stroke Width Comparison", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineStrokeWidths() {
    Box(
        modifier = Modifier
            .size(200.dp, 200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = 50f
            val endX = size.width - 50f
            val baseY = 50f
            val spacing = 50f

            val strokes = listOf(
                Triple(2f, 12f, 2f),   // Delgada
                Triple(6f, 20f, 6f),   // Media
                Triple(12f, 32f, 12f), // Gruesa
                Triple(20f, 48f, 20f)  // Muy gruesa
            )

            strokes.forEachIndexed { index, (strokeW, arrowS, arrowW) ->
                val y = baseY + index * spacing

                // Línea de referencia
                drawLine(
                    color = Color.Gray,
                    start = Offset(startX, y),
                    end = Offset(endX, y),
                    strokeWidth = 1f,
                    alpha = 0.2f
                )

                drawLineWithArrowHead(
                    start = Offset(startX, y),
                    end = Offset(endX, y),
                    brush = SolidColor(Color(0xFF00D9FF)),
                    strokeWidth = strokeW,
                    arrowSize = arrowS,
                    arrowWidth = arrowW,
                    arrowStyle = ArrowTipStyle.PENCIL,
                    arrowAtStart = true
                )
            }
        }
    }
}

@Preview(name = "Single Arrow Configurations", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineSingleArrows() {
    Box(
        modifier = Modifier
            .size(350.dp, 200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerY = size.height / 2
            val spacing = 70f

            // Solo inicio
            drawLineWithArrowHead(
                start = Offset(40f, centerY - spacing),
                end = Offset(160f, centerY - spacing),
                brush = SolidColor(Color.Magenta),
                strokeWidth = 5f,
                arrowSize = 18f,
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true,
                arrowAtEnd = false
            )

            // Solo fin
            drawLineWithArrowHead(
                start = Offset(190f, centerY - spacing),
                end = Offset(310f, centerY - spacing),
                brush = SolidColor(Color.Cyan),
                strokeWidth = 5f,
                arrowSize = 18f,
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.PENCIL
            )

            // Ambas (default)
            drawLineWithArrowHead(
                start = Offset(40f, centerY + spacing),
                end = Offset(160f, centerY + spacing),
                brush = SolidColor(Color.Yellow),
                strokeWidth = 5f,
                arrowSize = 18f,
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )

            // Ninguna (línea pura)
            drawLineWithArrowHead(
                start = Offset(190f, centerY + spacing),
                end = Offset(310f, centerY + spacing),
                brush = SolidColor(Color.Gray),
                strokeWidth = 5f,
                arrowSize = 18f,
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.NONE,
                arrowAtStart = true  // Ignorado por NONE
                // Ignorado por NONE
            )
        }
    }
}

@Preview(name = "Connected Graph - Network", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineConnectedGraph() {
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodes = listOf(
                Offset(size.width * 0.2f, size.height * 0.3f),
                Offset(size.width * 0.8f, size.height * 0.2f),
                Offset(size.width * 0.5f, size.height * 0.7f),
                Offset(size.width * 0.3f, size.height * 0.8f),
                Offset(size.width * 0.7f, size.height * 0.6f)
            )

            val connections = listOf(
                0 to 1, 0 to 2, 1 to 4, 2 to 3, 2 to 4, 3 to 4
            )

            // Dibujar conexiones primero (detrás de nodos)
            connections.forEach { (from, to) ->
                drawLineWithArrowHead(
                    start = nodes[from],
                    end = nodes[to],
                    brush = SolidColor(Color.Cyan.copy(alpha = 0.6f)),
                    strokeWidth = 3f,
                    arrowSize = 14f,
                    arrowWidth = 6f,
                    arrowStyle = ArrowTipStyle.PENCIL
                )
            }

            // Dibujar nodos
            nodes.forEach { pos ->
                drawCircle(
                    color = Color.Magenta,
                    center = pos,
                    radius = 12f
                )
                drawCircle(
                    color = Color.White,
                    center = pos,
                    radius = 6f
                )
            }
        }
    }
}

@Preview(name = "Chevron vs Pencil - Detail", group = "Line Arrow Tips", widthDp = 200, heightDp = 200)
@Composable
fun PreviewLineChevronDetail() {
    Box(
        modifier = Modifier
            .size(350.dp, 150.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y1 = size.height * 0.3f
            val y2 = size.height * 0.7f

            // Pencil arriba
            drawLineWithArrowHead(
                start = Offset(50f, y1),
                end = Offset(size.width - 50f, y1),
                brush = SolidColor(Color.Cyan),
                strokeWidth = 5f,
                arrowWidth = 5f,
                arrowStyle = ArrowTipStyle.PENCIL,
                arrowAtStart = true
            )

            // Chevron abajo (mismo tamaño para comparar)
            drawLineWithArrowHead(
                start = Offset(50f, y2),
                end = Offset(size.width - 50f, y2),
                brush = SolidColor(Color.Yellow),
                strokeWidth = 5f,
                arrowWidth = 12f,  // Chevron se ve mejor más ancho
                arrowStyle = ArrowTipStyle.CHEVRON,
                arrowAtStart = true
            )
        }
    }
}
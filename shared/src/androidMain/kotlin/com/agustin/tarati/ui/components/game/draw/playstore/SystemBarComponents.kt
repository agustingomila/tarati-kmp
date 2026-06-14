package com.agustin.tarati.ui.components.game.draw.playstore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Constantes base (en dp) que luego se multiplican por escala
private val SIGNAL_BAR_WIDTH = 2.5f.dp
private val SIGNAL_BAR_GAP = 1.2f.dp
private val SIGNAL_MAX_HEIGHT = 9f.dp

private val WIFI_DOT_RADIUS = 1.5f.dp
private val WIFI_ARC_STROKE = 1.5f.dp
private val WIFI_ARC_RADIUS_STEP = 3.2f.dp

private val BATTERY_BODY_WIDTH = 18f.dp
private val BATTERY_BODY_HEIGHT = 9f.dp
private val BATTERY_NUB_WIDTH = 2f.dp
private val BATTERY_NUB_HEIGHT = 4f.dp
private val BATTERY_STROKE = 1.5f.dp
private val BATTERY_FILL_PADDING = 2f.dp

/**
 * Dibuja las barras de señal (4 barras, altura variable según nivel).
 * @param startX Borde izquierdo del conjunto de barras
 * @param baseY Coordenada Y de la base de las barras (punto más bajo)
 * @param level Nivel de señal de 0 a 4 (número de barras activas)
 * @param color Color de las barras
 * @param alpha Opacidad (0..1)
 * @param scale Factor de escala global
 */
fun DrawScope.drawSignalBars(
    startX: Float,
    baseY: Float,
    level: Int,
    color: Color,
    alpha: Float = 0.9f,
    scale: Float = 1f
) {
    val barW = SIGNAL_BAR_WIDTH.toPx() * scale
    val barGap = SIGNAL_BAR_GAP.toPx() * scale
    val maxH = SIGNAL_MAX_HEIGHT.toPx() * scale
    val clampedLevel = level.coerceIn(0, 4)

    for (i in 0 until 4) {
        val isActive = i < clampedLevel
        if (isActive) {
            val height = maxH * (i + 1) / 3f
            val x = startX + i * (barW + barGap)
            drawRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(x, baseY - height),
                size = Size(barW, height)
            )
        }
    }
}

/**
 * Dibuja el icono de WiFi (punto central + arcos concéntricos).
 * @param centerX Centro horizontal del conjunto
 * @param centerY Centro vertical del punto central (y base de los arcos)
 * @param strength Nivel de intensidad: 0..3 (número de arcos dibujados)
 * @param color Color del icono
 * @param alpha Opacidad
 * @param scale Factor de escala
 */
fun DrawScope.drawWifiIcon(
    centerX: Float,
    centerY: Float,
    strength: Int,
    color: Color,
    alpha: Float = 0.9f,
    scale: Float = 1f
) {
    val dotRadius = WIFI_DOT_RADIUS.toPx() * scale
    val arcStroke = WIFI_ARC_STROKE.toPx() * scale
    val step = WIFI_ARC_RADIUS_STEP.toPx() * scale
    val clampedStrength = strength.coerceIn(0, 3)

    // Punto central
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = dotRadius,
        center = Offset(centerX, centerY)
    )

    // Arcos concéntricos (1 = más interno, 3 = más externo)
    for (i in 1..clampedStrength) {
        val radius = i * step
        drawArc(
            color = color.copy(alpha = alpha),
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = arcStroke)
        )
    }
}

/**
 * Dibuja el icono de batería con nivel de carga.
 * @param rightNubX Coordenada X del borde derecho del nub (pequeño saliente)
 * @param bodyY Coordenada Y superior del cuerpo de la batería
 * @param bodyHeight Altura del cuerpo (se usa para centrar el nub verticalmente)
 * @param chargePercent Porcentaje de carga (0..100)
 * @param color Color del contorno y relleno
 * @param alpha Opacidad
 * @param scale Factor de escala
 */
fun DrawScope.drawBatteryIcon(
    rightNubX: Float,
    bodyY: Float,
    bodyHeight: Float,
    chargePercent: Int,
    color: Color,
    alpha: Float = 0.9f,
    scale: Float = 1f
) {
    val nubW = BATTERY_NUB_WIDTH.toPx() * scale
    val nubH = BATTERY_NUB_HEIGHT.toPx() * scale
    val bodyW = BATTERY_BODY_WIDTH.toPx() * scale
    val bodyH = BATTERY_BODY_HEIGHT.toPx() * scale
    val strokeWidth = BATTERY_STROKE.toPx() * scale
    val fillPad = BATTERY_FILL_PADDING.toPx() * scale

    // Posiciones reales
    val nubLeft = rightNubX - nubW
    val bodyLeft = nubLeft - bodyW
    val nubTop = bodyY + (bodyH - nubH) / 2f

    // Nub
    drawRect(
        color = color,
        topLeft = Offset(nubLeft, nubTop),
        size = Size(nubW, nubH)
    )

    // Contorno
    drawRoundRect(
        color = color.copy(alpha = alpha),
        topLeft = Offset(bodyLeft, bodyY),
        size = Size(bodyW, bodyH),
        cornerRadius = CornerRadius(2.dp.toPx() * scale),
        style = Stroke(width = strokeWidth)
    )

    // Relleno según porcentaje
    val fillPercent = (chargePercent.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)
    val fillWidth = (bodyW - fillPad * 2) * fillPercent
    if (fillWidth > 0f) {
        drawRoundRect(
            color = color.copy(alpha = alpha),
            topLeft = Offset(bodyLeft + fillPad, bodyY + fillPad),
            size = Size(fillWidth, bodyH - fillPad * 2),
            cornerRadius = CornerRadius(1.dp.toPx() * scale)
        )
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 20)
@Composable
fun PreviewSignalBars() {
    Canvas(modifier = Modifier.size(180.dp, 80.dp)) {
        val baseY = size.height - 12f
        val startX = 30f
        val step = 60f

        listOf(4, 2, 0).forEachIndexed { index, level ->
            drawSignalBars(
                startX = startX + index * step,
                baseY = baseY,
                level = level,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 20)
@Composable
fun PreviewWifiIcon() {
    Canvas(modifier = Modifier.size(200.dp, 80.dp)) {
        val centerY = size.height - 15f
        val startX = 30f
        val step = 60f

        listOf(1, 2, 3).forEachIndexed { index, strength ->
            drawWifiIcon(
                centerX = startX + index * step,
                centerY = centerY,
                strength = strength,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 20)
@Composable
fun PreviewBatteryIcon() {
    Canvas(modifier = Modifier.size(220.dp, 80.dp)) {
        val bodyY = 20f
        val bodyH = 30f
        val startX = 60f + bodyH
        val step = 80f

        listOf(25, 85, 100).forEachIndexed { index, percent ->
            drawBatteryIcon(
                rightNubX = startX + index * step,
                bodyY = bodyY,
                bodyHeight = bodyH,
                chargePercent = percent,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 20)
@Composable
fun PreviewCombinedStatusIcons() {
    Canvas(modifier = Modifier.size(250.dp, 60.dp)) {
        val scale = 1f
        val iconColor = Color.Black
        val alpha = 0.9f
        val baseY = size.height - 10f
        val startGap = 30f

        // Señal (4 barras)
        val signalWidth = 4 * SIGNAL_BAR_WIDTH.toPx() + 3 * SIGNAL_BAR_GAP.toPx()  // ≈ 14.5 dp
        drawSignalBars(
            startX = startGap,
            baseY = baseY,
            level = 4,
            color = iconColor,
            alpha = alpha,
            scale = scale
        )

        // WiFi (centro a startGap + signalWidth + gap)
        val wifiCenterX = startGap * 3 + signalWidth
        drawWifiIcon(
            centerX = wifiCenterX,
            centerY = baseY,
            strength = 3,
            color = iconColor,
            alpha = alpha,
            scale = scale
        )

        // Batería (borde derecho del WiFi + radio máximo + gap)
        val wifiMaxRadius = 3 * WIFI_ARC_RADIUS_STEP.toPx()
        val batteryRightNubX = startGap + wifiCenterX + wifiMaxRadius + baseY
        val bodyHeight = 30f
        drawBatteryIcon(
            rightNubX = batteryRightNubX,
            bodyY = baseY - bodyHeight,
            bodyHeight = bodyHeight,
            chargePercent = 85,
            color = iconColor,
            alpha = alpha,
            scale = scale
        )
    }
}
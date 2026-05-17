package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs


/**
 * Devuelve el estado para construir las luces y sombras de las piezas
 */
fun getLightOfDay(
    hourOfDay: Float,
    baseRadius: Float,
): LightOfDay {
    val sunPosition = calculateSunPosition(hourOfDay)
    val shadowDirection = -sunPosition

    val shadowIntensity = calculateShadowIntensity(hourOfDay)
    val shadowLength = baseRadius * shadowIntensity * 0.2f

    val shadowOffsetX = shadowDirection.x * shadowLength
    val shadowOffsetY = shadowDirection.y * shadowLength

    return LightOfDay(
        sunPosition = sunPosition,
        shadowDirection = shadowDirection,
        shadowIntensity = shadowIntensity,
        shadowLength = shadowLength,
        shadowOffsetX = shadowOffsetX,
        shadowOffsetY = shadowOffsetY,
    )
}

/**
 * Calcula la intensidad de la sombra basada en la hora
 */
private fun calculateShadowIntensity(hour: Float): Float {
    val normalizedHour = (hour % 24f) / 24f
    val distanceFromNoon = abs(normalizedHour - 0.5f)
    return 0.5f + (distanceFromNoon * 1.5f)
}

data class LightOfDay(
    val sunPosition: Offset,
    val shadowDirection: Offset,
    val shadowIntensity: Float,
    val shadowLength: Float,
    val shadowOffsetX: Float,
    val shadowOffsetY: Float,
)
package com.agustin.tarati.ui.components.game.draw.pieces

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Sombra oval que se aleja y difumina mientras la pieza se eleva durante el volteo.
 *
 * ## 1 — Posición (world → local + alineación del borde)
 * La función corre dentro de `rotate(flipAngleDeg)`, por lo que los offsets en
 * world-space deben transformarse al frame local con la rotación inversa:
 * ```
 *   localOffX =  worldOffX·cosFlip + worldOffY·sinFlip
 *   localOffY = −worldOffX·sinFlip + worldOffY·cosFlip
 * ```
 * Cuando la pieza está plana (sinA=0) el óvalo se centra bajo la pieza, desplazado
 * por el shadowOffset. Cuando está de canto (sinA=1), el extremo inferior toca el
 * tablero en `position` y el óvalo se extiende en la dirección de la sombra.
 *
 * ## 2 — Ancho en canto (depende de la componente de luz perpendicular al eje)
 * ```
 *   lxLocal = (shadowOffX·cosFlip + shadowOffY·sinFlip) / |shadowOffset|  ∈ [−1,1]
 * ```
 * - |lxLocal| = 1 → luz ⊥ al eje → cara expuesta → sRx = sRy
 * - |lxLocal| = 0 → luz ∥ al eje → solo el canto → sRx = minSRx
 */
actual fun DrawScope.drawFlipShadow(
    position: Offset,
    radius: Float,
    sinA: Float,
    lightOfDay: LightOfDay,
    cosFlip: Float,
    sinFlip: Float,
    shadowColor: Color,
) {
    val distMultiplier = 1f + sinA * 2.8f
    val sRy = radius * 1.2f
    val minSRx = radius * COIN_EDGE_THICKNESS * 0.8f

    val lightLen = sqrt(
        lightOfDay.shadowOffsetX * lightOfDay.shadowOffsetX +
                lightOfDay.shadowOffsetY * lightOfDay.shadowOffsetY
    ).coerceAtLeast(0.001f)
    val lxLocal = (lightOfDay.shadowOffsetX * cosFlip + lightOfDay.shadowOffsetY * sinFlip) / lightLen
    val lyLocal = (-lightOfDay.shadowOffsetX * sinFlip + lightOfDay.shadowOffsetY * cosFlip) / lightLen

    val sRxAtEdge = minSRx + (radius - minSRx) * abs(lxLocal)
    val sRx = ((1f - sinA) * sRy + sinA * sRxAtEdge).coerceAtLeast(minSRx)

    val worldOffX = lightOfDay.shadowOffsetX * distMultiplier
    val worldOffY = lightOfDay.shadowOffsetY * distMultiplier
    val cxFlat = position.x + worldOffX * cosFlip + worldOffY * sinFlip
    val cyFlat = position.y - worldOffX * sinFlip + worldOffY * cosFlip
    val cxEdge = position.x + sRy * lxLocal
    val cyEdge = position.y + sRy * lyLocal

    val cx = (1f - sinA) * cxFlat + sinA * cxEdge
    val cy = (1f - sinA) * cyFlat + sinA * cyEdge

    val shadowOvalAngle = -(atan2(lxLocal.toDouble(), lyLocal.toDouble()) * 180.0 / PI).toFloat()

    val baseAlpha = (255 * 0.30f * lightOfDay.shadowIntensity * (1f - sinA * 0.45f)).toInt()
    val umbraBlur = sRy * (0.01f + sinA * 0.12f)
    val penumbraBlur = sRy * (0.1f + sinA * 0.65f)

    fun Paint.forShadow(alpha: Int, blurRadius: Float): Paint = apply {
        isAntiAlias = true
        color = shadowColor.toArgb()
        this.alpha = alpha
        maskFilter = BlurMaskFilter(blurRadius.coerceAtLeast(0.5f), BlurMaskFilter.Blur.NORMAL)
    }

    rotate(degrees = shadowOvalAngle, pivot = Offset(cx, cy)) {
        drawContext.canvas.nativeCanvas.apply {
            drawOval(
                cx - sRx, cy - sRy, cx + sRx, cy + sRy,
                Paint().forShadow(alpha = (baseAlpha * 0.80f).toInt(), blurRadius = umbraBlur),
            )
            drawOval(
                cx - sRx * (1f + sinA * 0.15f),
                cy - sRy,
                cx + sRx * (1f + sinA * 0.15f),
                cy + sRy * (1f + sinA * 0.35f),
                Paint().forShadow(alpha = (baseAlpha * sinA * 0.40f).toInt(), blurRadius = penumbraBlur),
            )
        }
    }
}
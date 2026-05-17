package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Parámetros pre-calculados para renderizar la sombra de una pieza en volteo.
 *
 * Esta clase separa la lógica matemática (portable) del renderizado específico
 * de plataforma (Matrix + BlurMaskFilter en Android, simplificado en Desktop).
 *
 * ## Algoritmo
 * La sombra usa una matriz de transformación afín que:
 * - Mantiene el tamaño en la dirección de la luz
 * - Comprime en la dirección perpendicular conforme la pieza se acerca al canto
 * - Interpola el centro de la sombra: plano → desplazado, canto → borde
 *
 * @property shadowPath Path a dibujar como sombra
 * @property shadowColor Color base de la sombra
 * @property position Posición donde dibujar (offset para translate)
 * @property radius Radio de la pieza
 * @property umbraBlur Radio de blur para la umbra (sombra principal)
 * @property penumbraBlur Radio de blur para la penumbra (halo exterior)
 * @property umbraAlpha Alpha de la umbra [0, 1]
 * @property penumbraAlpha Alpha de la penumbra [0, 1]
 * @property showPenumbra Si debe dibujarse la penumbra (false si sinA <= 0.05)
 * @property transformMatrix Matriz de transformación 3x3 como FloatArray(9) [Android only]
 */
data class MorphFlipShadowParams(
    val shadowPath: Path,
    val shadowColor: Color,
    val position: Offset,
    val radius: Float,
    val umbraBlur: Float,
    val penumbraBlur: Float,
    val umbraAlpha: Float,
    val penumbraAlpha: Float,
    val showPenumbra: Boolean,
    val transformMatrix: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MorphFlipShadowParams

        if (shadowColor != other.shadowColor) return false
        if (position != other.position) return false
        if (radius != other.radius) return false
        if (umbraBlur != other.umbraBlur) return false
        if (penumbraBlur != other.penumbraBlur) return false
        if (umbraAlpha != other.umbraAlpha) return false
        if (penumbraAlpha != other.penumbraAlpha) return false
        if (showPenumbra != other.showPenumbra) return false
        if (!transformMatrix.contentEquals(other.transformMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shadowColor.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + umbraBlur.hashCode()
        result = 31 * result + penumbraBlur.hashCode()
        result = 31 * result + umbraAlpha.hashCode()
        result = 31 * result + penumbraAlpha.hashCode()
        result = 31 * result + showPenumbra.hashCode()
        result = 31 * result + transformMatrix.contentHashCode()
        return result
    }
}

/**
 * Calcula los parámetros de sombra para una pieza en volteo.
 *
 * Esta función es Kotlin puro y funciona en todas las plataformas.
 * Separa la matemática compleja del renderizado específico de plataforma.
 *
 * @param position Posición de la pieza
 * @param radius Radio de la pieza
 * @param projection Proyección de la forma (para crear paths)
 * @param flipProgress Progreso del volteo [0, 1]
 * @param lightOfDay Configuración de iluminación
 * @param shadowColor Color base de la sombra
 * @param rimFrac Fracción del grosor del canto
 * @return Parámetros listos para renderizar
 */
fun computeMorphFlipShadowParams(
    position: Offset,
    radius: Float,
    projection: MorphShapeProjection,
    flipProgress: Float,
    lightOfDay: LightOfDay,
    shadowColor: Color,
    rimFrac: Float = 0.22f,
): MorphFlipShadowParams {
    val angle = flipProgress * PI.toFloat()
    val scale = cos(angle)
    // 0 cuando la pieza está plana (|scale|=1), 1 cuando está de canto (scale=0).
    val sinA = sqrt(max(0f, 1f - scale * scale))

    val pathSize = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)

    // ── Cálculo de la matriz de transformación ────────────────────────────────
    val distMulti = 1f + sinA * 2.8f

    val fullFacePath = projection.createPath(pathSize, scale = 1f)
    // Para Desktop: getBounds() es suficiente para aproximar
    val fullBounds = fullFacePath.getBounds()
    val sRy = (fullBounds.bottom - radius).coerceAtLeast(1f)

    val lightLen = sqrt(
        lightOfDay.shadowOffsetX * lightOfDay.shadowOffsetX +
                lightOfDay.shadowOffsetY * lightOfDay.shadowOffsetY
    ).coerceAtLeast(0.001f)
    val shadowDirX = lightOfDay.shadowOffsetX / lightLen
    val shadowDirY = lightOfDay.shadowOffsetY / lightLen

    // lxPerp: componente perpendicular de la luz respecto al eje de volteo.
    val flipRad = projection.axisAngleDeg * PI.toFloat() / 180f
    val cosFlip = cos(flipRad)
    val sinFlip = sin(flipRad)
    val lxPerp = abs(lightOfDay.shadowOffsetX * sinFlip - lightOfDay.shadowOffsetY * cosFlip) / lightLen
    val minSRx = sRy * rimFrac * 0.8f
    val sRxAtEdge = minSRx + (sRy - minSRx) * lxPerp
    val sRx = ((1f - sinA) * sRy + sinA * sRxAtEdge).coerceAtLeast(minSRx)

    // Centro interpolado: plano → bajo la pieza desplazado; canto → borde en position.
    val kx = lightOfDay.shadowOffsetX / radius * distMulti
    val ky = lightOfDay.shadowOffsetY / radius * distMulti
    val cxFlat = radius + kx * sRy
    val cyFlat = radius + ky * sRy
    val cxEdge = radius + sRy * shadowDirX
    val cyEdge = radius + sRy * shadowDirY
    val shadowCx = (1f - sinA) * cxFlat + sinA * cxEdge
    val shadowCy = (1f - sinA) * cyFlat + sinA * cyEdge

    // Matriz afín 2D: compresión ⊥ a la luz por factor k, identidad en la dirección de luz.
    val k = sRx / sRy
    val mxx = shadowDirX * shadowDirX + k * shadowDirY * shadowDirY
    val mxy = shadowDirX * shadowDirY * (1f - k)
    val myy = shadowDirY * shadowDirY + k * shadowDirX * shadowDirX

    val transformMatrix = floatArrayOf(
        mxx, mxy, shadowCx - mxx * radius - mxy * radius,
        mxy, myy, shadowCy - mxy * radius - myy * radius,
        0f, 0f, 1f,
    )

    // ── Cálculo de alphas y blur radios ───────────────────────────────────────
    val baseAlpha = 0.30f * lightOfDay.shadowIntensity * (1f - sinA * 0.45f)
    val umbraBlur = (radius * (0.01f + sinA * 0.12f)).coerceAtLeast(0.5f)
    val penumbraBlur = (radius * (0.10f + sinA * 0.65f)).coerceAtLeast(0.5f)
    val umbraAlpha = (baseAlpha * 0.80f).coerceIn(0f, 1f)
    val penumbraAlpha = (baseAlpha * sinA * 0.40f).coerceIn(0f, 1f)
    val showPenumbra = sinA > 0.05f

    return MorphFlipShadowParams(
        shadowPath = fullFacePath,
        shadowColor = shadowColor,
        position = position,
        radius = radius,
        umbraBlur = umbraBlur,
        penumbraBlur = penumbraBlur,
        umbraAlpha = umbraAlpha,
        penumbraAlpha = penumbraAlpha,
        showPenumbra = showPenumbra,
        transformMatrix = transformMatrix,
    )
}

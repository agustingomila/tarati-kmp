package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import com.agustin.tarati.ui.components.game.draw.common.MorphShape
import com.agustin.tarati.ui.components.game.draw.common.ShapeFlipPaths
import com.agustin.tarati.ui.components.game.draw.common.buildGeometry
import com.agustin.tarati.ui.components.game.draw.common.clampedRadius
import com.agustin.tarati.ui.components.game.draw.common.edgePath
import com.agustin.tarati.ui.components.game.draw.common.facePath
import com.agustin.tarati.ui.components.game.draw.common.sharpPath
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection.Companion.horizontalProj
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection.Companion.verticalProj
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// MorphShapeProjection
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Proyección 3D→2D de una [MorphShape] para animar el volteo de una pieza.
 *
 * El eje de volteo puede orientarse en cualquier dirección del plano mediante
 * [axisAngleDeg] (0° = eje horizontal, 90° = eje vertical, cualquier ángulo
 * intermedio o mayor produce ejes diagonales).
 *
 * Los constructores de conveniencia [verticalProj] y [horizontalProj] mantienen la API
 * anterior sin cambios; internamente ambos se reducen a [axisAngleDeg].
 *
 * ## Modelo geométrico (port de morph-shape.html)
 *
 * Sea `axR = axisAngleDeg × π/180`, `cosAx = cos(axR)`, `sinAx = sin(axR)`.
 *
 * La **cara** se proyecta con la matriz affine 2×2:
 * ```
 *   fmA = cosAx² + scale·sinAx²
 *   fmB = (1 − scale)·sinAx·cosAx
 *   fmD = sinAx² + scale·cosAx²
 * ```
 * que comprime la coordenada perpendicular al eje y deja intacta la paralela.
 *
 * El **canto** trabaja siempre en el frame "eje vertical" (edgePath comprende
 * solo la proyección X). Los vértices se pre-rotan por `cantoRot = 90° − axisAngleDeg`
 * antes de llamar a edgePath, y el path resultante se rota por
 * `cantoCanvasAngle = axisAngleDeg − 90°` de vuelta al frame original.
 *
 * @param shape         Forma base.
 * @param axisAngleDeg  Orientación del eje de volteo en el plano (0° = horizontal,
 *                      90° = vertical). Default 90°.
 */
class MorphShapeProjection(
    val shape: MorphShape,
    val axisAngleDeg: Float = 90f,
) {
    /**
     * Constructores de conveniencia para la API anterior.
     * [verticalProj]   → axisAngleDeg = 90°  (comprime X)
     * [horizontalProj] → axisAngleDeg = 0°   (comprime Y)
     */
    companion object {
        fun verticalProj(shape: MorphShape) = MorphShapeProjection(shape, 90f)
        fun horizontalProj(shape: MorphShape) = MorphShapeProjection(shape, 0f)
    }

    // ── Proyección affine de cara ──────────────────────────────────────────────
    // Equivalente a fm = [fmA, fmB, fmB, fmD, tx, ty] del HTML.
    // Retorna FloatArray(9) para ser usado con PathTransform.
    private fun faceMatrix(cx: Float, cy: Float, scale: Float): FloatArray {
        val axR = axisAngleDeg * PI.toFloat() / 180f
        val cosAx = cos(axR)
        val sinAx = sin(axR)
        val fmA = cosAx * cosAx + scale * sinAx * sinAx
        val fmB = (1f - scale) * sinAx * cosAx
        val fmD = sinAx * sinAx + scale * cosAx * cosAx
        // Row-major order para Matrix:
        // [ scaleX  skewX  transX ]
        // [ skewY   scaleY transY ]
        // [ 0       0      1      ]
        return floatArrayOf(
            fmA, fmB, cx * (1f - fmA) - cy * fmB,
            fmB, fmD, cy * (1f - fmD) - cx * fmB,
            0f, 0f, 1f,
        )
    }

    /**
     * Path de la cara SIN proyectar (sin transformación).
     * La proyección 3D se aplica en DrawScope usando scale() en drawMorphFlip.
     *
     * @param scale cos(ángulo): 1 = cara delantera, 0 = canto, −1 = cara trasera.
     *              Este parámetro se mantiene por compatibilidad pero ya no se usa aquí.
     */
    fun createPath(size: Size, scale: Float = 1f): Path {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f * shape.sizeFrac
        val ry = size.height / 2f * shape.sizeFrac
        val path: Path = if (shape.sides <= 1) {
            val r = min(rx, ry)
            Path().apply { addOval(Rect(cx - r, cy - r, cx + r, cy + r)) }
        } else {
            val offsets = shape.computeCenteredVertices(cx, cy, rx, ry)
            val r = clampedRadius(offsets, shape.cornerRadius * shape.sizeFrac)
            val ref = min(rx, ry).toDouble()
            if (r < 0.01f) sharpPath(offsets)
            else facePath(buildGeometry(offsets, r, ref) { i -> shape.curveAt(i) })
        }
        return path
    }

    /** Devuelve esta proyección como un [androidx.compose.ui.graphics.Shape] fijando el [scale]. */
    fun atScale(scale: Float): Shape =
        GenericShape { size, _ -> addPath(createPath(size, scale)) }

    /** true si [scale] corresponde a la cara delantera (scale ≥ 0). */
    fun isFrontFace(scale: Float): Boolean = scale >= 0f

    /**
     * Centroide del polígono en el espacio de coordenadas de path `[0, size] × [0, size]`.
     *
     * Usado en [drawMorphFlip] como pivot del `setScale` del indicador central.
     * Se calcula sobre los vértices sin proyectar (sin aplicar faceMatrix), lo que
     * es exacto para `scale = ±1.0` y produce un drift mínimo para `scale ∈ (−1, 1)`.
     */
    internal fun centroidInPathSpace(size: Size): Offset {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f * shape.sizeFrac
        val ry = size.height / 2f * shape.sizeFrac
        return shape.computeCentroid(cx, cy, rx, ry)
    }

    /**
     * Genera los paths de cara y canto para el renderizado 2.5D completo.
     *
     * **Canto** — port de `drawCantoPoly` / `drawCircleCanto` del HTML:
     *   - Los vértices se pre-rotan por `cantoRot = (90° − axisAngleDeg)` para
     *     que edgePath trabaje siempre con el eje vertical.
     *   - El path resultante se rota por `cantoCanvasAngle = (axisAngleDeg − 90°)`
     *     de vuelta al frame de pantalla.
     *   - Funciona para cualquier [axisAngleDeg], incluyendo ángulos diagonales.
     *
     * **Shift** — `(−ns·rimW/2·sinAx, +ns·rimW/2·cosAx)` centra el conjunto
     * cara + canto alrededor del centro del canvas para cualquier orientación de eje.
     *
     * @param scale    cos(ángulo): 1 = cara delantera, 0 = canto, −1 = cara trasera.
     * @param rimFrac  Grosor del canto como fracción del radio efectivo (0..0.5).
     *                 Ancho real en px = ref × rimFrac × |sin(ángulo)|.
     */
    fun flipPaths(size: Size, scale: Float, rimFrac: Float): ShapeFlipPaths {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f * shape.sizeFrac
        val ry = size.height / 2f * shape.sizeFrac
        val ref = min(rx, ry)
        val rimW = ref * rimFrac * sqrt(max(0f, 1f - scale * scale))
        val ns = if (scale >= 0f) 1f else -1f

        val axR = axisAngleDeg * PI.toFloat() / 180f
        val cosAx = cos(axR)
        val sinAx = sin(axR)

        // shift general: centra cara+canto en cualquier orientación de eje.
        // Equivale a ctx.translate(−shift·sinAx, +shift·cosAx) del HTML.
        val shift: Offset = if (rimW < 0.5f) Offset.Zero
        else Offset(-ns * rimW / 2f * sinAx, +ns * rimW / 2f * cosAx)

        val facePth = createPath(size, scale)
        if (rimW < 0.5f) return ShapeFlipPaths(facePth, null, Offset.Zero)

        // cantoRot:        rota Veff para que edgePath trabaje con eje vertical.
        // cantoCanvasAngle: ángulo de rotación del canto para devolverlo al frame original.
        // Eje vertical (axisAngle=90°): cantoRot=0°, cantoCanvasAngle=0° (sin rotación).
        // Eje horizontal (axisAngle=0°): cantoRot=90°, cantoCanvasAngle=-90° (rotación necesaria).
        val cantoRotRad = (90.0 - axisAngleDeg) * PI.toFloat() / 180f
        val cosCR = cos(cantoRotRad)
        val sinCR = sin(cantoRotRad)
        val cantoCanvasAngleDeg = axisAngleDeg - 90f

        val edgePth: Path = when {
            shape.sides <= 1 -> {
                circleEdgePath(cx, cy, ref, scale, rimW)
            }

            else -> {
                // Pre-rotación de vértices por cantoRot para llevar el eje al vertical.
                val vRaw = shape.computeCenteredVertices(cx, cy, rx, ry)
                val vef = if (abs(cantoRotRad) > 1e-4) {
                    Array(vRaw.size) { i ->
                        val dx = vRaw[i].x - cx
                        val dy = vRaw[i].y - cy
                        Offset((cx + dx * cosCR - dy * sinCR).toFloat(), (cy + dx * sinCR + dy * cosCR).toFloat())
                    }
                } else vRaw
                val r = clampedRadius(vef, shape.cornerRadius * shape.sizeFrac)
                if (r < 0.01f) return ShapeFlipPaths(facePth, null, Offset.Zero)
                val geo = buildGeometry(vef, r, ref.toDouble()) { i -> shape.curveAt(i) }
                edgePath(geo, cx, cy, scale, rimW)
            }
        }
        return ShapeFlipPaths(
            facePth,
            edgePth,
            shift,
            edgeRotationDeg = if (abs(cantoCanvasAngleDeg) > 0.1f) cantoCanvasAngleDeg else 0f,
            faceScale = scale,  // Scale de proyección 3D (cos del ángulo de volteo)
            faceAxisAngleDeg = axisAngleDeg  // Ángulo del eje para orientar el scale
        )
    }

    /**
     * Canto de un círculo proyectado (eje verticalProj, comprime X por [scale]).
     *
     * ctX = cx + scale × re × 4/3 → punto de control de la semicircunferencia
     * comprimida. El canto es la franja entre cara frontal y cara trasera con
     * desplazamiento [rimW] hacia el lado [ns].
     */
    fun circleEdgePath(cx: Float, cy: Float, re: Float, scale: Float, rimW: Float): Path {
        val ns = if (scale >= 0f) 1f else -1f
        val rd = ns * rimW
        val ctX = cx + scale * re * (4f / 3f)
        return Path().apply {
            moveTo(cx, cy - re)
            cubicTo(ctX, cy - re, ctX, cy + re, cx, cy + re)           // semicircunferencia frontal
            lineTo(cx + rd, cy + re)                                     // transversal inferior
            cubicTo(ctX + rd, cy + re, ctX + rd, cy - re, cx + rd, cy - re) // semicircunferencia trasera
            close()                                                      // transversal superior
        }
    }
}
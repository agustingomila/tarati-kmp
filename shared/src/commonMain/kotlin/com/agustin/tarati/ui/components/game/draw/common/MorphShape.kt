package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

// ─────────────────────────────────────────────────────────────────────────────
// MorphShape
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Polígono convexo de N lados con esquinas redondeadas y aristas curvables.
 *
 * @param sides             1 = círculo, 2 = cápsula, ≥ 3 = polígono.
 * @param cornerRadius      Radio de esquinas, clampeado a `rMax` automáticamente.
 * @param rotationDeg       Rotación en grados (0° = primer vértice a la derecha).
 * @param edgeCurveStrength Curvatura uniforme: 0 = recta, > 0 = convexa, < 0 = cóncava.
 * @param edgeCurves        Curvatura por arista (longitud N); prevalece si no vacío.
 * @param sizeFrac          Escala del polígono dentro de su bounding box (default 1.0).
 */
@Immutable
class MorphShape(
    val sides: Int,
    val cornerRadius: Float = 0f,
    val rotationDeg: Float = 0f,
    val edgeCurveStrength: Float = 0f,
    val edgeCurves: FloatArray = floatArrayOf(),
    val sizeFrac: Float = 1.0f,
) : Shape {

    fun createPath(size: Size): Path {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f * sizeFrac
        val ry = size.height / 2f * sizeFrac
        return if (sides <= 1) circlePath(cx, cy, min(rx, ry))
        else buildPath(cx, cy, rx, ry)
    }

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        GenericShape { s, _ -> addPath(createPath(s)) }.createOutline(size, layoutDirection, density)

    private fun computeVertices(cx: Float, cy: Float, rx: Float, ry: Float): Array<Offset> {
        val rotRad = rotationDeg * PI / 180.0
        return Array(sides) { i ->
            val a = rotRad + i * 2.0 * PI / sides
            Offset((cx + rx * cos(a)).toFloat(), (cy + ry * sin(a)).toFloat())
        }
    }

    fun computeCenteredVertices(cx: Float, cy: Float, rx: Float, ry: Float): Array<Offset> {
        val offsets = computeVertices(cx, cy, rx, ry)
        if (sides <= 1) return offsets
        val xMid = (offsets.minOf { it.x } + offsets.maxOf { it.x }) / 2f
        val yMid = (offsets.minOf { it.y } + offsets.maxOf { it.y }) / 2f
        val dx = cx - xMid
        val dy = cy - yMid
        if (dx * dx + dy * dy < 0.01f) return offsets
        return Array(offsets.size) { i -> Offset(offsets[i].x + dx, offsets[i].y + dy) }
    }

    fun computeCentroid(cx: Float, cy: Float, rx: Float, ry: Float): Offset {
        if (sides <= 1) return Offset(cx, cy)
        val offsets = computeCenteredVertices(cx, cy, rx, ry)
        return Offset(
            offsets.sumOf { it.x.toDouble() }.toFloat() / offsets.size,
            offsets.sumOf { it.y.toDouble() }.toFloat() / offsets.size,
        )
    }

    fun curveAt(i: Int): Double =
        (if (edgeCurves.isEmpty()) edgeCurveStrength else edgeCurves[i % edgeCurves.size])
            .toDouble().coerceIn(-1.0, 1.0)

    private fun circlePath(cx: Float, cy: Float, r: Float) = Path().apply {
        addOval(Rect(cx - r, cy - r, cx + r, cy + r))
    }

    private fun buildPath(cx: Float, cy: Float, rx: Float, ry: Float): Path {
        val offsets = computeCenteredVertices(cx, cy, rx, ry)
        val ref = min(rx, ry).toDouble()
        val r = clampedRadius(offsets, cornerRadius * sizeFrac)
        return if (r < 0.01f) sharpPath(offsets)
        else facePath(buildGeometry(offsets, r, ref) { i -> curveAt(i) })
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ShapeFlipPaths
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Paths para el renderizado completo de un volteo 2.5D.
 *
 * @param face Cara de la pieza (sin transformar - aplicar scale en DrawScope)
 * @param edge Canto de la pieza (sin rotar - aplicar rotación en DrawScope)
 * @param shift Desplazamiento para centrar cara+canto
 * @param edgeRotationDeg Ángulo de rotación del canto en grados.
 *        Aplicar con DrawScope.rotate() al dibujar el edge.
 *        0° = sin rotación, ±180° = rotación completa.
 * @param faceScale Factor de escala para proyección 3D de la cara.
 *        Aplicar con DrawScope.scale(scaleX = faceScale, scaleY = 1f).
 *        1f = sin proyección, 0f = completamente plano (canto), -1f = cara trasera.
 * @param faceAxisAngleDeg Ángulo del eje de volteo para orientar correctamente el scale.
 */
data class ShapeFlipPaths(
    val face: Path,
    val edge: Path?,
    val shift: Offset,
    val edgeRotationDeg: Float = 0f,
    val faceScale: Float = 1f,
    val faceAxisAngleDeg: Float = 90f,
)

// ─────────────────────────────────────────────────────────────────────────────
// buildGeometry
// ─────────────────────────────────────────────────────────────────────────────

fun buildGeometry(
    offsets: Array<Offset>,
    r: Float,
    ref: Double,
    curveAt: (Int) -> Double,
): ShapeGeo {
    val n = offsets.size
    val dIn = Array(n) { i -> (offsets[i] - offsets[(i - 1 + n) % n]).normalized() }
    val dOut = Array(n) { i -> (offsets[(i + 1) % n] - offsets[i]).normalized() }
    val alpha = DoubleArray(n) { i ->
        acos((-dIn[i].x * dOut[i].x - dIn[i].y * dOut[i].y).toDouble().coerceIn(-1.0, 1.0))
    }
    val sinAH = DoubleArray(n) { i -> sin(alpha[i] / 2) }
    val hs0 = DoubleArray(n) { i -> (PI - alpha[i]) / 2 }
    val doublesV = DoubleArray(n) { i -> if (sinAH[i] < 1e-4) r.toDouble() else r / sinAH[i] }
    val bi = Array(n) { i -> (Offset(-dIn[i].x, -dIn[i].y) + dOut[i]).normalized() }
    val aC = Array(n) { i -> offsets[i] + bi[i] * doublesV[i].toFloat() }
    val aBis = DoubleArray(n) { i ->
        atan2((offsets[i].y - aC[i].y).toDouble(), (offsets[i].x - aC[i].x).toDouble())
    }

    fun hs(vi: Int, useIncoming: Boolean): Double {
        val edgeIdx = if (useIncoming) (vi - 1 + n) % n else vi
        val raw = hs0[vi] * (1.0 - curveAt(edgeIdx) * r / ref)
        var h = raw.coerceIn(1e-4, 2 * PI - 1e-4)
        for (adj in 0 until n) {
            if (adj == vi) continue
            val dx = (aC[adj].x - aC[vi].x).toDouble()
            val dy = (aC[adj].y - aC[vi].y).toDouble()
            val cd = sqrt(dx * dx + dy * dy)
            if (cd < 1e-9 || cd >= 2.0 * r - 1e-6) continue
            val thetaLim = acos((cd / (2.0 * r)).coerceIn(-1.0 + 1e-9, 1.0))
            val thetaD = atan2(dy, dx)
            fun norm2pi(a: Double): Double = ((a % (2 * PI)) + 2 * PI) % (2 * PI)
            val hA: Double
            val hB: Double
            if (useIncoming) {
                hA = norm2pi(aBis[vi] - thetaD - thetaLim)
                hB = norm2pi(aBis[vi] - thetaD + thetaLim)
            } else {
                hA = norm2pi(thetaD - thetaLim - aBis[vi])
                hB = norm2pi(thetaD + thetaLim - aBis[vi])
            }
            val hLim = when {
                hA > 1e-4 && hA < hB -> hA; hB > 1e-4 -> hB; else -> 1e-4
            }
            if (hLim < h) h = maxOf(1e-4, hLim)
        }
        return h
    }

    val tS = Array(n) { i ->
        val h = hs(i, true)
        Offset((aC[i].x + r * cos(aBis[i] - h)).toFloat(), (aC[i].y + r * sin(aBis[i] - h)).toFloat())
    }
    val tE = Array(n) { i ->
        val h = hs(i, false)
        Offset((aC[i].x + r * cos(aBis[i] + h)).toFloat(), (aC[i].y + r * sin(aBis[i] + h)).toFloat())
    }
    val aSArr = DoubleArray(n) { i ->
        atan2((tS[i].y - aC[i].y).toDouble(), (tS[i].x - aC[i].x).toDouble())
    }
    val aEArr = DoubleArray(n) { i ->
        atan2((tE[i].y - aC[i].y).toDouble(), (tE[i].x - aC[i].x).toDouble())
    }
    return ShapeGeo(n, offsets, r, ref, aC, tS, tE, aSArr, aEArr)
}

// ─────────────────────────────────────────────────────────────────────────────
// facePath
// ─────────────────────────────────────────────────────────────────────────────

fun facePath(geo: ShapeGeo): Path = with(geo) {
    Path().apply {
        moveTo(tE[0].x, tE[0].y)
        for (i in 0 until n) {
            val next = (i + 1) % n
            val edgeStart = tE[i]
            val edgeEnd = tS[next]
            val edgeLen = offsetDist(edgeStart, edgeEnd)
            if (edgeLen < 0.5f) {
                lineTo(edgeEnd.x, edgeEnd.y)
            } else {
                val tanS = tangentAt(edgeStart, aC[i])
                val tanE = tangentAt(edgeEnd, aC[next])
                val h = edgeLen / 3f
                cubicTo(
                    edgeStart.x + h * tanS.x, edgeStart.y + h * tanS.y,
                    edgeEnd.x - h * tanE.x, edgeEnd.y - h * tanE.y,
                    edgeEnd.x, edgeEnd.y,
                )
            }
            addCornerArc(tS[next], tE[next], aC[next], r)
        }
        close()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers privados (todos KMP-compatibles)
// ─────────────────────────────────────────────────────────────────────────────

fun clampedRadius(offsets: Array<Offset>, requestedRadius: Float): Float {
    val n = offsets.size
    val dIn = Array(n) { i -> (offsets[i] - offsets[(i - 1 + n) % n]).normalized() }
    val dOut = Array(n) { i -> (offsets[(i + 1) % n] - offsets[i]).normalized() }
    val alpha = DoubleArray(n) { i ->
        acos((-dIn[i].x * dOut[i].x - dIn[i].y * dOut[i].y).toDouble().coerceIn(-1.0, 1.0))
    }
    val edgeLens = DoubleArray(n) { i -> offsetDist(offsets[i], offsets[(i + 1) % n]).toDouble() }
    var rMax = Double.MAX_VALUE
    for (i in 0 until n) {
        val sinAH = sin(alpha[i] / 2)
        val cosAH = cos(alpha[i] / 2)
        if (sinAH < 1e-4 || cosAH < 1e-6) continue
        val minHalf = min(edgeLens[(i - 1 + n) % n], edgeLens[i]) / 2.0
        rMax = min(rMax, minHalf * sinAH / cosAH)
    }
    if (rMax == Double.MAX_VALUE) {
        val cx = offsets.sumOf { it.x.toDouble() } / n
        val cy = offsets.sumOf { it.y.toDouble() } / n
        rMax = offsets.minOf { sqrt((it.x - cx).let { d -> d * d } + (it.y - cy).let { d -> d * d }) }
    }
    return requestedRadius.coerceIn(0f, (rMax * (1.0 - 1e-4)).toFloat())
}

fun sharpPath(offsets: Array<Offset>): Path = Path().apply {
    moveTo(offsets[0].x, offsets[0].y)
    for (i in 1 until offsets.size) lineTo(offsets[i].x, offsets[i].y)
    close()
}

fun tangentAt(offset: Offset, aC: Offset): Offset {
    val rx = offset.x - aC.x
    val ry = offset.y - aC.y
    val len = sqrt(rx * rx + ry * ry).coerceAtLeast(1e-6f)
    return Offset(-ry / len, rx / len)
}

fun Path.addCornerArc(start: Offset, end: Offset, arcCenter: Offset, r: Float) {
    if (r < 0.01f) {
        lineTo(end.x, end.y); return
    }
    val aStart = atan2((start.y - arcCenter.y).toDouble(), (start.x - arcCenter.x).toDouble())
    val aEnd = atan2((end.y - arcCenter.y).toDouble(), (end.x - arcCenter.x).toDouble())
    var span = aEnd - aStart; if (span <= 0.0) span += 2.0 * PI
    val segs = maxOf(1, ceil(span / (PI / 2)).toInt())
    val dA = span / segs
    repeat(segs) { k ->
        val a0 = aStart + k * dA
        val a1 = aStart + (k + 1) * dA
        val p0 = Offset((arcCenter.x + r * cos(a0)).toFloat(), (arcCenter.y + r * sin(a0)).toFloat())
        val p1 = Offset((arcCenter.x + r * cos(a1)).toFloat(), (arcCenter.y + r * sin(a1)).toFloat())
        val t0 = tangentAt(p0, arcCenter)
        val t1 = tangentAt(p1, arcCenter)
        val hl = (r * (4.0 / 3.0) * tan(dA / 4.0)).toFloat()
        cubicTo(p0.x + hl * t0.x, p0.y + hl * t0.y, p1.x - hl * t1.x, p1.y - hl * t1.y, p1.x, p1.y)
    }
}

fun Offset.normalized(): Offset {
    val len = sqrt(x * x + y * y)
    return if (len < 1e-6f) Offset.Zero else this / len
}

fun offsetDist(a: Offset, b: Offset): Float {
    val dx = b.x - a.x
    val dy = b.y - a.y
    return sqrt(dx * dx + dy * dy)
}
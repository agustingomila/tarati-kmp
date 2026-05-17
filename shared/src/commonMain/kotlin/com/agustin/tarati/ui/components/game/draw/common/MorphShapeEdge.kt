package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Funciones para generar paths de cantos 2.5D en proyecciones de volteo de piezas.
 *
 * Estas funciones crean paths del canto lateral visible durante animaciones de volteo,
 * soportando la proyección verticalProj que comprime el eje X por un factor [scale].
 *
 * ## Uso típico
 * - **edgePath**: para polígonos (N ≥ 2) con esquinas redondeadas
 * - **circleEdgePath**: caso especial optimizado para círculos (N = 1)
 */

/**
 * Path del canto lateral 2.5D para la proyección verticalProj (comprime X por [scale]).
 *
 * IDA   (CCW, offset rdx): perfil exterior del canto de polo-top a polo-bot.
 * VUELTA (CW, sin offset): retrace por la cara frontal de polo-bot a polo-top.
 * Cierre: transversal inferior (lineTo) + transversal superior (close).
 *
 * @param geo Geometría pre-computada del polígono
 * @param cx Centro X del path
 * @param cy Centro Y del path
 * @param scale Factor de compresión horizontal (-1 a 1). Signo determina cara visible.
 * @param rimW Grosor del canto en píxeles
 * @return Path del canto lateral con transversales superior e inferior
 */
@Suppress("NAME_SHADOWING")
fun edgePath(geo: ShapeGeo, cx: Float, cy: Float, scale: Float, rimW: Float): Path {
    val n = geo.n
    val r = geo.r
    val aC = geo.aC
    val tS = geo.tS
    val tE = geo.tE
    val aSArr = geo.aSArr
    val aEArr = geo.aEArr

    val ns = if (scale >= 0f) 1f else -1f
    val rdx = ns * rimW
    val pV: (Offset) -> Offset = { p -> Offset(cx + (p.x - cx) * scale, p.y) }
    val pT: (Offset) -> Offset = { t -> Offset(t.x * scale, t.y) }
    val path = Path()

    // Arco proyectado subdividido en ≤ π/2 por segmento.
    fun projArc(aStart: Double, aEnd: Double, offset: Offset, cw: Boolean, dx: Float) {
        var span = aEnd - aStart
        if (cw) {
            if (span >= 0.0) span -= 2 * PI
        } else {
            if (span <= 0.0) span += 2 * PI
        }
        if (abs(span) < 1e-6) return
        val segs = maxOf(1, ceil(abs(span) / (PI / 2)).toInt())
        val dA = span / segs
        val sign = if (cw) -1f else 1f
        repeat(segs) { k ->
            val a0 = aStart + k * dA
            val a1 = aStart + (k + 1) * dA
            val p0r = Offset((offset.x + r * cos(a0)).toFloat(), (offset.y + r * sin(a0)).toFloat())
            val p1r = Offset((offset.x + r * cos(a1)).toFloat(), (offset.y + r * sin(a1)).toFloat())
            val t0 = pT(tangentAt(p0r, offset))
            val t1 = pT(tangentAt(p1r, offset))
            val hl = (r * (4.0 / 3.0) * tan(abs(dA) / 4.0)).toFloat()
            val pp0 = pV(p0r)
            val pp1 = pV(p1r)
            path.cubicTo(
                pp0.x + dx + sign * hl * t0.x, pp0.y + sign * hl * t0.y,
                pp1.x + dx - sign * hl * t1.x, pp1.y - sign * hl * t1.y,
                pp1.x + dx, pp1.y,
            )
        }
    }

    // Arco completo de vértice idx.
    // reverse=false → IDA CCW con rdx.  reverse=true → VUELTA CW sin offset.
    fun arcVertex(idx: Int, reverse: Boolean) = projArc(
        if (reverse) aEArr[idx] else aSArr[idx],
        if (reverse) aSArr[idx] else aEArr[idx],
        aC[idx], reverse, if (reverse) 0f else rdx,
    )

    // Arco parcial de [from] a [to] con centro C.
    fun halfArc(from: Offset, to: Offset, offset: Offset, reverse: Boolean) {
        val aS = atan2((from.y - offset.y).toDouble(), (from.x - offset.x).toDouble())
        val aE = atan2((to.y - offset.y).toDouble(), (to.x - offset.x).toDouble())
        if (abs(aS - aE) < 1e-6) return
        projArc(aS, aE, offset, reverse, if (reverse) 0f else rdx)
    }

    // Arista bezier proyectada tE[prev] → tS[i] (IDA) o tS[i] → tE[prev] (VUELTA).
    fun edge(prev: Int, i: Int, reverse: Boolean) {
        val from = if (reverse) tS[i] else tE[prev]
        val to = if (reverse) tE[prev] else tS[i]
        val offFrom = if (reverse) aC[i] else aC[prev]
        val offTo = if (reverse) aC[prev] else aC[i]
        val dx = if (reverse) 0f else rdx
        val fp = pV(from)
        val tp = pV(to)
        val sx = fp.x + dx
        val ex = tp.x + dx
        if (sqrt(((ex - sx) * (ex - sx) + (tp.y - fp.y) * (tp.y - fp.y)).toDouble()) < 0.5) {
            path.lineTo(ex, tp.y)
            return
        }
        val h = offsetDist(from, to) / 3f
        val sign = if (reverse) -1f else 1f
        val tFrom = pT(tangentAt(from, offFrom))
        val tTo = pT(tangentAt(to, offTo))
        path.cubicTo(
            sx + sign * h * tFrom.x, fp.y + sign * h * tFrom.y,
            ex - sign * h * tTo.x, tp.y - sign * h * tTo.y,
            ex, tp.y,
        )
    }

    // Punto extremo Y del arco i (isBot=false → min Y, isBot=true → max Y).
    fun arcExtreme(i: Int, isBot: Boolean): Offset {
        val target = if (isBot) PI / 2.0 else -PI / 2.0
        var span = aEArr[i] - aSArr[i]
        if (span < 0.0) span += 2 * PI
        var off = target - aSArr[i]
        if (off < 0.0) off += 2 * PI
        return if (off <= span + 1e-6)
            Offset((aC[i].x + r * cos(target)).toFloat(), (aC[i].y + r * sin(target)).toFloat())
        else if (isBot) {
            if (tS[i].y >= tE[i].y) tS[i] else tE[i]
        } else {
            if (tS[i].y <= tE[i].y) tS[i] else tE[i]
        }
    }

    // Puntos de control bezier de arista i en espacio proyectado.
    fun edgeCPs(i: Int): Array<Offset> {
        val nx = (i + 1) % n
        val fr = tE[i]
        val to = tS[nx]
        val p0 = pV(fr)
        val p3 = pV(to)
        val h = offsetDist(fr, to) / 3f
        val tf = pT(tangentAt(fr, aC[i]))
        val tt = pT(tangentAt(to, aC[nx]))
        return arrayOf(
            p0, Offset(p0.x + h * tf.x, p0.y + h * tf.y),
            Offset(p3.x - h * tt.x, p3.y - h * tt.y), p3
        )
    }

    // Raíces de dBy/dt = 0 (extremos Y del bezier proyectado).
    fun bezierYExtremeT(offsets: Array<Offset>): List<Double> {
        val a = (offsets[1].y - offsets[0].y).toDouble()
        val b = (offsets[2].y - offsets[1].y).toDouble()
        val c = (offsets[3].y - offsets[2].y).toDouble()
        val a2 = a - 2 * b + c
        val b2 = b - a
        val c2 = a
        val res = mutableListOf<Double>()
        if (abs(a2) < 1e-9) {
            if (abs(b2) > 1e-9) {
                val t = -c2 / (2 * b2)
                if (t > 1e-6 && t < 1 - 1e-6) res.add(t)
            }
        } else {
            val disc = b2 * b2 - a2 * c2
            if (disc >= 0.0) {
                val sq = sqrt(disc)
                for (s in doubleArrayOf(-1.0, 1.0)) {
                    val t = (-b2 + s * sq) / a2
                    if (t > 1e-6 && t < 1 - 1e-6) res.add(t)
                }
            }
        }
        return res
    }

    fun bezierAt(offsets: Array<Offset>, t: Double): Offset {
        val f = t.toFloat()
        val mf = 1f - f
        return Offset(
            mf * mf * mf * offsets[0].x + 3 * mf * mf * f * offsets[1].x + 3 * mf * f * f * offsets[2].x + f * f * f * offsets[3].x,
            mf * mf * mf * offsets[0].y + 3 * mf * mf * f * offsets[1].y + 3 * mf * f * f * offsets[2].y + f * f * f * offsets[3].y,
        )
    }

    fun lerp2(a: Offset, b: Offset, t: Double): Offset {
        val f = t.toFloat()
        return Offset(a.x + f * (b.x - a.x), a.y + f * (b.y - a.y))
    }

    // De Casteljau 2ª mitad: B(t) → P[3]. Cursor ya en B(t) + (dx, 0).
    fun secondHalf(offsets: Array<Offset>, t: Double, dx: Float) {
        val m12 = lerp2(offsets[1], offsets[2], t)
        val m23 = lerp2(offsets[2], offsets[3], t)
        val m123 = lerp2(m12, m23, t)
        path.cubicTo(m123.x + dx, m123.y, m23.x + dx, m23.y, offsets[3].x + dx, offsets[3].y)
    }

    // De Casteljau 1ª mitad: P[0] → B(t). Cursor ya en P[0] + (dx, 0).
    fun firstHalf(offsets: Array<Offset>, t: Double, dx: Float) {
        val m01 = lerp2(offsets[0], offsets[1], t)
        val m12 = lerp2(offsets[1], offsets[2], t)
        val m23 = lerp2(offsets[2], offsets[3], t)
        val m012 = lerp2(m01, m12, t)
        val m123 = lerp2(m12, m23, t)
        val bt = lerp2(m012, m123, t)
        path.cubicTo(m01.x + dx, m01.y, m012.x + dx, m012.y, bt.x + dx, bt.y)
    }

    fun revP(offsets: Array<Offset>) = arrayOf(offsets[3], offsets[2], offsets[1], offsets[0])

    // ── Candidatos a polo (top = min Y, bot = max Y) ──────────────────────────

    data class Candidate(val isEdge: Boolean, val idx: Int, val t: Double, val pt: Offset)

    val arcTops = Array(n) { i -> arcExtreme(i, false) }
    val arcBots = Array(n) { i -> arcExtreme(i, true) }
    val candidates = mutableListOf<Candidate>()
    for (i in 0 until n) {
        candidates.add(Candidate(false, i, 0.0, pV(arcTops[i])))
        candidates.add(Candidate(false, i, 0.0, pV(arcBots[i])))
        val cps = edgeCPs(i)
        val yMin = minOf(cps[0].y, cps[3].y)
        val yMax = maxOf(cps[0].y, cps[3].y)
        for (t in bezierYExtremeT(cps)) {
            val pt = bezierAt(cps, t)
            if (pt.y < yMin - 0.5f || pt.y > yMax + 0.5f) candidates.add(Candidate(true, i, t, pt))
        }
    }

    // Buscar polo top (min Y) y polo bot (max Y).
    // Tie-break: preferir candidato con mayor ns·X (más cercano al lado visible).
    var pSC = candidates[0]
    var pEC = candidates[0]
    for (c in candidates) {
        val dS = c.pt.y - pSC.pt.y
        val dE = c.pt.y - pEC.pt.y
        if (dS < -0.5f || (abs(dS) <= 0.5f && ns * c.pt.x > ns * pSC.pt.x)) pSC = c
        if (dE > 0.5f || (abs(dE) <= 0.5f && ns * c.pt.x > ns * pEC.pt.x)) pEC = c
    }

    val poleS = pSC.pt
    val poleE = pEC.pt
    val startOnEdge = pSC.isEdge
    val endOnEdge = pEC.isEdge
    val startArc = if (startOnEdge) (pSC.idx + 1) % n else pSC.idx
    val endArc = pEC.idx
    val startECPs = if (startOnEdge) edgeCPs(pSC.idx) else null
    val endECPs = if (endOnEdge) edgeCPs(pEC.idx) else null

    // Secuencia de arcos de startArc a endArc en orden CCW.
    val arcSeq = mutableListOf<Int>()
    run {
        var cur = startArc
        val limit = n + 2
        while (arcSeq.size <= limit) {
            arcSeq.add(cur)
            if (cur == endArc) break
            cur = (cur + 1) % n
        }
    }
    val singleArcSimple = arcSeq.size == 1 && !startOnEdge && !endOnEdge

    // ── IDA (CCW, offset rdx) ────────────────────────────────────────────────
    path.moveTo(poleS.x + rdx, poleS.y)

    if (singleArcSimple) {
        halfArc(arcTops[startArc], arcBots[startArc], aC[startArc], false)
    } else {
        if (startOnEdge) {
            secondHalf(startECPs!!, pSC.t, rdx)
            if (arcSeq.size > 1 || endOnEdge) arcVertex(startArc, false)
        } else {
            halfArc(arcTops[startArc], tE[startArc], aC[startArc], false)
        }
        for (k in 1 until arcSeq.size) {
            edge(arcSeq[k - 1], arcSeq[k], false)
            if (k < arcSeq.size - 1 || endOnEdge) arcVertex(arcSeq[k], false)
        }
        if (endOnEdge) {
            firstHalf(endECPs!!, pEC.t, rdx)
        } else {
            halfArc(tS[arcSeq.last()], arcBots[arcSeq.last()], aC[arcSeq.last()], false)
        }
    }

    // Transversal inferior: poleE+rdx → poleE.
    path.lineTo(poleE.x, poleE.y)

    // ── VUELTA (CW, sin offset) ───────────────────────────────────────────────
    if (singleArcSimple) {
        halfArc(arcBots[startArc], arcTops[startArc], aC[startArc], true)
    } else {
        val last = arcSeq.last()
        if (endOnEdge) {
            secondHalf(revP(endECPs!!), 1.0 - pEC.t, 0f)
            if (arcSeq.size > 1 || startOnEdge) arcVertex(endArc, true)
        } else {
            halfArc(arcBots[last], tS[last], aC[last], true)
        }
        for (k in arcSeq.size - 1 downTo 1) {
            edge(arcSeq[k - 1], arcSeq[k], true)
            if (arcSeq[k - 1] == startArc) break
            arcVertex(arcSeq[k - 1], true)
        }
        if (startOnEdge) {
            if (arcSeq.size > 1) arcVertex(startArc, true)
            firstHalf(revP(startECPs!!), 1.0 - pSC.t, 0f)
        } else {
            halfArc(tE[startArc], arcTops[startArc], aC[startArc], true)
        }
    }

    // close() cierra la transversal superior: poleS → poleS+rdx.
    path.close()
    return path
}

/**
 * Canto de un círculo proyectado (eje verticalProj, comprime X por [scale]).
 *
 * ctX = cx + scale × re × 4/3 → punto de control de la semicircunferencia
 * comprimida. El canto es la franja entre cara frontal y cara trasera con
 * desplazamiento [rimW] hacia el lado [ns].
 *
 * @param cx Centro X del círculo
 * @param cy Centro Y del círculo
 * @param re Radio efectivo del círculo
 * @param scale Factor de compresión horizontal (-1 a 1). Signo determina cara visible.
 * @param rimW Grosor del canto en píxeles
 * @return Path del canto circular con transversales superior e inferior
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
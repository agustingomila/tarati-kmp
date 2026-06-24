package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.geometry.Offset

/**
 * Geometría pre-computada de un polígono con esquinas redondeadas.
 *
 * Contiene todos los datos necesarios para generar paths 2D (facePath) y
 * paths 2.5D proyectados (edgePath) sin tener que recalcular la geometría.
 *
 * @property n Número de vértices del polígono
 * @property offsets Posiciones de los vértices base
 * @property r Radio de las esquinas redondeadas
 * @property ref Valor de referencia para control de curvatura
 * @property aC Centros de los círculos de esquina
 * @property tS Puntos de entrada al arco de cada esquina
 * @property tE Puntos de salida del arco de cada esquina
 * @property aSArr Ángulos de entrada (atan2) de cada arco
 * @property aEArr Ángulos de salida (atan2) de cada arco
 */
data class ShapeGeo(
    val n: Int,
    private val offsets: Array<Offset>,
    val r: Float,
    private val ref: Double,
    val aC: Array<Offset>,      // centros de los círculos de esquina
    val tS: Array<Offset>,      // puntos de entrada al arco
    val tE: Array<Offset>,      // puntos de salida del arco
    val aSArr: DoubleArray,     // atan2(tS[i] − aC[i])
    val aEArr: DoubleArray,     // atan2(tE[i] − aC[i])
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ShapeGeo

        if (n != other.n) return false
        if (!offsets.contentEquals(other.offsets)) return false
        if (r != other.r) return false
        if (ref != other.ref) return false
        if (!aC.contentEquals(other.aC)) return false
        if (!tS.contentEquals(other.tS)) return false
        if (!tE.contentEquals(other.tE)) return false
        if (!aSArr.contentEquals(other.aSArr)) return false
        if (!aEArr.contentEquals(other.aEArr)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n
        result = 31 * result + offsets.contentHashCode()
        result = 31 * result + r.hashCode()
        result = 31 * result + ref.hashCode()
        result = 31 * result + aC.contentHashCode()
        result = 31 * result + tS.contentHashCode()
        result = 31 * result + tE.contentHashCode()
        result = 31 * result + aSArr.contentHashCode()
        result = 31 * result + aEArr.contentHashCode()
        return result
    }
}
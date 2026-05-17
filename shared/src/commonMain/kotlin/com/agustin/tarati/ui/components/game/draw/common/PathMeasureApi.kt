package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.graphics.Path

/**
 * Abstracción multiplataforma para medir y extraer segmentos de un Path.
 *
 * Esta interfaz permite operaciones avanzadas sobre paths que son necesarias
 * para animaciones complejas como el anillo de selección animado de piezas.
 *
 * ## Implementaciones
 * - **Android**: Usa `android.graphics.PathMeasure` con precisión completa
 * - **Desktop**: Implementación simplificada o aproximada
 *
 * ## Uso típico
 * ```kotlin
 * val measure = PathMeasureApi.create(path, closed = false)
 * val totalLength = measure.length
 * val segment = measure.getSegment(start = 0f, end = totalLength * 0.5f)
 * val (pos, tan) = measure.getPosTan(totalLength * 0.75f)
 * ```
 */
interface PathMeasureApi {
    /**
     * Longitud total del path en píxeles.
     */
    val length: Float

    /**
     * Extrae un segmento del path desde [startDistance] hasta [endDistance].
     *
     * @param startDistance Distancia de inicio en el path (0 a length)
     * @param endDistance Distancia final en el path (0 a length)
     * @param startWithMoveTo Si true, el segmento empieza con moveTo; si false, con lineTo
     * @return Path que representa el segmento extraído, o null si la operación falla
     */
    fun getSegment(
        startDistance: Float,
        endDistance: Float,
        startWithMoveTo: Boolean = true,
    ): Path?

    /**
     * Obtiene la posición y tangente en un punto específico del path.
     *
     * @param distance Distancia desde el inicio del path (0 a length)
     * @return Par de (posición, tangente) donde cada uno es FloatArray(2) con [x, y].
     *         Retorna null si la operación falla o el punto está fuera de rango.
     */
    fun getPosTan(distance: Float): PosTan?
}

/**
 * Crea una instancia de PathMeasureApi para medir el [path] dado.
 *
 * @param path Path a medir
 * @param closed Si el path debe tratarse como cerrado (conectar fin con inicio)
 * @return Instancia de PathMeasureApi específica de la plataforma
 */
expect fun createPathMeasure(path: Path, closed: Boolean = false): PathMeasureApi

/**
 * Resultado de [PathMeasureApi.getPosTan].
 *
 * @property position Posición [x, y] en el path
 * @property tangent Vector tangente unitario [x, y] en ese punto
 */
data class PosTan(
    val position: FloatArray,
    val tangent: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PosTan

        if (!position.contentEquals(other.position)) return false
        if (!tangent.contentEquals(other.tangent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.contentHashCode()
        result = 31 * result + tangent.contentHashCode()
        return result
    }
}
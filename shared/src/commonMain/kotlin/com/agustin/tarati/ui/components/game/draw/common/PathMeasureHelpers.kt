package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.graphics.Path

/**
 * Funciones helper para trabajar con PathMeasureApi de forma más conveniente.
 *
 * Estas extensiones simplifican operaciones comunes y manejan casos edge
 * como valores null o fuera de rango.
 */

/**
 * Mide el path y ejecuta [block] con el PathMeasureApi.
 *
 * Esto es un patrón de uso seguro que garantiza que el PathMeasureApi
 * se crea correctamente antes de usarse.
 *
 * @param closed Si el path debe tratarse como cerrado
 * @param block Bloque a ejecutar con el PathMeasureApi
 * @return Resultado del bloque, o null si falla la medición
 */
inline fun <T> Path.measure(
    closed: Boolean = false,
    block: (PathMeasureApi) -> T,
): T? {
    return try {
        val measure = createPathMeasure(this, closed)
        if (measure.length > 0f) {
            block(measure)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Extrae múltiples segmentos del path, manejando wrapping automático.
 *
 * Esta función es útil para animaciones que requieren segmentos que
 * pueden cruzar el punto 0 del path (como un anillo que se mueve
 * continuamente).
 *
 * @param measure PathMeasureApi para el path
 * @param segments Lista de pares (start, end) en distancia normalizada [0, 1]
 * @return Lista de paths con los segmentos extraídos (puede ser menor que segments si algunos fallan)
 */
fun extractSegments(
    measure: PathMeasureApi,
    segments: List<Pair<Float, Float>>,
): List<Path> {
    val totalLength = measure.length
    if (totalLength <= 0f) return emptyList()

    return segments.mapNotNull { (startNorm, endNorm) ->
        val start = startNorm * totalLength
        val end = endNorm * totalLength

        when {
            // Segmento normal (no cruza el origen)
            start < end -> {
                measure.getSegment(start, end, startWithMoveTo = true)
            }
            // Segmento que cruza el origen (wrap around)
            start > end -> {
                // Parte 1: desde start hasta el final
                val part1 = measure.getSegment(start, totalLength, startWithMoveTo = true)
                // Parte 2: desde el inicio hasta end
                val part2 = measure.getSegment(0f, end, startWithMoveTo = false)

                // Combinar ambas partes
                if (part1 != null && part2 != null) {
                    Path().apply {
                        addPath(part1)
                        addPath(part2)
                    }
                } else {
                    part1 ?: part2
                }
            }
            // start == end, segmento vacío
            else -> null
        }
    }
}

/**
 * Obtiene posición y tangente en una distancia normalizada [0, 1].
 *
 * @param measure PathMeasureApi para el path
 * @param normalizedDistance Distancia normalizada (0 = inicio, 1 = final)
 * @return PosTan con posición y tangente, o null si falla
 */
fun getPosTanNormalized(
    measure: PathMeasureApi,
    normalizedDistance: Float,
): PosTan? {
    val totalLength = measure.length
    if (totalLength <= 0f) return null

    val distance = normalizedDistance.coerceIn(0f, 1f) * totalLength
    return measure.getPosTan(distance)
}

/**
 * Divide el path en N segmentos equidistantes.
 *
 * Útil para crear efectos de animación con múltiples elementos
 * distribuidos uniformemente a lo largo de un path.
 *
 * @param measure PathMeasureApi para el path
 * @param count Número de segmentos a extraer
 * @param segmentLengthFraction Fracción de la longitud total que ocupa cada segmento [0, 1]
 * @return Lista de paths con los segmentos (puede ser menor que count si algunos fallan)
 */
fun distributeSegments(
    measure: PathMeasureApi,
    count: Int,
    segmentLengthFraction: Float = 0.1f,
): List<Path> {
    if (count <= 0) return emptyList()

    val segments = List(count) { i ->
        val centerNorm = i.toFloat() / count
        val halfLen = segmentLengthFraction / 2f
        val startNorm = (centerNorm - halfLen + 1f) % 1f
        val endNorm = (centerNorm + halfLen) % 1f
        startNorm to endNorm
    }

    return extractSegments(measure, segments)
}

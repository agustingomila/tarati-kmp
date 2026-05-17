package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.graphics.Path

/**
 * Implementación Desktop de PathMeasureApi con funcionalidad simplificada.
 *
 * Esta implementación proporciona capacidades básicas de medición de paths
 * sin depender de APIs nativas de Android. Es adecuada para casos donde
 * la precisión absoluta no es crítica o donde las animaciones pueden ser
 * simplificadas.
 *
 * ## Limitaciones
 * - La longitud es una aproximación basada en el bounding box
 * - getSegment retorna null (no implementado)
 * - getPosTan retorna valores aproximados o null
 *
 * ## Impacto Visual
 * - Animaciones de selección pueden ser deshabilitadas o simplificadas
 * - La experiencia de usuario en Desktop puede diferir ligeramente de Android
 */
private class JvmPathMeasure(
    private val path: Path,
    private val closed: Boolean,
) : PathMeasureApi {

    /**
     * Longitud aproximada basada en el perímetro del bounding box.
     * 
     * Esta es una aproximación conservadora. Para un path complejo,
     * la longitud real puede ser mayor. Para paths simples como
     * rectángulos o círculos, la aproximación es razonable.
     */
    override val length: Float by lazy {
        // Para paths complejos, esto es solo una aproximación
        // En un caso ideal, deberíamos iterar sobre todos los segmentos del path,
        // pero eso requiere acceso a la estructura interna del path que no está
        // disponible en la API pública de Compose.

        // Por ahora, retornamos una estimación conservadora.
        // En el peor caso, esto hará que las animaciones sean menos precisas
        // pero no causará crashes.
        val bounds = path.getBounds()
        val width = bounds.width
        val height = bounds.height

        // Aproximación del perímetro: suma de lados si es rectángulo,
        // o circunferencia si es circular
        if (width > 0f && height > 0f) {
            // Para un círculo: 2πr, para rectángulo: 2(w+h)
            // Usamos un promedio ponderado
            val rectPerimeter = 2f * (width + height)
            val avgRadius = (width + height) / 4f
            val circlePerimeter = 2f * Math.PI.toFloat() * avgRadius

            // Promedio que funciona razonablemente para la mayoría de formas
            (rectPerimeter + circlePerimeter) / 2f
        } else {
            0f
        }
    }

    override fun getSegment(
        startDistance: Float,
        endDistance: Float,
        startWithMoveTo: Boolean,
    ): Path? {
        // Implementación no disponible en JVM sin PathMeasure nativo.
        // Retornar null indica que la operación no está soportada.
        // El código que llama debe manejar este caso con un fallback.
        return null
    }

    override fun getPosTan(distance: Float): PosTan? {
        // Implementación aproximada basada en el bounding box.
        // Para animaciones complejas, esto no será preciso, pero permite
        // que el código compile y funcione de forma básica.

        val bounds = path.getBounds()
        val center = floatArrayOf(
            bounds.left + bounds.width / 2f,
            bounds.top + bounds.height / 2f
        )

        // Tangente apuntando hacia la derecha (simplificación)
        val tangent = floatArrayOf(1f, 0f)

        // Posición aproximada en el centro del bounds
        // (en un path real, deberíamos calcular la posición exacta
        // en el contorno basándonos en la distancia)
        return PosTan(position = center, tangent = tangent)
    }
}

actual fun createPathMeasure(path: Path, closed: Boolean): PathMeasureApi {
    return JvmPathMeasure(path, closed)
}

package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import android.graphics.PathMeasure as AndroidPathMeasure

/**
 * Implementación Android de PathMeasureApi usando android.graphics.PathMeasure.
 *
 * Proporciona medición precisa de paths con hardware acceleration cuando está disponible.
 * Esta es la implementación de referencia con precisión completa.
 */
private class AndroidPathMeasure(
    private val measure: AndroidPathMeasure,
) : PathMeasureApi {

    override val length: Float
        get() = measure.length

    override fun getSegment(
        startDistance: Float,
        endDistance: Float,
        startWithMoveTo: Boolean,
    ): Path? {
        val androidPath = android.graphics.Path()
        val success = measure.getSegment(startDistance, endDistance, androidPath, startWithMoveTo)

        return if (success) {
            Path().also { composePath ->
                composePath.asAndroidPath().set(androidPath)
            }
        } else {
            null
        }
    }

    override fun getPosTan(distance: Float): PosTan? {
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        val success = measure.getPosTan(distance, pos, tan)

        return if (success) {
            PosTan(position = pos, tangent = tan)
        } else {
            null
        }
    }
}

actual fun createPathMeasure(path: Path, closed: Boolean): PathMeasureApi {
    val androidPath = path.asAndroidPath()
    val measure = AndroidPathMeasure(androidPath, closed)
    return AndroidPathMeasure(measure)
}
package com.agustin.tarati.ui.components.game.draw.common

import androidx.compose.ui.graphics.Path
import kotlin.math.PI

private class WasmPathMeasure(
    private val path: Path,
) : PathMeasureApi {
    override val length: Float by lazy {
        val bounds = path.getBounds()
        val width = bounds.width
        val height = bounds.height
        if (width > 0f && height > 0f) {
            val rectPerimeter = 2f * (width + height)
            val avgRadius = (width + height) / 4f
            val circlePerimeter = 2f * PI.toFloat() * avgRadius
            (rectPerimeter + circlePerimeter) / 2f
        } else 0f
    }

    override fun getSegment(startDistance: Float, endDistance: Float, startWithMoveTo: Boolean): Path? = null

    override fun getPosTan(distance: Float): PosTan? {
        val bounds = path.getBounds()
        return PosTan(
            position = floatArrayOf(bounds.left + bounds.width / 2f, bounds.top + bounds.height / 2f),
            tangent = floatArrayOf(1f, 0f),
        )
    }
}

actual fun createPathMeasure(path: Path, closed: Boolean): PathMeasureApi = WasmPathMeasure(path)

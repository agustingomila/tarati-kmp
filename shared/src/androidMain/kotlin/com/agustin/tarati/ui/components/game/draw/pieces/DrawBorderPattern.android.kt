package com.agustin.tarati.ui.components.game.draw.pieces

import android.graphics.PathMeasure
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import com.agustin.tarati.ui.components.game.draw.common.MorphShape
import kotlin.math.PI
import kotlin.math.atan2

/**
 * Implementación Android de [drawBorderPattern] usando PathMeasure y asAndroidPath.
 */
actual fun DrawScope.drawBorderPattern(
    projectedFacePath: Path,
    pattern: BorderPattern,
    borderWidth: Float,
    borderColor: Color,
    accentColor: Color,
    projectionScale: Float,
    projectionAxisAngleDeg: Float,
    flatFacePath: Path,
    projectionShift: Offset,
    shape: MorphShape?,
) {
    val rawCenter = flatFacePath.getBounds().center
    val pieceCenter = Offset(
        rawCenter.x + projectionShift.x,
        rawCenter.y + projectionShift.y,
    )
    val projRotDeg = 90f - projectionAxisAngleDeg

    fun DrawScope.withProjection(block: DrawScope.() -> Unit) {
        withTransform({
            if (projRotDeg != 0f) rotate(-projRotDeg, pieceCenter)
            scale(projectionScale, 1f, pieceCenter)
            if (projRotDeg != 0f) rotate(projRotDeg, pieceCenter)
        }) { block() }
    }

    when (pattern) {
        BorderPattern.None ->
            withProjection {
                clipPath(flatFacePath) {
                    drawPath(flatFacePath, color = borderColor, style = Stroke(width = borderWidth * 2f))
                }
            }

        BorderPattern.DoubleRing ->
            withProjection {
                drawDoubleRingBorder(flatFacePath, borderWidth, borderColor, accentColor)
            }

        else -> {
            withProjection {
                clipPath(flatFacePath) {
                    drawPath(flatFacePath, color = borderColor, style = Stroke(width = borderWidth * 2f))
                }
            }

            val spacingNominal = borderWidth * 2.4f
            val measure = PathMeasure(flatFacePath.asAndroidPath(), true)
            val totalLength = measure.length
            if (totalLength < spacingNominal * 2f) return

            val posArr = FloatArray(2)
            val tanArr = FloatArray(2)

            val minPerSide = pattern.minPerSide
            val arcMarginFactor = pattern.arcMarginFactor

            val samples: List<Pair<Float, Float>> = if (shape != null && shape.sides > 1) {
                val sidesCount = shape.sides
                val segmentLen = totalLength / sidesCount
                val arcHalf = shape.cornerRadius * (PI.toFloat() / sidesCount)
                val endMargin = arcHalf * arcMarginFactor
                val straightLen = (segmentLen - endMargin).coerceAtLeast(segmentLen * 0.5f)

                buildList {
                    for (segIdx in 0 until sidesCount) {
                        val straightStart = segIdx * segmentLen
                        val n = (straightLen / spacingNominal).toInt().coerceAtLeast(minPerSide)
                        val spacingReal = straightLen / n
                        for (k in 0 until n) {
                            add(Pair(straightStart + (k + 0.5f) * spacingReal, spacingReal))
                        }
                    }
                }
            } else {
                buildList {
                    var d = spacingNominal * 0.5f
                    while (d < totalLength) {
                        add(Pair(d, spacingNominal))
                        d += spacingNominal
                    }
                }
            }

            withProjection {
                clipPath(flatFacePath) {
                    samples.forEach { (dist, spacingReal) ->
                        if (measure.getPosTan(dist, posArr, tanArr)) {
                            val flatPos = Offset(posArr[0], posArr[1])
                            val flatAngle = atan2(tanArr[1], tanArr[0]) * 180f / PI.toFloat()
                            val cellW: Float? = if (pattern.stretchable) spacingReal else null

                            when (pattern) {
                                BorderPattern.Fishtail -> drawFishtailStamp(
                                    pos = flatPos,
                                    tangentDeg = flatAngle,
                                    borderWidth = borderWidth,
                                    color = accentColor,
                                    cellWidth = cellW,
                                )

                                BorderPattern.Diamonds -> drawDiamondStamp(
                                    pos = flatPos,
                                    tangentDeg = flatAngle,
                                    borderWidth = borderWidth,
                                    color = accentColor,
                                )

                                BorderPattern.Chevron -> drawChevronStamp(
                                    pos = flatPos,
                                    tangentDeg = flatAngle,
                                    borderWidth = borderWidth,
                                    color = accentColor,
                                )

                                BorderPattern.Meander -> drawMeanderStamp(
                                    pos = flatPos,
                                    tangentDeg = flatAngle,
                                    borderWidth = borderWidth,
                                    color = accentColor,
                                    cellWidth = cellW,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
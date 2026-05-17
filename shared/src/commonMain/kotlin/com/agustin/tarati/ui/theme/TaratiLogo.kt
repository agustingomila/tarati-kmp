package com.agustin.tarati.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Themed composable rendition of the Tarati logo.
 *
 * Draws the 6 central regions surrounding A1, the 7 vertices (A1 + B1–B6),
 * and the letters T-A-R-A-T-I centred in each region with their base
 * pointing toward A1. All colors are sourced from the active [BoardPalette]
 * via [getBoardColors], so the logo adapts to any user-selected theme.
 *
 * The geometry mirrors [GameBoard.getPosition] for BRIDGE vertices:
 * ```
 * B[n].angle = (n-1) * (π/3) + π/2   (n = 1..6, 0-indexed here as 0..5)
 * B[n].offset = center + bridgeRadius * (cos(angle), sin(angle))
 * ```
 * where `bridgeRadius` is proportional to half the canvas size.
 *
 * @param size         Canvas size (width = height — the logo is square).
 * @param rotationDeg  Overall rotation in degrees (used for the Splash spin animation).
 * @param modifier     Standard Compose modifier.
 */
@Composable
fun TaratiLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    rotationDeg: Float = 0f,
) {
    val colors = getBoardColors()
    val textMeasurer = rememberTextMeasurer()

    val dotRadiusFactor = 0.047f
    val fontSizeFactor = 0.55f
    val fontVertScale = 0.84f

    // Alternate region colors matching the board pattern
    val regionColors = listOf(
        colors.boardPatternColor3,
        colors.boardPatternColor2,
        colors.boardPatternColor3,
        colors.boardPatternColor2,
        colors.boardPatternColor3,
        colors.boardPatternColor2,
    )

    Canvas(
        modifier = modifier
            .size(size)
            .padding(4.dp)
    ) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val center = Offset(cx, cy)

        // Maximum r that fits the flat-top hexagon in the available canvas:
        //   width  = 2r        → r ≤ width / 2
        //   height = √3 × r   → r ≤ height / √3
        val r = minOf(this.size.width / 2f, this.size.height / sqrt(3f))
        // Vertex dot radius ≈ 4.3% of bridge radius (matches logo.xml 1.952 / 45)
        val dotRadius = r * dotRadiusFactor
        // Font size derived from the physical region size in pixels, converted to
        // SP via density so it scales correctly on all screen sizes and densities.
        // DrawScope implements Density, so toSp() is available directly.
        val fontSize = (r * fontSizeFactor).toSp()

        // B[i] positions: flat-top orientation — arista en la parte superior.
        // Offset 4π/3 (240°) places B[0] at top-left and B[1] at top-right,
        // creating a horizontal top edge and putting region 0 ("T") at the top.
        val bridgeOffsets = (0..5).map { i ->
            val angle = i * (PI / 3).toFloat() + (4f * PI.toFloat() / 3f)
            Offset(
                cx + r * cos(angle),
                cy + r * sin(angle),
            )
        }

        // Apply overall rotation for splash animation
        rotate(degrees = rotationDeg, pivot = center) {

            // ── 6 triangular regions ──────────────────────────────────────────
            (0..5).forEach { i ->
                val b0 = bridgeOffsets[i]
                val b1 = bridgeOffsets[(i + 1) % 6]

                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x, center.y)
                    lineTo(b0.x, b0.y)
                    lineTo(b1.x, b1.y)
                    close()
                }
                drawPath(path = path, color = regionColors[i])

                // ── Letter centred in the region, base toward A1 ─────────────
                val letter = "TARATI"[i].toString()
                drawLetter(
                    letter = letter,
                    regionCenter = Offset(
                        (center.x + b0.x + b1.x) / 3f,
                        (center.y + b0.y + b1.y) / 3f,
                    ),
                    a1 = center,
                    textMeasurer = textMeasurer,
                    style = TextStyle(
                        color = colors.textColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight(1000),
                    ),
                    verticalScale = fontVertScale,
                )
            }

            // ── Bridge vertices B1–B6 ─────────────────────────────────────────
            bridgeOffsets.forEach { pos ->
                drawCircle(
                    color = colors.boardVertexColor,
                    radius = dotRadius,
                    center = pos,
                )
            }

            // ── Centre vertex A1 ──────────────────────────────────────────────
            drawCircle(
                color = colors.boardVertexColor,
                radius = dotRadius,
                center = center,
            )
        }
    }
}

/**
 * Draws [letter] centred at [regionCenter] and rotated so its baseline
 * faces [a1]. The angle is computed as `atan2(a1 - regionCenter)`, giving
 * the direction from the region centroid toward the centre of the board.
 */
private fun DrawScope.drawLetter(
    letter: String,
    regionCenter: Offset,
    a1: Offset,
    textMeasurer: TextMeasurer,
    style: TextStyle,
    verticalScale: Float = 0.85f,
) {
    val measured = textMeasurer.measure(letter, style)
    val tw = measured.size.width.toFloat()
    val th = measured.size.height.toFloat()

    // Angle from region centroid toward A1 (base of letter points to A1)
    val dx = a1.x - regionCenter.x
    val dy = a1.y - regionCenter.y
    // −90° flips the letter so the baseline (bottom) points toward A1.
    // +90° was 180° off — letters appeared inverted with base toward periphery.
    val angleDeg = (atan2(dy, dx) * (180.0 / PI)).toFloat() - 90f

    withTransform({
        // 1. Orient the letter toward A1
        rotate(degrees = angleDeg, pivot = regionCenter)
        // 2. Compress vertically around the letter centre.
        //    scaleX = 1f preserves width; scaleY < 1f squeezes height.
        scale(scaleX = 1f, scaleY = verticalScale, pivot = regionCenter)
        // 3. Position top-left of the text bounding box
        translate(regionCenter.x - tw / 2f, regionCenter.y - th / 2f)
    }) {
        drawText(textLayoutResult = measured)
    }
}
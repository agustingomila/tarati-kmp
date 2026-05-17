package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.theme.BoardColors
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

// ─────────────────────────────────────────────────────────────────────────────
// ShapeFlipCobAnimated — composable base con PALETA
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Anima el volteo de un [CobShape] usando los colores de [boardColors].
 *
 * Usa [drawMorphFlip] que aplica fill, borde, canto, textura y punto central
 * según el [ShapeColorScheme] del [cobShape] y el [CobColor] del frente.
 *
 * @param cobColor     Bando de la cara delantera (reverso = cobColor.opponent).
 * @param boardColors  Paleta activa.
 * @param axisAngleDeg Orientación del eje de volteo (0° = H, 90° = V, libre).
 * @param staticProgress null → animación continua.
 */
@Composable
fun ShapeFlipCobAnimated(
    modifier: Modifier = Modifier,
    cobShape: CobShape,
    cobColor: CobColor,
    boardColors: BoardColors,
    axisAngleDeg: Float = 90f,
    size: Dp = 96.dp,
    staticProgress: Float? = null,
    rimFrac: Float = 0.22f,
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val projection = remember(cobShape.shape, axisAngleDeg) {
        MorphShapeProjection(cobShape.shape, axisAngleDeg)
    }

    if (staticProgress == null) {
        LaunchedEffect(cobShape, axisAngleDeg) {
            while (true) {
                for (step in 0..100) {
                    animatedProgress = step / 100f; delay(30.milliseconds)
                }
                delay(1200.milliseconds); animatedProgress = 0f; delay(400.milliseconds)
            }
        }
    }

    Canvas(modifier = modifier.size(size)) {
        // staticProgress se pasa DIRECTAMENTE al renderer sin pasar por estado.
        // Así cada recomposición con un nuevo staticProgress renderiza en ese ángulo exacto,
        // independientemente de cuándo se compuso el composable por primera vez.
        val renderProgress = staticProgress ?: animatedProgress
        val r = min(this.size.width, this.size.height) / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawMorphFlip(
            position = center,
            radius = r,
            projection = projection,
            flipProgress = renderProgress,
            rimFrac = rimFrac,
            cobShape = cobShape,
            cobColor = cobColor,
            boardColors = boardColors,
        )
    }
}
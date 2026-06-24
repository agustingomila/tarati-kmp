package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.ABSOLUTE
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.animation.AnimatedCob
import com.agustin.tarati.ui.components.game.draw.pieces.ConversionAnimationType
import com.agustin.tarati.ui.components.game.draw.pieces.drawAnimatedCob
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.getBoardColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// ── Upgrade ───────────────────────────────────────────────────────────────────

@Preview(group = "Cob", showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun CobUpgradePreview() {
    val animationState = remember { PreviewAnimationState() }
    val boardColors: BoardColors = getBoardColors()

    LaunchedEffect(Unit) {
        while (true) {
            for (progress in 0..100 step 2) {
                animationState.upgradeProgress = progress / 100f
                delay(80.milliseconds)
            }
            delay(1000.milliseconds)
            animationState.upgradeProgress = 0f
            delay(500.milliseconds)
        }
    }

    val animatedCob = AnimatedCob(
        vertex = A1, cob = Cob(CobColor.BLACK, isUpgraded = true),
        currentPos = A1, targetPos = B1, targetColor = CobColor.WHITE,
        animationProgress = 0f, upgradeProgress = animationState.upgradeProgress, conversionProgress = 0f,
    )
    Box(modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawAnimatedCob(
                position = center, radius = size.minDimension / 4,
                vertex = Vertex(ABSOLUTE, 1), selectedVertex = null,
                animatedCob = animatedCob, colors = boardColors,
            )
        }
    }
}

// ── Conversión ────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun CobConversionPreview() {
    val animationState = remember { PreviewAnimationState() }
    val boardColors: BoardColors = getBoardColors()

    LaunchedEffect(Unit) {
        while (true) {
            animateConversion(animationState)
        }
    }

    val animatedCob = AnimatedCob(
        vertex = A1, cob = Cob(CobColor.WHITE),
        currentPos = A1, targetPos = B1, targetColor = CobColor.WHITE,
        animationProgress = 0f, upgradeProgress = 0f,
        conversionProgress = animationState.conversionProgress, isConverting = animationState.isConverting,
    )
    Box(modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawAnimatedCob(
                position = center, radius = size.minDimension / 4,
                vertex = Vertex(ABSOLUTE, 1), selectedVertex = null,
                animatedCob = animatedCob, colors = boardColors,
            )
        }
    }
}

private suspend fun animateConversion(animationState: PreviewAnimationState) {
    animationState.isConverting = true
    for (progress in 0..100 step 2) {
        animationState.conversionProgress = progress / 100f
        delay(120.milliseconds)
    }
    animationState.isConverting = false
    animationState.conversionProgress = 0f
    delay(2000.milliseconds)
}

// ── Secuencia completa ────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun CobAllAnimationsPreview() {
    val animationState = remember { PreviewAnimationState() }
    val boardColors: BoardColors = getBoardColors()

    LaunchedEffect(Unit) {
        while (true) {
            for (progress in 0..100 step 2) {
                animationState.upgradeProgress = progress / 100f
                delay(80.milliseconds)
            }
            delay(1000.milliseconds)
            animationState.upgradeProgress = 0f
            delay(1000.milliseconds)
            animateConversion(animationState)
        }
    }

    val animatedCob = AnimatedCob(
        vertex = A1, cob = Cob(CobColor.BLACK, isUpgraded = animationState.upgradeProgress > 0.5f),
        currentPos = A1, targetPos = B1, targetColor = CobColor.WHITE,
        animationProgress = 0f, upgradeProgress = animationState.upgradeProgress,
        conversionProgress = animationState.conversionProgress, isConverting = animationState.isConverting,
    )
    Box(modifier = Modifier.size(120.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawAnimatedCob(
                position = center, radius = size.minDimension / 4,
                vertex = Vertex(ABSOLUTE, 1), selectedVertex = null,
                animatedCob = animatedCob, colors = boardColors,
            )
        }
    }
}

// ── Comparador interactivo (FROM_CENTER / FROM_BORDER / FLIP) ─────────────────

@Preview(showBackground = true, widthDp = 300, heightDp = 250)
@Composable
fun CobConversionComparisonPreview() {
    var currentStyle by remember { mutableStateOf(ConversionAnimationType.FROM_CENTER) }
    val animationState = remember { PreviewAnimationState() }
    val boardColors: BoardColors = getBoardColors()

    LaunchedEffect(currentStyle) {
        animationState.conversionProgress = 0f
        animationState.isConverting = true
        for (progress in 0..100 step 2) {
            animationState.conversionProgress = progress / 100f
            delay(100.milliseconds)
        }
        animationState.isConverting = false
        delay(2000.milliseconds)
    }

    Column {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            listOf(
                ConversionAnimationType.FROM_CENTER to "Centro",
                ConversionAnimationType.FROM_BORDER to "Borde",
                ConversionAnimationType.FLIP to "Volteo",
            ).forEach { (style, label) ->
                Text(
                    text = label, fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable { currentStyle = style }
                        .background(if (currentStyle == style) Color.Blue.copy(alpha = 0.3f) else Color.Transparent)
                        .padding(8.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val animatedCob = AnimatedCob(
                    vertex = A1, cob = Cob(CobColor.WHITE),
                    currentPos = A1, targetPos = B1, targetColor = CobColor.BLACK,
                    animationProgress = 0f, upgradeProgress = 0f,
                    conversionProgress = animationState.conversionProgress,
                    isConverting = animationState.isConverting,
                )
                drawAnimatedCob(
                    position = center, radius = size.minDimension / 4,
                    vertex = Vertex(ABSOLUTE, 1), selectedVertex = null,
                    animatedCob = animatedCob, animationType = currentStyle, colors = boardColors,
                )
            }
        }
    }
}

// ── Volteo (FLIP) ─────────────────────────────────────────────────────────────

/**
 * Componente base de preview del volteo.
 *
 * @param staticProgress Si no es null, congela la animación en ese progreso
 *   (0f = pieza plana, 0.5f = completamente de canto, 1f = pieza volteada).
 *   Si es null, corre el ciclo completo de animación.
 */
@Composable
private fun CobFlipAnimated(
    cobColor: CobColor,
    isUpgraded: Boolean,
    vertex: Vertex = A1,
    hourOfDay: Float = 12f,
    staticProgress: Float? = null,
) {
    val animationState = remember { PreviewAnimationState() }
    val boardColors: BoardColors = getBoardColors()

    if (staticProgress == null) {
        LaunchedEffect(Unit) {
            while (true) {
                animationState.isConverting = true
                for (progress in 0..100 step 1) {
                    animationState.conversionProgress = progress / 100f
                    delay(50.milliseconds)
                }
                animationState.isConverting = false
                delay(1500.milliseconds)
                animationState.conversionProgress = 0f
                delay(500.milliseconds)
            }
        }
    }

    val flipProgress = staticProgress ?: animationState.conversionProgress
    val isConverting = staticProgress != null || animationState.isConverting

    Box(modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val animatedCob = AnimatedCob(
                vertex = vertex,
                cob = Cob(cobColor, isUpgraded = isUpgraded),
                currentPos = vertex, targetPos = B1,
                targetColor = cobColor.complement(),
                animationProgress = 0f,
                upgradeProgress = if (isUpgraded) 1f else 0f,
                conversionProgress = flipProgress,
                isConverting = isConverting,
            )
            drawAnimatedCob(
                position = center,
                radius = size.minDimension / 3.5f,
                vertex = Vertex(ABSOLUTE, 1), selectedVertex = null,
                animatedCob = animatedCob,
                animationType = ConversionAnimationType.FLIP,
                colors = boardColors, hourOfDay = hourOfDay,
            )
        }
    }
}

/** Etiqueta con fondo semitransparente para identificar cada celda. */
@Composable
private fun FlipLabel(text: String) {
    Text(
        text = text, fontSize = 9.sp, color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(vertical = 2.dp),
    )
}

/**
 * Las cuatro variantes del FLIP en una grilla 2×2:
 *
 *  Blanca normal  |  Blanca Rok
 *  ───────────────┼───────────────
 *  Negra normal   |  Negra Rok
 */
@Preview(group = "Cob", showBackground = true, widthDp = 300, heightDp = 320)
@Composable
fun CobFlipGridPreview() {
    val cellSize = 140.dp
    val variants = listOf(
        Triple(CobColor.WHITE, false, "Blanca → Negra"),
        Triple(CobColor.WHITE, true, "Blanca Rok → Negra Rok"),
        Triple(CobColor.BLACK, false, "Negra → Blanca"),
        Triple(CobColor.BLACK, true, "Negra Rok → Blanca Rok"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        variants.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { (color, upgraded, label) ->
                    Column(
                        modifier = Modifier.size(cellSize),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            CobFlipAnimated(cobColor = color, isUpgraded = upgraded)
                        }
                        FlipLabel(label)
                    }
                }
            }
        }
    }
}

/**
 * Vista ampliada de las cuatro variantes (normal y Rok, blanca y negra).
 * Examina la geometría 3D, el cambio de cara y el círculo central del Rok.
 */
@Preview(group = "Cob", showBackground = true, widthDp = 400, heightDp = 210)
@Composable
fun CobFlipDetailPreview() {
    val variants = listOf(
        Triple(CobColor.WHITE, false, "Blanca"),
        Triple(CobColor.WHITE, true, "Blanca Rok"),
        Triple(CobColor.BLACK, false, "Negra"),
        Triple(CobColor.BLACK, true, "Negra Rok"),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        variants.forEach { (color, upgraded, label) ->
            Column(
                modifier = Modifier.size(98.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CobFlipAnimated(cobColor = color, isUpgraded = upgraded)
                }
                Spacer(Modifier.height(16.dp))
                FlipLabel(label)
            }
        }
    }
}

// ── Sombra adaptativa: ejes de rotación y horas del día ──────────────────────

/**
 * Descriptor de una celda de sombra para [ShadowRow].
 *
 * @param vertex    Vértice que determina el eje de volteo. Por defecto B2 (≈96°).
 * @param hourOfDay Hora del día que determina la dirección de la luz.
 * @param line1     Primera etiqueta (eje o hora).
 * @param line2     Segunda etiqueta (descripción de la sombra esperada).
 */
data class ShadowCell(
    val vertex: Vertex = B2,
    val hourOfDay: Float = 12f,
    val line1: String,
    val line2: String,
)

data class ShadowCellList(val items: List<ShadowCell>)

/**
 * Fila de celdas de sombra reutilizable.
 *
 * @param staticProgress null → animación completa del volteo;
 *   0.5f → fotograma congelado de canto (sinA = 1, sombra al máximo).
 */
@Composable
private fun ShadowRow(cells: ShadowCellList, staticProgress: Float? = null) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        cells.items.forEach { cell ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.size(100.dp)) {
                    CobFlipAnimated(
                        cobColor = CobColor.WHITE, isUpgraded = false,
                        vertex = cell.vertex, hourOfDay = cell.hourOfDay,
                        staticProgress = staticProgress,
                    )
                }
                Spacer(Modifier.height(16.dp))
                FlipLabel(cell.line1)
                FlipLabel(cell.line2)
            }
        }
    }
}

/**
 * Sombra adaptativa — efecto del eje de volteo con luz diagonal (15:00).
 *
 * A las 15:00 la sombra mundial apunta a ~135° (abajo-derecha):
 *  - D3  ≈ 119° → |lxLocal| ≈ 0.28 → eje ≈ paralelo a la luz    → sombra fina
 *  - B2  ≈  96° → |lxLocal| ≈ 0.63 → eje a ~45° de la luz       → sombra diagonal
 *  - B3  ≈ 234° → |lxLocal| ≈ 0.99 → eje ≈ perpendicular         → sombra ancha
 *
 * Fila superior: animación dinámica — se aprecia el movimiento de la sombra.
 * Fila inferior: fotograma estático en progress = 0.5 (sinA = 1) — verifica
 *   que el borde cercano del óvalo coincida exactamente con el canto.
 */
@Preview(group = "Cob – Sombra adaptativa", showBackground = true, widthDp = 340, heightDp = 265)
@Composable
fun CobFlipRotationAnglesPreview() {
    val cells = ShadowCellList(
        listOf(
            ShadowCell(vertex = D3, hourOfDay = 15f, line1 = "≈119° (paralelo)", line2 = "Sombra fina"),
            ShadowCell(vertex = B2, hourOfDay = 15f, line1 = "≈ 96° (diagonal)", line2 = "Sombra 45°"),
            ShadowCell(vertex = B3, hourOfDay = 15f, line1 = "≈234° (perpend.)", line2 = "Sombra ancha"),
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(4.dp)) {
        ShadowRow(cells)                        // dinámica
        ShadowRow(cells, staticProgress = 0.5f) // estática — alineación del canto
    }
}

/**
 * Sombra adaptativa — efecto de la hora del día con eje fijo B2 (≈96°, ~horizontal E-O).
 *
 *  - 06:00 → sombra hacia O, casi paralela al eje   → |lxLocal| ≈ 0.10 → sombra fina
 *  - 09:00 → sombra diagonal NO                     → |lxLocal| ≈ 0.78 → sombra ~45°
 *  - 12:00 → sombra hacia abajo, ⊥ al eje           → |lxLocal| ≈ 0.99 → sombra ancha
 *
 * Fila superior: animación dinámica.
 * Fila inferior: fotograma estático en progress = 0.5 (sinA = 1).
 */
@Preview(group = "Cob – Sombra adaptativa", showBackground = true, widthDp = 340, heightDp = 265)
@Composable
fun CobFlipShadowHoursPreview() {
    val cells = ShadowCellList(
        listOf(
            ShadowCell(hourOfDay = 6f, line1 = "06:00", line2 = "Sombra fina"),
            ShadowCell(hourOfDay = 9f, line1 = "09:00", line2 = "Sombra ~45°"),
            ShadowCell(line1 = "12:00", line2 = "Sombra ancha"),
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(4.dp)) {
        ShadowRow(cells)                        // dinámica
        ShadowRow(cells, staticProgress = 0.5f) // estática
    }
}

// ── Estado compartido ─────────────────────────────────────────────────────────

class PreviewAnimationState {
    var upgradeProgress: Float by mutableFloatStateOf(0f)
    var conversionProgress: Float by mutableFloatStateOf(0f)
    var isConverting: Boolean by mutableStateOf(false)
}

// ── Extensión local ───────────────────────────────────────────────────────────

/** Color opuesto al actual (solo para uso en previews). */
private fun CobColor.complement(): CobColor = when (this) {
    CobColor.WHITE -> CobColor.BLACK
    CobColor.BLACK -> CobColor.WHITE
}
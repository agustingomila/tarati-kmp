package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.ui.components.game.draw.common.MorphShape
import com.agustin.tarati.ui.components.game.draw.pieces.CobColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.CobShape
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes
import com.agustin.tarati.ui.components.game.draw.pieces.ShapeColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.drawMorphFlip
import com.agustin.tarati.ui.theme.getBoardColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// ─────────────────────────────────────────────────────────────────────────────
// Primitivos compartidos
// ─────────────────────────────────────────────────────────────────────────────

/** Etiqueta con fondo semitransparente — idéntica a [FlipLabel] de CobAnimationPreview. */
@Composable
private fun MorphLabel(text: String) {
    Text(
        text = text, fontSize = 9.sp, color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(vertical = 2.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// MorphFlipAnimated — componente base de preview del volteo poligonal
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Volteo de una pieza poligonal para uso en previews.
 *
 * El eje de volteo se calcula con la misma fórmula áurea que [drawMorphConversionFlip]:
 * `flipAngleDeg = (vertex.hashCode() × 137.508) % 360`.
 * Esto garantiza que los previews de sombra sean idénticos a lo que verá el jugador
 * durante una captura real.
 *
 * @param shape         Forma poligonal a mostrar.
 * @param cobColor      Color del frente (reverso = opponent).
 * @param isUpgraded    Si true, muestra el indicador central de Rok.
 * @param vertex        Determina el eje de volteo (igual que en juego).
 * @param hourOfDay     Hora del día que controla la dirección de la luz.
 * @param staticProgress null → ciclo completo; 0.5 → fotograma de canto (sinA=1).
 */
@Composable
private fun MorphFlipAnimated(
    shape: MorphShape,
    cobColor: CobColor,
    isUpgraded: Boolean = false,
    vertex: Vertex = B2,
    hourOfDay: Float = 12f,
    staticProgress: Float? = null,
    sizeBoxDp: Int = 100,
) {
    var progress by remember { mutableFloatStateOf(staticProgress ?: 0f) }

    if (staticProgress == null) {
        LaunchedEffect(shape, vertex) {
            while (true) {
                for (step in 0..100) {
                    progress = step / 100f; delay(50.milliseconds)
                }
                delay(1500.milliseconds); progress = 0f; delay(500.milliseconds)
            }
        }
    }

    val renderProgress = staticProgress ?: progress

    // Eje determinista por vértice — misma fórmula que drawMorphConversionFlip.
    val flipAngleDeg = (vertex.hashCode() * 137.508f) % 360f
    val projection = remember(shape, flipAngleDeg) {
        MorphShapeProjection(shape, flipAngleDeg)
    }
    val cobShape = remember(shape, isUpgraded) {
        CobShape(
            shape = shape,
            colorScheme = if (isUpgraded) CobColorScheme.Default
            else ShapeColorScheme { cobColor: CobColor, boardColors ->
                CobColorScheme.Default.resolve(cobColor, boardColors).copy(center = null)
            },
        )
    }

    val boardColors = getBoardColors()
    Box(modifier = Modifier.size(sizeBoxDp.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawMorphFlip(
                position = center,
                radius = size.minDimension / 3.5f,
                projection = projection,
                flipProgress = renderProgress,
                cobShape = cobShape,
                cobColor = cobColor,
                boardColors = boardColors,
                hourOfDay = hourOfDay,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ShadowCell / ShadowRow — igual que en CobAnimationPreview
// ─────────────────────────────────────────────────────────────────────────────

data class MorphShadowCell(
    val shape: MorphShape,
    val vertex: Vertex = B2,
    val hourOfDay: Float = 12f,
    val line1: String,
    val line2: String,
)

/**
 * Wrapper estable para la lista de celdas de sombra.
 *
 * [List] es inestable para el compilador de Compose (porque [MutableList] implementa [List]).
 * El mismo patrón que usa [PaletteList] en el proyecto.
 */
data class MorphShadowCellList(val items: List<MorphShadowCell>)

@Composable
private fun MorphShadowRow(
    cells: MorphShadowCellList,
    cobColor: CobColor = CobColor.WHITE,
    staticProgress: Float? = null,
) {
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
                    MorphFlipAnimated(
                        shape = cell.shape,
                        cobColor = cobColor,
                        vertex = cell.vertex,
                        hourOfDay = cell.hourOfDay,
                        staticProgress = staticProgress,
                    )
                }
                Spacer(Modifier.height(14.dp))
                MorphLabel(cell.line1)
                MorphLabel(cell.line2)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 1 — Efecto del eje de volteo (mismos vértices que el preview circular)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Sombra proyectada — efecto del eje de volteo con luz diagonal (15:00).
 *
 * Mismos tres vértices que [CobFlipRotationAnglesPreview]:
 *  - D3  ≈ 119° → eje ≈ paralelo a la luz    → sombra fina
 *  - B2  ≈  96° → eje a ~45° de la luz       → sombra diagonal
 *  - B3  ≈ 234° → eje ≈ perpendicular         → sombra ancha
 *
 * Forma: Hexágono (referencia visual — fill similar al círculo).
 *
 * Fila superior: animación dinámica.
 * Fila inferior: fotograma estático en progress = 0.5 (sinA = 1).
 */
@Preview(group = "Morph – Sombra proyectada", showBackground = true, widthDp = 340, heightDp = 280)
@Composable
fun MorphFlipRotationAnglesPreview() {
    val shape = PieceTypes.Hexagon.shape
    val cells = MorphShadowCellList(
        listOf(
            MorphShadowCell(shape, D3, 15f, "≈119° (paralelo)", "Sombra fina"),
            MorphShadowCell(shape, B2, 15f, "≈ 96° (diagonal)", "Sombra 45°"),
            MorphShadowCell(shape, B3, 15f, "≈234° (perpend.)", "Sombra ancha"),
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(4.dp)) {
        MorphShadowRow(cells)
        MorphShadowRow(cells, staticProgress = 0.5f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 2 — Efecto de la hora del día (mismo eje B2 que el preview circular)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Sombra proyectada — efecto de la hora del día con eje fijo B2 (≈96°).
 *
 * Mismas horas que [CobFlipShadowHoursPreview]:
 *  - 06:00 → sombra hacia O, casi paralela al eje → sombra fina
 *  - 09:00 → sombra diagonal NO                   → sombra ~45°
 *  - 12:00 → sombra hacia abajo, ⊥ al eje         → sombra ancha
 *
 * Forma: Hexágono.
 */
@Preview(group = "Morph – Sombra proyectada", showBackground = true, widthDp = 340, heightDp = 280)
@Composable
fun MorphFlipShadowHoursPreview() {
    val shape = PieceTypes.Hexagon.shape
    val cells = MorphShadowCellList(
        listOf(
            MorphShadowCell(shape, B2, 6f, "06:00", "Sombra fina"),
            MorphShadowCell(shape, B2, 9f, "09:00", "Sombra ~45°"),
            MorphShadowCell(shape, B2, line1 = "12:00", line2 = "Sombra ancha"),
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(4.dp)) {
        MorphShadowRow(cells)
        MorphShadowRow(cells, staticProgress = 0.5f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 3 — Comparativa entre formas (misma hora, mismo eje)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Comparativa de sombra entre todas las formas poligonales.
 *
 * Eje B3 (≈234°, perpendicular a la luz de las 15:00 → sombra ancha) para que
 * la proyección sea máxima y las diferencias entre formas sean visibles.
 *
 * Fila superior: animación dinámica.
 * Fila inferior: fotograma de canto (sinA = 1).
 */
@Preview(group = "Morph – Sombra proyectada", showBackground = true, widthDp = 420, heightDp = 560)
@Composable
fun MorphFlipAllShapesShadowPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp),
    ) {
        // 3 formas por fila para caber en 420dp
        PIECE_TYPES.chunked(3).forEach { rowPairs ->
            val cells = MorphShadowCellList(rowPairs.map { pieceType ->
                MorphShadowCell(pieceType.shape, B3, 15f, localizedString(pieceType.nameRes), "B3 15:00")
            })
            MorphShadowRow(cells)
            MorphShadowRow(cells, staticProgress = 0.5f)
            Spacer(Modifier.height(4.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 4 — Triángulo: detalle de centrado de sombra
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Verifica que la sombra del triángulo esté anclada en su poleY correcto
 * (vértice inferior de la forma centrada, no el bounding box superior).
 *
 * Tres ejes (fila dinámica + fila estática) igual que el preview circular.
 */
@Preview(group = "Morph – Sombra proyectada", showBackground = true, widthDp = 340, heightDp = 280)
@Composable
fun MorphFlipTriangleShadowPreview() {
    val shape = PieceTypes.Triangle.shape
    val cells = MorphShadowCellList(
        listOf(
            MorphShadowCell(shape, D3, 15f, "≈119° (paralelo)", "Sombra fina"),
            MorphShadowCell(shape, B2, 15f, "≈ 96° (diagonal)", "Sombra 45°"),
            MorphShadowCell(shape, B3, 15f, "≈234° (perpend.)", "Sombra ancha"),
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(4.dp)) {
        MorphShadowRow(cells)
        MorphShadowRow(cells, staticProgress = 0.5f)
    }
}
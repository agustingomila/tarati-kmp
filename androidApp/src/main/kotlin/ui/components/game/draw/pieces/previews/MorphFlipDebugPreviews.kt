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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.pieces.BorderPattern
import com.agustin.tarati.ui.components.game.draw.pieces.CenterMotif
import com.agustin.tarati.ui.components.game.draw.pieces.CobColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.CobShape
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection
import com.agustin.tarati.ui.components.game.draw.pieces.PieceType
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes
import com.agustin.tarati.ui.components.game.draw.pieces.drawMorphFlip
import com.agustin.tarati.ui.theme.getBoardColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// ─────────────────────────────────────────────────────────────────────────────
// Helpers compartidos
// ─────────────────────────────────────────────────────────────────────────────

/** Etiqueta con fondo semitransparente. */
@Composable
private fun DebugLabel(text: String, fontSize: Int = 9) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(vertical = 2.dp),
    )
}

/** Etiqueta monoespacio para valores numéricos. */
@Composable
private fun DebugValueLabel(text: String) {
    Text(
        text = text,
        fontSize = 8.sp,
        color = Color(0xFF6366F1),
        textAlign = TextAlign.Center,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** Eje de volteo derivado del hash del vértice. */
private fun flipAxisForVertex(vertex: Vertex): Float =
    (vertex.hashCode() * 137.508f) % 360f

/** [CobShape] del [pieceType]. Rok incluye motivo central; Cob lo omite. */
private fun cobShapeFor(pieceType: PieceType, isUpgraded: Boolean): CobShape =
    if (isUpgraded) {
        CobShape(
            shape = pieceType.shape,
            colorScheme = CobColorScheme.Default,
            borderPattern = pieceType.borderPattern,
            centerMotif = pieceType.centerMotif,
        )
    } else {
        CobShape(
            shape = pieceType.shape,
            colorScheme = { cobColor, boardColors ->
                CobColorScheme.Default.resolve(cobColor, boardColors).copy(center = null)
            },
            borderPattern = pieceType.borderPattern,
            centerMotif = CenterMotif.None,
        )
    }

// ─────────────────────────────────────────────────────────────────────────────
// Renderizado base — drawMorphFlip aislado
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Renderiza un frame de [drawMorphFlip] en un canvas cuadrado.
 *
 * @param pieceType   Tipo de pieza con su guarda y motivo central.
 * @param cobColor    Color del frente (reverso = opponent).
 * @param isUpgraded  Si true, muestra el motivo central del PieceType.
 * @param vertex      Determina el eje de volteo.
 * @param progress    0f = cara delantera, 0.5f = canto, 1f = cara trasera.
 * @param sizeDp      Tamaño del canvas.
 * @param hourOfDay   Hora del día (afecta dirección de la luz).
 */
@Composable
private fun FlipFrame(
    pieceType: PieceType,
    cobColor: CobColor,
    isUpgraded: Boolean,
    vertex: Vertex,
    progress: Float,
    sizeDp: Int,
    hourOfDay: Float = 12f,
) {
    val axisAngleDeg = flipAxisForVertex(vertex)
    val projection = remember(pieceType.shape, axisAngleDeg) {
        MorphShapeProjection(pieceType.shape, axisAngleDeg)
    }
    val cobShape = remember(pieceType, isUpgraded) {
        cobShapeFor(pieceType, isUpgraded)
    }
    val boardColors = getBoardColors()

    Box(modifier = Modifier.size(sizeDp.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawMorphFlip(
                position = center,
                radius = size.minDimension / 3.5f,
                projection = projection,
                flipProgress = progress,
                cobShape = cobShape,
                cobColor = cobColor,
                boardColors = boardColors,
                hourOfDay = hourOfDay,
            )
        }
    }
}

/**
 * Versión animada del [FlipFrame].
 *
 * @param cycleDurationMs Duración del ciclo completo de volteo en milisegundos.
 * @param restDurationMs  Pausa al final de cada ciclo antes de reiniciar.
 */
@Composable
private fun FlipFrameAnimated(
    pieceType: PieceType,
    cobColor: CobColor,
    isUpgraded: Boolean,
    vertex: Vertex,
    sizeDp: Int,
    cycleDurationMs: Long = 20_000L,
    restDurationMs: Long = 2_000L,
    hourOfDay: Float = 12f,
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(pieceType, vertex, cycleDurationMs) {
        val steps = 200
        val stepDelay = cycleDurationMs / steps
        while (true) {
            for (step in 0..steps) {
                progress = step / steps.toFloat()
                delay(stepDelay.milliseconds)
            }
            delay(restDurationMs.milliseconds)
            progress = 0f
            delay(500.milliseconds)
        }
    }

    FlipFrame(
        pieceType = pieceType,
        cobColor = cobColor,
        isUpgraded = isUpgraded,
        vertex = vertex,
        progress = progress,
        sizeDp = sizeDp,
        hourOfDay = hourOfDay,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Datos auxiliares
// ─────────────────────────────────────────────────────────────────────────────

/** Piezas poligonales con guarda y motivo visibles. */
private val POLYGONAL_PIECES = listOf(
    PieceTypes.Triangle,
    PieceTypes.Square,
    PieceTypes.Pentagon,
    PieceTypes.Hexagon,
    PieceTypes.Diamond,
    PieceTypes.Capsule,
)

/** Progress uniformemente distribuidos en el ciclo de volteo. */
private val DEBUG_FRAMES_9 = listOf(
    0.00f, 0.125f, 0.25f, 0.375f, 0.50f, 0.625f, 0.75f, 0.875f, 1.00f,
)

private data class PatternSample(val pattern: BorderPattern, val label: String)

private val PATTERN_SAMPLES = listOf(
    PatternSample(BorderPattern.None, "None"),
    PatternSample(BorderPattern.DoubleRing, "DoubleRing"),
    PatternSample(BorderPattern.Fishtail, "Fishtail"),
    PatternSample(BorderPattern.Diamonds, "Diamonds"),
    PatternSample(BorderPattern.Chevron, "Chevron"),
    PatternSample(BorderPattern.Meander, "Meander"),
)

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 1 — Slow-motion, todos los tipos de pieza (Cob)
// ═══════════════════════════════════════════════════════════════════════════════

/** Grilla 2×3 de las piezas poligonales (Cob), con ciclo de 20s. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 380,
    heightDp = 380,
)
@Composable
fun MorphFlipSlowMotionAllPiecesPreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Slow-motion · todas las piezas (Cob) · ciclo 20s",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        POLYGONAL_PIECES.chunked(3).forEach { rowPieces ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowPieces.forEach { pieceType ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FlipFrameAnimated(
                            pieceType = pieceType,
                            cobColor = CobColor.WHITE,
                            isUpgraded = false,
                            vertex = B2,
                            sizeDp = 110,
                            cycleDurationMs = 20_000L,
                        )
                        Spacer(Modifier.height(2.dp))
                        DebugLabel(pieceType.id)
                        DebugValueLabel("border=${pieceType.borderPattern::class.simpleName}")
                    }
                }
                repeat(3 - rowPieces.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 2 — Slow-motion, todos los tipos de pieza (Rok)
// ═══════════════════════════════════════════════════════════════════════════════

/** Grilla 2×3 de las piezas poligonales (Rok, con motivo central), con ciclo de 20s. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 380,
    heightDp = 360,
)
@Composable
fun MorphFlipSlowMotionAllPiecesUpgradedPreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Slow-motion · todas las piezas (Rok) · ciclo 20s",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        POLYGONAL_PIECES.chunked(3).forEach { rowPieces ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowPieces.forEach { pieceType ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FlipFrameAnimated(
                            pieceType = pieceType,
                            cobColor = CobColor.WHITE,
                            isUpgraded = true,
                            vertex = B2,
                            sizeDp = 110,
                            cycleDurationMs = 20_000L,
                        )
                        Spacer(Modifier.height(2.dp))
                        DebugLabel(pieceType.id)
                        DebugValueLabel("motif=${pieceType.centerMotif::class.simpleName}")
                    }
                }
                repeat(3 - rowPieces.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 3 — Grilla de frames estáticos (Cob)
// ═══════════════════════════════════════════════════════════════════════════════

/** Una fila por pieza × 9 frames fijos (progress 0.00, 0.125, …, 1.00). */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 560,
    heightDp = 400,
)
@Composable
fun MorphFlipStaticFrameGridPreview() {
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Frame grid · Cob · progress ∈ [0, 1]",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.width(50.dp))
            DEBUG_FRAMES_9.forEach { p ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    DebugValueLabel("%.3f".format(p))
                }
            }
        }

        POLYGONAL_PIECES.forEach { pieceType ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pieceType.id,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.width(50.dp),
                )
                DEBUG_FRAMES_9.forEach { progress ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        FlipFrame(
                            pieceType = pieceType,
                            cobColor = CobColor.WHITE,
                            isUpgraded = false,
                            vertex = B2,
                            progress = progress,
                            sizeDp = 48,
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 4 — Grilla de frames estáticos (Rok)
// ═══════════════════════════════════════════════════════════════════════════════

/** Frame grid del Rok: cuerpo, borde y motivo central en cada progress. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 560,
    heightDp = 400,
)
@Composable
fun MorphFlipStaticFrameGridUpgradedPreview() {
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Frame grid · Rok · progress ∈ [0, 1]",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.width(50.dp))
            DEBUG_FRAMES_9.forEach { p ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    DebugValueLabel("%.3f".format(p))
                }
            }
        }

        POLYGONAL_PIECES.forEach { pieceType ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pieceType.id,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.width(50.dp),
                )
                DEBUG_FRAMES_9.forEach { progress ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        FlipFrame(
                            pieceType = pieceType,
                            cobColor = CobColor.WHITE,
                            isUpgraded = true,
                            vertex = B2,
                            progress = progress,
                            sizeDp = 48,
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 5 — Triángulo aislado
// ═══════════════════════════════════════════════════════════════════════════════

/** Triángulo + Chevron en grande, Cob y Rok lado a lado, ciclo de 30s. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 380,
    heightDp = 260,
)
@Composable
fun MorphFlipTriangleIsolatedPreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Triángulo · Chevron · ciclo 30s",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlipFrameAnimated(
                    pieceType = PieceTypes.Triangle,
                    cobColor = CobColor.WHITE,
                    isUpgraded = false,
                    vertex = B2,
                    sizeDp = 160,
                    cycleDurationMs = 30_000L,
                )
                Spacer(Modifier.height(4.dp))
                DebugLabel("Cob")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlipFrameAnimated(
                    pieceType = PieceTypes.Triangle,
                    cobColor = CobColor.WHITE,
                    isUpgraded = true,
                    vertex = B2,
                    sizeDp = 160,
                    cycleDurationMs = 30_000L,
                )
                Spacer(Modifier.height(4.dp))
                DebugLabel("Rok")
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 6 — Aislamiento por BorderPattern
// ═══════════════════════════════════════════════════════════════════════════════

/** Hexágono con cada una de las seis [BorderPattern], ciclo de 20s. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 380,
    heightDp = 340,
)
@Composable
fun MorphFlipBorderPatternIsolationPreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Aislamiento por BorderPattern · Hexágono · ciclo 20s",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        PATTERN_SAMPLES.chunked(3).forEach { rowPatterns ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowPatterns.forEach { sample ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val hexWithPattern = remember(sample.pattern) {
                            PieceTypes.Hexagon.copy(
                                borderPattern = sample.pattern,
                                centerMotif = CenterMotif.None,
                            )
                        }
                        FlipFrameAnimated(
                            pieceType = hexWithPattern,
                            cobColor = CobColor.WHITE,
                            isUpgraded = false,
                            vertex = B2,
                            sizeDp = 110,
                            cycleDurationMs = 20_000L,
                        )
                        Spacer(Modifier.height(2.dp))
                        DebugLabel(sample.label)
                    }
                }
                repeat(3 - rowPatterns.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Preview 7 — Comparación WHITE vs BLACK en slow-motion
// ═══════════════════════════════════════════════════════════════════════════════

/** Triángulo + Chevron, WHITE y BLACK lado a lado, ciclo de 25s. */
@Preview(
    group = "MorphFlip Debug",
    showBackground = true,
    backgroundColor = 0xFF1E1E1E,
    widthDp = 380,
    heightDp = 240,
)
@Composable
fun MorphFlipTriangleWhiteVsBlackPreview() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Triángulo · WHITE vs BLACK · ciclo 25s",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlipFrameAnimated(
                    pieceType = PieceTypes.Triangle,
                    cobColor = CobColor.WHITE,
                    isUpgraded = true,
                    vertex = B2,
                    sizeDp = 140,
                    cycleDurationMs = 25_000L,
                )
                Spacer(Modifier.height(4.dp))
                DebugLabel("WHITE front")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlipFrameAnimated(
                    pieceType = PieceTypes.Triangle,
                    cobColor = CobColor.BLACK,
                    isUpgraded = true,
                    vertex = B2,
                    sizeDp = 140,
                    cycleDurationMs = 25_000L,
                )
                Spacer(Modifier.height(4.dp))
                DebugLabel("BLACK front")
            }
        }
    }
}
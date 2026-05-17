package com.agustin.tarati.ui.components.game.draw.pieces.previews

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Path
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.common.MorphShape
import com.agustin.tarati.ui.components.game.draw.pieces.CobColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.CobShape
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection
import com.agustin.tarati.ui.components.game.draw.pieces.PieceType
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Capsule
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Circle
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Diamond
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Hexagon
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Pentagon
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Square
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes.Triangle
import com.agustin.tarati.ui.components.game.draw.pieces.ShapeColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.ShapeFlipCobAnimated
import com.agustin.tarati.ui.components.game.draw.pieces.drawMorphCob
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.getBoardColors
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

// ─────────────────────────────────────────────────────────────────────────────
// Constantes y helpers compartidos
// ─────────────────────────────────────────────────────────────────────────────

/** Color fijo del canto en previews geométricos (sin paleta). */
private val EDGE_COLOR = Color(0xFF6B6B6B)

// ─────────────────────────────────────────────────────────────────────────────
// ShapeFlipAnimated — composable base GEOMÉTRICO (colores explícitos)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Anima el volteo de una [MorphShape] con colores explícitos (sin paleta).
 * Útil para verificar geometría pura: cara en [colorFront]/[colorBack], canto en [EDGE_COLOR].
 */
@Composable
private fun ShapeFlipAnimated(
    modifier: Modifier = Modifier,
    shape: MorphShape,
    axisAngleDeg: Float = 90f,
    colorFront: Color,
    colorBack: Color,
    size: Dp = 96.dp,
    staticProgress: Float? = null,
    rimFrac: Float = 0.22f,
) {
    var flipProgress by remember { mutableFloatStateOf(staticProgress ?: 0f) }
    val projection = remember(shape, axisAngleDeg) { MorphShapeProjection(shape, axisAngleDeg) }

    if (staticProgress == null) {
        LaunchedEffect(shape, axisAngleDeg) {
            while (true) {
                for (step in 0..100) {
                    flipProgress = step / 100f; delay(30.milliseconds)
                }
                delay(1200.milliseconds); flipProgress = 0f; delay(400.milliseconds)
            }
        }
    }

    Canvas(modifier = modifier.size(size)) {
        val angle = flipProgress * PI.toFloat()
        val scale = cos(angle)
        val faceColor = if (projection.isFrontFace(scale)) colorFront else colorBack
        val paths = projection.flipPaths(this.size, scale, rimFrac)
        val shadowR = min(this.size.width, this.size.height) * 0.08f

        // Sombra — copia del path para no mutar el original.
        val shadowAlpha = (0.28f * (1f - 0.4f * abs(scale))).coerceAtLeast(0.05f)
        val shadowPath = Path(paths.face.asAndroidPath())
            .also { it.offset(0f, shadowR * 0.9f) }
        drawContext.canvas.nativeCanvas.drawPath(shadowPath, Paint().apply {
            isAntiAlias = true
            color = Color.Black.copy(alpha = shadowAlpha).toArgb()
            maskFilter = BlurMaskFilter(shadowR * 1.8f, BlurMaskFilter.Blur.NORMAL)
        })

        translate(paths.shift.x, paths.shift.y) {
            paths.edge?.let { drawPath(it, color = EDGE_COLOR) }
            drawPath(paths.face, color = faceColor)
        }
    }
}

/**
 * Muestra una pieza estática con [drawMorphCob] usando [boardColors].
 */
@Composable
fun ShapeCobStatic(
    cobShape: CobShape,
    cobColor: CobColor,
    boardColors: BoardColors,
    size: Dp = 80.dp,
) {
    Canvas(modifier = Modifier.size(size)) {
        val r = min(this.size.width, this.size.height) / 2f * cobShape.shape.sizeFrac
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawMorphCob(
            position = center,
            radius = r,
            cobShape = cobShape,
            cobColor = cobColor,
            boardColors = boardColors,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Catálogos
// ─────────────────────────────────────────────────────────────────────────────

private data class FlipSubject(
    val shape: MorphShape,
    val label: String,
    val colorFront: Color,
    val colorBack: Color,
)

private val flipSubjects = listOf(
    FlipSubject(
        MorphShape(sides = 1), "Círculo",
        Color(0xFF6B4EE6), Color(0xFFE64E88)
    ),
    FlipSubject(
        MorphShape(sides = 3, cornerRadius = 14f, rotationDeg = -90f), "Triángulo",
        Color(0xFF4EE68C), Color(0xFFB84EE6)
    ),
    FlipSubject(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 45f), "Cuadrado",
        Color(0xFF4ECBE6), Color(0xFFE6A44E)
    ),
    FlipSubject(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 0f), "Diamante",
        Color(0xFF4EE68C), Color(0xFFB84EE6)
    ),
    FlipSubject(
        MorphShape(sides = 5, cornerRadius = 12f, rotationDeg = -90f), "Pentágono",
        Color(0xFFE64E4E), Color(0xFF4E7AE6)
    ),
    FlipSubject(
        MorphShape(sides = 6, cornerRadius = 12f), "Hexágono",
        Color(0xFF4EE6D8), Color(0xFFE64E88)
    ),
    FlipSubject(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 45f, edgeCurveStrength = 0.4f),
        "Cuadrado\nconvexo", Color(0xFF6B4EE6), Color(0xFFE6A44E)
    ),
    FlipSubject(
        MorphShape(sides = 6, cornerRadius = 12f, edgeCurveStrength = -0.4f),
        "Hexágono\ncóncavo", Color(0xFFE64E4E), Color(0xFF4ECBE6)
    ),
    FlipSubject(
        MorphShape(sides = 2, cornerRadius = 24f), "Cápsula",
        Color(0xFFB84EE6), Color(0xFF4EE68C)
    ),
    FlipSubject(
        MorphShape(sides = 3, cornerRadius = 20f, rotationDeg = -90f),
        "Triángulo\nr alto", Color(0xFF4EE68C), Color(0xFFE64E4E)
    ),
    FlipSubject(
        MorphShape(sides = 6, cornerRadius = 16f, edgeCurveStrength = -0.7f),
        "Hex cóncavo\nxtrm", Color(0xFF4ECBE6), Color(0xFFE6A44E)
    ),
    FlipSubject(
        MorphShape(sides = 2, cornerRadius = 26f, rotationDeg = -90f),
        "Cápsula\nvertical", Color(0xFFB84EE6), Color(0xFF4EE6D8)
    ),
)

// Formas representativas para previews de paleta.
internal val PIECE_TYPES = listOf(
    Circle,
    Hexagon,
    Pentagon,
    Square,
    Diamond,
    Triangle,
    Capsule
)

// Paletas disponibles para los previews de paleta.
private val PREVIEW_PALETTES = listOf(
    ClassicPalette to "Classic",
    DarkPalette to "Dark",
    NaturePalette to "Nature",
    GrayscalePalette to "Grayscale",
    EmberPalette to "Ember",
    AuroraPalette to "Aurora",
    GildedPalette to "Gilded",
)

// CobShape con guarda y motivo central del PieceType — para previews de paleta.
private fun cobShapeForPreview(
    pieceType: PieceType,
    scheme: ShapeColorScheme = CobColorScheme.Default,
) = CobShape(
    shape = pieceType.shape,
    colorScheme = scheme,
    borderPattern = pieceType.borderPattern,
    centerMotif = pieceType.centerMotif,
)

// ─────────────────────────────────────────────────────────────────────────────
// Preview 1 — grilla geométrica eje 90° (colores explícitos)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 340, heightDp = 700)
@Composable
fun ShapeFlipVerticalPreview() {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Volteo — eje 90° (Vertical)", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        flipSubjects.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { s ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        ShapeFlipAnimated(
                            shape = s.shape,
                            axisAngleDeg = 90f,
                            colorFront = s.colorFront,
                            colorBack = s.colorBack
                        )
                        Text(
                            s.label, style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray, textAlign = TextAlign.Center
                        )
                    }
                }
                repeat(3 - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 2 — grilla geométrica eje 0° (colores explícitos)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 340, heightDp = 700)
@Composable
fun ShapeFlipHorizontalPreview() {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Volteo — eje 0° (Horizontal)", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        flipSubjects.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { s ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        ShapeFlipAnimated(
                            shape = s.shape,
                            axisAngleDeg = 0f,
                            colorFront = s.colorFront,
                            colorBack = s.colorBack
                        )
                        Text(
                            s.label, style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray, textAlign = TextAlign.Center
                        )
                    }
                }
                repeat(3 - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 3 — fotogramas clave geométricos (V+H × 5 ángulos)
// ─────────────────────────────────────────────────────────────────────────────

private val KEY_STEPS = listOf(0f, 0.25f, 0.50f, 0.75f, 1.0f)
private val KEY_LABELS = listOf("0°", "45°", "90°", "135°", "180°")

@Composable
private fun KeyFrameStrip(subject: FlipSubject, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            subject.label.replace('\n', ' '),
            style = MaterialTheme.typography.labelSmall, color = Color(0xFF6366F1)
        )
        listOf(90f to "V", 0f to "H").forEach { (axDeg, lbl) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    lbl, style = MaterialTheme.typography.labelSmall, color = Color.Gray,
                    modifier = Modifier.width(18.dp), textAlign = TextAlign.End
                )
                KEY_STEPS.forEachIndexed { idx, prog ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        ShapeFlipAnimated(
                            modifier = modifier, shape = subject.shape, axisAngleDeg = axDeg,
                            colorFront = subject.colorFront, colorBack = subject.colorBack,
                            size = 64.dp, staticProgress = prog
                        )
                        if (axDeg == 90f)
                            Text(
                                KEY_LABELS[idx], style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray, fontSize = 9.sp
                            )
                    }
                }
            }
        }
    }
}

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 520, heightDp = 600)
@Composable
fun ShapeFlipStaticFramesPreview() {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            "Fotogramas clave — cara + canto 2.5D",
            style = MaterialTheme.typography.titleSmall
        )
        listOf(flipSubjects[2], flipSubjects[5], flipSubjects[3]).forEach {
            KeyFrameStrip(it, Modifier.fillMaxWidth())
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 4 — selector interactivo geométrico
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 360, heightDp = 480)
@Composable
fun ShapeFlipInteractivePreview() {
    var selectedIdx by remember { mutableIntStateOf(2) }
    var selectedAxisIdx by remember { mutableIntStateOf(0) }
    val axes = listOf(90f to "V (90°)", 0f to "H (0°)", 45f to "Diag (45°)", 135f to "Diag (135°)")

    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Selector interactivo", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            axes.forEachIndexed { i, (_, label) ->
                Text(
                    label, fontSize = 11.sp,
                    modifier = Modifier
                        .clickable { selectedAxisIdx = i }
                        .background(
                            if (selectedAxisIdx == i) Color(0xFF4ECBE6).copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp))
            }
        }
        val subject = flipSubjects[selectedIdx]
        val (axDeg, _) = axes[selectedAxisIdx]
        ShapeFlipAnimated(
            shape = subject.shape,
            axisAngleDeg = axDeg,
            colorFront = subject.colorFront,
            colorBack = subject.colorBack,
            size = 160.dp
        )
        Text(
            subject.label.replace('\n', ' '),
            style = MaterialTheme.typography.labelMedium, color = Color.Gray
        )
        listOf(flipSubjects.take(6), flipSubjects.drop(6)).forEachIndexed { rowIdx, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIdx, s ->
                    val gIdx = rowIdx * 6 + colIdx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedIdx = gIdx }
                            .background(
                                if (selectedIdx == gIdx) Color(0xFF4ECBE6).copy(0.25f)
                                else Color.Transparent
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center) {
                        ShapeFlipAnimated(
                            shape = s.shape,
                            axisAngleDeg = axDeg,
                            colorFront = s.colorFront,
                            colorBack = s.colorBack,
                            size = 44.dp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 5 — Stress test geométrico
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 560, heightDp = 460)
@Composable
fun ShapeFlipStressTestPreview() {
    val stressSteps = List(7) { it / 6f }
    val stressLabels = listOf("0°", "30°", "60°", "90°", "120°", "150°", "180°")

    data class StressItem(val subject: FlipSubject, val fix: String, val detail: String)

    val items = listOf(
        StressItem(flipSubjects[9], "Subdivisión", "N=3  r=20  barrido ≈ 120° / esquina"),
        StressItem(flipSubjects[10], "Guard colisión", "N=6  r=16  ca=−0.7"),
        StressItem(flipSubjects[11], "Subdivisión", "N=2  r=26  barrido ≈ 180° / esquina"),
    )
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Stress test — fixes geométricos", style = MaterialTheme.typography.titleSmall)
        items.forEach { (subject, fix, detail) ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        subject.label.replace('\n', ' '),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        "· $fix", style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6366F1)
                    )
                    Text(
                        detail, style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray, fontSize = 9.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    stressSteps.forEachIndexed { idx, prog ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            ShapeFlipAnimated(
                                shape = subject.shape, axisAngleDeg = 90f,
                                colorFront = subject.colorFront, colorBack = subject.colorBack,
                                size = 64.dp, staticProgress = prog
                            )
                            Text(
                                stressLabels[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = if (stressLabels[idx] == "90°") Color(0xFF6366F1)
                                else Color.Gray, fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 6 — rimFrac comparativo (geométrico)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 380, heightDp = 300)
@Composable
fun ShapeFlipRimFracComparePreview() {
    val rimValues = listOf(0.00f, 0.15f, 0.22f, 0.35f)
    val subjects = listOf(flipSubjects[5], flipSubjects[9])
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("rimFrac — 0%, 15%, 22%, 35%  (45°)", style = MaterialTheme.typography.titleSmall)
        subjects.forEach { s ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    s.label.replace('\n', ' '),
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray,
                    modifier = Modifier.width(54.dp)
                )
                rimValues.forEach { rim ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        ShapeFlipAnimated(
                            shape = s.shape,
                            axisAngleDeg = 90f,
                            colorFront = s.colorFront,
                            colorBack = s.colorBack,
                            size = 64.dp,
                            staticProgress = 0.25f,
                            rimFrac = rim
                        )
                        Text(
                            "${(rim * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (rim == 0.22f) Color(0xFF6366F1) else Color.Gray,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 7 — Ejes diagonales (geométrico)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "ShapeFlip", showBackground = true, widthDp = 420, heightDp = 320)
@Composable
fun ShapeFlipDiagonalAxesPreview() {
    val axisDegrees = listOf(0f, 30f, 60f, 90f, 120f, 150f)
    val subjects = listOf(flipSubjects[2], flipSubjects[5])
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Ejes diagonales — axisAngleDeg continuo (45°)",
            style = MaterialTheme.typography.titleSmall
        )
        subjects.forEach { s ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    s.label.replace('\n', ' '),
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray,
                    modifier = Modifier.width(50.dp)
                )
                axisDegrees.forEach { axDeg ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        ShapeFlipAnimated(
                            shape = s.shape, axisAngleDeg = axDeg, colorFront = s.colorFront, colorBack = s.colorBack,
                            size = 56.dp, staticProgress = 0.25f
                        )
                        Text(
                            "${axDeg.toInt()}°",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (axDeg == 0f || axDeg == 90f) Color(0xFF6366F1)
                            else Color.Gray, fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 8 — Piezas estáticas con paletas (drawMorphCob)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Cuerpo compartido de las previews de paleta × formas.
 *
 * Cada fila = una paleta; cada columna = un tipo de pieza.
 * La [cobColor] determina si se muestran piezas claras (WHITE) u oscuras (BLACK).
 * Incluye guarda y motivo central del [PieceType]
 * para mostrar la apariencia premium real.
 */
@Composable
private fun ShapeCobPaletteGrid(cobColor: CobColor) {
    val cobColorLabel = if (cobColor == CobColor.WHITE) "WHITE" else "BLACK"
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Piezas estáticas — paletas × formas ($cobColorLabel)",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            "scheme: Default  ·  guarda + motivo central activos",
            style = MaterialTheme.typography.labelSmall, color = Color.Gray
        )

        PREVIEW_PALETTES.forEach { (palette, paletteName) ->
            val colors = getBoardColors(palette)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    paletteName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6366F1)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PIECE_TYPES.forEach { pieceType ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            ShapeCobStatic(
                                cobShape = cobShapeForPreview(pieceType),
                                cobColor = cobColor,
                                boardColors = colors,
                                size = 46.dp,
                            )
                            Text(
                                stringResource(pieceType.nameRes).take(4),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray, fontSize = 8.sp
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

/** Piezas claras (WHITE) — paletas × formas con guarda y motivo central. */
@Preview(group = "ShapeFlipPalette", showBackground = true, widthDp = 460, heightDp = 600)
@Composable
fun ShapeCobPaletteGridPreview_White() = ShapeCobPaletteGrid(CobColor.WHITE)

/** Piezas oscuras (BLACK) — paletas × formas con guarda y motivo central. */
@Preview(group = "ShapeFlipPalette", showBackground = true, widthDp = 460, heightDp = 600)
@Composable
fun ShapeCobPaletteGridPreview_Black() = ShapeCobPaletteGrid(CobColor.BLACK)

// ─────────────────────────────────────────────────────────────────────────────
// Preview 9 — Volteo con paletas (drawMorphFlip)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Muestra el Hexágono y el Cuadrado girando con [CobColorScheme.Default]
 * para cada paleta disponible. Compara WHITE vs BLACK en cada fila.
 */
@Preview(group = "ShapeFlipPalette", showBackground = true, widthDp = 480, heightDp = 600)
@Composable
fun ShapeFlipCobPalettePreview() {
    // Formas para el preview de volteo.
    // Usar PieceTypes para incluir guarda y motivo central reales.
    val flipShapes = listOf(
        Hexagon to "Hexágono",
        Square to "Cuadrado",
    )

    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Volteo con paleta — animado  ·  scheme: Default",
            style = MaterialTheme.typography.titleSmall
        )

        PREVIEW_PALETTES.forEach { (palette, paletteName) ->
            val colors = getBoardColors(palette)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    paletteName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.width(60.dp)
                )
                flipShapes.forEach { (shape, _) ->  // shape = PieceType
                    val cobShape = cobShapeForPreview(shape)
                    ShapeFlipCobAnimated(
                        cobShape = cobShape, cobColor = CobColor.WHITE, boardColors = colors,
                        axisAngleDeg = 90f, size = 72.dp
                    )
                    ShapeFlipCobAnimated(
                        cobShape = cobShape, cobColor = CobColor.BLACK, boardColors = colors,
                        axisAngleDeg = 90f, size = 72.dp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 10 — Color schemes comparados (Classic palette)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Compara los 4 [CobColorScheme] predefinidos sobre la paleta Classic.
 *
 * Cada fila = una forma poligonal.
 * Cada grupo de 4 columnas = [Default, Vivid, Outlined, PatternAccent] × 2 bandos.
 */
@Preview(group = "ShapeFlipPalette", showBackground = true, widthDp = 560, heightDp = 540)
@Composable
fun ShapeCobColorSchemesPreview() {
    val colors = getBoardColors(ClassicPalette)
    val schemes = listOf(
        CobColorScheme.Default to "Default",
        CobColorScheme.Vivid to "Vivid",
        CobColorScheme.Outlined to "Outlined",
        CobColorScheme.PatternAccent to "Pattern",
    )

    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Color schemes — paleta Classic",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            "W / B por cada scheme: Default · Vivid · Outlined · PatternAccent",
            style = MaterialTheme.typography.labelSmall, color = Color.Gray
        )

        PIECE_TYPES.forEach { pieceType ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    stringResource(pieceType.nameRes),
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray,
                    modifier = Modifier.width(52.dp)
                )
                schemes.forEach { (scheme, _) ->
                    val cobShape = cobShapeForPreview(pieceType, scheme)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ShapeCobStatic(cobShape, CobColor.WHITE, colors, 46.dp)
                        Spacer(Modifier.width(8.dp))
                        ShapeCobStatic(cobShape, CobColor.BLACK, colors, 46.dp)
                    }
                }
            }
        }

        // Labels de schemes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Spacer(Modifier.width(52.dp))
            schemes.forEach { (_, name) ->
                Text(
                    name, style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6366F1), fontSize = 8.sp,
                    modifier = Modifier.width(106.dp), textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 11 — Volteo con color schemes (Classic + Ember)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Compara los 4 schemes en animación de volteo con 2 paletas distintas.
 * Muestra cómo el mismo scheme produce resultados visuales diferentes
 * según la paleta activa.
 */
@Preview(group = "ShapeFlipPalette", showBackground = true, widthDp = 480, heightDp = 320)
@Composable
fun ShapeFlipColorSchemesPreview() {
    val hexShape = Hexagon  // usa borderPattern + centerMotif reales del PieceType
    val palettes = listOf(ClassicPalette to "Classic", EmberPalette to "Ember")
    val schemes = listOf(
        CobColorScheme.Default to "Default",
        CobColorScheme.Vivid to "Vivid",
        CobColorScheme.Outlined to "Outlined",
        CobColorScheme.PatternAccent to "Pattern",
    )

    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Color schemes — volteo animado  (Hexágono N=6)",
            style = MaterialTheme.typography.titleSmall
        )

        palettes.forEach { (palette, paletteName) ->
            val boardColors = getBoardColors(palette)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    paletteName,
                    style = MaterialTheme.typography.labelSmall, color = Color(0xFF6366F1)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    schemes.forEach { (scheme, schemeName) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val cobShape = cobShapeForPreview(hexShape, scheme)
                            ShapeFlipCobAnimated(
                                cobShape = cobShape,
                                cobColor = CobColor.WHITE,
                                boardColors = boardColors,
                                axisAngleDeg = 90f,
                                size = 72.dp
                            )
                            Text(
                                schemeName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray, fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
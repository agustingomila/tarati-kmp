package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.ui.components.game.draw.common.MorphShape
import com.agustin.tarati.ui.components.game.draw.pieces.MorphShapeProjection
import kotlin.math.PI
import kotlin.math.cos

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

// Pasos de ángulo: 0°, 30°, 60°, 90° (canto), 120°, 150°, 180° (back)
private val FLIP_ANGLES_DEG = listOf(0, 30, 60, 90, 120, 150, 180)
private val FLIP_SCALES = FLIP_ANGLES_DEG.map { cos(it * PI / 180.0).toFloat() }

@Composable
private fun ProjectionStrip(
    label: String,
    projection: MorphShapeProjection,
    colorFront: Color,
    colorBack: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FLIP_SCALES.forEachIndexed { idx, scale ->
                val isFront = projection.isFrontFace(scale)
                val color = if (isFront) colorFront else colorBack
                val angleLbl = "${FLIP_ANGLES_DEG[idx]}°"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Surface(
                        shape = projection.atScale(scale),
                        color = color,
                        modifier = Modifier.size(width = 56.dp, height = 56.dp),
                    ) {}
                    Text(angleLbl, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 1 — eje Vertical (equivalente a faceScaleX del sistema circular)
// ─────────────────────────────────────────────────────────────────────────────

private val verticalSubjects = listOf(
    Triple(
        MorphShape(sides = 1),
        "Círculo  N=1",
        Color(0xFF6B4EE6) to Color(0xFFE64E88),
    ),
    Triple(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 45f),
        "Cuadrado  N=4 r=18",
        Color(0xFF4ECBE6) to Color(0xFFE6A44E),
    ),
    Triple(
        MorphShape(sides = 3, cornerRadius = 14f, rotationDeg = -90f),
        "Triángulo  N=3 r=14",
        Color(0xFF4EE68C) to Color(0xFFB84EE6),
    ),
    Triple(
        MorphShape(sides = 6, cornerRadius = 12f),
        "Hexágono  N=6 r=12",
        Color(0xFFE64E4E) to Color(0xFF4E7AE6),
    ),
    Triple(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 45f, edgeCurveStrength = 0.4f),
        "Cuadrado convexo  s=0.4",
        Color(0xFF4EE6D8) to Color(0xFFE64E88),
    ),
    Triple(
        MorphShape(sides = 2, cornerRadius = 22f),
        "Cápsula  N=2 r=22",
        Color(0xFF6B4EE6) to Color(0xFFE6A44E),
    ),
)

@Preview(showBackground = true, widthDp = 520, heightDp = 680)
@Composable
private fun ProjectionVerticalPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "Proyección eje Vertical  (comprime X — equivale a faceScaleX)",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "Cara delantera → color izquierdo  |  Cara trasera → color derecho",
                style = MaterialTheme.typography.labelMedium, color = Color.Gray,
            )
            verticalSubjects.forEach { (shape, label, colors) ->
                ProjectionStrip(
                    label = label,
                    projection = MorphShapeProjection.verticalProj(shape),
                    colorFront = colors.first,
                    colorBack = colors.second,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 2 — eje Horizontal (comprime Y)
// ─────────────────────────────────────────────────────────────────────────────

private val horizontalSubjects = listOf(
    Triple(
        MorphShape(sides = 1),
        "Círculo  N=1",
        Color(0xFF6B4EE6) to Color(0xFFE64E88),
    ),
    Triple(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 45f),
        "Cuadrado  N=4 r=18  (Caso 1: apoyado en arista)",
        Color(0xFF4ECBE6) to Color(0xFFE6A44E),
    ),
    Triple(
        MorphShape(sides = 4, cornerRadius = 18f, rotationDeg = 0f),
        "Diamante  N=4 r=18  (Caso 2: apoyado en vértice)",
        Color(0xFF4EE68C) to Color(0xFFB84EE6),
    ),
    Triple(
        MorphShape(sides = 5, cornerRadius = 12f, rotationDeg = -90f),
        "Pentágono  N=5 r=12",
        Color(0xFFE64E4E) to Color(0xFF4E7AE6),
    ),
    Triple(
        MorphShape(sides = 6, cornerRadius = 12f),
        "Hexágono  N=6 r=12",
        Color(0xFF4EE6D8) to Color(0xFFE64E88),
    ),
    Triple(
        MorphShape(sides = 2, cornerRadius = 24f, rotationDeg = -90f),
        "Cápsula vertical  N=2 r=24",
        Color(0xFF6B4EE6) to Color(0xFFE6A44E),
    ),
)

@Preview(showBackground = true, widthDp = 520, heightDp = 680)
@Composable
private fun ProjectionHorizontalPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "Proyección eje Horizontal  (comprime Y)",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "Cara delantera → color izquierdo  |  Cara trasera → color derecho",
                style = MaterialTheme.typography.labelMedium, color = Color.Gray,
            )
            horizontalSubjects.forEach { (shape, label, colors) ->
                ProjectionStrip(
                    label = label,
                    projection = MorphShapeProjection.horizontalProj(shape),
                    colorFront = colors.first,
                    colorBack = colors.second,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview 3 — Cuadrado Caso 1 vs Caso 2 en detalle (como en el enunciado)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 520, heightDp = 280)
@Composable
private fun ProjectionSquareCasesPreview() {
    // Caso 1: rotationDeg=45° → apoyado en arista (A-B arriba, C-D abajo)
    // En 90° A-C y B-D quedan superpuestos → línea horizontal
    val case1 = MorphShapeProjection.horizontalProj(
        MorphShape(sides = 4, cornerRadius = 14f, rotationDeg = 45f),
    )
    // Caso 2: rotationDeg=0° → apoyado en vértice (B arriba, D abajo)
    // En 90° solo B-D quedan superpuestos → línea horizontal más fina
    val case2 = MorphShapeProjection.horizontalProj(
        MorphShape(sides = 4, cornerRadius = 14f, rotationDeg = 0f),
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Cuadrado — Caso 1 (arista) vs Caso 2 (vértice)",
                style = MaterialTheme.typography.headlineSmall,
            )
            listOf(
                case1 to "Caso 1  rotationDeg=45°  (apoyado en arista)",
                case2 to "Caso 2  rotationDeg=0°   (apoyado en vértice)",
            ).forEach { (proj, label) ->
                ProjectionStrip(
                    label = label,
                    projection = proj,
                    colorFront = Color(0xFF4ECBE6),
                    colorBack = Color(0xFFE64E88),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
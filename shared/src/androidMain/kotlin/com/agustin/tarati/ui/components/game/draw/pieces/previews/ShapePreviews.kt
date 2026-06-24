package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.agustin.tarati.ui.components.game.draw.pieces.morphShadow

// ─────────────────────────────────────────────────────────────────────────────
// Datos de muestra
// ─────────────────────────────────────────────────────────────────────────────

private data class ShapeSample(val shape: MorphShape, val color: Color, val label: String)

// ── Fila 1: polígonos básicos (sin curvatura de arista) ───────────────────────
private val basicSamples = listOf(
    ShapeSample(
        MorphShape(sides = 1),
        Color(0xFF6B4EE6), "Círculo\nN=1"
    ),
    ShapeSample(
        MorphShape(sides = 3, cornerRadius = 18f, rotationDeg = -90f),
        Color(0xFFE64E88), "Triángulo\nN=3 r=18"
    ),
    ShapeSample(
        MorphShape(sides = 4, cornerRadius = 20f, rotationDeg = 45f),
        Color(0xFF4ECBE6), "Cuadrado\nN=4 r=20"
    ),
    ShapeSample(
        MorphShape(sides = 5, cornerRadius = 16f, rotationDeg = -90f),
        Color(0xFF4EE68C), "Pentágono\nN=5 r=16"
    ),
    ShapeSample(
        MorphShape(sides = 6, cornerRadius = 14f),
        Color(0xFFE6A44E), "Hexágono\nN=6 r=14"
    ),
    ShapeSample(
        MorphShape(sides = 8, cornerRadius = 12f),
        Color(0xFFB84EE6), "Octágono\nN=8 r=12"
    ),
)

// ── Fila 2: aristas convexas (edgeCurveStrength > 0) ─────────────────────────
private val convexSamples = listOf(
    ShapeSample(
        MorphShape(sides = 3, cornerRadius = 12f, rotationDeg = -90f, edgeCurveStrength = 0.5f),
        Color(0xFFE64E4E), "Triángulo\ns=0.5"
    ),
    ShapeSample(
        MorphShape(sides = 4, cornerRadius = 16f, rotationDeg = 45f, edgeCurveStrength = 0.4f),
        Color(0xFF4E7AE6), "Cuadrado\ns=0.4"
    ),
    ShapeSample(
        MorphShape(sides = 6, cornerRadius = 10f, edgeCurveStrength = 0.6f),
        Color(0xFF4EE6D8), "Hexágono\ns=0.6"
    ),
    ShapeSample(
        MorphShape(sides = 5, cornerRadius = 12f, rotationDeg = -90f, edgeCurveStrength = 0.8f),
        Color(0xFF6B4EE6), "Pentágono\ns=0.8"
    ),
    ShapeSample(
        MorphShape(sides = 3, cornerRadius = 20f, rotationDeg = -90f, edgeCurveStrength = 0.3f),
        Color(0xFFE64E88), "Triángulo r grnd\ns=0.3"
    ),
    ShapeSample(
        MorphShape(sides = 6, cornerRadius = 22f, edgeCurveStrength = 0.5f),
        Color(0xFFE6A44E), "Hexágono r grnd\ns=0.5"
    ),
)

// ── Fila 3: aristas cóncavas y cápsulas (N=2) ────────────────────────────────
private val concaveAndCapsuleSamples = listOf(
    ShapeSample(
        MorphShape(sides = 4, cornerRadius = 16f, rotationDeg = 45f, edgeCurveStrength = -0.4f),
        Color(0xFF4ECBE6), "Cuadrado\ns=−0.4"
    ),
    ShapeSample(
        MorphShape(sides = 6, cornerRadius = 10f, edgeCurveStrength = -0.5f),
        Color(0xFF4EE68C), "Hexágono\ns=−0.5"
    ),
    ShapeSample(
        MorphShape(sides = 2, cornerRadius = 55f),
        Color(0xFFB84EE6), "Cápsula\nN=2 r=55"
    ),
    ShapeSample(
        MorphShape(sides = 2, cornerRadius = 22f, rotationDeg = -90f),
        Color(0xFF4E7AE6), "Cápsula vert.\nN=2 r=22"
    ),
    ShapeSample(
        MorphShape(sides = 2, cornerRadius = 32f, rotationDeg = -90f, edgeCurveStrength = -0.4f),
        Color(0xFFE64E4E), "Lenticular\nN=2 s=−0.4"
    ),
    ShapeSample(
        MorphShape(sides = 2, cornerRadius = 18f, rotationDeg = -90f, edgeCurveStrength = 0.8f),
        Color(0xFFE6A44E), "Lenticular cap.\nN=2 s=0.8"
    ),
)

// ── Fila 4: curvatura por arista — edgeCurves[bottom, left, top, right] para N=4
//    (MorphRectShape equivalente: top→[2], bottom→[0], start→[1], end→[3])
private val perEdgeSamples = listOf(
    // top Convex(0.3) + bottom Convex(0.3) → edgeCurves[0.3f, 0f, 0.3f, 0f]
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(0.3f, 0f, 0.3f, 0f)
        ),
        Color(0xFF6B4EE6), "Convexo ↑↓\nRecto ←→"
    ),
    // top Concave(0.3) + bottom Concave(0.3)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(-0.3f, 0f, -0.3f, 0f)
        ),
        Color(0xFFE64E88), "Cóncavo ↑↓\nRecto ←→"
    ),
    // top Convex(0.4) + bottom Concave(0.4)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(-0.4f, 0f, 0.4f, 0f)
        ),
        Color(0xFF4ECBE6), "Convexo ↑ Cóncavo ↓\nRecto ←→"
    ),
    // start Convex(0.4) + end Convex(0.4)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(0f, 0.4f, 0f, 0.4f)
        ),
        Color(0xFF4EE68C), "Recto ↑↓\nConvexo ←→"
    ),
    // todas Concave(0.5)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(-0.5f, -0.5f, -0.5f, -0.5f)
        ),
        Color(0xFFE6A44E), "Cóncavo ↑↓↕\ntodas las aristas"
    ),
    // top Convex(0.6) + start/end Convex(0.2)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(0f, 0.2f, 0.6f, 0.2f)
        ),
        Color(0xFF4E7AE6), "Convexo ↑ mucho\nConvexo ←→ sutil"
    ),
    // top Convex(0.8) + bottom Concave(0.1)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(-0.1f, 0f, 0.8f, 0f)
        ),
        Color(0xFFE64E4E), "Convexo ↑ + Cóncavo ↓\nRecto ←→"
    ),
    // top Convex(0.2) + bottom Concave(0.2) + start Convex(0.2) + end Concave(0.2)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurves = floatArrayOf(-0.2f, 0.2f, 0.2f, -0.2f)
        ),
        Color(0xFFB84EE6), "Convexo ↑←\nCóncavo ↓→"
    ),
    // todas Convex(0.35)
    ShapeSample(
        MorphShape(
            sides = 4, cornerRadius = 28f, rotationDeg = 45f,
            edgeCurveStrength = 0.35f
        ),
        Color(0xFF4EE6D8), "Convexo ↑↓↕\ntodas las aristas"
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 1100, heightDp = 580)
@Composable
private fun MorphShapePolygonsPreview() {
    val rows = listOf(
        "Básicos  (edgeCurveStrength = 0)" to basicSamples,
        "Convexos  (edgeCurveStrength > 0)" to convexSamples,
        "Cóncavos  (edgeCurveStrength < 0)  y  N=2  cápsulas" to concaveAndCapsuleSamples,
    )
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("MorphShape — polígonos", style = MaterialTheme.typography.headlineSmall)
            rows.forEach { (label, samples) ->
                Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    samples.forEach { s ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Surface(
                                shape = s.shape, color = s.color,
                                modifier = Modifier
                                    .size(76.dp)
                                    .morphShadow(s.shape, elevation = 5.dp, offsetY = 3.dp),
                            ) {}
                            Text(s.label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 400)
@Composable
private fun MorphShapePerEdgePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "MorphShape — curvatura por arista  (N=4, edgeCurves[bottom, left, top, right])",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Equivalente a MorphRectShape: top→[2], bottom→[0], start→[1], end→[3]  |  " +
                        "Convex(s) → +s  |  Concave(s) → −s  |  Flat → 0f",
                style = MaterialTheme.typography.labelSmall, color = Color.Gray
            )
            perEdgeSamples.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    row.forEach { s ->
                        Surface(
                            shape = s.shape, color = s.color,
                            modifier = Modifier
                                .weight(1f)
                                .size(76.dp)
                                .morphShadow(s.shape, elevation = 5.dp, offsetY = 3.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    s.label, color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
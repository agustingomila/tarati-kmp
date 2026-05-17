package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.pieces.BorderPattern
import com.agustin.tarati.ui.components.game.draw.pieces.CenterMotif
import com.agustin.tarati.ui.components.game.draw.pieces.CobColorScheme
import com.agustin.tarati.ui.components.game.draw.pieces.CobShape
import com.agustin.tarati.ui.components.game.draw.pieces.PieceType
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.getBoardColors
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────────────────────
// Constantes
// ─────────────────────────────────────────────────────────────────────────────

private val PREMIUM_TYPES = listOf(
    PieceTypes.Hexagon,
    PieceTypes.Square,
    PieceTypes.Triangle,
    PieceTypes.Diamond,
    PieceTypes.Pentagon,
    PieceTypes.Capsule,
)

private val ALL_BORDERS = listOf(
    BorderPattern.None to "None",
    BorderPattern.DoubleRing to "DoubleRing",
    BorderPattern.Fishtail to "Fishtail",
    BorderPattern.Diamonds to "Diamonds",
    BorderPattern.Chevron to "Chevron",
    BorderPattern.Meander to "Meander",
)

private val ALL_MOTIFS = listOf(
    CenterMotif.Default to "Default",
    CenterMotif.Cross to "Cross",
    CenterMotif.Trefoil to "Trefoil",
    CenterMotif.Ring to "Ring",
    CenterMotif.Compass to "Compass",
    CenterMotif.Star5 to "Star5",
    CenterMotif.DiamondCross to "DiamondCross",
)

private val ACCENT = Color(0xFF6366F1)

// ─────────────────────────────────────────────────────────────────────────────
// Tile helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BorderTile(
    pieceType: PieceType,
    border: BorderPattern,
    cobColor: CobColor,
    colors: BoardColors,
) = ShapeCobStatic(
    cobShape = CobShape(
        shape = pieceType.shape,
        colorScheme = CobColorScheme.Default,
        borderPattern = border,
        centerMotif = CenterMotif.None,
    ),
    cobColor = cobColor,
    boardColors = colors,
    size = 42.dp,
)

@Composable
private fun MotifTile(
    pieceType: PieceType,
    motif: CenterMotif,
    cobColor: CobColor,
    colors: BoardColors,
) = ShapeCobStatic(
    cobShape = CobShape(
        shape = pieceType.shape,
        colorScheme = CobColorScheme.Default,
        borderPattern = BorderPattern.None,
        centerMotif = motif,
    ),
    cobColor = cobColor,
    boardColors = colors,
    size = 42.dp,
)

// ─────────────────────────────────────────────────────────────────────────────
// Layout compartido
// ─────────────────────────────────────────────────────────────────────────────

/** Dos filas (WHITE / BLACK) de tiles para una sola variante de decoración. */
@Composable
private fun DecorationCard(
    title: String,
    subtitle: String,
    bgColor: Color = Color.Transparent,
    buildTile: @Composable (PieceType, CobColor) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(Modifier.height(2.dp))

        // Fila WHITE
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("W", fontSize = 9.sp, color = ACCENT, modifier = Modifier.width(16.dp))
            PREMIUM_TYPES.forEach { pt -> buildTile(pt, CobColor.WHITE) }
        }

        Spacer(Modifier.height(8.dp))

        // Fila BLACK
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("B", fontSize = 9.sp, color = ACCENT, modifier = Modifier.width(16.dp))
            PREMIUM_TYPES.forEach { pt -> buildTile(pt, CobColor.BLACK) }
        }

        // Pie de nombres de pieza
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Spacer(Modifier.width(20.dp))
            PREMIUM_TYPES.forEach { pt ->
                Text(
                    stringResource(pt.nameRes).take(4),
                    fontSize = 8.sp,
                    color = Color.Gray,
                    modifier = Modifier.width(52.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS INDIVIDUALES — BorderPattern
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_None() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("None", "Franja sólida — comportamiento base") { pt, color ->
            BorderTile(pt, BorderPattern.None, color, c)
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_DoubleRing() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("DoubleRing", "Dos anillos concéntricos finos") { pt, color ->
            BorderTile(pt, BorderPattern.DoubleRing, color, c)
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_Fishtails() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Fishtail", "Fishtails equidistantes sobre fondo sólido") { pt, color ->
            BorderTile(pt, BorderPattern.Fishtail, color, c)
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_Diamonds() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Diamonds", "Rombos orientados al perímetro") { pt, color ->
            BorderTile(pt, BorderPattern.Diamonds, color, c)
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_Chevron() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Chevron", "Flechas en la dirección del perímetro") { pt, color ->
            BorderTile(pt, BorderPattern.Chevron, color, c)
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun BorderPreview_Meander() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Meander", "Brackets alternados — guarda griega") { pt, color ->
            BorderTile(pt, BorderPattern.Meander, color, c)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS INDIVIDUALES — CenterMotif
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Default() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Default", "Polígono escalado — comportamiento original") { pt, color ->
            MotifTile(pt, CenterMotif.Default, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Cross() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Cross", "Cruz de brazos iguales (+)") { pt, color ->
            MotifTile(pt, CenterMotif.Cross, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Star() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Trefoil", "Estrella de 4 puntas (♦)") { pt, color ->
            MotifTile(pt, CenterMotif.Trefoil, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Ring() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Ring", "Anillo concéntrico fino") { pt, color ->
            MotifTile(pt, CenterMotif.Ring, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Compass() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Compass", "Rosa de los vientos de 8 puntas") { pt, color ->
            MotifTile(pt, CenterMotif.Compass, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_DiamondCross() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("DiamondCross", "Cruz dentro de diamante") { pt, color ->
            MotifTile(pt, CenterMotif.DiamondCross, color, c)
        }
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 380, heightDp = 170)
@Composable
fun MotifPreview_Star5() {
    val c = getBoardColors(ClassicPalette)
    MaterialTheme {
        DecorationCard("Star5", "Estrella de 5 puntas") { pt, color ->
            MotifTile(pt, CenterMotif.Star5, color, c)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GRILLAS COMPLETAS — todas las variantes × todas las piezas
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FullBorderGrid(cobColor: CobColor, colors: BoardColors, bgColor: Color) {
    val label = if (cobColor == CobColor.WHITE) "WHITE" else "BLACK"
    Column(
        modifier = Modifier
            .background(bgColor)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("Guardas — todas las variantes ($label)", style = MaterialTheme.typography.titleSmall)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        ALL_BORDERS.forEach { (border, name) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(name, fontSize = 9.sp, color = ACCENT, modifier = Modifier.width(64.dp))
                PREMIUM_TYPES.forEach { pt -> BorderTile(pt, border, cobColor, colors) }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Spacer(Modifier.width(76.dp))
            PREMIUM_TYPES.forEach { pt ->
                Text(
                    stringResource(pt.nameRes).take(4),
                    fontSize = 8.sp, color = Color.Gray,
                    modifier = Modifier.width(48.dp),
                )
            }
        }
    }
}

@Composable
private fun FullMotifGrid(cobColor: CobColor, colors: BoardColors, bgColor: Color) {
    val label = if (cobColor == CobColor.WHITE) "WHITE" else "BLACK"
    Column(
        modifier = Modifier
            .background(bgColor)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("Motivos centrales — todos ($label)", style = MaterialTheme.typography.titleSmall)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        ALL_MOTIFS.forEach { (motif, name) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(name, fontSize = 9.sp, color = ACCENT, modifier = Modifier.width(64.dp))
                PREMIUM_TYPES.forEach { pt -> MotifTile(pt, motif, cobColor, colors) }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Spacer(Modifier.width(76.dp))
            PREMIUM_TYPES.forEach { pt ->
                Text(
                    stringResource(pt.nameRes).take(4),
                    fontSize = 8.sp, color = Color.Gray,
                    modifier = Modifier.width(48.dp),
                )
            }
        }
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 410, heightDp = 400)
@Composable
fun BorderGrid_White_Classic() {
    MaterialTheme { FullBorderGrid(CobColor.WHITE, getBoardColors(ClassicPalette), Color.White) }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 410, heightDp = 400)
@Composable
fun BorderGrid_Black_Classic() {
    MaterialTheme {
        FullBorderGrid(CobColor.BLACK, getBoardColors(ClassicPalette), ClassicPalette.boardBackground)
    }
}

@Preview(group = "BorderPattern", showBackground = true, widthDp = 410, heightDp = 400)
@Composable
fun BorderGrid_White_Gilded() {
    MaterialTheme {
        FullBorderGrid(CobColor.WHITE, getBoardColors(GildedPalette), GildedPalette.boardBackground)
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 410, heightDp = 460)
@Composable
fun MotifGrid_White_Classic() {
    MaterialTheme { FullMotifGrid(CobColor.WHITE, getBoardColors(ClassicPalette), Color.White) }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 410, heightDp = 460)
@Composable
fun MotifGrid_Black_Classic() {
    MaterialTheme {
        FullMotifGrid(CobColor.BLACK, getBoardColors(ClassicPalette), ClassicPalette.boardBackground)
    }
}

@Preview(group = "CenterMotif", showBackground = true, widthDp = 410, heightDp = 460)
@Composable
fun MotifGrid_White_Gilded() {
    MaterialTheme {
        FullMotifGrid(CobColor.WHITE, getBoardColors(GildedPalette), GildedPalette.boardBackground)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview combinado — asignaciones reales del juego
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Cada pieza premium con su guarda y motivo central asignados, en tres paletas.
 */
@Preview(group = "CenterMotif", showBackground = true, widthDp = 420, heightDp = 255)
@Composable
fun PremiumPiecesAssignedPreview() {
    val palettes = listOf(
        ClassicPalette to "Classic",
        DarkPalette to "Dark",
        GildedPalette to "Gilded",
    )
    MaterialTheme {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "Asignaciones reales — guarda + motivo por pieza",
                style = MaterialTheme.typography.titleSmall,
            )
            palettes.forEach { (palette, paletteName) ->
                val colors = getBoardColors(palette)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.boardBackground)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                ) {
                    Text(
                        paletteName,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.width(48.dp),
                    )
                    PREMIUM_TYPES.forEach { pt ->
                        ShapeCobStatic(
                            cobShape = CobShape(
                                shape = pt.shape,
                                colorScheme = CobColorScheme.Default,
                                borderPattern = pt.borderPattern,
                                centerMotif = pt.centerMotif,
                            ),
                            cobColor = CobColor.WHITE,
                            boardColors = colors,
                            size = 42.dp,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Spacer(Modifier.width(72.dp))
                PREMIUM_TYPES.forEach { pt ->
                    Text(
                        stringResource(pt.nameRes).take(4),
                        fontSize = 8.sp, color = Color.Gray,
                        modifier = Modifier.width(52.dp),
                    )
                }
            }
        }
    }
}
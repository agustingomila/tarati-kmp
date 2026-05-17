package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypeSelector
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypeTile
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.getBoardColors

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PieceTypeSelector", showBackground = true, widthDp = 420, heightDp = 160)
@Composable
private fun PieceTypeSelectorClassicPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "Classic — seleccionado: Hexágono",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            PieceTypeSelector(
                selectedId = PieceTypes.Hexagon.id,
                boardColors = getBoardColors(ClassicPalette),
                onSelect = {},
            )
        }
    }
}

@Preview(group = "PieceTypeSelector", showBackground = true, widthDp = 420, heightDp = 160)
@Composable
private fun PieceTypeSelectorDarkPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "Dark — seleccionado: Triángulo",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            PieceTypeSelector(
                selectedId = PieceTypes.Triangle.id,
                boardColors = getBoardColors(DarkPalette),
                onSelect = {},
            )
        }
    }
}

@Preview(group = "PieceTypeSelector", showBackground = true, widthDp = 420, heightDp = 140)
@Composable
private fun PieceTypeTilesStaticPreview() {
    // Muestra todas las piezas en vista frontal (progress = 0) para comparar formas.
    val colors = getBoardColors(ClassicPalette)
    MaterialTheme {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(PieceTypes.all) { index, pieceType ->
                PieceTypeTile(
                    pieceType = pieceType,
                    flipProgress = 0f,      // vista frontal
                    isSelected = index == 1,
                    boardColors = colors,
                    onClick = {},
                )
            }
        }
    }
}
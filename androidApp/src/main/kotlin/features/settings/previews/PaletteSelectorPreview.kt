package features.settings.previews

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.settings.PaletteBoardTile
import com.agustin.tarati.features.settings.PaletteSelector
import com.agustin.tarati.services.billing.LockedPalettes
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.PaletteList

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PaletteSelector", showBackground = true, widthDp = 420, heightDp = 180)
@Composable
private fun PaletteSelectorPreview() {
    MaterialTheme {
        PaletteSelector(
            selectedPaletteName = ClassicPalette.name,
            palettes = PaletteList(listOf(ClassicPalette, DarkPalette, NaturePalette, GrayscalePalette)),
            onSelect = {},
        )
    }
}

@Preview(group = "PaletteSelector", showBackground = true, widthDp = 420, heightDp = 180)
@Composable
private fun PaletteSelectorWithLockedPreview() {
    MaterialTheme {
        PaletteSelector(
            selectedPaletteName = ClassicPalette.name,
            palettes = PaletteList(listOf(ClassicPalette, DarkPalette, NaturePalette, GrayscalePalette)),
            lockedPalettes = LockedPalettes(setOf(NaturePalette.name, GrayscalePalette.name)),
            onSelect = {},
            onPurchase = {},
        )
    }
}

@Preview(group = "PaletteSelector", showBackground = true, widthDp = 160, heightDp = 180)
@Composable
private fun PaletteBoardTileSelectedPreview() {
    MaterialTheme {
        PaletteBoardTile(
            palette = ClassicPalette,
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview(group = "PaletteSelector", showBackground = true, widthDp = 160, heightDp = 180)
@Composable
private fun PaletteBoardTileLockedPreview() {
    MaterialTheme {
        PaletteBoardTile(
            palette = DarkPalette,
            isSelected = false,
            isLocked = true,
            onClick = {},
        )
    }
}
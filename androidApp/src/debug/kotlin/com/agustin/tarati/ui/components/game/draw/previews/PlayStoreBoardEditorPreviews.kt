package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.game.previews.GameScreenPreview_DrawerClosed_Portrait
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 05 — Editor de tablero                           Teléfono centrado completo
// ─────────────────────────────────────────────────────────────────────────────
//  Teléfono completo (sin corte) con la pantalla de edición de tablero.
//  Se ven tanto el top bar como la bottom bar con los controles del editor.
//  Usa GameScreenPreview_DrawerClosed_Portrait que ya tiene isEditing = true.

@Composable
private fun BoardEditorStoreScreenshotContent(palette: BoardPalette) {
    GameScreenPreview_DrawerClosed_Portrait(palette)
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotCentered(
            title = "Editá el tablero",
            subtitle = "Configurá la posición inicial\nde cada nueva partida",
            palette = ClassicPalette,
        ) { BoardEditorStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotCentered(
            title = "Editá el tablero",
            subtitle = "Configurá la posición inicial\nde cada nueva partida",
            palette = DarkPalette,
        ) { BoardEditorStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotCentered(
            title = "Editá el tablero",
            subtitle = "Configurá la posición inicial\nde cada nueva partida",
            palette = NaturePalette,
        ) { BoardEditorStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotCentered(
            title = "Editá el tablero",
            subtitle = "Configurá la posición inicial\nde cada nueva partida",
            palette = GrayscalePalette,
        ) { BoardEditorStoreScreenshotContent(GrayscalePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotCentered(
            title = "Edit the board",
            subtitle = "Set the starting position\nfor each new game",
            palette = ClassicPalette,
        ) { BoardEditorStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotCentered(
            title = "Edit the board",
            subtitle = "Set the starting position\nfor each new game",
            palette = DarkPalette,
        ) { BoardEditorStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotCentered(
            title = "Edit the board",
            subtitle = "Set the starting position\nfor each new game",
            palette = NaturePalette,
        ) { BoardEditorStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreBoardEditor05_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotCentered(
            title = "Edit the board",
            subtitle = "Set the starting position\nfor each new game",
            palette = GrayscalePalette,
        ) { BoardEditorStoreScreenshotContent(GrayscalePalette) }
    }
}
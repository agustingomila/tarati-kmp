package com.agustin.tarati.ui.components.game.draw.playstore

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.game.previews.GameScreenPreviewConfig
import com.agustin.tarati.features.game.previews.GameScreenPreview_GameInProgress
import com.agustin.tarati.features.game.previews.previewRandomMidGameState
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.BOTTOM
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 03 — Historial de movimientos con navegación       TextPosition.BOTTOM
// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla principal de juego con la lista de movimientos del FAB desplegada.
//  BottomGameBar inicializa isExpanded + isHistoryOpen = true en LocalInspectionMode
//  cuando hay movimientos en el historial — no requiere ningún parámetro adicional.

@Composable
private fun MoveHistoryStoreScreenshotContent(palette: BoardPalette) {
    GameScreenPreview_GameInProgress(
        config = GameScreenPreviewConfig(
            palette = palette,
            drawerStateValue = DrawerValue.Closed,
            gameState = previewRandomMidGameState,
            historyPanelOpen = true,
        ),
    )
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Revisá cada jugada",
            subtitle = "Historial completo con navegación\npaso a paso por la partida",
            textPosition = BOTTOM, palette = ClassicPalette,
        ) { MoveHistoryStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Revisá cada jugada",
            subtitle = "Historial completo con navegación\npaso a paso por la partida",
            textPosition = BOTTOM, palette = DarkPalette,
        ) { MoveHistoryStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Revisá cada jugada",
            subtitle = "Historial completo con navegación\npaso a paso por la partida",
            textPosition = BOTTOM, palette = NaturePalette,
        ) { MoveHistoryStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Revisá cada jugada",
            subtitle = "Historial completo con navegación\npaso a paso por la partida",
            textPosition = BOTTOM, palette = GrayscalePalette,
        ) { MoveHistoryStoreScreenshotContent(GrayscalePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Review every move",
            subtitle = "Full game history with\nstep-by-step navigation",
            textPosition = BOTTOM, palette = ClassicPalette,
        ) { MoveHistoryStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Review every move",
            subtitle = "Full game history with\nstep-by-step navigation",
            textPosition = BOTTOM, palette = DarkPalette,
        ) { MoveHistoryStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Review every move",
            subtitle = "Full game history with\nstep-by-step navigation",
            textPosition = BOTTOM, palette = NaturePalette,
        ) { MoveHistoryStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameHistory03_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Review every move",
            subtitle = "Full game history with\nstep-by-step navigation",
            textPosition = BOTTOM, palette = GrayscalePalette,
        ) { MoveHistoryStoreScreenshotContent(GrayscalePalette) }
    }
}
package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.detail.previews.GameDetailScreenPreview
import com.agustin.tarati.features.detail.previews.PreviewGameDetailsViewModel
import com.agustin.tarati.features.detail.previews.randomFinalMatchDto
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.BOTTOM
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 07 — Detalle y análisis de partida guardada        TextPosition.BOTTOM
// ─────────────────────────────────────────────────────────────────────────────
//  Renderizado con UI real de Compose — no requiere drawable estático.
//  GameDetailScreenPreview incluye el Scaffold, la TopBar y el FAB correctamente
//  posicionados, que es la vista más representativa de esta feature.

@Composable
private fun GameDetailsStoreScreenshotContent(
    palette: BoardPalette,
    darkTheme: Boolean = false,
) {
    GameDetailScreenPreview(
        palette = palette,
        darkTheme = darkTheme,
        viewModel = PreviewGameDetailsViewModel(matchDto = randomFinalMatchDto()),
    )
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
            textPosition = BOTTOM, palette = ClassicPalette,
        ) { GameDetailsStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
            textPosition = BOTTOM, palette = DarkPalette,
        ) { GameDetailsStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
            textPosition = BOTTOM, palette = NaturePalette,
        ) { GameDetailsStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
            textPosition = BOTTOM, palette = GrayscalePalette,
        ) { GameDetailsStoreScreenshotContent(GrayscalePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
            textPosition = BOTTOM, palette = ClassicPalette,
        ) { GameDetailsStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
            textPosition = BOTTOM, palette = DarkPalette,
        ) { GameDetailsStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
            textPosition = BOTTOM, palette = NaturePalette,
        ) { GameDetailsStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreDetailsScreen07_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
            textPosition = BOTTOM, palette = GrayscalePalette,
        ) { GameDetailsStoreScreenshotContent(GrayscalePalette) }
    }
}
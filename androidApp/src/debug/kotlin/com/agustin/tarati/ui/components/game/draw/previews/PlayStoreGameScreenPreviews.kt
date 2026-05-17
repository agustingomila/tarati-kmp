package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.game.previews.GameScreenPreview_GameInProgress
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.TOP
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 01 — Tablero principal en partida                  TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────
//  Renderizado con UI real de Compose — no requiere drawable estático.
//  GameScreenPreview_GameInProgress muestra el tablero con una partida en curso,
//  drawer cerrado y estado mid-game representativo.

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
            textPosition = TOP, palette = ClassicPalette,
        ) { GameScreenPreview_GameInProgress(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
            textPosition = TOP, palette = DarkPalette,
        ) { GameScreenPreview_GameInProgress(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
            textPosition = TOP, palette = NaturePalette,
        ) { GameScreenPreview_GameInProgress(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
            textPosition = TOP, palette = GrayscalePalette,
        ) { GameScreenPreview_GameInProgress(GrayscalePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
            textPosition = TOP, palette = ClassicPalette,
        ) { GameScreenPreview_GameInProgress(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
            textPosition = TOP, palette = DarkPalette,
        ) { GameScreenPreview_GameInProgress(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
            textPosition = TOP, palette = NaturePalette,
        ) { GameScreenPreview_GameInProgress(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameScreen01_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
            textPosition = TOP, palette = GrayscalePalette,
        ) { GameScreenPreview_GameInProgress(GrayscalePalette) }
    }
}
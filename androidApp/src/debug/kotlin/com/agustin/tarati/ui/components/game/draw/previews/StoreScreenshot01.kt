package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.R
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_H_DP
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_W_DP
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.TOP
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette

// ══════════════════════════════════════════════════════════════════════════════
//  @PREVIEW — CAPTURAS PLAY STORE
//
//  Grupos   : PlayStore_ES  (español)  /  PlayStore_EN  (inglés)
//  Paletas  : Classic · Dark · Nature · Grayscale  (4 por captura × idioma)
//  Capturas : 01 tablero · 02 IA · 03 historial · 04 paletas ·
//             05 editor · 06 biblioteca · 07 detalle partida
//  Total    : 7 capturas × 2 idiomas × 4 paletas = 56 previews
//
//  Nomenclatura: StoreScreenshot{NN}_{LANG}_{PALETTE}
// ══════════════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────────────
// 01 — Tablero principal en partida                  TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_ES_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_es_classic,
    title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_ES_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_es_dark,
    title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_ES_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_es_nature,
    title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_ES_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_es_grayscale,
    title = "Tarati", subtitle = "El juego de estrategia de\nGeorge Spencer Brown",
    textPosition = TOP, palette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_EN_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_en_classic,
    title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_EN_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_en_dark,
    title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_EN_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_en_nature,
    title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot01_EN_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_01_en_grayscale,
    title = "Tarati", subtitle = "The strategy game by\nGeorge Spencer Brown",
    textPosition = TOP, palette = GrayscalePalette,
)

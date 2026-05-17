package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.R
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.BOTTOM
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette

// ─────────────────────────────────────────────────────────────────────────────
// 03 — Historial de movimientos con navegación       TextPosition.BOTTOM
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_ES_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_es_classic,
    title = "Revisá cada jugada", subtitle = "Historial completo con navegación\npaso a paso por la partida",
    textPosition = BOTTOM, palette = ClassicPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_ES_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_es_dark,
    title = "Revisá cada jugada", subtitle = "Historial completo con navegación\npaso a paso por la partida",
    textPosition = BOTTOM, palette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_ES_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_es_nature,
    title = "Revisá cada jugada", subtitle = "Historial completo con navegación\npaso a paso por la partida",
    textPosition = BOTTOM, palette = NaturePalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_ES_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_es_grayscale,
    title = "Revisá cada jugada", subtitle = "Historial completo con navegación\npaso a paso por la partida",
    textPosition = BOTTOM, palette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_EN_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_en_classic,
    title = "Review every move", subtitle = "Full game history with\nstep-by-step navigation",
    textPosition = BOTTOM, palette = ClassicPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_EN_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_en_dark,
    title = "Review every move", subtitle = "Full game history with\nstep-by-step navigation",
    textPosition = BOTTOM, palette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_EN_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_en_nature,
    title = "Review every move", subtitle = "Full game history with\nstep-by-step navigation",
    textPosition = BOTTOM, palette = NaturePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot03_EN_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_03_en_grayscale,
    title = "Review every move", subtitle = "Full game history with\nstep-by-step navigation",
    textPosition = BOTTOM, palette = GrayscalePalette,
)
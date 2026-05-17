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
// 07 — Detalle y análisis de partida                 TextPosition.BOTTOM
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_ES_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_es_classic,
    title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
    textPosition = BOTTOM, palette = ClassicPalette
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_ES_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_es_dark,
    title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
    textPosition = BOTTOM, palette = DarkPalette
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_ES_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_es_nature,
    title = "Analizá tus partidas", subtitle = "Historial completo de movimientos\ny posiciones para recargar",
    textPosition = BOTTOM, palette = NaturePalette
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_ES_Grayscale() = PlayStoreScreenshot(
    R.drawable.ss_07_es_grayscale,
    "Analizá tus partidas",
    "Historial completo de movimientos\ny posiciones para recargar",
    BOTTOM,
    GrayscalePalette
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_EN_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_en_classic,
    title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
    textPosition = BOTTOM, palette = ClassicPalette
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_EN_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_en_dark,
    title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
    textPosition = BOTTOM, palette = DarkPalette
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_EN_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_en_nature,
    title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
    textPosition = BOTTOM, palette = NaturePalette
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot07_EN_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_07_en_grayscale,
    title = "Analyze your games", subtitle = "Full move history and\nsaved positions to reload",
    textPosition = BOTTOM, palette = GrayscalePalette
)
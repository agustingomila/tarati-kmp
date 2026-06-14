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

// ─────────────────────────────────────────────────────────────────────────────
// 06 — Biblioteca de partidas guardadas              TextPosition.BOTTOM
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_ES_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_es_classic,
    title = "Guardá tus partidas", subtitle = "Explorá y reanudá cualquier\npartida anterior cuando quieras",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_ES_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_es_dark,
    title = "Guardá tus partidas", subtitle = "Explorá y reanudá cualquier\npartida anterior cuando quieras",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_ES_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_es_nature,
    title = "Guardá tus partidas", subtitle = "Explorá y reanudá cualquier\npartida anterior cuando quieras",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_ES_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_es_grayscale,
    title = "Guardá tus partidas", subtitle = "Explorá y reanudá cualquier\npartida anterior cuando quieras",
    textPosition = TOP, palette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_EN_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_en_classic,
    title = "Save your games", subtitle = "Browse and resume any\nprevious game whenever you want",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_EN_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_en_dark,
    title = "Save your games", subtitle = "Browse and resume any\nprevious game whenever you want",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_EN_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_en_nature,
    title = "Save your games", subtitle = "Browse and resume any\nprevious game whenever you want",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot06_EN_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_06_en_grayscale,
    title = "Save your games", subtitle = "Browse and resume any\nprevious game whenever you want",
    textPosition = TOP, palette = GrayscalePalette,
)
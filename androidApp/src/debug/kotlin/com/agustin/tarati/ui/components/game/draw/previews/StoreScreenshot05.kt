package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.R
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette

// ══════════════════════════════════════════════════════════════════════════════
//  05 — Editor de tablero, teléfono centrado completo
// ══════════════════════════════════════════════════════════════════════════════

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_ES_Classic() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_es_classic,
    title = "Editá el tablero",
    subtitle = "Configurá la posición inicial\nde cada nueva partida",
    palette = ClassicPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_ES_Dark() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_es_dark,
    title = "Editá el tablero",
    subtitle = "Configurá la posición inicial\nde cada nueva partida",
    palette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_ES_Nature() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_es_nature,
    title = "Editá el tablero",
    subtitle = "Configurá la posición inicial\nde cada nueva partida",
    palette = NaturePalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_ES_Grayscale() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_es_grayscale,
    title = "Editá el tablero",
    subtitle = "Configurá la posición inicial\nde cada nueva partida",
    palette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_EN_Classic() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_en_classic,
    title = "Edit the board", subtitle = "Set up the starting position\nfor every new game", palette = ClassicPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_EN_Dark() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_en_dark,
    title = "Edit the board", subtitle = "Set up the starting position\nfor every new game", palette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_EN_Nature() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_en_nature,
    title = "Edit the board", subtitle = "Set up the starting position\nfor every new game", palette = NaturePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot05b_EN_Grayscale() = PlayStoreScreenshotCentered(
    screenshotRes = R.drawable.ss_05_en_grayscale,
    title = "Edit the board", subtitle = "Set up the starting position\nfor every new game", palette = GrayscalePalette,
)
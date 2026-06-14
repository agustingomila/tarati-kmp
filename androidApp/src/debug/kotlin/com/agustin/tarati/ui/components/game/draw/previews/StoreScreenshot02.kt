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
// 02 — IA oponente con niveles de dificultad         TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_ES_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_es_classic,
    title = "Jugá contra la IA", subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_ES_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_es_dark,
    title = "Jugá contra la IA", subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_ES_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_es_nature,
    title = "Jugá contra la IA", subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_ES_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_es_grayscale,
    title = "Jugá contra la IA", subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
    textPosition = TOP, palette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_EN_Classic() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_en_classic,
    title = "Play against the AI", subtitle = "4 difficulty levels,\nfrom beginner to champion",
    textPosition = TOP, palette = ClassicPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_EN_Dark() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_en_dark,
    title = "Play against the AI", subtitle = "4 difficulty levels,\nfrom beginner to champion",
    textPosition = TOP, palette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_EN_Nature() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_en_nature,
    title = "Play against the AI", subtitle = "4 difficulty levels,\nfrom beginner to champion",
    textPosition = TOP, palette = NaturePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot02_EN_Grayscale() = PlayStoreScreenshot(
    screenshotRes = R.drawable.ss_02_en_grayscale,
    title = "Play against the AI", subtitle = "4 difficulty levels,\nfrom beginner to champion",
    textPosition = TOP, palette = GrayscalePalette,
)
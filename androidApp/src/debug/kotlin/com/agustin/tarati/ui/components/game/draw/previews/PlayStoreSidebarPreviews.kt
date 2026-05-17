package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.game.previews.GameScreenPreviewConfig
import com.agustin.tarati.features.game.previews.GameScreenPreview_WithDrawer_Portrait
import com.agustin.tarati.features.game.previews.previewRandomMidGameState
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.TOP
import com.agustin.tarati.ui.components.sidebar.SidebarUIState
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 02 — IA oponente con niveles de dificultad         TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────
//  Sidebar abierto con el dropdown de dificultad del bando negro expandido,
//  mostrando los 4 niveles disponibles.
//  Usa GameScreenPreview_WithDrawer_Portrait con el sidebarUIState pre-configurado
//  vía GameScreenPreviewConfig.sidebarUIState — sin cambios en el Sidebar.

private val sidebarWithDifficultyOpen = SidebarUIState(
    isDifficultyExpandedBlack = true,
)

@Composable
private fun SidebarStoreScreenshotContent(palette: BoardPalette) {
    GameScreenPreview_WithDrawer_Portrait(
        config = GameScreenPreviewConfig(
            palette = palette,
            drawerStateValue = DrawerValue.Open,
            gameState = previewRandomMidGameState,
            sidebarUIState = sidebarWithDifficultyOpen,
        ),
    )
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Jugá contra la IA",
            subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
            textPosition = TOP, palette = ClassicPalette,
        ) { SidebarStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Jugá contra la IA",
            subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
            textPosition = TOP, palette = DarkPalette,
        ) { SidebarStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Jugá contra la IA",
            subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
            textPosition = TOP, palette = NaturePalette,
        ) { SidebarStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Jugá contra la IA",
            subtitle = "4 niveles de dificultad,\ndesde principiante hasta campeón",
            textPosition = TOP, palette = GrayscalePalette,
        ) { SidebarStoreScreenshotContent(GrayscalePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Play against the AI",
            subtitle = "4 difficulty levels,\nfrom beginner to champion",
            textPosition = TOP, palette = ClassicPalette,
        ) { SidebarStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Play against the AI",
            subtitle = "4 difficulty levels,\nfrom beginner to champion",
            textPosition = TOP, palette = DarkPalette,
        ) { SidebarStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Play against the AI",
            subtitle = "4 difficulty levels,\nfrom beginner to champion",
            textPosition = TOP, palette = NaturePalette,
        ) { SidebarStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGameSidebar02_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Play against the AI",
            subtitle = "4 difficulty levels,\nfrom beginner to champion",
            textPosition = TOP, palette = GrayscalePalette,
        ) { SidebarStoreScreenshotContent(GrayscalePalette) }
    }
}
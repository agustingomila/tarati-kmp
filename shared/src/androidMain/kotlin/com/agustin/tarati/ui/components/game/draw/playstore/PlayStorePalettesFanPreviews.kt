package com.agustin.tarati.ui.components.game.draw.playstore

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.game.previews.GameScreenPreview_GameInProgress
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.TaratiTheme
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 04 — Abanico de paletas de colores              TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────
//  Tres teléfonos en forma de abanico mostrando la misma pantalla de juego
//  con diferentes paletas.
//  Asignación: izquierda → Classic, centro → Dark (primer plano), derecha → Nature.

@Composable
private fun PaletteCard(palette: BoardPalette) {
    TaratiTheme(palette = palette) {
        GameScreenPreview_GameInProgress(palette)
    }
}

// ── ES — Dark center (variante principal) ─────────────────────────────────────

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StorePalettesFan04_ES_DarkCenter() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotFanCompose(
            leftContent = { PaletteCard(ClassicPalette) },
            centerContent = { PaletteCard(DarkPalette) },
            rightContent = { PaletteCard(NaturePalette) },
            leftPalette = ClassicPalette,
            centerPalette = DarkPalette,
            rightPalette = NaturePalette,
            bgPalette = DarkPalette,
            title = "Personalizá tu tablero",
            subtitle = "Elegí entre 6 paletas de colores\ny temas desbloqueables de temporada",
        )
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StorePalettesFan04_ES_GrayscaleCenter() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreScreenshotFanCompose(
            leftContent = { PaletteCard(ClassicPalette) },
            centerContent = { PaletteCard(GrayscalePalette) },
            rightContent = { PaletteCard(NaturePalette) },
            leftPalette = ClassicPalette,
            centerPalette = GrayscalePalette,
            rightPalette = NaturePalette,
            bgPalette = GrayscalePalette,
            title = "Personalizá tu tablero",
            subtitle = "Elegí entre 6 paletas de colores\ny temas desbloqueables de temporada",
        )
    }
}

// ── EN — Dark center (variante principal) ─────────────────────────────────────

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StorePalettesFan04_EN_DarkCenter() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotFanCompose(
            leftContent = { PaletteCard(ClassicPalette) },
            centerContent = { PaletteCard(DarkPalette) },
            rightContent = { PaletteCard(NaturePalette) },
            leftPalette = ClassicPalette,
            centerPalette = DarkPalette,
            rightPalette = NaturePalette,
            bgPalette = DarkPalette,
            title = "Personalize your board",
            subtitle = "Choose from 6 color palettes\nand unlockable seasonal themes",
        )
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StorePalettesFan04_EN_GrayscaleCenter() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreScreenshotFanCompose(
            leftContent = { PaletteCard(ClassicPalette) },
            centerContent = { PaletteCard(GrayscalePalette) },
            rightContent = { PaletteCard(NaturePalette) },
            leftPalette = ClassicPalette,
            centerPalette = GrayscalePalette,
            rightPalette = NaturePalette,
            bgPalette = GrayscalePalette,
            title = "Personalize your board",
            subtitle = "Choose from 6 color palettes\nand unlockable seasonal themes",
        )
    }
}
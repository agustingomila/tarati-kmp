package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.R
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_H_DP
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_W_DP
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette

// ═════════════════════════════════════════════════════════════════════════════
//  04 — Abanico de paletas
//
//  Grupos: PlayStore_ES / PlayStore_EN
//
//  Asignación de tarjetas (personalizable):
//    izquierda → classic,  centro → dark (primer plano),  derecha → nature
//
//  Fondo neutro oscuro (DarkPalette) para que todas las paletas destaquen.
// ═════════════════════════════════════════════════════════════════════════════

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot04b_ES(): Unit = PlayStoreScreenshotFan(
    left = FanCard(R.drawable.ss_01_en_classic, ClassicPalette),
    center = FanCard(R.drawable.ss_01_en_dark, DarkPalette),
    right = FanCard(R.drawable.ss_01_en_nature, NaturePalette),
    title = "Personalizá tu tablero",
    subtitle = "Elegí entre 6 paletas de colores\ny temas desbloqueables de temporada",
    bgPalette = DarkPalette,
)

@Preview(group = "PlayStore_ES", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot04b_ES_GrayscaleCenter(): Unit = PlayStoreScreenshotFan(
    left = FanCard(R.drawable.ss_01_en_classic, ClassicPalette),
    center = FanCard(R.drawable.ss_01_en_grayscale, GrayscalePalette),
    right = FanCard(R.drawable.ss_01_en_nature, NaturePalette),
    title = "Personalizá tu tablero",
    subtitle = "Elegí entre 6 paletas de colores\ny temas desbloqueables de temporada",
    bgPalette = GrayscalePalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot04b_EN(): Unit = PlayStoreScreenshotFan(
    left = FanCard(R.drawable.ss_01_en_classic, ClassicPalette),
    center = FanCard(R.drawable.ss_01_en_dark, DarkPalette),
    right = FanCard(R.drawable.ss_01_en_nature, NaturePalette),
    title = "Personalize your board", subtitle = "Choose from 6 color palettes\nand unlockable seasonal themes",
    bgPalette = DarkPalette,
)

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreScreenshot04b_EN_GrayscaleCenter(): Unit = PlayStoreScreenshotFan(
    left = FanCard(R.drawable.ss_01_en_classic, ClassicPalette),
    center = FanCard(R.drawable.ss_01_en_grayscale, GrayscalePalette),
    right = FanCard(R.drawable.ss_01_en_nature, NaturePalette),
    title = "Personalize your board", subtitle = "Choose from 6 color palettes\nand unlockable seasonal themes",
    bgPalette = GrayscalePalette,
)
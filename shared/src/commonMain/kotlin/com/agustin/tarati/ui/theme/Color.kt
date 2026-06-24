package com.agustin.tarati.ui.theme

import androidx.compose.ui.graphics.Color

// ── Esquema oscuro ────────────────────────────────────────────────────────────
val DarkPrimary: Color = Color(0xFFB69CEC)
val DarkSecondary: Color = Color(0xFF7C5FD9)
val DarkTertiary: Color = Color(0xFFEFCE57)

// ── Esquema claro ─────────────────────────────────────────────────────────────
val LightPrimary: Color = Color(0xFF6650a4)
val LightSecondary: Color = Color(0xFF625b71)
val LightTertiary: Color = Color(0xFF7D5279)

// ── Fondos y superficies ──────────────────────────────────────────────────────
val DarkBackground: Color = Color(0xFF1A1420)
val DarkSurface: Color = Color(0xFF231A2A)
val LightBackground: Color = Color(0xFFF8F4FF)
val LightSurface: Color = Color(0xFFF5F0FC)

// ── Colores de texto (esquema oscuro) ─────────────────────────────────────────
val OnPrimaryDark: Color = Color(0xFF2E1F52)
val OnSecondaryDark: Color = Color(0xFF2E1F52)
val OnTertiaryDark: Color = Color(0xFF2E1F52)
val OnBackgroundDark: Color = Color(0xFFE5DFF5)
val OnSurfaceDark: Color = Color(0xFFE5DFF5)

// ── Colores de texto (esquema claro) ──────────────────────────────────────────
val OnBackgroundLight: Color = Color(0xFF3A2C5D)
val OnSurfaceLight: Color = Color(0xFF3A2C5D)
val OnPrimaryLight: Color = Color.White
val OnSecondaryLight: Color = Color.White
val OnTertiaryLight: Color = Color.White

// ── Error y acento ────────────────────────────────────────────────────────────
val ErrorVariant: Color = Color(0xFF9D62F0)
val Tarati: Color = Color(0xFF57936B)

// ── Extensiones de manipulación de color ─────────────────────────────────────

/**
 * Interpola el color hacia blanco por [factor] (0 = sin cambio, 1 = blanco puro).
 * Se usa para generar containers y variantes claras a partir de colores de paleta.
 */
fun Color.lighten(factor: Float): Color {
    val f = factor.coerceIn(0f, 1f)
    return Color(
        red = red + (1f - red) * f,
        green = green + (1f - green) * f,
        blue = blue + (1f - blue) * f,
        alpha = alpha,
    )
}

/**
 * Escala el color hacia negro por [factor] (0 = sin cambio, 1 = negro puro).
 * Se usa para generar fondos oscuros y containers oscuros a partir de colores de paleta.
 */
fun Color.darken(factor: Float): Color {
    val f = factor.coerceIn(0f, 1f)
    return Color(
        red = red * (1f - f),
        green = green * (1f - f),
        blue = blue * (1f - f),
        alpha = alpha,
    )
}
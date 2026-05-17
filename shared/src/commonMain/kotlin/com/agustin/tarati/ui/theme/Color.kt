package com.agustin.tarati.ui.theme

import androidx.compose.ui.graphics.Color

// ── Esquema oscuro ────────────────────────────────────────────────────────────
val DarkPrimary = Color(0xFFB69CEC)
val DarkSecondary = Color(0xFF7C5FD9)
val DarkTertiary = Color(0xFFEFCE57)

// ── Esquema claro ─────────────────────────────────────────────────────────────
val LightPrimary = Color(0xFF6650a4)
val LightSecondary = Color(0xFF625b71)
val LightTertiary = Color(0xFF7D5279)

// ── Fondos y superficies ──────────────────────────────────────────────────────
val DarkBackground = Color(0xFF1A1420)
val DarkSurface = Color(0xFF231A2A)
val LightBackground = Color(0xFFF8F4FF)
val LightSurface = Color(0xFFF5F0FC)

// ── Colores de texto (esquema oscuro) ─────────────────────────────────────────
val OnPrimaryDark = Color(0xFF2E1F52)
val OnSecondaryDark = Color(0xFF2E1F52)
val OnTertiaryDark = Color(0xFF2E1F52)
val OnBackgroundDark = Color(0xFFE5DFF5)
val OnSurfaceDark = Color(0xFFE5DFF5)

// ── Colores de texto (esquema claro) ──────────────────────────────────────────
val OnBackgroundLight = Color(0xFF3A2C5D)
val OnSurfaceLight = Color(0xFF3A2C5D)
val OnPrimaryLight = Color.White
val OnSecondaryLight = Color.White
val OnTertiaryLight = Color.White

// ── Error y acento ────────────────────────────────────────────────────────────
val ErrorVariant = Color(0xFF9D62F0)
val Tarati = Color(0xFF57936B)

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
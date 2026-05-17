// PaletteColorScheme.kt — archivo completo corregido
package com.agustin.tarati.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Deriva un [ColorScheme] de Material 3 a partir de los colores de la paleta del tablero.
 *
 * ## Estrategia de derivación
 *
 * En lugar de definir un [ColorScheme] hardcodeado por paleta, los roles se
 * calculan algorítmicamente desde los colores que cada paleta ya tiene definidos:
 *
 * | Rol M3                    | Fuente (modo claro)               | Fuente (modo oscuro)               |
 * |---------------------------|-----------------------------------|------------------------------------|
 * | primary                   | `neutralColor`                    | `neutralColor.lighten(0.3f)`       |
 * | secondary                 | `boardPatternColor1`              | `boardPatternColor1.lighten(0.2f)` |
 * | tertiary                  | `boardPerimeterColor`             | `boardPerimeterColor.lighten(0.2f)`|
 * | background                | `boardPatternColor3.lighten(45%)`| `boardBackground.darken(60%)`      |
 * | surface                   | `boardPatternColor3.lighten(30%)`| `boardBackground.darken(45%)`      |
 * | surfaceVariant            | `boardPatternColor3.lighten(10%)`| `boardBackground.darken(30%)`      |
 * | surfaceContainerLowest    | `boardPatternColor3.lighten(55%)`| `boardBackground.darken(70%)`      |
 * | surfaceContainerLow       | `boardPatternColor3.lighten(42%)`| `boardBackground.darken(55%)`      |
 * | surfaceContainer          | `boardPatternColor3.lighten(30%)`| `boardBackground.darken(42%)`      |
 * | surfaceContainerHigh      | `boardPatternColor3.lighten(18%)`| `boardBackground.darken(28%)`      |
 * | surfaceContainerHighest   | `boardPatternColor3.lighten(8%)` | `boardBackground.darken(15%)`      |
 * | surfaceDim                | `boardPatternColor3.lighten(18%)`| `boardBackground.darken(55%)`      |
 * | surfaceBright             | `boardPatternColor3.lighten(50%)`| `boardBackground.darken(25%)`      |
 * | surfaceTint               | `primary`                         | `primary`                          |
 * | onBackground/Surface      | `boardVertexColor`                | fijo `0xFFE8E0F0`                  |
 *
 * Los colores "on" se derivan automáticamente por luminancia para garantizar
 * legibilidad (texto oscuro sobre fondos claros, claro sobre fondos oscuros).
 *
 * Esta función trabaja para todas las paletas actuales y estacionales sin
 * necesitar mantenimiento adicional al agregar nuevas paletas.
 */
fun BoardPalette.toColorScheme(darkTheme: Boolean): ColorScheme {
    fun onColor(base: Color): Color =
        if (base.luminance() > 0.35f) Color(0xFF1A1A1A) else Color(0xFFF5F0EB)

    return if (!darkTheme) {
        toLight(::onColor)
    } else {
        toDark(::onColor)
    }
}

private fun BoardPalette.toLight(onColor: (Color) -> Color): ColorScheme {
    val primary = neutralColor
    val secondary = boardPatternColor1
    val tertiary = boardPerimeterColor
    val bg = boardPatternColor3.lighten(0.45f)
    val surface = boardPatternColor3.lighten(0.30f)

    return lightColorScheme(
        primary = primary,
        onPrimary = onColor(primary),
        primaryContainer = primary.lighten(0.55f),
        onPrimaryContainer = primary.darken(0.35f),

        secondary = secondary,
        onSecondary = onColor(secondary),
        secondaryContainer = secondary.lighten(0.50f),
        onSecondaryContainer = secondary.darken(0.35f),

        tertiary = tertiary,
        onTertiary = onColor(tertiary),
        tertiaryContainer = tertiary.lighten(0.50f),
        onTertiaryContainer = tertiary.darken(0.30f),

        background = bg,
        onBackground = boardVertexColor,
        surface = surface,
        onSurface = boardVertexColor,
        surfaceVariant = boardPatternColor3.lighten(0.10f),
        onSurfaceVariant = boardVertexColor.lighten(0.20f),

        surfaceContainerLowest = boardPatternColor3.lighten(0.55f),
        surfaceContainerLow = boardPatternColor3.lighten(0.42f),
        surfaceContainer = boardPatternColor3.lighten(0.30f),
        surfaceContainerHigh = boardPatternColor3.lighten(0.18f),
        surfaceContainerHighest = boardPatternColor3.lighten(0.08f),

        surfaceDim = boardPatternColor3.lighten(0.18f),
        surfaceBright = boardPatternColor3.lighten(0.50f),
        surfaceTint = primary,

        outline = boardEdgeColor.lighten(0.20f),
        outlineVariant = boardEdgeColor.lighten(0.45f),

        error = ErrorVariant,
        onError = Color.White,
        errorContainer = ErrorVariant.lighten(0.55f),
        onErrorContainer = ErrorVariant.darken(0.40f),

        scrim = Color.Black,
    )
}

private fun BoardPalette.toDark(onColor: (Color) -> Color): ColorScheme {
    val primary = neutralColor.lighten(0.30f)
    val secondary = boardPatternColor1.lighten(0.25f)
    val tertiary = boardPerimeterColor.lighten(0.20f)
    val bg = boardBackground.darken(0.60f)
    val surface = boardBackground.darken(0.45f)

    return darkColorScheme(
        primary = primary,
        onPrimary = onColor(primary),
        primaryContainer = neutralColor.darken(0.25f),
        onPrimaryContainer = neutralColor.lighten(0.55f),

        secondary = secondary,
        onSecondary = onColor(secondary),
        secondaryContainer = boardPatternColor1.darken(0.40f),
        onSecondaryContainer = boardPatternColor1.lighten(0.45f),

        tertiary = tertiary,
        onTertiary = onColor(tertiary),
        tertiaryContainer = boardPerimeterColor.darken(0.45f),
        onTertiaryContainer = boardPerimeterColor.lighten(0.45f),

        background = bg,
        onBackground = Color(0xFFE8E0F0),
        surface = surface,
        onSurface = Color(0xFFE8E0F0),
        surfaceVariant = boardBackground.darken(0.30f),
        onSurfaceVariant = Color(0xFFCAC4D0),

        surfaceContainerLowest = boardBackground.darken(0.70f),
        surfaceContainerLow = boardBackground.darken(0.55f),
        surfaceContainer = boardBackground.darken(0.42f),
        surfaceContainerHigh = boardBackground.darken(0.28f),
        surfaceContainerHighest = boardBackground.darken(0.15f),

        surfaceDim = boardBackground.darken(0.55f),
        surfaceBright = boardBackground.darken(0.25f),
        surfaceTint = primary,

        outline = boardEdgeColor.lighten(0.20f),
        outlineVariant = boardEdgeColor.darken(0.10f),

        error = ErrorVariant.lighten(0.25f),
        onError = Color(0xFF690005),
        errorContainer = ErrorVariant.darken(0.45f),
        onErrorContainer = ErrorVariant.lighten(0.45f),

        scrim = Color.Black,
    )
}
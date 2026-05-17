package com.agustin.tarati.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

enum class AppTheme {
    MODE_AUTO,
    MODE_DAY,
    MODE_NIGHT,
}

/**
 * Tema global de la aplicación.
 *
 * ## Paleta y aislamiento en previews
 * El parámetro [palette] desacopla los previews del `PaletteManager` global:
 *
 * - **Producción** (`palette = null`): lee `PaletteManager.currentPalette` (un
 *   `State`), por lo que reacciona a los cambios de paleta del usuario.
 * - **Previews** (`palette = X`): usa `X` directamente sin leer el State global.
 *   Ningún cambio externo invalida esta composición — previews aislados entre sí.
 *
 * ## LocalBoardPalette
 * La paleta resuelta se provee como `LocalBoardPalette` para que todos los
 * composables descendientes — incluyendo el renderizador del tablero — la
 * consuman vía `LocalBoardPalette.current` en lugar de acceder al singleton
 * directamente. Esto garantiza que el tablero use la misma paleta que el resto
 * de la UI tanto en producción como en previews.
 *
 * @param darkTheme    Aplica modo oscuro.
 * @param dynamicColor Color dinámico del sistema (Android 12+). Tiene precedencia
 *                     sobre [palette] y [PaletteManager] si está activo.
 * @param palette      Paleta explícita. `null` activa el comportamiento reactivo
 *                     vía [PaletteManager].
 */
@Composable
fun TaratiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    palette: BoardPalette? = null,
    content: @Composable () -> Unit,
) {
    // Cuando palette != null no se lee PaletteManager.currentPalette,
    // evitando la subscripción al State global que causa contaminación entre previews.
    val resolvedPalette = palette ?: PaletteManager.currentPalette

    val colorScheme = resolvedPalette.toColorScheme(darkTheme)

    // LocalBoardPalette provee la paleta a todos los descendientes, incluyendo
    // getBoardColors() → rememberBoardColors(), que la lee desde el CompositionLocal
    // en lugar del PaletteManager singleton.
    CompositionLocalProvider(LocalBoardPalette provides resolvedPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
package com.agustin.tarati.features.library.previews

import androidx.compose.runtime.Composable
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.TaratiTheme

// ── Helper de preview ─────────────────────────────────────────────────────────

/**
 * Wrapper de tema para previews.
 *
 * Pasa [palette] directamente a [TaratiTheme] en lugar de llamar a
 * `PaletteManager.setPalette`. `TaratiTheme` solo suscribe al State global del
 * `PaletteManager` cuando `palette == null`, así que al pasarla explícitamente
 * cada preview queda completamente aislado: los cambios de paleta de otros
 * previews no invalidan esta composición.
 */
@Composable
fun PreviewContainer(
    palette: BoardPalette = ClassicPalette,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    TaratiTheme(darkTheme = darkTheme, palette = palette, content = content)
}
package com.agustin.tarati.ui.layout

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Categorías de layout según el ancho disponible de la ventana.
 *
 * Se deriva una sola vez en [AppContent] vía [BoxWithConstraints] y se
 * propaga hacia abajo a través de [LocalScreenLayout]. Cualquier composable
 * puede consumirlo sin recibir parámetros adicionales.
 *
 * Añadir nuevos escenarios solo requiere extender este enum y actualizar
 * [screenLayoutFor] — los sitios de consumo no cambian.
 */
enum class ScreenLayout {
    /** Pantalla estrecha (< 600dp) — móvil portrait, sidebar como drawer modal. */
    Compact,

    /** Pantalla media (600–840dp) — tablet / landscape de teléfono. */
    Medium,

    /** Pantalla ancha (≥ 840dp) — PC / tablet grande, sidebar permanente, multi-panel. */
    Expanded,
}

/** Breakpoints en dp. Modificar aquí afecta a todos los consumidores. */
object ScreenLayoutBreakpoints {
    val Medium = 600.dp
    val Expanded = 840.dp
}

/** Derivación pura: ancho → categoría de layout. */
fun screenLayoutFor(width: Dp): ScreenLayout = when {
    width >= ScreenLayoutBreakpoints.Expanded -> ScreenLayout.Expanded
    width >= ScreenLayoutBreakpoints.Medium   -> ScreenLayout.Medium
    else                                      -> ScreenLayout.Compact
}

/**
 * CompositionLocal que expone la categoría de layout activa.
 * Default: [ScreenLayout.Compact] (conservador — no asume pantalla grande).
 */
val LocalScreenLayout = compositionLocalOf { ScreenLayout.Compact }

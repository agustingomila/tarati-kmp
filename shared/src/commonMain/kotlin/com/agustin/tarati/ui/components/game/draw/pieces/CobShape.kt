package com.agustin.tarati.ui.components.game.draw.pieces

import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Descriptor visual completo de una pieza poligonal: geometría + esquema de color.
 *
 * @param borderPattern Guarda decorativa del borde inset. [BorderPattern.None] conserva
 *                      el comportamiento original (franja sólida).
 * @param centerMotif   Motivo central del Rok. [CenterMotif.Default] conserva el
 *                      polígono escalado original. [CenterMotif.None] suprime el centro.
 */
data class CobShape(
    val shape: MorphShape,
    val colorScheme: ShapeColorScheme = CobColorScheme.Default,
    val borderPattern: BorderPattern = BorderPattern.None,
    val centerMotif: CenterMotif = CenterMotif.Default,
)
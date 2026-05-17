package com.agustin.tarati.ui.components.game.draw.pieces

/**
 * Preferencia del usuario para el tipo de animación al capturar piezas.
 *
 * Se persiste en DataStore y se resuelve a [ConversionAnimationType] en
 * [BoardAnimationViewModel].
 */
enum class ConversionAnimationStyle {
    /** FROM_CENTER o FROM_BORDER, elegido al azar por captura. */
    TRANSFORMATION,

    /** Volteo de moneda siempre. */
    FLIP,

    /** Elige al azar entre los tres tipos disponibles por cada captura. */
    SURPRISE,
}
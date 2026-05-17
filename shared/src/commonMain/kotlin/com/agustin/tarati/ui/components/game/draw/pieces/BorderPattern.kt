package com.agustin.tarati.ui.components.game.draw.pieces

// ─────────────────────────────────────────────────────────────────────────────
// BorderPattern — catálogo de guardas
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Guarda decorativa para la banda de borde interior de una pieza poligonal.
 *
 * La función de dibujo [drawBorderPattern] está en DrawBorderPattern.kt
 * como expect/actual porque usa android.graphics.PathMeasure y asAndroidPath().
 */
sealed class BorderPattern {

    open val minPerSide: Int get() = 1
    open val arcMarginFactor: Float get() = 1f
    open val stretchable: Boolean get() = false

    object None : BorderPattern()
    object DoubleRing : BorderPattern()

    object Fishtail : BorderPattern() {
        override val minPerSide: Int get() = 2
        override val arcMarginFactor: Float get() = 1f
        override val stretchable: Boolean get() = true
    }

    object Diamonds : BorderPattern() {
        override val minPerSide: Int get() = 2
        override val arcMarginFactor: Float get() = 1f
    }

    object Chevron : BorderPattern() {
        override val minPerSide: Int get() = 1
        override val arcMarginFactor: Float get() = 1f
    }

    object Meander : BorderPattern() {
        override val minPerSide: Int get() = 2
        override val arcMarginFactor: Float get() = 1f
        override val stretchable: Boolean get() = true
    }
}
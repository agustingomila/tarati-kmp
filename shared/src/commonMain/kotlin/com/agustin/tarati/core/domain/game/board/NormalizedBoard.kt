package com.agustin.tarati.core.domain.game.board

data class NormalizedBoard(
    val x: Float,
    val y: Float,
) {
    fun rotate(orientation: BoardOrientation): NormalizedBoard =
        when (orientation) {
            BoardOrientation.PORTRAIT_WHITE -> NormalizedBoard(x, y)
            BoardOrientation.PORTRAIT_BLACK -> NormalizedBoard(1 - x, 1 - y)
            BoardOrientation.LANDSCAPE_WHITE -> NormalizedBoard(1 - y, x)
            BoardOrientation.LANDSCAPE_BLACK -> NormalizedBoard(y, 1 - x)
        }
}

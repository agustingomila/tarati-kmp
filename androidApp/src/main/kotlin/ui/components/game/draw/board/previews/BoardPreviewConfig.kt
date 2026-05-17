package com.agustin.tarati.ui.components.game.draw.board.previews

import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.theme.BoardColors

data class BoardPreviewConfig(
    val gameState: GameState,
    val playerSide: CobColor = CobColor.WHITE,
    val orientation: BoardOrientation,
    val boardVisualState: BoardVisualState,
    val isEditing: Boolean = false,
    val darkTheme: Boolean = false,
    val boardColors: BoardColors,
    val debug: Boolean = false,
)

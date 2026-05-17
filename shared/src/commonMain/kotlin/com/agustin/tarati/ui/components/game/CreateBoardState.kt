package com.agustin.tarati.ui.components.game

import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.features.settings.BoardVisualState

data class CreateBoardState(
    val gameState: GameState,
    val playerSide: CobColor,
    val aiEnabled: Boolean,
    val whiteIsAI: Boolean,
    val blackIsAI: Boolean,
    val isEditing: Boolean,
    val isTutorialActive: Boolean,
    val isAIThinking: Boolean,
    val boardOrientation: BoardOrientation,
    val editBoardOrientation: BoardOrientation,
    val boardVisualState: BoardVisualState,
)
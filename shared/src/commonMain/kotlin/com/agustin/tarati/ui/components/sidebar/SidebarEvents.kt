package com.agustin.tarati.ui.components.sidebar


import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.Move

interface SidebarEvents {
    fun onMoveToCurrent()

    fun onMoveToIndex(moveIndex: Int)

    fun onUndo()

    fun onRedo()

    fun onDifficultyChangeWhite(difficulty: Difficulty)

    fun onDifficultyChangeBlack(difficulty: Difficulty)

    fun onSetPlayerIsAI(color: CobColor, isAI: Boolean)

    fun onSettings()

    fun onNewGame(color: CobColor)

    fun onEditBoard()

    fun onRotateBoard()

    fun onGamesLibrary()

    fun onOnlineLobby()

    fun onSaveGame()

    fun onAboutClick()

    fun onCopyMoveHistory(moves: List<Move>)

    fun onShowAchievements()
}
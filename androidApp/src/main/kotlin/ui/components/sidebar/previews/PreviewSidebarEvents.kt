package com.agustin.tarati.ui.components.sidebar.previews

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.ui.components.sidebar.SidebarEvents

class PreviewSidebarEvents : SidebarEvents {
    override fun onMoveToCurrent() {}

    override fun onMoveToIndex(moveIndex: Int) {}

    override fun onUndo() {}

    override fun onRedo() {}

    override fun onDifficultyChangeWhite(difficulty: Difficulty) {}

    override fun onDifficultyChangeBlack(difficulty: Difficulty) {}

    override fun onSetPlayerIsAI(color: CobColor, isAI: Boolean) {}

    override fun onSettings() {}

    override fun onNewGame(color: CobColor) {}

    override fun onEditBoard() {}

    override fun onRotateBoard() {}

    override fun onGamesLibrary() {}

    override fun onSaveGame() {}

    override fun onAboutClick() {}

    override fun onCopyMoveHistory(moves: List<Move>) {}

    override fun onShowAchievements() {}
}
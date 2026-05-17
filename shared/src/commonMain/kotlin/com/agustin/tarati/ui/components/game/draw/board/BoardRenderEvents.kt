package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.Size
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.play.GameState

interface BoardRenderEvents {
    fun onReset()

    fun onBoardSizeChange(size: Size)

    fun onUpdateBoardOrientation(orientation: BoardOrientation)

    fun onSyncState(gameState: GameState)
}

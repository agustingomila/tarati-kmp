package com.agustin.tarati.ui.components.editor

import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.StateFlow

interface IEditBoardManager {
    val isEditing: StateFlow<Boolean>
    val editColor: StateFlow<CobColor>
    val editTurn: StateFlow<CobColor>
    val editBoardOrientation: StateFlow<BoardOrientation>
}
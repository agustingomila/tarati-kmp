package com.agustin.tarati.ui.components.editor

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.PieceCounts

data class EditColorState(
    val playerSide: CobColor = WHITE,
    val editColor: CobColor = WHITE,
    val editTurn: CobColor = WHITE,
)

data class EditActionState(
    val pieceCounts: PieceCounts = PieceCounts(4, 4),
    val isValidDistribution: Boolean = true,
    val isCompletedDistribution: Boolean = true,
)

data class EditColorEvents(
    val onPlayerSideToggle: () -> Unit = {},
    val onColorToggle: () -> Unit = {},
    val onTurnToggle: () -> Unit = {},
)

data class EditActionEvents(
    val onRotate: () -> Unit = {},
    val onStartGame: () -> Unit = {},
    val onClearBoard: () -> Unit = {},
    val onCopyPosition: () -> Unit = {},
    val onPastePosition: () -> Unit = {},
)

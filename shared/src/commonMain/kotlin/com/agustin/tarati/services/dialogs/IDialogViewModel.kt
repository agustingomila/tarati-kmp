package com.agustin.tarati.services.dialogs

import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.StateFlow

interface IDialogViewModel {
    val newGameColor: StateFlow<CobColor>
    val showNewGameDialog: StateFlow<Boolean>
    val showGameOverDialog: StateFlow<Boolean>
    val showAboutDialog: StateFlow<Boolean>

    fun showNewGameDialog(initialColor: CobColor = CobColor.WHITE)

    fun showGameOverDialog()

    fun showAboutDialog()

    fun resetDialogs()
}
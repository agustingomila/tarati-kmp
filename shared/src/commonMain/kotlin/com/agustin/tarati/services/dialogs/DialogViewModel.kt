package com.agustin.tarati.services.dialogs

import androidx.lifecycle.ViewModel
import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DialogViewModel :
    ViewModel(),
    IDialogViewModel {
    private val _newGameColor = MutableStateFlow(CobColor.WHITE)
    override val newGameColor: StateFlow<CobColor> = _newGameColor.asStateFlow()

    fun updateNewGameColor(color: CobColor) {
        _newGameColor.update { color }
    }

    private val _showNewGameDialog = MutableStateFlow(false)
    override val showNewGameDialog: StateFlow<Boolean> = _showNewGameDialog.asStateFlow()

    fun updateShowNewGameDialog(show: Boolean) {
        _showNewGameDialog.update { show }
    }

    private val _showGameOverDialog = MutableStateFlow(false)
    override val showGameOverDialog: StateFlow<Boolean> = _showGameOverDialog.asStateFlow()

    fun updateShowGameOverDialog(show: Boolean) {
        _showGameOverDialog.update { show }
    }

    private val _showAboutDialog = MutableStateFlow(false)
    override val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    fun updateShowAboutDialog(show: Boolean) {
        _showAboutDialog.update { show }
    }

    override fun showNewGameDialog(initialColor: CobColor) {
        updateShowGameOverDialog(false)
        updateShowAboutDialog(false)
        updateNewGameColor(initialColor)
        updateShowNewGameDialog(true)
    }

    override fun showGameOverDialog() {
        updateShowNewGameDialog(false)
        updateShowAboutDialog(false)
        updateShowGameOverDialog(true)
    }

    override fun showAboutDialog() {
        updateShowGameOverDialog(false)
        updateShowNewGameDialog(false)
        updateShowAboutDialog(true)
    }

    override fun resetDialogs() {
        updateShowGameOverDialog(false)
        updateShowNewGameDialog(false)
        updateShowAboutDialog(false)
    }
}
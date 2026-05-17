package com.agustin.tarati.services.dialogs

import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.StateFlow

class DialogService(
    private val dialogViewModel: IDialogViewModel,
) : IDialogViewModel {
    override val newGameColor: StateFlow<CobColor> get() = dialogViewModel.newGameColor
    override val showNewGameDialog: StateFlow<Boolean> get() = dialogViewModel.showNewGameDialog
    override val showGameOverDialog: StateFlow<Boolean> get() = dialogViewModel.showGameOverDialog
    override val showAboutDialog: StateFlow<Boolean> get() = dialogViewModel.showAboutDialog

    override fun showNewGameDialog(initialColor: CobColor) = dialogViewModel.showNewGameDialog(initialColor)

    override fun showGameOverDialog() = dialogViewModel.showGameOverDialog()

    override fun showAboutDialog() = dialogViewModel.showAboutDialog()

    override fun resetDialogs() = dialogViewModel.resetDialogs()
}
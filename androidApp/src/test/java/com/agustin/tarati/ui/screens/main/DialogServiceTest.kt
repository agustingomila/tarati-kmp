package com.agustin.tarati.ui.screens.main

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.dialogs.DialogService
import com.agustin.tarati.services.dialogs.DialogViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DialogServiceTest {
    @Test
    fun `should show new game dialog`() {
        val mockViewModel = mockk<DialogViewModel>()
        val dialogService = DialogService(mockViewModel)

        every { mockViewModel.showNewGameDialog(any()) } just Runs

        dialogService.showNewGameDialog(CobColor.WHITE)

        verify { mockViewModel.showNewGameDialog(CobColor.WHITE) }
    }

    @Test
    fun `should reset all dialogs`() {
        val mockViewModel = mockk<DialogViewModel>()
        val dialogService = DialogService(mockViewModel)

        every { mockViewModel.resetDialogs() } just Runs

        dialogService.resetDialogs()

        verify { mockViewModel.resetDialogs() }
    }
}

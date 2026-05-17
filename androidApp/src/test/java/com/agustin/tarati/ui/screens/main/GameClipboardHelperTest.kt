package com.agustin.tarati.ui.screens.main

import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.services.clipboard.IClipboardService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GameClipboardHelperTest {
    @Test
    fun `should copy board position`() =
        runTest {
            val mockService = mockk<IClipboardService>()
            val helper = GameClipboardHelper(mockService)

            // Simulamos que el copyText funciona correctamente
            coEvery { mockService.copyText(any(), any()) } returns true

            // Llamamos al método que queremos probar
            helper.copyBoardPosition(initialGameState().toPositionNotation())

            // Verificamos que se llamó a copyText con los parámetros correctos
            coVerify {
                mockService.copyText(
                    label = "tarati-pos",
                    text = initialGameState().toPositionNotation(),
                )
            }
        }
}

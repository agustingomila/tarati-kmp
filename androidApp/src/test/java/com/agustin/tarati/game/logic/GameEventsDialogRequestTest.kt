package com.agustin.tarati.game.logic

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.features.game.DialogRequest
import com.agustin.tarati.features.game.GameEvents
import com.agustin.tarati.features.game.IGameService
import com.agustin.tarati.services.achievements.IAchievementsManager
import com.agustin.tarati.services.clock.IClockService
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.tutorial.ITutorialService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Verifica que [GameEvents.dialogRequest] emite los eventos correctos para
 * cada acción de diálogo. Esto garantiza que el desacoplamiento entre la lógica
 * de dominio ([GameEvents]) y el bus de mensajes ([UIMessageBus]) se mantiene:
 * [GameEvents] no sabe nada de Compose; [GameScreen] es quien traduce.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameEventsDialogRequestTest {

    private lateinit var events: GameEvents

    @Before
    fun setUp() {
        val gameService = mockk<IGameService>(relaxed = true)
        every { gameService.gameState } returns MutableStateFlow(GameState.initialGameState())
        every { gameService.history } returns MutableStateFlow(StableHistoryList(emptyList()))
        every { gameService.gameStatus } returns MutableStateFlow(GameStatus.PLAYING)
        every { gameService.moveIndex } returns MutableStateFlow(-1)
        every { gameService.startedFromEditedBoard } returns MutableStateFlow(false)
        every { gameService.startedFromImportedGame } returns MutableStateFlow(false)

        events = GameEvents(
            drawerState = mockk(relaxed = true),
            gameService = gameService,
            aiEngine = mockk<IAIEngine>(relaxed = true),
            onGamesLibrary = {},
            onSaveGame = {},
            onCopyMovesToClipboard = {},
            animationCoordinator = mockk<AnimationCoordinator>(relaxed = true),
            tutorialService = mockk<ITutorialService>(relaxed = true),
            soundService = mockk<ISoundService>(relaxed = true),
            achievementsManager = mockk<IAchievementsManager>(relaxed = true),
            specialEventManager = mockk(relaxed = true),
            difficulty = { Difficulty.DEFAULT },
            playerSide = { CobColor.WHITE },
            whiteIsAI = { false },
            blackIsAI = { true },
            scope = CoroutineScope(UnconfinedTestDispatcher()),
            clockService = mockk<IClockService>(relaxed = true),
            timeControlProvider = { TimeControlMode.Unlimited },
        )
    }

    // ── showNewGameDialog ─────────────────────────────────────────────────────

    @Test
    fun `showNewGameDialog emits NewGame request with correct color`(): TestResult = runTest {
        val collected = mutableListOf<DialogRequest>()
        val job = launch(UnconfinedTestDispatcher()) {
            events.dialogRequest.collect { collected.add(it) }
        }

        events.showNewGameDialog(CobColor.BLACK)

        assertEquals(1, collected.size)
        assertEquals(DialogRequest.NewGame(CobColor.BLACK), collected[0])
        job.cancel()
    }

    @Test
    fun `showNewGameDialog emits for each call`(): TestResult = runTest {
        val collected = mutableListOf<DialogRequest>()
        val job = launch(UnconfinedTestDispatcher()) {
            events.dialogRequest.collect { collected.add(it) }
        }

        events.showNewGameDialog(CobColor.WHITE)
        events.showNewGameDialog(CobColor.BLACK)

        assertEquals(2, collected.size)
        assertEquals(DialogRequest.NewGame(CobColor.WHITE), collected[0])
        assertEquals(DialogRequest.NewGame(CobColor.BLACK), collected[1])
        job.cancel()
    }

    // ── showAboutDialog ───────────────────────────────────────────────────────

    @Test
    fun `showAboutDialog emits About request`(): TestResult = runTest {
        val collected = mutableListOf<DialogRequest>()
        val job = launch(UnconfinedTestDispatcher()) {
            events.dialogRequest.collect { collected.add(it) }
        }

        events.showAboutDialog()

        assertEquals(1, collected.size)
        assertEquals(DialogRequest.About, collected[0])
        job.cancel()
    }

    // ── gameOver ──────────────────────────────────────────────────────────────

    @Test
    fun `gameOver emits nothing before 2500ms delay`(): TestResult = runTest(StandardTestDispatcher()) {
        val collected = mutableListOf<DialogRequest>()
        val collectJob = launch { events.dialogRequest.collect { collected.add(it) } }

        events.gameOver(scope = this)

        // Advance just short of the delay
        advanceTimeBy(2_499L.milliseconds)

        assertTrue("No request before delay expires", collected.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun `gameOver emits GameOver request after 2500ms delay`(): TestResult = runTest(StandardTestDispatcher()) {
        val collected = mutableListOf<DialogRequest>()
        val collectJob = launch { events.dialogRequest.collect { collected.add(it) } }

        events.gameOver(scope = this)

        advanceTimeBy(2_501L.milliseconds)

        assertEquals(1, collected.size)
        assertEquals(DialogRequest.GameOver(CobColor.WHITE), collected[0])
        collectJob.cancel()
    }

    // ── mixed sequence ────────────────────────────────────────────────────────

    @Test
    fun `different request types are emitted in order`(): TestResult = runTest {
        val collected = mutableListOf<DialogRequest>()
        val job = launch(UnconfinedTestDispatcher()) {
            events.dialogRequest.collect { collected.add(it) }
        }

        events.showAboutDialog()
        events.showNewGameDialog(CobColor.WHITE)

        assertEquals(2, collected.size)
        assertEquals(DialogRequest.About, collected[0])
        assertEquals(DialogRequest.NewGame(CobColor.WHITE), collected[1])
        job.cancel()
    }
}

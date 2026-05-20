package com.agustin.tarati.game.logic

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.manager.GameManager
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.core.domain.game.time.TimeControlMode
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Verifies that [GameEvents.applyMove] rejects moves carrying a stale [GameState]. */
@OptIn(ExperimentalCoroutinesApi::class)
class DuplicateMoveTest {

    private lateinit var gameManager: GameManager
    private lateinit var events: GameEvents

    // Minimal board: White at C1, Black at C7
    private val initialState = GameState(
        cobs = mapOf(
            C1 to Cob(WHITE, false),
            C7 to Cob(BLACK, false),
        ),
        currentTurn = WHITE,
    )

    // A valid first White move: C1 → B1
    private val firstMove = Move(C1 to B1)

    @Before
    fun setUp() {
        gameManager = GameManager(
            GameManagerState(
                gameState = initialState,
                gameStatus = GameStatus.PLAYING,
                history = StableHistoryList(emptyList()),
                moveIndex = -1,
            )
        )

        // Wrap GameManager in a relaxed IGameService mock so GameEvents depends
        // on the interface, not the concrete class. Critical methods are delegated
        // to the real gameManager so assertions on history/gameState still work.
        val gameService = mockk<IGameService>(relaxed = true)
        every { gameService.gameState } returns gameManager.gameState
        every { gameService.history } returns gameManager.history
        every { gameService.moveIndex } returns gameManager.moveIndex
        every { gameService.gameStatus } returns gameManager.gameStatus
        every { gameService.startedFromEditedBoard } returns MutableStateFlow(false)
        every { gameService.startedFromImportedGame } returns MutableStateFlow(false)
        every { gameService.addMove(any(), any(), any()) } answers {
            gameManager.addMove(
                move = firstArg(),
                nextState = secondArg(),
                onMoveRecord = thirdArg(),
            )
        }

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
            playerSide = { WHITE },
            whiteIsAI = { false },
            blackIsAI = { true },
            scope = CoroutineScope(UnconfinedTestDispatcher()),
            clockService = mockk<IClockService>(relaxed = true),
            timeControlProvider = { TimeControlMode.Unlimited },
        )
    }

    // ── Core guard ────────────────────────────────────────────────────────────

    @Test
    fun `first applyMove with current state is accepted`() {
        events.applyMove(firstMove, initialState)

        // One entry in history means the move was recorded
        assertEquals(1, gameManager.history.value.size)
        assertEquals(firstMove, gameManager.history.value[0].move)
    }

    @Test
    fun `second applyMove with stale state is rejected`() {
        // Simulates first tap: move is accepted and GameManager advances to S1
        events.applyMove(firstMove, initialState)
        assertEquals(1, gameManager.history.value.size)

        // Simulates second tap before Compose recompose:
        // the pointerInput coroutine still holds initialState (S0), but
        // gameManager.gameState.value is now S1 — the guard must reject this.
        events.applyMove(firstMove, initialState)

        // History must still have exactly 1 entry, not 2
        assertEquals(
            "Duplicate move from stale Compose state must be rejected",
            1,
            gameManager.history.value.size,
        )
    }

    @Test
    fun `sequential valid moves are both accepted`() {
        // First move: White C1 → B1
        events.applyMove(firstMove, initialState)
        assertEquals(1, gameManager.history.value.size)

        // Second move: Black plays from the new live state (simulates recompose)
        val stateAfterFirst = gameManager.gameState.value
        val secondMove = Move(C7 to B4)
        events.applyMove(secondMove, stateAfterFirst)

        assertEquals(
            "Both sequential moves must be recorded",
            2,
            gameManager.history.value.size,
        )
    }

    @Test
    fun `applyMove with wrong state in middle of history is rejected`() {
        // Apply two moves normally
        events.applyMove(firstMove, initialState)
        val stateAfterFirst = gameManager.gameState.value
        events.applyMove(Move(C7 to B4), stateAfterFirst)
        assertEquals(2, gameManager.history.value.size)

        // Now try to inject a move using an old intermediate state (S1)
        // This could happen if a slow device queues a gesture from a previous turn
        events.applyMove(Move(C1 to B1), stateAfterFirst)

        assertEquals(
            "Move against superseded state must be rejected",
            2,
            gameManager.history.value.size,
        )
    }

    @Test
    fun `applyMove guard uses live StateFlow value not Compose snapshot`() {
        // After the first valid move, gameManager.gameState.value is S1
        events.applyMove(firstMove, initialState)
        val liveStateAfterMove = gameManager.gameState.value

        // S0 and S1 must be different values
        assert(initialState != liveStateAfterMove) {
            "GameManager must have advanced to a new state after addMove"
        }

        // A call with S0 is rejected precisely because gameState.value is S1
        events.applyMove(firstMove, initialState)
        assertEquals(1, gameManager.history.value.size)
    }
}
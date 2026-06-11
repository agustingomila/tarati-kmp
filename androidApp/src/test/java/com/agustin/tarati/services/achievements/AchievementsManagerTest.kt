package com.agustin.tarati.services.achievements

import com.agustin.tarati.R
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameEndReason
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.features.online.auth.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AchievementsManagerTest {

    /**
     * Fake reporter que registra en memoria qué logros fueron desbloqueados
     * y con qué pasos, sin tocar Play Games SDK ni Activity.
     */
    private class FakeAchievementsReporter : IAchievementsReporter {
        val unlockedAchievements = mutableSetOf<Int>()
        val stepUpdates = mutableMapOf<Int, Int>()

        override fun unlock(achievementResId: Int) {
            unlockedAchievements.add(achievementResId)
        }

        override fun setSteps(achievementResId: Int, steps: Int): Boolean {
            stepUpdates[achievementResId] = steps
            return true
        }

        override fun loadAchievements(
            onResult: (List<AchievementSnapshot>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }

        fun wasUnlocked(achievementResId: Int) = achievementResId in unlockedAchievements
        fun reset() {
            unlockedAchievements.clear(); stepUpdates.clear()
        }
    }

    private lateinit var reporter: FakeAchievementsReporter
    private lateinit var repository: AchievementsRepository
    private lateinit var manager: AchievementsManager

    @Before
    fun setUp() {
        reporter = FakeAchievementsReporter()
        repository = mockk(relaxed = true) {
            coEvery { incrementTotalCaptures(any()) } returns 1
            coEvery { incrementTotalPromotions() } returns 1
            coEvery { incrementTotalWins() } returns 1
            coEvery { incrementTotalGames() } returns 1
            coEvery { getCachedSteps(any()) } returns 0
        }
        manager = AchievementsManager(
            context = mockk(relaxed = true),
            repository = repository,
            activityProvider = mockk(relaxed = true),
            reporter = reporter,
            aiEngine = mockk<IAIEngine>(relaxed = true),
            syncService = mockk(relaxed = true),
            authRepository = mockk<AuthRepository>(relaxed = true),
        )
    }

    // ── onMoveApplied ─────────────────────────────────────────────────────────

    @Test
    fun onMoveApplied_withCapture_unlocksFirstCapture() = runTest {
        val oldState = GameState.createGameState {
            setTurn(WHITE)
            setCob(C3, WHITE)
            setCob(B1, BLACK)
        }
        val move = Move(C3 to B2)
        val newState = oldState.applyMove(move)

        manager.onMoveApplied(move, oldState, newState)

        assertTrue(reporter.wasUnlocked(R.string.achievement_first_capture))
    }

    @Test
    fun onMoveApplied_withCapture_updatesFlipperSteps() = runTest {
        val oldState = GameState.createGameState {
            setTurn(WHITE)
            setCob(C3, WHITE)
            setCob(B1, BLACK)
        }
        val move = Move(C3 to B2)
        val newState = oldState.applyMove(move)
        coEvery { repository.incrementTotalCaptures(any()) } returns 10

        manager.onMoveApplied(move, oldState, newState)

        assertEquals(10, reporter.stepUpdates[R.string.achievement_the_flipper])
    }

    @Test
    fun onMoveApplied_noCapture_doesNotUnlockFirstCapture() = runTest {
        // WHITE en C3 mueve a B2, pero no hay pieza negra adyacente a B2
        val oldState = GameState.createGameState {
            setTurn(WHITE)
            setCob(C3, WHITE)
        }
        val move = Move(C3 to B2)
        val newState = oldState.applyMove(move)

        manager.onMoveApplied(move, oldState, newState)

        assertFalse(reporter.wasUnlocked(R.string.achievement_first_capture))
    }

    @Test
    fun onMoveApplied_withPromotion_unlocksFirstPromotion() = runTest {
        val oldState = GameState.createGameState {
            setTurn(WHITE)
            setCob(C7, WHITE, isUpgraded = false)
        }
        val move = Move(C7 to D3)
        val newState = oldState.applyMove(move)

        manager.onMoveApplied(move, oldState, newState)

        assertTrue(reporter.wasUnlocked(R.string.achievement_first_promotion))
    }

    // ── onGameOver ────────────────────────────────────────────────────────────

    @Test
    fun onGameOver_humanWins_unlocksFirstVictory() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_first_victory))
    }

    @Test
    fun onGameOver_humanLoses_doesNotUnlockFirstVictory() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = BLACK,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertFalse(reporter.wasUnlocked(R.string.achievement_first_victory))
    }

    @Test
    fun onGameOver_humanWinsByMit_unlocksMit() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_mit))
    }

    @Test
    fun onGameOver_humanWinsByStalemit_unlocksStalemit() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.STALEMIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_stalemit))
    }

    @Test
    fun onGameOver_humanWinsByTriple_unlocksEternalLoop() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.TRIPLE,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_eternal_loop))
    }

    @Test
    fun onGameOver_humanWinsOnChampion_unlocksChampion() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.CHAMPION)

        assertTrue(reporter.wasUnlocked(R.string.achievement_champion))
    }

    @Test
    fun onGameOver_humanWinsOnNonChampion_doesNotUnlockChampion() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.HARD)

        assertFalse(reporter.wasUnlocked(R.string.achievement_champion))
    }

    @Test
    fun onGameOver_draw_doesNotUnlockVictoryAchievements() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.FIFTY_MOVES,
            winner = null,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.CHAMPION)

        assertFalse(reporter.wasUnlocked(R.string.achievement_first_victory))
        assertFalse(reporter.wasUnlocked(R.string.achievement_champion))
    }

    @Test
    fun onGameOver_fiftyMoveDraw_unlocksFiftyMoveRule() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.FIFTY_MOVES,
            winner = null,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_fifty_move_rule))
    }

    @Test
    fun onGameOver_doesNotUnlockPlay10GamesBeforeReaching10() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = BLACK,
            moveHistory = emptyMap(),
        )
        coEvery { repository.incrementTotalGames() } returns 5

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertFalse(reporter.wasUnlocked(R.string.achievement_play_10_games))
    }

    // ── Difficulty achievements ───────────────────────────────────────────────

    @Test
    fun onGameOver_humanWinsOnEasy_unlocksApprentice() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.EASY)

        assertTrue(reporter.wasUnlocked(R.string.achievement_apprentice))
    }

    @Test
    fun onGameOver_humanWinsOnMedium_unlocksStrategist() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertTrue(reporter.wasUnlocked(R.string.achievement_strategist))
    }

    @Test
    fun onGameOver_humanWinsOnHard_unlocksTactician() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.HARD)

        assertTrue(reporter.wasUnlocked(R.string.achievement_tactician))
    }

    @Test
    fun onGameOver_humanWinsOnEasy_doesNotUnlockStrategistOrTactician() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.EASY)

        assertFalse(reporter.wasUnlocked(R.string.achievement_strategist))
        assertFalse(reporter.wasUnlocked(R.string.achievement_tactician))
    }

    @Test
    fun onGameOver_humanWinsOnMedium_doesNotUnlockApprenticeOrTactician() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = WHITE,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.DEFAULT)

        assertFalse(reporter.wasUnlocked(R.string.achievement_apprentice))
        assertFalse(reporter.wasUnlocked(R.string.achievement_tactician))
    }

    @Test
    fun onGameOver_humanLoses_doesNotUnlockDifficultyAchievements() = runTest {
        val matchState = MatchState(
            gameState = mockk(relaxed = true),
            gameEndReason = GameEndReason.MIT,
            winner = BLACK,
            moveHistory = emptyMap(),
        )

        manager.onGameOver(matchState, playerSide = WHITE, difficulty = Difficulty.HARD)

        assertFalse(reporter.wasUnlocked(R.string.achievement_apprentice))
        assertFalse(reporter.wasUnlocked(R.string.achievement_strategist))
        assertFalse(reporter.wasUnlocked(R.string.achievement_tactician))
    }

    // ── onTutorialCompleted ───────────────────────────────────────────────────

    @Test
    fun onTutorialCompleted_unlocksWelcomeToTarati() = runTest {
        manager.onTutorialCompleted()

        assertTrue(reporter.wasUnlocked(R.string.achievement_welcome_to_tarati))
    }

    // ── setSteps cache guard ──────────────────────────────────────────────────

    @Test
    fun setSteps_doesNotReportWhenNoCacheProgress() = runTest {
        coEvery { repository.getCachedSteps(R.string.achievement_the_flipper) } returns 10
        coEvery { repository.incrementTotalCaptures(any()) } returns 10 // same as cached

        val oldState = GameState.createGameState {
            setTurn(WHITE)
            setCob(C1, WHITE)
            setCob(B4, BLACK)
        }
        val move = Move(C1 to B1)
        manager.onMoveApplied(move, oldState, oldState.applyMove(move))

        assertFalse(R.string.achievement_the_flipper in reporter.stepUpdates)
    }
}
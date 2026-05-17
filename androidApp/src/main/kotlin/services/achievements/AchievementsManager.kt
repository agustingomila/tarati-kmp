package com.agustin.tarati.services.achievements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.agustin.tarati.R
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.GameBoard.deadVertices
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameResult
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.ui.theme.SeasonalThemeManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.games.PlayGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages all Google Play Games achievements for Tarati.
 *
 * All Play Games interactions are delegated to [IAchievementsReporter], keeping
 * this class free of Activity references and testable without Play Games infrastructure.
 *
 * ## Call sites in [GameEvents]:
 * - [onMoveApplied]        — every human move (captures, promotions, dead-cob win)
 * - [onGameOver]           — end of every game (win type, difficulty, counters)
 * - [onTutorialCompleted]  — when the tutorial reaches Completed state
 * - [showAchievementsUI]   — opens the Play Games achievements screen
 *
 * ## Reinstall resilience
 * On reinstall the local DataStore is wiped while Play Games retains server progress.
 * [syncFromServerIfNeeded] is called once per installation from
 * [MainActivity.onResume]. It queries Play Games and calls
 * [AchievementsRepository.ensureXAtLeast] to restore the local floor, ensuring subsequent
 * [setSteps] calls pass values that Play Games will accept.
 *
 * ## Incremental counters
 * Total captures, promotions, wins and games are persisted via [AchievementsRepository].
 * The session-level cache is used only to avoid redundant network calls within a single
 * session, never as a hard gate between sessions.
 *
 * ## Seasonal palette unlocks
 * Winning on [Difficulty.CHAMPION] on Halloween or Christmas day permanently unlocks
 * the corresponding palette via [AchievementsRepository.unlockHalloween] /
 * [AchievementsRepository.unlockChristmas], reflected in real time to
 * [SettingsViewModel].
 */
class AchievementsManager(
    private val context: Context,
    private val repository: AchievementsRepository,
    private val activityProvider: ActivityProvider,
    private val reporter: IAchievementsReporter,
    private val aiEngine: IAIEngine,
) : IAchievementsManager {
    private val logger = getLogger(javaClass.simpleName)

    // ── Startup sync ──────────────────────────────────────────────────────────

    /**
     * Queries Play Games for the current server-side progress of every incremental
     * achievement and restores local counters to match if the server has a higher value.
     * One-time operation per installation; skipped if already done this install.
     *
     * Called from [MainActivity.onResume].
     */
    suspend fun syncFromServerIfNeeded() {
        if (!repository.needsServerSync()) return

        reporter.loadAchievements(
            onResult = { achievements ->
                CoroutineScope(Dispatchers.IO).launch {
                    achievements.forEach { applyServerAchievementToLocalCounters(it) }
                    repository.markServerSyncDone()
                    logger.debug("syncFromServer: completed successfully")
                }
            },
            onFailure = { e ->
                // Don't mark sync as done — will retry on next launch.
                logger.warn("syncFromServer: failed, will retry on next launch — ${e.message}")
            },
        )
    }

    /**
     * Maps a single Play Games achievement snapshot to local state updates.
     *
     * ## Incremental achievements
     * Counters are restored to at least the server value, preventing stale-low
     * [setSteps] calls that Play Games silently ignores.
     *
     * ## One-shot palette unlocks
     * If the server reports a palette achievement as UNLOCKED, the local DataStore
     * flag is set. This is the reinstall-resilience path: DataStore is wiped on
     * reinstall while Play Games retains the server state permanently.
     * Covers all four palette achievements: Halloween, Christmas, Aurora, Ember.
     */
    private suspend fun applyServerAchievementToLocalCounters(snapshot: AchievementSnapshot) {
        // ── Incremental counters ──────────────────────────────────────────────
        if (snapshot.isIncremental) {
            val serverSteps = snapshot.currentSteps
            logger.debug("syncFromServer: ${snapshot.id} serverSteps=$serverSteps")

            when (snapshot.id) {
                context.getString(R.string.achievement_the_flipper) ->
                    repository.ensureTotalCapturesAtLeast(serverSteps)

                context.getString(R.string.achievement_rok_master) ->
                    repository.ensureTotalPromotionsAtLeast(serverSteps)

                context.getString(R.string.achievement_unstoppable),
                context.getString(R.string.achievement_grandmaster) ->
                    repository.ensureTotalWinsAtLeast(serverSteps)

                context.getString(R.string.achievement_play_10_games) ->
                    repository.ensureTotalGamesAtLeast(serverSteps)
            }
            return
        }

        // ── One-shot palette unlocks ──────────────────────────────────────────
        // If the server says the achievement is unlocked but local DataStore has
        // lost that flag (reinstall), restore it so the palette reappears in Settings.
        if (!snapshot.isUnlocked) return

        when (snapshot.id) {
            context.getString(R.string.achievement_halloween_theme) -> {
                logger.debug("syncFromServer: restoring Halloween palette unlock")
                repository.unlockHalloween()
            }

            context.getString(R.string.achievement_christmas_theme) -> {
                logger.debug("syncFromServer: restoring Christmas palette unlock")
                repository.unlockChristmas()
            }

            context.getString(R.string.achievement_the_first_light) -> {
                logger.debug("syncFromServer: restoring Aurora palette unlock")
                repository.unlockAurora()
            }

            context.getString(R.string.achievement_the_dark_side) -> {
                logger.debug("syncFromServer: restoring Ember palette unlock")
                repository.unlockEmber()
            }
        }
    }

    // ── Move-level events ─────────────────────────────────────────────────────

    /**
     * Called after every human move is applied.
     *
     * Handles:
     * - **First Capture** / **The Flipper** (incremental, 50 steps)
     * - **First Promotion** / **Rok Master** (incremental, 25 steps)
     * - **Dead but Dangerous** — promotion of a dead cob that directly wins the game.
     */
    override suspend fun onMoveApplied(
        move: Move,
        previousState: GameState,
        newState: GameState,
    ) {
        val (rokFlips, cobFlips) = move.countFlipsByType(previousState, newState)
        val totalFlips = rokFlips + cobFlips
        val isUpgrade = move.isUpgradeMove(previousState, newState)
        val isDeadCobPromotion = move.isPromotion() &&
                deadVertices[previousState.currentTurn]?.contains(move.from) == true

        if (totalFlips > 0) {
            unlock(R.string.achievement_first_capture)
            val newTotal = repository.incrementTotalCaptures(totalFlips)
            setSteps(R.string.achievement_the_flipper, newTotal, maxSteps = 50)
        }

        if (isUpgrade) {
            unlock(R.string.achievement_first_promotion)
            val newTotal = repository.incrementTotalPromotions()
            setSteps(R.string.achievement_rok_master, newTotal, maxSteps = 25)
        }

        // Dead but Dangerous: a dead cob was promoted and the game ends on this very move.
        // Only unlock if the promoting player wins — not a draw.
        if (isDeadCobPromotion && newState.isGameOver(aiEngine.positionHistory)) {
            val winner = newState.getMatchState(aiEngine.positionHistory).winner
            if (winner == previousState.currentTurn) {
                unlock(R.string.achievement_dead_but_dangerous)
            }
        }
    }

    // ── Game-over events ──────────────────────────────────────────────────────

    /**
     * Called at the end of every game.
     *
     * Handles:
     * - **First Victory** / **Unstoppable** / **Grandmaster** (win counters)
     * - **Champion** (win on CHAMPION difficulty)
     * - **Mit** / **Stalemit** / **Eternal Loop** (win type)
     * - **Play 10 Games** (game counter, regardless of outcome)
     * - **Fifty Move Rule** (draw by 50-move rule)
     * - **Seasonal palette unlock** (permanent unlock on Champion win on seasonal day)
     *
     * [playerSide] is the human player's color. Win achievements only trigger on
     * human wins. Draw achievements trigger regardless of outcome.
     */
    override suspend fun onGameOver(
        matchState: MatchState,
        playerSide: CobColor,
        difficulty: Difficulty,
    ) {
        val humanWon = matchState.winner == playerSide

        if (humanWon) {
            unlock(R.string.achievement_first_victory)

            when (matchState.gameResult) {
                GameResult.MIT -> unlock(R.string.achievement_mit)
                GameResult.STALEMIT -> unlock(R.string.achievement_stalemit)
                GameResult.TRIPLE -> unlock(R.string.achievement_eternal_loop)
                else -> Unit
            }

            // Logros por dificultad
            when (difficulty) {
                Difficulty.EASY -> unlock(R.string.achievement_apprentice)
                Difficulty.MEDIUM -> unlock(R.string.achievement_strategist)
                Difficulty.HARD -> unlock(R.string.achievement_tactician)
                Difficulty.CHAMPION -> {
                    unlock(R.string.achievement_champion)
                    unlockSeasonalPaletteIfApplicable()
                }
            }

            val totalWins = repository.incrementTotalWins()
            setSteps(R.string.achievement_unstoppable, totalWins, maxSteps = 10)
            setSteps(R.string.achievement_grandmaster, totalWins, maxSteps = 50)
        }

        if (matchState.gameResult == GameResult.FIFTY_MOVES) {
            unlock(R.string.achievement_fifty_move_rule)
        }

        val totalGames = repository.incrementTotalGames()
        setSteps(R.string.achievement_play_10_games, totalGames, maxSteps = 10)
    }

    // ── Tutorial event ────────────────────────────────────────────────────────

    /** Called when the tutorial reaches Completed state. Handles: **Welcome to Tarati**. */
    override suspend fun onTutorialCompleted() {
        unlock(R.string.achievement_welcome_to_tarati)
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    /**
     * Launches the Play Games achievements screen.
     * Requires a live Activity — silently skipped if none is available.
     * Uses [Activity.startActivity] directly since no result is needed:
     * Play Games manages its own overlay state internally.
     */
    override fun showAchievementsUI() {
        val activity = activityProvider.get() ?: run {
            getLogger().debug("showAchievementsUI skipped: no activity")
            return
        }

        getLogger().debug("showAchievementsUI: requesting intent")

        PlayGames.getAchievementsClient(activity)
            .achievementsIntent
            .addOnSuccessListener { intent ->
                getLogger().info("showAchievementsUI: intent received, launching")
                launchAchievementsIntent(intent)
            }
            .addOnFailureListener { e ->
                // ApiException: 4 = SIGN_IN_REQUIRED — estado esperado cuando el usuario
                // no tiene sesión activa de Play Games. No es un error de la app.
                val isSignInRequired = (e as? ApiException)?.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED
                if (isSignInRequired) {
                    getLogger().debug("showAchievementsUI: user not signed in to Play Games")
                    Toast.makeText(
                        context,
                        context.getString(R.string.achievements_sign_in_required),
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    getLogger().error("showAchievementsUI failed: ${e.message}", e)
                }
            }
    }

    /**
     * Launches the achievements Intent via [ActivityProvider.intentLauncher] —
     * registered in MainActivity using [androidx.activity.result.registerForActivityResult] — to avoid
     * the IntentRedirect Hardening block on Android 14+ that affects
     * [Activity.startActivity] with nested-extras intents from Play Games.
     * Falls back to [Activity.startActivity] if the launcher is unavailable.
     */
    private fun launchAchievementsIntent(intent: Intent) {
        val launcher = activityProvider.intentLauncher
        if (launcher != null) {
            launcher(intent)
        } else {
            getLogger().debug("launchAchievementsIntent: launcher unavailable, falling back to startActivity")
            activityProvider.get()?.startActivity(intent)
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Unlocks an achievement. Silently skipped when no Activity is available.
     *
     * Note: [unlock] returns void in Play Games SDK, not a Task. We wrap in
     * try/catch for immediate exceptions but cannot attach success/failure listeners.
     */
    private fun unlock(achievementResId: Int) {
        reporter.unlock(achievementResId)
    }

    /**
     * Permanently unlocks the seasonal palette for today if applicable.
     * Idempotent — safe to call when already unlocked.
     */
    private suspend fun unlockSeasonalPaletteIfApplicable() {
        when {
            SeasonalThemeManager.isHalloweenDay() -> repository.unlockHalloween()
            SeasonalThemeManager.isChristmasDay() -> repository.unlockChristmas()
        }
    }

    /**
     * Submits incremental steps to Play Games via [reporter].
     *
     * The session-level cache avoids redundant network calls within a single session.
     * It is NOT a cross-session gate: after reinstall it resets to 0 while the server
     * retains real progress. Play Games silently ignores calls with a value lower than
     * its current total, so it is always safe to call this when the local counter advances.
     */
    private suspend fun setSteps(achievementResId: Int, steps: Int, maxSteps: Int) {
        val clampedSteps = steps.coerceAtMost(maxSteps)

        val cachedThisSession = repository.getCachedSteps(achievementResId)
        if (clampedSteps <= cachedThisSession) {
            logger.debug("setSteps skipped (session cache hit): ${context.getString(achievementResId)} = $clampedSteps")
            return
        }

        val submitted = reporter.setSteps(achievementResId, clampedSteps)
        if (submitted) {
            repository.updateCachedSteps(achievementResId, clampedSteps)
        }
    }
}
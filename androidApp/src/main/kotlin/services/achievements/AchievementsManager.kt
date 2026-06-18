package com.agustin.tarati.services.achievements

import android.content.Context
import android.content.Intent
import com.agustin.tarati.R
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.features.online.auth.AuthRepository
import com.agustin.tarati.ui.theme.SeasonalThemeManager
import com.google.android.gms.games.PlayGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Gestiona los logros de Tarati en Android.
 *
 * Extiende [BaseAchievementsManager] con dos canales de entrega paralelos:
 * - **Google Play Games** vía [IAchievementsReporter]
 * - **Servidor de Tarati** vía [AchievementSyncService]
 *
 * ## Reinstall resilience
 * [syncFromServerIfNeeded] consulta Play Games una vez por instalación y restaura
 * los contadores locales desde el servidor, previniendo llamadas [setSteps] con
 * valores obsoletos que Play Games ignora silenciosamente.
 *
 * ## Caché de sesión (GPlay)
 * [setSteps] usa [AchievementsRepository.getCachedSteps] para evitar llamadas
 * redundantes a la red dentro de la misma sesión. El servidor siempre recibe
 * el valor actualizado independientemente del caché.
 */
class AchievementsManager(
    private val context: Context,
    private val repository: AchievementsRepository,
    private val activityProvider: ActivityProvider,
    private val reporter: IAchievementsReporter,
    aiEngine: IAIEngine,
    private val syncService: AchievementSyncService,
    private val authRepository: AuthRepository,
) : BaseAchievementsManager(aiEngine) {

    private val logger = getLogger(javaClass.simpleName)
    private val scope = CoroutineScope(Dispatchers.IO)

    // ── Contadores: DataStore como fuente de verdad ───────────────────────────

    override suspend fun incrementCaptures(amount: Int): Int = repository.incrementTotalCaptures(amount)
    override suspend fun incrementPromotions(): Int = repository.incrementTotalPromotions()
    override suspend fun incrementWins(): Int = repository.incrementTotalWins()
    override suspend fun incrementGames(): Int = repository.incrementTotalGames()

    // ── Entrega de logros: GPlay + servidor ───────────────────────────────────

    override suspend fun onUnlock(achievementId: AchievementId) {
        val resId = achievementId.toAndroidResId() ?: return
        reporter.unlock(resId)
        val token = authRepository.getToken()
        if (token != null) {
            scope.launch { syncService.unlock(token, achievementId) }
        }
    }

    override suspend fun onProgress(achievementId: AchievementId, steps: Int, maxSteps: Int) {
        val resId = achievementId.toAndroidResId() ?: return
        val clamped = steps.coerceAtMost(maxSteps)
        setStepsGPlay(resId, clamped)
        val token = authRepository.getToken()
        if (token != null) {
            scope.launch { syncService.progress(token, achievementId, clamped) }
        }
    }

    /** Paleta estacional: desbloqueo en GPlay + servidor (desde super) + DataStore local. */
    override suspend fun onChampionWin() {
        super.onChampionWin()  // → onUnlock(HALLOWEEN/CHRISTMAS) → GPlay + servidor
        when {
            SeasonalThemeManager.isHalloweenDay() -> repository.unlockHalloween()
            SeasonalThemeManager.isChristmasDay() -> repository.unlockChristmas()
        }
    }

    // ── Startup sync ──────────────────────────────────────────────────────────

    /**
     * Restaura contadores locales desde Play Games una vez por instalación.
     * Llamado desde [MainActivity.onResume].
     */
    suspend fun syncFromServerIfNeeded() {
        if (!repository.needsServerSync()) return

        reporter.loadAchievements(
            onResult = { achievements ->
                scope.launch {
                    achievements.forEach { applyServerAchievementToLocalCounters(it) }
                    repository.markServerSyncDone()
                    logger.debug("syncFromServer: completed successfully")
                }
            },
            onFailure = { e ->
                logger.warn("syncFromServer: failed, will retry on next launch — ${e.message}")
            },
        )
    }

    private suspend fun applyServerAchievementToLocalCounters(snapshot: AchievementSnapshot) {
        if (snapshot.isIncremental) {
            val serverSteps = snapshot.currentSteps
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

        if (!snapshot.isUnlocked) return

        when (snapshot.id) {
            context.getString(R.string.achievement_halloween_theme) -> repository.unlockHalloween()
            context.getString(R.string.achievement_christmas_theme) -> repository.unlockChristmas()
            context.getString(R.string.achievement_the_first_light) -> repository.unlockAurora()
            context.getString(R.string.achievement_the_dark_side) -> repository.unlockEmber()
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    override fun showAchievementsUI(onNavigateToScreen: () -> Unit) {
        val activity = activityProvider.get() ?: run {
            getLogger().debug("showAchievementsUI skipped: no activity")
            onNavigateToScreen()
            return
        }

        // Verifica si el usuario tiene sesión Google activa antes de intentar abrir Play Games.
        PlayGames.getGamesSignInClient(activity)
            .isAuthenticated
            .addOnSuccessListener { result ->
                if (result.isAuthenticated) {
                    PlayGames.getAchievementsClient(activity)
                        .achievementsIntent
                        .addOnSuccessListener { intent -> launchAchievementsIntent(intent) }
                        .addOnFailureListener { e ->
                            getLogger().error("showAchievementsUI failed: ${e.message}", e)
                            onNavigateToScreen()
                        }
                } else {
                    getLogger().debug("showAchievementsUI: user not signed in to Play Games — navigating to own screen")
                    onNavigateToScreen()
                }
            }
            .addOnFailureListener { e ->
                getLogger().debug("showAchievementsUI: could not check sign-in status — ${e.message}")
                onNavigateToScreen()
            }
    }

    /**
     * Lanza el Intent de logros vía [ActivityProvider.intentLauncher] para
     * evitar el bloqueo IntentRedirect Hardening en Android 14+.
     */
    private fun launchAchievementsIntent(intent: Intent) {
        val launcher = activityProvider.intentLauncher
        if (launcher != null) {
            launcher(intent)
        } else {
            activityProvider.get()?.startActivity(intent)
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    /**
     * Envía pasos a Play Games con caché de sesión para evitar llamadas redundantes.
     * El servidor recibe el valor actualizado independientemente del caché.
     */
    private suspend fun setStepsGPlay(achievementResId: Int, steps: Int) {
        val cachedThisSession = repository.getCachedSteps(achievementResId)
        if (steps <= cachedThisSession) return
        val submitted = reporter.setSteps(achievementResId, steps)
        if (submitted) repository.updateCachedSteps(achievementResId, steps)
    }
}

// ── Mapeo AchievementId ↔ Android resource ID ────────────────────────────────

/**
 * Traduce un [AchievementId] canónico al resource ID de Google Play Games.
 * Retorna null para logros que no tienen contraparte en Play Games (actualmente ninguno).
 */
private fun AchievementId.toAndroidResId(): Int? = when (this) {
    AchievementId.WELCOME_TO_TARATI -> R.string.achievement_welcome_to_tarati
    AchievementId.FIRST_CAPTURE -> R.string.achievement_first_capture
    AchievementId.FIRST_PROMOTION -> R.string.achievement_first_promotion
    AchievementId.FIRST_VICTORY -> R.string.achievement_first_victory
    AchievementId.PLAY_10_GAMES -> R.string.achievement_play_10_games
    AchievementId.THE_FLIPPER -> R.string.achievement_the_flipper
    AchievementId.ROK_MASTER -> R.string.achievement_rok_master
    AchievementId.UNSTOPPABLE -> R.string.achievement_unstoppable
    AchievementId.CHAMPION -> R.string.achievement_champion
    AchievementId.MIT -> R.string.achievement_mit
    AchievementId.STALEMIT -> R.string.achievement_stalemit
    AchievementId.ETERNAL_LOOP -> R.string.achievement_eternal_loop
    AchievementId.FIFTY_MOVE_RULE -> R.string.achievement_fifty_move_rule
    AchievementId.DEAD_BUT_DANGEROUS -> R.string.achievement_dead_but_dangerous
    AchievementId.GRANDMASTER -> R.string.achievement_grandmaster
    AchievementId.HALLOWEEN_THEME -> R.string.achievement_halloween_theme
    AchievementId.CHRISTMAS_THEME -> R.string.achievement_christmas_theme
    AchievementId.APPRENTICE -> R.string.achievement_apprentice
    AchievementId.STRATEGIST -> R.string.achievement_strategist
    AchievementId.TACTICIAN -> R.string.achievement_tactician
    AchievementId.THE_FIRST_LIGHT -> R.string.achievement_the_first_light
    AchievementId.THE_DARK_SIDE -> R.string.achievement_the_dark_side
}

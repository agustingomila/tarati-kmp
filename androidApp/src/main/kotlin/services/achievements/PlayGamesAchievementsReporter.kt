package com.agustin.tarati.services.achievements

import android.content.Context
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.achievement.Achievement

/**
 * Implementación productiva de [IAchievementsReporter].
 * Delega directamente al SDK de Google Play Games.
 * Requiere una [android.app.Activity] activa vía [ActivityProvider].
 */
class PlayGamesAchievementsReporter(
    private val context: Context,
    private val activityProvider: ActivityProvider,
) : IAchievementsReporter {

    override fun unlock(achievementResId: Int) {
        val activity = activityProvider.get() ?: run {
            getLogger().debug("unlock skipped: no activity for ${context.getString(achievementResId)}")
            return
        }

        val achievementId = context.getString(achievementResId)
        getLogger().debug("unlock: submitting $achievementId")

        try {
            PlayGames.getAchievementsClient(activity).unlock(achievementId)
            getLogger().info("unlock: submitted successfully $achievementId")
        } catch (e: Exception) {
            getLogger().error("unlock: exception for $achievementId - ${e.message}", e)
        }
    }

    /**
     * @return true si el envío se realizó, false si no había Activity disponible.
     * La validación de maxSteps es responsabilidad del caller ([AchievementsManager]).
     */
    override fun setSteps(achievementResId: Int, steps: Int): Boolean {
        val activity = activityProvider.get() ?: run {
            getLogger().debug("setSteps skipped: no activity")
            return false
        }

        val achievementId = context.getString(achievementResId)
        getLogger().debug("setSteps: submitting $achievementId = $steps")

        return try {
            PlayGames.getAchievementsClient(activity).setSteps(achievementId, steps)
            getLogger().info("setSteps: submitted successfully $achievementId = $steps")
            true
        } catch (e: Exception) {
            getLogger().error("setSteps: exception for $achievementId = $steps - ${e.message}", e)
            false
        }
    }

    override fun loadAchievements(
        onResult: (List<AchievementSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val activity = activityProvider.get() ?: run {
            getLogger().debug("loadAchievements skipped: no activity")
            onFailure(IllegalStateException("No activity available"))
            return
        }

        PlayGames.getAchievementsClient(activity)
            .load(/* p0 = */ true)
            .addOnSuccessListener { annotatedData ->
                try {
                    // The buffer is only valid for the duration of this callback.
                    // Copy all required fields immediately; never hold a reference past release().
                    val snapshots = mutableListOf<AchievementSnapshot>()
                    val buffer = annotatedData.get()
                    if (buffer != null) {
                        for (i in 0 until buffer.count) {
                            val a = buffer.get(i)
                            snapshots.add(
                                AchievementSnapshot(
                                    id = a.achievementId,
                                    type = a.type,
                                    state = a.state,
                                    currentSteps = if (a.type == Achievement.TYPE_INCREMENTAL) a.currentSteps else 0,
                                )
                            )
                        }
                        buffer.release()
                    }
                    getLogger().info("loadAchievements: loaded ${snapshots.size} achievements")
                    onResult(snapshots)
                } catch (e: Exception) {
                    getLogger().error("loadAchievements: error processing buffer — ${e.message}", e)
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                getLogger().warn("loadAchievements: failed — ${e.message}")
                onFailure(e)
            }
    }
}
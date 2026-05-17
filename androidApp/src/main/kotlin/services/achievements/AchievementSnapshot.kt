package com.agustin.tarati.services.achievements

import com.google.android.gms.games.achievement.Achievement

/**
 * Immutable snapshot of the fields needed from a Play Games [Achievement].
 *
 * The Play Games SDK returns achievements inside a [com.google.android.gms.common.data.DataBuffer]
 * that is automatically closed after the Task callback returns. Any access to the live
 * [Achievement] reference after that point throws "Buffer is closed".
 *
 * [PlayGamesAchievementsReporter] copies all required fields into this snapshot inside
 * the callback before calling [com.google.android.gms.common.data.DataBuffer.release],
 * so callers like [AchievementsManager] can safely process the data asynchronously.
 */
data class AchievementSnapshot(
    val id: String,
    val type: Int,
    val state: Int,
    val currentSteps: Int,
) {
    val isIncremental: Boolean get() = type == Achievement.TYPE_INCREMENTAL
    val isUnlocked: Boolean get() = state == Achievement.STATE_UNLOCKED
}
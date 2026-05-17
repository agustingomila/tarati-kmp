package com.agustin.tarati.features.seasonal

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.MatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementación no-op de [ISpecialEventManager] para Desktop.
 * Los eventos especiales (Halloween, Christmas) son Android-only por ahora.
 */
class NoOpSpecialEventManager : ISpecialEventManager {

    override val activeEvents: StateFlow<List<SpecialEvent>> =
        MutableStateFlow(emptyList())

    override val pendingCelebration: StateFlow<SpecialEvent?> =
        MutableStateFlow(null)

    override suspend fun refreshIfNeeded() = Unit
    override suspend fun isGiftSeen(event: SpecialEvent): Boolean = true
    override suspend fun markGiftSeen(event: SpecialEvent) = Unit
    override fun dismissCelebration() = Unit
    override suspend fun onGameResult(matchState: MatchState, playerSide: CobColor) = Unit
}
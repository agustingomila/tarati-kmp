package com.agustin.tarati.features.seasonal

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.MatchState
import kotlinx.coroutines.flow.StateFlow

/**
 * Public contract for the special events' system.
 *
 * Separating the interface from [SpecialEventManager] allows:
 * - UI components ([SpecialEventOverlay]) to depend on the abstraction, not the
 *   concrete class — consistent with [IBoardAnimationViewModel], [ISettingsViewModel], etc.
 * - [GameEvents] to call [onGameResult] without coupling to DataStore or Play Games.
 * - Tests to provide a fake implementation without spinning up the full Koin graph.
 *
 * ## Implementations
 * - [SpecialEventManager] in androidApp — full implementation with DataStore + Play Games
 * - [NoOpSpecialEventManager] in desktopApp — no-op for Desktop
 */
interface ISpecialEventManager {

    // ── State ─────────────────────────────────────────────────────────────────

    val activeEvents: StateFlow<List<SpecialEvent>>
    val pendingCelebration: StateFlow<SpecialEvent?>

    // ── UI API ────────────────────────────────────────────────────────────────

    suspend fun refreshIfNeeded()
    suspend fun isGiftSeen(event: SpecialEvent): Boolean
    suspend fun markGiftSeen(event: SpecialEvent)
    fun dismissCelebration()

    // ── Game hook ─────────────────────────────────────────────────────────────

    suspend fun onGameResult(matchState: MatchState, playerSide: CobColor)
}
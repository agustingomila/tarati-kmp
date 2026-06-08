package com.agustin.tarati.ui.layout

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// ── Display mode ──────────────────────────────────────────────────────────────

/** Indica si una pantalla se renderiza a pantalla completa o embebida en el panel lateral. */
enum class DisplayMode { FullScreen, CompanionPanel }

// ── Destinations ─────────────────────────────────────────────────────────────

/** Destinos posibles del panel lateral en layouts [ScreenLayout.Expanded]. */
sealed interface CompanionPanelDestination {
    data object None : CompanionPanelDestination
    data object Lobby : CompanionPanelDestination
    data object Settings : CompanionPanelDestination
    data object Library : CompanionPanelDestination
    data object Leaderboard : CompanionPanelDestination
    data class Profile(val userId: String) : CompanionPanelDestination
    data class GameDetails(val gameId: String) : CompanionPanelDestination
}

// ── Controller ────────────────────────────────────────────────────────────────

/**
 * Controla la navegación dentro del panel lateral.
 *
 * Creado una sola vez en [AppContent] y propagado vía [LocalCompanionPanelController].
 * Implementa un back-stack mínimo de un nivel: [navigate] recuerda el destino anterior
 * para que los sub-destinos (Profile desde Leaderboard) puedan volver con [back].
 */
class CompanionPanelController {
    var destination: CompanionPanelDestination by mutableStateOf(CompanionPanelDestination.None)
        private set

    private var previous: CompanionPanelDestination = CompanionPanelDestination.None

    val isOpen: Boolean get() = destination !is CompanionPanelDestination.None

    fun navigate(dest: CompanionPanelDestination) {
        previous = destination
        destination = dest
    }

    fun back() {
        destination = previous
        previous = CompanionPanelDestination.None
    }

    fun close() {
        previous = CompanionPanelDestination.None
        destination = CompanionPanelDestination.None
    }
}

val LocalCompanionPanelController = compositionLocalOf { CompanionPanelController() }
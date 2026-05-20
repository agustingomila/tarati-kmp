package com.agustin.tarati.features.online.lobby


import com.agustin.tarati.core.data.database.dto.MatchDto
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato público de [OnlineLobbyViewModel].
 *
 * Consumido por [OnlineLobbyScreen] y [NavGraph]. Permite
 * test doubles sin depender del ViewModel concreto.
 */
interface IOnlineLobbyViewModel {

    // ── State ──────────────────────────────────────────────────────────────────

    /** Partidas actualmente en curso. Refresco cada [LIVE_POLL_INTERVAL]. */
    val liveGames: StateFlow<LiveGamesUiState>

    /** Búsquedas abiertas en colas de matchmaking. Refresco integrado al polling. */
    val openSearches: StateFlow<OpenSearchesUiState>

    /** Historial paginado del usuario autenticado. */
    val history: StateFlow<GameHistoryUiState>

    /** Filtros y ordenamiento del tab de lobby (en vivo + búsquedas). */
    val lobbyFilters: StateFlow<LobbyFilters>

    // ── Polling (en vivo + búsquedas) ─────────────────────────────────────────

    /** Inicia el polling. Llamar en [LaunchedEffect(Unit)] del tab "En Vivo". */
    fun startLivePolling()

    fun stopLivePolling()

    /** Refresca la lista de búsquedas abiertas inmediatamente. */
    fun refreshOpenSearches()

    // ── Lobby filters ──────────────────────────────────────────────────────────

    fun setShowLiveGames(show: Boolean)

    fun setShowOpenSearches(show: Boolean)

    fun setLobbySort(sort: LobbySort)

    // ── Game history ───────────────────────────────────────────────────────────

    fun loadHistory()

    fun loadMoreHistory()

    // ── History filters ────────────────────────────────────────────────────────

    fun setTimeControlFilter(tc: String?)

    fun setResultFilter(result: String?)

    fun setRatedFilter(rated: Boolean?)

    fun clearFilters()

    // ── Social feed ────────────────────────────────────────────────────────────

    /** Partidas recientes de jugadores seguidos. */
    val feedState: StateFlow<GameHistoryUiState>

    /** Carga la primera página del feed. Llamar en [LaunchedEffect(Unit)] del tab "Seguidos". */
    fun loadFeed()

    fun loadMoreFeed()

    // ── Game preview ───────────────────────────────────────────────────────────

    /**
     * Obtiene una partida del servidor y la convierte a [MatchDto].
     * Llamar antes de navegar a GameDetailsScreen para pre-cargar el detalle.
     *
     * @return [MatchDto] si la carga y conversión fueron exitosas, null si hubo error.
     */
    suspend fun loadAndPreviewGame(gameId: String): MatchDto?
}
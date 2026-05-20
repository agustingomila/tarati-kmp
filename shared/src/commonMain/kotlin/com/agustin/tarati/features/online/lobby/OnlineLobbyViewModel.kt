package com.agustin.tarati.features.online.lobby


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.lobby.OnlineLobbyViewModel.Companion.LIVE_POLL_INTERVAL
import com.agustin.tarati.network.models.GameHistoryDto
import com.agustin.tarati.network.models.LiveGameDto
import com.agustin.tarati.network.models.OpenSearchDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

// ── State models ───────────────────────────────────────────────────────────────

data class LiveGamesUiState(
    val games: List<LiveGameDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class OpenSearchesUiState(
    val searches: List<OpenSearchDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

/** Criterio de ordenamiento de ítems del lobby. */
enum class LobbySort {
    /** Más recientes al inicio (búsquedas recién creadas, partidas recién empezadas). */
    NEWEST,

    /** Más antiguos al inicio (búsquedas con más espera arriba). */
    OLDEST,

    /** Mayor rating primero. */
    RATING_DESC,
}

/**
 * Filtros y ordenamiento del tab "En Vivo" del lobby.
 * Controla qué tipos de ítems se muestran y cómo se ordenan.
 */
data class LobbyFilters(
    val showLiveGames: Boolean = true,
    val showOpenSearches: Boolean = true,
    val sort: LobbySort = LobbySort.NEWEST,
)

data class HistoryFilters(
    val timeControl: String? = null,   // null = todos
    val result: String? = null,        // "win" | "loss" | "draw" | null
    val rated: Boolean? = null,        // null = todos
)

data class GameHistoryUiState(
    val games: List<GameHistoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val filters: HistoryFilters = HistoryFilters(),
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val total: Long = 0,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

/**
 * ViewModel para [OnlineLobbyScreen].
 *
 * ## Tab "En Vivo"
 * Polling unificado de [GET /api/live-games] y [GET /api/lobby/open-searches]
 * cada [LIVE_POLL_INTERVAL]. Ambas listas se refrescan en el mismo ciclo para
 * que la UI siempre esté sincronizada.
 *
 * Los filtros [LobbyFilters] controlan qué tipos de ítems muestra la pantalla
 * y con qué criterio de ordenamiento. El ViewModel expone los datos crudos —
 * la pantalla aplica los filtros sobre ellos.
 *
 * ## Tab "Mis Partidas"
 * Carga paginada con filtros por time control, resultado y tipo de partida.
 */
class OnlineLobbyViewModel(
    private val repository: OnlineLobbyRepository,
    private val authViewModel: IAuthViewModel,
) : ViewModel(), IOnlineLobbyViewModel {

    private val logger = getLogger("OnlineLobbyViewModel")

    private val _liveGames = MutableStateFlow(LiveGamesUiState())
    override val liveGames: StateFlow<LiveGamesUiState> = _liveGames.asStateFlow()

    private val _openSearches = MutableStateFlow(OpenSearchesUiState())
    override val openSearches: StateFlow<OpenSearchesUiState> = _openSearches.asStateFlow()

    private val _history = MutableStateFlow(GameHistoryUiState())
    override val history: StateFlow<GameHistoryUiState> = _history.asStateFlow()

    private val _lobbyFilters = MutableStateFlow(LobbyFilters())
    override val lobbyFilters: StateFlow<LobbyFilters> = _lobbyFilters.asStateFlow()

    private val _feedState = MutableStateFlow(GameHistoryUiState())
    override val feedState: StateFlow<GameHistoryUiState> = _feedState.asStateFlow()

    private var pollingJob: Job? = null

    companion object {
        /** Intervalo de refresco del lobby (partidas en vivo + búsquedas). */
        val LIVE_POLL_INTERVAL = 5.seconds
        const val PAGE_SIZE = 20
    }

    // ── Polling ────────────────────────────────────────────────────────────────

    override fun startLivePolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchLiveGames()
                fetchOpenSearches()
                delay(LIVE_POLL_INTERVAL)
            }
        }
    }

    override fun stopLivePolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun refreshOpenSearches() {
        viewModelScope.launch { fetchOpenSearches() }
    }

    // Retorna el access token actual, refrescándolo si está próximo a expirar.
    private suspend fun getValidToken(): String? {
        if (authViewModel.isTokenExpiringSoon()) authViewModel.refreshToken()
        return authViewModel.accessToken
    }

    private suspend fun fetchLiveGames() {
        val token = getValidToken() ?: run {
            _liveGames.update { it.copy(isLoading = false) }
            return
        }
        _liveGames.update { it.copy(isLoading = it.games.isEmpty(), error = null) }

        repository.getLiveGames(token)
            .onSuccess { games ->
                logger.debug("fetchLiveGames: received ${games.size} live games")
                _liveGames.update { it.copy(games = games, isLoading = false) }
            }
            .onFailure { e ->
                logger.error("fetchLiveGames error: ${e::class.simpleName} — ${e.message}")
                _liveGames.update { it.copy(isLoading = false, error = e.message) }
            }
    }

    private suspend fun fetchOpenSearches() {
        val token = getValidToken() ?: return
        _openSearches.update { it.copy(isLoading = it.searches.isEmpty(), error = null) }

        repository.getOpenSearches(token)
            .onSuccess { searches ->
                logger.debug("fetchOpenSearches: received ${searches.size} open searches")
                _openSearches.update { it.copy(searches = searches, isLoading = false) }
            }
            .onFailure { e ->
                logger.error("fetchOpenSearches error: ${e::class.simpleName} — ${e.message}")
                _openSearches.update { it.copy(isLoading = false, error = e.message) }
            }
    }

    // ── Lobby filters ──────────────────────────────────────────────────────────

    override fun setShowLiveGames(show: Boolean) {
        _lobbyFilters.update { it.copy(showLiveGames = show) }
    }

    override fun setShowOpenSearches(show: Boolean) {
        _lobbyFilters.update { it.copy(showOpenSearches = show) }
    }

    override fun setLobbySort(sort: LobbySort) {
        _lobbyFilters.update { it.copy(sort = sort) }
    }

    // ── Game history ───────────────────────────────────────────────────────────

    override fun loadHistory() {
        viewModelScope.launch {
            val token = getValidToken() ?: run {
                logger.debug("loadHistory: no token, skipping")
                return@launch
            }
            _history.update { it.copy(isLoading = true, error = null, currentPage = 0, games = emptyList()) }

            repository.getGameHistory(
                token = token,
                page = 0,
                limit = PAGE_SIZE,
                timeControl = _history.value.filters.timeControl,
                result = _history.value.filters.result,
                rated = _history.value.filters.rated,
            ).onSuccess { paged ->
                _history.update {
                    it.copy(
                        games = paged.items,
                        isLoading = false,
                        currentPage = 0,
                        total = paged.total,
                        hasMore = paged.items.size < paged.total,
                    )
                }
            }.onFailure { e ->
                _history.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    override fun loadMoreHistory() {
        val state = _history.value
        if (state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            val token = getValidToken() ?: return@launch
            val nextPage = state.currentPage + 1
            _history.update { it.copy(isLoadingMore = true) }

            repository.getGameHistory(
                token = token,
                page = nextPage,
                limit = PAGE_SIZE,
                timeControl = state.filters.timeControl,
                result = state.filters.result,
                rated = state.filters.rated,
            ).onSuccess { paged ->
                _history.update {
                    it.copy(
                        games = it.games + paged.items,
                        isLoadingMore = false,
                        currentPage = nextPage,
                        total = paged.total,
                        hasMore = (it.games.size + paged.items.size) < paged.total,
                    )
                }
            }.onFailure { e ->
                _history.update { it.copy(isLoadingMore = false, error = e.message) }
            }
        }
    }

    // ── History filters ────────────────────────────────────────────────────────

    override fun setTimeControlFilter(tc: String?) {
        _history.update { it.copy(filters = it.filters.copy(timeControl = tc)) }
        loadHistory()
    }

    override fun setResultFilter(result: String?) {
        _history.update { it.copy(filters = it.filters.copy(result = result)) }
        loadHistory()
    }

    override fun setRatedFilter(rated: Boolean?) {
        _history.update { it.copy(filters = it.filters.copy(rated = rated)) }
        loadHistory()
    }

    override fun clearFilters() {
        _history.update { it.copy(filters = HistoryFilters()) }
        loadHistory()
    }

    // ── Game preview ──────────────────────────────────────────────────────────

    override suspend fun loadAndPreviewGame(gameId: String): MatchDto? {
        val token = getValidToken() ?: return null
        return repository.getGame(token = token, gameId = gameId)
            .getOrNull()
            ?.toMatchDto()
    }

    // ── Social feed ────────────────────────────────────────────────────────────

    override fun loadFeed() {
        viewModelScope.launch {
            val token = getValidToken() ?: return@launch
            _feedState.update { it.copy(isLoading = true, error = null, currentPage = 0, games = emptyList()) }
            repository.getFeed(token = token, page = 0, limit = PAGE_SIZE)
                .onSuccess { paged ->
                    _feedState.update {
                        it.copy(
                            games = paged.items,
                            isLoading = false,
                            currentPage = 0,
                            total = paged.total,
                            hasMore = paged.items.size < paged.total,
                        )
                    }
                }
                .onFailure { e ->
                    _feedState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    override fun loadMoreFeed() {
        val state = _feedState.value
        if (state.isLoadingMore || !state.hasMore) return
        viewModelScope.launch {
            val token = getValidToken() ?: return@launch
            val nextPage = state.currentPage + 1
            _feedState.update { it.copy(isLoadingMore = true) }
            repository.getFeed(token = token, page = nextPage, limit = PAGE_SIZE)
                .onSuccess { paged ->
                    _feedState.update {
                        it.copy(
                            games = it.games + paged.items,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            total = paged.total,
                            hasMore = (it.games.size + paged.items.size) < paged.total,
                        )
                    }
                }
                .onFailure { e ->
                    _feedState.update { it.copy(isLoadingMore = false, error = e.message) }
                }
        }
    }
}
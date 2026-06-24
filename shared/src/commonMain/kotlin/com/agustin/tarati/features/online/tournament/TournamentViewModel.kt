package com.agustin.tarati.features.online.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.game.IOnlineGameViewModel
import com.agustin.tarati.features.online.game.TournamentEvent
import com.agustin.tarati.network.models.CreateTournamentRequest
import com.agustin.tarati.network.models.TournamentStatus
import com.agustin.tarati.network.models.TournamentSummaryDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel de torneos.
 *
 * Gestiona la lista de torneos y el detalle de un torneo específico.
 * Se suscribe a [IOnlineGameViewModel.tournamentEvents] para recibir actualizaciones
 * en tiempo real (standings, nueva ronda, fin) mientras la pantalla de detalle está activa.
 *
 * Registrado como `viewModel` (no `single`) en Koin: cada pantalla de torneo
 * tiene su propia instancia con su propio estado de carga.
 */
class TournamentViewModel(
    private val repository: TournamentRepository,
    private val authViewModel: IAuthViewModel,
    onlineGameViewModel: IOnlineGameViewModel,
) : ViewModel(), ITournamentViewModel {

    private val _listState = MutableStateFlow(TournamentListUiState(isLoading = true))
    override val listState: StateFlow<TournamentListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(TournamentDetailUiState())
    override val detailState: StateFlow<TournamentDetailUiState> = _detailState.asStateFlow()

    // ID del torneo que se está viendo en detalle — para filtrar eventos WS
    private var currentDetailId: String? = null
    private var pollingJob: Job? = null

    init {
        onlineGameViewModel.tournamentEvents
            .onEach { event ->
                val token = authViewModel.accessToken ?: authViewModel.getStoredToken()
                val targetId = currentDetailId

                when (event) {
                    is TournamentEvent.StandingsUpdated -> {
                        if (event.tournamentId != targetId) return@onEach
                        // Actualiza standings inline para feedback inmediato
                        _detailState.value = _detailState.value.let { s ->
                            s.copy(tournament = s.tournament?.copy(standings = event.standings))
                        }
                        // Recarga el detalle completo para capturar el fixture actualizado
                        // (estado ACTIVE → COMPLETED + resultado de la partida)
                        if (token != null) loadTournament(token, targetId)
                    }

                    is TournamentEvent.RoundStarted -> {
                        // Torneo pasó a ACTIVE: refrescar lista + detalle si está abierto.
                        if (token != null) loadTournaments(token)
                        if (token != null && event.tournamentId == targetId) loadTournament(token, targetId)
                    }

                    is TournamentEvent.Finished -> {
                        // Torneo terminó: refrescar lista + detalle si está abierto.
                        if (token != null) loadTournaments(token)
                        if (token != null && event.tournamentId == targetId) loadTournament(token, targetId)
                    }

                    is TournamentEvent.Cancelled -> {
                        // Torneo cancelado: refrescar lista + detalle si está abierto.
                        if (token != null) loadTournaments(token)
                        if (token != null && event.tournamentId == targetId) loadTournament(token, targetId)
                    }

                    is TournamentEvent.GameAssigned -> Unit // Manejado globalmente en AppContent
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Polling ────────────────────────────────────────────────────────────────

    override fun startTournamentPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                val token = authViewModel.accessToken ?: authViewModel.getStoredToken()
                if (token != null) loadTournaments(token)
                delay(POLL_INTERVAL) // cancellation point — CancellationException detiene el loop
            }
        }
    }

    override fun stopTournamentPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // ── Lista ──────────────────────────────────────────────────────────────────

    override fun loadTournaments(token: String) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            repository.getTournaments(token)
                .onSuccess { tournaments ->
                    _listState.value = TournamentListUiState(
                        registering = tournaments.filter { it.status == TournamentStatus.REGISTERING },
                        active = tournaments.filter { it.status == TournamentStatus.ACTIVE },
                        finished = tournaments.filter { it.status == TournamentStatus.FINISHED },
                    )
                }
                .onFailure { e ->
                    _listState.value = _listState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    // ── Detalle ────────────────────────────────────────────────────────────────

    override fun loadTournament(token: String, id: String) {
        currentDetailId = id
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            repository.getTournament(token, id)
                .onSuccess { t -> _detailState.value = TournamentDetailUiState(tournament = t) }
                .onFailure { e -> _detailState.value = TournamentDetailUiState(error = e.message) }
        }
    }

    // ── Acciones ───────────────────────────────────────────────────────────────

    override suspend fun createTournament(
        token: String,
        request: CreateTournamentRequest,
    ): Result<TournamentSummaryDto> =
        repository.createTournament(token, request)
            .also { if (it.isSuccess) loadTournaments(token) }

    override suspend fun register(token: String, id: String): Result<Unit> =
        repository.register(token, id)
            .also { if (it.isSuccess) loadTournament(token, id) }

    override suspend fun unregister(token: String, id: String): Result<Unit> =
        repository.unregister(token, id)
            .also { if (it.isSuccess) loadTournament(token, id) }

    override suspend fun start(token: String, id: String): Result<Unit> =
        repository.start(token, id)
            .also { if (it.isSuccess) loadTournament(token, id) }

    override suspend fun cancel(token: String, id: String): Result<Unit> =
        repository.cancel(token, id)
            .also { if (it.isSuccess) loadTournament(token, id) }

    companion object {
        private val POLL_INTERVAL = 30.seconds
    }
}

package com.agustin.tarati.features.online.tournament

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agustin.tarati.network.models.CreateTournamentRequest
import com.agustin.tarati.network.models.TournamentDetailDto
import com.agustin.tarati.network.models.TournamentSummaryDto
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ITournamentViewModel {
    val listState: StateFlow<TournamentListUiState>
    val detailState: StateFlow<TournamentDetailUiState>

    fun loadTournaments(token: String)
    fun loadTournament(token: String, id: String)
    fun startTournamentPolling()
    fun stopTournamentPolling()
    suspend fun createTournament(token: String, request: CreateTournamentRequest): Result<TournamentSummaryDto>
    suspend fun register(token: String, id: String): Result<Unit>
    suspend fun unregister(token: String, id: String): Result<Unit>
    suspend fun start(token: String, id: String): Result<Unit>
    suspend fun cancel(token: String, id: String): Result<Unit>
}

@Immutable
data class TournamentListUiState(
    val registering: List<TournamentSummaryDto> = emptyList(),
    val active: List<TournamentSummaryDto> = emptyList(),
    val finished: List<TournamentSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && error == null && registering.isEmpty() && active.isEmpty() && finished.isEmpty()
}

@Immutable
data class TournamentDetailUiState(
    val tournament: TournamentDetailDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

package com.agustin.tarati.features.online.tournament

import com.agustin.tarati.network.models.CreateTournamentRequest
import com.agustin.tarati.network.models.TournamentDetailDto
import com.agustin.tarati.network.models.TournamentStatus
import com.agustin.tarati.network.models.TournamentSummaryDto
import kotlinx.coroutines.flow.StateFlow

interface ITournamentViewModel {
    val listState: StateFlow<TournamentListUiState>
    val detailState: StateFlow<TournamentDetailUiState>

    fun loadTournaments(token: String)
    fun loadTournament(token: String, id: String)
    suspend fun createTournament(token: String, request: CreateTournamentRequest): Result<TournamentSummaryDto>
    suspend fun register(token: String, id: String): Result<Unit>
    suspend fun unregister(token: String, id: String): Result<Unit>
    suspend fun start(token: String, id: String): Result<Unit>
}

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

data class TournamentDetailUiState(
    val tournament: TournamentDetailDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

package com.agustin.tarati.features.online.social


import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agustin.tarati.network.models.LeaderboardEntryDto
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class LeaderboardUiState(
    val entries: List<LeaderboardEntryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@Stable
interface ILeaderboardViewModel {
    val leaderboardState: StateFlow<LeaderboardUiState>
    val selectedTc: StateFlow<String>
    fun selectTimeControl(tc: String)
    fun refresh()
}

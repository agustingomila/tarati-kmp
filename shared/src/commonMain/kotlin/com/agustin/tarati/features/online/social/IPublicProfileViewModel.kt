package com.agustin.tarati.features.online.social


import com.agustin.tarati.features.online.lobby.GameHistoryUiState
import com.agustin.tarati.network.models.PublicProfileDto
import com.agustin.tarati.network.models.ServerAchievementDto
import kotlinx.coroutines.flow.StateFlow

data class PublicProfileUiState(
    val profile: PublicProfileDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class FollowStatusUiState(
    val isFollowing: Boolean = false,
    val followersCount: Long = 0,
    val followingCount: Long = 0,
    val isLoading: Boolean = false,
)

interface IPublicProfileViewModel {
    val profileState: StateFlow<PublicProfileUiState>
    val historyState: StateFlow<GameHistoryUiState>
    val followStatusState: StateFlow<FollowStatusUiState>
    val achievements: StateFlow<List<ServerAchievementDto>>

    /** True si el perfil visualizado corresponde al usuario autenticado. */
    val isOwnProfile: Boolean

    fun loadMoreHistory()
    fun setTimeControlFilter(tc: String?)
    fun setResultFilter(result: String?)
    fun setRatedFilter(rated: Boolean?)
    fun clearFilters()

    /** Alterna entre seguir y dejar de seguir al usuario del perfil. */
    fun toggleFollow()

    /** Envía un desafío directo al usuario del perfil. */
    fun sendChallenge(timeControl: String, rated: Boolean)
}

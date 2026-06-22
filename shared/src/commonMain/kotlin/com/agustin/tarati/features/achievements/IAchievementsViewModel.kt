package com.agustin.tarati.features.achievements

import androidx.compose.runtime.Stable
import com.agustin.tarati.network.models.ServerAchievementDto
import kotlinx.coroutines.flow.StateFlow

@Stable
interface IAchievementsViewModel {
    val serverAchievements: StateFlow<Map<String, ServerAchievementDto>>
    val isLoading: StateFlow<Boolean>
    fun refresh()
}

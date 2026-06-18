package com.agustin.tarati.features.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.network.models.ServerAchievementDto
import com.agustin.tarati.services.achievements.AchievementSyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Carga y expone el estado de logros del usuario autenticado desde el servidor.
 * La pantalla [AchievementsScreen] mezcla este estado con [AchievementsMetadata] (estático)
 * para construir la vista completa sin lógica de merge en el ViewModel.
 */
class AchievementsViewModel(
    private val syncService: AchievementSyncService,
    private val authViewModel: IAuthViewModel,
) : ViewModel(), IAchievementsViewModel {

    private val _serverAchievements = MutableStateFlow<Map<String, ServerAchievementDto>>(emptyMap())
    override val serverAchievements: StateFlow<Map<String, ServerAchievementDto>> = _serverAchievements.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = authViewModel.accessToken ?: run {
                _isLoading.value = false
                return@launch
            }
            syncService.getAll(token).onSuccess { list ->
                _serverAchievements.value = list.associateBy { it.achievementId }
            }
            _isLoading.value = false
        }
    }
}

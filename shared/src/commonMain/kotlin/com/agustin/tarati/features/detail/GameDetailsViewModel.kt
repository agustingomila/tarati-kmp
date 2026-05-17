package com.agustin.tarati.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameDetailsViewModel(
    private val gameRepository: GameRepository,
) : ViewModel(),
    IGameDetailsViewModel {
    private val _currentMatchDto = MutableStateFlow<MatchDto?>(null)
    override val currentMatchDto: StateFlow<MatchDto?> = _currentMatchDto.asStateFlow()

    override fun updateCurrentMatchDto(matchDto: MatchDto) {
        _currentMatchDto.update { matchDto }
    }

    private val _isEditing = MutableStateFlow(false)
    override val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    override fun loadGame(gameId: String) {
        viewModelScope.launch {
            gameRepository.loadGame(gameId)?.let { match ->
                _currentMatchDto.update { match }
            }
        }
    }

    override fun saveGame(matchDto: MatchDto) {
        viewModelScope.launch {
            gameRepository.saveGame(matchDto)
            _currentMatchDto.update { matchDto }
            _isEditing.update { false }
        }
    }

    override fun setEditing(editing: Boolean) {
        _isEditing.update { editing }
    }
}
package com.agustin.tarati.features.detail

import com.agustin.tarati.core.data.database.dto.MatchDto
import kotlinx.coroutines.flow.StateFlow

interface IGameDetailsViewModel {
    val currentMatchDto: StateFlow<MatchDto?>
    val isEditing: StateFlow<Boolean>

    fun loadGame(gameId: String)

    fun saveGame(matchDto: MatchDto)

    fun setEditing(editing: Boolean)

    fun updateCurrentMatchDto(matchDto: MatchDto)
}
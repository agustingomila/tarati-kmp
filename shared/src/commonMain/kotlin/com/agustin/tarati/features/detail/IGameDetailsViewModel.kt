package com.agustin.tarati.features.detail

import androidx.compose.runtime.Stable
import com.agustin.tarati.core.data.database.dto.MatchDto
import kotlinx.coroutines.flow.StateFlow

/**
 * Anotado [@Stable]: solo expone `StateFlow` (consumidos vía `collectAsState`) y
 * funciones. Permite a Compose saltar recomposiciones en los composables que
 * reciben el ViewModel como parámetro.
 */
@Stable
interface IGameDetailsViewModel {
    val currentMatchDto: StateFlow<MatchDto?>
    val isEditing: StateFlow<Boolean>

    fun loadGame(gameId: String)

    fun saveGame(matchDto: MatchDto)

    fun setEditing(editing: Boolean)

    fun updateCurrentMatchDto(matchDto: MatchDto)
}
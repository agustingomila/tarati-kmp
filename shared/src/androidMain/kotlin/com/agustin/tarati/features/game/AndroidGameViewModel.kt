package com.agustin.tarati.features.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.utils.putSerializable
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.ui.components.game.KEY_GAME_HISTORY
import com.agustin.tarati.ui.components.game.KEY_GAME_STATE
import com.agustin.tarati.ui.components.game.KEY_GAME_STATUS
import com.agustin.tarati.ui.components.game.KEY_IS_EDITING
import com.agustin.tarati.ui.components.game.KEY_MOVE_INDEX

class AndroidGameViewModel(
    private val savedStateHandle: SavedStateHandle,
    sr: SettingsRepository,
    aiEngine: IAIEngine,
) : GameViewModel(sr, aiEngine) {

    // ── Player settings ───────────────────────────────────────────────────────

    override val playerSettings: IPlayerSettingsHolder by lazy {
        PlayerSettingsHolder(savedStateHandle, sr, viewModelScope)
    }

    init {
        playerSettings.loadFromDataStore()
    }

    // ── Session state ─────────────────────────────────────────────────────────

    override fun saveGameState() {
        savedStateHandle.putSerializable(KEY_GAME_STATE, gameState.value)
        savedStateHandle.putSerializable(KEY_GAME_HISTORY, history.value)
        savedStateHandle.putSerializable(KEY_GAME_STATUS, gameStatus.value)
        savedStateHandle[KEY_MOVE_INDEX] = moveIndex.value
    }

    override fun persistEditingState(isEditing: Boolean) {
        savedStateHandle[KEY_IS_EDITING] = isEditing
    }
}
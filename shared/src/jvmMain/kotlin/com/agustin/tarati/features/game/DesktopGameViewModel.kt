package com.agustin.tarati.features.game

import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.features.settings.SettingsRepository

class DesktopGameViewModel(
    sr: SettingsRepository,
    aiEngine: IAIEngine,
) : GameViewModel(sr, aiEngine) {

    // ── Player settings ───────────────────────────────────────────────────────

    override val playerSettings: IPlayerSettingsHolder by lazy {
        DesktopPlayerSettingsHolder(sr, viewModelScope)
    }

    init {
        playerSettings.loadFromDataStore()
    }

    // ── Session state (no-op: Desktop has no SavedStateHandle) ────────────────

    override fun saveGameState() = Unit

    override fun persistEditingState(isEditing: Boolean) = Unit
}
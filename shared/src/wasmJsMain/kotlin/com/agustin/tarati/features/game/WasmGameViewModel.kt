package com.agustin.tarati.features.game

import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.features.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WasmGameViewModel(
    sr: SettingsRepository,
    aiEngine: IAIEngine,
) : GameViewModel(sr, aiEngine) {

    override val playerSettings: IPlayerSettingsHolder by lazy {
        WasmPlayerSettingsHolder(sr, viewModelScope)
    }

    init {
        playerSettings.loadFromDataStore()
    }

    override fun saveGameState() = Unit

    override fun persistEditingState(isEditing: Boolean) = Unit
}

internal class WasmPlayerSettingsHolder(
    private val sr: SettingsRepository,
    private val scope: CoroutineScope,
) : IPlayerSettingsHolder {

    private val _aIEnabled = MutableStateFlow(true)
    override val aIEnabled: StateFlow<Boolean> = _aIEnabled.asStateFlow()

    private val _whiteIsAI = MutableStateFlow(false)
    override val whiteIsAI: StateFlow<Boolean> = _whiteIsAI.asStateFlow()

    private val _blackIsAI = MutableStateFlow(true)
    override val blackIsAI: StateFlow<Boolean> = _blackIsAI.asStateFlow()

    private val _difficultyWhite = MutableStateFlow(Difficulty.DEFAULT)
    override val difficultyWhite: StateFlow<Difficulty> = _difficultyWhite.asStateFlow()

    private val _difficultyBlack = MutableStateFlow(Difficulty.DEFAULT)
    override val difficultyBlack: StateFlow<Difficulty> = _difficultyBlack.asStateFlow()

    private val _playerSide = MutableStateFlow(CobColor.WHITE)
    override val playerSide: StateFlow<CobColor> = _playerSide.asStateFlow()

    override fun loadFromDataStore() {
        scope.launch {
            _whiteIsAI.value = sr.whiteIsAI.first()
            _blackIsAI.value = sr.blackIsAI.first()
            _difficultyWhite.value = sr.difficultyWhite.first()
            _difficultyBlack.value = sr.difficultyBlack.first()
            _aIEnabled.value = _whiteIsAI.value || _blackIsAI.value
        }
    }

    override fun updateAIEnabled(newAIEnabled: Boolean) {
        _aIEnabled.update { newAIEnabled }
    }

    override fun updatePlayerType(color: CobColor, isAI: Boolean) {
        when (color) {
            CobColor.WHITE -> {
                _whiteIsAI.update { isAI }; scope.launch { sr.setWhiteIsAI(isAI) }
            }

            CobColor.BLACK -> {
                _blackIsAI.update { isAI }; scope.launch { sr.setBlackIsAI(isAI) }
            }
        }
        updateAIEnabled(_whiteIsAI.value || _blackIsAI.value)
    }

    override fun updateDifficulty(color: CobColor, difficulty: Difficulty) {
        when (color) {
            CobColor.WHITE -> {
                _difficultyWhite.update { difficulty }; scope.launch { sr.setDifficultyWhite(difficulty) }
            }

            CobColor.BLACK -> {
                _difficultyBlack.update { difficulty }; scope.launch { sr.setDifficultyBlack(difficulty) }
            }
        }
    }

    override fun updatePlayerSide(newSide: CobColor) {
        _playerSide.update { newSide }
    }

    override fun togglePlayerSide() = updatePlayerSide(_playerSide.value.opponent)
}

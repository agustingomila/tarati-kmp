package com.agustin.tarati.desktop.features.game

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

/**
 * Desktop version of PlayerSettingsHolder without SavedStateHandle.
 * 
 * Manages player configuration: human/AI assignment, difficulty, and board side.
 * Settings are persisted only to [SettingsRepository] (cross-session).
 * 
 * Unlike the Android version, this doesn't use SavedStateHandle because Desktop
 * doesn't have Android's lifecycle (screen rotation, process death). The app
 * state lives entirely in memory until the window is closed.
 */
internal class DesktopPlayerSettingsHolder(
    private val sr: SettingsRepository,
    private val scope: CoroutineScope,
) {
    private val _aIEnabled = MutableStateFlow(true)
    val aIEnabled: StateFlow<Boolean> = _aIEnabled.asStateFlow()

    private val _whiteIsAI = MutableStateFlow(false)
    val whiteIsAI: StateFlow<Boolean> = _whiteIsAI.asStateFlow()

    private val _blackIsAI = MutableStateFlow(true)
    val blackIsAI: StateFlow<Boolean> = _blackIsAI.asStateFlow()

    private val _difficultyWhite = MutableStateFlow(Difficulty.DEFAULT)
    val difficultyWhite: StateFlow<Difficulty> = _difficultyWhite.asStateFlow()

    private val _difficultyBlack = MutableStateFlow(Difficulty.DEFAULT)
    val difficultyBlack: StateFlow<Difficulty> = _difficultyBlack.asStateFlow()

    private val _playerSide = MutableStateFlow(CobColor.WHITE)
    val playerSide: StateFlow<CobColor> = _playerSide.asStateFlow()

    // ── Initial load from SettingsRepository ──────────────────────────────────

    /** Loads all settings from SettingsRepository on init. */
    fun loadFromDataStore() {
        scope.launch {
            _whiteIsAI.value = sr.whiteIsAI.first()
            _blackIsAI.value = sr.blackIsAI.first()
            _difficultyWhite.value = sr.difficultyWhite.first()
            _difficultyBlack.value = sr.difficultyBlack.first()
            _aIEnabled.value = _whiteIsAI.value || _blackIsAI.value
        }
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    fun updateAIEnabled(newAIEnabled: Boolean) {
        _aIEnabled.update { newAIEnabled }
        // No SavedStateHandle to persist to in Desktop
    }

    fun updatePlayerType(color: CobColor, isAI: Boolean) {
        when (color) {
            CobColor.WHITE -> {
                _whiteIsAI.update { isAI }
                scope.launch { sr.setWhiteIsAI(isAI) }
            }

            CobColor.BLACK -> {
                _blackIsAI.update { isAI }
                scope.launch { sr.setBlackIsAI(isAI) }
            }
        }
        updateAIEnabled(_whiteIsAI.value || _blackIsAI.value)
    }

    fun updateDifficulty(color: CobColor, difficulty: Difficulty) {
        when (color) {
            CobColor.WHITE -> {
                _difficultyWhite.update { difficulty }
                scope.launch { sr.setDifficultyWhite(difficulty) }
            }

            CobColor.BLACK -> {
                _difficultyBlack.update { difficulty }
                scope.launch { sr.setDifficultyBlack(difficulty) }
            }
        }
    }

    fun updatePlayerSide(newSide: CobColor) {
        _playerSide.update { newSide }
        // No SavedStateHandle to persist to in Desktop
    }

    fun togglePlayerSide() = updatePlayerSide(_playerSide.value.opponent)
}

package com.agustin.tarati.features.game

import androidx.lifecycle.SavedStateHandle
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.ui.components.game.KEY_AI_ENABLED
import com.agustin.tarati.ui.components.game.KEY_BLACK_IS_AI
import com.agustin.tarati.ui.components.game.KEY_DIFFICULTY_BLACK
import com.agustin.tarati.ui.components.game.KEY_DIFFICULTY_WHITE
import com.agustin.tarati.ui.components.game.KEY_PLAYER_SIDE
import com.agustin.tarati.ui.components.game.KEY_WHITE_IS_AI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gestiona la configuración de jugadores: bando humano/IA, dificultad y lado del tablero.
 *
 * Cada ajuste se persiste en dos capas:
 * - [androidx.lifecycle.SavedStateHandle] — sobrevive cambios de configuración (rotación de pantalla).
 * - [SettingsRepository] — sobrevive reinicios de la app.
 *
 * Los valores se inicializan desde [androidx.lifecycle.SavedStateHandle] en la declaración (sesión activa).
 * Si la sesión es nueva (valor ausente), [loadFromDataStore] los carga desde DataStore.
 * Esto garantiza que un mismo launch del ViewModel siempre use [androidx.lifecycle.SavedStateHandle],
 * mientras que un reinicio frío usa DataStore.
 *
 * Mismo patrón que [EditBoardManager].
 */
internal class PlayerSettingsHolder(
    private val savedStateHandle: SavedStateHandle,
    private val sr: SettingsRepository,
    private val scope: CoroutineScope,
) {
    private val _aIEnabled = MutableStateFlow(savedStateHandle[KEY_AI_ENABLED] ?: true)
    val aIEnabled: StateFlow<Boolean> = _aIEnabled.asStateFlow()

    private val _whiteIsAI = MutableStateFlow(
        savedStateHandle.get<Boolean>(KEY_WHITE_IS_AI) ?: false,
    )
    val whiteIsAI: StateFlow<Boolean> = _whiteIsAI.asStateFlow()

    private val _blackIsAI = MutableStateFlow(
        savedStateHandle.get<Boolean>(KEY_BLACK_IS_AI) ?: true,
    )
    val blackIsAI: StateFlow<Boolean> = _blackIsAI.asStateFlow()

    private val _difficultyWhite = MutableStateFlow(
        savedStateHandle.get<String>(KEY_DIFFICULTY_WHITE)?.let { Difficulty.valueOf(it) }
            ?: Difficulty.DEFAULT,
    )
    val difficultyWhite: StateFlow<Difficulty> = _difficultyWhite.asStateFlow()

    private val _difficultyBlack = MutableStateFlow(
        savedStateHandle.get<String>(KEY_DIFFICULTY_BLACK)?.let { Difficulty.valueOf(it) }
            ?: Difficulty.DEFAULT,
    )
    val difficultyBlack: StateFlow<Difficulty> = _difficultyBlack.asStateFlow()

    private val _playerSide = MutableStateFlow(
        savedStateHandle.get<String>(KEY_PLAYER_SIDE)?.let { CobColor.valueOf(it) }
            ?: CobColor.WHITE,
    )
    val playerSide: StateFlow<CobColor> = _playerSide.asStateFlow()

    // ── Carga inicial desde DataStore ─────────────────────────────────────────

    /** Carga desde DataStore solo los valores que no fueron restaurados por SavedStateHandle. */
    fun loadFromDataStore() {
        scope.launch {
            if (savedStateHandle.get<Boolean>(KEY_WHITE_IS_AI) == null)
                _whiteIsAI.value = sr.whiteIsAI.first()
            if (savedStateHandle.get<Boolean>(KEY_BLACK_IS_AI) == null)
                _blackIsAI.value = sr.blackIsAI.first()
            if (savedStateHandle.get<String>(KEY_DIFFICULTY_WHITE) == null)
                _difficultyWhite.value = sr.difficultyWhite.first()
            if (savedStateHandle.get<String>(KEY_DIFFICULTY_BLACK) == null)
                _difficultyBlack.value = sr.difficultyBlack.first()
        }
    }

    // ── Mutaciones ────────────────────────────────────────────────────────────

    fun updateAIEnabled(newAIEnabled: Boolean) {
        _aIEnabled.update { newAIEnabled }
        savedStateHandle[KEY_AI_ENABLED] = newAIEnabled
    }

    fun updatePlayerType(color: CobColor, isAI: Boolean) {
        when (color) {
            CobColor.WHITE -> {
                _whiteIsAI.update { isAI }
                savedStateHandle[KEY_WHITE_IS_AI] = isAI
                scope.launch { sr.setWhiteIsAI(isAI) }
            }

            CobColor.BLACK -> {
                _blackIsAI.update { isAI }
                savedStateHandle[KEY_BLACK_IS_AI] = isAI
                scope.launch { sr.setBlackIsAI(isAI) }
            }
        }
        updateAIEnabled(_whiteIsAI.value || _blackIsAI.value)
    }

    fun updateDifficulty(color: CobColor, difficulty: Difficulty) {
        when (color) {
            CobColor.WHITE -> {
                _difficultyWhite.update { difficulty }
                savedStateHandle[KEY_DIFFICULTY_WHITE] = difficulty.name
                scope.launch { sr.setDifficultyWhite(difficulty) }
            }

            CobColor.BLACK -> {
                _difficultyBlack.update { difficulty }
                savedStateHandle[KEY_DIFFICULTY_BLACK] = difficulty.name
                scope.launch { sr.setDifficultyBlack(difficulty) }
            }
        }
    }

    fun updatePlayerSide(newSide: CobColor) {
        _playerSide.update { newSide }
        savedStateHandle[KEY_PLAYER_SIDE] = newSide.name
    }

    fun togglePlayerSide() = updatePlayerSide(_playerSide.value.opponent)
}
package com.agustin.tarati.features.game

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.StateFlow

interface IPlayerSettingsHolder {
    val aIEnabled: StateFlow<Boolean>
    val whiteIsAI: StateFlow<Boolean>
    val blackIsAI: StateFlow<Boolean>
    val difficultyWhite: StateFlow<Difficulty>
    val difficultyBlack: StateFlow<Difficulty>
    val playerSide: StateFlow<CobColor>

    fun loadFromDataStore()
    fun updateAIEnabled(newAIEnabled: Boolean)
    fun updatePlayerType(color: CobColor, isAI: Boolean)
    fun updateDifficulty(color: CobColor, difficulty: Difficulty)
    fun updatePlayerSide(newSide: CobColor)
    fun togglePlayerSide()
}
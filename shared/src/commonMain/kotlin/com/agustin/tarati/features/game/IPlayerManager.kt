package com.agustin.tarati.features.game

import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.coroutines.flow.StateFlow

interface IPlayerManager {
    val playerSide: StateFlow<CobColor>
    val aIEnabled: StateFlow<Boolean>
    val whiteIsAI: StateFlow<Boolean>
    val blackIsAI: StateFlow<Boolean>

    /**
     * Sets the Human/AI assignment for [color] without restarting the game.
     * Updates [whiteIsAI] / [blackIsAI] and [aIEnabled] reactively so all
     * observers (GameEffects, Sidebar, GameScreenState) respond immediately.
     */
    fun updatePlayerType(color: CobColor, isAI: Boolean)
}
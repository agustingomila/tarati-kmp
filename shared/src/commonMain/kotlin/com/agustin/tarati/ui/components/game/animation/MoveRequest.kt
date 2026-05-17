package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import kotlin.time.Clock

data class MoveRequest(
    val move: Move,
    val oldGameState: GameState,
    val newGameState: GameState,
    val isGameOver: Boolean = false,
    val requestTs: Long = Clock.System.now().toEpochMilliseconds()
)
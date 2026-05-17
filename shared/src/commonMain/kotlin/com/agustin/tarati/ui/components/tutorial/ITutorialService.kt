package com.agustin.tarati.ui.components.tutorial

import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move

interface ITutorialService {
    fun onMoveAttempted(
        move: Move,
        onMoveAccepted: (gameState: GameState) -> Unit,
        onMoveRejected: (move: List<Move>) -> Unit,
    )

    fun isCompleted(): Boolean

    fun nextStep()

    fun previousStep()

    fun endTutorial()

    fun repeatCurrentStep()

    fun getCurrentGameState(): GameState?

    fun requestUserInteraction(moves: List<Move>)

    fun resetTutorial()

    fun closeTutorial()

    fun startTutorial()
}
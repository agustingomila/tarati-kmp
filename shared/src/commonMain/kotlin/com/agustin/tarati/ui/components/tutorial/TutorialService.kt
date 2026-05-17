package com.agustin.tarati.ui.components.tutorial

import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move

class TutorialService(
    private val tutorialViewModel: ITutorialViewModel,
) : ITutorialService {
    override fun onMoveAttempted(
        move: Move,
        onMoveAccepted: (gameState: GameState) -> Unit,
        onMoveRejected: (move: List<Move>) -> Unit,
    ) {
        tutorialViewModel.onMoveAttempted(move, onMoveAccepted, onMoveRejected)
    }

    override fun isCompleted(): Boolean = tutorialViewModel.isCompleted()

    override fun nextStep() {
        tutorialViewModel.nextStep()
    }

    override fun previousStep() {
        tutorialViewModel.previousStep()
    }

    override fun endTutorial() {
        tutorialViewModel.endTutorial()
    }

    override fun repeatCurrentStep() {
        tutorialViewModel.repeatCurrentStep()
    }

    override fun getCurrentGameState(): GameState? = tutorialViewModel.getCurrentGameState()

    override fun requestUserInteraction(moves: List<Move>) {
        tutorialViewModel.requestUserInteraction(moves)
    }

    override fun resetTutorial() {
        tutorialViewModel.resetTutorial()
    }

    override fun closeTutorial() {
        tutorialViewModel.closeTutorial()
    }

    override fun startTutorial() {
        tutorialViewModel.startTutorial()
    }
}
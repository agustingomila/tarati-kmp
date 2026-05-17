package com.agustin.tarati.ui.components.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.tutorial.TutorialManager
import com.agustin.tarati.core.domain.tutorial.TutorialState
import com.agustin.tarati.core.domain.tutorial.isCompleted
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TutorialViewModel(
    val animationCoordinator: AnimationCoordinator,
    override val soundService: ISoundService,
) : ViewModel(),
    ITutorialViewModel {
    override val tutorialManager: TutorialManager by lazy { TutorialManager(animationCoordinator) }
    override val tutorialState: StateFlow<TutorialState> = tutorialManager.tutorialState
    override val progress get() = tutorialManager.progress

    override fun onMoveAttempted(
        move: Move,
        onMoveAccepted: (gameState: GameState) -> Unit,
        onMoveRejected: (move: List<Move>) -> Unit,
    ) {
        if (tutorialManager.isWaitingForUserInteraction()) {
            val moveAccepted = tutorialManager.onUserMove(move)

            if (moveAccepted) {
                val currentGameState = tutorialManager.getCurrentGameState()
                if (currentGameState != null) {
                    soundService.playMoveSound()
                    onMoveAccepted(currentGameState)
                }
            } else {
                soundService.playIllegalMoveSound()
                onMoveRejected(tutorialManager.getExpectedMoves())
            }
        }
    }

    private fun onStep() = soundService.playTutorialStepSound()

    override fun isCompleted(): Boolean = tutorialManager.progress.isCompleted()

    override fun nextStep() = tutorialManager.nextStep(::onStep)

    override fun previousStep() = tutorialManager.previousStep(::onStep)

    override fun endTutorial() = tutorialManager.endTutorial()

    override fun repeatCurrentStep() = tutorialManager.repeatCurrentStep(::onStep)

    override fun getCurrentGameState(): GameState? = tutorialManager.getCurrentGameState()

    override fun requestUserInteraction(moves: List<Move>) = tutorialManager.requestUserInteraction(moves)

    override fun resetTutorial() = tutorialManager.reset()

    override fun closeTutorial() = tutorialManager.closeTutorial()

    override fun startTutorial() {
        viewModelScope.launch {
            tutorialManager.loadRulesTutorial(::onStep)
            tutorialManager.getCurrentGameState()
        }
    }
}
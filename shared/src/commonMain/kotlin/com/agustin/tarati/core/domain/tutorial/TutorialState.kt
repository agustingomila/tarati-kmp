package com.agustin.tarati.core.domain.tutorial

import com.agustin.tarati.core.domain.game.play.Move

sealed class TutorialState {
    object Idle : TutorialState()

    data class ShowingStep(
        val step: TutorialStep,
    ) : TutorialState()

    data class WaitingForMove(
        val step: TutorialStep,
        val expectedMove: List<Move> = listOf(),
    ) : TutorialState()

    object Completed : TutorialState()
}

data class TutorialProgress(
    val currentStepIndex: Int,
    val totalSteps: Int,
)

fun TutorialProgress.isCompleted(): Boolean = currentStepIndex == totalSteps

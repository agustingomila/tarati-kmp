package com.agustin.tarati.ui.components.tutorial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.tutorial.TutorialState
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.perform_the_indicated_move
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TutorialOverlay(
    boardSize: Size,
    boardOrientation: BoardOrientation,
    tutorialEvents: TutorialEvents,
    tutorialViewModel: ITutorialViewModel = koinViewModel<TutorialViewModel>(),
    updateGameState: (GameState) -> Unit,
) {
    if (boardSize == Size.Zero) return

    val coordinateMapper =
        remember(boardSize, boardOrientation) {
            TutorialCoordinateMapper(boardSize, boardOrientation)
        }

    val state by tutorialViewModel.tutorialState.collectAsState()

    LaunchedEffect(state) {
        val newGameState = tutorialViewModel.getCurrentGameState()
        newGameState?.let {
            updateGameState(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is TutorialState.ShowingStep,
            is TutorialState.WaitingForMove,
                -> {
                val step =
                    when (state) {
                        is TutorialState.ShowingStep -> (state as TutorialState.ShowingStep).step
                        is TutorialState.WaitingForMove -> (state as TutorialState.WaitingForMove).step
                        else -> null
                    }

                if (step != null) {
                    val progress = tutorialViewModel.progress

                    // Extraer vértice objetivo de las animaciones para posicionar burbuja
                    val targetVertex =
                        step.animations
                            .flatten()
                            .filterIsInstance<HighlightAnimation.Vertex>()
                            .firstOrNull()
                            ?.highlight
                            ?.vertex

                    val bubbleConfig =
                        if (targetVertex != null) {
                            coordinateMapper.getBubblePositionForVertex(targetVertex)
                        } else {
                            getDefaultBubbleConfigForStep(step)
                        }

                    // Determinar si estamos esperando interacción del usuario
                    val isWaitingForMove = state is TutorialState.WaitingForMove

                    TutorialBubble(
                        title = localizedString(step.titleRes),
                        bubbleState =
                            TutorialBubbleState(
                                contentState =
                                    TutorialBubbleContentState(
                                        description =
                                            if (isWaitingForMove) {
                                                localizedString(
                                                    Res.string.perform_the_indicated_move,
                                                    localizedString(step.descriptionRes),
                                                )
                                            } else {
                                                localizedString(step.descriptionRes)
                                            },
                                        canGoBack = progress.currentStepIndex > 1,
                                        canGoForward = true,
                                        currentStep = progress.currentStepIndex,
                                        totalSteps = progress.totalSteps,
                                    ),
                                config = bubbleConfig,
                            ),
                        bubbleEvents =
                            tutorialBubbleEvents(
                                viewModel = tutorialViewModel,
                                tutorialEvents = tutorialEvents,
                                updateGameState = updateGameState,
                            ),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            is TutorialState.Idle -> {}
            is TutorialState.Completed -> tutorialEvents.onFinishTutorial()
        }
    }
}

fun tutorialBubbleEvents(
    viewModel: ITutorialViewModel,
    tutorialEvents: TutorialEvents,
    updateGameState: (GameState) -> Unit,
): TutorialBubbleEvents =
    object : TutorialBubbleEvents {
        override fun onNext() {
            if (viewModel.tutorialManager.isWaitingForUserInteraction()) {
                // Skip interactive step: apply expected move to show result briefly,
                // then nextStep (300ms internal delay) transitions to the next step.
                tutorialEvents.onSkipInteractiveStep()
                viewModel.nextStep()
            } else {
                tutorialEvents.onPreStepTutorial()
                viewModel.nextStep()
            }

            if (viewModel.isCompleted()) {
                tutorialEvents.onFinishTutorial()
            }
        }

        override fun onPrevious() {
            tutorialEvents.onPreStepTutorial()
            viewModel.previousStep()
        }

        override fun onSkip() = tutorialEvents.onSkipTutorial()

        override fun onRepeat() {
            tutorialEvents.onPreStepTutorial()
            viewModel.repeatCurrentStep()

            val tutorialState = viewModel.getCurrentGameState()
            if (tutorialState != null) {
                updateGameState(tutorialState)
            }
        }
    }
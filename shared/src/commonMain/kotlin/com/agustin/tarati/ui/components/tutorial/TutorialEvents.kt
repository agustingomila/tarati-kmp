package com.agustin.tarati.ui.components.tutorial

data class TutorialEvents(
    val onPreStepTutorial: () -> Unit = {},
    val onCompleted: () -> Unit = {},
    val onFinishTutorial: () -> Unit = {},
    val onSkipTutorial: () -> Unit = {},
    val onSkipInteractiveStep: () -> Unit = {},
)
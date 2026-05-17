package com.agustin.tarati.ui.components.tutorial

data class TutorialBubbleContentState(
    val description: String,
    val canGoBack: Boolean,
    val canGoForward: Boolean,
    val currentStep: Int,
    val totalSteps: Int,
)

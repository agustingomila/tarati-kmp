package com.agustin.tarati.ui.components.tutorial

import androidx.compose.runtime.Stable

@Stable
interface TutorialBubbleEvents {
    fun onNext()

    fun onPrevious()

    fun onSkip()

    fun onRepeat()
}
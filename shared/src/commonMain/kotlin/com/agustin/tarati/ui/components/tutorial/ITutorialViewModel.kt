package com.agustin.tarati.ui.components.tutorial

import com.agustin.tarati.core.domain.tutorial.TutorialManager
import com.agustin.tarati.core.domain.tutorial.TutorialProgress
import com.agustin.tarati.core.domain.tutorial.TutorialState
import com.agustin.tarati.services.sound.ISoundService
import kotlinx.coroutines.flow.StateFlow

interface ITutorialViewModel : ITutorialService {
    val soundService: ISoundService
    val tutorialManager: TutorialManager
    val tutorialState: StateFlow<TutorialState>
    val progress: TutorialProgress
}
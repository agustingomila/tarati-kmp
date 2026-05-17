package com.agustin.tarati.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.ui.components.editor.IEditBoardManager
import kotlinx.coroutines.flow.StateFlow

interface IGameModel :
    IGameService,
    IEditBoardManager,
    IPlayerManager,
    IGamesLibraryManager {

    /**
     * Estado combinado del juego, expuesto como [State] de Compose para que
     * [GameScreen] y [GameScreenState] reaccionen a cambios sin observar cada
     * flow individual. Se declara como propiedad [Composable] porque combina
     * internamente cuatro [StateFlow] con [combine] + [collectAsState].
     *
     * Para consumidores fuera de Compose (NavGraph, tests), usar los StateFlows
     * individuales [gameState], [history], [moveIndex], [gameStatus] de [IGameService].
     */
    val gameManagerState: State<GameManagerState>
        @Composable get

    val boardPosition: StateFlow<String>
    val boardOrientation: StateFlow<BoardOrientation>

    /**
     * True when the user has manually rotated the board via the sidebar button.
     * While true, automatic orientation updates from [GameEffects] are ignored.
     */
    val isManuallyRotated: StateFlow<Boolean>

    val pasteRequested: StateFlow<Boolean>

    /** False when GameScreen is entered via game import (no logo animation). */
    val showLogoTransition: StateFlow<Boolean>
    val userName: StateFlow<String>

    /** Difficulty used when the White side is controlled by the AI engine. */
    val difficultyWhite: StateFlow<Difficulty>

    /** Difficulty used when the Black side is controlled by the AI engine. */
    val difficultyBlack: StateFlow<Difficulty>
}
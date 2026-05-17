package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation

sealed class AnimationEvent {
    data class MoveEvent(
        val move: Move,
        val oldGameState: GameState,
        val newGameState: GameState,
        /**
         * True si este movimiento terminó la partida, evaluado en
         * [GameEvents.applyMove]
         * inmediatamente después de registrar el movimiento en el historial de posiciones.
         *
         * Este flag viaja con el [MoveEvent] para que el pipeline de animación
         * sepa cuándo emitir el sonido de fin de partida y señalizar
         * [IBoardAnimationViewModel.gameOverReady], sin necesidad de re-evaluar
         * [GameState.isGameOver] más tarde (cuando el historial global ya fue modificado
         * por movimientos posteriores en partidas AI vs. AI). Especialmente relevante
         * en triple repetición, donde la misma posición aparece 3 veces y dispararía
         * el flag en cada ocurrencia.
         */
        val isGameOver: Boolean = false,
    ) : AnimationEvent()

    data class HighlightEvent(
        val highlights: List<HighlightAnimation>,
        val source: String = "unknown",
    ) : AnimationEvent()

    data class TutorialHighlightEvent(
        val highlights: List<List<HighlightAnimation>>,
        val source: String = "unknown",
    ) : AnimationEvent()

    object StopHighlights : AnimationEvent()

    object Reset : AnimationEvent()

    object SyncState : AnimationEvent()

    object ClearQueue : AnimationEvent()

    /**
     * Señaliza un fin de partida que **no proviene de un movimiento animado**
     * (p. ej. [GameEvents.claimFiftyMoveDraw]).
     *
     * Al no haber animación de movimiento en curso, [IBoardAnimationViewModel.gameOverReady]
     * se emite inmediatamente desde [AnimationCoordinator], en lugar de esperar a que
     * el consumidor del `moveChannel` termine de animar el último movimiento.
     */
    object NotifyGameOver : AnimationEvent()
}
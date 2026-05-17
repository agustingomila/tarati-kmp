package com.agustin.tarati.ui.components.game.animation

class AnimationCoordinator(
    private val animationViewModel: IBoardAnimationViewModel,
) {
    fun handleEvent(event: AnimationEvent) {
        when (event) {
            is AnimationEvent.MoveEvent ->
                animationViewModel.processMove(
                    move = event.move,
                    oldGameState = event.oldGameState,
                    newGameState = event.newGameState,
                    isGameOver = event.isGameOver,
                )

            is AnimationEvent.HighlightEvent ->
                animationViewModel.animateSerie(
                    sequences = listOf(event.highlights),
                    source = event.source,
                )

            is AnimationEvent.TutorialHighlightEvent ->
                animationViewModel.loadTutorialStep(
                    sequences = event.highlights,
                    source = event.source,
                )

            AnimationEvent.StopHighlights -> animationViewModel.stopHighlights()
            AnimationEvent.Reset -> animationViewModel.reset()
            AnimationEvent.SyncState -> animationViewModel.forceSync()
            AnimationEvent.ClearQueue -> animationViewModel.clearQueue()

            // Fin de partida sin movimiento animado (ej. tablas por 50 movimientos reclamadas
            // automáticamente por la IA). Se señaliza gameOverReady de forma inmediata, sin
            // esperar al consumidor del moveChannel, porque no hay animación pendiente.
            AnimationEvent.NotifyGameOver -> animationViewModel.notifyGameOver()
        }
    }
}
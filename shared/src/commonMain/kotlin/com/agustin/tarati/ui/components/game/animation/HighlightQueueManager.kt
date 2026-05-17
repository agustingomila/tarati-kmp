package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.sequences.GameOverSequenceProvider
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Gestiona la cola de secuencias de highlights del tablero.
 *
 * Separado de [BoardAnimationViewModel] para aislar la lógica de highlights
 * (secuencias de animación visual sobre vértices, aristas y regiones) del
 * pipeline de movimientos de piezas. Ambas responsabilidades comparten el mismo
 * [CoroutineScope] del ViewModel, pero operan sobre estados distintos y pueden
 * razonarse de forma independiente.
 *
 * ## Cola FIFO con [animateSerie]
 * Las secuencias se añaden a [animationQueue] y se procesan una a una en
 * [processQueue]. [ui.components.game.animation.SequenceLoadMode] controla si la secuencia nueva cancela la
 * actual, espera a que termine, o se encola inmediatamente.
 *
 * ## Interacción con [BoardAnimationViewModel]
 * El pipeline de movimientos llama a [animateParallel] directamente (efectos
 * durante el movimiento de piezas). Los callers externos ([AnimationCoordinator],
 * [GameScreenSideEffects]) pasan por
 * [IBoardAnimationViewModel], que delega aquí.
 *
 * @param scope          Scope del ViewModel propietario — las coroutines de
 *                       highlights se cancelan al destruirse el ViewModel.
 * @param animateEffects Flow de solo lectura que indica si los efectos visuales
 *                       están habilitados. Controlado por el ViewModel.
 * @param onHighlightsChanged Callback invocado cuando la lista de highlights activos
 *                       cambia, para actualizar el StateFlow del ViewModel.
 */
internal class HighlightQueueManager(
    private val scope: CoroutineScope,
    private val animateEffects: StateFlow<Boolean>,
    private val onHighlightsChanged: (List<HighlightAnimation>) -> Unit,
) {
    private val logger = getLogger("HighlightQueueManager")

    private val animationQueue = mutableListOf<AnimationGroup>()
    private var isProcessingQueue = false
    private var currentAnimationJob: Job? = null

    // ── Public API ────────────────────────────────────────────────────────────

    fun animateParallel(
        highlights: List<HighlightAnimation>,
        source: String,
        currentHighlights: List<HighlightAnimation>,
    ) {
        scope.launch {
            val newHighlights = highlights.filter { !currentHighlights.contains(it) }
            if (newHighlights.isEmpty()) {
                logger.warn("Skipping existing animation from $source...")
                return@launch
            }
            setHighlights(newHighlights)
        }
    }

    fun animateSerie(
        sequences: List<List<HighlightAnimation>>,
        source: String,
        mode: SequenceLoadMode,
    ) {
        scope.launch {
            when (mode) {
                SequenceLoadMode.CANCEL_CURRENT -> cancelCurrentQueueAndWait()
                SequenceLoadMode.WAIT_FOR_COMPLETION -> waitForCurrentAnimations()
                SequenceLoadMode.QUEUE_IMMEDIATELY -> Unit
            }

            sequences.forEachIndexed { index, sequence ->
                animationQueue.add(
                    AnimationGroup(
                        highlights = sequence,
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                        source = "$source sequence: $index size: ${sequence.size}",
                    ),
                )
            }

            logger.debug("Processing queue.")
            processQueue()
        }
    }

    fun clearQueue() {
        scope.launch { cancelCurrentQueueAndWait() }
    }

    fun stopHighlights() {
        scope.launch { onHighlightsChanged(emptyList()) }
    }

    fun loadTutorialStep(
        sequences: List<List<HighlightAnimation>>,
        source: String,
    ) {
        animateSerie(
            sequences = sequences,
            source = source,
            mode = SequenceLoadMode.CANCEL_CURRENT,
        )
    }

    fun animateGameOver(matchState: MatchState) {
        if (animateEffects.value) {
            val sequences = GameOverSequenceProvider.getRandomSequence(matchState)
            animateSerie(
                sequences = sequences,
                source = "game_over",
                mode = SequenceLoadMode.WAIT_FOR_COMPLETION,
            )
        }
        // Sonido de game over se emite en BoardAnimationViewModel.animateMoveSequence,
        // al final de la animación del último movimiento, para sincronización visual.
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private suspend fun setHighlights(highlights: List<HighlightAnimation>) {
        onHighlightsChanged(highlights)
        val maxDuration = highlights.maxOfOrNull { getAnimationDuration(it) } ?: 0L
        delay(maxDuration.milliseconds)
        onHighlightsChanged(emptyList())
    }

    private fun processQueue() {
        if (isProcessingQueue) {
            logger.debug("Queue already being processed, skipping.")
            return
        }
        if (animationQueue.isEmpty()) {
            logger.debug("Queue is empty, nothing to process")
            return
        }

        logger.debug("Starting queue processing with ${animationQueue.size} groups")
        isProcessingQueue = true

        currentAnimationJob = scope.launch {
            try {
                while (animationQueue.isNotEmpty()) {
                    val group = animationQueue.removeAt(0)
                    logger.debug("Executing group: ${group.groupId} from ${group.source}")
                    setHighlights(group.highlights)
                }
            } catch (_: CancellationException) {
                logger.debug("Cancelling queue processing")
            } finally {
                currentAnimationJob = null
                isProcessingQueue = false
                logger.debug("Finalizing queue processed")
            }

            // Pequeña pausa para asegurar estabilidad
            delay(16L.milliseconds) // ~1 frame a 60fps
        }
    }

    private suspend fun waitForCurrentAnimations() {
        currentAnimationJob?.let { job ->
            if (job.isActive) {
                try {
                    job.join()
                } catch (_: CancellationException) {
                    // Ignorar cancelaciones externas
                }
            }
        }
        delay(16L.milliseconds)
    }

    private suspend fun cancelCurrentQueueAndWait() {
        animationQueue.clear()
        onHighlightsChanged(emptyList())

        currentAnimationJob?.let { job ->
            if (job.isActive) {
                job.cancel()
                try {
                    job.join()
                } catch (_: CancellationException) {
                    // Ignorar — es normal al cancelar
                }
            }
        }

        currentAnimationJob = null
        isProcessingQueue = false

        delay(16L.milliseconds) // ~1 frame a 60fps
    }
}

private data class AnimationGroup(
    val highlights: List<HighlightAnimation>,
    val groupId: String = uuid4().toString(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val source: String = "unknown",
)
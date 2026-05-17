package com.agustin.tarati.ui.components.game.highlights.sequences.previews

import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.MatchState.Companion.createInitialMatchState
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.ui.components.game.animation.getAnimationDuration
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.createVertexAnimation
import com.agustin.tarati.ui.components.game.highlights.sequences.GameOverSequenceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PreviewAnimationController(
    private val coroutineScope: CoroutineScope,
) : IAnimationPreview {
    private val logger = getLogger()

    private val _currentHighlights = MutableStateFlow<List<HighlightAnimation>>(emptyList())
    val currentHighlights: StateFlow<List<HighlightAnimation>> = _currentHighlights.asStateFlow()

    private fun updateCurrentHighlights(highlights: List<HighlightAnimation>) {
        _currentHighlights.update { highlights }
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private fun updateIsPlaying(value: Boolean) {
        _isPlaying.update { value }
    }

    private val _currentSequenceName = MutableStateFlow("Animation Preview")
    val currentSequenceName: StateFlow<String> = _currentSequenceName.asStateFlow()

    private fun updateCurrentSequenceName(value: String) {
        _currentSequenceName.update { value }
    }

    private var animationJob: Job? = null

    // Datos de ejemplo para preview
    private val previewMatchState by lazy {
        createInitialMatchState().copy(
            winner = CobColor.WHITE, // IMPORTANTE: ¡Las secuencias de Game Over necesitan un ganador!
        )
    }

    override fun animateSequence(
        sequences: List<List<HighlightAnimation>>,
        source: String,
    ) {
        logger.debug("animateSequence called - ${sequences.size} sequences from $source")

        animationJob?.cancel()
        updateIsPlaying(true)
        updateCurrentSequenceName(source)

        animationJob =
            coroutineScope.launch {
                logger.debug("Starting animation job")

                // Reproducir cada secuencia una por una
                for ((index, sequence) in sequences.withIndex()) {
                    logger.debug("Showing sequence $index with ${sequence.size} highlights")
                    updateCurrentHighlights(sequence)

                    // Calcular la duración máxima de esta secuencia
                    val maxDuration = sequence.maxOfOrNull { getAnimationDuration(it) } ?: 300L
                    logger.debug("Waiting $maxDuration ms for sequence $index")

                    // Esperar antes de la siguiente secuencia
                    delay((maxDuration + 100L).milliseconds) // Pequeño margen entre secuencias
                }

                // Al finalizar, mantener la última secuencia visible
                updateIsPlaying(false)
                logger.debug("Animation finished")
            }
    }

    override fun animateGameOverSequence(sequenceIndex: Int) {
        logger.debug("animateGameOverSequence called - index: $sequenceIndex")

        updateIsPlaying(true)

        val sequences =
            if (sequenceIndex >= 0) {
                GameOverSequenceProvider.getSpecificSequence(previewMatchState, sequenceIndex)
            } else {
                GameOverSequenceProvider.getRandomSequence(previewMatchState)
            }

        logger.debug("Got ${sequences.size} sequences for Game Over")

        if (sequences.isEmpty()) {
            logger.debug("No sequences returned! Check MatchState configuration.")
            // Fallback: crear una secuencia simple de prueba
            val fallbackSequence =
                listOf(
                    listOf(
                        createVertexAnimation(
                            vertex = GameBoard.A1,
                            duration = 1000L,
                            pulse = true,
                        ),
                        createVertexAnimation(
                            vertex = GameBoard.B1,
                            duration = 1000L,
                            pulse = true,
                        ),
                    ),
                )
            animateSequence(fallbackSequence, "Fallback Sequence")
            return
        }

        updateCurrentSequenceName(
            if (sequenceIndex >= 0) {
                "Game Over Sequence ${sequenceIndex + 1}"
            } else {
                "Random Game Over Sequence"
            },
        )

        animateSequence(sequences, _currentSequenceName.value)
    }

    override fun stopHighlights() {
        logger.debug("stopHighlights called")
        animationJob?.cancel()
        updateIsPlaying(false)
        updateCurrentHighlights(emptyList())
        updateCurrentSequenceName("Animation Preview")
    }

    override fun getAvailableSequenceCount(): Int = GameOverSequenceProvider.getSequenceCount()
}

package com.agustin.tarati.ui.components.game.highlights.sequences

import com.agustin.tarati.core.domain.game.play.GameResult
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.utils.logging.LoggingFactory
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation

object GameOverSequenceProvider {
    private val logger = LoggingFactory.getLogger("GameOverSequenceProvider")

    private val sequences: List<(MatchState) -> List<List<HighlightAnimation>>> =
        listOf(
            ::createGameOverSequence,
            ::createAlternativeGameOverSequence,
        )

    fun getRandomSequence(matchState: MatchState): List<List<HighlightAnimation>> {
        // Tablas (50 movimientos, triple repetición, etc.): animación dedicada.
        if (matchState.winner == null && matchState.gameResult != GameResult.PLAYING) {
            val sequence = createDrawSequence(matchState)
            logger.debug("GameOverSequenceProvider - Draw sequence, size: ${sequence.size}")
            return sequence
        }
        val selectedIndex = sequences.indices.random()
        logger.debug("GameOverSequenceProvider - Random index: $selectedIndex")
        val sequence = sequences[selectedIndex](matchState)
        logger.debug("GameOverSequenceProvider - Random sequence size: ${sequence.size}")
        return sequence
    }

    fun getSpecificSequence(
        matchState: MatchState,
        index: Int,
    ): List<List<HighlightAnimation>> {
        val safeIndex = index.coerceIn(0, sequences.size - 1)
        logger.debug("GameOverSequenceProvider - Specific index: $safeIndex")
        val sequence = sequences[safeIndex](matchState)
        logger.debug("GameOverSequenceProvider - Specific sequence size: ${sequence.size}")
        return sequence
    }

    fun getSequenceCount(): Int {
        logger.debug("GameOverSequenceProvider - Total sequences: ${sequences.size}")
        return sequences.size
    }
}
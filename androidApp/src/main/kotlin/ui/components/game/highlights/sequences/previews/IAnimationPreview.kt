package com.agustin.tarati.ui.components.game.highlights.sequences.previews

import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation

interface IAnimationPreview {
    fun animateSequence(
        sequences: List<List<HighlightAnimation>>,
        source: String = "preview",
    )

    fun animateGameOverSequence(sequenceIndex: Int = -1)

    fun stopHighlights()

    fun getAvailableSequenceCount(): Int
}

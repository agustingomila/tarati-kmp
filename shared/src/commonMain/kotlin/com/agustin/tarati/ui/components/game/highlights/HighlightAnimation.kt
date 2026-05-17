package com.agustin.tarati.ui.components.game.highlights

import com.agustin.tarati.ui.components.game.highlights.base.BaseHighlight
import com.agustin.tarati.ui.components.game.highlights.base.DynamicEdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.EdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.RegionHighlight
import com.agustin.tarati.ui.components.game.highlights.base.VertexHighlight

sealed class HighlightAnimation {
    open val highlight: BaseHighlight? = null

    data class Vertex(
        override val highlight: VertexHighlight,
    ) : HighlightAnimation()

    data class FireballEdge(
        override val highlight: EdgeHighlight,
    ) : HighlightAnimation()

    data class Arrow(
        override val highlight: EdgeHighlight,
    ) : HighlightAnimation()

    data class ElectricEdge(
        override val highlight: EdgeHighlight,
    ) : HighlightAnimation()

    data class Region(
        override val highlight: RegionHighlight,
    ) : HighlightAnimation()

    data class DynamicFireballEdge(
        override val highlight: DynamicEdgeHighlight,
    ) : HighlightAnimation()

    data class DynamicElectricEdge(
        override val highlight: DynamicEdgeHighlight,
    ) : HighlightAnimation()

    /**
     * Concentric ring burst at target position B when force arcs arrive: ((B)).
     * [highlight].from == [highlight].to == screen position of B.
     */
    data class DynamicForceArcImpact(
        override val highlight: DynamicEdgeHighlight,
    ) : HighlightAnimation()

    /**
     * Arc waves that travel from the moving piece toward each captured piece.
     * [highlight].from = interpolated position of the moving piece.
     * [highlight].to   = screen position of the target (B).
     * The [highlight].pulse progress (0–1) drives arc travel and size.
     */
    data class DynamicForceArc(
        override val highlight: DynamicEdgeHighlight,
    ) : HighlightAnimation()

    /**
     * Force arc connecting two board vertices (vertex-to-vertex, not screen positions).
     * Resolved to screen coordinates at draw time in
     * [BoardRenderer], same pattern
     * as [ElectricEdge]. Used in the GameOver alternative sequence to connect pieces.
     */
    data class ForceArcEdge(
        override val highlight: EdgeHighlight,
    ) : HighlightAnimation()

    data class Pause(
        val duration: Long = 300L,
    ) : HighlightAnimation()
}
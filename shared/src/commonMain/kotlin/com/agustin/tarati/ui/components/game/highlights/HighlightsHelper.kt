package com.agustin.tarati.ui.components.game.highlights

import androidx.compose.ui.geometry.Offset
import com.agustin.tarati.core.domain.game.board.Edge
import com.agustin.tarati.core.domain.game.board.Region
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.ui.components.game.highlights.base.DynamicEdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.EdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.RegionHighlight
import com.agustin.tarati.ui.components.game.highlights.base.VertexHighlight

fun createVertexAnimation(
    vertex: Vertex,
    duration: Long = 100L,
    startDelay: Long = 0L,
    pulse: Boolean = false,
    persistent: Boolean = false,
): HighlightAnimation =
    HighlightAnimation.Vertex(
        VertexHighlight(
            vertex = vertex,
            pulse = pulse,
            persistent = persistent,
            duration = duration,
            startDelay = startDelay,
        ),
    )

fun createRegionAnimation(
    region: Region,
    duration: Long = 100L,
): HighlightAnimation =
    HighlightAnimation.Region(
        RegionHighlight(
            region = region,
            duration = duration,
            pulse = false,
        ),
    )

fun createArrowAnimation(
    edge: Edge,
    duration: Long = 2000L,
    startDelay: Long = 0L,
    pulse: Boolean = false,
): HighlightAnimation.Arrow =
    HighlightAnimation.Arrow(
        EdgeHighlight(
            edge = edge,
            pulse = pulse,
            duration = duration,
            startDelay = startDelay,
        ),
    )

fun createMoveDynamicHighlight(
    fromPos: Offset,
    toPos: Offset,
    toVertex: Vertex,
): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.DynamicFireballEdge(
            DynamicEdgeHighlight(
                from = fromPos,
                to = toPos,
                pulse = true,
                duration = 600L,
                startDelay = 200L,
            ),
        ),
        HighlightAnimation.Vertex(
            VertexHighlight(
                vertex = toVertex,
                pulse = true,
                duration = 600L,
                startDelay = 400L,
            ),
        ),
    )

fun createValidMovesHighlights(validMoves: List<Vertex>): List<HighlightAnimation> =
    validMoves.map { vertex ->
        HighlightAnimation.Vertex(
            VertexHighlight(
                vertex = vertex,
                duration = 400L,
            ),
        )
    }

fun createRegionHighlight(
    region: Region,
    duration: Long = 400L,
): HighlightAnimation =
    HighlightAnimation.Region(
        RegionHighlight(
            region = region,
            duration = duration,
        ),
    )

fun createCaptureHighlight(vertex: Vertex): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.Vertex(
            VertexHighlight(
                vertex = vertex,
                pulse = true,
                duration = 600L,
                action = HighlightAction.CAPTURE,
            ),
        ),
    )

/**
 * Concentric ring burst at [pos] when force arcs arrive at the capture target.
 * Rendered as [HighlightAnimation.DynamicForceArcImpact] using a degenerate from==to.
 */
fun createForceArcImpactHighlight(pos: Offset): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.DynamicForceArcImpact(
            DynamicEdgeHighlight(
                from = pos,
                to = pos,
                pulse = true,
                duration = 400L,
                action = HighlightAction.CAPTURE,
            ),
        ),
    )

fun createForceArcDynamicHighlight(
    fromPos: Offset,
    toPos: Offset,
): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.DynamicForceArc(
            DynamicEdgeHighlight(
                from = fromPos,
                to = toPos,
                pulse = true,
                duration = 300L,
                action = HighlightAction.CAPTURE,
            ),
        ),
    )

fun createCaptureDynamicHighlight(
    fromPos: Offset,
    toPos: Offset,
): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.DynamicElectricEdge(
            DynamicEdgeHighlight(
                from = fromPos,
                to = toPos,
                pulse = true,
                duration = 300L,
                action = HighlightAction.CAPTURE,
            ),
        ),
    )

fun createUpgradeHighlight(vertex: Vertex): List<HighlightAnimation> =
    listOf(
        HighlightAnimation.Vertex(
            VertexHighlight(
                vertex = vertex,
                pulse = true,
                duration = 600L,
                action = HighlightAction.UPGRADE,
            ),
        ),
    )

/**
 * Highlights enemy pieces that would be captured if any available move is made from the
 * currently selected vertex. Shown as persistent pulsing CAPTURE highlights while the
 * piece remains selected. Cleared when the selection is canceled or a move is made.
 */
fun createSelectionCaptureHighlights(vertices: List<Vertex>): List<HighlightAnimation> =
    vertices.map { vertex ->
        HighlightAnimation.Vertex(
            VertexHighlight(
                vertex = vertex,
                pulse = true,
                persistent = true,
                duration = 800L,
                action = HighlightAction.CAPTURE,
            ),
        )
    }

fun Vertex.createHighlight(
    duration: Long = 100L,
    pulse: Boolean = false,
    persistent: Boolean = false,
): HighlightAnimation = createVertexAnimation(this, duration, pulse = pulse, persistent = persistent)

fun Region.createHighlight(duration: Long = 100L): HighlightAnimation = createRegionAnimation(this, duration)

fun Edge.createElectricHighlight(
    duration: Long = 100L,
    pulse: Boolean = false,
    persistent: Boolean = false,
): HighlightAnimation = createElectricEdgeAnimation(this, duration, pulse = pulse, persistent = persistent)

fun createElectricEdgeAnimation(
    edge: Edge,
    duration: Long = 100L,
    startDelay: Long = 0L,
    pulse: Boolean = false,
    persistent: Boolean = false,
): HighlightAnimation =
    HighlightAnimation.ElectricEdge(
        EdgeHighlight(
            edge = edge,
            pulse = pulse,
            persistent = persistent,
            duration = duration,
            startDelay = startDelay,
        ),
    )

fun Edge.createForceArcHighlight(
    duration: Long = 300L,
    pulse: Boolean = false,
): HighlightAnimation = createForceArcEdgeAnimation(this, duration, pulse)

fun createForceArcEdgeAnimation(
    edge: Edge,
    duration: Long = 300L,
    pulse: Boolean = true,
): HighlightAnimation =
    HighlightAnimation.ForceArcEdge(
        EdgeHighlight(
            edge = edge,
            pulse = pulse,
            duration = duration,
        ),
    )
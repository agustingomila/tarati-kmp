package com.agustin.tarati.ui.components.game.draw.board.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.Edge
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.B6
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C10
import com.agustin.tarati.core.domain.game.board.GameBoard.C11
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C4
import com.agustin.tarati.core.domain.game.board.GameBoard.C5
import com.agustin.tarati.core.domain.game.board.GameBoard.C6
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.C9
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.board.GameBoard.bridgeEdges
import com.agustin.tarati.core.domain.game.board.GameBoard.bridgeVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.centralRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.circumferenceRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.domesticRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.Region
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.ui.components.game.animation.AnimatedCob
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
import com.agustin.tarati.ui.components.game.draw.board.drawFireballEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawRegionHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawVertexHighlight
import com.agustin.tarati.ui.components.game.draw.pieces.drawAnimatedCob
import com.agustin.tarati.ui.components.game.draw.pieces.drawCob
import com.agustin.tarati.ui.components.game.highlights.HighlightAction
import com.agustin.tarati.ui.components.game.highlights.base.EdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.RegionHighlight
import com.agustin.tarati.ui.components.game.highlights.base.VertexHighlight
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ChristmasPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.HalloweenPalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.getBoardColors

// ── Shared constants ──────────────────────────────────────────────────────────

private const val ICON_DP = 512
private val ORIENTATION = BoardOrientation.PORTRAIT_WHITE

/**
 * Base canvas para todos los achievement icons.
 * Escala y centra el tablero completo (incluyendo perímetro) y aplica un marco redondeado.
 * @param BoardPalette Tema de colores (Classic, Nature, Dark, Grayscale)
 */
@Composable
private fun AchievementIcon(
    palette: BoardPalette = ClassicPalette,
    paddingFactor: Float = 0.96f,
    expansion: Float = 0.12f,
    cornerRadiusDP: Int = 24,
    draw: DrawScope.(colors: BoardColors, size: Size) -> Unit
) {
    val colors = getBoardColors(palette)
    Canvas(
        modifier = Modifier
            .size(ICON_DP.dp)
            .clip(RoundedCornerShape(cornerRadiusDP.dp))
    ) {
        // Fondo oscuro (sin transformar)
        drawRect(color = colors.neutralColor)

        // Calcular bounding box de todos los vértices
        val positions = vertices.map { getVisualPosition(it, this.size, ORIENTATION) }
        val minX = positions.minOf { it.x }
        val maxX = positions.maxOf { it.x }
        val minY = positions.minOf { it.y }
        val maxY = positions.maxOf { it.y }

        val boardCenterX = (minX + maxX) / 2f
        val boardCenterY = (minY + maxY) / 2f

        val boardWidth = maxX - minX
        val boardHeight = maxY - minY
        val expandedWidth = boardWidth * (1 + 2 * expansion)
        val expandedHeight = boardHeight * (1 + 2 * expansion)

        val scaleX = this.size.width / expandedWidth * paddingFactor
        val scaleY = this.size.height / expandedHeight * paddingFactor
        val finalScale = minOf(scaleX, scaleY)

        val canvasCenterX = this.size.width / 2f
        val canvasCenterY = this.size.height / 2f

        // Transformaciones: llevar centro del tablero al origen, escalar, luego al centro del canvas
        translate(canvasCenterX, canvasCenterY) {
            scale(finalScale, finalScale, Offset.Zero) {
                translate(-boardCenterX, -boardCenterY) {
                    drawBoardBackground(
                        canvasSize = this@Canvas.size,
                        orientation = ORIENTATION,
                        regionsVisible = true,
                        perimeterVisible = true,
                        colors = colors,
                    )
                    draw(colors, this@Canvas.size)
                }
            }
        }

        // Borde redondeado decorativo (sin transformar)
        drawRoundRect(
            color = Color.White.copy(alpha = 0.2f),
            style = Stroke(width = 4f),
            size = size,
            cornerRadius = CornerRadius(cornerRadiusDP.dp.toPx(), cornerRadiusDP.dp.toPx())
        )
    }
}

/** Draws a cob at a given vertex, scaled by [scale] relative to board-derived radius. */
private fun DrawScope.iconCob(
    vertex: Vertex,
    cob: Cob,
    colors: BoardColors,
    scale: Float = 1f,
    selected: Boolean = false,
) {
    val pos = getVisualPosition(vertex, size, ORIENTATION)
    val radius = minOf(size.width, size.height) * 0.065f * scale
    drawCob(
        position = pos,
        radius = radius,
        selectedVertex = if (selected) vertex else null,
        vertex = vertex,
        cob = cob,
        hourOfDay = 20f,
        colors = colors,
    )
}

/** Dibuja el mismo tipo de cob en múltiples vértices. */
private fun DrawScope.iconCobs(
    vertices: List<Vertex>,
    cob: Cob,
    colors: BoardColors,
    scale: Float = 1f,
) = vertices.forEach { iconCob(it, cob, colors, scale) }

/** Dibuja un cob animado (mejorando) en un vértice. */
private fun DrawScope.iconAnimatedCob(
    vertex: Vertex,
    cob: Cob,
    upgradeProgress: Float,
    colors: BoardColors,
    scale: Float = 1f,
) {
    val pos = getVisualPosition(vertex, size, ORIENTATION)
    val radius = minOf(size.width, size.height) * 0.065f * scale
    drawAnimatedCob(
        position = pos,
        radius = radius,
        vertex = vertex,
        selectedVertex = null,
        animatedCob = AnimatedCob(
            vertex = vertex,
            cob = cob,
            currentPos = vertex,
            targetPos = vertex,
            animationProgress = 1f,
            upgradeProgress = upgradeProgress,
        ),
        hourOfDay = 20f,
        colors = colors,
    )
}

/** Dibuja cobs heterogéneos a partir de pares (Vertex, Cob). */
private fun DrawScope.iconCobPairs(
    pairs: List<Pair<Vertex, Cob>>,
    colors: BoardColors,
    scale: Float = 1f,
) = pairs.forEach { (vertex, cob) -> iconCob(vertex, cob, colors, scale) }

/** Dibuja el rok blanco en el centro con aristas radiantes hacia los vértices dados. */
private fun DrawScope.iconCenterRok(targets: List<Vertex>, colors: BoardColors) {
    targets.forEach { iconEdge(Edge(A1 to it), colors) }
    iconCob(A1, Cob(WHITE, true), colors, scale = 1.4f)
}

// ── Icon-scoped draw helpers ──────────────────────────────────────────────────
// Eliminan la repetición de canvasSize/orientation/colors en cada llamada.

private fun DrawScope.iconRegion(region: Region, colors: BoardColors) =
    drawRegionHighlight(
        RegionHighlight(region = region, duration = 500L, pulse = false),
        canvasSize = size, orientation = ORIENTATION, colors = colors,
    )

private fun DrawScope.iconVertex(vertex: Vertex, action: HighlightAction, colors: BoardColors) =
    drawVertexHighlight(
        VertexHighlight(vertex = vertex, action = action, pulse = false),
        canvasSize = size, orientation = ORIENTATION, colors = colors,
    )

private fun DrawScope.iconEdge(edge: Edge, colors: BoardColors) =
    drawFireballEdgeHighlightFromVertex(
        EdgeHighlight(edge = edge, pulse = false),
        canvasSize = size, orientation = ORIENTATION, colors = colors,
    )

// ─────────────────────────────────────────────────────────────────────────────
// 1. Welcome to Tarati — full board with all pieces in starting positions
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconWelcomeToTarati() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        iconCobs(listOf(C1, C2, D1, D2), Cob(WHITE, false), colors)
        iconCobs(listOf(C7, C8, D3, D4), Cob(BLACK, false), colors)
        iconVertex(A1, HighlightAction.MOVE, colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. First Capture — white cob captures black cob at bridge vertex
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconFirstCapture() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        // Black cob about to be captured
        iconCob(B4, Cob(BLACK, false), colors, scale = 1.2f)
        drawVertexHighlight(
            highlight = VertexHighlight(vertex = B4, action = HighlightAction.CAPTURE, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
        // FireballEdge showing capture path
        drawFireballEdgeHighlightFromVertex(
            highlight = EdgeHighlight(edge = Edge(B1 to A1), pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
        // White cob making the capture
        iconCob(B1, Cob(WHITE, false), colors, scale = 1.3f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. First Promotion — cob promoting to rok at enemy home base
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconFirstPromotion() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        iconCobs(listOf(C3, B2, B6, C12), Cob(WHITE, false), colors)
        iconCobs(listOf(C4, A1, C11), Cob(BLACK, false), colors)
        iconAnimatedCob(C7, Cob(WHITE, false), upgradeProgress = 0.6f, colors = colors, scale = 1.4f)
        iconVertex(C7, HighlightAction.UPGRADE, colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. First Victory — white rok dominates the center
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconFirstVictory() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        // Dominant white rok at the absolute center
        iconCob(A1, Cob(WHITE, true), colors, scale = 1.6f)
        drawVertexHighlight(
            highlight = VertexHighlight(vertex = A1, action = HighlightAction.MOVE, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Play 10 Games — number "10" formed by pieces on bridge ring
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconPlay10Games() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        iconCobPairs(
            listOf(
                B2 to Cob(WHITE, false),
                B3 to Cob(WHITE, true),
                B5 to Cob(BLACK, false),
                B6 to Cob(BLACK, true),
                C9 to Cob(BLACK, true),
                C10 to Cob(BLACK, false),
                C11 to Cob(BLACK, true),
                C12 to Cob(BLACK, false),
            ), colors
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. The Flipper — rok surrounded by capture opportunities
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconTheFlipper() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        iconCob(A1, Cob(WHITE, true), colors, scale = 1.4f)
        bridgeVertices.forEach { iconVertex(it, HighlightAction.CAPTURE, colors) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. Rok Master — three white roks controlling the center
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconRokMaster() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        listOf(B1, B4).forEach { iconCob(it, Cob(WHITE, true), colors, scale = 1.3f) }
        iconCob(A1, Cob(WHITE, true), colors, scale = 1.6f)
        listOf(B1, A1, B4).forEach { iconVertex(it, HighlightAction.UPGRADE, colors) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. Unstoppable — diagonal edge path connecting both home bases
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconUnstoppable() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        iconEdge(Edge(D1 to D3), colors)
        iconEdge(Edge(D2 to D4), colors)
        iconCobs(listOf(D1, D2), Cob(WHITE, true), colors, scale = 1.2f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. Champion — golden rok at center with bridge ring highlighted
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconChampion() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        bridgeVertices.forEach { iconVertex(it, HighlightAction.UPGRADE, colors) }
        iconAnimatedCob(A1, Cob(WHITE, true), upgradeProgress = 1f, colors = colors, scale = 2f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. Mit — white pieces dominating, last black piece surrounded
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconMit() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        iconCobs(listOf(C4, C10, C3, C5, C11, C12), Cob(WHITE, false), colors)
        iconCob(B4, Cob(BLACK, false), colors, scale = 1.1f)
        iconVertex(B4, HighlightAction.CAPTURE, colors)
        iconEdge(Edge(B1 to A1), colors)
        iconCob(B1, Cob(WHITE, true), colors, scale = 1.3f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 11. Stalemit — black pieces trapped in central regions
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconStalemit() {
    AchievementIcon(palette = DarkPalette) { colors, _ ->
        listOf(2, 3).forEach { iconRegion(centralRegions[it], colors) }
        iconCobs(listOf(B3, B5), Cob(BLACK, false), colors)
        iconCobs(listOf(C4, B2, B6, C11), Cob(WHITE, false), colors, scale = 1.1f)
        iconCobs(listOf(B1, A1), Cob(WHITE, true), colors, scale = 1.1f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 12. Eternal Loop — circular bridge ring with pieces in triangle formation
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconEternalLoop() {
    AchievementIcon(palette = DarkPalette) { colors, _ ->
        bridgeEdges.forEach { iconEdge(it, colors) }
        iconCobPairs(
            listOf(
                B1 to Cob(WHITE, false),
                B3 to Cob(BLACK, false),
                B5 to Cob(WHITE, false),
            ), colors
        )
        iconCob(A1, Cob(BLACK, true), colors, scale = 1.4f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 13. Fifty Move Rule — long game with pieces in starting circumferences
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconFiftyMoveRule() {
    AchievementIcon(palette = DarkPalette) { colors, _ ->
        centralRegions.forEach { iconRegion(it, colors) }
        listOf(C1 to C7, C9 to C3, C5 to C11).forEach { (white, black) ->
            iconCob(black, Cob(BLACK, true), colors)
            iconEdge(Edge(white to black), colors)
            iconCob(white, Cob(WHITE, true), colors)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 14. Dead but Dangerous — promotion from dead vertex capturing last enemy
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconDeadButDangerous() {
    AchievementIcon(palette = DarkPalette) { colors, _ ->
        // Black home base region highlighted
        drawRegionHighlight(
            highlight = RegionHighlight(region = domesticRegions[1], duration = 500L, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
        // Last black piece about to be captured
        iconCob(D4, Cob(BLACK, false), colors, scale = 1.1f)
        // White cob promoting from dead vertex D3
        iconAnimatedCob(vertex = D3, cob = Cob(WHITE, false), upgradeProgress = 0.75f, colors = colors, scale = 1.5f)
        // Upgrade highlight on dead vertex
        drawVertexHighlight(
            highlight = VertexHighlight(vertex = D3, action = HighlightAction.UPGRADE, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )

        // White home base region highlighted
        drawRegionHighlight(
            highlight = RegionHighlight(region = domesticRegions[0], duration = 500L, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
        // Last white piece about to be captured
        iconCob(D2, Cob(WHITE, false), colors, scale = 1.1f)
        // Black cob promoting from dead vertex D1
        iconAnimatedCob(vertex = D1, cob = Cob(BLACK, false), upgradeProgress = 0.75f, colors = colors, scale = 1.5f)
        // Upgrade highlight on dead vertex
        drawVertexHighlight(
            highlight = VertexHighlight(vertex = D1, action = HighlightAction.UPGRADE, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 15. Grandmaster — symmetric endgame with all roks and remaining cobs
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconGrandmaster() {
    AchievementIcon(palette = GrayscalePalette) { colors, _ ->
        circumferenceRegions.filterIndexed { i, _ -> i % 3 == 0 }
            .forEach { iconRegion(it, colors) }
        iconAnimatedCob(A1, Cob(WHITE, true), upgradeProgress = 1f, colors = colors, scale = 1.6f)
        listOf(B1, B2, B3).forEach { iconCob(it, Cob(WHITE, true), colors) }
        listOf(B4, B5, B6).forEach { iconCob(it, Cob(BLACK, true), colors) }
        listOf(D1, D2).forEach { iconCob(it, Cob(WHITE, false), colors) }
        listOf(D3, D4).forEach { iconCob(it, Cob(BLACK, false), colors) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 16. Halloween Theme — dark board, black rok at center surrounded by captures
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconHalloweenTheme() {
    AchievementIcon(palette = HalloweenPalette) { colors, _ ->
        bridgeVertices.forEach { iconVertex(it, HighlightAction.CAPTURE, colors) }
        iconCob(C2, Cob(WHITE, true), colors)
        iconCob(C5, Cob(WHITE, false), colors)
        iconCob(C8, Cob(WHITE, true), colors)
        iconCob(C11, Cob(WHITE, false), colors)
        iconAnimatedCob(A1, Cob(BLACK, true), upgradeProgress = 1f, colors = colors, scale = 2f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 17. Christmas Theme — star at center, tree-shaped piece formation
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconChristmasTheme() {
    AchievementIcon(palette = ChristmasPalette) { colors, _ ->
        // Black cobs and roks forming a star
        iconCob(C1, Cob(BLACK, false), colors)
        iconCob(C3, Cob(BLACK, true), colors)
        iconCob(C5, Cob(BLACK, false), colors)
        iconCob(C7, Cob(BLACK, true), colors)
        iconCob(C9, Cob(BLACK, false), colors)
        iconCob(C11, Cob(BLACK, true), colors)
        // Middle branches — white roks on bridge ring
        GameBoard.centerVertices.forEach { v ->
            drawVertexHighlight(
                highlight = VertexHighlight(vertex = v, action = HighlightAction.UPGRADE, pulse = false),
                canvasSize = size, orientation = ORIENTATION, colors = colors,
            )
            iconCob(v, Cob(WHITE, true), colors)
        }
        // Star at the top — fully upgraded white rok at absolute center
        iconAnimatedCob(vertex = A1, cob = Cob(WHITE, true), upgradeProgress = 1f, colors = colors, scale = 2f)
        drawVertexHighlight(
            highlight = VertexHighlight(vertex = A1, action = HighlightAction.UPGRADE, pulse = false),
            canvasSize = size, orientation = ORIENTATION, colors = colors,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 18. Apprentice
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconApprentice() {
    AchievementIcon(palette = ClassicPalette) { colors, _ ->
        listOf(0, 2, 4).forEach {
            drawRegionHighlight(
                highlight = RegionHighlight(region = centralRegions[it], duration = 500L, pulse = false),
                canvasSize = size, orientation = ORIENTATION, colors = colors,
            )
        }
        iconCob(C1, Cob(WHITE, false), colors)
        iconCob(C7, Cob(BLACK, false), colors)
        iconCenterRok(listOf(C1, C7), colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 19. Strategist
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconStrategist() {
    AchievementIcon(palette = NaturePalette) { colors, _ ->
        listOf(0, 4, 8).forEach {
            drawRegionHighlight(
                highlight = RegionHighlight(region = circumferenceRegions[it], duration = 500L, pulse = false),
                canvasSize = size, orientation = ORIENTATION, colors = colors,
            )
        }
        listOf(B1, B3, B5).forEach { iconCob(it, Cob(WHITE, false), colors) }
        listOf(B2, B4, B6).forEach { iconCob(it, Cob(BLACK, false), colors) }
        iconCenterRok(listOf(B1, B3, B5), colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 20. Tactician
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconTactician() {
    AchievementIcon(palette = DarkPalette) { colors, _ ->
        listOf(2, 6, 10).forEach {
            drawRegionHighlight(
                highlight = RegionHighlight(region = circumferenceRegions[it], duration = 500L, pulse = false),
                canvasSize = size, orientation = ORIENTATION, colors = colors,
            )
        }
        listOf(C1, C2, C5, C6, C9, C10).forEach { iconCob(it, Cob(WHITE, false), colors) }
        listOf(B2, B4, B6).forEach { iconCob(it, Cob(BLACK, false), colors) }
        iconCenterRok(listOf(C1, C2, C5, C6, C9, C10), colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 21. The First Light — white roks radiating from the center, dawn spreading
// Logro: ganar con Blancas. Desbloquea la paleta Aurora.
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconFirstLight() {
    AchievementIcon(palette = AuroraPalette) { colors, _ ->
        // Aristas bridge resaltadas — luz irradiando desde el centro hacia afuera
        bridgeEdges.forEach { iconEdge(it, colors) }

        // Roks blancos en los vértices puente alternos — luz llegando a los puntos de apoyo
        listOf(B1, B3, B5).forEach { iconCob(it, Cob(WHITE, true), colors, scale = 1.1f) }

        // Piezas negras en los bridge restantes con highlight de captura — oscuridad retrocediendo
        listOf(B2, B4, B6).forEach { v ->
            iconVertex(v, HighlightAction.CAPTURE, colors)
            iconCob(v, Cob(BLACK, false), colors, scale = 0.9f)
        }

        // Piezas negras en D3/D4 — últimas en pie, siendo capturadas
        listOf(D3, D4).forEach { v ->
            iconVertex(v, HighlightAction.CAPTURE, colors)
            iconCob(v, Cob(BLACK, false), colors)
        }

        // Roks blancos seguros en la base blanca
        listOf(D1, D2).forEach { iconCob(it, Cob(WHITE, false), colors) }

        // Rok blanco animado en el centro absoluto — la primera luz, plenamente expandida
        iconAnimatedCob(
            vertex = A1,
            cob = Cob(WHITE, true),
            upgradeProgress = 1f,
            colors = colors,
            scale = 2.0f,
        )
        iconVertex(A1, HighlightAction.UPGRADE, colors)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 22. The Dark Side — black roks dominating from center, ember fire spreading
// Logro: ganar con Negras. Desbloquea la paleta Ember.
// ─────────────────────────────────────────────────────────────────────────────

@Preview(group = "AchievementIcons", showBackground = false, widthDp = ICON_DP, heightDp = ICON_DP)
@Composable
fun IconDarkSide() {
    AchievementIcon(palette = EmberPalette) { colors, _ ->
        // Regiones centrales resaltadas — fuego en el corazón del tablero
        centralRegions.forEachIndexed { i, region ->
            if (i % 2 == 0) iconRegion(region, colors)
        }

        // Roks negros en todos los bridge — control total, brasas cerrando el tablero
        bridgeVertices.forEach { iconCob(it, Cob(BLACK, true), colors, scale = 1.1f) }

        // Roks negros y blancos en la circunferencia
        iconVertex(C5, HighlightAction.CAPTURE, colors)
        iconCob(C5, Cob(WHITE, true), colors, scale = 0.9f)
        iconVertex(C11, HighlightAction.CAPTURE, colors)
        iconCob(C11, Cob(BLACK, true), colors, scale = 0.9f)

        // Roks negros dominando la base negra
        listOf(D3, C8).forEach { v ->
            iconVertex(v, HighlightAction.CAPTURE, colors)
            iconCob(v, Cob(BLACK, false), colors, scale = 0.9f)
        }
        iconCob(D4, Cob(BLACK, true), colors)

        // Roks blancos dominando la base blanca
        listOf(C2, D1).forEach { v ->
            iconVertex(v, HighlightAction.CAPTURE, colors)
            iconCob(v, Cob(WHITE, false), colors)
        }
        iconCob(D2, Cob(WHITE, true), colors)

        // Rok negro animado en el centro absoluto — el lado oscuro, en su máximo poder
        iconAnimatedCob(
            vertex = A1,
            cob = Cob(BLACK, true),
            upgradeProgress = 1f,
            colors = colors,
            scale = 2.0f,
        )
        iconVertex(A1, HighlightAction.UPGRADE, colors)
    }
}
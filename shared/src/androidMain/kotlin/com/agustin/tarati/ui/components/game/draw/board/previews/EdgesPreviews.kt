package com.agustin.tarati.ui.components.game.draw.board.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.Edge
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.buildPositionCache
import com.agustin.tarati.ui.components.game.draw.board.drawEdges
import com.agustin.tarati.ui.components.game.draw.board.drawElectricEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawFireballEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawForceArcDynamicHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawForceArcImpactHighlight
import com.agustin.tarati.ui.components.game.draw.board.getHighlightsSegmentsRange
import com.agustin.tarati.ui.components.game.highlights.HighlightAction
import com.agustin.tarati.ui.components.game.highlights.base.DynamicEdgeHighlight
import com.agustin.tarati.ui.components.game.highlights.base.EdgeHighlight
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.ChristmasPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.HalloweenPalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.getBoardColors
import kotlin.random.Random

// ==================== CLASSIC ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== DARK ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== NATURE ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}
// ==================== GRAYSCALE ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== CHRISTMAS ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== HALLOWEEN ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_BLACK,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== GILDED ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== AURORA ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}


// ==================== EMBER ====================

@Preview(group = "Edges", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PreviewDrawEdgesEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewDrawEdges(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewFireballEdgeHighlightsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation)
}

@Preview(group = "Edges", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewElectricEdgeHighlightsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewElectricEdgeHighlights(boardColors, boardOrientation, true)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcHighlightEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
) {
    PreviewForceArcHighlight(boardColors)
}

@Preview(group = "DynamicForceArc", showBackground = true, widthDp = 200, heightDp = 250)
@Composable
fun PreviewForceArcImpactHighlightEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
) {
    PreviewForceArcImpactHighlight(boardColors)
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun PreviewDrawEdges(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawEdges(
                        canvasSize = size,
                        orientation = boardOrientation,
                        boardState =
                            PreviewStates.emptyBoardState.copy(
                                boardVisualState =
                                    PreviewStates.emptyBoardState.boardVisualState.copy(
                                        edgesVisibles = true,
                                    ),
                                boardOrientation = boardOrientation,
                            ),
                        colors = boardColors,
                    )
                }
                Text(
                    text = "Aristas del Tablero",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun PreviewElectricEdgeHighlights(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
    isElectric: Boolean = false,
) {
    val random = remember { Random(seed = 42) }
    var randomSegments by remember { mutableIntStateOf(8) }
    var randomSeed by remember { mutableFloatStateOf(0.5f) }

    TaratiTheme {
        Column {
            Text(
                text = "Resaltados de Aristas${if (isElectric) " (Alt)" else ""}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Surface(
                    modifier = Modifier.size(150.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                    ) {
                        val highlight =
                            EdgeHighlight(
                                edge = Edge(D1 to D3),
                                pulse = true,
                            )
                        Canvas(modifier = Modifier.matchParentSize()) {
                            if (isElectric) {
                                val positionCache = buildPositionCache(size, boardOrientation)
                                val segmentsRange = getHighlightsSegmentsRange(highlight, positionCache)
                                randomSegments = random.nextInt(segmentsRange.first, segmentsRange.second)
                                randomSeed = random.nextFloat()

                                drawElectricEdgeHighlightFromVertex(
                                    highlight = highlight,
                                    canvasSize = size,
                                    orientation = boardOrientation,
                                    variationFactor = randomSeed,
                                    randomSegments = randomSegments,
                                    colors = boardColors,
                                )
                            } else {
                                drawFireballEdgeHighlightFromVertex(
                                    canvasSize = size,
                                    orientation = boardOrientation,
                                    highlight = highlight,
                                    colors = boardColors,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== FORCE ARC COMPOSABLES GENERALES ====================

/**
 * Muestra el efecto de arcos de fuerza viajando de A )))) B.
 * El highlight usa [System.currentTimeMillis] internamente, por lo que
 * cada render del preview muestra una fase distinta del ciclo.
 */
@Composable
fun PreviewForceArcHighlight(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
) {
    TaratiTheme {
        Column {
            Text(
                text = "Arco de Fuerza A )))) B",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.size(150.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val highlight = DynamicEdgeHighlight(
                            from = Offset(size.width * 0.15f, size.height * 0.5f),
                            to = Offset(size.width * 0.85f, size.height * 0.5f),
                            pulse = true,
                            duration = 300L,
                            action = HighlightAction.CAPTURE,
                        )
                        drawForceArcDynamicHighlight(highlight, boardColors)
                    }
                }
            }
        }
    }
}

/**
 * Muestra el efecto de ondas concéntricas en B: ((B)).
 * El highlight usa [System.currentTimeMillis] internamente.
 */
@Composable
fun PreviewForceArcImpactHighlight(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
) {
    TaratiTheme {
        Column {
            Text(
                text = "Impacto de Arco ((B))",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.size(150.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val highlight = DynamicEdgeHighlight(
                            from = center,
                            to = center,
                            pulse = true,
                            duration = 400L,
                            action = HighlightAction.CAPTURE,
                        )
                        drawForceArcImpactHighlight(highlight, boardColors)
                    }
                }
            }
        }
    }
}
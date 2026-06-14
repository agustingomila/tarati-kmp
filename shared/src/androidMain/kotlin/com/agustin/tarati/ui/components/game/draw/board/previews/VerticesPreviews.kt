package com.agustin.tarati.ui.components.game.draw.board.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.ui.components.game.draw.board.drawVertexHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawVertices
import com.agustin.tarati.ui.components.game.highlights.HighlightAction
import com.agustin.tarati.ui.components.game.highlights.base.VertexHighlight
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

// ==================== CLASSIC ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesClassicWithLabels(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesClassicNoLabels(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsClassicLandscape(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== DARK ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesDarkWithLabels(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesDarkNoLabels(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsDarkLandscape(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_BLACK,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== NATURE ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesNatureWithLabels(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesNatureNoLabels(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsNatureLandscape(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== GRAYSCALE ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesGrayscaleWithLabels(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesGrayscaleNoLabels(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsGrayscaleLandscape(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== CHRISTMAS ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesChristmasWithLabels(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesChristmasNoLabels(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsChristmasLandscape(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== HALLOWEEN ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesHalloweenWithLabels(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesHalloweenNoLabels(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsHalloweenLandscape(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_BLACK,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== EMBER ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesEmberWithLabels(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesEmberNoLabels(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsEmberLandscape(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== AURORA ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesAuroraWithLabels(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesAuroraNoLabels(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsAuroraLandscape(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== GILDED ====================

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesGildedWithLabels(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesWithLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewDrawVerticesGildedNoLabels(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewDrawVerticesNoLabels(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewVertexHighlightsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

@Preview(group = "Vertices", showBackground = true, widthDp = 600, heightDp = 350)
@Composable
fun PreviewVertexHighlightsGildedLandscape(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    PreviewVertexHighlights(boardColors, boardOrientation)
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun PreviewDrawVerticesWithLabels(
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
                    drawVertices(
                        canvasSize = size,
                        vWidth = 60f,
                        selectedVertex = A1,
                        adjacentVertexes = listOf(B2, C3),
                        boardState =
                            PreviewStates.populatedBoardState.copy(
                                boardVisualState =
                                    PreviewStates.populatedBoardState.boardVisualState.copy(
                                        labelsVisibles = true,
                                        verticesVisibles = true,
                                    ),
                                boardOrientation = boardOrientation,
                            ),
                        colors = boardColors,
                    )
                }
                Text(
                    text = "Vértices con Etiquetas",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun PreviewDrawVerticesNoLabels(
    boardColors: BoardColors = getBoardColors(NaturePalette),
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
                    drawVertices(
                        canvasSize = size,
                        vWidth = 60f,
                        selectedVertex = null,
                        adjacentVertexes = emptyList(),
                        boardState =
                            PreviewStates.emptyBoardState.copy(
                                boardVisualState =
                                    PreviewStates.emptyBoardState.boardVisualState.copy(
                                        labelsVisibles = false,
                                        verticesVisibles = true,
                                    ),
                                boardOrientation = boardOrientation,
                            ),
                        colors = boardColors,
                    )
                }
                Text(
                    text = "Vértices sin Etiquetas",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun PreviewVertexHighlights(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Resaltados de Vértices",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Captura
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawVertexHighlight(
                                highlight = VertexHighlight(
                                    vertex = A1,
                                    action = HighlightAction.CAPTURE,
                                    pulse = true,
                                ),
                                canvasSize = size,
                                orientation = boardOrientation,
                                colors = boardColors,
                            )
                        }
                        Text(
                            text = "Captura",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                // Upgrade
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawVertexHighlight(
                                highlight = VertexHighlight(
                                    vertex = B2,
                                    action = HighlightAction.UPGRADE,
                                    pulse = true,
                                ),
                                canvasSize = size,
                                orientation = boardOrientation,
                                colors = boardColors,
                            )
                        }
                        Text(
                            text = "Upgrade",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                // Movimiento normal
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawVertexHighlight(
                                highlight = VertexHighlight(
                                    vertex = C3,
                                    action = HighlightAction.MOVE,
                                    pulse = false,
                                ),
                                canvasSize = size,
                                orientation = boardOrientation,
                                colors = boardColors,
                            )
                        }
                        Text(
                            text = "Movimiento",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
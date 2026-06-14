package com.agustin.tarati.ui.components.game.draw.board.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.GameBoard.centralRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.domesticRegions
import com.agustin.tarati.ui.components.game.draw.board.drawBoardPatternSingleColor
import com.agustin.tarati.ui.components.game.draw.board.drawBoardPatternTwoColors
import com.agustin.tarati.ui.components.game.draw.board.drawRegionHighlight
import com.agustin.tarati.ui.components.game.highlights.base.RegionHighlight
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

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== DARK ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== NATURE ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== GRAYSCALE ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== CHRISTMAS ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== HALLOWEEN ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== GILDED ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== AURORA ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== EMBER ====================

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewRegionHighlightsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewRegionHighlights(boardColors, boardOrientation)
}

@Preview(group = "Regions", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardPatternsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    PreviewBoardPatterns(boardColors, boardOrientation)
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun PreviewRegionHighlights(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Resaltados de Regiones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Región central con pulso
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
                            val centralRegion = centralRegions.firstOrNull()
                            if (centralRegion != null) {
                                drawRegionHighlight(
                                    highlight = RegionHighlight(
                                        region = centralRegion,
                                        duration = 500L,
                                        pulse = true,
                                    ),
                                    canvasSize = size,
                                    orientation = boardOrientation,
                                    colors = boardColors,
                                )
                            }
                        }
                        Text(
                            text = "Central + Pulso",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                // Región doméstica sin pulso
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
                            val domesticRegion = domesticRegions.firstOrNull()
                            if (domesticRegion != null) {
                                drawRegionHighlight(
                                    highlight = RegionHighlight(
                                        region = domesticRegion,
                                        duration = 500L,
                                        pulse = false,
                                    ),
                                    canvasSize = size,
                                    orientation = boardOrientation,
                                    colors = boardColors,
                                )
                            }
                        }
                        Text(
                            text = "Doméstica",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                // Región de circunferencia
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
                            val circumferenceRegion = GameBoard.circumferenceRegions.firstOrNull()
                            if (circumferenceRegion != null) {
                                drawRegionHighlight(
                                    highlight = RegionHighlight(
                                        region = circumferenceRegion,
                                        duration = 500L,
                                        pulse = true,
                                    ),
                                    canvasSize = size,
                                    orientation = boardOrientation,
                                    colors = boardColors,
                                )
                            }
                        }
                        Text(
                            text = "Circunferencia",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewBoardPatterns(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Patrones del Tablero",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Patrón dos colores
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
                            drawBoardPatternTwoColors(
                                canvasSize = size,
                                regions = centralRegions.take(4),
                                surfaceColor1 = boardColors.boardPatternColor3,
                                surfaceColor2 = boardColors.boardPatternColor2,
                                borderColor = boardColors.boardPatternBorderColor,
                                orientation = boardOrientation,
                            )
                        }
                        Text(
                            text = "2 Colores",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                // Patrón un color
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
                            drawBoardPatternSingleColor(
                                canvasSize = size,
                                regions = domesticRegions.take(2),
                                surfaceColor = boardColors.boardPatternColor1,
                                borderColor = boardColors.boardPatternBorderColor,
                                orientation = boardOrientation,
                            )
                        }
                        Text(
                            text = "1 Color",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
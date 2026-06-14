package com.agustin.tarati.ui.components.game.draw.board.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.components.editor.previews.boardRenderEventsEmpty
import com.agustin.tarati.ui.components.editor.previews.tapEventsEmpty
import com.agustin.tarati.ui.components.game.BoardState
import com.agustin.tarati.ui.components.game.BoardState.Companion.createInitialBoardState
import com.agustin.tarati.ui.components.game.draw.board.BoardRenderer
import com.agustin.tarati.ui.components.game.draw.board.createEmptyBoardRenderData
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
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

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererClassicVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(ClassicPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererClassicVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(ClassicPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundClassicFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(ClassicPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundClassicNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(ClassicPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundClassicNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(ClassicPalette),
    )
}

// ==================== DARK ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererDarkVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(DarkPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererDarkVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(DarkPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundDarkFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(DarkPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_BLACK,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundDarkNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(DarkPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_BLACK,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundDarkNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(DarkPalette),
    )
}

// ==================== NATURE ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererNatureVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(NaturePalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererNatureVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(NaturePalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundNatureFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(NaturePalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundNatureNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(NaturePalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundNatureNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(NaturePalette),
    )
}

// ==================== GRAYSCALE ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererGrayscaleVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(GrayscalePalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererGrayscaleVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(GrayscalePalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundGrayscaleFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(GrayscalePalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundGrayscaleNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(GrayscalePalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundGrayscaleNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(GrayscalePalette),
    )
}

// ==================== CHRISTMAS ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererChristmasVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(ChristmasPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererChristmasVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(ChristmasPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundChristmasFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(ChristmasPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundChristmasNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(ChristmasPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundChristmasNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(ChristmasPalette),
    )
}

// ==================== HALLOWEEN ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererHalloweenVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(HalloweenPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererHalloweenVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(HalloweenPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundHalloweenFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(HalloweenPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_BLACK,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundHalloweenNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(HalloweenPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_BLACK,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundHalloweenNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(HalloweenPalette),
    )
}

// ==================== GILDED ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererGildedVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(GildedPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererGildedVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(GildedPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundGildedFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(GildedPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundGildedNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(GildedPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundGildedNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(GildedPalette),
    )
}

// ==================== EMBER ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererEmberVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(EmberPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererEmberVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(EmberPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundEmberFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(EmberPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundEmberNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(EmberPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundEmberNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(EmberPalette),
    )
}

// ==================== AURORA ====================

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererAuroraVerticesVisible() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(AuroraPalette),
        verticesVisible = true,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardRendererAuroraVerticesHidden() {
    PreviewBoardRenderer(
        boardColors = getBoardColors(AuroraPalette),
        verticesVisible = false,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundAuroraFull() {
    PreviewBoardBackgroundFull(
        boardColors = getBoardColors(AuroraPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 500, heightDp = 300)
@Composable
fun PreviewBoardBackgroundAuroraNoRegions() {
    PreviewBoardBackgroundNoRegions(
        boardColors = getBoardColors(AuroraPalette),
        boardOrientation = BoardOrientation.LANDSCAPE_WHITE,
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 300, heightDp = 500)
@Composable
fun PreviewBoardBackgroundAuroraNoPerimeter() {
    PreviewBoardBackgroundNoPerimeter(
        boardColors = getBoardColors(AuroraPalette),
    )
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun PreviewBoardRenderer(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
    verticesVisible: Boolean = true,
) {
    FullBackground(colors = boardColors, boardOrientation = boardOrientation)
    TaratiTheme {
        Box(
            modifier = Modifier.size(400.dp),
        ) {
            BoardRenderer(
                modifier = Modifier.matchParentSize(),
                playerSide = CobColor.WHITE,
                boardState = PreviewStates.populatedBoardState.copy(
                    boardOrientation = boardOrientation,
                    boardVisualState = PreviewStates.populatedBoardState.boardVisualState.copy(
                        verticesVisibles = verticesVisible,
                    ),
                ),
                boardData = createEmptyBoardRenderData(),
                boardEvents = boardRenderEventsEmpty,
                tapEvents = tapEventsEmpty,
            )
        }
    }
}

@Composable
fun PreviewBoardBackgroundFull(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_WHITE,
) {
    TaratiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                FullBackground(Modifier.matchParentSize(), boardOrientation, boardColors)
                Text(
                    text = "Fondo Completo",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun FullBackground(
    modifier: Modifier = Modifier,
    boardOrientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
    colors: BoardColors = getBoardColors(ClassicPalette),
) {
    Canvas(modifier = modifier) {
        drawBoardBackground(
            canvasSize = size,
            orientation = boardOrientation,
            regionsVisible = true,
            perimeterVisible = true,
            colors = colors,
        )
    }
}

@Composable
fun PreviewBoardBackgroundNoRegions(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    boardOrientation: BoardOrientation = BoardOrientation.LANDSCAPE_BLACK,
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
                    drawBoardBackground(
                        canvasSize = size,
                        orientation = boardOrientation,
                        regionsVisible = false,
                        perimeterVisible = true,
                        colors = boardColors,
                    )
                }
                Text(
                    text = "Sin Regiones",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun PreviewBoardBackgroundNoPerimeter(
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
                    drawBoardBackground(
                        canvasSize = size,
                        orientation = boardOrientation,
                        regionsVisible = true,
                        perimeterVisible = false,
                        colors = boardColors,
                    )
                }
                Text(
                    text = "Sin Perímetro",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

object PreviewStates {
    val emptyBoardState = createInitialBoardState()

    val populatedBoardState =
        BoardState(
            gameState =
                GameState(
                    cobs =
                        mapOf(
                            A1 to Cob(CobColor.WHITE, false),
                            B2 to Cob(CobColor.WHITE, true),
                            C3 to Cob(CobColor.BLACK, false),
                            D4 to Cob(CobColor.BLACK, true),
                        ),
                    currentTurn = CobColor.BLACK,
                ),
            boardVisualState =
                BoardVisualState(
                    labelsVisibles = true,
                    verticesVisibles = true,
                    edgesVisibles = true,
                    regionsVisibles = true,
                    perimeterVisible = true,
                    animateEffects = true,
                ),
            boardOrientation = BoardOrientation.PORTRAIT_WHITE,
            isEditing = false,
            aiEnabled = false,
            newGame = false,
        )

    val editingBoardState =
        BoardState(
            gameState =
                GameState(
                    cobs =
                        mapOf(
                            A1 to Cob(CobColor.WHITE, false),
                            B5 to Cob(CobColor.BLACK, true),
                        ),
                    currentTurn = CobColor.WHITE,
                ),
            boardVisualState = BoardVisualState(),
            boardOrientation = BoardOrientation.PORTRAIT_WHITE,
            isEditing = true,
            aiEnabled = false,
            newGame = false,
        )
}
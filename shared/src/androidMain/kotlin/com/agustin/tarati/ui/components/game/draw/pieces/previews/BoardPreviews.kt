package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.C11
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C5
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState.Companion.createGameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.components.editor.previews.boardRenderEventsEmpty
import com.agustin.tarati.ui.components.editor.previews.tapEventsEmpty
import com.agustin.tarati.ui.components.game.Board
import com.agustin.tarati.ui.components.game.BoardState
import com.agustin.tarati.ui.components.game.draw.board.createEmptyBoardRenderData
import com.agustin.tarati.ui.components.game.draw.board.previews.BoardPreviewConfig
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.ChristmasPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.HalloweenPalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.PaletteManager
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.rememberBoardColors

// ==================== CLASSIC ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Classic_Portrait_VerticesVisible() {
    PaletteManager.setPalette(ClassicPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Classic_Portrait_VerticesHidden() {
    PaletteManager.setPalette(ClassicPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Classic_Landscape_EdgesVisibles() {
    PaletteManager.setPalette(ClassicPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_WHITE,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(edgesVisibles = true, verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== DARK ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Dark_Portrait_VerticesVisible() {
    PaletteManager.setPalette(DarkPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Dark_Portrait_VerticesHidden() {
    PaletteManager.setPalette(DarkPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Dark_Landscape_Editing() {
    PaletteManager.setPalette(DarkPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            isEditing = true,
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== NATURE ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Nature_Portrait_VerticesVisible() {
    PaletteManager.setPalette(NaturePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Nature_Portrait_VerticesHidden() {
    PaletteManager.setPalette(NaturePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Nature_Landscape_LabelsVisibles() {
    PaletteManager.setPalette(NaturePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_WHITE,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = true,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== GRAYSCALE ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Grayscale_Portrait_VerticesVisible() {
    PaletteManager.setPalette(GrayscalePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_BLACK,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Grayscale_Portrait_VerticesHidden() {
    PaletteManager.setPalette(GrayscalePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_BLACK,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Grayscale_Landscape_EdgesVisibles() {
    PaletteManager.setPalette(GrayscalePalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = true,
                verticesVisibles = false,
            ),
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== CHRISTMAS ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Christmas_Portrait_VerticesVisible() {
    PaletteManager.setPalette(ChristmasPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Christmas_Portrait_VerticesHidden() {
    PaletteManager.setPalette(ChristmasPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Christmas_Landscape_Full() {
    PaletteManager.setPalette(ChristmasPalette)
    val exampleGameState = createGameState {
        setTurn(WHITE)
        setCob(C2, WHITE, true)
        setCob(C8, BLACK, true)
        setCob(B1, WHITE, false)
        setCob(B4, BLACK, false)
        setCob(C5, WHITE, true)
        setCob(C11, BLACK, true)
    }
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_WHITE,
            gameState = exampleGameState,
            playerSide = WHITE,
            boardVisualState = BoardVisualState(
                labelsVisibles = true,
                edgesVisibles = true,
                verticesVisibles = true,
            ),
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== HALLOWEEN ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Halloween_Portrait_VerticesVisible() {
    PaletteManager.setPalette(HalloweenPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Halloween_Portrait_VerticesHidden() {
    PaletteManager.setPalette(HalloweenPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Halloween_Landscape_Editing() {
    PaletteManager.setPalette(HalloweenPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            isEditing = true,
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== GILDED ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Gilded_Portrait_VerticesVisible() {
    PaletteManager.setPalette(GildedPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Gilded_Portrait_VerticesHidden() {
    PaletteManager.setPalette(GildedPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Gilded_Landscape_Editing() {
    PaletteManager.setPalette(GildedPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            isEditing = true,
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== AURORA ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Aurora_Portrait_VerticesVisible() {
    PaletteManager.setPalette(AuroraPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Aurora_Portrait_VerticesHidden() {
    PaletteManager.setPalette(AuroraPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Aurora_Landscape_Editing() {
    PaletteManager.setPalette(AuroraPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            isEditing = true,
            boardColors = rememberBoardColors(),
        ),
    )
}

//// ==================== EMBER ====================

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Ember_Portrait_VerticesVisible() {
    PaletteManager.setPalette(EmberPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(BLACK),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(verticesVisibles = true),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun BoardPreview_Ember_Portrait_VerticesHidden() {
    PaletteManager.setPalette(EmberPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.PORTRAIT_WHITE,
            gameState = initialGameState(WHITE),
            playerSide = WHITE,
            boardVisualState = BoardVisualState(verticesVisibles = false),
            boardColors = rememberBoardColors(),
        ),
    )
}

@Preview(group = "Board", showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun BoardPreview_Ember_Landscape_Editing() {
    PaletteManager.setPalette(EmberPalette)
    BoardPreview(
        BoardPreviewConfig(
            orientation = BoardOrientation.LANDSCAPE_BLACK,
            gameState = initialGameState(),
            playerSide = BLACK,
            boardVisualState = BoardVisualState(
                labelsVisibles = false,
                edgesVisibles = false,
                verticesVisibles = false,
            ),
            isEditing = true,
            boardColors = rememberBoardColors(),
        ),
    )
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun BoardPreview(previewConfig: BoardPreviewConfig) {
    TaratiTheme {
        Board(
            playerSide = previewConfig.playerSide,
            boardState =
                BoardState(
                    gameState = previewConfig.gameState,
                    aiEnabled = false,
                    boardOrientation = previewConfig.orientation,
                    boardVisualState = previewConfig.boardVisualState,
                    isEditing = previewConfig.isEditing,
                ),
            boardData = createEmptyBoardRenderData().copy(gameState = previewConfig.gameState),
            boardEvents = boardRenderEventsEmpty,
            tapEvents = tapEventsEmpty,
        )
    }
}
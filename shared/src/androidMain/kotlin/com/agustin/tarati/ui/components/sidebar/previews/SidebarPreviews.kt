package com.agustin.tarati.ui.components.sidebar.previews

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.manager.GameManagerState.Companion.createInitialUiState
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.features.library.previews.exampleMoveHistory
import com.agustin.tarati.ui.components.sidebar.Sidebar
import com.agustin.tarati.ui.components.sidebar.SidebarGameState
import com.agustin.tarati.ui.components.sidebar.SidebarUIState

@Preview(showBackground = true, widthDp = 280, heightDp = 800)
@Composable
fun SidebarPreview() {
    MaterialTheme {
        val sidebarGameState =
            SidebarGameState(
                gameManagerState =
                    createInitialUiState().copy(
                        history = StableHistoryList(exampleMoveHistory),
                        moveIndex = 5,
                    ),
                playerSide = WHITE,
                difficultyWhite = Difficulty.DEFAULT,
                difficultyBlack = Difficulty.DEFAULT,
                isAIEnabled = true,
                boardOrientation = BoardOrientation.PORTRAIT_WHITE,
                whiteIsAI = false,
                blackIsAI = true,
            )

        Sidebar(
            sidebarState = sidebarGameState,
            events = PreviewSidebarEvents(),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=891dp,height=411dp")
@Composable
fun SidebarPreview_Dark() {
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        val sidebarGameState =
            SidebarGameState(
                gameManagerState =
                    createInitialUiState().copy(
                        history = StableHistoryList(exampleMoveHistory),
                        moveIndex = 5,
                    ),
                playerSide = BLACK,
                difficultyWhite = Difficulty.DEFAULT,
                difficultyBlack = Difficulty.DEFAULT,
                isAIEnabled = false,
                boardOrientation = BoardOrientation.PORTRAIT_WHITE,
                whiteIsAI = false,
                blackIsAI = true,
            )

        Sidebar(
            sidebarState = sidebarGameState,
            events = PreviewSidebarEvents(),
        )
    }
}

@Preview(showBackground = true, widthDp = 280, heightDp = 800)
@Composable
fun SidebarPreview_ExpandedDropdown() {
    MaterialTheme {
        val sidebarGameState =
            SidebarGameState(
                gameManagerState =
                    createInitialUiState().copy(
                        history = StableHistoryList(exampleMoveHistory),
                        moveIndex = 3,
                    ),
                playerSide = WHITE,
                difficultyWhite = Difficulty.DEFAULT,
                difficultyBlack = Difficulty.DEFAULT,
                isAIEnabled = true,
                boardOrientation = BoardOrientation.PORTRAIT_WHITE,
                whiteIsAI = false,
                blackIsAI = true,
            )

        var uiState by remember {
            mutableStateOf(
                SidebarUIState(
                    isDifficultyExpandedBlack = true
                )
            )
        }

        Sidebar(
            sidebarState = sidebarGameState,
            uiState = uiState,
            events = PreviewSidebarEvents(),
            onUIStateChange = { uiState = it },
        )
    }
}

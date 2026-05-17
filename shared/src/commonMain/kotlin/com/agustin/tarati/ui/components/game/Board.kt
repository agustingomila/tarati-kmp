package com.agustin.tarati.ui.components.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.time.ClockState
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.components.game.behaviors.TapEvents
import com.agustin.tarati.ui.components.game.draw.board.BoardRenderData
import com.agustin.tarati.ui.components.game.draw.board.BoardRenderEvents
import com.agustin.tarati.ui.components.game.draw.board.BoardRenderer
import com.agustin.tarati.ui.components.game.draw.board.PlayerCornerIndicators
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
import com.agustin.tarati.ui.theme.getBoardColors

/**
 * Contenedor principal del tablero con sus overlays.
 *
 * ## Overlays
 * El `Box` central superpone en orden:
 * 1. `Board` — canvas de renderizado (siempre).
 * 2. `content` — controles del editor (solo en modo edición).
 * 3. `tutorial` — overlay del tutorial (solo durante el tutorial).
 * 4. `turnIndicator` — indicador de turno, esquina superior derecha
 *    (solo en partida normal).
 * 5. `giftOverlay` — ícono de regalo de eventos especiales, esquina inferior
 *    izquierda (solo en partida normal, cuando hay eventos activos).
 * 6. [PlayerCornerIndicators] — insignias de tipo de jugador en las esquinas
 *    interiores del tablero renderizado (siempre que no haya edición activa).
 */
@Composable
fun CreateBoard(
    modifier: Modifier = Modifier,
    state: CreateBoardState,
    tapEvents: TapEvents,
    boardRenderData: BoardRenderData,
    boardRenderEvents: BoardRenderEvents,
    boardVisualState: BoardVisualState,
    tutorial: @Composable () -> Unit,
    content: @Composable () -> Unit,
    turnIndicator: @Composable (modifier: Modifier) -> Unit,
    giftOverlay: @Composable (modifier: Modifier) -> Unit = {},
    clockState: ClockState = ClockState.initial(TimeControlMode.Unlimited),
    preMovesEnabled: Boolean = false,
) {
    var factor by remember { mutableFloatStateOf(1f) }

    // En Edición el tablero más pequeño da espacio a los controles
    LaunchedEffect(state.isEditing) {
        val isEditing = state.isEditing
        factor = (if (isEditing) 0.85f else 1f)
    }

    // Construir el estado para Board
    val boardState =
        BoardState(
            gameState = state.gameState,
            aiEnabled = state.aiEnabled,
            whiteIsAI = state.whiteIsAI,
            blackIsAI = state.blackIsAI,
            boardOrientation =
                when {
                    state.isEditing -> state.editBoardOrientation
                    else -> state.boardOrientation
                },
            boardVisualState = boardVisualState,
            isEditing = state.isEditing,
        )

    Box(
        modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Board(
            modifier = Modifier.fillMaxSize(factor),
            playerSide = state.playerSide,
            boardState = boardState,
            tapEvents = tapEvents,
            boardEvents = boardRenderEvents,
            boardData = boardRenderData,
            isAIThinking = state.isAIThinking,
            preMovesEnabled = preMovesEnabled,
        )

        // ── Indicadores de tipo de jugador ────────────────────────────────────
        // Se dibujan ANTES de los overlays interactivos para que queden por
        // debajo de TurnIndicator, GiftOverlay y las burbujas que estos muestran.
        if (!state.isEditing && !state.isTutorialActive) {
            PlayerCornerIndicators(
                modifier = Modifier.fillMaxSize(factor),
                boardOrientation = boardState.boardOrientation,
                whiteIsAI = boardState.whiteIsAI,
                blackIsAI = boardState.blackIsAI,
                clockState = clockState,
            )
        }

        when {
            state.isEditing -> content()

            state.isTutorialActive -> tutorial()

            else -> {
                // TurnIndicator en TopEnd y gift icon en TopStart — horizontalmente
                // alineados al mismo nivel dentro del Box del tablero.
                turnIndicator(Modifier.align(Alignment.TopEnd))
                giftOverlay(Modifier.align(Alignment.BottomStart))
            }
        }
    }
}

@Composable
fun Board(
    modifier: Modifier = Modifier,
    playerSide: CobColor,
    boardState: BoardState,
    tapEvents: TapEvents,
    boardData: BoardRenderData,
    boardEvents: BoardRenderEvents,
    isAIThinking: Boolean = false,
    preMovesEnabled: Boolean = false,
) {
    val boardColors = getBoardColors()

    Canvas(modifier = modifier) {
        drawBoardBackground(
            canvasSize = size,
            orientation = boardState.boardOrientation,
            edgesVisible = boardState.boardVisualState.edgesVisibles,
            regionsVisible = boardState.boardVisualState.regionsVisibles,
            perimeterVisible = boardState.boardVisualState.perimeterVisible,
            colors = boardColors,
        )
    }

    BoardRenderer(
        modifier = modifier,
        playerSide = playerSide,
        boardState = boardState,
        tapEvents = tapEvents,
        boardEvents = boardEvents,
        boardData = boardData,
        isAIThinking = isAIThinking,
        preMovesEnabled = preMovesEnabled,
    )
}
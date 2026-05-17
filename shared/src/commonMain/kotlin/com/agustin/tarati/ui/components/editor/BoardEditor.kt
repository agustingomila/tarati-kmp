package com.agustin.tarati.ui.components.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.PieceCounts
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.board_notation
import com.agustin.tarati.shared.generated.resources.clear_board
import com.agustin.tarati.shared.generated.resources.invalid_schema
import com.agustin.tarati.shared.generated.resources.paste_board_position
import com.agustin.tarati.shared.generated.resources.piece
import com.agustin.tarati.shared.generated.resources.rotate_board
import com.agustin.tarati.shared.generated.resources.side
import com.agustin.tarati.shared.generated.resources.start
import com.agustin.tarati.shared.generated.resources.start_game
import com.agustin.tarati.shared.generated.resources.turn
import com.agustin.tarati.ui.components.game.draw.board.drawIndicatorPiece
import com.agustin.tarati.ui.theme.TaratiIcons
import com.agustin.tarati.ui.theme.getBoardColors
import org.jetbrains.compose.resources.StringResource

@Composable
fun EditControls(
    isLandscapeScreen: Boolean,
    colorState: EditColorState,
    actionState: EditActionState,
    editBoard: IEditBoard,
) {
    val colorEvents =
        EditColorEvents(
            onPlayerSideToggle = editBoard::togglePlayerSide,
            onColorToggle = editBoard::toggleEditColor,
            onTurnToggle = editBoard::toggleEditTurn,
        )
    val actionEvents =
        EditActionEvents(
            onRotate = editBoard::rotateEditBoard,
            onStartGame = editBoard::startGameFromEditedState,
            onClearBoard = editBoard::clearEditBoard,
            onCopyPosition = editBoard::copyBoardToClipboard,
            onPastePosition = { editBoard.pasteBoardFromClipboard(true) },
        )

    CreateEditControls(
        isLandscapeScreen = isLandscapeScreen,
        colorState = colorState,
        colorEvents = colorEvents,
        actionState = actionState,
        actionEvents = actionEvents,
    )
}

@Composable
fun CreateEditControls(
    isLandscapeScreen: Boolean,
    colorState: EditColorState,
    colorEvents: EditColorEvents,
    actionState: EditActionState,
    actionEvents: EditActionEvents,
) {
    if (isLandscapeScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            LeftControls(
                modifier = Modifier.align(CenterStart),
                state = colorState,
                events = colorEvents,
            )

            RightControls(
                modifier = Modifier.align(CenterEnd),
                state = actionState,
                events = actionEvents,
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            TopControls(
                modifier = Modifier.align(TopCenter),
                state = colorState,
                events = colorEvents,
            )

            BottomControls(
                modifier = Modifier.align(BottomCenter),
                state = actionState,
                events = actionEvents,
            )
        }
    }
}

@Composable
fun LeftControls(
    modifier: Modifier = Modifier,
    state: EditColorState = EditColorState(),
    events: EditColorEvents = EditColorEvents(),
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ColorToggleButton(
            currentColor = state.editColor,
            onColorToggle = events.onColorToggle,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlayerSideToggleButton(
            playerSide = state.playerSide,
            onPlayerSideToggle = events.onPlayerSideToggle,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TurnToggleButton(
            currentTurn = state.editTurn,
            onTurnToggle = events.onTurnToggle,
        )
    }
}

@Composable
fun TopControls(
    modifier: Modifier = Modifier,
    state: EditColorState = EditColorState(),
    events: EditColorEvents = EditColorEvents(),
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        ColorToggleButton(
            currentColor = state.editColor,
            onColorToggle = events.onColorToggle,
        )
        PlayerSideToggleButton(
            playerSide = state.playerSide,
            onPlayerSideToggle = events.onPlayerSideToggle,
        )
        TurnToggleButton(
            currentTurn = state.editTurn,
            onTurnToggle = events.onTurnToggle,
        )
    }
}

@Composable
fun RightControls(
    modifier: Modifier = Modifier,
    state: EditActionState = EditActionState(),
    events: EditActionEvents = EditActionEvents(),
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RotateBoardButton(onClick = events.onRotate)
        Spacer(modifier = Modifier.height(12.dp))
        PositionControls(
            onCopyBoard = events.onCopyPosition,
            onPasteBoard = events.onPastePosition,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ClearBoardButton(onClick = events.onClearBoard)
        Spacer(modifier = Modifier.height(12.dp))
        PieceCounter(
            whiteCount = state.pieceCounts.white,
            blackCount = state.pieceCounts.black,
            isValid = state.isValidDistribution,
        )
        Spacer(modifier = Modifier.height(12.dp))
        StartGameButton(
            isCompletedDistribution = state.isCompletedDistribution,
            onClick = events.onStartGame,
        )
    }
}

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    state: EditActionState = EditActionState(),
    events: EditActionEvents = EditActionEvents(),
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RotateButton(onRotate = events.onRotate)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PositionControls(
                onCopyBoard = events.onCopyPosition,
                onPasteBoard = events.onPastePosition,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ClearBoardButton(onClick = events.onClearBoard)
        }

        StartButtonAndPieceCounter(
            pieceCounts = state.pieceCounts,
            isValidDistribution = state.isValidDistribution,
            isCompletedDistribution = state.isCompletedDistribution,
            onClick = events.onStartGame,
        )
    }
}

@Composable
fun RotateBoardButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(40.dp),
    ) {
        Icon(
            TaratiIcons.RotateRight,
            contentDescription = localizedString(Res.string.rotate_board),
        )
    }
}

@Composable
fun TurnToggleButton(
    currentTurn: CobColor,
    onTurnToggle: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        CreateToggleButton(currentTurn, Res.string.turn, onTurnToggle)
    }
}

@Composable
fun PlayerSideToggleButton(
    playerSide: CobColor,
    onPlayerSideToggle: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        CreateToggleButton(playerSide, Res.string.side, onPlayerSideToggle)
    }
}

@Composable
fun CreateToggleButton(
    color: CobColor,
    textRes: StringResource,
    onToggle: () -> Unit,
) {
    val bc = getBoardColors()
    val fillColor = if (color == WHITE) bc.whiteCobColor else bc.blackCobColor
    val borderColor = if (color == WHITE) bc.whiteCobBorderColor else bc.blackCobBorderColor

    FloatingActionButton(
        onClick = onToggle,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(40.dp),
    ) {
        CobDisc(cobColor = color, sizeDp = 24)
    }
    Spacer(modifier = Modifier.height(4.dp))
    LocalizedText(
        resource = textRes,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun StartGameButton(
    isCompletedDistribution: Boolean,
    onClick: () -> Unit,
) {
    CreateButton(
        onClick = onClick,
        isEnabled = isCompletedDistribution,
        icon = TaratiIcons.PlayArrow,
        buttonRes = Res.string.start_game,
        contentRes = Res.string.start,
    )
}

@Composable
fun CreateButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    icon: ImageVector,
    buttonRes: StringResource,
    contentRes: StringResource,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
            ),
    ) {
        LocalizedText(buttonRes)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(icon, contentDescription = localizedString(contentRes))
    }
}

@Composable
fun ClearBoardButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Icon(
            TaratiIcons.Delete,
            contentDescription = localizedString(Res.string.clear_board),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
fun StartButtonAndPieceCounter(
    pieceCounts: PieceCounts,
    isValidDistribution: Boolean,
    isCompletedDistribution: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PieceCounter(
            whiteCount = pieceCounts.white,
            blackCount = pieceCounts.black,
            isValid = isValidDistribution,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CreateButton(
            onClick = onClick,
            isEnabled = isCompletedDistribution,
            icon = TaratiIcons.PlayArrow,
            buttonRes = Res.string.start_game,
            contentRes = Res.string.start,
        )
    }
}

@Composable
fun RotateButton(onRotate: () -> Unit) {
    FloatingActionButton(
        onClick = onRotate,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Icon(
            TaratiIcons.RotateRight,
            contentDescription = localizedString(Res.string.rotate_board),
        )
    }
}

@Composable
fun ColorToggleButton(
    currentColor: CobColor,
    onColorToggle: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        CreateToggleButton(currentColor, Res.string.piece, onColorToggle)
    }
}

@Composable
private fun CobDisc(
    cobColor: CobColor,
    sizeDp: Int,
) {
    val bc = getBoardColors()
    Canvas(modifier = Modifier.size(sizeDp.dp)) {
        val r = size.minDimension / 2f
        val c = Offset(r, r)
        drawIndicatorPiece(
            position = c,
            radius = r,
            cobColor = cobColor,
            colors = bc
        )
    }
}

// Contador de Piezas en Edición de Tablero
@Composable
fun PieceCounter(
    whiteCount: Int,
    blackCount: Int,
    isValid: Boolean,
) {
    val bc = getBoardColors()
    val whiteFill = bc.whiteCobColor
    val whiteBorder = bc.whiteCobBorderColor
    val blackFill = bc.blackCobColor
    val blackBorder = bc.blackCobBorderColor
    // Color del número derivado de la luminancia del disco para garantizar legibilidad.
    val whiteText = if (whiteFill.luminance() > 0.35f) Color(0xFF1A1A1A) else Color(0xFFF5F0EB)
    val blackText = if (blackFill.luminance() > 0.35f) Color(0xFF1A1A1A) else Color(0xFFF5F0EB)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            if (isValid) {
                CardDefaults.cardColors()
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Disco blanco con número superpuesto
            Box(contentAlignment = Alignment.Center) {
                CobDisc(cobColor = WHITE, sizeDp = 28)
                Text(
                    whiteCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = whiteText,
                )
            }

            Text(" — ", modifier = Modifier.padding(horizontal = 8.dp))

            // Disco negro con número superpuesto
            Box(contentAlignment = Alignment.Center) {
                CobDisc(cobColor = CobColor.BLACK, sizeDp = 28)
                Text(
                    blackCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = blackText,
                )
            }

            // Indicador de validez
            if (!isValid) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    TaratiIcons.Warning,
                    localizedString(Res.string.invalid_schema),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
fun PositionControls(
    onCopyBoard: () -> Unit,
    onPasteBoard: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingActionButton(
            onClick = onCopyBoard,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(32.dp),
        ) {
            LocalizedText(
                Res.string.board_notation,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(2.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FloatingActionButton(
            onClick = onPasteBoard,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                TaratiIcons.Download,
                contentDescription = localizedString(Res.string.paste_board_position),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
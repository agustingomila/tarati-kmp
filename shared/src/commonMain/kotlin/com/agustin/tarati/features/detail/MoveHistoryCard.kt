package com.agustin.tarati.features.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import com.agustin.tarati.core.domain.game.play.HistoryEntry
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.move_history
import com.agustin.tarati.shared.generated.resources.total_moves
import com.agustin.tarati.ui.components.movelist.MoveHistoryList
import org.jetbrains.compose.resources.stringResource

@Composable
fun MoveHistoryCard(
    modifier: Modifier = Modifier,
    gameDto: GameDto,
    currentMoveIndex: Int = -1,
    onMoveClick: ((moveIndex: Int) -> Unit)? = null,
) {
    val moves = gameDto.moveHistory

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
        ) {
            Text(
                text = stringResource(Res.string.move_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Text(
                text = stringResource(Res.string.total_moves, moves.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            // Reconstruct StableHistoryList by replaying the move history from
            // the recorded initial position. This mirrors what GameManager does
            // when loading a saved game, and ensures MoveHistoryList receives
            // the same data structure used everywhere else.
            val history = remember(gameDto) {
                var state = runCatching {
                    parseBoardNotation(gameDto.initialBoardPosition)
                }.getOrElse { initialGameState() }

                val entries = gameDto.moveHistory.map { move ->
                    state = state.applyMove(move)
                    HistoryEntry(move, state)
                }
                StableHistoryList(entries)
            }

            MoveHistoryList(
                history = history,
                moveIndex = currentMoveIndex,
                onMoveClick = onMoveClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }
    }
}

@Composable
fun MoveRow(
    moveNumber: Int,
    whiteMove: String,
    blackMove: String,
    isEven: Boolean,
    whiteIsCurrent: Boolean = false,
    blackIsCurrent: Boolean = false,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    if (isEven) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "$moveNumber.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(32.dp),
        )

        // Movimiento blancas — resaltado en primary si es el turno activo
        Text(
            text = whiteMove.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            color = if (whiteIsCurrent) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (whiteIsCurrent) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )

        // Movimiento negras — resaltado en primary si es el turno activo
        Text(
            text = blackMove.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            color = if (blackIsCurrent) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (blackIsCurrent) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}
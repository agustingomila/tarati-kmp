package com.agustin.tarati.ui.components.movelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.core.domain.game.play.TurnGroup
import com.agustin.tarati.core.domain.game.play.groupByTurns
import com.agustin.tarati.core.domain.game.play.moveIndexToGroupIndex

/**
 * Lista de historial de movimientos centralizada, reutilizada por el Sidebar,
 * el FAB de la pantalla de juego y la pantalla de detalle de partida.
 *
 * ## Responsabilidad
 * Solo renderiza la [LazyColumn] con filas de movimientos y gestiona el
 * auto-scroll al movimiento activo. No incluye container (Card / Surface),
 * botones de navegación ni cabeceras — cada caller los agrega según su contexto.
 *
 * ## Navegación por clic
 * [onMoveClick] recibe el índice plano del movimiento seleccionado (base-0,
 * igual al índice en [StableHistoryList]). Pasar `null` desactiva el clic
 * y es el modo apropiado para contextos de solo lectura (detalle de partida).
 *
 * ## Índice plano vs. índice de fila
 * El historial se muestra agrupado en pares de turno (blancas | negras). El
 * índice plano se calcula a partir del [rowIndex] y la columna (0 = blancas,
 * 1 = negras). [TurnGroup] puede tener 1 o 2 movimientos internos (promoción
 * forzada + movimiento posterior), pero visualmente ocupa una sola celda.
 *
 * @param history       Historial estable de movimientos de la partida.
 * @param moveIndex     Índice plano del movimiento actualmente visualizado (-1 = inicio).
 * @param onMoveClick   Callback invocado con el índice plano al hacer clic en una celda.
 *                      `null` = modo solo lectura (sin interacción).
 * @param modifier      Modificador aplicado al [LazyColumn].
 */
@Composable
fun MoveHistoryList(
    modifier: Modifier = Modifier,
    history: StableHistoryList,
    moveIndex: Int,
    onMoveClick: ((moveIndex: Int) -> Unit)? = null,
) {
    val moves = history.getMoves()
    val groups = moves.groupByTurns().chunked(2)
    val currentGroupIndex = moves.moveIndexToGroupIndex(moveIndex)
    val listState = rememberLazyListState()

    // Auto-scroll al movimiento activo. Patrón compartido con los tres callers
    // anteriores: se calcula la fila destino y se anima suavemente hasta ella.
    LaunchedEffect(currentGroupIndex, groups.size) {
        if (groups.isEmpty()) return@LaunchedEffect
        val targetRow = (currentGroupIndex / 2)
            .coerceAtLeast(0)
            .coerceAtMost(groups.lastIndex)
        listState.animateScrollToItem(targetRow)
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        itemsIndexed(groups) { rowIndex, pair ->
            // Calcular el índice plano del primer movimiento de esta fila.
            // Cada fila contiene hasta dos TurnGroups (blancas + negras).
            // Un TurnGroup con promoción forzada ocupa dos índices planos,
            // por eso se recalcula acumulando el tamaño de los grupos anteriores.
            val flatIndexWhite = groups
                .take(rowIndex)
                .flatten()
                .sumOf { it.moves.size }

            val white = pair.firstOrNull()
            val black = pair.getOrNull(1)
            val flatIndexBlack = flatIndexWhite + (white?.moves?.size ?: 0)

            MoveHistoryRow(
                rowNumber = rowIndex + 1,
                white = white,
                black = black,
                currentGroupIndex = currentGroupIndex,
                rowIndex = rowIndex,
                flatIndexWhite = flatIndexWhite,
                flatIndexBlack = flatIndexBlack,
                onMoveClick = onMoveClick,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            if (rowIndex < groups.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                )
            }
        }
    }
}

// ── Row y celda ──────────────────────────────────────────────────────────────

@Composable
private fun MoveHistoryRow(
    rowNumber: Int,
    white: TurnGroup?,
    black: TurnGroup?,
    currentGroupIndex: Int,
    rowIndex: Int,
    flatIndexWhite: Int,
    flatIndexBlack: Int,
    onMoveClick: ((moveIndex: Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$rowNumber.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
        )
        MoveCell(
            group = white,
            isCurrent = currentGroupIndex == rowIndex * 2,
            onClick = if (white != null && onMoveClick != null) {
                { onMoveClick(flatIndexWhite) }
            } else null,
            modifier = Modifier.weight(1f),
        )
        MoveCell(
            group = black,
            isCurrent = currentGroupIndex == rowIndex * 2 + 1,
            onClick = if (black != null && onMoveClick != null) {
                { onMoveClick(flatIndexBlack) }
            } else null,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MoveCell(
    group: TurnGroup?,
    isCurrent: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (group != null) {
            Text(
                text = group.notation,
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrent) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
            )
        }
    }
}
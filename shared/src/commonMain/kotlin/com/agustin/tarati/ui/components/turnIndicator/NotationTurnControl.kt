package com.agustin.tarati.ui.components.turnIndicator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.a_board_game_by_george_spencer_brown
import com.agustin.tarati.shared.generated.resources.copy_position
import com.agustin.tarati.shared.generated.resources.toggle_details
import com.agustin.tarati.ui.components.TooltipIconButton
import com.agustin.tarati.ui.theme.TaratiIcons
import org.jetbrains.compose.resources.stringResource

/**
 * Indicador de turno con panel de notación de posición integrado.
 *
 * En landscape el panel de notación está colapsado por defecto; en portrait está
 * expandido. El chevron permite alternar el estado en cualquier orientación.
 *
 * ## Layout
 * ```
 * Expandido:  [>][ nº  notación  📋 ][●]
 * Colapsado:  [<][●]
 * ```
 *
 * @param positionNotation  Notación de la posición actual, pre-computada por el caller.
 *                          Pasar un String estable evita llamadas a toPositionNotation()
 *                          en cada recomposición.
 * @param showNotation      Si false, el panel de notación y el chevron no se renderizan.
 * @param showTurnIndicator Si false, el círculo indicador de turno no se renderiza.
 */
@Composable
fun NotationTurnControl(
    isLandscape: Boolean,
    positionNotation: String,
    moveIndex: Int,
    currentTurn: CobColor,
    turnState: TurnIndicatorState,
    logoVisible: Boolean,
    indicatorEvents: IndicatorEvents,
    onCopyPositionToClipboard: () -> Unit,
    modifier: Modifier = Modifier,
    showNotation: Boolean = true,
    showTurnIndicator: Boolean = true,
    onCirclePositioned: ((centre: Offset) -> Unit)? = null,
) {
    // Portrait: expanded by default. Landscape: collapsed by default.
    // isLandscape as key resets to the correct default on orientation change.
    var expanded by rememberSaveable(isLandscape) { mutableStateOf(!isLandscape) }

    // Estado del texto superior con la notación de la partida
    var showGameState by rememberSaveable { mutableStateOf(true) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        if (showNotation) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut(),
            ) {
                PositionNotationLabel(
                    positionNotation = positionNotation,
                    moveIndex = moveIndex,
                    showGameState = showGameState,
                    onCopyPositionToClipboard = onCopyPositionToClipboard,
                    onShowGameState = { showGameState = it },
                )
            }

            TooltipIconButton(
                tooltip = stringResource(Res.string.toggle_details),
                onClick = { expanded = !expanded },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = if (expanded) {
                        TaratiIcons.KeyboardArrowRight
                    } else {
                        TaratiIcons.KeyboardArrowLeft
                    },
                    contentDescription = stringResource(Res.string.toggle_details),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        if (showTurnIndicator) {
            // key(Unit) ancla la identidad de composición del TurnIndicator, evitando
            // que cambios en el slot table (AnimatedVisibility antes de él) recreen el
            // InfiniteTransition y congelen la dirección de rotación.
            key(Unit) {
                TurnIndicator(
                    state = turnState,
                    currentTurn = currentTurn,
                    logoVisible = logoVisible,
                    onCirclePositioned = onCirclePositioned,
                    indicatorEvents = indicatorEvents,
                )
            }
        }
    }
}

@Composable
private fun PositionNotationLabel(
    positionNotation: String,
    moveIndex: Int,
    showGameState: Boolean,
    onCopyPositionToClipboard: () -> Unit,
    onShowGameState: (Boolean) -> Unit,
) {
    // Move number: moveIndex is 0-based index of the last recorded entry,
    // so the current move number is moveIndex + 1 (or 0 before any moves).
    val moveNumber = (moveIndex + 1).coerceAtLeast(0)

    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        tonalElevation = 2.dp,
        modifier = Modifier.padding(end = 4.dp),
    ) {
        if (showGameState) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "$moveNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    text = positionNotation,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { onShowGameState(false) },
                )
                Spacer(modifier = Modifier.width(8.dp))
                TooltipIconButton(
                    tooltip = stringResource(Res.string.copy_position),
                    onClick = onCopyPositionToClipboard,
                    modifier = Modifier.size(16.dp),
                ) {
                    Icon(
                        imageVector = TaratiIcons.ContentCopy,
                        contentDescription = stringResource(Res.string.copy_position),
                    )
                }
            }
        } else {
            Text(
                text = localizedString(Res.string.a_board_game_by_george_spencer_brown),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onShowGameState(true) },
            )
        }
    }
}

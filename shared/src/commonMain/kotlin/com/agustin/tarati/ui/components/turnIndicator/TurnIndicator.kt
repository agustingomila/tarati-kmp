package com.agustin.tarati.ui.components.turnIndicator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.ai_thinking
import com.agustin.tarati.shared.generated.resources.human_turn
import com.agustin.tarati.shared.generated.resources.must_promote
import com.agustin.tarati.shared.generated.resources.start_game
import com.agustin.tarati.ui.theme.TaratiLogo
import com.agustin.tarati.ui.theme.getBoardColors

/**
 * Indicador circular de turno con animación de rotación.
 *
 * ## Dirección de giro según el turno
 * El logo gira en sentido **horario** (0° → 360°) cuando es el turno de las
 * blancas, y en sentido **antihorario** (0° → -360°) cuando es el turno de
 * las negras. Esto solo aplica al estado [TurnIndicatorState.AI_THINKING],
 * que es el único que usa [DrawLogo] con rotación continua. Los estados
 * [TurnIndicatorState.HUMAN_TURN] y [TurnIndicatorState.MUST_PROMOTE] muestran
 * [DrawLogoStatic] sin rotación; [TurnIndicatorState.NEUTRAL] muestra [DrawNewGameIcon].
 *
 * @param state              Estado visual actual del indicador.
 * @param currentTurn        Color del jugador que tiene el turno activo.
 * @param size               Tamaño del círculo del indicador.
 * @param logoVisible        Si el logo interior debe mostrarse.
 * @param onCirclePositioned Callback con la posición central en coordenadas de ventana,
 *                           usado para posicionar overlays del tutorial.
 * @param indicatorEvents    Eventos de interacción (solo activos en estado [TurnIndicatorState.NEUTRAL]).
 */
@Composable
fun TurnIndicator(
    modifier: Modifier = Modifier,
    state: TurnIndicatorState,
    currentTurn: CobColor,
    size: Dp = 60.dp,
    logoVisible: Boolean = true,
    onCirclePositioned: ((centre: Offset) -> Unit)? = null,
    indicatorEvents: IndicatorEvents,
) {
    val boardColors = getBoardColors()

    val pieceColor =
        when (currentTurn) {
            CobColor.WHITE -> boardColors.whiteCobColor
            CobColor.BLACK -> boardColors.blackCobColor
        }

    val (indicatorColor, borderColor, isClickable, contentDescription) =
        when (state) {
            TurnIndicatorState.AI_THINKING ->
                Quadruple(
                    pieceColor,
                    null,
                    false,
                    localizedString(Res.string.ai_thinking),
                )

            TurnIndicatorState.HUMAN_TURN ->
                Quadruple(
                    pieceColor,
                    null,
                    false,
                    localizedString(Res.string.human_turn),
                )

            TurnIndicatorState.MUST_PROMOTE ->
                Quadruple(
                    pieceColor,
                    boardColors.highlightVertexUpgrade1Color,
                    false,
                    localizedString(Res.string.must_promote),
                )

            TurnIndicatorState.NEUTRAL ->
                Quadruple(
                    boardColors.neutralColor,
                    null,
                    true,
                    localizedString(Res.string.start_game),
                )
        }

    val infiniteTransition = rememberInfiniteTransition(label = "TurnIndicatorTransition")

    // Sentido de giro: horario para blancas (→ 360°), antihorario para negras (→ -360°).
    // Solo tiene efecto visual en AI_THINKING, que es el único estado que usa DrawLogo.
    val rotationTarget = if (currentTurn == CobColor.WHITE) 360f else -360f
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = rotationTarget,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
            ),
        label = "TurnIndicatorRotation",
    )

    val borderPulse by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600, easing = LinearEasing),
            ),
        label = "TurnIndicatorBorderPulse",
    )

    val borderModifier =
        if (borderColor != null) {
            Modifier.border(borderPulse.dp, borderColor, CircleShape)
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .padding(8.dp)
                .size(size)
                .clip(CircleShape)
                .then(borderModifier)
                .background(indicatorColor)
                .clickable(isClickable) {
                    if (isClickable) indicatorEvents.onTouch()
                }
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInWindow()
                    val cx = pos.x + coords.size.width / 2f
                    val cy = pos.y + coords.size.height / 2f
                    onCirclePositioned?.invoke(Offset(cx, cy))
                },
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            TurnIndicatorState.AI_THINKING ->
                if (logoVisible) DrawLogo(
                    size = size,
                    rotation = rotation,
                    contentDescription = contentDescription,
                )

            TurnIndicatorState.HUMAN_TURN ->
                if (logoVisible) DrawLogoStatic(
                    size = size,
                    contentDescription = contentDescription,
                )

            TurnIndicatorState.MUST_PROMOTE ->
                if (logoVisible) DrawLogoStatic(
                    size = size,
                    contentDescription = contentDescription,
                )

            TurnIndicatorState.NEUTRAL ->
                if (logoVisible) DrawNewGameIcon(
                    size = size,
                    contentDescription = contentDescription,
                )
        }
    }
}

/** Convenience holder for four values, used to unpack indicator display properties. */
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun DrawLogo(
    size: Dp = 60.dp,
    rotation: Float,
    contentDescription: String,
) {
    TaratiLogo(
        size = size * 0.8f,
        rotationDeg = rotation,
    )
}

@Composable
fun DrawLogoStatic(
    size: Dp = 60.dp,
    contentDescription: String,
) {
    TaratiLogo(size = size * 0.8f)
}

@Composable
fun DrawNewGameIcon(
    size: Dp = 60.dp,
    contentDescription: String,
) {
    TaratiLogo(size = size * 0.8f)
}


/**
 * Small pulsing badge shown near the TurnIndicator when the current human player
 * can claim a draw by the 50-move rule (§7.2.2). Tapping it claims the draw.
 *
 * Visual language:
 * - Amber/gold fill — neutral "claim available" colour, distinct from piece colours.
 * - Pulsing border — same animation rhythm as MUST_PROMOTE to keep the UI vocabulary consistent.
 * - "50" label — immediately recognisable to chess-literate players; unambiguous to others.
 */
@Composable
fun FiftyMoveClaimBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val transition = rememberInfiniteTransition(label = "FiftyMovePulse")
    val borderWidth by transition.animateFloat(
        initialValue = 2f,
        targetValue = 5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "FiftyMoveBorderPulse",
    )

    val amber = Color(0xFFFFA000)

    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(amber)
                .border(borderWidth.dp, amber.copy(alpha = 0.5f), CircleShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "50",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )
    }
}
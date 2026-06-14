package com.agustin.tarati.ui.components.turnIndicator.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.ai_thinking
import com.agustin.tarati.shared.generated.resources.human_turn
import com.agustin.tarati.shared.generated.resources.must_promote
import com.agustin.tarati.shared.generated.resources.new_game
import com.agustin.tarati.ui.components.turnIndicator.IndicatorEvents
import com.agustin.tarati.ui.components.turnIndicator.TurnIndicator
import com.agustin.tarati.ui.components.turnIndicator.TurnIndicatorState
import com.agustin.tarati.ui.theme.TaratiTheme

fun createPreviewIndicatorEvents(): IndicatorEvents =
    object : IndicatorEvents {
        override fun onTouch() = Unit
    }

@Preview(showBackground = true)
@Composable
fun TurnIndicatorPreview_AllStates() {
    TaratiTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LocalizedText(Res.string.ai_thinking, style = MaterialTheme.typography.titleMedium)

            TurnIndicator(
                state = TurnIndicatorState.AI_THINKING,
                currentTurn = CobColor.BLACK,
                size = 80.dp,
                indicatorEvents = createPreviewIndicatorEvents(),
            )

            LocalizedText(Res.string.human_turn, style = MaterialTheme.typography.titleMedium)

            TurnIndicator(
                state = TurnIndicatorState.HUMAN_TURN,
                currentTurn = CobColor.WHITE,
                size = 80.dp,
                indicatorEvents = createPreviewIndicatorEvents(),
            )

            LocalizedText(Res.string.must_promote, style = MaterialTheme.typography.titleMedium)

            TurnIndicator(
                state = TurnIndicatorState.MUST_PROMOTE,
                currentTurn = CobColor.WHITE,
                size = 80.dp,
                indicatorEvents = createPreviewIndicatorEvents(),
            )

            LocalizedText(Res.string.new_game, style = MaterialTheme.typography.titleMedium)

            TurnIndicator(
                state = TurnIndicatorState.NEUTRAL,
                currentTurn = CobColor.BLACK,
                size = 80.dp,
                indicatorEvents = createPreviewIndicatorEvents(),
            )
        }
    }
}
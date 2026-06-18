package com.agustin.tarati.core.domain.game.manager

import androidx.compose.runtime.Stable
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class GameManagerState(
    val gameState: GameState,
    val gameStatus: GameStatus,
    val history: StableHistoryList,
    val moveIndex: Int,
) {
    companion object {
        fun createInitialUiState(): GameManagerState =
            GameManagerState(
                gameState = initialGameState(),
                gameStatus = GameStatus.NO_PLAYING,
                history = StableHistoryList(listOf()),
                moveIndex = -1,
            )
    }
}

package com.agustin.tarati.services.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.utils.logging.LoggingFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class AIViewModel(
    private val aiEngine: IAIEngine,
) : ViewModel(),
    IAIService {

    private val logger = LoggingFactory.getLogger("aiViewModel")

    private val _isAIThinking = MutableStateFlow(false)
    override val isAIThinking: StateFlow<Boolean> = _isAIThinking.asStateFlow()

    // extraBufferCapacity = 1: retains a move emitted during a configuration
    // change until the new composition starts collecting.
    private val _pendingAIMove = MutableSharedFlow<Move>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val pendingAIMove: SharedFlow<Move> = _pendingAIMove.asSharedFlow()

    /**
     * Lanza el cómputo de la IA en [androidx.lifecycle.viewModelScope], que sobrevive rotaciones
     * de pantalla. El resultado se emite a [pendingAIMove] y es recogido por
     * el Composable via [androidx.compose.runtime.LaunchedEffect].
     *
     * Ignorado si el motor ya está pensando, evitando cómputos duplicados cuando
     * [GameEffects] re-dispara tras la rotación
     * con los mismos [AiThinkingDependencies].
     */
    override val positionHistory: Map<String, Int>
        get() = aiEngine.positionHistory

    override fun requestAIMove(gameState: GameState) {
        if (_isAIThinking.value) return

        viewModelScope.launch {
            _isAIThinking.update { true }
            logger.debug("AI starting to think...")

            try {
                val result = withContext(Dispatchers.Default) {
                    aiEngine.getNextMove(gameState = gameState)
                }

                logger.debug("AI calculated move: ${result.move}")

                // withContext(Default) corre código bloqueante sin puntos de
                // suspensión; isActive verifica cancelación al retornar.
                if (isActive) {
                    result.move?.let { _pendingAIMove.emit(it) }
                }
            } catch (e: CancellationException) {
                throw e // preservar structured concurrency
            } catch (t: Throwable) {
                logger.error(t.message.orEmpty(), t)
            } finally {
                _isAIThinking.update { false }
            }
        }
    }
}
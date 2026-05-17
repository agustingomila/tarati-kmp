package com.agustin.tarati.features.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.repositories.SavedGame
import com.agustin.tarati.core.domain.repository.GameRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GamesLibraryViewModel(
    private val gameRepository: GameRepository,
) : ViewModel(),
    IGamesLibraryViewModel {

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Lista reactiva de partidas filtrada por [searchQuery], expuesta como
     * [StateFlow] para que `collectAsState()` lea el valor actual de forma
     * síncrona en la primera composición.
     *
     * ## Pipeline
     * 1. [debounce] absorbe las pulsaciones intermedias: solo dispara una
     *    nueva consulta si el usuario deja de escribir durante 300 ms.
     * 2. [flatMapLatest] cancela el flow anterior ante cada nueva query,
     *    evitando resultados desactualizados.
     * 3. Cuando la query está en blanco se usa [GameRepository.savedGames]
     *    directamente para evitar `LIKE '%%'` innecesario en Room.
     * 4. [stateIn] convierte el flow frío en un [StateFlow] con
     *    `initialValue = emptyList()`. [SharingStarted.WhileSubscribed] libera
     *    la suscripción a Room 5 segundos después de que la UI desaparece,
     *    preservando el valor en rotaciones de pantalla.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val savedGames: StateFlow<List<SavedGame>> =
        _searchQuery
            .debounce(300L.milliseconds)
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    gameRepository.savedGames
                } else {
                    gameRepository.searchGames(query.trim())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    override fun setSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    override fun deleteGame(gameId: String) {
        viewModelScope.launch {
            gameRepository.deleteGame(gameId)
        }
    }

    override fun loadGame(gameId: String): Flow<MatchDto?> =
        flow {
            val gameData = gameRepository.loadGame(gameId)
            emit(gameData)
        }

    override fun saveCurrentGame(match: MatchDto) {
        viewModelScope.launch {
            gameRepository.saveGame(match)
        }
    }
}
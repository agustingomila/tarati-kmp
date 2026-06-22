package com.agustin.tarati.features.library

import androidx.compose.runtime.Stable
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.repositories.SavedGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface IGamesLibraryViewModel {
    /**
     * Lista de partidas actualmente visible, ya filtrada por [searchQuery].
     *
     * Se declara como [kotlinx.coroutines.flow.StateFlow] (no como [kotlinx.coroutines.flow.Flow]) para que `collectAsState()`
     * use la sobrecarga que lee [kotlinx.coroutines.flow.StateFlow.value] de forma síncrona en la
     * primera composición, sin depender de una corutina. Esto garantiza que
     * los previews de Compose muestren datos desde el primer frame, y que la
     * UI de producción no flashee el estado vacío al arrancar.
     */
    val savedGames: StateFlow<List<SavedGame>>

    /**
     * Texto de búsqueda activo. La UI lo colecta para controlar el campo
     * de búsqueda y para distinguir "sin resultados" de "lista vacía".
     */
    val searchQuery: StateFlow<String>

    fun setSearchQuery(query: String)

    fun deleteGame(gameId: String)

    fun loadGame(gameId: String): Flow<MatchDto?>

    fun saveCurrentGame(match: MatchDto)
}
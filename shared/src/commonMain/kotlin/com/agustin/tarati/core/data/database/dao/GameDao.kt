package com.agustin.tarati.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agustin.tarati.core.data.database.entities.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY createdAt DESC")
    fun getSavedGames(): Flow<List<GameEntity>>

    /**
     * Búsqueda libre en las columnas más relevantes de la partida.
     *
     * El operador `||` de SQLite concatena los literales `'%'` con el
     * parámetro, produciendo el patrón `%query%` sin exponer la sintaxis
     * al caller. Room no admite parámetros de tipo wildcard en LIKE, así
     * que este es el patrón canónico recomendado.
     *
     * Columnas incluidas: jugadores, resultado, fecha, evento y lugar —
     * las más buscables desde la perspectiva del usuario.
     */
    @Query(
        """
        SELECT * FROM games
        WHERE white       LIKE '%' || :query || '%'
           OR black       LIKE '%' || :query || '%'
           OR result      LIKE '%' || :query || '%'
           OR date        LIKE '%' || :query || '%'
           OR event       LIKE '%' || :query || '%'
           OR site        LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
        """,
    )
    fun searchGames(query: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: String): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: String)
}
package com.agustin.tarati.core.data.repositories

import com.agustin.tarati.core.data.database.dao.GameDao
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.parseMatchDto
import com.agustin.tarati.core.data.database.entities.GameEntity
import com.agustin.tarati.core.data.database.entities.GameEntity.Companion.parseGameEntity
import com.agustin.tarati.core.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación de [GameRepository] usando Room Database.
 *
 * **Plataformas soportadas**:
 * - Android (SQLite nativo)
 * - Desktop/JVM (SQLite via JDBC)
 * - iOS (SQLite nativo)
 *
 * **Funcionamiento**:
 * - Usa [GameDao] para operaciones CRUD
 * - Convierte entre [MatchDto] ↔ [GameEntity]
 * - Devuelve Flows reactivos para la UI
 *
 * **Instanciación**:
 * ```kotlin
 * // Android
 * val db = Room.databaseBuilder(context, TaratiDatabase::class.java, "tarati.db").build()
 * val repository = RoomGameRepository(db.gameDao())
 *
 * // Desktop
 * val db = Room.databaseBuilder<TaratiDatabase>(
 *     name = getDatabasePath()
 * ).setDriver(BundledSQLiteDriver()).build()
 * val repository = RoomGameRepository(db.gameDao())
 * ```
 */
class RoomGameRepository(
    private val gameDao: GameDao,
) : GameRepository {

    override val savedGames: Flow<List<SavedGame>>
        get() = gameDao.getSavedGames().map { entities ->
            entities.mapNotNull { entity ->
                parseMatchDto(entity)?.toSavedGame()
            }
        }

    override fun searchGames(query: String): Flow<List<SavedGame>> =
        gameDao.searchGames(query).map { entities ->
            entities.mapNotNull { entity ->
                parseMatchDto(entity)?.toSavedGame()
            }
        }

    override suspend fun saveGame(dto: MatchDto) {
        val entity = parseGameEntity(dto)
        gameDao.saveGame(entity)
    }

    override suspend fun loadGame(gameId: String): MatchDto? {
        val entity = gameDao.getGameById(gameId) ?: return null
        return parseMatchDto(entity)
    }

    override suspend fun deleteGame(gameId: String) {
        gameDao.deleteGameById(gameId)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun MatchDto.toSavedGame() = SavedGame(
        id = id.orEmpty(),
        whitePlayer = header.white,
        blackPlayer = header.black,
        result = header.result,
        date = header.date,
        moveCount = game.moveHistory.size,
    )
}
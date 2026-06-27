package com.agustin.tarati.services.achievements

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.features.online.auth.AuthRepository
import com.agustin.tarati.network.models.ServerAchievementDto
import kotlinx.coroutines.flow.update

private val PALETTE_ACHIEVEMENTS = setOf(
    AchievementId.HALLOWEEN_THEME,
    AchievementId.CHRISTMAS_THEME,
    AchievementId.THE_FIRST_LIGHT,
    AchievementId.THE_DARK_SIDE,
)

/**
 * Implementación de [IAchievementsManager] para Desktop y Web.
 *
 * Persiste los logros en el servidor de Tarati cuando hay sesión activa.
 * Si el usuario no está autenticado o la red no está disponible, los unlocks
 * y progresos se encolan en memoria ([pendingUnlocks] / [pendingProgress]) y
 * se reintenta antes de procesar el siguiente evento.
 *
 * ## Contadores entre sesiones
 * [syncFromServer] carga el progreso actual del servidor al iniciar y restaura
 * los contadores in-memory, garantizando continuidad incluso tras reiniciar la app.
 * Debe llamarse una vez al inicio de sesión desde el ViewModel o composable raíz.
 */
class ServerAchievementsManager(
    private val syncService: AchievementSyncService,
    private val authRepository: AuthRepository,
    aiEngine: IAIEngine,
) : BaseAchievementsManager(aiEngine) {

    private val pendingUnlocks = mutableSetOf<AchievementId>()
    private val pendingProgress = mutableMapOf<AchievementId, Int>()

    // ── Sincronización inicial ────────────────────────────────────────────────

    /**
     * Carga el progreso actual del servidor y restaura los contadores in-memory.
     * Actualiza [unlockedPaletteAchievements] con los logros de paleta ya desbloqueados.
     * No-op si el usuario no tiene sesión activa.
     */
    override suspend fun syncFromServer() {
        val token = authRepository.getToken() ?: return
        syncService.getAll(token).onSuccess { dtos -> dtos.forEach(::applyServerProgress) }
    }

    private fun applyServerProgress(dto: ServerAchievementDto) {
        val id = AchievementId.fromId(dto.achievementId) ?: return
        when (id) {
            AchievementId.THE_FLIPPER -> totalCaptures = maxOf(totalCaptures, dto.currentSteps)
            AchievementId.ROK_MASTER -> totalPromotions = maxOf(totalPromotions, dto.currentSteps)
            AchievementId.UNSTOPPABLE,
            AchievementId.GRANDMASTER -> totalWins = maxOf(totalWins, dto.currentSteps)

            AchievementId.PLAY_10_GAMES -> totalGames = maxOf(totalGames, dto.currentSteps)
            else -> {}
        }
        if (dto.unlockedAt != null && id in PALETTE_ACHIEVEMENTS) {
            unlockedPalettes.update { it + id }
        }
    }

    // ── Entrega de logros ─────────────────────────────────────────────────────

    override suspend fun onUnlock(achievementId: AchievementId) {
        flushPending()
        val token = authRepository.getToken()
        if (token != null) {
            if (!syncService.unlock(token, achievementId)) pendingUnlocks.add(achievementId)
        } else {
            pendingUnlocks.add(achievementId)
        }
        if (achievementId in PALETTE_ACHIEVEMENTS) {
            unlockedPalettes.update { it + achievementId }
        }
    }

    override suspend fun onProgress(achievementId: AchievementId, steps: Int, maxSteps: Int) {
        val clamped = steps.coerceAtMost(maxSteps)
        flushPending()
        val token = authRepository.getToken()
        if (token != null) {
            if (!syncService.progress(token, achievementId, clamped)) pendingProgress[achievementId] = clamped
        } else {
            pendingProgress[achievementId] = clamped
        }
    }

    // ── Cola de pendientes ────────────────────────────────────────────────────

    private suspend fun flushPending() {
        if (pendingUnlocks.isEmpty() && pendingProgress.isEmpty()) return
        val token = authRepository.getToken() ?: return

        pendingUnlocks.toSet().forEach { id ->
            if (syncService.unlock(token, id)) pendingUnlocks.remove(id)
        }
        pendingProgress.toMap().forEach { (id, steps) ->
            if (syncService.progress(token, id, steps)) pendingProgress.remove(id)
        }
    }
}

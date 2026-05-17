package com.agustin.tarati.features.seasonal


import com.agustin.tarati.R
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.services.achievements.AchievementsRepository
import com.agustin.tarati.services.achievements.IAchievementsReporter
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.PaletteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Gestiona el ciclo de vida completo de los eventos especiales:
 * activación por fecha, verificación de condiciones, unlock de paleta y
 * emisión del estado que la UI consume para mostrar el ícono de regalo y
 * los dialogs.
 *
 * ## Separación de responsabilidades
 * Esta clase **no sabe nada de la lógica de juego**. Recibe el resultado
 * de una partida vía [onGameResult] y decide si corresponde desbloquear algún
 * evento. La verificación de que la partida fue legítima (Human vs. AI, sin
 * cambio de bando, etc.) ya fue realizada por el guard `achievementsEnabled`
 * en [GameEvents] antes de llamar a este manager.
 *
 * ## Flujo de unlock
 * ```
 * onGameResult(matchState, playerSide)
 *   → comprobar si hay eventos activos hoy
 *   → para cada evento activo no desbloqueado:
 *       → comprobar condición (WinAsWhite / WinAsBlack)
 *       → si se cumple:
 *           → persistir unlock en AchievementsRepository
 *           → aplicar paleta inmediatamente (PaletteManager + SettingsRepository)
 *           → emitir pendingCelebration → la UI muestra el dialog de celebración
 * ```
 *
 * ## Por qué single (no ViewModel)
 * [GameEvents] recibe este manager vía inyección de constructor,
 * igual que [AchievementsManager].
 * Uses a [CoroutineScope] (injectable, default: `Dispatchers.IO + SupervisorJob()`) for
 * the init refresh and any internal async DataStore work.
 */
class SpecialEventManager(
    private val specialEventRepository: SpecialEventRepository,
    private val achievementsRepository: AchievementsRepository,
    private val settingsRepository: SettingsRepository,
    private val reporter: IAchievementsReporter,
    /**
     * Coroutine scope for async DataStore operations and the init refresh.
     * Defaults to a long-lived IO scope for production.
     * Tests inject [kotlinx.coroutines.test.UnconfinedTestDispatcher] scope for
     * immediate execution without needing advanceUntilIdle().
     */
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    /** Event list — injectable for testing (default: production SPECIAL_EVENTS). */
    private val specialEvents: List<SpecialEvent> = SPECIAL_EVENTS,
    /**
     * Date provider — injectable for testing.
     * Returns (month 1-12, day) for today.
     * Default: Calendar.getInstance().
     */
    private val dateProvider: () -> Pair<Int, Int> = {
        val cal = java.util.Calendar.getInstance()
        (cal.get(java.util.Calendar.MONTH) + 1) to cal.get(java.util.Calendar.DAY_OF_MONTH)
    },
    /**
     * Palette applier — injectable to avoid PaletteManager singleton in tests.
     * Default: PaletteManager.setPalette().
     */
    private val paletteApplier: (BoardPalette) -> Unit = { PaletteManager.setPalette(it) },
) : ISpecialEventManager {

    // ── Estado expuesto a la UI ───────────────────────────────────────────────

    /**
     * Lista de eventos activos hoy que aún no han sido desbloqueados.
     * La UI la observa para decidir cuántos íconos de regalo mostrar.
     *
     * Se recalcula en [refreshActiveEvents], que se llama en el constructor
     * y cada vez que la partida termina. No es un Flow reactivo porque la fecha
     * no cambia durante una sesión (la app requiere reinicio al cruzar medianoche).
     */
    private val _activeEvents = MutableStateFlow<List<SpecialEvent>>(emptyList())
    override val activeEvents: StateFlow<List<SpecialEvent>> = _activeEvents.asStateFlow()

    /**
     * Evento recién desbloqueado que espera que la UI muestre el dialog de celebración.
     * La UI llama a [dismissCelebration] cuando el usuario cierra el dialog.
     */
    private val _pendingCelebration = MutableStateFlow<SpecialEvent?>(null)
    override val pendingCelebration: StateFlow<SpecialEvent?> = _pendingCelebration.asStateFlow()

    init {
        scope.launch { refreshActiveEvents() }
    }

    // ── API de UI ─────────────────────────────────────────────────────────────

    /**
     * Recalcula los eventos activos con la fecha actual. Llamado desde
     * [SpecialEventOverlay] vía LaunchedEffect al entrar en composición, para
     * que el ícono aparezca sin reiniciar si la fecha cambió en la misma sesión.
     */
    override suspend fun refreshIfNeeded() = refreshActiveEvents()

    /**
     * Returns `true` if the user has already tapped the gift icon for [event] this year.
     * When `true`, the pulsing animation is suppressed.
     *
     * ## Threading
     * `suspend` — delegates to [SpecialEventRepository.isGiftSeen].
     * In Compose, call from `withContext(Dispatchers.IO)` inside a LaunchedEffect:
     * ```kotlin
     * LaunchedEffect(event.id) {
     *     isSeen = withContext(Dispatchers.IO) { manager.isGiftSeen(event) }
     * }
     * ```
     */
    override suspend fun isGiftSeen(event: SpecialEvent): Boolean =
        specialEventRepository.isGiftSeen(event.id)

    /**
     * Marks the gift icon for [event] as seen. The pulsing animation stops and
     * the icon remains static to attract attention without being intrusive.
     * Called when the user taps the icon for the first time.
     *
     * ## Threading
     * `suspend` — delegates to [SpecialEventRepository.markGiftSeen].
     * In Compose, use `rememberCoroutineScope().launch(Dispatchers.IO)` from an onClick lambda.
     */
    override suspend fun markGiftSeen(event: SpecialEvent) {
        specialEventRepository.markGiftSeen(event.id)
    }

    /**
     * Cierra el dialog de celebración. Llamado por la UI cuando el usuario
     * toca el botón de confirmación.
     */
    override fun dismissCelebration() {
        _pendingCelebration.value = null
    }

    // ── Hook de resultado de partida ──────────────────────────────────────────

    /**
     * Punto de entrada desde [GameEvents] al
     * finalizar una partida.
     *
     * Este método es llamado **únicamente** cuando `achievementsEnabled` es `true`
     * en [GameEvents], garantizando que la partida
     * fue Human vs. AI, sin cambios de bando y sin historial alterado. Las verificaciones
     * aquí son solo las propias del evento (condición de victoria y fecha).
     *
     * @param matchState Estado final de la partida (contiene el ganador).
     * @param playerSide Color del bando humano.
     */
    override suspend fun onGameResult(matchState: MatchState, playerSide: CobColor) {
        refreshActiveEvents()

        val winner = matchState.winner ?: return
        if (winner != playerSide) return

        val events = _activeEvents.value
        if (events.isEmpty()) return

        for (event in events) {
            if (isEventAlreadyUnlocked(event)) continue
            if (!conditionMet(event.condition, playerSide)) continue

            unlock(event)
            break
        }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    /**
     * Recalcula [_activeEvents] filtrando [SPECIAL_EVENTS] por:
     * 1. El día actual está en [SpecialEvent.activeDays] (mes/día, sin año).
     * 2. La paleta correspondiente aún no está desbloqueada.
     */
    private suspend fun refreshActiveEvents() {
        val today = today()
        _activeEvents.value = specialEvents.filter { event ->
            event.activeDays.any { (month, day) -> month == today.first && day == today.second }
                    && !isEventAlreadyUnlocked(event)
        }
    }

    /**
     * Verifica si la paleta asociada a [event] ya fue desbloqueada permanentemente.
     * Reads DataStore flows via `Flow.first()`. Must be called from a suspend context.
     */
    private suspend fun isEventAlreadyUnlocked(event: SpecialEvent): Boolean {
        return when (event.reward.paletteName) {
            "Aurora" -> achievementsRepository.auroraUnlocked.first()
            "Ember" -> achievementsRepository.emberUnlocked.first()
            else -> false
        }
    }

    /**
     * Verifica si la condición específica del evento se cumple dados los parámetros
     * de la partida que acaba de terminar.
     */
    private fun conditionMet(
        condition: SpecialEventCondition,
        playerSide: CobColor,
    ): Boolean = when (condition) {
        SpecialEventCondition.WinAsWhite -> playerSide == CobColor.WHITE
        SpecialEventCondition.WinAsBlack -> playerSide == CobColor.BLACK
    }

    /**
     * Ejecuta el unlock de [event]:
     * 1. Persiste el unlock en [AchievementsRepository] (permanente, sobrevive reinstalaciones).
     * 2. Aplica la paleta inmediatamente via [PaletteManager] (efecto visual en tiempo real).
     * 3. Persiste la paleta como preferencia activa en [SettingsRepository].
     * 4. Refresca [_activeEvents] para que el ícono de regalo desaparezca.
     * 5. Emite [_pendingCelebration] para que la UI muestre el dialog de celebración.
     */
    private suspend fun unlock(event: SpecialEvent) {
        // 1. Persistencia permanente del unlock (local DataStore)
        when (event.reward.paletteName) {
            "Aurora" -> achievementsRepository.unlockAurora()
            "Ember" -> achievementsRepository.unlockEmber()
        }

        // 1b. Report to Play Games
        val achievementResId = when (event.reward.paletteName) {
            "Aurora" -> R.string.achievement_the_first_light
            "Ember" -> R.string.achievement_the_dark_side
            else -> null
        }
        achievementResId?.let { reporter.unlock(it) }

        // 2. Aplicar paleta inmediatamente
        val palette = when (event.reward.paletteName) {
            "Aurora" -> AuroraPalette
            "Ember" -> EmberPalette
            else -> null
        }
        palette?.let { paletteApplier(it) }

        // 3. Persistir como preferencia activa
        settingsRepository.setPalette(event.reward.paletteName)

        // 4. Refrescar lista de eventos activos (el ícono desaparece)
        refreshActiveEvents()

        // 5. Disparar dialog de celebración
        _pendingCelebration.value = event
    }

    /** Devuelve (mes, día) del día actual delegando en [dateProvider]. */
    private fun today(): Pair<Int, Int> = dateProvider()
}
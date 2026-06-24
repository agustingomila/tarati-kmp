package com.agustin.tarati.ui.theme

import com.agustin.tarati.ui.theme.SeasonalThemeManager.isPaletteAvailable
import com.agustin.tarati.ui.theme.SeasonalThemeManager.todayKey
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Centraliza toda la lógica de temas estacionales: detección de fecha,
 * disponibilidad de paletas y clave de idempotencia para la auto-aplicación.
 *
 * ## Por qué object y no class
 * La lógica es completamente stateless y multiplataforma —
 * usa [kotlinx.datetime] en lugar de java.util.Calendar. Un `object` expresa
 * explícitamente esta ausencia de estado y hace que los tests sean directos
 * sin necesidad de instanciar ni mockear.
 *
 * ## Clave de idempotencia "MM-dd"
 * El auto-aplicado del tema ocurre exactamente una vez por día. La clave
 * [todayKey] usa "MM-dd" (sin año) para que el comportamiento se repita
 * cada año sin lógica adicional. Se almacena en DataStore y se compara
 * en [SettingsViewModel.applySeasonalThemeIfNeeded].
 *
 * ## Separación entre acceso temporal y desbloqueo permanente
 * [isPaletteAvailable] implementa dos vías de acceso ortogonales:
 * la fecha activa el acceso temporal para todos los jugadores, mientras
 * que la victoria en Champion ese día otorga el desbloqueo permanente
 * persistido en [AchievementsRepository].
 * Mantener estas dos condiciones en un único lugar evita que la lógica de disponibilidad
 * quede dispersa entre SettingsViewModel, SettingsScreen y AchievementsManager.
 *
 * ## Multiplataforma
 * Usa `kotlinx.datetime` que funciona en todas las plataformas KMP:
 * - Android, iOS, Desktop, Web
 * - Zona horaria local del dispositivo
 */
object SeasonalThemeManager {

    const val HALLOWEEN_PALETTE: String = "Halloween"
    const val CHRISTMAS_PALETTE: String = "Christmas"

    // Paletas de eventos especiales — solo disponibles al desbloquearlas,
    // sin acceso temporal por fecha (a diferencia de Halloween/Christmas).
    const val AURORA_PALETTE: String = "Aurora"
    const val EMBER_PALETTE: String = "Ember"

    private const val HALLOWEEN_MONTH = 10  // October (1-indexed)
    private const val HALLOWEEN_DAY = 31
    private const val CHRISTMAS_MONTH = 12  // December (1-indexed)
    private const val CHRISTMAS_DAY = 25

    /**
     * Obtiene la fecha actual en la zona horaria local del dispositivo.
     */
    private fun getToday() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    /** Returns "MM-dd" string for today, used as idempotency key. */
    fun todayKey(): String {
        val today = getToday()
        val month = today.month.number  // 1-indexed (1-12)
        val day = today.day
        return "${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    fun isHalloweenDay(): Boolean {
        val today = getToday()
        return today.month.number == HALLOWEEN_MONTH &&
                today.day == HALLOWEEN_DAY
    }

    fun isChristmasDay(): Boolean {
        val today = getToday()
        return today.month.number == CHRISTMAS_MONTH &&
                today.day == CHRISTMAS_DAY
    }

    /**
     * Returns the seasonal palette name for today, or null if today is not a seasonal day.
     */
    fun getSeasonalPaletteForToday(): String? = when {
        isHalloweenDay() -> HALLOWEEN_PALETTE
        isChristmasDay() -> CHRISTMAS_PALETTE
        else -> null
    }

    /** Returns true if this palette name is a seasonal one (auto-applied by date). */
    fun isSeasonalPalette(name: String): Boolean =
        name == HALLOWEEN_PALETTE || name == CHRISTMAS_PALETTE

    /**
     * Determina si una paleta debe aparecer en el selector de Settings.
     *
     * ## Lógica por tipo de paleta
     * - **Estacionales** (Halloween, Christmas): disponibles el día del evento O
     *   si fueron desbloqueadas permanentemente ganando en Champion ese día.
     * - **Eventos especiales** (Aurora, Ember): disponibles **únicamente** si fueron
     *   desbloqueadas. No hay acceso temporal por fecha — deben ganarse.
     * - **Resto**: siempre disponibles.
     */
    fun isPaletteAvailable(
        paletteName: String,
        halloweenUnlocked: Boolean,
        christmasUnlocked: Boolean,
        auroraUnlocked: Boolean,
        emberUnlocked: Boolean,
    ): Boolean = when (paletteName) {
        HALLOWEEN_PALETTE -> isHalloweenDay() || halloweenUnlocked
        CHRISTMAS_PALETTE -> isChristmasDay() || christmasUnlocked
        AURORA_PALETTE -> auroraUnlocked
        EMBER_PALETTE -> emberUnlocked
        else -> true
    }
}
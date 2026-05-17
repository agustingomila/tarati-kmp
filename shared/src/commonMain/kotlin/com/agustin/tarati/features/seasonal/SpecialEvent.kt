package com.agustin.tarati.features.seasonal

import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.special_event_dark_side_desc
import com.agustin.tarati.shared.generated.resources.special_event_dark_side_title
import com.agustin.tarati.shared.generated.resources.special_event_first_light_desc
import com.agustin.tarati.shared.generated.resources.special_event_first_light_title
import com.agustin.tarati.shared.generated.resources.special_event_palette_aurora
import com.agustin.tarati.shared.generated.resources.special_event_palette_ember
import org.jetbrains.compose.resources.StringResource

// ── Modelos ───────────────────────────────────────────────────────────────────

/**
 * Define la condición que debe cumplirse para desbloquear el evento.
 *
 * El guard `achievementsEnabled` de [GameEvents]
 * ya garantiza que la partida es Human vs. AI, sin cambios de bando y sin historial
 * alterado por undo.
 * Las condiciones aquí expresan únicamente la regla específica del evento.
 */
sealed class SpecialEventCondition {
    /** Ganar una partida jugando con las Blancas. */
    object WinAsWhite : SpecialEventCondition()

    /** Ganar una partida jugando con las Negras. */
    object WinAsBlack : SpecialEventCondition()
}

/**
 * Recompensa que se otorga al cumplir el evento.
 *
 * @param paletteName Nombre canónico de la paleta (coincide con
 *                    [BoardPalette.name]).
 *                    Se usa como key de persistencia en
 *                    [AchievementsRepository].
 * @param displayNameRes String resource para mostrar el nombre en el dialog de celebración.
 */
data class SpecialEventReward(
    val paletteName: String,
    val displayNameRes: StringResource,
)

/**
 * Evento especial — logro desbloqueeable en fechas concretas.
 *
 * @param id          Identificador único estable. Se usa como parte de la clave de
 *                    persistencia, nunca debe cambiar entre versiones.
 * @param titleRes    Título del evento que aparece en el gift bubble (sin spoiler de la paleta).
 * @param descRes     Descripción de cómo alcanzarlo, también sin revelar la paleta.
 * @param condition   Condición de victoria a verificar.
 * @param reward      Paleta que se desbloquea al cumplir la condición.
 * @param activeDays  Lista de pares (mes, día) en los que el ícono aparece.
 *                    Mes en formato 1-12. El año no importa — recurrencia anual.
 */
data class SpecialEvent(
    val id: String,
    val titleRes: StringResource,
    val descRes: StringResource,
    val condition: SpecialEventCondition,
    val reward: SpecialEventReward,
    val activeDays: List<Pair<Int, Int>>,
)

// ── Definición de eventos ─────────────────────────────────────────────────────

/**
 * Catálogo completo de eventos especiales del juego.
 *
 * ## Calendario (recurrente cada año)
 * - 4/4 → "The First Light"  (Aurora)
 * - 5/4 → "The Dark Side"  (Ember)
 * - 6/4 → Última oportunidad para ambos
 *
 * ## Asignación de días
 * - `FIRST_LIGHT` está activo en 4/4 y 6/4.
 * - `DARK_SIDE`  está activo en 5/4 y 6/4.
 * El día 6/4 es el "último chance": ambos eventos están disponibles para quienes
 * no los hayan desbloqueado aún.
 */
val SPECIAL_EVENTS: List<SpecialEvent> = listOf(

    SpecialEvent(
        id = "first_light",
        titleRes = Res.string.special_event_first_light_title,
        descRes = Res.string.special_event_first_light_desc,
        condition = SpecialEventCondition.WinAsWhite,
        reward = SpecialEventReward(
            paletteName = "Aurora",
            displayNameRes = Res.string.special_event_palette_aurora,
        ),
        activeDays = listOf(4 to 4, 4 to 6), // April 4 and April 6 — (month to day)
    ),

    SpecialEvent(
        id = "dark_side",
        titleRes = Res.string.special_event_dark_side_title,
        descRes = Res.string.special_event_dark_side_desc,
        condition = SpecialEventCondition.WinAsBlack,
        reward = SpecialEventReward(
            paletteName = "Ember",
            displayNameRes = Res.string.special_event_palette_ember,
        ),
        activeDays = listOf(4 to 5, 4 to 6), // April 5 and April 6 — (month to day)
    ),
)
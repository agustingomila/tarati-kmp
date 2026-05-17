package com.agustin.tarati.features.seasonal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.util.*

private val Context.specialEventDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "special_events")

/**
 * Persiste el estado de los eventos especiales que no encaja en
 * [AchievementsRepository]:
 * el flag "el usuario ya tocó el ícono de regalo este año" (seen-this-year).
 *
 * ## Separación de responsabilidades
 * - **[AchievementsRepository]**
 * — almacena si la paleta está desbloqueada permanentemente (`auroraUnlocked`,
 * `emberUnlocked`). Esos flows los consume [SettingsViewModel]
 * para filtrar la lista de paletas disponibles.
 * - **[SpecialEventRepository]** — almacena si el ícono de regalo fue tocado este año
 *   (para desactivar la animación). Sólo lo consume [SpecialEventManager].
 *
 * ## Clave compuesta `{eventId}_{year}`
 * Al incluir el año en la clave, el flag expira naturalmente el año siguiente sin
 * lógica adicional: la app simplemente no encuentra la clave del año en curso y
 * trata el evento como no-visto, volviendo a mostrar la animación.
 *
 * ## Threading
 * All methods are `suspend`. Callers must dispatch to [kotlinx.coroutines.Dispatchers.IO].
 * In Compose, use `withContext(Dispatchers.IO)` inside a LaunchedEffect.
 */
class SpecialEventRepository(private val context: Context) {

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Returns `true` if the user has already tapped the gift icon for [eventId] this year.
     * When `true`, the icon is displayed without the pulsing animation.
     *
     * ## Threading
     * `suspend` — must be called from a coroutine. In Compose, use
     * `withContext(Dispatchers.IO)` inside a LaunchedEffect:
     * ```kotlin
     * LaunchedEffect(event.id) {
     *     isSeen = withContext(Dispatchers.IO) { manager.isGiftSeen(event) }
     * }
     * ```
     */
    suspend fun isGiftSeen(eventId: String): Boolean {
        val key = seenKey(eventId)
        return context.specialEventDataStore.data.first()[key] ?: false
    }

    /**
     * Marks the gift icon for [eventId] as seen for the current year.
     * Called when the user taps the icon for the first time in this annual window.
     * Idempotent — safe to call multiple times.
     *
     * ## Threading
     * `suspend` — must be called from a coroutine. In Compose, use
     * `rememberCoroutineScope().launch(Dispatchers.IO)` from an onClick lambda.
     */
    suspend fun markGiftSeen(eventId: String) {
        context.specialEventDataStore.edit { prefs ->
            prefs[seenKey(eventId)] = true
        }
    }

    // ── Clave privada ─────────────────────────────────────────────────────────

    /**
     * Clave de DataStore para el flag seen-this-year de [eventId].
     *
     * El año se embebe en la clave para que el flag expire automáticamente el año
     * siguiente: `special_event_seen_first_light_2026`, `special_event_seen_first_light_2027`, etc.
     */
    private fun seenKey(eventId: String): Preferences.Key<Boolean> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        return booleanPreferencesKey("special_event_seen_${eventId}_$year")
    }
}
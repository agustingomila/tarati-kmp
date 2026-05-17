package com.agustin.tarati.services.achievements

import android.app.Activity
import android.content.Intent
import java.lang.ref.WeakReference

/**
 * Proveedor de referencia débil a la [Activity] activa, registrado como
 * singleton en Koin para ser accedido desde servicios sin contexto de UI.
 *
 * ## Por qué WeakReference
 * [AchievementsManager] necesita una [Activity] para llamar a
 * `PlayGames.getAchievementsClient(activity)`, pero no puede recibirla como
 * parámetro en cada llamada sin propagarla por toda la cadena de lógica de
 * juego. Una referencia fuerte almacenada en un singleton retendría la Activity
 * más allá de su ciclo de vida, causando una memory leak. [WeakReference]
 * permite al GC liberar la Activity cuando el sistema la destruye; las llamadas
 * a [AchievementsManager] simplemente verifican si la referencia sigue viva y
 * omiten la operación si no (el juego continúa sin crash).
 *
 * ## Ciclo de vida
 * [set] se llama en [MainActivity.onResume] — el momento más temprano en que
 * la Activity está garantizadamente visible y usable. [clear] se llama en
 * [MainActivity.onPause] — el momento en que la Activity deja de ser foreground.
 * Este intervalo cubre todos los casos de uso legítimos (abrir logros, reportar
 * logros tras victoria) sin retener la referencia en background.
 *
 * ## intentLauncher
 * En Android 14+, `startActivity` con intents de extras anidados (como los
 * generados por Play Games SDK) es bloqueado por IntentRedirect Hardening.
 * El launcher registrado via [androidx.activity.result.registerForActivityResult]
 * en [MainActivity] es la vía correcta para lanzar estos intents
 * y se almacena aquí junto a la Activity para mantener cohesión del acceso a contexto de UI.
 */
class ActivityProvider {
    private var activityRef: WeakReference<Activity> = WeakReference(null)

    /** Registered in MainActivity via [androidx.activity.result.registerForActivityResult]. */
    var intentLauncher: ((Intent) -> Unit)? = null

    fun set(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun clear() {
        activityRef = WeakReference(null)
    }

    fun get(): Activity? = activityRef.get()
}
package com.agustin.tarati.services.achievements

import android.app.Activity
import com.google.android.gms.games.PlayGames
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Gestiona la autenticación con Google Play Games Services.
 *
 * ## Inicialización
 * Debe inicializarse en [Application.onCreate()] o antes de usar cualquier
 * servicio de Google Play Games:
 * ```kotlin
 * class TaratiApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         PlayGamesSdk.initialize(this)
 *     }
 * }
 * ```
 *
 * ## Uso en MainActivity
 * ```kotlin
 * class MainActivity : ComponentActivity() {
 *     private val authManager: GamesAuthManager by inject()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         lifecycleScope.launch {
 *             authManager.signInSilently()
 *         }
 *     }
 *
 *     override fun onResume() {
 *         super.onResume()
 *         lifecycleScope.launch {
 *             authManager.signInSilently()
 *         }
 *     }
 * }
 * ```
 *
 * ## Estado de Autenticación
 * Observa [isAuthenticated] para reaccionar a cambios de autenticación:
 * ```kotlin
 * authManager.isAuthenticated.collectAsState { authenticated ->
 *     if (authenticated) {
 *         // Habilitar features de Google Play
 *     }
 * }
 * ```
 */
class GamesAuthManager(
    activity: Activity
) {
    private val gamesSignInClient = PlayGames.getGamesSignInClient(activity)

    private val _isAuthenticated = MutableStateFlow(false)
    private val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    /**
     * Intenta autenticar silenciosamente (sin UI).
     *
     * Este método debe llamarse en:
     * - [Activity.onCreate]
     * - [Activity.onResume]
     *
     * Si la autenticación silenciosa falla, el usuario verá un banner
     * para iniciar sesión manualmente la primera vez.
     *
     * @return true si la autenticación fue exitosa
     */
    suspend fun signInSilently(): Boolean {
        return try {
            val result = gamesSignInClient.isAuthenticated().await()
            _isAuthenticated.value = result.isAuthenticated
            result.isAuthenticated
        } catch (e: Exception) {
            e.printStackTrace()
            _isAuthenticated.value = false
            false
        }
    }

    /**
     * Intenta autenticar con UI (muestra diálogo de Google Play).
     *
     * Llama a este método cuando el usuario toca un botón de "Iniciar sesión"
     * o cuando quieres forzar la autenticación interactiva.
     *
     * @return true si la autenticación fue exitosa
     */
    suspend fun signInInteractively(): Boolean {
        return try {
            val result = gamesSignInClient.signIn().await()
            _isAuthenticated.value = result.isAuthenticated
            result.isAuthenticated
        } catch (e: Exception) {
            e.printStackTrace()
            _isAuthenticated.value = false
            false
        }
    }

    /**
     * Cierra la sesión de Google Play Games.
     *
     * Nota: signOut() en Google Play Games v2 es un método void,
     * no retorna un Task, por lo que no necesita await().
     */
    fun signOut() {
        try {
            // gamesSignInClient.signOut()
            _isAuthenticated.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.agustin.tarati.features.online.supporter

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agustin.tarati.services.billing.SupporterInterval
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Estado de la pantalla de pago Supporter.
 *
 * El monto lo cobra el proveedor (Polar) en su propia página, así que el cliente solo elige el
 * intervalo (sin monto in-app).
 *
 * @param interval    Pago único o suscripción mensual.
 * @param isSupporter true si el usuario ya posee el entitlement `supporter`.
 * @param isLoading   true mientras se crea el Checkout.
 * @param error       Clave de error a localizar (o null).
 */
@Immutable
data class SupporterUiState(
    val interval: SupporterInterval = SupporterInterval.ONCE,
    val isSupporter: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel de la pantalla Supporter. Inicia el Checkout del proveedor activo (Polar en
 * Desktop/Web) y refleja el ownership leído del servidor (entitlements C2).
 */
@Stable
interface ISupporterViewModel {
    val uiState: StateFlow<SupporterUiState>

    /** Emite la URL de Checkout a abrir en el browser cuando [checkout] tiene éxito. */
    val checkoutUrlEvent: SharedFlow<String>

    /** true si el checkout web está disponible en esta plataforma (Desktop/Web; false en Android/iOS). */
    val stripeAvailable: Boolean

    fun selectInterval(interval: SupporterInterval)

    /** Crea el Checkout y, si todo va bien, emite la URL en [checkoutUrlEvent]. */
    fun checkout()

    /** Refresca los entitlements desde el servidor (al volver del pago). */
    fun refresh()
}

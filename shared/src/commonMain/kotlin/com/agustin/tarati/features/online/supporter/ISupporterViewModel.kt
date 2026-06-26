package com.agustin.tarati.features.online.supporter

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agustin.tarati.services.billing.SupporterInterval
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Estado de la pantalla de pago Supporter (fase C3).
 *
 * @param interval         Pago único o suscripción mensual.
 * @param amountCents      Monto activo en centavos de USD (preset o custom).
 * @param customAmountText Texto del campo de monto libre (en dólares, como lo tipea el usuario).
 * @param isSupporter      true si el usuario ya posee el entitlement `supporter`.
 * @param isLoading        true mientras se crea el Checkout Session.
 * @param error            Mensaje de error a mostrar (clave a localizar o texto crudo).
 */
@Immutable
data class SupporterUiState(
    val interval: SupporterInterval = SupporterInterval.ONCE,
    val amountCents: Int = 500,
    val customAmountText: String = "",
    val isSupporter: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel de la pantalla Supporter. Inicia el flujo de Stripe Checkout y refleja el
 * estado de ownership leído del servidor (entitlements C2).
 */
@Stable
interface ISupporterViewModel {
    val uiState: StateFlow<SupporterUiState>

    /** Emite la URL de Checkout a abrir en el browser cuando [checkout] tiene éxito. */
    val checkoutUrlEvent: SharedFlow<String>

    /** true si el pago Stripe está disponible en esta plataforma (Desktop/Web). */
    val stripeAvailable: Boolean

    fun selectInterval(interval: SupporterInterval)

    /** Selecciona un monto preset (en centavos). */
    fun selectPreset(amountCents: Int)

    /** Actualiza el campo de monto libre (en dólares); recalcula [SupporterUiState.amountCents]. */
    fun setCustomAmount(text: String)

    /** Crea el Checkout Session y, si todo va bien, emite la URL en [checkoutUrlEvent]. */
    fun checkout()

    /** Refresca los entitlements desde el servidor (al volver del pago). */
    fun refresh()
}

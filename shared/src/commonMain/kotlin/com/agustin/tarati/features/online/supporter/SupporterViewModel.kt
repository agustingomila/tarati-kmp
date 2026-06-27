package com.agustin.tarati.features.online.supporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.services.billing.EntitlementsRepository
import com.agustin.tarati.services.billing.SUPPORTER_PRODUCT_ID
import com.agustin.tarati.services.billing.SupporterInterval
import com.agustin.tarati.services.billing.supporterStripeAvailable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Implementación de [ISupporterViewModel].
 *
 * Proveedor activo en Desktop/Web: **Polar** (Merchant of Record). El monto lo cobra Polar en
 * su propia página (pay-what-you-want o precio fijo), así que el cliente solo elige el intervalo
 * (único / mensual) y no maneja montos. El grant del entitlement llega por el webhook server-side;
 * al volver del browser, [refresh] trae el estado y `isSupporter` se actualiza por el collector.
 */
class SupporterViewModel(
    private val entitlementsRepository: EntitlementsRepository,
    override val stripeAvailable: Boolean = supporterStripeAvailable(),
) : ViewModel(), ISupporterViewModel {

    private val _uiState = MutableStateFlow(SupporterUiState())
    override val uiState: StateFlow<SupporterUiState> = _uiState.asStateFlow()

    private val _checkoutUrlEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val checkoutUrlEvent: SharedFlow<String> = _checkoutUrlEvent.asSharedFlow()

    init {
        // Reflejar el ownership leído del servidor en `isSupporter`.
        viewModelScope.launch {
            entitlementsRepository.entitlements.collect { owned ->
                _uiState.update { it.copy(isSupporter = SUPPORTER_PRODUCT_ID in owned) }
            }
        }
    }

    override fun selectInterval(interval: SupporterInterval) {
        _uiState.update { it.copy(interval = interval) }
    }

    override fun checkout() {
        if (!stripeAvailable) return
        val interval = _uiState.value.interval
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val url = entitlementsRepository.startPolarCheckout(interval)
            if (url != null) {
                _checkoutUrlEvent.emit(url)
            } else {
                _uiState.update { it.copy(error = ERROR_CHECKOUT_FAILED) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    override fun refresh() {
        viewModelScope.launch { entitlementsRepository.refresh() }
    }

    companion object {
        /** Clave de error: el servidor no pudo crear el checkout (503/502/red). */
        const val ERROR_CHECKOUT_FAILED: String = "checkout_failed"
    }
}

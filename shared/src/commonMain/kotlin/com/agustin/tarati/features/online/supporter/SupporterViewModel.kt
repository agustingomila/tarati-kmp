package com.agustin.tarati.features.online.supporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.services.billing.EntitlementsRepository
import com.agustin.tarati.services.billing.SUPPORTER_MIN_CENTS
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
 * No requiere token explícito: [EntitlementsRepository] resuelve la sesión internamente
 * (mismo patrón que C2). El grant del entitlement llega por el webhook server-side; tras
 * volver del browser, [refresh] trae el estado actualizado y `isSupporter` se actualiza
 * por el collector del flow de entitlements.
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

    override fun selectPreset(amountCents: Int) {
        _uiState.update { it.copy(amountCents = amountCents, customAmountText = "", error = null) }
    }

    override fun setCustomAmount(text: String) {
        val cents = parseDollarsToCents(text)
        _uiState.update {
            it.copy(
                customAmountText = text,
                amountCents = cents ?: it.amountCents,
                error = null,
            )
        }
    }

    override fun checkout() {
        if (!stripeAvailable) return
        val state = _uiState.value
        if (state.amountCents < SUPPORTER_MIN_CENTS) {
            _uiState.update { it.copy(error = ERROR_MIN_AMOUNT) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val url = entitlementsRepository.startStripeCheckout(state.amountCents, state.interval)
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
        /** Clave de error: monto por debajo del mínimo. */
        const val ERROR_MIN_AMOUNT = "min_amount"

        /** Clave de error: el servidor no pudo crear el checkout (503/502/red). */
        const val ERROR_CHECKOUT_FAILED = "checkout_failed"

        /**
         * Parsea un monto en dólares (texto del usuario) a centavos. Acepta coma o punto
         * decimal. Devuelve null si no es un número válido o es <= 0.
         */
        fun parseDollarsToCents(text: String): Int? {
            val normalized = text.trim().replace(',', '.')
            if (normalized.isEmpty()) return null
            val dollars = normalized.toDoubleOrNull() ?: return null
            if (dollars <= 0.0) return null
            return (dollars * 100).toInt()
        }
    }
}

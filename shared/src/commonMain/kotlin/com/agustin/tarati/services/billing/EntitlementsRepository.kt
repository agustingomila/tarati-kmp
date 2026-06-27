package com.agustin.tarati.services.billing

import com.agustin.tarati.features.online.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** `productId` del entitlement global que desbloquea todo (modelo Supporter, fase C4). */
const val SUPPORTER_PRODUCT_ID: String = com.agustin.tarati.network.models.SUPPORTER_PRODUCT_ID

/** Montos sugeridos (en centavos de USD) para el pago Supporter vía Stripe (fase C3). */
val SUPPORTER_PRESET_CENTS: List<Int> = listOf(300, 500, 1000)

/** Monto mínimo aceptado para el pago Supporter (USD 2.00, en centavos). El server re-valida. */
const val SUPPORTER_MIN_CENTS: Int = 200

/** Intervalo de cobro del pago Supporter — el `key` viaja en el DTO al servidor. */
enum class SupporterInterval(val key: String) {
    ONCE("once"),
    MONTHLY("month"),
}

/**
 * Regla de desbloqueo cross-platform: un producto está desbloqueado si el usuario
 * es supporter (desbloquea todo) o posee ese `productId` específico.
 *
 * Vive en el cliente (el servidor devuelve la lista cruda). Existe desde ya pero
 * recién se USA al reactivar el gate en C4.
 */
fun isUnlocked(productId: String, owned: Set<String>): Boolean =
    SUPPORTER_PRODUCT_ID in owned || productId in owned

/**
 * Fuente de ownership cross-platform leída del servidor de Tarati.
 *
 * Convierte Desktop/Web de no-op a compras reales: una compra validada en Android
 * (C2) queda disponible en el resto de plataformas vía `GET /api/entitlements`.
 *
 * Cache solo en memoria (decisión MVP): el ciclo de vida lo gobierna la sesión
 * ([refresh] al autenticar, [clear] al cerrar sesión). Arranque offline = vacío
 * hasta el primer [refresh]; impacto nulo mientras el gate de compra siga off (C4).
 */
interface EntitlementsRepository {
    /** `productId` activos del servidor. Emite vacío sin sesión. */
    val entitlements: StateFlow<Set<String>>

    /** Recarga los entitlements desde el servidor. No-op (limpia) si no hay token. */
    suspend fun refresh()

    /**
     * Valida un recibo de Google Play en el servidor y, si concede, refresca.
     * @return true si el servidor validó la compra.
     */
    suspend fun validateGooglePlay(productId: String, purchaseToken: String): Boolean

    /**
     * Crea un Stripe Checkout Session para el pago Supporter (Desktop/Web, fase C3, dormido).
     * @return La URL de Checkout a abrir en el browser, o null si no hay sesión o el
     *         servidor no pudo crearla. El grant llega luego vía webhook → [refresh].
     */
    suspend fun startStripeCheckout(amountCents: Int, interval: SupporterInterval): String?

    /**
     * Crea un Checkout de Polar (proveedor activo Web/Desktop). Sin monto: Polar lo cobra en
     * su página. @return La URL a abrir en el browser, o null. El grant llega vía webhook → [refresh].
     */
    suspend fun startPolarCheckout(interval: SupporterInterval): String?

    /** Vacía el estado local (logout). No persiste nada. */
    fun clear()
}

class EntitlementsRepositoryImpl(
    private val syncService: EntitlementSyncService,
    private val authRepository: AuthRepository,
) : EntitlementsRepository {

    private val _entitlements = MutableStateFlow<Set<String>>(emptySet())
    override val entitlements: StateFlow<Set<String>> = _entitlements.asStateFlow()

    override suspend fun refresh() {
        val token = authRepository.getToken()
        if (token == null) {
            _entitlements.value = emptySet()
            return
        }
        syncService.getActive(token).onSuccess { ids -> _entitlements.value = ids.toSet() }
    }

    override suspend fun validateGooglePlay(productId: String, purchaseToken: String): Boolean {
        val token = authRepository.getToken() ?: return false
        val granted = syncService.validateGooglePlay(token, productId, purchaseToken)
        if (granted) refresh()
        return granted
    }

    override suspend fun startStripeCheckout(amountCents: Int, interval: SupporterInterval): String? {
        val token = authRepository.getToken() ?: return null
        return syncService.createStripeCheckout(token, amountCents, interval.key).getOrNull()
    }

    override suspend fun startPolarCheckout(interval: SupporterInterval): String? {
        val token = authRepository.getToken() ?: return null
        return syncService.createPolarCheckout(token, interval.key).getOrNull()
    }

    override fun clear() {
        _entitlements.value = emptySet()
    }
}

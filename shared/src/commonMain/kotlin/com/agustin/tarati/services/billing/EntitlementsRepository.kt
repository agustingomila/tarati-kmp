package com.agustin.tarati.services.billing

import com.agustin.tarati.features.online.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** `productId` del entitlement global que desbloquea todo (modelo Supporter, fase C4). */
const val SUPPORTER_PRODUCT_ID: String = "supporter"

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

    override fun clear() {
        _entitlements.value = emptySet()
    }
}

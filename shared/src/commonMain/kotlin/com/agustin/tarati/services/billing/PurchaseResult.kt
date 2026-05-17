package com.agustin.tarati.services.billing

/**
 * Resultado de un intento de compra iniciado por [IBillingManager.launchPurchaseFlow].
 *
 * La UI observa [IBillingManager.purchaseResult] para reaccionar a cada outcome.
 */
sealed class PurchaseResult {

    /** La compra se completó y fue reconocida en Google Play. */
    data class Success(val productId: String) : PurchaseResult()

    /**
     * El usuario canceló el diálogo de compra de Google Play.
     * No requiere feedback al usuario; se ignora silenciosamente.
     */
    data object Cancelled : PurchaseResult()

    /**
     * Google Play devolvió un error.
     *
     * @param responseCode Código de error de [com.android.billingclient.api.BillingClient.BillingResponseCode].
     * @param debugMessage Mensaje de depuración; nunca mostrar al usuario.
     */
    data class Error(
        val responseCode: Int,
        val debugMessage: String,
    ) : PurchaseResult()
}
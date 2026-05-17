package com.agustin.tarati.services.billing

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato del gestor de compras in-app.
 *
 * El caller (típicamente [SettingsViewModel])
 * observa [purchasedProductIds] para conocer qué productos están desbloqueados y
 * llama a [launchPurchaseFlow] cuando el usuario quiere comprar un tipo de pieza.
 *
 * ## Ciclo de vida
 * La implementación conecta [com.android.billingclient.api.BillingClient] en el
 * constructor y lo desconecta cuando el scope del singleton es cancelado.
 * [queryOwnedPurchases] debe invocarse desde `MainActivity.onResume` para
 * mantener [purchasedProductIds] actualizado sin depender de la caché local.
 *
 * ## Validación del servidor
 * La implementación actual reconoce y acepta las compras del lado del cliente.
 * Para producción con riesgo real de fraude se recomienda añadir validación
 * server-side, pero para un juego de bajo valor es suficiente el reconocimiento
 * inmediato.
 */
interface IBillingManager {

    /**
     * `true` mientras el [com.android.billingclient.api.BillingClient] está
     * conectado y listo para procesar peticiones.
     */
    val billingReady: StateFlow<Boolean>

    /**
     * Conjunto de `productId`s comprados y reconocidos en Google Play.
     * Persiste en memoria durante la sesión; se refresca en [queryOwnedPurchases].
     *
     * Un [PieceType] está desbloqueado
     * cuando su `productId` está contenido en este conjunto, o cuando `productId == null`
     * (piezas gratuitas como el círculo).
     */
    val purchasedProductIds: StateFlow<Set<String>>

    /**
     * Emite exactamente un [PurchaseResult] por cada intento de compra completado
     * (éxito, cancelación o error). La UI lo observa con `LaunchedEffect` para
     * mostrar feedback puntual sin retener estado.
     */
    val purchaseResult: SharedFlow<PurchaseResult>

    /**
     * Inicia el flujo de compra de Google Play para [productId].
     *
     * La [android.app.Activity] en primer plano se obtiene internamente de
     * [ActivityProvider]. Si no hay Activity
     * disponible o el cliente no está listo, emite [PurchaseResult.Error].
     *
     * @param productId El ID del producto en Google Play Console.
     */
    fun launchPurchaseFlow(productId: String)

    /**
     * Consulta Google Play para actualizar [purchasedProductIds] con el estado real
     * de las compras. Debe invocarse desde `MainActivity.onResume`.
     */
    suspend fun queryOwnedPurchases()
}
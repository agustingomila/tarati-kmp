package com.agustin.tarati.services.billing

import com.agustin.tarati.features.online.devServerUrl
import com.agustin.tarati.network.models.EntitlementsResponse
import com.agustin.tarati.network.models.GooglePlayPurchaseRequest
import com.agustin.tarati.network.models.StripeCheckoutRequest
import com.agustin.tarati.network.models.StripeCheckoutResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/**
 * Cliente HTTP para los endpoints de entitlements del servidor de Tarati.
 *
 * Molde de [com.agustin.tarati.services.achievements.AchievementSyncService]:
 * los fallos de red se capturan internamente y se reportan como [Result.Failure]
 * o [Boolean] false — el caller decide cómo reaccionar.
 */
class EntitlementSyncService(private val httpClient: HttpClient) {

    private val baseUrl = devServerUrl

    /**
     * Obtiene los `productId` activos del usuario autenticado.
     * Usado para poblar el estado local de ownership al iniciar sesión.
     */
    suspend fun getActive(token: String): Result<List<String>> = runCatching {
        httpClient.get("$baseUrl/api/entitlements") {
            header("Authorization", "Bearer $token")
        }.body<EntitlementsResponse>().productIds
    }

    /**
     * Envía un recibo de Google Play para validación server-side.
     * @return true si el servidor validó y concedió el producto (HTTP 2xx).
     */
    suspend fun validateGooglePlay(token: String, productId: String, purchaseToken: String): Boolean = runCatching {
        val response: HttpResponse = httpClient.post("$baseUrl/api/entitlements/google-play") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(GooglePlayPurchaseRequest(productId, purchaseToken))
        }
        response.status.isSuccess()
    }.getOrDefault(false)

    /**
     * Crea un Stripe Checkout Session para el pago Supporter (fase C3, Desktop/Web).
     * @return La URL de Checkout a abrir en el browser, o [Result.failure] si el server
     *         no pudo crearlo (503 sin credenciales, 502 error de Stripe, red).
     */
    suspend fun createStripeCheckout(token: String, amountCents: Int, interval: String): Result<String> =
        runCatching {
            httpClient.post("$baseUrl/api/checkout/stripe") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(StripeCheckoutRequest(amountCents, interval))
            }.body<StripeCheckoutResponse>().checkoutUrl
        }
}

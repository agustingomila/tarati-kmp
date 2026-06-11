package com.agustin.tarati.services.achievements

import com.agustin.tarati.features.online.devServerUrl
import com.agustin.tarati.network.models.AchievementProgressRequest
import com.agustin.tarati.network.models.ServerAchievementDto
import com.agustin.tarati.network.models.UnlockAchievementRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Cliente HTTP para los endpoints de logros del servidor de Tarati.
 *
 * Todas las funciones retornan un tipo de resultado sin lanzar excepciones —
 * los fallos de red se capturan internamente y se reportan como [Boolean]
 * false o [Result.Failure]. El caller decide si encolar el intento fallido.
 */
class AchievementSyncService(private val httpClient: HttpClient) {

    private val baseUrl = devServerUrl

    /**
     * Desbloquea un logro one-shot en el servidor.
     * @return true si el servidor procesó la solicitud correctamente.
     */
    suspend fun unlock(token: String, achievementId: AchievementId): Boolean = runCatching {
        httpClient.post("$baseUrl/api/achievements/unlock") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(UnlockAchievementRequest(achievementId.id))
        }
    }.isSuccess

    /**
     * Actualiza los pasos de un logro incremental en el servidor.
     * El servidor solo avanza — nunca retrocede el contador.
     * @return true si el servidor procesó la solicitud correctamente.
     */
    suspend fun progress(token: String, achievementId: AchievementId, steps: Int): Boolean = runCatching {
        httpClient.post("$baseUrl/api/achievements/progress") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(AchievementProgressRequest(achievementId.id, steps))
        }
    }.isSuccess

    /**
     * Obtiene todos los logros del usuario autenticado desde el servidor.
     * Usado para restaurar los contadores in-memory al iniciar una sesión.
     */
    suspend fun getAll(token: String): Result<List<ServerAchievementDto>> = runCatching {
        httpClient.get("$baseUrl/api/achievements") {
            header("Authorization", "Bearer $token")
        }.body()
    }
}

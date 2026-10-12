package id.ganyem.app.network

import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient (
    private val baseUrl: String,
    private val tokenManager: TokenManager,
    private val refreshAccessToken: suspend () -> AppTokens
) {
    val client = HttpClient{
        install(ContentNegotiation) {
            json(
                Json{
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    fun accessToken(): String? = tokenManager.getAccessToken()
    fun close() {
        client.close()
    }

    suspend fun get(path: String): HttpResponse {
        val tokens = tokenManager.getCurrentTokens()
            ?: error("Access token tidak tersedia. Silahkan login.")

        val token = if (tokens.isExpiredSoon()) {
            refreshAccessToken().accessToken
        } else {
            tokens.accessToken
        }

        val response = executeGet(path, token)
        if (response.status != HttpStatusCode.Unauthorized) {
            return response
        }

        // Refresh
        val newTokens = refreshAccessToken()

        // Retry
        return executeGet(path, newTokens.accessToken)
    }

    private suspend fun executeGet(
        path: String,
        token: String
    ): HttpResponse {
        return client.get(
            "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"
        ) {
            header("Authorization", "Bearer $token")
        }
    }
}
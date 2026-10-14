package id.ganyem.app.network

import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String,
    private val tokenManager: TokenManager,
    private val refreshAccessToken: suspend () -> AppTokens
) {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun get(path: String) =
        request(HttpMethod.Get, path)

    suspend fun post(
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {}
    ) = request(HttpMethod.Post, path, configure)

    suspend fun put(
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {}
    ) = request(HttpMethod.Put, path, configure)

    suspend fun delete(
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {}
    ) = request(HttpMethod.Delete, path, configure)



    private suspend fun request(
        method: HttpMethod,
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {}
    ) = authenticatedRequest { token ->
        client.request(
            "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"
        ) {
            this.method = method
            header("Authorization", "Bearer $token")
            configure()
        }
    }

    private suspend fun authenticatedRequest(
        execute: suspend (String) -> io.ktor.client.statement.HttpResponse
    ): io.ktor.client.statement.HttpResponse {
        val tokens = tokenManager.getCurrentTokens()
            ?: error("Access token tidak tersedia. Silahkan login.")

        val token = if (tokens.isExpiredSoon()) {
            refreshAccessToken().accessToken
        } else {
            tokens.accessToken
        }

        val response = execute(token)
        if (response.status != HttpStatusCode.Unauthorized) {
            return response
        }

        // Refresh
        val newTokens = refreshAccessToken()

        // Retry
        val retryResponse = execute(newTokens.accessToken)

        if (retryResponse.status == HttpStatusCode.Unauthorized) {
            error("Autentikasi gagal setelah refresh token.")
        }

        return retryResponse
    }
}
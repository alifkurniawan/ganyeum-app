package id.ganyem.app.network

import id.ganyem.app.auth.AppTokens
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ExchangeRequest(
    val idToken: String
)

@Serializable
data class ExchangeResponse(
    val accessToken: String,
    val expiresIn: Long
)
class AuthApi (
    private val baseUrl: String
) {
    private val client = HttpClient{
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun exchangeToken(firebaseIdToken: String): AppTokens {
        val response = client.post(
            "$baseUrl/api/auth/exchange"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                ExchangeRequest(
                    idToken = firebaseIdToken
                )
            )
        }
        val result = response.body<ExchangeResponse>()
        require(result.accessToken.isNotBlank()) {
            "Backend returned an empty access token"
        }

        return AppTokens(
            accessToken = result.accessToken,
            expiresIn = result.expiresIn
        )
    }

    fun close() {
        client.close()
    }
}
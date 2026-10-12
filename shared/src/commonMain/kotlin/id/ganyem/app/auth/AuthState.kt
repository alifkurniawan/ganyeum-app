package id.ganyem.app.auth

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class AppTokens(
    val accessToken: String,
    var expiresIn: Long
) {
    fun isExpiredSoon(
        bufferMillis: Long = 60_000
    ): Boolean {
        return Clock.System.now().toEpochMilliseconds() >= expiresIn - bufferMillis
    }

    fun isExpired() : Boolean {
        return Clock.System.now().toEpochMilliseconds() > expiresIn
    }
}

sealed interface AuthState {
    data object Unauthenticated: AuthState
    data object Loading: AuthState
    data class Authenticated(
        val token: AppTokens
    ) : AuthState
    data class Error(
        val message: String
    ) : AuthState
}
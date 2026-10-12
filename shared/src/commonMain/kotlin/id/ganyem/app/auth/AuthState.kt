package id.ganyem.app.auth

data class AppTokens(
    val accessToken: String,
    var expiresIn: Long
)

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
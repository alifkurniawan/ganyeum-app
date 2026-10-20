package id.ganyem.app.auth

import id.ganyem.app.network.AuthApi
import kotlin.coroutines.cancellation.CancellationException

interface AuthSessionRepository {
    suspend fun logout()
    suspend fun restoreSession(): AppTokens?
}

class AuthRepository (
    private val googleAuth: GoogleAuthProvider,
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val tokenStorage: TokenStorage
) : AuthSessionRepository {
    suspend fun login(): AppTokens {
        val firebaseIdToken = googleAuth.signInAndGetIdToken()
        val tokens = authApi.exchangeToken(firebaseIdToken)
        tokenManager.updateTokens(tokens)
        tokenStorage.save(tokens)
        return tokens
    }

    override suspend fun logout() {
        googleAuth.signOut()
        tokenManager.clearTokens()
        tokenStorage.clear()
    }

    suspend fun refreshAccessToken(): AppTokens {
        val firebaseIdToken = googleAuth.getIdToken(forceRefresh = true)
        val tokens =  authApi.exchangeToken(firebaseIdToken)
        tokenManager.updateTokens(tokens)
        tokenStorage.save(tokens)
        return tokens
    }

    override suspend fun restoreSession(): AppTokens? {
        val tokens = tokenStorage.load()

        if (tokens != null && !tokens.isExpiredSoon()) {
            tokenManager.updateTokens(tokens)
            return tokens
        }

        return try {
            refreshAccessToken()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            tokenManager.clearTokens()
            tokenStorage.clear()
            null
        }
    }
}

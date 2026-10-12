package id.ganyem.app.auth

import id.ganyem.app.network.AuthApi

class AuthRepository (
    private val googleAuth: GoogleAuthProvider,
    private val authApi: AuthApi
) {
    suspend fun login(): AppTokens {
        val firebaseIdToken = googleAuth.signInAndGetIdToken()
        return authApi.exchangeToken(firebaseIdToken)
    }

    suspend fun logout() {
        googleAuth.signOut()
    }
}
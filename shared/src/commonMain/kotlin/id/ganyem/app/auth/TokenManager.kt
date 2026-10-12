package id.ganyem.app.auth

class TokenManager (
    initialTokens: AppTokens? = null
) {
    private var currentTokens: AppTokens? = initialTokens

    fun getAccessToken(): String? {
        return currentTokens?.accessToken
    }

    fun updateTokens(tokens: AppTokens) {
        currentTokens = tokens
    }

    fun clearTokens() {
        currentTokens = null
    }

    fun getCurrentTokens(): AppTokens? {
        return currentTokens
    }
}
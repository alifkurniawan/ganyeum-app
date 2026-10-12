package id.ganyem.app.auth

interface TokenStorage{
    suspend fun save(tokens: AppTokens)
    suspend fun load(): AppTokens?
    suspend fun clear()
}
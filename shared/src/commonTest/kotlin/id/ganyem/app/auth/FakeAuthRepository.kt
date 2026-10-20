package id.ganyem.app.auth

class FakeAuthRepository : AuthSessionRepository {
    var sessionToRestore: AppTokens? = null
    var restoreError: Exception? = null
    var logoutError: Exception? = null
    var restoreCalls = 0
    var logoutCalls = 0

    override suspend fun restoreSession(): AppTokens? {
        restoreCalls++
        restoreError?.let { throw it }
        return sessionToRestore
    }

    override suspend fun logout() {
        logoutCalls++
        logoutError?.let { throw it }
    }
}

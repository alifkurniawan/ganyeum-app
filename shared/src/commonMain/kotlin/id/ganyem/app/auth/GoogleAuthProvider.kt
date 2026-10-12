package id.ganyem.app.auth

interface GoogleAuthProvider {
    suspend fun signInAndGetIdToken(): String

    suspend fun getIdToken(forceRefresh: Boolean = false): String
    suspend fun signOut()
}
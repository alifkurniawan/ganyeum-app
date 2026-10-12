package id.ganyem.app.auth

interface GoogleAuthProvider {
    suspend fun signInAndGetIdToken(): String
    suspend fun signOut()
}
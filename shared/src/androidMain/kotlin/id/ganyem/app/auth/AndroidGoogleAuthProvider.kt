package id.ganyem.app.auth

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AndroidGoogleAuthProvider(
    private val activity: Activity,
    private val webClientId: String

    ) : GoogleAuthProvider{
    private val credentialManager = CredentialManager.create(activity)

    override suspend fun signInAndGetIdToken(): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )

        val credential = result.credential
        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            error("Kredential Google tidak valid")
        }

        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCredential = com.google.firebase.auth.GoogleAuthProvider.getCredential(
            googleCredential.idToken,
            null
        )

        val authResult = FirebaseAuth.getInstance()
            .signInWithCredential(firebaseCredential)
            .await()

        val user = authResult.user?: error("Firebase tidak mengembalikan user")
        return user.getIdToken(false)
            .await()
            .token
            ?: error("Firebase ID Token tidak tersedia")
    }

    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()

        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
    }
}
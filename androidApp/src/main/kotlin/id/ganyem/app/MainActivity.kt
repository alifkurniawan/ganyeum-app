package id.ganyem.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import id.ganyem.app.auth.AndroidGoogleAuthProvider
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.network.AuthApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val googleAuth = AndroidGoogleAuthProvider(
            activity = this,
            webClientId = getString(R.string.default_web_client_id)
        )

        val authApi = AuthApi(
            baseUrl = "http://192.168.1.5:8080"
        )

        val repository = AuthRepository(
            googleAuth = googleAuth,
            authApi = authApi
        )

        setContent {
            App(repository = repository)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}
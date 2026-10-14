package id.ganyem.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import id.ganyem.app.auth.AndroidGoogleAuthProvider
import id.ganyem.app.auth.AndroidTokenStorage
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.auth.TokenManager
import id.ganyem.app.auth.TokenStorage
import id.ganyem.app.network.ApiClient
import id.ganyem.app.network.AuthApi
import id.ganyem.app.product.DefaultProductRepository
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val baseUrl = "http://192.168.1.8:8080/api"
        val googleAuth = AndroidGoogleAuthProvider(
            activity = this,
            webClientId = getString(R.string.default_web_client_id)
        )

        val authApi = AuthApi(
            baseUrl = baseUrl
        )

        val tokenManager = TokenManager()
        val tokenStorage = AndroidTokenStorage(this)

        val repository = AuthRepository(
            googleAuth = googleAuth,
            authApi = authApi,
            tokenManager = tokenManager,
            tokenStorage = tokenStorage
        )

        val apiClient = ApiClient(
            baseUrl = baseUrl,
            tokenManager = tokenManager,
            refreshAccessToken = {
                repository.refreshAccessToken()
            }
        )

        val productApi = ProductApi(
            apiClient = apiClient
        )

        val productRepository = DefaultProductRepository(
            productApi
        )

        setContent {
            App(
                repository = repository,
                productRepository = productRepository,
                productApi = productApi
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}
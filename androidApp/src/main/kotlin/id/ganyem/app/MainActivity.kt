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
import id.ganyem.app.customer.CustomerApi
import id.ganyem.app.customer.CustomerRepository
import id.ganyem.app.customer.CustomerRepositoryImpl
import id.ganyem.app.network.ApiClient
import id.ganyem.app.network.AuthApi
import id.ganyem.app.order.OrderApi
import id.ganyem.app.order.OrderRepositoryImpl
import id.ganyem.app.product.DefaultProductRepository
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.shipping.Shipping
import id.ganyem.app.shipping.ShippingApi
import id.ganyem.app.shipping.ShippingRepositoryImpl

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

        val customerApi = CustomerApi(
            apiClient = apiClient
        )

        val customerRepository = CustomerRepositoryImpl(
            customerApi= customerApi
        )

        val shippingApi = ShippingApi(
            apiClient = apiClient
        )

        val shippingRepository = ShippingRepositoryImpl(
            shippingApi = shippingApi
        )

        val orderRepository = OrderRepositoryImpl(
            orderApi = OrderApi(
                apiClient = apiClient
            )
        )


        setContent {
            App(
                repository = repository,
                productRepository = productRepository,
                productApi = productApi,
                customerRepository = customerRepository,
                shippingRepository = shippingRepository,
                orderRepository = orderRepository
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}

package id.ganyem.app.di

import id.ganyem.app.shared.R
import androidx.activity.ComponentActivity
import id.ganyem.app.auth.AndroidGoogleAuthProvider
import id.ganyem.app.auth.AndroidTokenStorage
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.auth.TokenManager
import id.ganyem.app.customer.CustomerApi
import id.ganyem.app.customer.CustomerRepository
import id.ganyem.app.customer.CustomerRepositoryImpl
import id.ganyem.app.network.ApiClient
import id.ganyem.app.network.AuthApi
import id.ganyem.app.order.OrderApi
import id.ganyem.app.order.OrderRepository
import id.ganyem.app.order.OrderRepositoryImpl
import id.ganyem.app.product.DefaultProductRepository
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.shipping.ShippingApi
import id.ganyem.app.shipping.ShippingRepository
import id.ganyem.app.shipping.ShippingRepositoryImpl

class AppContainer(
    activity: ComponentActivity
) {
    private val baseUrl = "http://192.168.1.8:8080/api"

    private val tokenManager = TokenManager()
    private val tokenStorage = AndroidTokenStorage(activity)

    private val authApi = AuthApi(
        baseUrl = baseUrl
    )

    val authRepository = AuthRepository(
        googleAuth = AndroidGoogleAuthProvider(
            activity = activity,
            webClientId = activity.getString(
                R.string.default_web_client_id
            )
        ),
        authApi = authApi,
        tokenManager = tokenManager,
        tokenStorage = tokenStorage
    )

    private val apiClient = ApiClient(
        baseUrl = baseUrl,
        tokenManager = tokenManager,
        refreshAccessToken = {
            authRepository.refreshAccessToken()
        }
    )

    val productApi = ProductApi(
        apiClient = apiClient
    )

    val productRepository: ProductRepository =
        DefaultProductRepository(productApi)

    val customerRepository: CustomerRepository =
        CustomerRepositoryImpl(
            customerApi = CustomerApi(apiClient)
        )

    val shippingRepository: ShippingRepository =
        ShippingRepositoryImpl(
            shippingApi = ShippingApi(apiClient)
        )

    val orderRepository: OrderRepository =
        OrderRepositoryImpl(
            orderApi = OrderApi(apiClient)
        )
}

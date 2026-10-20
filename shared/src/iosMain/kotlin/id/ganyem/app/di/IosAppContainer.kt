package id.ganyem.app.di

import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.auth.GoogleAuthProvider
import id.ganyem.app.auth.IosTokenStorage
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

class IosAppContainer(
    baseUrl: String,
    googleAuthProvider: GoogleAuthProvider
) {
    private val tokenManager = TokenManager()
    private val authApi = AuthApi(baseUrl)

    val authRepository = AuthRepository(
        googleAuth = googleAuthProvider,
        authApi = authApi,
        tokenManager = tokenManager,
        tokenStorage = IosTokenStorage()
    )

    private val apiClient = ApiClient(
        baseUrl = baseUrl,
        tokenManager = tokenManager,
        refreshAccessToken = { authRepository.refreshAccessToken() }
    )

    val productApi = ProductApi(apiClient)
    val productRepository: ProductRepository = DefaultProductRepository(productApi)
    val customerRepository: CustomerRepository = CustomerRepositoryImpl(CustomerApi(apiClient))
    val shippingRepository: ShippingRepository = ShippingRepositoryImpl(ShippingApi(apiClient))
    val orderRepository: OrderRepository = OrderRepositoryImpl(OrderApi(apiClient))
}

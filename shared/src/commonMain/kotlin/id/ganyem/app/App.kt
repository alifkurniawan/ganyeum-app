package id.ganyem.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.customer.CustomerRepository
import id.ganyem.app.order.OrderRepository
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.shipping.ShippingRepository
import id.ganyem.app.ui.HomeScreen
import id.ganyem.app.ui.LoginScreen
import id.ganyem.app.ui.MainScreen
import id.ganyem.app.ui.order.OrderScreen
import id.ganyem.app.ui.order.OrderListScreen
import id.ganyem.app.ui.product.AddProductScreen
import id.ganyem.app.ui.product.EditProductScreen
import id.ganyem.app.ui.product.ProductListScreen
import kotlinx.coroutines.launch

@Composable
fun App(
    repository: AuthRepository,
    productRepository: ProductRepository,
    productApi: ProductApi,
    customerRepository: CustomerRepository,
    shippingRepository: ShippingRepository,
    orderRepository: OrderRepository
) {
    val scope = rememberCoroutineScope()

    var tokens by remember {
        mutableStateOf<AppTokens?>(null)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showAddProduct by remember {
        mutableStateOf(false)
    }

    var selectedProduct by remember {
        mutableStateOf<Product?>(null)
    }

    var showCreateOrder by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(repository) {
        try {
            tokens = repository.restoreSession()
        } catch (e: Exception) {
            error = e.message ?: "Gagal memulihkan sesi."
            tokens = null
        } finally {
            loading = false
        }
    }

    when {
        loading -> {
            CircularProgressIndicator()
        }

        tokens == null -> {
            LoginScreen(
                repository = repository,
                onLoginSuccess = { appTokens ->
                    tokens = appTokens
                    error = null
                }
            )
        }

        showCreateOrder -> {
            OrderScreen(
                onBack = {showCreateOrder = false },
                customerRepository = customerRepository,
                productRepository = productRepository,
                shippingRepository = shippingRepository,
                orderRepository = orderRepository
            )
        }

        else -> {
            MainScreen(
                onCreateOrder = {
                    showCreateOrder = true
                },
                homeContent = {
                    PlaceholderScreen("Home")
                },
                ordersContent = {
                    OrderListScreen(repository = orderRepository)
                },
                itemsContent = {
                    when {
                        selectedProduct != null -> {
                            EditProductScreen(
                                product = selectedProduct!!,
                                repository = productRepository,
                                onBack = {
                                    selectedProduct = null
                                }
                            )
                        }

                        showAddProduct -> {
                            AddProductScreen(
                                productApi = productApi,
                                onBack = {
                                    showAddProduct = false
                                }
                            )
                        }

                        else -> {
                            ProductListScreen(
                                repository = productRepository,
                                onAddProduct = {
                                    showAddProduct = true
                                },
                                onEditProduct = { product ->
                                    selectedProduct = product
                                }
                            )
                        }
                    }
                },
                othersContent = {
                    HomeScreen(
                        accessToken = tokens?.accessToken,
                        onAddProduct = {},
                        onLogout = {
                            scope.launch {
                                try {
                                    repository.logout()
                                } finally {
                                    tokens = null
                                }
                            }
                        }
                    )
                }
            )

        }
    }
}

@Composable

private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title — Coming soon")
    }
}

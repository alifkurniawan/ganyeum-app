package id.ganyem.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.customer.CustomerRepository
import id.ganyem.app.order.OrderRepository
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.shipping.ShippingRepository
import id.ganyem.app.ui.LaunchScreen
import id.ganyem.app.ui.LoginScreen
import id.ganyem.app.ui.MainScreen
import id.ganyem.app.ui.OthersScreen
import id.ganyem.app.ui.auth.AuthViewModel
import id.ganyem.app.ui.order.OrderListScreen
import id.ganyem.app.ui.order.OrderScreen
import id.ganyem.app.ui.product.AddProductScreen
import id.ganyem.app.ui.product.EditProductScreen
import id.ganyem.app.ui.product.ProductListScreen
import id.ganyem.app.ui.product.ProductScreenState
import id.ganyem.app.ui.theme.GanyemTheme

@Composable
fun App(
    repository: AuthRepository,
    productRepository: ProductRepository,
    productApi: ProductApi,
    customerRepository: CustomerRepository,
    shippingRepository: ShippingRepository,
    orderRepository: OrderRepository
) {
    var productScreenState by remember {
        mutableStateOf<ProductScreenState>(ProductScreenState.List)
    }

    var showCreateOrder by remember {
        mutableStateOf(false)
    }

    val authViewModel = viewModel<AuthViewModel>(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(repository)
            }
        }
    )

    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authViewModel) {
        authViewModel.restoreSession()
    }

    GanyemTheme {
        when {
            authState.isLoading -> {
                LaunchScreen()
            }

            authState.tokens == null -> {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = { appTokens ->
                        authViewModel.onLoginSuccess(appTokens)
                    }
                )
            }

            showCreateOrder -> {
                OrderScreen(
                    onBack = { showCreateOrder = false },
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
                        when (val state = productScreenState) {
                            ProductScreenState.List -> {
                                ProductListScreen(
                                    repository = productRepository,
                                    onAddProduct = {
                                        productScreenState = ProductScreenState.Add
                                    },
                                    onEditProduct = { product ->
                                        productScreenState = ProductScreenState.Edit(product)
                                    }
                                )
                            }
                            ProductScreenState.Add -> {
                                AddProductScreen(
                                    productApi = productApi,
                                    onBack = {
                                        productScreenState = ProductScreenState.List
                                    }
                                )
                            }
                            is ProductScreenState.Edit -> {
                                EditProductScreen(
                                    product = state.product,
                                    repository = productRepository,
                                    onBack = {
                                        productScreenState = ProductScreenState.List
                                    }
                                )
                            }
                        }
                    },
                    othersContent = {
                        OthersScreen(
                            customerRepository = customerRepository,
                            onLogout = { authViewModel.logout() }
                        )
                    }
                )

            }
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

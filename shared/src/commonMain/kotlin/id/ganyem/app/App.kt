package id.ganyem.app

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductApi
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.ui.LoginScreen
import id.ganyem.app.ui.product.AddProductScreen
import id.ganyem.app.ui.product.EditProductScreen
import id.ganyem.app.ui.product.ProductListScreen

@Composable
fun App(
    repository: AuthRepository,
    productRepository: ProductRepository,
    productApi: ProductApi
) {
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

        else -> {
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
        }
    }
}
package id.ganyem.app.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductRepository

@Composable
fun ProductListScreen(
    repository: ProductRepository,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit
) {
    val vm: ProductListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ProductListViewModel(repository)
            }
        }
    )

    var productToDelete by remember {
        mutableStateOf<Product?>(null)
    }

    val state by vm.uiState.collectAsState()

    LaunchedEffect(vm) {
        vm.loadProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daftar Produk",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = vm::loadProducts,
                enabled = !state.isLoading
            ) {
                Text("Refresh")
            }
        }

        Button(
            onClick = onAddProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Produk")
        }

        Spacer(Modifier.height(16.dp))

        if (state.isLoading && state.products.isEmpty()) {
            CircularProgressIndicator()
        }

        state.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

            TextButton(onClick = vm::loadProducts) {
                Text("Coba lagi")
            }
        }

        if (!state.isLoading &&
            state.error == null &&
            state.products.isEmpty()
        ) {
            Text("Belum ada produk.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.products,
                key = { it.id }
            ) { product ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            product.productName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Harga jual: ${product.sellingPrice}")
                        Text("Harga awal: ${product.initialPrice}")
                        Text("Stok: ${product.quantity}")

                        Button(
                            onClick = { onEditProduct(product) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = {
                                productToDelete = product
                            },
                            enabled = state.deletingProductId != product.id,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.deletingProductId == product.id) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Hapus")
                            }
                        }

                    }
                }
            }
        }

        productToDelete?.let {product ->
            AlertDialog(
                onDismissRequest = {
                    if (state.deletingProductId == null) {
                        productToDelete = null
                    }
                },
                title = {
                    Text("Hapus product?")
                },
                text = {
                    Text(
                        "Produk \"${product.productName}\" akan dihapus. " +
                                "Tindakan ini mungkin tidak dapat dibatalkan."
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = state.deletingProductId == null,
                        onClick = {
                            vm.deleteProduct(product.id)
                            productToDelete = null
                        }
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = state.deletingProductId == null,
                        onClick = {
                            productToDelete = null
                        }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
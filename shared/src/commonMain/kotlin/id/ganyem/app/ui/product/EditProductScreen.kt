package id.ganyem.app.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductRepository

@Composable
fun EditProductScreen (
    product: Product,
    repository: ProductRepository,
    onBack: () -> Unit
) {
    val vm: EditProductViewModel = viewModel(
        key = "edit-product-${product.id}",
        factory = viewModelFactory {
            initializer {
                EditProductViewModel(
                    product = product,
                    repository = repository
                )
            }
        }
    )

    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Edit Product",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = state.productName,
            onValueChange = vm::onProductNameChange,
            label = { Text("Nama produk") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        OutlinedTextField(
            value = state.sellingPrice,
            onValueChange = vm::onSellingPriceChange,
            label = { Text("Harga jual") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        OutlinedTextField(
            value = state.initialPrice,
            onValueChange = vm::onInitialPriceChange,
            label = { Text("Harga awal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        state.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isSuccess) {
            Text(
                text = "Produk berhasil diperbarui.",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = vm::submit,
            enabled = !state.isLoading && !state.isSuccess,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Simpan Perubahan")
            }
        }

        TextButton(
            onClick = onBack,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali")
        }

        if (state.isSuccess) {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke Daftar Produk")
            }
        }
    }
}


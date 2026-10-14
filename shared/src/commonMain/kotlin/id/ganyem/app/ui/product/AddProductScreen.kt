package id.ganyem.app.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ganyem.app.product.ProductApi

@Composable
fun AddProductScreen(
    productApi: ProductApi,
    onBack: () -> Unit
) {

    val viewModel: AddProductViewModel = viewModel(
        factory = viewModelFactory {
            initializer { AddProductViewModel(productApi = productApi) }
        }
    )

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        Text(
            text = "Add Product",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = state.productName,
            onValueChange = viewModel::onProductNameChange,
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading

        )

        OutlinedTextField(
            value = state.sellingPrice,
            onValueChange = viewModel::onSellingPriceChange,
            label = { Text("Selling Price") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        OutlinedTextField(
            value = state.initialPrice,
            onValueChange = viewModel::onInitialPriceChange,
            label = { Text("Initial Price") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        OutlinedTextField(
            value = state.quantity,
            onValueChange = viewModel::onQuantityChange,
            label = { Text("Stock") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isSuccess) {
            Text(
                text = "Produk berhasil ditambahkan"
            )
        }

        Button(
            onClick = viewModel::submit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Simpan Product")
            }
        }

        TextButton(
            onClick = {
                viewModel.resetForm()
                onBack()
            },
            enabled = !state.isLoading
        ) {
            Text("Kembali")
        }
    }
}


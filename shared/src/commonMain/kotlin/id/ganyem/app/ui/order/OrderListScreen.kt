package id.ganyem.app.ui.order

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.ganyem.app.order.OrderRepository

@Composable
fun OrderListScreen(repository: OrderRepository) {
    val vm: OrderListViewModel = viewModel(factory = viewModelFactory {
        initializer { OrderListViewModel(repository) }
    })
    val state by vm.uiState.collectAsState()

    LaunchedEffect(vm) { vm.loadOrders() }

    Column(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Daftar Order", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { vm.loadOrders() }, enabled = !state.isLoading) { Text("Refresh") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.status,
            onValueChange = { vm.setFilter(OrderFilter.STATUS, it) },
            label = { Text("Status") },
            placeholder = { Text("Contoh: PENDING") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = state.orderId,
            onValueChange = { vm.setFilter(OrderFilter.ORDER_ID, it) },
            label = { Text("Order ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = state.invoiceId,
            onValueChange = { vm.setFilter(OrderFilter.INVOICE_ID, it) },
            label = { Text("Invoice ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.fromDate,
                onValueChange = { vm.setFilter(OrderFilter.FROM_DATE, it) },
                label = { Text("Dari tanggal") }, placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            OutlinedTextField(
                value = state.toDate,
                onValueChange = { vm.setFilter(OrderFilter.TO_DATE, it) },
                label = { Text("Sampai tanggal") }, placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
        }
        Button(onClick = vm::applyFilters, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) {
            Text("Terapkan Filter")
        }
        Spacer(Modifier.height(8.dp))
        if (state.isLoading && state.orders.isEmpty()) CircularProgressIndicator()
        state.error?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { vm.loadOrders() }) { Text("Coba lagi") }
        }
        if (!state.isLoading && state.error == null && state.orders.isEmpty()) Text("Belum ada order.")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.orders, key = { it.id }) { order ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(order.orderId, style = MaterialTheme.typography.titleMedium)
                        Text("Status: ${order.status}")
                        Text("Pelanggan: ${order.customerName} · ${order.customerPhoneNumber}")
                        Text("Alamat: ${order.customerAddress}")
                        Text("Produk: ${order.products.joinToString { "${it.productName} × ${it.quantity}" }}")
                        Text("Invoice: ${order.invoiceId}")
                        Text("Tanggal: ${order.invoiceIssuedDate}")
                        Text("Pengiriman: ${order.shippingName} · ${order.trackingNumber}")
                        Text("Total: ${order.totalPrice}", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = vm::previousPage, enabled = !state.isLoading && state.currentPage > 1) {
                Text("Sebelumnya")
            }
            Text("Halaman ${state.currentPage} / ${state.totalPage.coerceAtLeast(1)} · ${state.totalData} order")
            OutlinedButton(onClick = vm::nextPage, enabled = !state.isLoading && state.currentPage < state.totalPage) {
                Text("Berikutnya")
            }
        }
    }
}

package id.ganyem.app.ui.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import id.ganyem.app.customer.CustomerRepository
import id.ganyem.app.order.OrderRepository
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.shipping.ShippingRepository

@Composable
fun OrderScreen(
    onBack: () -> Unit,
    customerRepository: CustomerRepository,
    productRepository: ProductRepository,
    shippingRepository: ShippingRepository,
    orderRepository: OrderRepository
) {
    val customerViewModel: CustomerViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CustomerViewModel(customerRepository)
            }
        }
    )

    val orderViewModel: OrderViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                OrderViewModel(
                    productRepository,
                    orderRepository
                )
            }
        }
    )

    val shippingViewModel: ShippingViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ShippingViewModel(shippingRepository)
            }
        }
    )


    val customerUiState by customerViewModel.uiState.collectAsState()
    val orderUiState by orderViewModel.uiState.collectAsState()
    val shippingUiState by shippingViewModel.uiState.collectAsState()

    var newCustomerName by remember { mutableStateOf("") }
    var newCustomerPhone by remember { mutableStateOf("") }
    var showCreateCustomerForm by remember { mutableStateOf(false) }
    var customerAddress by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        customerViewModel.loadCustomers()
        orderViewModel.loadProducts()
        shippingViewModel.loadShipping()
    }

    LaunchedEffect(orderUiState.isOrderCreated) {
        if (orderUiState.isOrderCreated) {
            newCustomerName = ""
            newCustomerPhone = ""
            customerAddress = ""
            customerViewModel.clearSelectedCustomer()
            shippingViewModel.clearSelectedShipping()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Buat Order",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Pilih Customer",
            style = MaterialTheme.typography.titleMedium
        )

        customerUiState.selectedCustomer?.let { customer ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Customer Terpilih",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = customer.phoneNumber)

                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Alamat Pengiriman") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Button(
                        onClick = {
                            customerViewModel.clearSelectedCustomer()
                            customerAddress = ""
                        }
                    ) {
                        Text("Ganti Customer")
                    }
                }
            }
        }

        OutlinedTextField(
            value = customerUiState.query,
            onValueChange = { query ->
                customerViewModel.searchCustomers(query)
            },
            label = {
                Text("Cari nama atau nomor telepon")
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                showCreateCustomerForm = !showCreateCustomerForm
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (showCreateCustomerForm) {
                    "Batal Tambah Customer"
                } else {
                    "Tambah Customer Baru"
                }
            )
        }

        if (showCreateCustomerForm) {
            OutlinedTextField(
                value = newCustomerName,
                onValueChange = { newCustomerName = it },
                label = { Text("Nama Customer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = newCustomerPhone,
                onValueChange = { newCustomerPhone = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    customerViewModel.createCustomer(
                        name = newCustomerName,
                        phoneNumber = newCustomerPhone,
                        onSuccess = {
                            newCustomerName = ""
                            newCustomerPhone = ""
                            showCreateCustomerForm = false
                        }
                    )
                },
                enabled = !customerUiState.isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (customerUiState.isCreating) {
                        "Menyimpan..."
                    } else {
                        "Simpan Customer"
                    }
                )
            }
        }


        when {
            customerUiState.isLoading -> CircularProgressIndicator()

            customerUiState.error != null ->
                Text(
                    text = customerUiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )

            customerUiState.customers.isEmpty() -> Text("Belum ada customer.")

            else -> {
                customerUiState.customers.forEach { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                customerViewModel.selectCustomer(customer)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = customer.name,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(text = customer.phoneNumber)

                            if (customerUiState.selectedCustomer?.id == customer.id) {
                                Text(
                                    text = "Dipilih",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                if (customerUiState.currentPage < customerUiState.totalPage) {
                    Button(
                        onClick = {
                            customerViewModel.loadNextPage()
                        },
                        enabled = !customerUiState.isLoading
                    ) {
                        Text("Muat Customer Berikutnya")
                    }
                }
            }
        }

        Text(
            text = "Pilih Produk",
            style = MaterialTheme.typography.titleMedium
        )

        when {
            orderUiState.isLoadingProducts -> {
                CircularProgressIndicator()
            }

            orderUiState.error != null -> {
                Text(
                    text = orderUiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            orderUiState.products.isEmpty() -> {
                Text("Belum ada produk.")
            }

            else -> {
                orderUiState.products.forEach { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                orderViewModel.addProduct(product)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = product.productName,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(text = "Harga: Rp${product.sellingPrice}")
                            Text(text = "Stok: ${product.quantity}")
                        }
                    }
                }
            }
        }

        Text(
            text = "Produk dalam Order",
            style = MaterialTheme.typography.titleMedium
        )

        if (orderUiState.orderItems.isEmpty()) {
            Text("Belum ada produk yang dipilih.")
        } else {
            orderUiState.orderItems.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.product.productName,
                            style = MaterialTheme.typography.titleSmall
                        )

                        androidx.compose.foundation.layout.Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    orderViewModel.updateQuantity(
                                        item.product.id,
                                        item.quantity - 1
                                    )
                                },
                                enabled = item.quantity > 1
                            ) {
                                Text("−")
                            }

                            Text("Jumlah: ${item.quantity}")

                            Button(
                                onClick = {
                                    orderViewModel.updateQuantity(
                                        item.product.id,
                                        item.quantity + 1
                                    )
                                },
                                enabled = item.quantity < item.product.quantity
                            ) {
                                Text("+")
                            }
                        }

                        Text(
                            text = "Subtotal: Rp${item.product.sellingPrice * item.quantity}"
                        )

                        Button(
                            onClick = {
                                orderViewModel.removeProduct(item.product.id)
                            }
                        ) {
                            Text("Hapus Produk")
                        }
                    }
                }
            }
        }

        Text(
            text = "Pengiriman",
            style = MaterialTheme.typography.titleMedium
        )

        when {
            shippingUiState.isLoading -> {
                CircularProgressIndicator()
            }

            shippingUiState.error != null -> {
                Text(
                    text = shippingUiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            shippingUiState.shippingOptions.isEmpty() -> {
                Text("Belum ada metode pengiriman.")
            }

            else -> {
                shippingUiState.shippingOptions.forEach { shipping ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                shippingViewModel.selectShipping(shipping)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = shipping.name,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = if (shipping.requiresTracking) {
                                    "Ekspedisi"
                                } else {
                                    "Pengiriman mandiri atau pengambilan"
                                }
                            )

                            if (shippingUiState.selectedShipping?.id == shipping.id) {
                                Text(
                                    text = "Dipilih",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (shippingUiState.selectedShipping?.requiresTracking == true) {
            OutlinedTextField(
                value = shippingUiState.shippingCost,
                onValueChange = { value ->
                    shippingViewModel.updateShippingCost(value)
                },
                label = {
                    Text("Biaya Ekspedisi (Rp)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = shippingUiState.trackingNumber,
                onValueChange = { value ->
                    shippingViewModel.updateTrackingNumber(value)
                },
                label = {
                    Text("Tracking Number")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ringkasan Biaya",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Subtotal Produk: Rp${orderUiState.totalPrice}")

                Text(
                    text = "Pengiriman: ${
                        shippingUiState.selectedShipping?.name ?: "Belum dipilih"
                    }"
                )

                val shippingCost =
                    shippingUiState.shippingCost.toLongOrNull() ?: 0L

                Text("Biaya Pengiriman: Rp$shippingCost")

                HorizontalDivider()

                Text(
                    text = "Total: Rp${orderUiState.totalPrice + shippingCost}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(
            onClick = {
                customerViewModel.reset()
                shippingViewModel.reset()
                orderViewModel.reset()
                onBack()
            }
        ) {
            Text("Kembali")
        }

        Button(
            onClick = {
                orderViewModel.submitOrder(
                    customer = customerViewModel.uiState.value.selectedCustomer,
                    customerAddress = customerAddress,
                    shipping = shippingViewModel.uiState.value.selectedShipping,
                    shippingCost = shippingViewModel.uiState.value.shippingCost,
                    trackingNumber = shippingViewModel.uiState.value.trackingNumber
                )
            },
            enabled = !orderUiState.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (orderUiState.isSubmitting) {
                CircularProgressIndicator()
            } else {
                Text("Buat Order")
            }
        }

        if (orderUiState.isOrderCreated) {
            Text(
                text = "Order berhasil dibuat!",
                color = MaterialTheme.colorScheme.primary
            )
        }

        orderUiState.submitError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

    }
}
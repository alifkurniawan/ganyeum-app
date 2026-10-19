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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun OrderScreen(
    onBack: () -> Unit,
    customerRepository: CustomerRepository
) {
    val customerViewModel: CustomerViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CustomerViewModel(customerRepository)
            }
        }
    )

    val customerUiState by customerViewModel.uiState.collectAsState()
    var newCustomerName by remember { mutableStateOf("") }
    var newCustomerPhone by remember { mutableStateOf("") }
    var showCreateCustomerForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        customerViewModel.loadCustomers()
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
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

                    Button(
                        onClick = {
                            customerViewModel.clearSelectedCustomer()
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

        Button(
            onClick = onBack
        ) {
            Text("Kembali")
        }

    }
}
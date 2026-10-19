package id.ganyem.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.ganyem.app.customer.Customer
import id.ganyem.app.customer.CustomerRepository

private enum class OthersPage { MENU, REPORT, ACCOUNT, CUSTOMERS }

@Composable
fun OthersScreen(
    customerRepository: CustomerRepository,
    onLogout: () -> Unit
) {
    var page by remember { mutableStateOf(OthersPage.MENU) }

    when (page) {
        OthersPage.MENU -> OthersMenu(
            onReport = { page = OthersPage.REPORT },
            onAccount = { page = OthersPage.ACCOUNT },
            onCustomers = { page = OthersPage.CUSTOMERS },
            onLogout = onLogout
        )
        OthersPage.REPORT -> SimplePage("Report", "Analisis penjualan, profit, dan tren") { page = OthersPage.MENU }
        OthersPage.ACCOUNT -> AccountPage(onBack = { page = OthersPage.MENU })
        OthersPage.CUSTOMERS -> CustomersPage(customerRepository, onBack = { page = OthersPage.MENU })
    }
}

@Composable
private fun OthersMenu(
    onReport: () -> Unit,
    onAccount: () -> Unit,
    onCustomers: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Others", style = MaterialTheme.typography.headlineMedium)
        Text("Kelola laporan dan akun bisnis Anda", color = MaterialTheme.colorScheme.onSurfaceVariant)
        MenuCard("Report", "Analisis penjualan, profit, dan tren", onReport)
        MenuCard("Account", "Atur nama, deskripsi, dan foto profil", onAccount)
        MenuCard("Customers", "Lihat daftar existing customer", onCustomers)
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Sign out") }
    }
}

@Composable
private fun MenuCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SimplePage(title: String, description: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onBack) { Text("Kembali") }
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(description, style = MaterialTheme.typography.bodyLarge)
        Text("Laporan akan ditampilkan di sini.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AccountPage(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onBack) { Text("Kembali") }
        Text("Account", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(name, { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        OutlinedTextField(photo, { photo = it }, label = { Text("URL foto profil") }, modifier = Modifier.fillMaxWidth())
        Text("Perubahan akun belum tersimpan ke server.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CustomersPage(repository: CustomerRepository, onBack: () -> Unit) {
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(repository) {
        try {
            customers = repository.getCustomer().data
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat customer."
        } finally {
            loading = false
        }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onBack) { Text("Kembali") }
        Text("Customers", style = MaterialTheme.typography.headlineMedium)
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            customers.isEmpty() -> Text("Belum ada customer.")
            else -> customers.forEach { customer ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(customer.name, style = MaterialTheme.typography.titleMedium)
                            Text(customer.phoneNumber, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

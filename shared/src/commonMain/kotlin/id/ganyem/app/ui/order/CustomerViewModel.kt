package id.ganyem.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ganyem.app.customer.CreateCustomerRequest
import id.ganyem.app.customer.Customer
import id.ganyem.app.customer.CustomerRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerUiState(
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val query: String = "",
    val currentPage: Int = 1,
    val totalPage: Int = 0,
    val totalData: Int = 0,
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null
)

class CustomerViewModel (
    private val repository: CustomerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCustomers(
        query: String = _uiState.value.query,
        page: Int = 1,
        append: Boolean = false
    ) {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                query = query,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val response = repository.getCustomer(query, page)
                _uiState.update {
                    it.copy(
                        customers = if (append) {
                            it.customers + response.data
                        } else {
                            response.data
                        },
                        currentPage = response.currentPage,
                        totalPage = response.totalPage,
                        totalData = response.totalData,
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = null
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    fun searchCustomers(query: String) {
        _uiState.update { it.copy(query = query) }
        loadCustomers(query = query, page = 1)
    }

    fun selectCustomer(customer: Customer) {
        _uiState.update {
            it.copy(
                selectedCustomer = customer,
                error = null
            )
        }
    }

    fun clearSelectedCustomer() {
        _uiState.update {
            it.copy(selectedCustomer = null)
        }
    }

    fun loadNextPage() {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.currentPage >= state.totalPage
        ) {
            return
        }

        loadCustomers(
            query = state.query,
            page = state.currentPage + 1,
            append = true
        )
    }

    fun createCustomer(
        name: String,
        phoneNumber: String,
        onSuccess: (Customer) -> Unit
    ) {
        if (_uiState.value.isCreating) return

        val normalizedName = name.trim()
        val normalizedPhone = phoneNumber.trim()

        if (normalizedName.isBlank() || normalizedPhone.isBlank()) {
            _uiState.update {
                it.copy(error = "Nama dan nomor telepon wajib diisi.")
            }
            return
        }

        _uiState.update {
            it.copy(isCreating = true, error = null)
        }

        viewModelScope.launch {
            try {
                repository.createCustomer(
                    CreateCustomerRequest(
                        name = normalizedName,
                        phoneNumber = normalizedPhone
                    )
                )

                val response = repository.getCustomer(
                    query = normalizedPhone,
                    page = 1
                )

                val createdCustomer = response.data.firstOrNull {
                    it.phoneNumber == normalizedPhone
                } ?: error(
                    "Customer berhasil dibuat, tetapi tidak ditemukan pada hasil pencarian."
                )

                _uiState.update {
                    it.copy(
                        selectedCustomer = createdCustomer,
                        customers = listOf(createdCustomer) +
                                it.customers.filterNot {
                                        customer -> customer.id == createdCustomer.id
                                }
                    )
                }

                onSuccess(createdCustomer)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Gagal membuat customer."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isCreating = false)
                }
            }
        }
    }

}
package id.ganyem.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.order.Order
import id.ganyem.app.order.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderListUiState(
    val orders: List<Order> = emptyList(),
    val currentPage: Int = 1,
    val totalPage: Int = 0,
    val totalData: Int = 0,
    val status: String = "",
    val orderId: String = "",
    val toDate: String = "",
    val fromDate: String = "",
    val invoiceId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrderListViewModel(private val repository: OrderRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState = _uiState.asStateFlow()

    fun setFilter(field: OrderFilter, value: String) {
        _uiState.update { state ->
            when (field) {
                OrderFilter.STATUS -> state.copy(status = value)
                OrderFilter.ORDER_ID -> state.copy(orderId = value)
                OrderFilter.TO_DATE -> state.copy(toDate = value)
                OrderFilter.FROM_DATE -> state.copy(fromDate = value)
                OrderFilter.INVOICE_ID -> state.copy(invoiceId = value)
            }
        }
    }

    fun applyFilters() = loadOrders(page = 1)
    fun nextPage() = loadOrders(page = _uiState.value.currentPage + 1)
    fun previousPage() = loadOrders(page = (_uiState.value.currentPage - 1).coerceAtLeast(1))

    fun loadOrders(page: Int = _uiState.value.currentPage) {
        if (_uiState.value.isLoading) return
        val filters = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = repository.getOrders(
                    status = filters.status,
                    page = page,
                    orderId = filters.orderId,
                    toDate = filters.toDate,
                    fromDate = filters.fromDate,
                    invoiceId = filters.invoiceId
                )
                _uiState.update {
                    it.copy(orders = response.data, currentPage = response.currentPage,
                        totalPage = response.totalPage, totalData = response.totalData)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal mengambil order") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

enum class OrderFilter { STATUS, ORDER_ID, TO_DATE, FROM_DATE, INVOICE_ID }

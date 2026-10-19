package id.ganyem.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.order.CreateOrderItemRequest
import id.ganyem.app.order.CreateOrderRequest
import id.ganyem.app.order.OrderRepository
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderUiState(
    val products: List<Product> = emptyList(),
    val orderItems: List<OrderItem> = emptyList(),
    val totalPrice: Long = 0L,
    val isLoadingProducts: Boolean = false,
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val isOrderCreated: Boolean = false,
    val submitError: String? = null
)

class OrderViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProducts() {
        if (_uiState.value.isLoadingProducts) return

        _uiState.update {
            it.copy(isLoadingProducts = true, error = null)
        }

        viewModelScope.launch {
            try {
                val response = productRepository.getProducts()

                _uiState.update {
                    it.copy(products = response.data)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Gagal mengambil produk."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoadingProducts = false)
                }
            }
        }
    }

    fun addProduct(product: Product) {
        val currentItems = _uiState.value.orderItems
        val existingItem = currentItems.find {
            it.product.id == product.id
        }

        if (existingItem != null) {
            return
        }

        if (product.quantity <= 0) {
            _uiState.update {
                it.copy(error = "Stok produk habis.")
            }
            return
        }

        _uiState.update {
            it.copy(
                orderItems = it.orderItems + OrderItem(
                    product = product,
                    quantity = 1
                ),
                error = null
            )
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val item = _uiState.value.orderItems.find {
            it.product.id == productId
        } ?: return

        if (newQuantity < 1 || newQuantity > item.product.quantity) {
            return
        }

        _uiState.update { state ->
            val orderItems = state.orderItems.map { orderItem ->
                if (orderItem.product.id == productId) {
                    orderItem.copy(quantity = newQuantity)
                } else {
                    orderItem
                }
            }

            state.copy(
                orderItems = orderItems,
                totalPrice = calculateTotal(orderItems)
            )
        }


    }

    private fun calculateTotal(items: List<OrderItem>): Long {
        return items.sumOf { item ->
            item.product.sellingPrice * item.quantity
        }
    }

    fun removeProduct(productId: String) {
        _uiState.update { state ->
            val updatedItems = state.orderItems.filterNot {
                it.product.id == productId
            }

            state.copy(
                orderItems = updatedItems,
                totalPrice = calculateTotal(updatedItems),
                error = null
            )
        }
    }

    fun submitOrder(
        customer: id.ganyem.app.customer.Customer?,
        customerAddress: String,
        shipping: id.ganyem.app.shipping.Shipping?,
        shippingCost: String,
        trackingNumber: String,
    ) {
        val state = _uiState.value

        if (state.isSubmitting) return

        if (customer == null) {
            _uiState.update {
                it.copy(submitError = "Pilih customer terlebih dahulu.")
            }
            return
        }

        if (customerAddress.isBlank()) {
            _uiState.update {
                it.copy(submitError = "Alamat pengiriman wajib diisi.")
            }
            return
        }

        if (state.orderItems.isEmpty()) {
            _uiState.update {
                it.copy(submitError = "Tambahkan minimal satu produk.")
            }
            return
        }

        if (shipping == null) {
            _uiState.update {
                it.copy(submitError = "Pilih metode pengiriman.")
            }
            return
        }

        val cost = if (shipping.requiresTracking) {
            shippingCost.toLongOrNull()
        } else {
            0L
        }

        if (cost == null || cost < 0L) {
            _uiState.update {
                it.copy(submitError = "Masukkan biaya ekspedisi yang valid.")
            }
            return
        }

        val request = CreateOrderRequest(
            customerId = customer.id,
            customerName = customer.name,
            customerPhoneNumber = customer.phoneNumber,
            customerAddress = customerAddress.trim(),
            items = state.orderItems.map { item ->
                CreateOrderItemRequest(
                    productId = item.product.id,
                    quantity = item.quantity
                )
            },
            shippingId = shipping.id,
            shippingCost = cost,
            trackingNumber = trackingNumber
        )

        _uiState.update {
            it.copy(
                isSubmitting = true,
                isOrderCreated = false,
                submitError = null
            )
        }

        viewModelScope.launch {
            try {
                orderRepository.createOrder(request)
                _uiState.update {
                    it.copy(
                        totalPrice = 0L,
                        orderItems = emptyList(),
                        isOrderCreated = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        submitError = e.message ?: "Gagal membuat order."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isSubmitting = false)
                }
            }
        }
    }

    fun reset() {
        _uiState.update {
            OrderUiState()
        }
    }
}
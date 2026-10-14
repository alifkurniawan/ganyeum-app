package id.ganyem.app.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.product.CreateProductRequest
import id.ganyem.app.product.ProductCreator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddProductUiState(
    val productName: String = "",
    val sellingPrice: String = "",
    val initialPrice: String = "",
    val quantity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AddProductViewModel (
    private val productApi: ProductCreator
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AddProductUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun onProductNameChange(value: String) {
        updateForm { copy(productName = value) }
    }

    fun onSellingPriceChange(value: String) {
        updateForm { copy(sellingPrice = value) }
    }

    fun onInitialPriceChange(value: String) {
        updateForm { copy(initialPrice = value) }
    }

    fun onQuantityChange(value: String) {
        updateForm { copy(quantity = value) }
    }

    private fun updateForm(
        transform: AddProductUiState.() -> AddProductUiState
    ) {
        _uiState.update {
            it.transform().copy(
                error = null,
                isSuccess = false
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        val name = state.productName.trim()
        val price = state.sellingPrice.toLongOrNull()
        val cost = state.initialPrice.toLongOrNull()
        val stock = state.quantity.toIntOrNull()

        val validationError = when {
            name.isBlank() -> "Nama produk wajib diisi"
            price == null || price < 0 -> "Invalid price"
            cost == null || cost < 0 -> "invalid cost"
            stock == null || stock < 0 -> "invalid stock"
            else -> null
        }

        if (validationError != null) {
            _uiState.update {
                it.copy(
                    error = validationError,
                    isSuccess = false
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )
        }

        viewModelScope.launch {
            try {
                productApi.createProduct(
                    CreateProductRequest(
                        productName = name,
                        sellingPrice = price!!,
                        initialPrice = cost!!,
                        quantity = stock!!
                    )
                )
                _uiState.update {
                    it.copy(isSuccess = true)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message
                            ?: "Gagal menyimpan produk"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }


        }

    }

    fun resetForm() {
        _uiState.value = AddProductUiState()
    }
}

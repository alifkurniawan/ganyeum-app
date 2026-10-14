package id.ganyem.app.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductRepository
import id.ganyem.app.product.UpdateProductRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProductUiState(
    val productName: String = "",
    val sellingPrice: String = "",
    val initialPrice: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class EditProductViewModel (
    private val product: Product,
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProductUiState(
        productName = product.productName,
        sellingPrice = product.sellingPrice.toString(),
        initialPrice = product.initialPrice.toString()
    ))

    val uiState = _uiState.asStateFlow()

    fun onProductNameChange(value: String) {
        _uiState.update {
            it.copy(
                productName = value,
                error = null,
                isSuccess = false,
            )
        }
    }

    fun onSellingPriceChange(value: String) {
        _uiState.update {
            it.copy(
                sellingPrice = value,
                error = null,
                isSuccess = false
            )
        }
    }

    fun onInitialPriceChange(value: String) {
        _uiState.update {
            it.copy(
                initialPrice = value,
                error = null,
                isSuccess = false
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        val name = state.productName.trim()
        val cost = state.initialPrice.toLongOrNull()
        val price = state.sellingPrice.toLongOrNull()

        val validationError = when {
            name.isBlank() ->
                "Nama produk wajib diisi."

            price == null || price < 0 ->
                "Harga jual harus berupa bilangan bulat nonnegatif."

            cost == null || cost < 0 ->
                "Harga awal harus berupa bilangan bulat nonnegatif."
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

        val validPrice = price ?: return
        val validCost = cost ?: return

        _uiState.update {
            it.copy(isLoading = true, error = null, isSuccess = false)
        }

        viewModelScope.launch {
            try {
                repository.updateProduct(
                    id = product.id,
                    request = UpdateProductRequest(
                        productName = name,
                        initialPrice = validCost,
                        sellingPrice = validPrice
                    )
                )
                _uiState.update {
                    it.copy(isSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message
                        ?: "Gagal memperbaharui produk")
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}

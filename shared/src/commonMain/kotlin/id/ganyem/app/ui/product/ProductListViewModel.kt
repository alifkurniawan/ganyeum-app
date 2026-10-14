package id.ganyem.app.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val currentPage: Int = 1,
    val totalPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deletingProductId: String? = null
)

class ProductListViewModel (
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProducts() {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                _uiState.update {
                    it.copy(
                        products = response.data,
                        currentPage = response.currentPage,
                        totalPage = response.totalPage
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message
                            ?: "Gagal mengambil produk"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun deleteProduct(id: String) {
        if (_uiState.value.deletingProductId != null) return

        _uiState.update {
            it.copy(
                deletingProductId = id
            )
        }

        viewModelScope.launch {
            try {
                repository.deleteProduct(id)
                _uiState.update {
                    it.copy(products = it.products.filterNot { product -> product.id == id })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message?: "Gagal menghapus product."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(deletingProductId = null)
                }
            }
        }
    }

}
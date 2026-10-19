package id.ganyem.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.shipping.Shipping
import id.ganyem.app.shipping.ShippingRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShippingUiState(
    val shippingOptions: List<Shipping> = emptyList(),
    val selectedShipping: Shipping? = null,
    val shippingCost: String = "",
    val trackingNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ShippingViewModel(
    private val repository: ShippingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShippingUiState())
    val uiState = _uiState.asStateFlow()

    fun loadShipping() {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                val response = repository.getShipping()

                _uiState.update {
                    it.copy(shippingOptions = response.data)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Gagal mengambil daftar shipping."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun selectShipping(shipping: Shipping) {
        _uiState.update {
            it.copy(
                selectedShipping = shipping,
                shippingCost = if (shipping.requiresTracking) it.shippingCost else "0",
                error = null,
                trackingNumber = ""
            )
        }
    }

    fun updateShippingCost(value: String) {
        if (value.isNotEmpty() && value.any { !it.isDigit() }) return

        _uiState.update {
            it.copy(shippingCost = value, error = null)
        }
    }

    fun updateTrackingNumber(value: String) {
        _uiState.update {
            it.copy(trackingNumber = value, error = null)
        }
    }

    fun reset() {
        _uiState.update {
            ShippingUiState()
        }
    }

    fun clearSelectedShipping() {
        _uiState.update {
            it.copy(
                trackingNumber = "",
                shippingCost = "",
                selectedShipping = null
            )
        }
    }
}
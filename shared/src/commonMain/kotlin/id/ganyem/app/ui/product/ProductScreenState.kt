package id.ganyem.app.ui.product

import id.ganyem.app.product.Product

sealed interface ProductScreenState {
    data object List : ProductScreenState
    data object Add : ProductScreenState
    data class Edit(val product: Product) : ProductScreenState
}

package id.ganyem.app.ui.order

import id.ganyem.app.product.Product

data class OrderItem(
    val product: Product,
    val quantity: Int = 1
)
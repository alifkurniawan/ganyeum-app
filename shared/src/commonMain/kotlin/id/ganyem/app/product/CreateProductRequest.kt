package id.ganyem.app.product

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val productName: String,
    val sellingPrice: Long,
    val initialPrice: Long,
    val quantity: Int
)

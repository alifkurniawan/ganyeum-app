package id.ganyem.app.shipping

import kotlinx.serialization.Serializable

@Serializable
data class Shipping(
    val id: Int,
    val name: String,
    val requiresTracking: Boolean
)

@Serializable
data class ShippingListResponse(
    val data: List<Shipping>,
    val currentPage: Int,
    val totalPage: Int,
    val totalData: Int
)
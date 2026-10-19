package id.ganyem.app.order

import kotlinx.serialization.Serializable

@Serializable
data class Order (
    val id: String
)
@Serializable
data class OrderListResponse (
    val data: List<Order>,
    val currentPage: Int,
    val totalPage: Int,
    val totalData: Int

) {
}

@Serializable
data class CreateOrderRequest(
    val customerId: String,
    val customerName: String,
    val customerPhoneNumber: String,
    val customerAddress: String,
    val items: List<CreateOrderItemRequest>,
    val shippingId: Int,
    val shippingCost: Long,
    val trackingNumber: String
)

@Serializable
data class CreateOrderItemRequest(
    val productId: String,
    val quantity: Int
)
package id.ganyem.app.order

import kotlinx.serialization.Serializable

@Serializable
data class Order (
    val id: String,
    val orderId: String = "",
    val status: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhoneNumber: String = "",
    val customerAddress: String = "",
    val products: List<OrderProduct> = emptyList(),
    val subtotal: Long = 0,
    val totalInitialCost: Long = 0,
    val profit: Long = 0,
    val shippingCost: Long = 0,
    val totalPrice: Long = 0,
    val shippingId: Int = 0,
    val shippingName: String = "",
    val trackingNumber: String = "",
    val invoiceId: String = "",
    val invoiceIssuedDate: String = ""
)

@Serializable
data class OrderProduct(
    val productId: String,
    val productName: String,
    val pricePerItem: Long,
    val initialCostPerItem: Long,
    val quantity: Int,
    val totalPrice: Long,
    val totalInitialCost: Long
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

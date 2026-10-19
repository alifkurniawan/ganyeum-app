package id.ganyem.app.order

interface OrderRepository {
    suspend fun getOrders(
        status: String? = null,
        page: Int = 1,
        orderId: String? = null,
        toDate: String? = null,
        fromDate: String? = null,
        invoiceId: String? = null
    ): OrderListResponse
    suspend fun createOrder(request: CreateOrderRequest)
}

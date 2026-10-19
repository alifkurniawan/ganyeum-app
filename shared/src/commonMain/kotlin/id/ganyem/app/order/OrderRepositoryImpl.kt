package id.ganyem.app.order

class OrderRepositoryImpl (
    private val orderApi: OrderApi
) : OrderRepository {
    override suspend fun getOrders(
        status: String?,
        page: Int,
        orderId: String?,
        toDate: String?,
        fromDate: String?,
        invoiceId: String?
    ): OrderListResponse {
        return orderApi.getOrder(status, page, orderId, toDate, fromDate, invoiceId)
    }

    override suspend fun createOrder(request: CreateOrderRequest) {
        orderApi.createOrder(request)
    }
}

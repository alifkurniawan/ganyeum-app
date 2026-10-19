package id.ganyem.app.order

class OrderRepositoryImpl (
    private val orderApi: OrderApi
) : OrderRepository {
    override suspend fun getOrder(
        query: String,
        page: Int
    ): OrderListResponse {
        return orderApi.getOrder(query, page)
    }

    override suspend fun createOrder(request: CreateOrderRequest) {
        orderApi.createOrder(request)
    }
}
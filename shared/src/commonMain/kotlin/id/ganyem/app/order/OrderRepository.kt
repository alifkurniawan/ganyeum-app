package id.ganyem.app.order

interface OrderRepository {
    suspend fun getOrder(query: String = "", page: Int = 1): OrderListResponse
    suspend fun createOrder(request: CreateOrderRequest)
}
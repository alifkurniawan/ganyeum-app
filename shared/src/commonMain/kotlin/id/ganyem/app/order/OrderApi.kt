package id.ganyem.app.order

import id.ganyem.app.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess


class OrderApi (
    private val apiClient: ApiClient
) {
    suspend fun createOrder(
        request: CreateOrderRequest
    ) {
        val response = apiClient.post("/orders") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal membuat order: HTTP ${response.status.value}"
            )
        }
    }

    suspend fun getOrder(
        status: String? = null,
        page: Int = 1,
        orderId: String? = null,
        toDate: String? = null,
        fromDate: String? = null,
        invoiceId: String? = null
    ): OrderListResponse {
        val response = apiClient.get("/orders") {
            url {
                parameters.append("page", page.toString())
                parameters.append("size", "50")
                status?.takeIf { it.isNotBlank() }?.let { parameters.append("status", it) }
                orderId?.takeIf { it.isNotBlank() }?.let { parameters.append("orderId", it) }
                toDate?.takeIf { it.isNotBlank() }?.let { parameters.append("toDate", it) }
                fromDate?.takeIf { it.isNotBlank() }?.let { parameters.append("fromDate", it) }
                invoiceId?.takeIf { it.isNotBlank() }?.let { parameters.append("invoiceId", it) }
            }
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal mengambil order: HTTP ${response.status.value}"
            )
        }
        val result = response.body<OrderListResponse>()
        return result
    }
}

package id.ganyem.app.shipping

import id.ganyem.app.network.ApiClient
import io.ktor.client.call.body
import io.ktor.http.isSuccess

class ShippingApi(
    private val apiClient: ApiClient
) {
    suspend fun getShipping(): ShippingListResponse {
        val response = apiClient.get("/shipping")

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal mengambil daftar shipping: HTTP ${response.status.value}"
            )
        }

        return response.body<ShippingListResponse>()
    }
}
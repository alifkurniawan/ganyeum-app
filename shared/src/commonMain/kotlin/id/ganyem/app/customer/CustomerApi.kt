package id.ganyem.app.customer

import id.ganyem.app.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess


class CustomerApi (
    private val apiClient: ApiClient
) {
    suspend fun createCustomer(
        request: CreateCustomerRequest
    ) {
        val response = apiClient.post("/customers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal membuat custimer: HTTP ${response.status.value}"
            )
        }
    }

    suspend fun getCustomer(query: String = "", page: Int = 1): CustomerListResponse {
        val response = apiClient.get("/customers") {
            url {
                parameters.append("query", query)
                parameters.append("page", page.toString())
            }
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal mengambil customer: HTTP ${response.status.value}"
            )
        }
        val result = response.body<CustomerListResponse>()
        return result
    }
}

package id.ganyem.app.product

import id.ganyem.app.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

fun interface ProductCreator {
    suspend fun createProduct(request: CreateProductRequest)
}

class ProductApi (
    private val apiClient: ApiClient
) : ProductCreator {
    override suspend fun createProduct(
        request: CreateProductRequest
    ) {
        val response = apiClient.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal membuat product: HTTP ${response.status.value}"
            )
        }
    }

    suspend fun updateProduct(
        id: String,
        request: UpdateProductRequest
    ) {
        if (id.isBlank()) {
            throw IllegalStateException(
                "ID is invalid"
            )
        }

        val response = apiClient.put("/products/${id}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal membuat product: HTTP ${response.status.value}"
            )
        }
    }

    suspend fun getProducts(): ProductListResponse {
        val response = apiClient.get("/products")
        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal mengambil product: HTTP ${response.status.value}"
            )
        }
        val result = response.body<ProductListResponse>()
        return result
    }

    suspend fun deleteProduct(id: String) {
        val response = apiClient.delete("/products/${id}")
        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Gagal mengambil product: HTTP ${response.status.value}"
            )
        }
    }
}

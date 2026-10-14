package id.ganyem.app.product

class ProductRepository (
    private val productApi: ProductApi
) {
    suspend fun getProducts(): ProductListResponse {
        return productApi.getProducts()
    }

    suspend fun updateProduct(id: String, request: UpdateProductRequest) {
        productApi.updateProduct(id, request)
    }

    suspend fun createProduct(request: CreateProductRequest) {
        productApi.createProduct(request)
    }

    suspend fun deleteProduct(id: String) {
        productApi.deleteProduct(id)
    }
}
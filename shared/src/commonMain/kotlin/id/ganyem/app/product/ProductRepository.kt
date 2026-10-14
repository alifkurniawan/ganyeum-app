package id.ganyem.app.product

interface ProductRepository {
    suspend fun getProducts(): ProductListResponse
    suspend fun updateProduct(id: String, request: UpdateProductRequest)
    suspend fun createProduct(request: CreateProductRequest)

    suspend fun deleteProduct(id: String)
}
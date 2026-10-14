package id.ganyem.app.product

class DefaultProductRepository (
    private val productApi: ProductApi
) : ProductRepository {
    override suspend fun getProducts(): ProductListResponse {
        return productApi.getProducts()
    }

    override suspend fun updateProduct(id: String, request: UpdateProductRequest) {
        productApi.updateProduct(id, request)
    }

    override suspend fun createProduct(request: CreateProductRequest) {
        productApi.createProduct(request)
    }

    override suspend fun deleteProduct(id: String) {
        productApi.deleteProduct(id)
    }
}
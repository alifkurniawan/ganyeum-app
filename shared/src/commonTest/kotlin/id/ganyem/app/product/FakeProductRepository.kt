package id.ganyem.app.product

class FakeProductRepository : ProductRepository {
    var getProductsCalls = 0
    val deletedProductIds = mutableListOf<String>()
    val updatedProductRequests = mutableListOf<Pair<String, UpdateProductRequest>>()
    var beforeUpdate: suspend () -> Unit = {}
    var productListResponse = ProductListResponse(
        data = emptyList<Product>(),
        currentPage = 1,
        totalPage= 1,
        totalData = 0
    )
    var shouldFail = false

    override suspend fun getProducts(): ProductListResponse {
        getProductsCalls++
        if (shouldFail) {
            throw IllegalStateException("Simulated API failure")
        }
        return productListResponse
    }

    override suspend fun updateProduct(
        id: String,
        request: UpdateProductRequest
    ) {
        beforeUpdate()
        updatedProductRequests += id to request
        if (shouldFail) {
            throw IllegalStateException("Simulated API failure")
        }
    }

    override suspend fun createProduct(request: CreateProductRequest) {
        if (shouldFail) {
            throw IllegalStateException("Simulated API failure")
        }
    }

    override suspend fun deleteProduct(id: String) {
        deletedProductIds += id
        if (shouldFail) {
            throw IllegalStateException("Simulated API failure")
        }
    }
}

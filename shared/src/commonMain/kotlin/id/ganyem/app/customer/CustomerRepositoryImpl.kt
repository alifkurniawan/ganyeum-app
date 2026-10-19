package id.ganyem.app.customer

class CustomerRepositoryImpl (
    private val customerApi: CustomerApi
) : CustomerRepository{
    override suspend fun getCustomer(
        query: String,
        page: Int
    ): CustomerListResponse {
        return customerApi.getCustomer(query, page)
    }

    override suspend fun createCustomer(request: CreateCustomerRequest) {
        customerApi.createCustomer(request)
    }
}
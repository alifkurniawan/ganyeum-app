package id.ganyem.app.customer

interface CustomerRepository {
    suspend fun getCustomer(query: String = "", page: Int = 1): CustomerListResponse
    suspend fun createCustomer(request: CreateCustomerRequest)
}
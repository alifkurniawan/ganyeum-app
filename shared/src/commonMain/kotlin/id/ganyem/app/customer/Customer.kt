package id.ganyem.app.customer

import kotlinx.serialization.Serializable

@Serializable
data class Customer (
    val id: String,
    val name: String,
    val phoneNumber: String
) {
}

@Serializable
data class CreateCustomerRequest(
    val name: String,
    val phoneNumber: String
)

@Serializable
data class CustomerListResponse (
    val data: List<Customer>,
    val currentPage: Int,
    val totalPage: Int,
    val totalData: Int

) {
}
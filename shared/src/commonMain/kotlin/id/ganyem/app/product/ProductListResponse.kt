package id.ganyem.app.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse (
    val data: List<Product>,
    val currentPage: Int,
    val totalPage: Int,
    val totalData: Int

) {
}
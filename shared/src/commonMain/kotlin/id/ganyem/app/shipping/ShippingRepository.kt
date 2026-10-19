package id.ganyem.app.shipping

interface ShippingRepository {
    suspend fun getShipping(): ShippingListResponse
}
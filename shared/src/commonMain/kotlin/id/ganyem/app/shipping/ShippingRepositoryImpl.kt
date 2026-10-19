package id.ganyem.app.shipping

class ShippingRepositoryImpl (
    private val shippingApi: ShippingApi
) : ShippingRepository{
    override suspend fun getShipping(): ShippingListResponse {
        return shippingApi.getShipping()
    }


}
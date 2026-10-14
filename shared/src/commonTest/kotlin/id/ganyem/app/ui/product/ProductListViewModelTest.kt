package id.ganyem.app.ui.product

import id.ganyem.app.product.FakeProductRepository
import id.ganyem.app.product.Product
import id.ganyem.app.product.ProductListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.apply
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts returns products on success`() = runTest(dispatcher) {
        val repository = FakeProductRepository().apply {
            productListResponse = ProductListResponse(
                data = listOf(
                    Product(
                        id = "1L",
                        productName = "Kopi",
                        sellingPrice = 20000L,
                        initialPrice = 12000L,
                        quantity = 10
                    )
                ),
                currentPage = 1,
                totalPage = 1,
                totalData = 1
            )
        }

        val viewModel = ProductListViewModel(repository)
        viewModel.loadProducts()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.products.size)
        assertEquals(
            "Kopi",
            viewModel.uiState.value.products.first().productName
        )
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadProducts exposes error on failure`() = runTest(dispatcher) {
        val repository = FakeProductRepository().apply {
            shouldFail = true
        }

        val viewModel = ProductListViewModel(repository)

        viewModel.loadProducts()
        advanceUntilIdle()

        assertEquals(
            "Simulated API failure",
            viewModel.uiState.value.error
        )
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadProducts updates pagination metadata`() = runTest(dispatcher) {
        val repository = FakeProductRepository().apply {
            productListResponse = ProductListResponse(
                data = emptyList(),
                currentPage = 3,
                totalPage = 5,
                totalData = 42
            )
        }
        val viewModel = ProductListViewModel(repository)

        viewModel.loadProducts()
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.currentPage)
        assertEquals(5, viewModel.uiState.value.totalPage)
    }

    @Test
    fun `loadProducts ignores a duplicate request while loading`() = runTest(dispatcher) {
        val repository = FakeProductRepository()
        val viewModel = ProductListViewModel(repository)

        viewModel.loadProducts()
        viewModel.loadProducts()
        advanceUntilIdle()

        assertEquals(1, repository.getProductsCalls)
    }

    @Test
    fun `deleteProduct removes product and clears deleting state`() = runTest(dispatcher) {
        val product = sampleProduct("1")
        val repository = FakeProductRepository().apply {
            productListResponse = ProductListResponse(
                data = listOf(product, sampleProduct("2")),
                currentPage = 1,
                totalPage = 1,
                totalData = 2
            )
        }
        val viewModel = ProductListViewModel(repository)
        viewModel.loadProducts()
        advanceUntilIdle()

        viewModel.deleteProduct("1")
        assertEquals("1", viewModel.uiState.value.deletingProductId)
        advanceUntilIdle()

        assertEquals(listOf("2"), viewModel.uiState.value.products.map { it.id })
        assertEquals(listOf("1"), repository.deletedProductIds)
        assertNull(viewModel.uiState.value.deletingProductId)
    }

    @Test
    fun `deleteProduct exposes error and keeps product when API fails`() = runTest(dispatcher) {
        val product = sampleProduct("1")
        val repository = FakeProductRepository().apply {
            productListResponse = ProductListResponse(
                data = listOf(product),
                currentPage = 1,
                totalPage = 1,
                totalData = 1
            )
        }
        val viewModel = ProductListViewModel(repository)
        viewModel.loadProducts()
        advanceUntilIdle()
        repository.shouldFail = true

        viewModel.deleteProduct("1")
        advanceUntilIdle()

        assertEquals("Simulated API failure", viewModel.uiState.value.error)
        assertEquals(listOf(product), viewModel.uiState.value.products)
        assertNull(viewModel.uiState.value.deletingProductId)
    }

    @Test
    fun `deleteProduct ignores another delete while one is in progress`() = runTest(dispatcher) {
        val repository = FakeProductRepository()
        val viewModel = ProductListViewModel(repository)

        viewModel.deleteProduct("1")
        viewModel.deleteProduct("2")
        advanceUntilIdle()

        assertEquals(listOf("1"), repository.deletedProductIds)
        assertNull(viewModel.uiState.value.deletingProductId)
    }

    private fun sampleProduct(id: String) = Product(
        id = id,
        productName = "Produk $id",
        sellingPrice = 20000L,
        initialPrice = 12000L,
        quantity = 10
    )
}

package id.ganyem.app.ui.product

import id.ganyem.app.product.FakeProductRepository
import id.ganyem.app.product.Product
import id.ganyem.app.product.UpdateProductRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditProductViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `viewModel initializes fields from product`() = runTest(dispatcher) {
        val viewModel = EditProductViewModel(sampleProduct(), FakeProductRepository())

        assertEquals(
            EditProductUiState(
                productName = "Kopi",
                sellingPrice = "25000",
                initialPrice = "15000"
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `submit updates product and marks success`() = runTest(dispatcher) {
        val repository = FakeProductRepository()
        val viewModel = EditProductViewModel(sampleProduct(), repository)
        viewModel.onProductNameChange("  Kopi Arabika  ")
        viewModel.onSellingPriceChange("30000")
        viewModel.onInitialPriceChange("18000")

        viewModel.submit()
        advanceUntilIdle()

        assertEquals(
            listOf(
                "product-1" to UpdateProductRequest(
                    productName = "Kopi Arabika",
                    sellingPrice = 30000L,
                    initialPrice = 18000L
                )
            ),
            repository.updatedProductRequests
        )
        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `submit exposes repository failure`() = runTest(dispatcher) {
        val repository = FakeProductRepository().apply { shouldFail = true }
        val viewModel = EditProductViewModel(sampleProduct(), repository)

        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Simulated API failure", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `submit remains loading while repository update is pending`() = runTest(dispatcher) {
        val updateGate = CompletableDeferred<Unit>()
        val repository = FakeProductRepository().apply {
            beforeUpdate = { updateGate.await() }
        }
        val viewModel = EditProductViewModel(sampleProduct(), repository)

        viewModel.submit()
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)

        updateGate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `submit reports validation error without calling repository`() = runTest(dispatcher) {
        val repository = FakeProductRepository()
        val viewModel = EditProductViewModel(sampleProduct(), repository)
        viewModel.onSellingPriceChange("invalid")

        viewModel.submit()
        advanceUntilIdle()

        assertEquals(
            "Harga jual harus berupa bilangan bulat nonnegatif.",
            viewModel.uiState.value.error
        )
        assertTrue(repository.updatedProductRequests.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    private fun sampleProduct() = Product(
        id = "product-1",
        productName = "Kopi",
        sellingPrice = 25000L,
        initialPrice = 15000L,
        quantity = 8
    )
}

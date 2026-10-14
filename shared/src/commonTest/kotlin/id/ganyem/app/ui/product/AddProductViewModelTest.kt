package id.ganyem.app.ui.product

import id.ganyem.app.product.CreateProductRequest
import id.ganyem.app.product.ProductCreator
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
class AddProductViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `submit creates product and marks success`() = runTest(dispatcher) {
        var submitted: CreateProductRequest? = null
        val viewModel = AddProductViewModel(
            ProductCreator { submitted = it }
        )
        fillValidForm(viewModel)

        viewModel.submit()
        advanceUntilIdle()

        assertEquals(
            CreateProductRequest("Kopi", 25000L, 15000L, 10),
            submitted
        )
        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `submit exposes API failure`() = runTest(dispatcher) {
        val viewModel = AddProductViewModel(
            ProductCreator { throw IllegalStateException("API gagal") }
        )
        fillValidForm(viewModel)

        viewModel.submit()
        advanceUntilIdle()

        assertEquals("API gagal", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `submit remains loading until API completes`() = runTest(dispatcher) {
        val apiResult = CompletableDeferred<Unit>()
        val viewModel = AddProductViewModel(
            ProductCreator { apiResult.await() }
        )
        fillValidForm(viewModel)

        viewModel.submit()
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)

        apiResult.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `submit reports validation error without calling API`() = runTest(dispatcher) {
        var apiCalled = false
        val viewModel = AddProductViewModel(
            ProductCreator { apiCalled = true }
        )
        viewModel.onSellingPriceChange("100")
        viewModel.onInitialPriceChange("50")
        viewModel.onQuantityChange("2")

        viewModel.submit()
        advanceUntilIdle()

        assertEquals("Nama produk wajib diisi", viewModel.uiState.value.error)
        assertFalse(apiCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `resetForm clears values and status`() = runTest(dispatcher) {
        val viewModel = AddProductViewModel(ProductCreator {})
        fillValidForm(viewModel)
        viewModel.submit()
        advanceUntilIdle()

        viewModel.resetForm()

        assertEquals(AddProductUiState(), viewModel.uiState.value)
    }

    private fun fillValidForm(viewModel: AddProductViewModel) {
        viewModel.onProductNameChange(" Kopi ")
        viewModel.onSellingPriceChange("25000")
        viewModel.onInitialPriceChange("15000")
        viewModel.onQuantityChange("10")
    }
}

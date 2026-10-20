package id.ganyem.app.ui.auth

import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.FakeAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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
class AuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `restoreSession sets restored tokens and finishes loading`() = runTest(dispatcher) {
        val tokens = AppTokens("access-token", Long.MAX_VALUE)
        val repository = FakeAuthRepository().apply { sessionToRestore = tokens }
        val viewModel = AuthViewModel(repository)

        viewModel.restoreSession()
        advanceUntilIdle()

        assertEquals(tokens, viewModel.uiState.value.tokens)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        assertEquals(1, repository.restoreCalls)
    }

    @Test
    fun `restoreSession exposes repository failure and finishes loading`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            restoreError = IllegalStateException("Sesi gagal dipulihkan")
        }
        val viewModel = AuthViewModel(repository)

        viewModel.restoreSession()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.tokens)
        assertEquals("Sesi gagal dipulihkan", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onLoginSuccess stores tokens and clears error`() = runTest(dispatcher) {
        val viewModel = AuthViewModel(FakeAuthRepository())
        val tokens = AppTokens("access-token", Long.MAX_VALUE)

        viewModel.onLoginSuccess(tokens)

        assertEquals(tokens, viewModel.uiState.value.tokens)
        assertNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `logout clears session after repository succeeds`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        val tokens = AppTokens("access-token", Long.MAX_VALUE)
        viewModel.onLoginSuccess(tokens)

        viewModel.logout()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.tokens)
        assertNull(viewModel.uiState.value.error)
        assertEquals(1, repository.logoutCalls)
    }

    @Test
    fun `logout keeps session and exposes repository failure`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            logoutError = IllegalStateException("Logout gagal")
        }
        val viewModel = AuthViewModel(repository)
        val tokens = AppTokens("access-token", Long.MAX_VALUE)
        viewModel.onLoginSuccess(tokens)

        viewModel.logout()
        advanceUntilIdle()

        assertEquals(tokens, viewModel.uiState.value.tokens)
        assertEquals("Logout gagal", viewModel.uiState.value.error)
        assertEquals(1, repository.logoutCalls)
    }


    @Test
    fun `restoreSession only runs once when called repeatedly`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            sessionToRestore = AppTokens("access-token", Long.MAX_VALUE)
        }
        val viewModel = AuthViewModel(repository)

        viewModel.restoreSession()
        viewModel.restoreSession()
        viewModel.restoreSession()

        advanceUntilIdle()

        assertEquals(1, repository.restoreCalls)
        assertFalse(viewModel.uiState.value.isLoading)
    }


    @Test
    fun `logout only runs once when called repeatedly`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)

        viewModel.logout()
        viewModel.logout()
        viewModel.logout()

        advanceUntilIdle()

        assertEquals(1, repository.logoutCalls)
    }


    @Test
    fun `restoreSession propagates cancellation`() = runTest(dispatcher) {
        val repository = FakeAuthRepository().apply {
            restoreError = kotlinx.coroutines.CancellationException("Test cancelled")
        }
        val viewModel = AuthViewModel(repository)

        viewModel.restoreSession()
        advanceUntilIdle()

        assertEquals(1, repository.restoreCalls)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.tokens)
    }



}

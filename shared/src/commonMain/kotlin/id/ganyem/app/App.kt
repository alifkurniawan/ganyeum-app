package id.ganyem.app

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.ui.HomeScreen
import id.ganyem.app.ui.LoginScreen
import kotlinx.coroutines.launch

@Composable
fun App(repository: AuthRepository) {
    val scope = rememberCoroutineScope()
    var tokens by remember {
        mutableStateOf<AppTokens?>(null)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(repository) {
        try {
            tokens = repository.restoreSession()
        } catch (e: Exception) {
            error = e.message ?: "Gagal memulihkan sesi."
            tokens = null
        } finally {
            loading = false
        }
    }

    when {
        loading -> {
            CircularProgressIndicator()
        }

        tokens == null -> {
            LoginScreen(
                repository = repository,
                onLoginSuccess = { appTokens ->
                    tokens = appTokens
                    error = null
                }
            )
        }

        else -> {
            HomeScreen(
                accessToken = tokens!!.accessToken,
                onLogout = {
                    scope.launch {
                        repository.logout()
                        tokens = null
                    }
                }
            )
        }
    }
}

package id.ganyem.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthSessionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val tokens: AppTokens? = null,
    val isLoading: Boolean = true,
    val error: String? = null

)

class AuthViewModel(
    private val repository: AuthSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    private var isLoggingOut = false
    private var hasRestoredSession = false

    fun restoreSession() {
        if (hasRestoredSession) return
        hasRestoredSession = true

        viewModelScope.launch {

            try {
                val tokens = repository.restoreSession()
                _uiState.update {
                    it.copy(tokens = tokens, error = null)
                }
            } catch (e: CancellationException) {
                hasRestoredSession = false
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        tokens = null,
                        error = e.message ?: "Gagal memulihkan sesi."
                    )
                }
            } finally {
                hasRestoredSession = true
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun onLoginSuccess(tokens: AppTokens) {
        _uiState.update {
            it.copy(tokens = tokens, error = null)
        }
    }

    fun logout() {
        if (isLoggingOut) return
        isLoggingOut = true
        viewModelScope.launch {
            try {
                repository.logout()
                _uiState.update {
                    it.copy(tokens = null, error = null)
                }
            } catch (e: CancellationException) {

                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Gagal logout."
                    )
                }
            } finally {
                isLoggingOut = false
            }
        }
    }
}

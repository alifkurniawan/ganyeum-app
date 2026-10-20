package id.ganyem.app.auth

data class AuthUiState(
    val tokens: AppTokens? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

package id.ganyem.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen (
    repository: AuthRepository,
    onLoginSuccess: (AppTokens) -> Unit
) {
    val scope = rememberCoroutineScope()
    var loading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selamat Datang",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text("Login menggunakan akun Google kamu")
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    errorMessage = null

                    try{
                        var result = repository.login()
                        onLoginSuccess(result)
                    } catch (e: Exception) {
                        errorMessage = e.message?: "Login gagal"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Memproses login...")
            } else {
                Text("Login dengan Google")
            }
        }

        errorMessage?.let { message ->
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }


    }



}
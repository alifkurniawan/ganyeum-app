package id.ganyem.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import id.ganyem.app.di.AppContainer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = AppContainer(
            activity = this,
            baseUrl = BuildConfig.API_BASE_URL)

        setContent {
            App(
                repository = container.authRepository,
                productRepository = container.productRepository,
                productApi = container.productApi,
                customerRepository = container.customerRepository,
                shippingRepository = container.shippingRepository,
                orderRepository = container.orderRepository
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}
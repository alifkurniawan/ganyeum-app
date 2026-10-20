package id.ganyem.app

import androidx.compose.ui.window.ComposeUIViewController
import id.ganyem.app.auth.GoogleAuthProvider
import id.ganyem.app.di.IosAppContainer
import platform.UIKit.UIViewController

fun MainViewController(
    apiBaseUrl: String,
    googleAuthProvider: GoogleAuthProvider
): UIViewController {
    val container = IosAppContainer(apiBaseUrl, googleAuthProvider)
    return ComposeUIViewController {
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

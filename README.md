# Ganyem

Ganyem is a cross-platform mobile app for managing a product catalog and orders. It is built with Kotlin Multiplatform and Compose Multiplatform. The `shared` module contains most of the UI and business logic, with platform-specific entry points for Android and iOS.

## Available features

- Google sign-in and session restoration with tokens.
- View, add, edit, and delete products.
- Create orders by selecting a customer, products, and a shipping service.
- View the order list.
- View or manage customer data from the **Others** menu.

> The Home screen currently displays a “Coming soon” placeholder. The app requires a running Ganyem API backend that is reachable from the device.

## Technology

- Kotlin Multiplatform, Kotlin Coroutines, and Kotlin Serialization.
- Compose Multiplatform and Material 3 for the shared UI.
- Ktor Client for HTTP communication (OkHttp on Android, Darwin on iOS).
- Google/Firebase Authentication for sign-in.
- Android: Gradle, Android Gradle Plugin, minimum SDK 24.
- iOS: SwiftUI entry point hosting the shared Compose UI.

## Project structure

```text
androidApp/    Android application entry point and build configuration
iosApp/        Xcode project and SwiftUI entry point
shared/        Shared Compose UI, authentication, models, repositories, and API
```

The main shared code is in `shared/src/commonMain/kotlin/id/ganyem/app`. Platform-specific implementations are in `androidMain` and `iosMain`; tests are in `commonTest`, `androidHostTest`, and `iosTest`.

## Running the app

### Prerequisites

- A JDK compatible with the project's Gradle Wrapper.
- Android Studio and the Android SDK to build or run the Android app.
- Xcode with iOS Simulator support to run the iOS app.
- The Ganyem API backend and the required Google/Firebase Authentication configuration.

### Android

1. Open the project in Android Studio and wait for Gradle sync to complete.
2. Make sure the Firebase/Google Services configuration is available for the Android app.
3. Set the backend URL in `androidApp/build.gradle.kts` using `API_BASE_URL` in the `debug` configuration (the `release` configuration has its own value). The current default debug URL is `http://192.168.1.8:8080/api`; change it to an address reachable from your device or emulator.
4. Run the Android app configuration from Android Studio, or build a debug APK:

   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

### iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Set the `API_BASE_URL` build setting so it is available as `API_BASE_URL` in the app's `Info.plist`. The app will stop at startup if this value is empty or unset.
3. Configure Google Sign-In for iOS, then select an iOS Simulator or device and run the app from Xcode.

For an emulator or device, use a backend address that the device can reach; `localhost` usually refers to the device itself.

## Tests

Run the shared logic tests on the Android host:

```bash
./gradlew :shared:testAndroidHostTest
```

iOS Simulator tests can be run on macOS with a suitable simulator:

```bash
./gradlew :shared:iosSimulatorArm64Test
```

## API and authentication

The app uses `/api` as the API base path. The endpoints it uses include authentication, `/products`, `/customers`, `/shippings`, and `/orders`. Authenticated API requests send the access token as a Bearer token. The token is refreshed through Google authentication when it is close to expiring or after a 401 response.

The backend contract must match the models and requests in `shared/src/commonMain/kotlin/id/ganyem/app`. Backend and Firebase configuration details are maintained outside this repository.

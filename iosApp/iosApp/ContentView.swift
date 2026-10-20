import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        guard let apiBaseUrl = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String,
              !apiBaseUrl.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty,
              !apiBaseUrl.contains("$(") else {
            fatalError("Set API_BASE_URL in the iOS app's Info.plist build settings.")
        }
        return MainViewControllerKt.MainViewController(
            apiBaseUrl: apiBaseUrl,
            googleAuthProvider: IOSGoogleAuthProvider()
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

import FirebaseAuth
import FirebaseCore
import GoogleSignIn
import Shared
import UIKit

final class IOSGoogleAuthProvider: NSObject, Shared.GoogleAuthProvider {
    func signInAndGetIdToken(
        completionHandler: @escaping (String?, (any Error)?) -> Void
    ) {
        DispatchQueue.main.async {
            guard let clientID = FirebaseApp.app()?.options.clientID else {
                completionHandler(nil, NSError(
                    domain: "GanyemAuth",
                    code: 1,
                    userInfo: [NSLocalizedDescriptionKey: "Firebase client ID tidak tersedia. Periksa GoogleService-Info.plist."]
                ))
                return
            }

            GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)
            guard let presenter = Self.topViewController() else {
                completionHandler(nil, NSError(
                    domain: "GanyemAuth",
                    code: 2,
                    userInfo: [NSLocalizedDescriptionKey: "Tidak menemukan layar untuk menampilkan Google Sign-In."]
                ))
                return
            }

            GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
                if let error {
                    completionHandler(nil, error)
                    return
                }
                guard let user = result?.user,
                      let idToken = user.idToken?.tokenString else {
                    completionHandler(nil, NSError(
                        domain: "GanyemAuth",
                        code: 3,
                        userInfo: [NSLocalizedDescriptionKey: "Google tidak mengembalikan ID token."]
                    ))
                    return
                }

                let credential = FirebaseAuth.GoogleAuthProvider.credential(
                    withIDToken: idToken,
                    accessToken: user.accessToken.tokenString
                )
                Auth.auth().signIn(with: credential) { authResult, error in
                    if let error {
                        completionHandler(nil, error)
                        return
                    }
                    guard let firebaseUser = authResult?.user else {
                        completionHandler(nil, NSError(
                            domain: "GanyemAuth",
                            code: 4,
                            userInfo: [NSLocalizedDescriptionKey: "Firebase tidak mengembalikan user."]
                        ))
                        return
                    }
                    firebaseUser.getIDToken { token, error in
                        if let error {
                            completionHandler(nil, error)
                        } else if let token {
                            completionHandler(token, nil)
                        } else {
                            completionHandler(nil, NSError(
                                domain: "GanyemAuth",
                                code: 5,
                                userInfo: [NSLocalizedDescriptionKey: "Firebase ID token tidak tersedia."]
                            ))
                        }
                    }
                }
            }
        }
    }

    func getIdToken(
        forceRefresh: Bool,
        completionHandler: @escaping (String?, (any Error)?) -> Void
    ) {
        guard let user = Auth.auth().currentUser else {
            completionHandler(nil, NSError(
                domain: "GanyemAuth",
                code: 6,
                userInfo: [NSLocalizedDescriptionKey: "Sesi Firebase tidak tersedia. Silakan login kembali."]
            ))
            return
        }
        user.getIDTokenForcingRefresh(forceRefresh) { token, error in
            if let error {
                completionHandler(nil, error)
            } else if let token {
                completionHandler(token, nil)
            } else {
                completionHandler(nil, NSError(
                    domain: "GanyemAuth",
                    code: 7,
                    userInfo: [NSLocalizedDescriptionKey: "Firebase ID token tidak tersedia."]
                ))
            }
        }
    }

    func signOut(completionHandler: @escaping ((any Error)?) -> Void) {
        do {
            try Auth.auth().signOut()
            GIDSignIn.sharedInstance.signOut()
            completionHandler(nil)
        } catch {
            completionHandler(error)
        }
    }

    private static func topViewController(
        from root: UIViewController? = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first(where: \.isKeyWindow)?.rootViewController
    ) -> UIViewController? {
        if let presented = root?.presentedViewController {
            return topViewController(from: presented)
        }
        if let navigation = root as? UINavigationController {
            return topViewController(from: navigation.visibleViewController)
        }
        if let tab = root as? UITabBarController {
            return topViewController(from: tab.selectedViewController)
        }
        return root
    }
}

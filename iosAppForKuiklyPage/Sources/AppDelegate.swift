/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import UIKit
import OpenA2UIIOSRender

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Tell the render pod which Kotlin/Native framework hosts the A2UI
        // Kotlin code so it can resolve `A2UICoreEntry` at runtime. Must be
        // set BEFORE any A2UI static entry point that touches the Kotlin
        // bridge (e.g. `A2UIDemoIconRegistration.register()` below).
        // This target links `pod 'a2ui_demo'`, whose framework binary is A2UIDemo.
        A2UISurfaceView.frameworkName = "A2UIDemo"

        window = UIWindow(frame: UIScreen.main.bounds)
        let nav = UINavigationController(rootViewController: A2UIDemoListViewController())
        nav.navigationBar.prefersLargeTitles = false
        window?.rootViewController = nav
        window?.makeKeyAndVisible()
        return true
    }
}

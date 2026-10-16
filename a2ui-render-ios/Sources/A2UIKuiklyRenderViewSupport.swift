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

import Foundation
import OpenKuiklyIOSRender

// The KMP-side `A2UICustomViewRegistry` singleton is reached through the
// `A2UICustomViewRegistryBridgeProtocol` runtime bridge declared inside
// `A2UISurfaceView.swift` — we intentionally avoid `import A2UICore` so
// this render pod stays framework-agnostic (see file-top comment there).

/// Utilities for enabling A2UI in a Kuikly page host on iOS
/// (`KuiklyRenderViewController` / `KuiklyBaseView`), aligned with
/// Android's `A2UIKuiklyBaseViewSupport`.
///
/// ### Why this exists
///
/// On Android, the Kuikly render pipeline discovers custom render views
/// through a per-page `IKuiklyRenderExport` map, so A2UI has to *inject*
/// its globally-registered custom render views into that map — hence
/// `A2UIKuiklyBaseViewSupport.wrap(delegate)`, which hooks
/// `registerExternalRenderView(export)` and calls
/// `A2UIAndroidCustomViewRegistry.attachExport(export)`.
///
/// **iOS is different**: `KuiklyRenderLayerHandler` resolves render views
/// via `NSClassFromString(viewName)` (see `core-render-ios/Handler/
/// KuiklyRenderLayerHandler.mm`), so once a business view class exists
/// under the exact `viewName` string (or its `K…` variant), it is
/// automatically instantiable by any Kuikly page in the process —
/// no per-page registry needs to be touched.
///
/// That said, we still ship this class for **API parity** with Android
/// and to solve a subtler-but-real KMP-side issue: the DSL applier uses
/// the KMP-side `com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry` to
/// tell "registered A2UI custom component" apart from "unknown viewName
/// → DivView fallback". That KMP registry is only populated when *some*
/// `A2UISurfaceView` core has already forwarded a `REGISTER_CUSTOM_VIEW`
/// KMP method call. Apps that host A2UI **only** inside a Kuikly page
/// (never through the standalone native `A2UISurfaceView`) would thus
/// start with an empty KMP-side registry, and any `A2UISurface { }` in
/// their Kuikly DSL page would render a `DivView` fallback instead of
/// the intended `A2UICustomComponentView`.
///
/// `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()` fixes
/// that by proactively pushing every currently-registered custom viewName
/// into the KMP-side registry, and by installing a listener so subsequent
/// `A2UISurfaceView.registerCustomRenderView` calls also propagate.
///
/// ### Usage
///
/// ```swift
/// class MyKuiklyPageVC: UIViewController {
///     override func viewDidLoad() {
///         super.viewDidLoad()
///         // Call this before instantiating the KuiklyRenderView so any
///         // A2UISurface { ... } embedded inside the loaded page resolves
///         // its custom render views correctly.
///         A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()
///         // ... normal Kuikly page hosting code ...
///     }
/// }
/// ```
///
/// This method is safe to call multiple times and safe to call before
/// any custom view has been registered — it's idempotent and every
/// register-after-call will still fan-out correctly.
@objc(A2UIKuiklyRenderViewSupport)
public final class A2UIKuiklyRenderViewSupport: NSObject {

    /// Whether `prepareForKuiklyPageHosting()` has already run for this
    /// process. Guards against redundant listener installation while
    /// keeping the method itself idempotent.
    private static var prepared = false
    private static let prepareLock = NSLock()

    private override init() { super.init() }

    /// Ensure A2UI's KMP-side custom-view registry is populated so that
    /// any `A2UISurface { ... }` block inside a Kuikly page loaded via
    /// `KuiklyRenderViewController` / `KuiklyBaseView` correctly resolves
    /// its user-registered custom render views (as opposed to falling
    /// back to a plain `DivView`).
    ///
    /// Threading: safe to call from any thread. Internally serialises
    /// listener installation with an `NSLock`.
    ///
    /// This is the iOS moral equivalent of Android's
    /// `A2UIKuiklyBaseViewSupport.wrap(delegate)` — the naming and the
    /// "call once before your first Kuikly page mounts" contract are
    /// intentional so business code reads similarly across platforms.
    @objc
    public static func prepareForKuiklyPageHosting() {
        prepareLock.lock()
        defer { prepareLock.unlock() }
        guard !prepared else { return }
        prepared = true

        // 1) Replay currently-registered custom viewNames into the
        //    process-global KMP-side `A2UICustomViewRegistry`. Unlike
        //    Android, iOS doesn't need to touch a per-page render export
        //    map — `KuiklyRenderLayerHandler` resolves views via
        //    `NSClassFromString`. The only thing that needs proactive
        //    filling is the KMP-side DSL registry used by the applier to
        //    tell "custom component" apart from "unknown viewName →
        //    DivView fallback".
        for viewName in A2UISurfaceView.snapshotRegisteredCustomViewNames() {
            A2UICustomViewRegistryBridge.shared?.register(viewName: viewName)
        }

        // 2) Install a listener so future `A2UISurfaceView
        //    .registerCustomRenderView(...)` calls also propagate to the
        //    KMP registry immediately — mirrors the Android registry's
        //    ChangeListener fan-out pattern.
        A2UISurfaceView.addCustomViewRegistryChangeListener(
            onRegister: { viewName in
                A2UICustomViewRegistryBridge.shared?.register(viewName: viewName)
            },
            onUnregister: { viewName in
                A2UICustomViewRegistryBridge.shared?.unregister(viewName: viewName)
            }
        )

        // 3) Belt-and-braces touch of `A2UIBridgeModule` so the ObjC
        //    class is kept alive even if the host app's linker drops
        //    `-ObjC` / `-force_load`. Without a live symbol table entry
        //    `KuiklyRenderLayerHandler.p_moduleHandlerWithModuleName:`
        //    would eventually hit its `NSAssert(NO, "创建Module失败 … ")`
        //    for A2UI method calls, which K/N cannot catch as a Kotlin
        //    `Throwable`. The matching KMP-side `objc_lookUpClass`
        //    pre-check (`isA2UIBridgeModuleAvailable` in `Platform.ios.kt`)
        //    still protects us if this touch somehow didn't happen.
        _ = NSClassFromString("A2UIBridgeModule")
    }

    /// Convenience overload: performs the same KMP-side custom-view
    /// registry preparation as the no-argument variant **and** force-runs
    /// the `@Page` registry warm-up for the given Kuikly page.
    ///
    /// ### Why the warm-up matters
    ///
    /// The KSP-generated `KuiklyCoreEntry.callKotlinMethod(...)` only
    /// invokes `triggerRegisterPages()` when `BridgeManager.isDidInit()`
    /// is still `false`. Any A2UI code path reached earlier in the
    /// process — notably `A2UIDSLBridge.initialize()` triggered by a
    /// native-hosted `A2UISurfaceView`, by `A2UISurfaceView
    /// .registerCustomFunction / registerCustomRenderView
    /// / registerA2UIIcons`, or by this very class's custom-view registry
    /// hookup — can flip `isDidInit()` to `true` **without** running
    /// `triggerRegisterPages()`. In that case the generated entry's
    /// guard swallows the pager registration branch and the subsequent
    /// `createPager(pageName)` throws `PagerNotFoundException` on the
    /// Kuikly context thread.
    ///
    /// `KuiklyBaseView.isPageExist(withPageName:frameworkName:)` bypasses
    /// the `isDidInit()` guard inside the generated entry and always
    /// runs `triggerRegisterPages()`. The call is idempotent, so it is
    /// safe to run on every host VC and however many times is convenient.
    ///
    /// ### Usage
    ///
    /// ```swift
    /// override func viewDidLoad() {
    ///     super.viewDidLoad()
    ///     // One call does both: KMP-registry fan-out + @Page warm-up.
    ///     A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
    ///         pageName: "MyBizPage",
    ///         frameworkName: "A2UIDemo"
    ///     )
    ///     // ... normal Kuikly page hosting code ...
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - pageName: The `@Page` name declared on the Kuikly page you
    ///     are about to mount — same string later handed to
    ///     `KuiklyRenderViewControllerBaseDelegator(pageName:...)`.
    ///   - frameworkName: The `baseName` of the KMP framework that hosts
    ///     the `@Page` — same string your delegate's
    ///     `fetchContextCode(withPageName:resultCallback:)` would return.
    ///
    /// Threading: safe to call from any thread. The warm-up call itself
    /// hops onto Kuikly's context thread internally.
    @objc(prepareForKuiklyPageHostingWithPageName:frameworkName:)
    public static func prepareForKuiklyPageHosting(pageName: String,
                                                   frameworkName: String) {
        prepareForKuiklyPageHosting()

        // Force-run `triggerRegisterPages()` so `createPager(pageName)`
        // on the Kuikly context thread finds the generated @Page entry
        // regardless of prior `BridgeManager.init(true)` calls. See the
        // KDoc above for the full rationale.
        _ = KuiklyBaseView.isPageExist(withPageName: pageName,
                                        frameworkName: frameworkName)
    }
}

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
import OpenKuiklyIOSRender
import ObjectiveC.runtime

// MARK: - Shadow protocols for A2UI Kotlin bridge
//
// These local `@objc` protocols mirror the ObjC surface of two
// Kotlin/Native classes that live inside `:a2ui` (compiled into
// either `A2UICore.framework` or `A2UIDemo.framework`, depending on
// which pod the host app links):
//
//   - `A2UICoreEntry`                (Kotlin class, `@ObjCName("A2UICoreEntry")`)
//   - `A2UICoreEntry.IOSDelegate`    (Kotlin interface, `@ObjCName("A2UICoreEntryDelegate")`)
//
// We deliberately re-declare them here as `@objc` protocols instead of
// `import A2UICore` so this render pod stays framework-agnostic and can
// be linked into apps that use *either* `A2UICore` (Native-page) *or*
// `A2UIDemo` (Kuikly-page) as their single Kotlin/Native framework —
// which is what prevents Kotlin stdlib duplicate-symbol errors at link
// time. The actual class is resolved via `NSClassFromString("A2UICoreEntry")`
// inside `initSurface()` at runtime, taking advantage of the fact that
// `@ObjCName` strips the enclosing framework prefix so the exposed ObjC
// class name is stable across both frameworks.

/// Selector contract must match `A2UICoreEntry.IOSDelegate.callNative(…)`
/// in Kotlin (see `a2ui/src/iosMain/kotlin/…/A2UICoreEntry.kt`).
@objc protocol A2UICoreEntryDelegateProtocol {
    @objc(callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:)
    func callNative(
        methodId: Int32,
        arg0: Any?,
        arg1: Any?,
        arg2: Any?,
        arg3: Any?,
        arg4: Any?,
        arg5: Any?
    ) -> Any?
}

/// Selector contract must match `A2UICoreEntry.callKotlinMethod(…)` and
/// `A2UICoreEntry.hrCoreDelegate` in Kotlin.
@objc protocol A2UICoreEntryProtocol {
    @objc var hrCoreDelegate: A2UICoreEntryDelegateProtocol? { get set }

    @objc(callKotlinMethodMethodId:arg0:arg1:arg2:)
    func callKotlinMethod(
        methodId: Int32,
        arg0: Any?,
        arg1: Any?,
        arg2: Any?
    )

    @objc func destroy()
}

/// Selector contract must match `A2UICustomViewRegistryBridge` in Kotlin
/// (see `a2ui/src/iosMain/kotlin/…/A2UICoreEntry.kt`).
@objc protocol A2UICustomViewRegistryBridgeProtocol {
    @objc(registerViewName:)
    func register(viewName: String)

    @objc(unregisterViewName:)
    func unregister(viewName: String)
}

/// Runtime resolver for Kotlin/Native classes exposed via `@ObjCName` from
/// the A2UI Kotlin module (`:a2ui`).
///
/// Why this exists: Kotlin/Native's `@ObjCName` on a top-level class *usually*
/// strips the enclosing framework prefix from the exposed ObjC class name, but
/// this is not 100% reliable across K/N versions and cache modes — some builds
/// still ship the class under `<FrameworkName><ObjCName>`. Rather than
/// hard-code "A2UICore" / "A2UIDemo" here (which would tightly couple the
/// render pod to a specific set of downstream framework names), we let the
/// host app tell us which framework it links via
/// `A2UISurfaceView.frameworkName`, mirroring the pattern Kuikly uses in
/// `KuiklyRenderFrameworkContextHandler.entryClassWithFrameworkName:`.
///
/// Resolution order for `resolveClass(objCName:frameworkName:)`:
///   1. `<frameworkName><objCName>`                 — normal K/N naming
///   2. `<objCName>`                                — when `@ObjCName` wins outright
///   3. `<uppercaseOnly(frameworkName)><objCName>`  — K/N's alternate cache-mode naming
///
/// Step 3 mirrors the same defensive fallback in Kuikly's
/// `removeLowerCaseCharacters:`; it's cheap and has been observed in the wild.
internal enum A2UIFrameworkClassResolver {

    /// Resolve a Kotlin/Native class by its `@ObjCName` value, tolerating
    /// the various ways K/N may or may not prefix it with the enclosing
    /// framework name.
    ///
    /// - Parameters:
    ///   - objCName: The value passed to `@ObjCName(...)` in Kotlin.
    ///   - frameworkName: The name of the Kotlin/Native framework the host
    ///     app links (e.g. `"A2UICore"` or `"A2UIDemo"`). Pass `nil` to skip
    ///     prefixed candidates and only try the bare name.
    /// - Returns: The resolved ObjC class, or `nil` if none of the candidates
    ///   are registered with the ObjC runtime.
    static func resolveClass(objCName: String, frameworkName: String?) -> AnyClass? {
        for name in candidateNames(objCName: objCName, frameworkName: frameworkName) {
            if let cls = NSClassFromString(name) {
                return cls
            }
        }
        return nil
    }

    /// Same as `resolveClass` but returns the **name** of the first
    /// registered class (rather than the class itself). Useful for
    /// callers that need to construct related protocol names such as
    /// `<realClassName>Delegate` — K/N derives the delegate protocol
    /// name from the *exposed* class name after framework-prefix
    /// mangling (e.g. `AUIDA2UICoreEntry` →
    /// `AUIDA2UICoreEntryDelegate`), so we must use
    /// the exact resolved name, not the bare `@ObjCName` value.
    static func resolveClassName(objCName: String, frameworkName: String?) -> String? {
        for name in candidateNames(objCName: objCName, frameworkName: frameworkName) {
            if NSClassFromString(name) != nil {
                return name
            }
        }
        return nil
    }

    /// Same as `resolveClass` but also invokes the class-level `+companion`
    /// getter (which K/N always emits for a Kotlin `companion object`) and
    /// then reads the `-shared` **instance** property on that Companion
    /// instance. Mirrors Kuikly's `KuiklyRenderFrameworkContextHandler`
    /// implementation, which relies on the exact same `+companion` →
    /// `-<member>` two-hop pattern.
    ///
    /// This is deliberately **not** a `cls.responds(to: "shared")` check —
    /// K/N does not flatten companion members onto the enclosing class as
    /// `+shared` class methods. It exposes them as instance members of a
    /// separately-registered `<Class>Companion` ObjC class instead, and the
    /// only class-level access point is `+companion`.
    static func resolveSharedSingleton<T>(
        objCName: String,
        frameworkName: String?,
        as protocolType: T.Type
    ) -> T? {
        let companionSelector = NSSelectorFromString("companion")
        let sharedSelector = NSSelectorFromString("shared")
        for name in candidateNames(objCName: objCName, frameworkName: frameworkName) {
            guard let cls = NSClassFromString(name) as? NSObject.Type,
                  cls.responds(to: companionSelector) else {
                continue
            }
            guard let companionUnmanaged = cls.perform(companionSelector),
                  let companionInstance = companionUnmanaged.takeUnretainedValue() as AnyObject?,
                  companionInstance.responds(to: sharedSelector) else {
                continue
            }
            guard let sharedUnmanaged = companionInstance.perform(sharedSelector),
                  let rawInstance = sharedUnmanaged.takeUnretainedValue() as AnyObject? else {
                continue
            }
            // The Kotlin/Native emitted class does not statically declare
            // conformance to our Swift `@objc` shadow protocol, so a plain
            // `as? T` cast fails even though the runtime selectors line up.
            // `unsafeBitCast` is safe here because `@objc` protocol
            // references are just `id`s at the ObjC level — we only rely on
            // selector dispatch, not on Swift protocol witness tables.
            let coerced = unsafeBitCast(rawInstance, to: T.self)
            return coerced
        }
        return nil
    }

    /// Enumerate the ObjC class-name candidates we're willing to try, in
    /// priority order. See the doc comment on the enum for rationale.
    static func candidateNames(objCName: String, frameworkName: String?) -> [String] {
        var out: [String] = []
        if let raw = frameworkName, !raw.isEmpty {
            let normalized = normalizeFrameworkPrefix(raw)
            out.append(normalized + objCName)
            out.append(objCName)
            // Kuikly-parity: keep all non-lowercase-letter characters
            // (uppercase letters, digits, symbols). Mirrors
            // `KuiklyRenderFrameworkContextHandler.removeLowerCaseCharacters:`.
            let nonLowercase = removeLowercaseLetters(from: normalized)
            if !nonLowercase.isEmpty && nonLowercase != normalized {
                out.append(nonLowercase + objCName)
            }
            // A2UI-specific: framework names may contain digits (e.g.
            // "A2UICore"). Observed K/N cache-mode prefixes strip *both*
            // lowercase letters and digits, yielding "AUIC" instead of
            // "A2UIC". Add a stricter candidate that keeps only uppercase
            // ASCII letters so we still resolve `AUICA2UICoreEntry`.
            let uppercaseLettersOnly = keepUppercaseLettersOnly(normalized)
            if !uppercaseLettersOnly.isEmpty
                && uppercaseLettersOnly != normalized
                && uppercaseLettersOnly != nonLowercase {
                out.append(uppercaseLettersOnly + objCName)
            }
        } else {
            out.append(objCName)
        }
        return out
    }

    /// Uppercase the first letter — K/N always capitalizes the framework name
    /// prefix regardless of how the podspec spells it.
    private static func normalizeFrameworkPrefix(_ name: String) -> String {
        guard let first = name.first else { return name }
        return String(first).uppercased() + name.dropFirst()
    }

    /// Drop lowercase letters only (digits/symbols preserved). Mirrors
    /// Kuikly's `+ (NSString *)removeLowerCaseCharacters:`.
    private static func removeLowercaseLetters(from input: String) -> String {
        return input.filter { !$0.isLowercase }
    }

    /// Keep only uppercase ASCII letters (A–Z). Stricter than
    /// `removeLowercaseLetters` — also drops digits and symbols. Needed for
    /// K/N cache-mode prefixes on framework names that contain digits.
    private static func keepUppercaseLettersOnly(_ input: String) -> String {
        return input.filter { ("A"..."Z").contains($0) }
    }
}

/// Runtime resolver for the shared `A2UICustomViewRegistryBridge` singleton.
/// `A2UICustomViewRegistryBridge.Companion.shared` is exposed to ObjC as a
/// class getter `+shared` on `A2UICustomViewRegistryBridge`.
internal enum A2UICustomViewRegistryBridge {
    static var shared: A2UICustomViewRegistryBridgeProtocol? {
        A2UIFrameworkClassResolver.resolveSharedSingleton(
            objCName: "A2UICustomViewRegistryBridge",
            frameworkName: A2UISurfaceView.frameworkName,
            as: A2UICustomViewRegistryBridgeProtocol.self
        )
    }
}

/// Selector contract must match `A2UIIconRegistryBridge` in Kotlin
/// (see `a2ui/src/iosMain/kotlin/…/A2UICoreEntry.kt`).
@objc protocol A2UIIconRegistryBridgeProtocol {
    @objc(registerIconName:uri:)
    func registerIcon(name: String, uri: String)

    @objc(unregisterIconName:)
    func unregisterIcon(name: String)
}

/// Runtime resolver for the shared `A2UIIconRegistryBridge` singleton.
/// `A2UIIconRegistryBridge.Companion.shared` is exposed to ObjC as a
/// class getter `+shared` on `A2UIIconRegistryBridge`.
///
/// The underlying Kotlin object (`A2UIIconRegistry` in commonMain) is a
/// process-global singleton, so registrations survive across
/// A2UISurfaceView lifecycles.
internal enum A2UIIconRegistryBridge {
    static var shared: A2UIIconRegistryBridgeProtocol? {
        A2UIFrameworkClassResolver.resolveSharedSingleton(
            objCName: "A2UIIconRegistryBridge",
            frameworkName: A2UISurfaceView.frameworkName,
            as: A2UIIconRegistryBridgeProtocol.self
        )
    }
}

/// Selector contract must match `A2UICustomFunctionRegistryBridge` in
/// Kotlin (see `a2ui/src/iosMain/kotlin/…/A2UICoreEntry.kt`).
///
/// The `spec` wire format is the same `"returnType|invokeMode"` string
/// used by the per-surface `REGISTER_CUSTOM_FUNCTION` `A2UIKotlinMethod`
/// message — e.g. `"string|sync"`, `"void|async"` — so the Swift caller
/// can reuse its existing descriptor string verbatim.
@objc protocol A2UICustomFunctionRegistryBridgeProtocol {
    @objc(registerName:spec:)
    func register(name: String, spec: String)

    @objc(unregisterName:)
    func unregister(name: String)
}

/// Runtime resolver for the shared `A2UICustomFunctionRegistryBridge`
/// singleton. `A2UICustomFunctionRegistryBridge.Companion.shared` is
/// exposed to ObjC as a class getter `+shared` on
/// `A2UICustomFunctionRegistryBridge`.
///
/// The underlying Kotlin object
/// (`com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry`) is a
/// process-global singleton, so registrations pushed through this bridge
/// survive across A2UISurfaceView lifecycles and are visible to every
/// A2UI instance — including those hosted inside a Kuikly page via
/// `A2UISurfaceDSLView`, which has no per-surface `A2UICoreEntry` to
/// receive the traditional `REGISTER_CUSTOM_FUNCTION` broadcast.
internal enum A2UICustomFunctionRegistryBridge {
    static var shared: A2UICustomFunctionRegistryBridgeProtocol? {
        A2UIFrameworkClassResolver.resolveSharedSingleton(
            objCName: "A2UICustomFunctionRegistryBridge",
            frameworkName: A2UISurfaceView.frameworkName,
            as: A2UICustomFunctionRegistryBridgeProtocol.self
        )
    }
}


/// iOS A2UI Surface View.
/// This is the main entry point for embedding A2UI rendered content in an iOS application.
///
/// Architecture (aligned with Kuikly DSL rendering flow):
/// - Uses A2UICoreEntry (Kotlin/Native) as the bridge between Native and Kotlin A2UI logic.
/// - Native → Kotlin: via A2UICoreEntry.callKotlinMethod (INIT, WRITE_STREAM, FINISH_STREAM, DESTROY)
/// - Kotlin → Native: via A2UICoreEntryDelegate.callNative (CREATE_VIEW, SET_PROP, etc.)
/// - The delegate dispatches render commands to KuiklyRenderLayerHandler.
///
/// Threading model:
/// - writeStream and finishStream schedule work on the Context Queue,
///   where Kotlin logic executes.
/// - Kotlin sends render commands back via the delegate, which are collected as pending UI tasks.
/// - UI tasks are batch-flushed to the Main Thread when FLUSH_UI_TASKS is received.
///
/// Usage:
/// ```swift
/// // Once per process (e.g. in AppDelegate.didFinishLaunching), tell the
/// // render pod which Kotlin/Native framework hosts the A2UI Kotlin code:
/// A2UISurfaceView.frameworkName = "A2UICore"   // or "A2UIDemo", etc.
///
/// let surfaceView = A2UISurfaceView(frame: bounds)
/// surfaceView.initSurface()
/// surfaceView.writeStream(jsonChunk)
/// surfaceView.finishStream()
/// ```
public class A2UISurfaceView: UIView {

    // MARK: - Kotlin/Native Framework Binding

    /// Name of the Kotlin/Native framework that hosts the A2UI Kotlin code
    /// (`:a2ui` module). Typical values are `"A2UICore"` (when the host
    /// app links `pod 'a2ui'`) or `"A2UIDemo"` (when it links `pod 'a2ui_demo'`),
    /// but any downstream repackaging is fine as long as the name matches the
    /// actual framework binary linked into the app.
    ///
    /// **Set this once, early**, before any `A2UISurfaceView.initSurface()`
    /// call and before any `registerIcon` / register-custom-view call, since
    /// those static entry points also resolve Kotlin classes via this name.
    /// AppDelegate.`application(_:didFinishLaunchingWithOptions:)` is a good
    /// place.
    ///
    /// Leaving this `nil` still works if Kotlin/Native's `@ObjCName` fully
    /// stripped the framework prefix from the exposed class names in your
    /// build — but that's not guaranteed across K/N versions, so setting it
    /// explicitly is strongly recommended.
    public static var frameworkName: String?

    // MARK: - Custom Function Registry (Global)

    /// Return type tokens for custom A2UI functions (see A2UIFunctionReturnType in commonMain).
    public static let returnTypeString  = "string"
    public static let returnTypeBoolean = "boolean"
    public static let returnTypeNumber  = "number"
    public static let returnTypeArray   = "array"
    public static let returnTypeVoid    = "void"

    /// Invoke mode tokens.
    public static let invokeModeSync    = "sync"
    public static let invokeModeAsync   = "async"

    /// A user-supplied handler for a custom A2UI function.
    /// - Parameter args: Resolved argument map from the A2UI stream.
    /// - Returns: One of:
    ///     - `String` for return type `"string"`;
    ///     - `Bool` for `"boolean"`;
    ///     - `NSNumber` / `Double` / `Int` for `"number"`;
    ///     - JSON `String` or `[Any]` for `"array"`;
    ///     - anything (including `nil`) for `"void"`.
    ///
    /// Threading:
    ///   * `"sync"` handlers run on the Kuikly Context Queue. Keep the work
    ///     brief — this queue drives A2UI rendering.
    ///   * `"async"` handlers (void only) run on the main queue and return
    ///     immediately.
    public typealias Handler = ([String: Any]) -> Any?

    /// Global handler table.
    private static var globalHandlers: [String: Handler] = [:]
    private static var globalDescriptors: [String: String] = [:]
    /// Thread guard for the two tables above.
    private static let handlersLock = NSLock()

    /// Live surfaces set (weak refs) — used to broadcast register/unregister
    /// to any surface that has already booted its A2UICoreEntry.
    private static let liveSurfaces = NSHashTable<A2UISurfaceView>.weakObjects()
    private static let liveSurfacesLock = NSLock()

    /// Guard for `ensureConformsToKotlinDelegateProtocol(entryClassName:)`
    /// — we only need to run the runtime dance once per (delegate protocol
    /// name, target class) pair. Keyed by the derived protocol name.
    private static var didConformToKotlinDelegateProtocol = Set<String>()
    private static let conformToKotlinDelegateProtocolLock = NSLock()

    /// Runtime-attach an ObjC protocol named `<entryClassName>Delegate` to
    /// the `A2UISurfaceView` class so Kotlin/Native's `hrCoreDelegate`
    /// dispatch drops down to `objc_msgSend` on this Swift class instead
    /// of a null-vtable Kotlin trampoline (which crashes as EXC_BAD_ACCESS).
    ///
    /// Kotlin/Native does **not** emit an ObjC `@protocol` for a Kotlin
    /// `interface`. Even a top-level, `@ObjCName`-annotated interface stays
    /// unregistered from the ObjC runtime's perspective — verified by
    /// `NSProtocolFromString(...) == nil` at startup.
    ///
    /// However, when the interface is declared **nested inside** an
    /// `@ObjCName`-annotated class and left unannotated itself, K/N
    /// dispatches `hrCoreDelegate?.callNative(...)` as ordinary `id`-based
    /// ObjC messaging, gated by a `class_conformsToProtocol` check against
    /// a protocol whose name is derived from the outer class: for an outer
    /// class exposed to ObjC as `Foo`, the protocol name is `FooDelegate`.
    ///
    /// So for our case, if the resolved Kotlin entry class ends up as
    /// `AUICA2UICoreEntry` at runtime, we must allocate & attach a
    /// protocol named `AUICA2UICoreEntryDelegate`. The `entryClassName`
    /// argument is the real class name we already resolved through
    /// `NSClassFromString` — passing it in avoids hardcoding the K/N
    /// name-mangling scheme.
    ///
    /// This mirrors the trick used by Kuikly's
    /// `KuiklyRenderFrameworkContextHandler.entryClassWithFrameworkName:`.
    /// The protocol is intentionally empty — K/N only checks conformance
    /// by name and then falls back to `respondsToSelector:` + `msgSend`
    /// for the actual method dispatch.
    ///
    /// Ensure `targetClass` conforms to the K/N-emitted ObjC delegate
    /// protocol named `<entryClassName>Delegate` (e.g. `AUICA2UICoreEntryDelegate`).
    ///
    /// Called from every callsite that installs a Swift instance as a
    /// K/N-side delegate. Kotlin/Native gates `id`-based delegate
    /// dispatch on `class_conformsToProtocol(cls, <ObjCProto>)`; if the
    /// check fails, K/N falls back to Kotlin vtable dispatch, which is
    /// empty for Swift objects and crashes at `EXC_BAD_ACCESS`.
    ///
    /// - Parameters:
    ///   - entryClassName: The runtime ObjC class name of the enclosing
    ///     Kotlin class that owns the delegate (e.g. the resolved value
    ///     of `A2UICoreEntry`).
    ///   - targetClass: The Swift/ObjC class we need to *pretend* conforms
    ///     to that protocol. Defaults to `A2UISurfaceView.self` because
    ///     that's the historical caller.
    ///
    /// The protocol is intentionally empty — K/N only checks conformance
    /// by name and then falls back to `respondsToSelector:` + `msgSend`
    /// for the actual method dispatch.
    ///
    /// Idempotent per (protocol name, class) pair.
    fileprivate static func ensureConformsToKotlinDelegateProtocol(
        entryClassName: String,
        targetClass: AnyClass = A2UISurfaceView.self
    ) {
        let protoName = "\(entryClassName)Delegate"
        // Dedup key includes the target class so that adding the protocol
        // to multiple Swift classes doesn't get swallowed by the first
        // one's cache entry.
        let dedupKey = "\(protoName)@\(NSStringFromClass(targetClass))"

        conformToKotlinDelegateProtocolLock.lock()
        let already = didConformToKotlinDelegateProtocol.contains(dedupKey)
        if !already { didConformToKotlinDelegateProtocol.insert(dedupKey) }
        conformToKotlinDelegateProtocolLock.unlock()
        guard !already else { return }

        var proto: Protocol? = NSProtocolFromString(protoName)
        if proto == nil {
            // Allocate a brand-new empty protocol. K/N doesn't care about
            // method requirements — it only checks `class_conformsToProtocol`.
            if let allocated = protoName.withCString({ objc_allocateProtocol($0) }) {
                objc_registerProtocol(allocated)
                proto = NSProtocolFromString(protoName)
                print("[A2UISurfaceView] Allocated & registered empty ObjC protocol `\(protoName)` at runtime.")
            } else {
                print("[A2UISurfaceView] WARNING: objc_allocateProtocol(`\(protoName)`) failed — Kotlin→Native callback will crash.")
                return
            }
        }
        guard let realProto = proto else { return }
        if !class_conformsToProtocol(targetClass, realProto) {
            let added = class_addProtocol(targetClass, realProto)
            print("[A2UISurfaceView] class_addProtocol(\(NSStringFromClass(targetClass)), \(protoName)) → \(added)")
        }
    }

    /// Register a custom A2UI function **globally**. Once registered, the
    /// function is available to every A2UISurfaceView in this process.
    ///
    /// - Note: Reserved names (the 14 built-in A2UI v0.9 functions) are
    ///         ignored by the Kotlin registry.
    ///
    /// - Parameters:
    ///   - name: Function name as it appears in the A2UI catalog.
    ///   - returnType: One of `returnTypeString` / `returnTypeBoolean` /
    ///                 `returnTypeNumber` / `returnTypeArray` /
    ///                 `returnTypeVoid`.
    ///   - invokeMode: One of `invokeModeSync` / `invokeModeAsync`. Non-void
    ///                 functions are always treated as SYNC regardless.
    ///   - handler: The callback that performs the actual work.
    @objc(registerCustomFunction:returnType:invokeMode:handler:)
    public static func registerCustomFunction(
        name: String,
        returnType: String,
        invokeMode: String = "sync",
        handler: @escaping Handler
    ) {
        guard !name.isEmpty else { return }
        let spec = "\(returnType)|\(invokeMode)"
        handlersLock.lock()
        globalHandlers[name] = handler
        globalDescriptors[name] = spec
        handlersLock.unlock()
        // 1) Push the descriptor to the KMP-side process-global registry so
        //    that the Kuikly-page embedded mode (`A2UISurfaceDSLView`, which
        //    has no per-surface `A2UICoreEntry`) can also see the function.
        //    Mirrors how [registerA2UIIcon] talks to `A2UIIconRegistryBridge`.
        //    Safe & idempotent alongside the per-surface broadcast below —
        //    both writes target the same KMP `A2UICustomFunctionRegistry`
        //    JVM-singleton object.
        //
        //    Evaluation-time invocation of custom functions in Kuikly-page
        //    embedded mode no longer needs a Swift-side delegate: it flows
        //    through the standard Kuikly Module channel (`A2UIBridgeModule`
        //    → `A2UIBridgeModuleHelper.invokeCustomFunctionWithName:argsJson:spec:`
        //    → `A2UISurfaceView.invokeCustomFunctionLocallyStatic`).
        A2UICustomFunctionRegistryBridge.shared?.register(name: name, spec: spec)
        // 2) Broadcast to every currently-live standalone `A2UISurfaceView`
        //    so their per-instance `A2UICoreEntry` learns the new descriptor
        //    without waiting for its own init cycle (Native-page mode path).
        broadcastRegister(name: name, spec: spec)
    }

    /// Reverse of `registerCustomFunction`. Safe to call for names that
    /// were never registered.
    @objc(unregisterCustomFunction:)
    public static func unregisterCustomFunction(name: String) {
        guard !name.isEmpty else { return }
        handlersLock.lock()
        globalHandlers.removeValue(forKey: name)
        globalDescriptors.removeValue(forKey: name)
        handlersLock.unlock()
        // Mirror the two-way write in [registerCustomFunction] so both
        // hosting modes stay in sync when a function is dropped.
        A2UICustomFunctionRegistryBridge.shared?.unregister(name: name)
        broadcastUnregister(name: name)
    }

    private static func broadcastRegister(name: String, spec: String) {
        liveSurfacesLock.lock()
        let surfaces = liveSurfaces.allObjects
        liveSurfacesLock.unlock()
        for s in surfaces {
            s.dispatchRegisterCustomFunction(name: name, spec: spec)
        }
    }

    private static func broadcastUnregister(name: String) {
        liveSurfacesLock.lock()
        let surfaces = liveSurfaces.allObjects
        liveSurfacesLock.unlock()
        for s in surfaces {
            s.dispatchUnregisterCustomFunction(name: name)
        }
    }

    private static func snapshotDescriptors() -> [(String, String)] {
        handlersLock.lock()
        let snapshot = globalDescriptors.map { ($0.key, $0.value) }
        handlersLock.unlock()
        return snapshot
    }

    private static func lookupHandler(name: String) -> Handler? {
        handlersLock.lock()
        let h = globalHandlers[name]
        handlersLock.unlock()
        return h
    }

    /// Static counterpart of the instance method `invokeCustomFunctionLocally`.
    ///
    /// Used by `A2UIBridgeModuleHelper.invokeCustomFunctionWithName:argsJson:spec:`
    /// to service evaluation-time custom-function invocations in
    /// Kuikly-page embedded mode, where there is no `A2UISurfaceView`
    /// instance to dispatch through. The Native-page path (per-surface
    /// `A2UICoreEntry` → `perform` → instance
    /// `invokeCustomFunctionLocally`) is unchanged.
    ///
    /// Threading: called on the Kuikly Context Queue by the KMP DSL
    /// bridge (via the Module channel). `void|async` handlers are
    /// dispatched to the main queue and this method returns `nil`
    /// immediately; all other handlers run inline (must return quickly
    /// to avoid stalling render/parse).
    internal static func invokeCustomFunctionLocallyStatic(
        name: String,
        argsJson: String,
        spec: String
    ) -> Any? {
        guard let handler = lookupHandler(name: name) else { return nil }
        let args: [String: Any]
        if let data = argsJson.data(using: .utf8),
           let parsed = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
            args = parsed
        } else {
            args = [:]
        }
        let parts = spec.split(separator: "|")
        let returnType = parts.count > 0 ? String(parts[0]).lowercased() : "string"
        let invokeMode = parts.count > 1 ? String(parts[1]).lowercased() : "sync"

        if returnType == "void" && invokeMode == "async" {
            DispatchQueue.main.async {
                _ = handler(args)
            }
            return nil
        }

        let raw = handler(args)
        switch raw {
        case .none:
            return nil
        case let s as String:
            return s
        case let b as Bool:
            return b
        case let n as NSNumber:
            return n
        case let arr as [Any]:
            if let data = try? JSONSerialization.data(withJSONObject: arr, options: []),
               let json = String(data: data, encoding: .utf8) {
                return json
            }
            return "[]"
        case .some(let other):
            return "\(other)"
        }
    }

    // MARK: - Icon Registry (Global)

    /// Reserved icon name used to look up the fallback image when a runtime
    /// icon `name` (typically arriving via data binding) is not registered.
    /// Business apps should include this key when calling
    /// [registerA2UIIcons] so unregistered names have a predictable rendering.
    /// Must stay in sync with `A2UIIconRegistry.FALLBACK_KEY` on the Kotlin side.
    @objc public static let iconFallbackKey: String = "_fallback"

    /// Register a batch of business-supplied Icon images **globally**. Once
    /// at least one icon has been registered, EVERY A2UI Icon component
    /// (including those whose `name` is data-bound at runtime) is rendered
    /// via KRImageView instead of the built-in unicode/emoji glyph.
    ///
    /// The values must be image URIs understood by
    /// KuiklyRenderComponentExpandProtocol's `hr_setImageWithUrl:` — the
    /// bundled `A2UIComponentExpandHandler` accepts `assets://` (mapped
    /// to `Bundle.main`), `http(s)://`, and any URL that
    /// `+[UIImage imageWithData:]` can decode.
    ///
    /// If an [iconFallbackKey] entry is provided, unregistered icon names
    /// fall back to it; otherwise the underlying Kotlin registry falls back
    /// to the first registered image, then to an empty URI.
    ///
    /// - Parameter icons: name → image URI mapping. Names are either
    ///                    camelCase (`arrowUpward`) or snake_case
    ///                    (`arrow_upward`); both are accepted.
    ///
    /// - Note: The A2UIIconRegistry Kotlin object is a process-global
    ///   singleton exported by the A2UICore framework, so registrations
    ///   survive across A2UISurfaceView lifecycles — future surfaces
    ///   automatically inherit them without any per-surface replay.
    @objc(registerA2UIIcons:)
    public static func registerA2UIIcons(_ icons: [String: String]) {
        for (name, uri) in icons {
            registerA2UIIcon(name: name, uri: uri)
        }
    }

    /// Register or overwrite a single custom Icon image **globally**.
    ///
    /// See [registerA2UIIcons] for the URI value contract.
    ///
    /// - Parameters:
    ///   - name: Icon `name` as defined by the A2UI protocol (camelCase or
    ///           snake_case), or [iconFallbackKey].
    ///   - uri:  Image URI (e.g. `assets://a2ui_icons/home.png`).
    @objc(registerA2UIIcon:uri:)
    public static func registerA2UIIcon(name: String, uri: String) {
        guard !name.isEmpty, !uri.isEmpty else { return }
        A2UIIconRegistryBridge.shared?.registerIcon(name: name, uri: uri)
    }

    /// Reverse of [registerA2UIIcon]. Safe to call for names that were never
    /// registered.
    @objc(unregisterA2UIIcon:)
    public static func unregisterA2UIIcon(name: String) {
        guard !name.isEmpty else { return }
        A2UIIconRegistryBridge.shared?.unregisterIcon(name: name)
    }

    // MARK: - Custom Render View Registration (Global)

    /// Factory closure type for creating a custom render view.
    public typealias RenderViewCreator = () -> UIView

    /// Size returned by an [A2UICustomViewMeasurer]. Width and height are
    /// expressed in points (iOS's native UIKit unit). Return `0` on an
    /// axis to tell the Flex engine to fall back to the parent-provided
    /// constraint or the JSON-side `width` / `height` hard override.
    public struct A2UICustomViewSize {
        public let width: CGFloat
        public let height: CGFloat
        public init(width: CGFloat, height: CGFloat) {
            self.width = width
            self.height = height
        }
    }

    /// Optional intrinsic-size measurer attached to a custom render view at
    /// registration time. The framework calls it synchronously on the Kuikly
    /// Context Queue each time the Flex engine needs an intrinsic size for
    /// the registered component — the role `+hrv_createShadow` used to fill
    /// before this project migrated custom-view measurement off the Shadow
    /// mechanism onto the standard Kuikly Module channel.
    ///
    /// Returning `nil` (or omitting the registration entirely) delegates to
    /// A2UI's default fallback: `width = parent cross-axis constraint`,
    /// `height = 0`. Authors may still supply `width` / `height` in JSON as
    /// a hard override; those always win over the measurer result (see
    /// `A2UICustomComponentView.measure` on the KMP side).
    ///
    /// Threading: the closure runs inline on the Kuikly Context Queue, so
    /// keep it synchronous and fast — do not touch UIKit, do not block on
    /// IO. If the final size depends on async work (e.g. a WebView's loaded
    /// content height) use the second-pass path
    /// [notifyA2UICustomViewSizeChanged] from the async callback.
    ///
    /// - Parameters:
    ///   - constraintWidth:   Cross-axis constraint from the parent (pt).
    ///   - constraintHeight:  Main-axis constraint from the parent (pt);
    ///                        `-1` when unconstrained.
    public typealias A2UICustomViewMeasurer = (
        _ constraintWidth: CGFloat,
        _ constraintHeight: CGFloat
    ) -> A2UICustomViewSize?

    /// Global custom render view creator table.
    private static var customViewCreators: [String: RenderViewCreator] = [:]
    /// Global measurer table — keyed by viewName. Entries are optional; only
    /// views whose authors want to control intrinsic size appear here.
    private static var customViewMeasurers: [String: A2UICustomViewMeasurer] = [:]
    private static let viewCreatorsLock = NSLock()

    /// Register a custom render view **globally**. Once registered, the
    /// view is available to every A2UISurfaceView instance in this process.
    ///
    /// This API is aligned with the Kuikly DSL custom component registration
    /// flow. The same `KuiklyRenderViewExportProtocol` implementation used in
    /// Kuikly DSL can be reused here without modification.
    ///
    /// ### Optional `measurer`
    ///
    /// Supply `measurer` when the component's intrinsic size is something
    /// you can compute from constants or parent constraints — e.g. a
    /// WebView whose design height is always 240pt. A2UI calls it
    /// synchronously on the Kuikly Context Queue whenever the Flex engine
    /// needs an intrinsic size, exactly where `+hrv_createShadow` used to
    /// run before this project migrated to the Module channel. See
    /// [A2UICustomViewMeasurer] for the threading / return-value contract.
    ///
    /// If `measurer` is omitted, A2UI falls back to
    /// `width = parent cross-axis constraint`, `height = 0`; authors can
    /// still pin the size by writing `width` / `height` in the JSON
    /// component spec, which always wins.
    ///
    /// - Parameters:
    ///   - viewName: The view name as it appears in the A2UI protocol
    ///               (e.g. "KRMyCustomView").
    ///   - creator: A factory closure that creates the view instance.
    ///             The view must conform to `KuiklyRenderViewExportProtocol`.
    ///   - measurer: Optional intrinsic-size resolver — see
    ///              [A2UICustomViewMeasurer].
    @objc(registerCustomRenderView:creator:)
    public static func registerCustomRenderView(
        viewName: String,
        creator: @escaping RenderViewCreator
    ) {
        registerCustomRenderView(viewName: viewName, creator: creator, measurer: nil)
    }

    /// Register a custom render view **globally** with an optional measurer.
    /// Swift-only overload (ObjC callers use the two-arg selector above).
    public static func registerCustomRenderView(
        viewName: String,
        creator: @escaping RenderViewCreator,
        measurer: A2UICustomViewMeasurer?
    ) {
        guard !viewName.isEmpty else { return }
        viewCreatorsLock.lock()
        customViewCreators[viewName] = creator
        if let measurer = measurer {
            customViewMeasurers[viewName] = measurer
        } else {
            customViewMeasurers.removeValue(forKey: viewName)
        }
        viewCreatorsLock.unlock()
        // Also register with the ObjC layer so KuiklyRenderLayerHandler can find it
        A2UIRenderHelper.registerCustomViewCreator({ creator() }, forViewName: viewName)
        // Push the viewName to the KMP-side process-global custom-view
        // registry immediately so the DSL applier (used by the Kuikly-page
        // embedded mode `A2UISurfaceDSLView`) can distinguish this from
        // an unknown viewName. Mirrors the register path used by
        // [registerA2UIIcon] and [registerCustomFunction] — no host-side
        // `prepareForKuiklyPageHosting()` call is required for this to work.
        A2UICustomViewRegistryBridge.shared?.register(viewName: viewName)
        // Notify every currently-live surface view so its Kotlin core learns
        // about the new viewName without waiting for its own init cycle.
        broadcastRegisterCustomView(viewName: viewName)
    }

    /// Unregister a previously registered custom render view.
    /// Safe to call for names that were never registered.
    @objc(unregisterCustomRenderView:)
    public static func unregisterCustomRenderView(viewName: String) {
        guard !viewName.isEmpty else { return }
        viewCreatorsLock.lock()
        customViewCreators.removeValue(forKey: viewName)
        customViewMeasurers.removeValue(forKey: viewName)
        viewCreatorsLock.unlock()
        A2UIRenderHelper.unregisterCustomViewCreator(forViewName: viewName)
        // Mirror the immediate KMP-side write in [registerCustomRenderView].
        A2UICustomViewRegistryBridge.shared?.unregister(viewName: viewName)
        broadcastUnregisterCustomView(viewName: viewName)
    }

    /// Internal: look up a registered custom view creator.
    internal static func lookupViewCreator(viewName: String) -> RenderViewCreator? {
        viewCreatorsLock.lock()
        let creator = customViewCreators[viewName]
        viewCreatorsLock.unlock()
        return creator
    }

    /// Internal: look up a registered intrinsic-size measurer.
    ///
    /// Called from `A2UIBridgeModuleHelper.calculateCustomViewSize` on the
    /// Kuikly Context Queue when the KMP side issues
    /// `A2UIBridgeModule.calculateCustomViewSize` for a custom component.
    internal static func lookupMeasurer(viewName: String) -> A2UICustomViewMeasurer? {
        viewCreatorsLock.lock()
        let measurer = customViewMeasurers[viewName]
        viewCreatorsLock.unlock()
        return measurer
    }
    /// Snapshot of all currently-registered custom viewNames. Used to replay the
    /// full set into a freshly-initialised surface's Kotlin core.
    private static func snapshotCustomViewNames() -> [String] {
        viewCreatorsLock.lock()
        let names = Array(customViewCreators.keys)
        viewCreatorsLock.unlock()
        return names
    }

    /// Internal API used by `A2UIKuiklyRenderViewSupport` to seed the KMP-side
    /// custom-view registry when a host app boots up A2UI **only** through the
    /// Kuikly-page integration path (no `A2UISurfaceView` instantiated). Not part
    /// of the public API surface — hosting apps should call
    /// `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()` instead.
    internal static func snapshotRegisteredCustomViewNames() -> [String] {
        return snapshotCustomViewNames()
    }

    // MARK: - Custom-view registry change listeners

    /// Type of a per-viewName change callback used by
    /// `addCustomViewRegistryChangeListener`.
    internal typealias CustomViewChangeCallback = (String) -> Void

    /// A pair of register/unregister callbacks. We box them in a class so the
    /// listener array can hold them by reference (removing a listener would
    /// require identity comparison; today we never remove).
    private final class CustomViewChangeListener {
        let onRegister: CustomViewChangeCallback
        let onUnregister: CustomViewChangeCallback
        init(onRegister: @escaping CustomViewChangeCallback,
             onUnregister: @escaping CustomViewChangeCallback) {
            self.onRegister = onRegister
            self.onUnregister = onUnregister
        }
    }

    private static var customViewChangeListeners: [CustomViewChangeListener] = []
    private static let customViewChangeListenersLock = NSLock()

    /// Install a listener that receives every future
    /// `registerCustomRenderView` / `unregisterCustomRenderView` event on
    /// this class. Used by `A2UIKuiklyRenderViewSupport` to fan-out
    /// changes to the KMP-side `A2UICustomViewRegistry`. Not part of the
    /// public SDK surface — hosting apps should use
    /// `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()`.
    internal static func addCustomViewRegistryChangeListener(
        onRegister: @escaping CustomViewChangeCallback,
        onUnregister: @escaping CustomViewChangeCallback
    ) {
        customViewChangeListenersLock.lock()
        customViewChangeListeners.append(
            CustomViewChangeListener(onRegister: onRegister, onUnregister: onUnregister)
        )
        customViewChangeListenersLock.unlock()
    }

    private static func notifyListenersOfRegister(viewName: String) {
        customViewChangeListenersLock.lock()
        let listeners = customViewChangeListeners
        customViewChangeListenersLock.unlock()
        for l in listeners {
            l.onRegister(viewName)
        }
    }

    private static func notifyListenersOfUnregister(viewName: String) {
        customViewChangeListenersLock.lock()
        let listeners = customViewChangeListeners
        customViewChangeListenersLock.unlock()
        for l in listeners {
            l.onUnregister(viewName)
        }
    }

    /// Broadcast a `registerCustomRenderView` to every currently-live surface's
    /// Kotlin core (so already-initialised surfaces learn about the new viewName),
    /// and additionally notify listeners installed via
    /// `addCustomViewRegistryChangeListener` (used by the Kuikly-page hosting
    /// support to keep the KMP-side registry in sync).
    private static func broadcastRegisterCustomView(viewName: String) {
        liveSurfacesLock.lock()
        let surfaces = liveSurfaces.allObjects
        liveSurfacesLock.unlock()
        for s in surfaces {
            s.dispatchRegisterCustomView(viewName: viewName)
        }
        notifyListenersOfRegister(viewName: viewName)
    }

    /// Companion to `broadcastRegisterCustomView`.
    private static func broadcastUnregisterCustomView(viewName: String) {
        liveSurfacesLock.lock()
        let surfaces = liveSurfaces.allObjects
        liveSurfacesLock.unlock()
        for s in surfaces {
            s.dispatchUnregisterCustomView(viewName: viewName)
        }
        notifyListenersOfUnregister(viewName: viewName)
    }

    // MARK: - Properties

    /// Unique instance ID for this A2UI surface
    private let instanceId = "a2ui_\(UUID().uuidString)"

    /// The A2UI Core Entry (Kotlin/Native compiled class) resolved at
    /// runtime through `NSClassFromString("A2UICoreEntry")`; typed via the
    /// local `A2UICoreEntryProtocol` shadow protocol so the render pod
    /// avoids a compile-time dependency on any specific Kotlin/Native
    /// framework module (see the file-top comment for the full rationale).
    private var coreEntry: A2UICoreEntryProtocol?

    /// The Kuikly render layer handler that manages native view creation and property setting
    private var renderLayerHandler: KuiklyRenderLayerHandler?

    /// Whether the surface has been initialized
    private var isInitialized = false

    /// Pending UI tasks collected from Kotlin→Native render commands.
    /// Accessed only from context queue.
    private var pendingUITasks: [() -> Void] = []

    /// Tasks to execute when this surface view is deallocated.
    /// Required by KRModalView and other components that call hr_rootView.performWhenRenderViewDeallocWithTask.
    private var deallocTasks: [() -> Void] = []

    /// Context param exposed for components that access hr_rootView.contextParam
    @objc public var contextParam: KuiklyContextParam?

    // MARK: - Constants

    /// A2UI Native Method IDs (must match Core NativeMethod in BridgeManager.kt)
    private enum NativeMethod {
        static let createView = 1           // CREATE_RENDER_VIEW
        static let removeView = 2           // REMOVE_RENDER_VIEW
        static let insertSubView = 3        // INSERT_SUB_RENDER_VIEW
        static let setProp = 4              // SET_VIEW_PROP
        static let setFrame = 5             // SET_RENDER_VIEW_FRAME
        static let calculateRenderViewSize = 6 // CALCULATE_RENDER_VIEW_SIZE
        static let callViewMethod = 7       // CALL_VIEW_METHOD
        static let callModuleMethod = 8     // CALL_MODULE_METHOD
        static let createShadow = 9         // CREATE_SHADOW
        static let removeShadow = 10        // REMOVE_SHADOW
        static let setShadowProp = 11       // SET_SHADOW_PROP
        static let setShadowForView = 12    // SET_SHADOW_FOR_VIEW
        static let setTimeout = 13          // SET_TIMEOUT
        static let callShadowMethod = 14    // CALL_SHADOW_METHOD
        static let fireFatalException = 15  // FIRE_FATAL_EXCEPTION
        static let flushUITasks = 16        // SYNC_FLUSH_UI
        static let callTDFModuleMethod = 17 // CALL_TDF_MODULE_METHOD
        // A2UI-specific methods (start from 100 to avoid conflicts)
        static let measureShadowSize = 100
        static let actionCallback = 101
        static let contentHeightChanged = 102
        static let showDateTimePicker = 103
        static let dataModelChanged = 104
        static let openUrl = 105
        static let executeFunctionCall = 106
        /// Invoke a previously-registered custom A2UI function on the native (platform) side.
        /// See A2UINativeMethod.INVOKE_CUSTOM_FUNCTION for full semantics.
        static let invokeCustomFunction = 107
    }

    /// A2UI Kotlin Method IDs (must match A2UIKotlinMethod.kt)
    private enum KotlinMethod {
        static let initialize = 1
        static let writeStream = 2
        static let finishStream = 3
        static let destroy = 4
        static let setSurfaceSize = 5
        static let viewClick = 6
        static let viewEvent = 7
        static let pickerResult = 9
        /// Register a custom function's metadata in the Kotlin core (global scope).
        static let registerCustomFunction = 10
        /// Reverse of registerCustomFunction.
        static let unregisterCustomFunction = 11
        /// Register a custom render view's viewName in the Kotlin core (global scope).
        /// The DSL applier uses this to decide between A2UICustomComponentView and
        /// a plain DivView fallback for unknown/malformed viewNames.
        static let registerCustomView = 12
        /// Reverse of registerCustomView.
        static let unregisterCustomView = 13
    }

    /// Last known view dimensions in points (for passing to Kotlin layer)
    private var lastKnownWidth: CGFloat = 0
    private var lastKnownHeight: CGFloat = 0

    /// Whether finishStream has been called (to trigger re-layout on size change)
    private var streamFinished = false

    /// Buffered stream chunks waiting for valid dimensions before being sent
    private var pendingStreamChunks: [String] = []

    /// Whether finishStream was called before dimensions were available
    private var pendingFinishStream = false

    /// Content height in points, updated when root view frame is set
    private var contentHeight: CGFloat = 0

    /// Tag of the root view (first child inserted into platform root)
    private var rootViewTag: Int = -1

    /// Listener for surface events (action callbacks, etc.)
    public weak var surfaceListener: A2UISurfaceViewDelegate?

    /// Tags that have touchEnable set (need tap gesture recognizers)
    private var clickableTags: Set<Int> = []

    /// Tags whose viewName doesn't support shadow (no `hrv_createShadow`).
    /// We track them here so subsequent shadow-related NativeMethod calls
    /// (calculateRenderViewSize / setShadowForView / setShadowProp / callShadowMethod / measureShadowSize / removeShadow)
    /// can be safely skipped instead of hitting the `shadow can't be nil` assertion
    /// inside `KuiklyRenderLayerHandler.p_shadowHandlerWithTag:`.
    /// Still useful for the case where a host-registered custom view forgets to
    /// implement `hrv_createShadow`; unknown viewNames themselves no longer reach
    /// A2UICustomComponentView (the DSL applier routes them to DivView).
    private var unshadowedTags: Set<Int> = []

    /// Height constraint for self-sizing in Auto Layout
    private var heightConstraint: NSLayoutConstraint?

    public override var intrinsicContentSize: CGSize {
        if contentHeight > 0 {
            return CGSize(width: UIView.noIntrinsicMetric, height: contentHeight)
        }
        return super.intrinsicContentSize
    }

    // MARK: - Initialization

    public override init(frame: CGRect) {
        super.init(frame: frame)
        clipsToBounds = true
    }

    public required init?(coder: NSCoder) {
        super.init(coder: coder)
        clipsToBounds = true
    }

    /// Initialize the A2UI surface view.
    /// Sets up the Kuikly render layer handler and creates the A2UICoreEntry.
    ///
    /// - Parameters:
    ///   - configJson: Optional JSON configuration string.
    ///   - frameworkName: Optional override for the Kotlin/Native framework
    ///     name that hosts the A2UI Kotlin code (`:a2ui` module). If
    ///     `nil`, falls back to the process-wide default set via
    ///     `A2UISurfaceView.frameworkName`. Typical values are `"A2UICore"`
    ///     or `"A2UIDemo"`. See the doc comment on `frameworkName` for the
    ///     recommended one-time initialization pattern.
    public func initSurface(configJson: String? = nil, frameworkName: String? = nil) {
        guard !isInitialized else { return }
        isInitialized = true

        // Capture current view dimensions if available
        if bounds.width > 0 {
            lastKnownWidth = bounds.width
            // For self-sizing mode (height=0), use a large constraint height
            lastKnownHeight = bounds.height > 0 ? bounds.height : 10000
        }

        // Create render layer handler
        let contextParam = KuiklyContextParam()
        self.contextParam = contextParam
        let handler = KuiklyRenderLayerHandler(rootView: self, contextParam: contextParam)
        handler.didInit()
        renderLayerHandler = handler

        // Resolve the A2UICoreEntry Kotlin class at runtime (not import-time)
        // via `A2UIFrameworkClassResolver`. Framework-name precedence:
        //   1. The `frameworkName:` argument passed to this call, if non-nil.
        //   2. The process-wide default `A2UISurfaceView.frameworkName`.
        //   3. If both are nil, only the bare `@ObjCName` candidate is tried
        //      — which works when Kotlin/Native successfully stripped the
        //      framework prefix, but is fragile across K/N versions.
        //
        // Falling through with a nil `coreEntry` would just no-op every
        // subsequent callKotlinMethod call, which is a lousy failure mode —
        // assert loudly instead so integrators notice the missing A2UI
        // Kotlin framework early.
        let effectiveFrameworkName = frameworkName ?? Self.frameworkName
        let entryObjCName = "A2UICoreEntry"
        let candidateNames = A2UIFrameworkClassResolver.candidateNames(
            objCName: entryObjCName,
            frameworkName: effectiveFrameworkName
        )
        var resolvedEntry: A2UICoreEntryProtocol?
        var resolvedName: String?
        for name in candidateNames {
            guard let cls = NSClassFromString(name) as? NSObject.Type else { continue }
            let raw = cls.init()
            // The Kotlin/Native emitted class does not statically declare
            // conformance to our Swift `@objc` shadow protocol, so a plain
            // `as? A2UICoreEntryProtocol` cast fails even though the runtime
            // selectors line up 1:1. `unsafeBitCast` is safe here because
            // `@objc` protocol references are just `id`s at the ObjC level —
            // we only rely on selector dispatch, not on Swift protocol
            // witness tables. We *don't* pre-check `responds(to:)` because
            // K/N-emitted selector names can vary slightly across K/N
            // versions (e.g. an extra arg-label prefix). If the class name
            // matches, we trust it; if a later call goes wrong the crash
            // will point to the exact bad selector, which is far more
            // actionable than a generic "class not found".
            resolvedEntry = unsafeBitCast(raw, to: A2UICoreEntryProtocol.self)
            resolvedName = name
            break
        }
        guard let entry = resolvedEntry else {
            assertionFailure(
                "A2UISurfaceView: cannot resolve Kotlin class `\(entryObjCName)` at runtime. " +
                "Tried candidates: \(candidateNames.joined(separator: ", ")). " +
                "Make sure the host app links an A2UI Kotlin/Native framework and " +
                "that `A2UISurfaceView.frameworkName` (currently \(effectiveFrameworkName.map { "\"\($0)\"" } ?? "nil")) " +
                "matches the framework's binary name (e.g. \"A2UICore\" for pod 'a2ui', " +
                "\"A2UIDemo\" for pod 'a2ui_demo'). A2UI rendering will not function."
            )
            return
        }
        // Dynamically attach an ObjC protocol named
        // `<resolvedEntryClassName>Delegate` (e.g. `AUICA2UICoreEntryDelegate`)
        // to this class *before* we hand ourselves off as the delegate.
        // K/N generates that protocol name from the outer entry class's
        // ObjC name and uses `class_conformsToProtocol` to decide between
        // ObjC `msgSend` dispatch and Kotlin vtable dispatch. Failing this
        // check drops K/N into the vtable path — which crashes because
        // `A2UISurfaceView` is not a Kotlin implementer of the interface.
        //
        // We derive the protocol name from `resolvedName` rather than
        // hardcoding `A2UICoreEntryDelegate`, because Kotlin/Native does
        // not honor the `@ObjCName` on our (now-nested) `Delegate` — the
        // real name at runtime always follows the enclosing class's ObjC
        // name plus a `Delegate` suffix (mirrors Kuikly's
        // `KuiklyRenderFrameworkContextHandler.entryClassWithFrameworkName:`).
        if let resolvedEntryClassName = resolvedName {
            Self.ensureConformsToKotlinDelegateProtocol(entryClassName: resolvedEntryClassName)
        }

        entry.hrCoreDelegate = self
        coreEntry = entry

        // Track this surface so register/unregister broadcasts can find it.
        Self.liveSurfacesLock.lock()
        Self.liveSurfaces.add(self)
        Self.liveSurfacesLock.unlock()

        // Augment config with the real screen size (in points == dp) so full-screen
        // overlays (e.g. Modal/KRModalView) size their Kuikly flex frame to the whole
        // screen instead of the self-sizing card.
        let effectiveConfig = Self.injectDeviceSize(into: configJson)

        // Initialize the Kotlin A2UI instance on context queue
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            guard let self = self else { return }
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.initialize),
                arg0: self.instanceId,
                arg1: effectiveConfig,
                arg2: nil
            )
            // Replay every globally-registered custom function so this fresh
            // instance's Kotlin core learns about them.
            let snapshot = Self.snapshotDescriptors()
            for (name, spec) in snapshot {
                self.coreEntry?.callKotlinMethod(
                    methodId: Int32(KotlinMethod.registerCustomFunction),
                    arg0: self.instanceId,
                    arg1: name,
                    arg2: spec
                )
            }
            // Replay every globally-registered custom render view viewName so
            // this fresh instance's Kotlin core (A2UIApplier.createDSLView)
            // can distinguish custom components from unknown / malformed names.
            let viewNames = Self.snapshotCustomViewNames()
            for name in viewNames {
                self.coreEntry?.callKotlinMethod(
                    methodId: Int32(KotlinMethod.registerCustomView),
                    arg0: self.instanceId,
                    arg1: name,
                    arg2: nil
                )
            }
        })

        // Ensure layoutSubviews will be called after Auto Layout resolves our bounds.
        // This is critical for the WRAP_CONTENT flow where bounds.width is 0 at init time.
        setNeedsLayout()
    }

    /// Add `deviceWidth`/`deviceHeight` (in points == dp) to the config JSON so
    /// full-screen components can be laid out against the real screen size.
    private static func injectDeviceSize(into configJson: String?) -> String {
        let screen = UIScreen.main.bounds.size
        var obj: [String: Any] = [:]
        if let configJson = configJson,
           let data = configJson.data(using: .utf8),
           let parsed = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
            obj = parsed
        }
        obj["deviceWidth"] = Double(screen.width)
        obj["deviceHeight"] = Double(screen.height)
        if let data = try? JSONSerialization.data(withJSONObject: obj),
           let str = String(data: data, encoding: .utf8) {
            return str
        }
        return configJson ?? "{}"
    }

    public override func layoutSubviews() {
        super.layoutSubviews()
        if bounds.width > 0 && isInitialized {
            let newWidth = bounds.width
            // Determine effective height for layout constraint:
            // - If we haven't computed content height yet (contentHeight == 0), use a large value
            //   so the flex engine can freely determine content height (WRAP_CONTENT mode).
            //   This is critical because bounds.height may be the minimum height (e.g. 60pt)
            //   from the parent's greaterThanOrEqualTo constraint, which would incorrectly
            //   constrain the flex layout.
            // - If heightConstraint exists, we're in self-sizing mode, keep using large value.
            // - Otherwise (fixed height from parent), use bounds.height.
            let effectiveHeight: CGFloat
            if contentHeight == 0 || heightConstraint != nil || bounds.height <= 0 {
                effectiveHeight = 10000
            } else {
                effectiveHeight = bounds.height
            }

            // If there are buffered stream chunks waiting for valid dimensions, send them now
            if !pendingStreamChunks.isEmpty || pendingFinishStream {
                let chunks = pendingStreamChunks
                pendingStreamChunks.removeAll()
                let shouldFinish = pendingFinishStream
                pendingFinishStream = false
                lastKnownWidth = newWidth
                lastKnownHeight = effectiveHeight
                A2UIRenderHelper.perform(onContextQueue: { [weak self] in
                    guard let self = self else { return }
                    let sizeStr = "\(newWidth),\(effectiveHeight)"
                    self.coreEntry?.callKotlinMethod(
                        methodId: Int32(KotlinMethod.setSurfaceSize),
                        arg0: self.instanceId,
                        arg1: sizeStr,
                        arg2: nil
                    )
                    for chunk in chunks {
                        self.coreEntry?.callKotlinMethod(
                            methodId: Int32(KotlinMethod.writeStream),
                            arg0: self.instanceId,
                            arg1: chunk,
                            arg2: nil
                        )
                    }
                    if shouldFinish {
                        self.coreEntry?.callKotlinMethod(
                            methodId: Int32(KotlinMethod.finishStream),
                            arg0: self.instanceId,
                            arg1: nil,
                            arg2: nil
                        )
                    }
                })
            } else {
                // Only re-send if width actually changed (avoid infinite layout loops)
                guard newWidth != lastKnownWidth || (lastKnownHeight == 0 && effectiveHeight > 0) else { return }
                lastKnownWidth = newWidth
                lastKnownHeight = effectiveHeight
                let sizeStr = "\(newWidth),\(effectiveHeight)"
                A2UIRenderHelper.perform(onContextQueue: { [weak self] in
                    guard let self = self else { return }
                    self.coreEntry?.callKotlinMethod(
                        methodId: Int32(KotlinMethod.setSurfaceSize),
                        arg0: self.instanceId,
                        arg1: sizeStr,
                        arg2: nil
                    )
                    // If stream was already finished, trigger a re-layout with proper dimensions
                    if self.streamFinished {
                        self.coreEntry?.callKotlinMethod(
                            methodId: Int32(KotlinMethod.finishStream),
                            arg0: self.instanceId,
                            arg1: nil,
                            arg2: nil
                        )
                    }
                })
            }
        }
    }

    // MARK: - Public API

    /// Feed one A2UI envelope message into the surface's message stream.
    ///
    /// ### Contract (aligned with A2UI protocol v0.9 §Envelope message structure)
    /// A2UI's server-to-client channel is defined as **a stream of JSON objects**,
    /// where each object is a single envelope containing exactly one of:
    /// `createSurface`, `updateComponents`, `updateDataModel`, or `deleteSurface`.
    ///
    /// The recommended way to call this method is once per protocol message:
    /// ```swift
    /// surfaceView.writeStream(#"{"version":"v0.9","createSurface":{...}}"#)
    /// surfaceView.writeStream(#"{"version":"v0.9","updateComponents":{...}}"#)
    /// surfaceView.writeStream(#"{"version":"v0.9","updateDataModel":{...}}"#)
    /// ```
    ///
    /// ### Accepted input
    /// - A single, complete envelope object as a JSON string.
    /// - A JSONL / newline-delimited concatenation of envelope objects (also
    ///   permitted by the protocol as a valid transport framing) — the
    ///   underlying parser will split them by top-level `{ ... }` boundaries.
    /// - A partial JSON string that will be completed by subsequent
    ///   `writeStream` calls; the parser buffers characters until a top-level
    ///   `{ ... }` is closed.
    ///
    /// ### NOT accepted
    /// - A JSON array wrapping multiple envelopes (`[ {...}, {...} ]`).
    ///   The A2UI protocol does **not** define an array wrapper; it is a
    ///   convenience packaging that some demo assets use to store multiple
    ///   messages in a single file. Unpacking such an array is the
    ///   responsibility of the caller (demo / integration layer), which
    ///   should iterate the array and invoke `writeStream` once per envelope
    ///   object.
    ///
    ///   If the input starts with `[`, this method logs an error and returns
    ///   without dispatching anything. It will never crash on this input.
    ///
    /// If the view has not yet been laid out (width = 0), the call is
    /// buffered and will be dispatched once valid dimensions are available
    /// via `layoutSubviews`.
    ///
    /// - Parameter chunk: A single A2UI envelope JSON object (or a JSONL run
    ///   of them, or a continuation of a partial envelope from a previous call).
    public func writeStream(_ chunk: String) {
        ensureInitialized()
        // Reject non-protocol JSON-array wrapping. Per A2UI v0.9, server-to-client
        // is a stream of JSON objects; an outer array is not part of the protocol.
        // The caller (demo / integration layer) must unpack such arrays and feed
        // each envelope object individually.
        if let firstNonWs = chunk.first(where: { !$0.isWhitespace }), firstNonWs == "[" {
            NSLog("[A2UISurfaceView] writeStream: input starts with '[' — a JSON-array wrapper is NOT part of the A2UI protocol. Please unpack the array on the caller side and feed each envelope object via writeStream() individually. Input dropped.")
            return
        }
        let w = lastKnownWidth
        let h = lastKnownHeight
        if w <= 0 {
            // Buffer the chunk until we have valid dimensions
            pendingStreamChunks.append(chunk)
            return
        }
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            guard let self = self else { return }
            let effectiveH = h > 0 ? h : CGFloat(10000)
            let sizeStr = "\(w),\(effectiveH)"
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.setSurfaceSize),
                arg0: self.instanceId,
                arg1: sizeStr,
                arg2: nil
            )
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.writeStream),
                arg0: self.instanceId,
                arg1: chunk,
                arg2: nil
            )
        })
    }

    /// Mark the message stream as finished.
    ///
    /// If the view has not yet been laid out (width = 0), the finish is deferred
    /// until valid dimensions are available via layoutSubviews.
    public func finishStream() {
        streamFinished = true
        let w = lastKnownWidth
        let h = lastKnownHeight
        if w <= 0 {
            // Defer finish until we have valid dimensions
            pendingFinishStream = true
            return
        }
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            guard let self = self else { return }
            let effectiveH = h > 0 ? h : CGFloat(10000)
            let sizeStr = "\(w),\(effectiveH)"
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.setSurfaceSize),
                arg0: self.instanceId,
                arg1: sizeStr,
                arg2: nil
            )
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.finishStream),
                arg0: self.instanceId,
                arg1: nil,
                arg2: nil
            )
        })
    }

    /// Destroy the surface and clean up resources.
    public func destroy() {
        guard isInitialized else { return }
        isInitialized = false

        Self.liveSurfacesLock.lock()
        Self.liveSurfaces.remove(self)
        Self.liveSurfacesLock.unlock()

        let entry = coreEntry
        let id = instanceId
        coreEntry = nil

        A2UIRenderHelper.perform(onContextQueue: {
            entry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.destroy),
                arg0: id,
                arg1: nil,
                arg2: nil
            )
            entry?.destroy()
        })
    }

    // MARK: - KuiklyRenderView Compatibility

    /// Called by components (e.g. KRModalView) via hr_rootView to register cleanup tasks.
    /// This mimics KuiklyRenderView's performWhenRenderViewDeallocWithTask: method.
    @objc public func performWhenRenderViewDealloc(withTask task: @escaping () -> Void) {
        deallocTasks.append(task)
    }

    /// Called by components (e.g. KRImageView) via hr_rootView to schedule a task after first screen load.
    /// In A2UI there is no "first screen" concept, so we execute the task immediately.
    @objc public func performWhenViewDidLoad(withTask task: @escaping () -> Void) {
        task()
    }

    /// Called by components (e.g. KRImageView) via hr_rootView to get a module instance.
    /// This mimics KuiklyRenderView's moduleWithName: method.
    /// Currently supports KRMemoryCacheModule for base64 image loading.
    @objc public func moduleWithName(_ moduleName: String) -> NSObject? {
        if moduleName == "KRMemoryCacheModule" {
            return A2UIRenderHelper.sharedMemoryCacheModule() as? NSObject
        }
        return nil
    }

    deinit {
        // Execute all registered dealloc tasks
        for task in deallocTasks {
            task()
        }
        deallocTasks.removeAll()
    }

    // MARK: - Private

    private func ensureInitialized() {
        if !isInitialized {
            initSurface()
        }
    }

    /// Flush all pending UI tasks to main thread in one batch.
    /// Must be called from the context queue.
    private func flushUITasks() {
        guard !pendingUITasks.isEmpty else { return }
        let tasks = pendingUITasks
        pendingUITasks.removeAll()

        A2UIRenderHelper.perform(onMainQueue: {
            for task in tasks {
                task()
            }
        })
    }

    /// Add a UI task to the pending batch.
    /// Must be called from the context queue.
    private func postToMainThread(_ task: @escaping () -> Void) {
        pendingUITasks.append(task)
    }
}

// MARK: - A2UICoreEntryDelegateProtocol (Kotlin → Native)

extension A2UISurfaceView: A2UICoreEntryDelegateProtocol {

    /// Handle render commands from Kotlin A2UI logic.
    /// Shadow operations execute directly on context queue.
    /// View operations are collected and batch-flushed to main thread.
    ///
    /// Note: the Swift method name must match the protocol
    /// (`callNative(methodId:arg0:…)`). We *also* pin the ObjC selector
    /// explicitly here — `@objc(callNativeMethodId:arg0:…)` — because
    /// inheriting the protocol's selector through Swift's protocol
    /// conformance machinery has been observed to silently fall back to
    /// the auto-derived `callNativeWithMethodId:arg0:…` form on some
    /// Swift/toolchain combinations. Making it explicit here keeps the
    /// selector wire-compatible with Kotlin's
    /// `A2UICoreEntry.IOSDelegate.callNative` (which K/N emits as
    /// `callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:`).
    @objc(callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:)
    public func callNative(
        methodId: Int32,
        arg0: Any?,
        arg1: Any?,
        arg2: Any?,
        arg3: Any?,
        arg4: Any?,
        arg5: Any?
    ) -> Any? {
        guard let handler = renderLayerHandler else { return nil }

        switch Int(methodId) {
        case NativeMethod.createView:
            guard let tag = (arg1 as? NSNumber),
                  let viewName = arg2 as? String else { return nil }
            postToMainThread {
                A2UIRenderHelper.createRenderView(with: handler, tag: tag, viewName: viewName)
            }

        case NativeMethod.removeView:
            guard let tag = (arg1 as? NSNumber) else { return nil }
            postToMainThread {
                A2UIRenderHelper.removeRenderView(with: handler, tag: tag)
            }

        case NativeMethod.insertSubView:
            guard let parentTag = (arg1 as? NSNumber)?.intValue,
                  let childTag = (arg2 as? NSNumber),
                  let index = (arg3 as? NSNumber)?.intValue else { return nil }
            // Map A2UI's PLATFORM_ROOT_TAG (0) to Kuikly's ROOT_VIEW_TAG (-1)
            let effectiveParentTag = NSNumber(value: parentTag == 0 ? -1 : parentTag)
            // Track root view tag (first child inserted into platform root)
            if parentTag == 0 && rootViewTag == -1 {
                rootViewTag = childTag.intValue
            }
            postToMainThread {
                A2UIRenderHelper.insertSubView(with: handler, parentTag: effectiveParentTag, childTag: childTag, at: index)
            }

        case NativeMethod.setProp:
            guard let tag = (arg1 as? NSNumber),
                  let key = arg2 as? String,
                  let value = arg3 else { return nil }
            // Event registration (arg4 == 1), e.g. "click", "pan", "progressDidChanged".
            // The raw value from Kotlin is a Kotlin callback, NOT an ObjC block, so
            // passing it straight to the native layer crashes in setCss_<event>:
            // (_Block_copy on a non-block). Wrap it in a real block that dispatches the
            // event back to Kotlin, mirroring the Android render path.
            let isEvent = (arg4 as? NSNumber)?.intValue == 1
            if isEvent {
                let eventName = key
                let tagValue = tag.intValue
                let callback: @convention(block) (Any?) -> Void = { [weak self] result in
                    guard let self = self else { return }
                    let resultStr: String?
                    switch result {
                    case let s as String:
                        resultStr = s
                    case let dict as [AnyHashable: Any]:
                        if let data = try? JSONSerialization.data(withJSONObject: dict, options: []),
                           let json = String(data: data, encoding: .utf8) {
                            resultStr = json
                        } else {
                            resultStr = nil
                        }
                    case .none:
                        resultStr = nil
                    case .some(let other):
                        resultStr = "\(other)"
                    }
                    A2UIRenderHelper.perform(onContextQueue: { [weak self] in
                        guard let self = self else { return }
                        self.coreEntry?.callKotlinMethod(
                            methodId: Int32(KotlinMethod.viewEvent),
                            arg0: self.instanceId,
                            arg1: "\(tagValue)|\(eventName)",
                            arg2: resultStr
                        )
                    })
                }
                postToMainThread {
                    A2UIRenderHelper.applyProp(with: handler, tag: tag, key: key, value: callback)
                }
            }
            // When touchEnable is set to 1, just set the prop.
            // Click handling is done via Kuikly Core's standard click event
            // registered in A2UIApplier (cross-platform, works on all platforms).
            else if key == "touchEnable" && "\(value)" == "1" {
                clickableTags.insert(tag.intValue)
                postToMainThread {
                    A2UIRenderHelper.applyProp(with: handler, tag: tag, key: key, value: value)
                }
            } else {
                postToMainThread {
                    A2UIRenderHelper.applyProp(with: handler, tag: tag, key: key, value: value)
                }
            }

        case NativeMethod.setFrame:
            // Core sends: callNativeMethod(SET_RENDER_VIEW_FRAME, instanceId, tag, x, y, width, height)
            // So arg1=tag, arg2=x, arg3=y, arg4=width, arg5=height (all as NSNumber)
            guard let tag = (arg1 as? NSNumber),
                  let x = (arg2 as? NSNumber)?.doubleValue,
                  let y = (arg3 as? NSNumber)?.doubleValue,
                  let w = (arg4 as? NSNumber)?.doubleValue,
                  let h = (arg5 as? NSNumber)?.doubleValue else { return nil }
            let frame = CGRect(x: x, y: y, width: w, height: h)
            // If this is the root view frame, update content height for self-sizing
            if tag.intValue == rootViewTag {
                let newContentHeight = CGFloat(h)
                if newContentHeight != contentHeight && newContentHeight > 0 {
                    contentHeight = newContentHeight
                    postToMainThread { [weak self] in
                        guard let self = self else { return }
                        A2UIRenderHelper.applyFrame(with: handler, tag: tag, frame: frame)
                        self.invalidateIntrinsicContentSize()
                        // Update height constraint
                        if let hc = self.heightConstraint {
                            hc.constant = newContentHeight
                        } else {
                            let hc = self.heightAnchor.constraint(equalToConstant: newContentHeight)
                            hc.priority = .defaultHigh
                            hc.isActive = true
                            self.heightConstraint = hc
                        }
                        // Force parent (e.g. UIStackView) to re-layout with new height
                        self.superview?.setNeedsLayout()
                        self.superview?.superview?.setNeedsLayout()
                    }
                    return nil
                }
            }
            postToMainThread {
                A2UIRenderHelper.applyFrame(with: handler, tag: tag, frame: frame)
            }

        case NativeMethod.callViewMethod:
            guard let tag = (arg1 as? NSNumber),
                  let method = arg2 as? String else { return nil }
            let params = arg3 as? String
            postToMainThread {
                A2UIRenderHelper.callViewMethod(with: handler, tag: tag, method: method, params: params)
            }

        case NativeMethod.calculateRenderViewSize:
            // Core sends this for view size calculation
            guard let tag = (arg1 as? NSNumber),
                  let constraintWidth = (arg2 as? NSNumber)?.doubleValue,
                  let constraintHeight = (arg3 as? NSNumber)?.doubleValue else { return nil }
            // Guard: if this tag has no shadow registered (unknown/unsupported viewName),
            // return empty string so that Kotlin Shadow.calculateRenderViewSize falls back to Size(0, 0)
            // instead of triggering NSAssert("shadow can't be nil") inside KuiklyRenderLayerHandler.
            if unshadowedTags.contains(tag.intValue) { return "" }
            let constraintSize = CGSize(width: constraintWidth, height: constraintHeight)
            let size = A2UIRenderHelper.calculateSize(with: handler, tag: tag, constraintSize: constraintSize)
            return "\(size.width)|\(size.height)"

        case NativeMethod.callModuleMethod:
            // Forward KRMemoryCacheModule calls so that base64 image data stored
            // by Kotlin Core (via setMemoryCache) is available when KRImageView
            // retrieves it via [rootView moduleWithName:].
            // Note: KRMemoryCacheModule.setMemoryObjectWithKey requires main thread.
            guard let moduleName = arg1 as? String,
                  let method = arg2 as? String else { return nil }
            let params = arg3 as? String
            if moduleName == "KRMemoryCacheModule" {
                postToMainThread {
                    A2UIRenderHelper.callMemoryCacheModuleMethod(method, params: params)
                }
                return nil
            }
            if moduleName == "A2UIBridgeModule" {
                // Native-page mode routing: KMP's
                // `BridgeManager.callModuleMethod("A2UIBridgeModule", …)` lands
                // here instead of going through a Kuikly `KuiklyRenderCore`
                // (independent A2UISurfaceView doesn't own one). Resolving
                // locally keeps the three-platform "always go through
                // A2UIBridgeModule" invariant intact without introducing a
                // separate KMP-side bypass.
                //
                // Fresh instance per call is fine — the module is stateless
                // and the common case is `calculateCustomViewSize`
                // (syncCall=1) which returns a "w|h" NSString measured by
                // the registered `measurer`. We hop through
                // `NSClassFromString` + `-hrv_callWithMethod:params:callback:`
                // so this Swift file does not need to import the ObjC class
                // directly.
                guard let cls = NSClassFromString("A2UIBridgeModule") as? NSObject.Type else {
                    return nil
                }
                let instance = cls.init()
                let sel = NSSelectorFromString("hrv_callWithMethod:params:callback:")
                guard instance.responds(to: sel) else { return nil }
                typealias CallFn = @convention(c) (NSObject, Selector, NSString, Any?, Any?) -> AnyObject?
                let imp = instance.method(for: sel)
                let fn = unsafeBitCast(imp, to: CallFn.self)
                return fn(instance, sel, method as NSString, params, nil)
            }
            return nil

        case NativeMethod.setTimeout:
            // Timeout handling - not yet needed in A2UI
            break

        case NativeMethod.createShadow:
            // Shadow operations run directly on context queue
            guard let tag = (arg1 as? NSNumber),
                  let viewName = arg2 as? String else { return nil }
            // Only create shadow when the target class actually implements `hrv_createShadow`.
            // Otherwise we would insert `nil` into KuiklyRenderLayerHandler's shadowRegistry,
            // and any later shadow lookup would trip the `shadow can't be nil` NSAssert.
            // This happens when Kotlin's A2UICustomComponentView is instantiated for an
            // unknown / unmapped viewName (e.g. a stray `Tab` node in malformed JSON).
            if !A2UIRenderHelper.viewSupportsShadow(viewName) {
                unshadowedTags.insert(tag.intValue)
            } else {
                A2UIRenderHelper.createShadow(with: handler, tag: tag, viewName: viewName)
            }

        case NativeMethod.setShadowProp:
            guard let tag = (arg1 as? NSNumber),
                  let key = arg2 as? String,
                  let value = arg3 else { return nil }
            if unshadowedTags.contains(tag.intValue) { return nil }
            A2UIRenderHelper.applyShadowProp(with: handler, tag: tag, key: key, value: value)

        case NativeMethod.setShadowForView:
            guard let tag = (arg1 as? NSNumber) else { return nil }
            if unshadowedTags.contains(tag.intValue) { return nil }
            // Get shadow object on context queue (safe, shadow is NSObject not UIView)
            let shadow = A2UIRenderHelper.shadow(with: handler, tag: tag)
            if let shadow = shadow {
                // Get pre-main-queue task on context queue (safe, just creates a block)
                let preTask = A2UIRenderHelper.shadowPreTask(shadow)
                // Batch the actual UI work through postToMainThread to ensure correct ordering
                // with createView/setFrame (they all flush together)
                postToMainThread {
                    preTask?()
                    A2UIRenderHelper.applyShadow(with: handler, tag: tag, shadow: shadow)
                }
            }

        case NativeMethod.callShadowMethod:
            guard let tag = (arg1 as? NSNumber),
                  let method = arg2 as? String else { return nil }
            let params = arg3 as? String ?? ""
            if unshadowedTags.contains(tag.intValue) { return nil }
            return A2UIRenderHelper.callShadowMethod(with: handler, tag: tag, method: method, params: params)

        case NativeMethod.fireFatalException:
            // Fatal exception from Core - log it
            let message = arg1 as? String ?? "unknown"
            print("[A2UI] FATAL: \(message)")

        case NativeMethod.measureShadowSize:
            // Synchronous measurement - runs on context queue
            guard let tag = (arg1 as? NSNumber),
                  let constraintWidth = (arg2 as? NSNumber)?.doubleValue,
                  let constraintHeight = (arg3 as? NSNumber)?.doubleValue else { return nil }
            // If shadow was never created for this tag (unsupported viewName), report zero size.
            if unshadowedTags.contains(tag.intValue) { return "0.0,0.0" }
            let constraintSize = CGSize(width: constraintWidth, height: constraintHeight)
            let size = A2UIRenderHelper.calculateSize(with: handler, tag: tag, constraintSize: constraintSize)
            return "\(size.width),\(size.height)"

        case NativeMethod.removeShadow:
            guard let tag = (arg1 as? NSNumber) else { return nil }
            if unshadowedTags.remove(tag.intValue) != nil { return nil }
            A2UIRenderHelper.removeShadow(with: handler, tag: tag)

        case NativeMethod.callTDFModuleMethod:
            // TDF module method calls - not yet supported in A2UI demo
            break

        case NativeMethod.flushUITasks:
            flushUITasks()

        case NativeMethod.actionCallback:
            guard let tag = (arg1 as? NSNumber)?.intValue,
                  let actionJson = arg2 as? String else { return nil }
            let surfaceId = arg3 as? String ?? ""
            // Dispatch action to listener on main thread
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.surfaceListener?.a2uiSurfaceView?(self, didReceiveAction: actionJson, fromTag: tag, inSurface: surfaceId)
            }

        case NativeMethod.contentHeightChanged:
            guard let height = (arg1 as? NSNumber)?.floatValue else { return nil }
            let newContentHeight = CGFloat(height)
            if newContentHeight != contentHeight && newContentHeight > 0 {
                contentHeight = newContentHeight
                DispatchQueue.main.async { [weak self] in
                    self?.invalidateIntrinsicContentSize()
                    self?.heightConstraint?.constant = newContentHeight
                    self?.setNeedsLayout()
                }
            }

        case NativeMethod.showDateTimePicker:
            guard let tag = (arg1 as? NSNumber)?.intValue,
                  let configJson = arg2 as? String else { return nil }
            DispatchQueue.main.async { [weak self] in
                self?.showDateTimePickerSheet(tag: tag, configJson: configJson)
            }

        case NativeMethod.dataModelChanged:
            guard let dataModelJson = arg1 as? String else { return nil }
            let changedKey = arg2 as? String ?? ""
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.surfaceListener?.a2uiSurfaceView?(self, didChangeDataModel: dataModelJson, forKey: changedKey)
            }

        case NativeMethod.executeFunctionCall:
            guard let functionName = arg1 as? String else { return nil }
            let argsJson = arg2 as? String ?? "{}"
            DispatchQueue.main.async { [weak self] in
                self?.executeFunctionCall(functionName, argsJson: argsJson)
            }

        case NativeMethod.invokeCustomFunction:
            // Custom user function callback. Called on the Kuikly Context Queue.
            // arg1: name, arg2: argsJson, arg3: "returnType|invokeMode"
            guard let name = arg1 as? String,
                  let spec = arg3 as? String else { return nil }
            let argsJson = arg2 as? String ?? "{}"
            return invokeCustomFunctionLocally(name: name, argsJson: argsJson, spec: spec)

        default:
            break
        }

        return nil
    }
}

// MARK: - Tap Handling

extension A2UISurfaceView {

    @objc private func handleViewTap(_ gesture: UITapGestureRecognizer) {
        guard let tapGesture = gesture as? A2UITapGestureRecognizer else { return }
        let tag = tapGesture.viewTag
        // Dispatch click event to Kotlin on context queue
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            guard let self = self else { return }
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.viewClick),
                arg0: self.instanceId,
                arg1: "\(tag)",
                arg2: nil
            )
        })
    }
}

// MARK: - A2UISurfaceViewDelegate Protocol

/// Delegate protocol for A2UISurfaceView events.
/// Allows the hosting ViewController to receive action callbacks.
@objc public protocol A2UISurfaceViewDelegate: AnyObject {
    /// Called when an action is dispatched from a user interaction
    @objc optional func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didReceiveAction action: String, fromTag tag: Int, inSurface surfaceId: String)
    /// Called when a new surface is created
    @objc optional func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didCreateSurface surfaceId: String)
    /// Called when the message stream is finished
    @objc optional func a2uiSurfaceViewDidFinishStream(_ surfaceView: A2UISurfaceView)
    /// Called when an error occurs
    @objc optional func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didEncounterError message: String)
    /// Called when the data model changes due to user interaction (two-way binding).
    /// - Parameters:
    ///   - dataModelJson: Full JSON snapshot of the current data model
    ///   - changedKey: The specific key that was changed
    @objc optional func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didChangeDataModel dataModelJson: String, forKey changedKey: String)
}

// MARK: - A2UITapGestureRecognizer

/// Custom tap gesture recognizer that carries the view tag.
private class A2UITapGestureRecognizer: UITapGestureRecognizer {
    let viewTag: Int

    init(tag: Int, target: Any?, action: Selector?) {
        self.viewTag = tag
        super.init(target: target, action: action)
    }
}

// MARK: - Date/Time Picker

extension A2UISurfaceView {

    // MARK: - FunctionCall Execution

    /// Execute a client-side function call per A2UI protocol.
    /// Handles standard functions like openUrl.
    ///
    /// `showAlert` was removed from the built-in set: per A2UI v0.9 spec
    /// only `openUrl` is a standard `action.functionCall` intent, so hosts
    /// that want an alert must emit an `action.event` (e.g. `event.name =
    /// "toast"`) and pop the dialog themselves in their delegate.
    ///
    /// - Parameters:
    ///   - functionName: The function name (e.g. "openUrl")
    ///   - argsJson: JSON string of function arguments
    private func executeFunctionCall(_ functionName: String, argsJson: String) {
        guard let data = argsJson.data(using: .utf8),
              let args = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] else {
            print("[A2UI] Failed to parse functionCall args: \(argsJson)")
            return
        }

        switch functionName {
        case "openUrl":
            let urlStr = args["url"] as? String ?? ""
            if let url = URL(string: urlStr) {
                UIApplication.shared.open(url)
            }

        default:
            print("[A2UI] Unknown functionCall: \(functionName)(\(argsJson))")
        }
    }

    // MARK: - Custom Function Execution

    /// Forward a global `registerCustomFunction` into this surface's Kotlin core.
    fileprivate func dispatchRegisterCustomFunction(name: String, spec: String) {
        let id = instanceId
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            self?.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.registerCustomFunction),
                arg0: id,
                arg1: name,
                arg2: spec
            )
        })
    }

    /// Companion to `dispatchRegisterCustomFunction`.
    fileprivate func dispatchUnregisterCustomFunction(name: String) {
        let id = instanceId
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            self?.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.unregisterCustomFunction),
                arg0: id,
                arg1: name,
                arg2: nil
            )
        })
    }

    /// Forward a global `registerCustomRenderView` into this surface's Kotlin core
    /// so that the DSL applier can decide between A2UICustomComponentView and a
    /// DivView fallback for unknown/malformed viewNames.
    fileprivate func dispatchRegisterCustomView(viewName: String) {
        let id = instanceId
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            self?.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.registerCustomView),
                arg0: id,
                arg1: viewName,
                arg2: nil
            )
        })
    }

    /// Companion to `dispatchRegisterCustomView`.
    fileprivate func dispatchUnregisterCustomView(viewName: String) {
        let id = instanceId
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            self?.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.unregisterCustomView),
                arg0: id,
                arg1: viewName,
                arg2: nil
            )
        })
    }

    /// Execute a registered custom function on this surface's behalf.
    ///
    /// Threading: called from the NativeBridge delegate on the Kuikly Context
    /// Queue. For SYNC handlers we invoke inline and return the raw value to
    /// Kotlin; for ASYNC void handlers we post to main queue and return nil.
    fileprivate func invokeCustomFunctionLocally(name: String, argsJson: String, spec: String) -> Any? {
        guard let handler = A2UISurfaceView.lookupHandler(name: name) else { return nil }
        let args: [String: Any]
        if let data = argsJson.data(using: .utf8),
           let parsed = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
            args = parsed
        } else {
            args = [:]
        }
        let parts = spec.split(separator: "|")
        let returnType = parts.count > 0 ? String(parts[0]).lowercased() : "string"
        let invokeMode = parts.count > 1 ? String(parts[1]).lowercased() : "sync"

        if returnType == "void" && invokeMode == "async" {
            // Fire-and-forget on the main queue.
            DispatchQueue.main.async {
                _ = handler(args)
            }
            return nil
        }

        // SYNC path (also used for void|sync): execute inline.
        // NOTE: handlers MUST return quickly; this call blocks the Kuikly
        //       Context Queue that drives A2UI rendering.
        let raw = handler(args)
        switch raw {
        case .none:
            return nil
        case let s as String:
            return s
        case let b as Bool:
            return b
        case let n as NSNumber:
            return n
        case let arr as [Any]:
            // Encode arrays to JSON so the Kotlin coercer treats them as arrays.
            if let data = try? JSONSerialization.data(withJSONObject: arr, options: []),
               let json = String(data: data, encoding: .utf8) {
                return json
            }
            return "[]"
        case .some(let other):
            return "\(other)"
        }
    }

    // MARK: - Date/Time Picker

    /// Show a native date/time picker using UIDatePicker in an action sheet style.
    /// Uses UIAlertController with a UIDatePicker embedded for a clean, full-screen experience.
    ///
    /// - Parameters:
    ///   - tag: The A2UI protocol tag of the DateTimeInput component
    ///   - configJson: JSON config: {"tag":N,"enableDate":bool,"enableTime":bool,"value":"ISO8601","label":"..."}
    func showDateTimePickerSheet(tag: Int, configJson: String) {
        guard let data = configJson.data(using: .utf8),
              let config = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] else { return }

        let enableDate = config["enableDate"] as? Bool ?? true
        let enableTime = config["enableTime"] as? Bool ?? false
        let value = config["value"] as? String ?? ""
        let label = config["label"] as? String ?? "Select"
        let minStr = config["min"] as? String ?? ""
        let maxStr = config["max"] as? String ?? ""

        // Parse current value into a Date (use current date if empty)
        let initialDate: Foundation.Date
        if value.isEmpty {
            initialDate = Foundation.Date()
        } else {
            var components = DateComponents()
            components.year = 2025; components.month = 1; components.day = 1
            components.hour = 0; components.minute = 0

            let datePart = value.components(separatedBy: "T").first ?? value
            let dateFields = datePart.components(separatedBy: "-")
            if dateFields.count >= 3 {
                components.year = Int(dateFields[0]) ?? 2025
                components.month = Int(dateFields[1]) ?? 1
                components.day = Int(dateFields[2]) ?? 1
            }
            if value.contains("T") {
                let timePart = value.components(separatedBy: "T").last?
                    .components(separatedBy: "Z").first?
                    .components(separatedBy: "+").first ?? ""
                let timeFields = timePart.components(separatedBy: ":")
                if timeFields.count >= 2 {
                    components.hour = Int(timeFields[0]) ?? 0
                    components.minute = Int(timeFields[1]) ?? 0
                }
            }

            let calendar = Calendar.current
            initialDate = calendar.date(from: components) ?? Foundation.Date()
        }

        // Parse min/max constraints into Date objects
        let minimumDate = parseDateTimeString(minStr)
        let maximumDate: Foundation.Date?
        if !maxStr.isEmpty && !maxStr.contains("T") && !maxStr.contains(":") {
            // Pure date max: set to end of day (23:59) so the entire day is selectable
            maximumDate = parseDateTimeString(maxStr + "T23:59:00")
        } else {
            maximumDate = parseDateTimeString(maxStr)
        }

        // Determine picker mode
        let pickerMode: UIDatePicker.Mode
        if enableDate && enableTime {
            pickerMode = .dateAndTime
        } else if enableTime {
            pickerMode = .time
        } else {
            pickerMode = .date
        }

        // Create alert controller with UIDatePicker
        let alertController = UIAlertController(title: label, message: "\n\n\n\n\n\n\n\n\n\n\n", preferredStyle: .actionSheet)

        let datePicker = UIDatePicker()
        datePicker.datePickerMode = pickerMode
        datePicker.date = initialDate
        if let minDate = minimumDate {
            datePicker.minimumDate = minDate
        }
        if let maxDate = maximumDate {
            datePicker.maximumDate = maxDate
        }
        if #available(iOS 13.4, *) {
            datePicker.preferredDatePickerStyle = .wheels
        }
        datePicker.frame = CGRect(x: 0, y: 40, width: alertController.view.bounds.width - 20, height: 220)
        datePicker.autoresizingMask = [.flexibleWidth]
        alertController.view.addSubview(datePicker)

        // Done action
        let doneAction = UIAlertAction(title: "Done", style: .default) { [weak self] _ in
            let selectedDate = datePicker.date
            let cal = Calendar.current
            let y = cal.component(.year, from: selectedDate)
            let m = cal.component(.month, from: selectedDate)
            let d = cal.component(.day, from: selectedDate)
            let h = cal.component(.hour, from: selectedDate)
            let min = cal.component(.minute, from: selectedDate)

            let isoString = String(format: "%04d-%02d-%02dT%02d:%02d:00Z", y, m, d, h, min)
            self?.sendPickerResult(tag: tag, isoString: isoString)
        }
        alertController.addAction(doneAction)

        // Cancel action
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        alertController.addAction(cancelAction)

        // Present from the nearest view controller
        if let viewController = findViewController() {
            viewController.present(alertController, animated: true, completion: nil)
        }
    }

    /// Send picker result back to Kotlin layer.
    private func sendPickerResult(tag: Int, isoString: String) {
        A2UIRenderHelper.perform(onContextQueue: { [weak self] in
            guard let self = self else { return }
            self.coreEntry?.callKotlinMethod(
                methodId: Int32(KotlinMethod.pickerResult),
                arg0: self.instanceId,
                arg1: "\(tag)|\(isoString)",
                arg2: nil
            )
        })
    }

    /// Parse an ISO 8601 date/time string into a Date object.
    /// Supports formats: "2026-06-01", "09:00:00", "2026-06-01T09:00:00", "2026-06-01T09:00:00Z"
    private func parseDateTimeString(_ str: String) -> Foundation.Date? {
        if str.isEmpty { return nil }

        let calendar = Calendar.current
        var components = DateComponents()

        if str.contains("T") {
            // Full ISO 8601: "2026-06-01T09:00:00Z"
            let datePart = str.components(separatedBy: "T").first ?? ""
            let dateFields = datePart.components(separatedBy: "-")
            if dateFields.count >= 3 {
                components.year = Int(dateFields[0])
                components.month = Int(dateFields[1])
                components.day = Int(dateFields[2])
            }
            let timePart = str.components(separatedBy: "T").last?
                .components(separatedBy: "Z").first?
                .components(separatedBy: "+").first ?? ""
            let timeFields = timePart.components(separatedBy: ":")
            if timeFields.count >= 2 {
                components.hour = Int(timeFields[0])
                components.minute = Int(timeFields[1])
            }
        } else if str.contains(":") {
            // Pure time: "09:00:00" or "09:00"
            let timeFields = str.components(separatedBy: ":")
            if timeFields.count >= 2 {
                components.hour = Int(timeFields[0])
                components.minute = Int(timeFields[1])
            }
            // Use a reference date for time-only constraints
            components.year = 2000; components.month = 1; components.day = 1
        } else if str.contains("-") {
            // Pure date: "2026-06-01"
            let dateFields = str.components(separatedBy: "-")
            if dateFields.count >= 3 {
                components.year = Int(dateFields[0])
                components.month = Int(dateFields[1])
                components.day = Int(dateFields[2])
            }
            components.hour = 0; components.minute = 0
        } else {
            return nil
        }

        return calendar.date(from: components)
    }

    /// Find the nearest UIViewController that can present the picker.
    private func findViewController() -> UIViewController? {
        var responder: UIResponder? = self
        while let nextResponder = responder?.next {
            if let vc = nextResponder as? UIViewController {
                return vc
            }
            responder = nextResponder
        }
        return nil
    }
}

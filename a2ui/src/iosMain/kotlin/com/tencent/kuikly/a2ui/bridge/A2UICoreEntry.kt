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

package com.tencent.kuikly.a2ui.bridge

import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.core.nvi.NativeBridge
import kotlin.experimental.ExperimentalObjCName

/**
 * iOS A2UI Core Entry class.
 *
 * This is the entry point for Native (iOS/ObjC/Swift) to call into Kotlin A2UI logic.
 * It mirrors the pattern of KSP-generated KuiklyCoreEntry in Kuikly DSL,
 * but is hand-written since A2UI doesn't need @Page annotation scanning.
 *
 * The @ObjCName annotation ensures this class is accessible from ObjC/Swift as "A2UICoreEntry".
 *
 * Each A2UICoreEntry instance represents one A2UI rendering instance.
 * The native layer (A2UISurfaceView) creates one entry per surface view.
 *
 * Usage from iOS native (ObjC):
 * ```objc
 * A2UICoreEntry *entry = [[A2UICoreEntry alloc] init];
 * entry.hrCoreDelegate = self;
 * [entry callKotlinMethodMethodId:1 arg0:instanceId arg1:configJson arg2:nil];
 * [entry callKotlinMethodMethodId:2 arg0:instanceId arg1:chunk arg2:nil];
 * [entry callKotlinMethodMethodId:3 arg0:instanceId arg1:nil arg2:nil];
 * [entry callKotlinMethodMethodId:4 arg0:instanceId arg1:nil arg2:nil];
 * ```
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICoreEntry")
class A2UICoreEntry {

    /**
     * Delegate for Kotlin → Native communication.
     * Must be set before calling [callKotlinMethod].
     * Named "hrCoreDelegate" to match Kuikly DSL's convention.
     *
     * NOTE: [Delegate] is declared as a **nested** interface with **no
     * `@ObjCName`** on either the interface or its method. This exactly
     * mirrors the KSP-generated `KuiklyCoreEntry.Delegate` in Kuikly's
     * `IOSTargetEntryBuilder`, and it is critical for correctness.
     *
     * If the interface is promoted to a top-level Kotlin declaration
     * (with or without `@ObjCName`), Kotlin/Native emits a Kotlin-side
     * trampoline and dispatches `hrCoreDelegate?.callNative(...)` through
     * the Kotlin vtable — which crashes with EXC_BAD_ACCESS when the
     * actual delegate is a Swift/ObjC object rather than a Kotlin
     * implementer. Keep the interface nested and unannotated.
     *
     * The render pod (`OpenA2UIIOSRender`) attaches a runtime-allocated
     * ObjC protocol named `A2UICoreEntryDelegate` to its Swift
     * `A2UISurfaceView` class (via `class_addProtocol`) so K/N's ObjC
     * conformance check succeeds and dispatch drops down to
     * `objc_msgSend` on the delegate.
     */
    @ObjCName("hrCoreDelegate")
    var hrCoreDelegate: Delegate? = null

    private var instanceId: String? = null
    private var hadRegisterNativeBridge = false

    /**
     * Call a Kotlin method from native.
     *
     * @param methodId One of [A2UIKotlinMethod] constants
     * @param arg0 First argument (typically instanceId)
     * @param arg1 Second argument (varies by method)
     * @param arg2 Third argument (varies by method)
     */
    // Kotlin/Native builds the final ObjC selector by appending each
    // parameter's name (first-letter-uppercased) after the method label.
    // So `@ObjCName("callKotlinMethod")` + first param `methodId` yields the
    // real selector `callKotlinMethodMethodId:arg0:arg1:arg2:`, which is what
    // the render pod's Swift shadow protocol expects. Do **not** pre-bake the
    // first parameter's name into the @ObjCName value — K/N will append it
    // again, producing `callKotlinMethodMethodIdMethodId:...` and every
    // caller will hit `unrecognized selector`.
    @ObjCName("callKotlinMethod")
    fun callKotlinMethod(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?) {
        // Initialize BridgeManager on first call
        if (!A2UIBridgeManager.isDidInit()) {
            A2UIBridgeManager.init()
        }

        // Register NativeBridge on first call for this instance
        val currentInstanceId = arg0 as? String ?: return
        if (!hadRegisterNativeBridge) {
            hadRegisterNativeBridge = true
            instanceId = currentInstanceId

            val nativeBridge = NativeBridge()
            val delegate = hrCoreDelegate
            nativeBridge.iosNativeBridgeDelegate = object : NativeBridge.IOSNativeBridgeDelegate {
                override fun callNative(
                    methodId: Int,
                    arg0: Any?,
                    arg1: Any?,
                    arg2: Any?,
                    arg3: Any?,
                    arg4: Any?,
                    arg5: Any?
                ): Any? {
                    return delegate?.callNative(methodId, arg0, arg1, arg2, arg3, arg4, arg5)
                }
            }
            A2UIBridgeManager.registerNativeBridge(currentInstanceId, nativeBridge)
        }

        // Dispatch to BridgeManager
        A2UIBridgeManager.callKotlinMethod(methodId, arg0, arg1, arg2)
    }

    /**
     * Destroy this entry and clean up.
     */
    @ObjCName("destroy")
    fun destroy() {
        instanceId?.let { id ->
            A2UIBridgeManager.callKotlinMethod(A2UIKotlinMethod.DESTROY, id, null, null)
        }
        hrCoreDelegate = null
        instanceId = null
        hadRegisterNativeBridge = false
    }

    /**
     * Nested delegate interface for Kotlin → Native callbacks.
     *
     * Intentionally has **no `@ObjCName`** — neither on the interface
     * itself nor on `callNative`. Kotlin/Native therefore treats it as
     * a plain Kotlin type and emits `id`-based ObjC dispatch for
     * `hrCoreDelegate?.callNative(...)`, which is exactly what we
     * need to hand off to the Swift `A2UISurfaceView` delegate via
     * `objc_msgSend`. See the KDoc on [hrCoreDelegate] and the mirror
     * pattern in Kuikly's `IOSTargetEntryBuilder`.
     *
     * The ObjC selector K/N generates for this method is
     * `callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:` (K/N appends
     * the first parameter name automatically). The Swift
     * `A2UISurfaceView.callNative(...)` implementation is declared
     * with the matching `@objc(callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:)`
     * attribute.
     */
    interface Delegate {
        fun callNative(
            methodId: Int,
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?
        ): Any?
    }
}

/**
 * ObjC-facing bridge over the commonMain [A2UICustomViewRegistry] singleton.
 *
 * Purpose: expose the KMP-side custom-view registry to iOS native code
 * (specifically the render pod, `OpenA2UIIOSRender`) **without** forcing
 * that pod to `import A2UICore` at compile time. The render pod resolves
 * this class at runtime via `NSClassFromString("A2UICustomViewRegistryBridge")`,
 * so it can work equally well whether the app links `A2UICore.framework`
 * (Native-page scenario) or `A2UIDemo.framework` (Kuikly-page scenario)
 * — the same Kotlin source is compiled into whichever framework ships
 * with the app, and only one is ever present in a given build.
 *
 * The `@ObjCName` annotation pins the exposed ObjC class name so
 * Kotlin/Native never prefixes it with the enclosing framework name.
 *
 * Selectors (must match what render-pod's Swift shadow protocol expects):
 *   - `+shared`                         → the process-global singleton
 *   - `-registerViewName:`              → forward to `A2UICustomViewRegistry.register`
 *   - `-unregisterViewName:`            → forward to `A2UICustomViewRegistry.unregister`
 *
 * Intentionally kept as a class (not `object`) so ObjC callers can do
 * `[[A2UICustomViewRegistryBridge shared] registerViewName:@"..."]`,
 * mirroring the idiomatic Swift form `A2UICustomViewRegistryBridge.shared.register(viewName:)`.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICustomViewRegistryBridge")
class A2UICustomViewRegistryBridge private constructor() {

    // Real ObjC selector emitted by K/N is `registerViewName:` — the first
    // parameter's name (`viewName`) is appended automatically. Don't pre-bake
    // it into the @ObjCName value.
    @ObjCName("register")
    fun register(viewName: String) {
        A2UICustomViewRegistry.register(viewName)
    }

    @ObjCName("unregister")
    fun unregister(viewName: String) {
        A2UICustomViewRegistry.unregister(viewName)
    }

    companion object {
        @ObjCName("shared")
        val shared: A2UICustomViewRegistryBridge = A2UICustomViewRegistryBridge()
    }
}

/**
 * ObjC-facing bridge over the commonMain [A2UIIconRegistry] singleton.
 *
 * Purpose: expose the KMP-side icon registry to iOS native code
 * (specifically the render pod, `OpenA2UIIOSRender`) **without** forcing
 * that pod to `import A2UICore` at compile time. The render pod resolves
 * this class at runtime via `NSClassFromString("A2UIIconRegistryBridge")`,
 * so it can work equally well whether the app links `A2UICore.framework`
 * (Native-page scenario) or `A2UIDemo.framework` (Kuikly-page scenario)
 * — the same Kotlin source is compiled into whichever framework ships
 * with the app, and only one is ever present in a given build.
 *
 * The `@ObjCName` annotation pins the exposed ObjC class name so
 * Kotlin/Native never prefixes it with the enclosing framework name.
 *
 * Selectors (must match what render-pod's Swift shadow protocol expects):
 *   - `+shared`                    → the process-global singleton
 *   - `-registerIconName:uri:`     → forward to `A2UIIconRegistry.registerIcon`
 *   - `-unregisterIconName:`       → forward to `A2UIIconRegistry.unregisterIcon`
 *
 * Only `registerIcon` / `unregisterIcon` are bridged because those are the
 * only entry points used by the render pod's public Swift API. The rest of
 * the [A2UIIconRegistry] surface (resolveIconUriOrFallback, isEmpty, …) is
 * used from Kotlin common code only and does not need to cross the ObjC
 * boundary.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UIIconRegistryBridge")
class A2UIIconRegistryBridge private constructor() {

    // Real ObjC selector emitted by K/N is `registerIconName:uri:` — the
    // first parameter's name (`name`) is appended automatically. Don't
    // pre-bake it into the @ObjCName value.
    @ObjCName("registerIcon")
    fun registerIcon(name: String, uri: String) {
        A2UIIconRegistry.registerIcon(name, uri)
    }

    @ObjCName("unregisterIcon")
    fun unregisterIcon(name: String) {
        A2UIIconRegistry.unregisterIcon(name)
    }

    companion object {
        @ObjCName("shared")
        val shared: A2UIIconRegistryBridge = A2UIIconRegistryBridge()
    }
}

/**
 * ObjC-facing bridge over the commonMain [A2UICustomFunctionRegistry] singleton.
 *
 * Purpose: expose the KMP-side custom-function metadata registry to iOS
 * native code (specifically the render pod, `OpenA2UIIOSRender`) **without**
 * forcing that pod to `import A2UICore` at compile time. The render pod
 * resolves this class at runtime via
 * `NSClassFromString("A2UICustomFunctionRegistryBridge")`, so it can work
 * equally well whether the app links `A2UICore.framework` (Native-page
 * scenario) or `A2UIDemo.framework` (Kuikly-page scenario) — the same
 * Kotlin source is compiled into whichever framework ships with the app,
 * and only one is ever present in a given build.
 *
 * The `@ObjCName` annotation pins the exposed ObjC class name so
 * Kotlin/Native never prefixes it with the enclosing framework name.
 *
 * Design note — why *this* bridge exists on top of the per-instance
 * `A2UICoreEntry.callKotlinMethod(REGISTER_CUSTOM_FUNCTION, …)` path:
 *
 *   In the standalone `A2UISurfaceView` (Native-page) mode the render pod
 *   forwards every registration through the surface's own `A2UICoreEntry`
 *   using the `REGISTER_CUSTOM_FUNCTION` `A2UIKotlinMethod` — that codepath
 *   still exists and this bridge does not replace it.
 *
 *   In the Kuikly-page embedded mode (`A2UISurfaceDSLView` hosted inside a
 *   KuiklyBaseView), there is *no* per-surface `A2UICoreEntry` — the whole
 *   A2UI stack reuses the enclosing page's Kuikly NativeBridge. So the
 *   render pod's `broadcastRegister` to `liveSurfaces` fans out to zero
 *   recipients and the KMP-side `A2UICustomFunctionRegistry` stays empty.
 *   The DSL applier's `evaluateCustomFunctionAsString` then falls through
 *   with `descriptor-not-found` and Text nodes bound to those functions
 *   render as empty strings.
 *
 *   This bridge fixes that gap by exposing a *process-global* register /
 *   unregister entry point that the render pod calls directly on every
 *   `A2UISurfaceView.registerCustomFunction(…)`, mirroring how
 *   [A2UIIconRegistryBridge] and [A2UICustomViewRegistryBridge] already
 *   operate. `A2UICustomFunctionRegistry` itself is a JVM-singleton
 *   `object`, so writes from both paths (this bridge + the per-instance
 *   `REGISTER_CUSTOM_FUNCTION` messages) are idempotent.
 *
 * Selectors (must match what render-pod's Swift shadow protocol expects):
 *   - `+shared`                      → the process-global singleton
 *   - `-registerName:spec:`          → forward to `A2UICustomFunctionRegistry.register(name, spec)`
 *   - `-unregisterName:`             → forward to `A2UICustomFunctionRegistry.unregister(name)`
 *
 * The `spec` wire format is the same `"returnType|invokeMode"` string used
 * by `REGISTER_CUSTOM_FUNCTION` — e.g. `"string|sync"`, `"void|async"` —
 * so the Swift caller can reuse its existing descriptor string verbatim.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICustomFunctionRegistryBridge")
class A2UICustomFunctionRegistryBridge private constructor() {

    // Real ObjC selector emitted by K/N is `registerName:spec:` — the
    // first parameter's name (`name`) is appended automatically. Don't
    // pre-bake it into the @ObjCName value.
    @ObjCName("register")
    fun register(name: String, spec: String) {
        A2UICustomFunctionRegistry.register(name, spec)
    }

    @ObjCName("unregister")
    fun unregister(name: String) {
        A2UICustomFunctionRegistry.unregister(name)
    }

    companion object {
        @ObjCName("shared")
        val shared: A2UICustomFunctionRegistryBridge = A2UICustomFunctionRegistryBridge()
    }
}

/**
 * ObjC-facing bridge that lets the iOS Swift/ObjC layer push a native
 * date/time picker result back into the KMP `A2UIBridgeManager`.
 *
 * Why this exists
 * ---------------
 * In **Kuikly-page-embedded mode** iOS no longer has a per-surface
 * `A2UICoreEntry` to transport `PICKER_RESULT` through — the KMP core
 * now reaches the native side exclusively via the standard Kuikly
 * Module channel (`A2UIBridgeModule.showDateTimePicker`). The picker,
 * however, is user-driven and the result is produced asynchronously
 * long after `callModuleMethod` returns, so it cannot be delivered
 * through the module's synchronous return value.
 *
 * The symmetric solution — mirroring the OHOS
 * `a2uiNotifyPickerResult` NAPI writer and the Android
 * `A2UIAndroidNativeMethodHandler.sendPickerResult` → `callKotlinMethod`
 * path — is to let the Swift module expose a tiny top-level bridge that
 * reaches straight back into `A2UIBridgeManager.callKotlinMethod(
 * PICKER_RESULT, …)` on the Kuikly Context Thread.
 *
 * Selectors (must match what Swift's `A2UIBridgeModule` expects):
 *   - `+shared`                         → the process-global singleton
 *   - `-sendDslInstanceId:tag:isoString:` → forward to
 *     `A2UIBridgeManager.callKotlinMethod(PICKER_RESULT, dslInstanceId,
 *     "<tag>|<isoString>", nil)`.
 *
 * Why not call `A2UIBridgeManager.callKotlinMethod` directly from Swift
 * ---------------------------------------------------------------------
 * `A2UIBridgeManager` is a Kotlin `object` with multi-arg methods, which
 * K/N exposes as class-level ObjC selectors with mangled names
 * (`A2UIBridgeManagerCompanion.callKotlinMethodMethodId:arg0:arg1:arg2:`).
 * That is callable but fragile across K/N versions. Wrapping it with
 * this bridge pins the public ObjC selector to a stable shape and keeps
 * every K/N-generated name internal to this module.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UIPickerResultBridge")
class A2UIPickerResultBridge private constructor() {

    /**
     * Push the picker result back into the KMP core.
     *
     * Thread-safe wrt. the KMP side because [A2UIBridgeManager.callKotlinMethod]
     * for [A2UIKotlinMethod.PICKER_RESULT] only touches `dslBridgeMap` and
     * dispatches `handlePickerResult` → `A2UIDSLBridge.handlePickerResult`
     * synchronously. The Swift caller is expected to invoke us on the
     * Kuikly Context Queue (via `A2UIRenderHelper.perform(onContextQueue:)`).
     */
    // Real ObjC selector emitted by K/N is
    // `sendDslInstanceId:tag:isoString:` — the first parameter's name
    // (`dslInstanceId`) is appended automatically. Don't pre-bake it
    // into the @ObjCName value.
    @ObjCName("send")
    fun send(dslInstanceId: String, tag: Int, isoString: String) {
        A2UIBridgeManager.callKotlinMethod(
            A2UIKotlinMethod.PICKER_RESULT,
            dslInstanceId,
            "$tag|$isoString",
            null
        )
    }

    companion object {
        @ObjCName("shared")
        val shared: A2UIPickerResultBridge = A2UIPickerResultBridge()
    }
}

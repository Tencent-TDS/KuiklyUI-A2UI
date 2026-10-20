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

package com.tencent.kuikly.a2ui.demo.bridge

import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod
import com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.core.nvi.NativeBridge
import kotlin.experimental.ExperimentalObjCName

// ─────────────────────────────────────────────────────────────────────────
// Why these classes live here (in :demo/iosMain) instead of :a2ui/iosMain
// ─────────────────────────────────────────────────────────────────────────
//
// This file is a near-verbatim copy of
//   a2ui/src/iosMain/kotlin/com/tencent/kuikly/a2ui/bridge/A2UICoreEntry.kt
// with only two differences:
//   1. the Kotlin package is `com.tencent.kuikly.a2ui.demo.bridge`
//      (so the Kotlin FQN does not collide with :a2ui's copy);
//   2. the KDoc at the top of each class is trimmed, pointing readers
//      back to the :a2ui original for the full "ObjC-selector / K/N
//      quirks" rationale.
//
// All five `@ObjCName` strings — `A2UICoreEntry`,
// `A2UICustomViewRegistryBridge`, `A2UIIconRegistryBridge`,
// `A2UICustomFunctionRegistryBridge`, `A2UIPickerResultBridge` — stay
// unchanged, so the render pod's `NSClassFromString(...)` resolver and
// its runtime-attached shadow protocols continue to work verbatim.
//
// Why duplicate?
// ──────────────
//
// Kuikly's own `:demo` module hosts `KuiklyCoreEntry` in **its own**
// iosMain (KSP-generated), and because that class is "base-module-owned"
// Kotlin/Native emits full ObjC class metadata into `shared.framework`
// without needing any `export(project(":core"))` in the cocoapods block.
//
// A2UI's `:demo` module did not previously have this property: the five
// `@ObjCName` entry classes lived in `:a2ui/iosMain`, and the only way
// to make them survive into `A2UIDemo.framework`'s ObjC surface was to
// write `export(project(":a2ui"))`. That is the sole purpose that
// `export(:a2ui)` served — Kotlin-side `api(project(":a2ui"))` would
// have been enough for Kotlin consumers, but ObjC runtime symbols need
// the `export`.
//
// Hosting a thin-shell copy in `:demo/iosMain` turns these five classes
// into "base-module-owned" from `A2UIDemo.framework`'s point of view,
// which lets us delete `export(project(":a2ui"))` from
// `demo/build.2.1.21.gradle.kts` without losing any Swift-side symbol.
//
// What about `pod 'a2ui'` (Native-page scenario)?
// ───────────────────────────────────────────────
//
// Hosts that link `A2UICore.framework` directly (via `pod 'a2ui'` — e.g.
// `iosAppForNativePage`) still rely on the ORIGINAL `:a2ui/iosMain`
// copy of these classes. That copy is intentionally kept in place and
// is NOT changed by this refactor. Because `pod 'a2ui_demo'` and
// `pod 'a2ui'` are never linked into the same app (see the integration
// docs' "pick-one-pod" rule), the two copies never collide at runtime.
//
// Thread-safety and state
// ───────────────────────
//
// `A2UIBridgeManager`, `A2UICustomViewRegistry`, `A2UIIconRegistry`,
// `A2UICustomFunctionRegistry` are all commonMain singletons (Kotlin
// `object`). Both copies of the shell classes forward to the SAME
// in-process singleton — there is only one `A2UIBridgeManager` per
// `A2UIDemo.framework` process. Hosting the shell twice does not
// duplicate state.
// ─────────────────────────────────────────────────────────────────────────

/**
 * iOS A2UI Core Entry class (`:demo` iosMain shell — see file header).
 *
 * Semantics are identical to
 * `a2ui/src/iosMain/.../A2UICoreEntry.kt`'s `A2UICoreEntry`. See that
 * file for the full KDoc on `@ObjCName` nesting rules, selector
 * mangling, and the Kuikly `NativeBridge` round-trip pattern.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICoreEntry")
class A2UICoreEntry {

    /**
     * See `A2UICoreEntry.hrCoreDelegate` in `:a2ui/iosMain` for the
     * full rationale. Must stay a nested interface with no `@ObjCName`
     * on either the interface or `callNative` — K/N would otherwise
     * emit a Kotlin-vtable dispatch that crashes against Swift/ObjC
     * delegates.
     */
    @ObjCName("hrCoreDelegate")
    var hrCoreDelegate: Delegate? = null

    private var instanceId: String? = null
    private var hadRegisterNativeBridge = false

    // Real ObjC selector emitted by K/N is
    // `callKotlinMethodMethodId:arg0:arg1:arg2:` — see `:a2ui`
    // original for the "don't pre-bake first param name" caveat.
    @ObjCName("callKotlinMethod")
    fun callKotlinMethod(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?) {
        if (!A2UIBridgeManager.isDidInit()) {
            A2UIBridgeManager.init()
        }

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

        A2UIBridgeManager.callKotlinMethod(methodId, arg0, arg1, arg2)
    }

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
     * See `A2UICoreEntry.Delegate` in `:a2ui/iosMain` for the
     * "nested, un-annotated" invariant and the K/N-generated selector
     * `callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:`.
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
 * ObjC-facing bridge over `A2UICustomViewRegistry` (`:demo` shell).
 * See `:a2ui/iosMain/.../A2UICoreEntry.kt`'s `A2UICustomViewRegistryBridge`
 * KDoc for the full rationale. Both the `:demo` shell and the `:a2ui`
 * original forward to the same in-process singleton.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICustomViewRegistryBridge")
class A2UICustomViewRegistryBridge private constructor() {

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
 * ObjC-facing bridge over `A2UIIconRegistry` (`:demo` shell).
 * See `:a2ui/iosMain/.../A2UICoreEntry.kt`'s `A2UIIconRegistryBridge`
 * KDoc for the full rationale.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UIIconRegistryBridge")
class A2UIIconRegistryBridge private constructor() {

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
 * ObjC-facing bridge over `A2UICustomFunctionRegistry` (`:demo` shell).
 * See `:a2ui/iosMain/.../A2UICoreEntry.kt`'s
 * `A2UICustomFunctionRegistryBridge` KDoc for the full rationale,
 * including why this process-global bridge exists on top of the
 * per-instance `REGISTER_CUSTOM_FUNCTION` A2UIKotlinMethod path.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UICustomFunctionRegistryBridge")
class A2UICustomFunctionRegistryBridge private constructor() {

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
 * ObjC-facing bridge that lets Swift push a native date/time picker
 * result back into the KMP core (`:demo` shell).
 *
 * See `:a2ui/iosMain/.../A2UICoreEntry.kt`'s `A2UIPickerResultBridge`
 * KDoc for the full rationale (asymmetric async result delivery under
 * the Kuikly-page-embedded mode where `A2UIBridgeModule`'s synchronous
 * return value cannot carry the user-driven picker outcome).
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("A2UIPickerResultBridge")
class A2UIPickerResultBridge private constructor() {

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

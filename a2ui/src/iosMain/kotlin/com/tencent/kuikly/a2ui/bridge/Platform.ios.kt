package com.tencent.kuikly.a2ui.bridge

import kotlinx.cinterop.ExperimentalForeignApi
import platform.objc.objc_lookUpClass

/**
 * iOS now participates in the standard Kuikly Module channel just like
 * Android and OHOS: the render pod ships an ObjC `A2UIBridgeModule` class
 * that `KuiklyRenderLayerHandler` picks up via `NSClassFromString`, so
 * `BridgeManager.callModuleMethod("A2UIBridgeModule", …)` reaches it
 * without any per-page registration.
 *
 * The crash risk documented on `isModuleBasedPlatform` (iOS raises an
 * ObjC `NSException` through `NSAssert` when the class is missing) is
 * mitigated at three layers:
 *   1. Pod packaging keeps `A2UIBridgeModule` alive via `-ObjC` /
 *      `-force_load` so dead-stripping never removes it.
 *   2. `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()`
 *      touches `NSClassFromString("A2UIBridgeModule")` at app boot as
 *      an extra safety net.
 *   3. [isA2UIBridgeModuleAvailable] runs a `objc_lookUpClass` pre-check
 *      so if the class really is missing we degrade to a no-op instead
 *      of trapping on NSAssert.
 */
internal actual val isModuleBasedPlatform: Boolean = true

@OptIn(ExperimentalForeignApi::class)
internal actual fun isA2UIBridgeModuleAvailable(): Boolean {
    // objc_lookUpClass returns NULL (not NSException) when the class is
    // not linked into the process — this is the safe pre-check Kuikly's
    // own NSAssert path cannot do on our behalf.
    return objc_lookUpClass("A2UIBridgeModule") != null
}

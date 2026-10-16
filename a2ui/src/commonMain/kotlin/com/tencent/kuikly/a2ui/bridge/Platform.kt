package com.tencent.kuikly.a2ui.bridge

/**
 * Compile-time platform gate used by [A2UIBridgeManager.dispatchToA2UIBridgeModule].
 *
 * Returns `true` on platforms whose A2UI render pod ships an
 * `A2UIBridgeModule` class that `BridgeManager.callModuleMethod(
 * "A2UIBridgeModule", …)` can reach — i.e. the platforms that handle
 * A2UI-specific native requests (showDateTimePicker, executeFunctionCall,
 * invokeCustomFunction, contentHeightChanged, actionCallback, openUrl,
 * …) through the standard Kuikly module bus instead of a platform-local
 * methodId extension point or a KMP-side singleton hook.
 *
 * On platforms where this is `false`, `BridgeManager.callModuleMethod(
 * "A2UIBridgeModule", …)` would either throw a Kotlin exception (loggy
 * but recoverable) or — on iOS — raise an Objective-C `NSException` that
 * a `catch (Throwable)` in K/N cannot catch (process abort). The guard
 * is therefore mandatory, not just an optimisation.
 *
 * iOS specifically: we gate on this constant *and* on
 * `isA2UIBridgeModuleAvailable()` (an `objc_lookUpClass` pre-check) so
 * an app that forgot to link the A2UI render pod still degrades to a
 * no-op instead of trapping on `NSAssert` deep inside Kuikly's
 * `KuiklyRenderLayerHandler`.
 *
 * Resolved per source set:
 * - `androidMain`     → `true`  (A2UIBridgeModule registered via A2UIKuiklyBaseViewSupport)
 * - `ohosArm64Main`   → `true`  (A2UIBridgeModule registered via A2UIKuiklyRenderViewSupport)
 * - `iosMain`         → `true`  (A2UIBridgeModule picked up via NSClassFromString, see A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting)
 * - `jvmMain`         → `false` (no platform-side A2UI render pod)
 */
internal expect val isModuleBasedPlatform: Boolean

/**
 * Platform-side runtime check for whether the `A2UIBridgeModule` class
 * is actually linked into the current process.
 *
 * On Apple targets this uses `objc_lookUpClass("A2UIBridgeModule")` so
 * we can early-return `false` from [A2UIBridgeManager.dispatchToA2UIBridgeModule]
 * before Kuikly's `KuiklyRenderLayerHandler` would otherwise call
 * `NSAssert(NO, "创建Module失败 …")` on a missing class. On non-Apple
 * targets this is a cheap `true` — Android and OHOS already resolve the
 * module through their standard module registry, so the check never
 * matters.
 */
internal expect fun isA2UIBridgeModuleAvailable(): Boolean

package com.tencent.kuikly.a2ui.bridge

/**
 * Compile-time platform gate used by [A2UIBridgeManager.dispatchToA2UIBridgeModule].
 *
 * Returns `true` on platforms whose A2UI render pod registers a custom
 * `A2UIBridgeModule` on the enclosing Kuikly page's delegate — i.e. the
 * platforms that handle A2UI-specific native method ids (showDateTimePicker,
 * executeFunctionCall, invokeCustomFunction, contentHeightChanged,
 * actionCallback, openUrl, …) through the standard Kuikly module bus
 * instead of a platform-local methodId extension point.
 *
 * On platforms where this is `false`, `BridgeManager.callModuleMethod(
 * "A2UIBridgeModule", …)` would either throw a Kotlin exception (loggy
 * but recoverable) or — on iOS — raise an Objective-C `NSException` that
 * a `catch (Throwable)` in K/N cannot catch (process abort). The guard
 * is therefore mandatory, not just an optimisation.
 *
 * Resolved per source set:
 * - `androidMain`     → `true`  (A2UIBridgeModule registered via A2UIKuiklyBaseViewSupport)
 * - `ohosArm64Main`   → `true`  (A2UIBridgeModule registered via A2UIKuiklyRenderViewSupport)
 * - `iosMain`         → `false` (iOS uses A2UICustomFunctionNativeInvoker singleton instead)
 * - `jvmMain`         → `false` (no platform-side A2UI render pod)
 */
internal expect val isModuleBasedPlatform: Boolean

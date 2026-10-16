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

/**
 * Platform gate controlling whether [com.tencent.kuikly.a2ui.views.A2UICustomComponentView]
 * creates a Kuikly [com.tencent.kuikly.core.base.Shadow] to drive measurement.
 *
 * Why this exists:
 *
 * The Kuikly `Shadow` mechanism is a renderer-side object that lets the Flex
 * engine ask the native (JVM / ObjC / C++) runtime for a view's *intrinsic*
 * size. Each platform wires it up differently:
 *
 *  - Android: `IKuiklyRenderShadowExport` is a plain Kotlin interface. The
 *    renderer resolves it against the custom-view registry — no extra ABI.
 *  - iOS:     Objective-C runtime looks up `+hrv_createShadow` on the class
 *    returned by `NSClassFromString(viewName)` — again no extra ABI.
 *  - OHOS:    Shadow creators live in a **C++ static map**
 *    (`IKRRenderShadowExport::gRegisterShadowCreator`). ArkTS has no
 *    equivalent lookup, so the only way for a business app's custom
 *    component to appear there is to add a C++ translation unit to its
 *    `libxxx_entry.so` that calls `IKRRenderShadowExport::RegisterShadowCreator`.
 *    That couples business code to Kuikly's C++ ABI — exactly what the
 *    Module-channel refactor in this project set out to avoid.
 *
 * Resolution:
 *
 * On OHOS we **skip Shadow creation** in [A2UICustomComponentView] and
 * instead drive measurement through the standard Kuikly Module channel
 * (`A2UIBridgeModule.calculateCustomViewSize`, `syncCall=1`):
 *
 *  - Authors register an optional `measurer` alongside their custom-view
 *    creator (ArkTS: `registerA2UICustomRenderView(name, creator, measurer)`).
 *    When present, A2UI calls it on the Kuikly Context Thread each measure
 *    pass. Its return value drives Flex measurement, mirroring what the
 *    native Shadow does on Android / iOS.
 *  - For async-sized components, authors call the second-pass helper
 *    (ArkTS: `notifyA2UICustomViewSizeChanged(view, w, h)`); A2UI only
 *    triggers a relayout when dimensions actually changed.
 *  - When no measurer is registered the fallback is
 *    `width = parent cross-axis constraint`, `height = 0`; JSON-side
 *    `width` / `height` act as a hard override at any point.
 *
 * On Android / iOS the long-standing Shadow mechanism remains the single
 * source of intrinsic size and the JSON-side `width` / `height` are
 * **optional overrides** (merged in [A2UICustomComponentView.measure]
 * after the shadow result).
 *
 * Resolved per source set:
 * - `androidMain`     → `true`
 * - `iosMain`         → `true`
 * - `ohosArm64Main`   → `false` (shadow path is impossible without a C++ ABI patch; uses the Module-measurer channel instead)
 * - `jvmMain`         → `false` (there is no native renderer at all)
 */
internal expect val isCustomComponentShadowRequired: Boolean

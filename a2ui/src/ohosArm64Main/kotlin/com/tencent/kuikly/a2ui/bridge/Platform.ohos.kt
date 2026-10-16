package com.tencent.kuikly.a2ui.bridge

internal actual val isModuleBasedPlatform: Boolean = true

// OHOS resolves A2UIBridgeModule through its ArkTS module registry map
// returned by A2UIKuiklyRenderViewSupport.getCustomRenderModuleCreatorRegisterMap.
// Missing registrations surface as a Kotlin exception the Module dispatcher
// already catches, so no pre-check is needed here.
internal actual fun isA2UIBridgeModuleAvailable(): Boolean = true

// OHOS has no ArkTS-reachable Shadow registry — Shadow creators live in
// a C++ static map (IKRRenderShadowExport::gRegisterShadowCreator) that
// can only be populated from a C++ translation unit linked into the host
// app's libxxx_entry.so. We deliberately chose not to re-introduce such
// a C++ ABI patch (that was the whole point of the ArkTS-only custom
// component registration refactor), so A2UICustomComponentView skips the
// Shadow pathway and uses the Module-measurer channel instead:
//
//   1. On every measure pass, [A2UICustomComponentView.measure] asks the
//      ArkTS side through `A2UIBridgeModule.calculateCustomViewSize`
//      (standard Kuikly Module channel, syncCall=1). If the author
//      supplied a `measurer` when calling `registerA2UICustomRenderView`,
//      its return value drives Flex measurement exactly the way an
//      Android `IKuiklyRenderShadowExport` / iOS `+hrv_createShadow`
//      would — restoring the "intrinsic size is the component's own
//      responsibility" contract that A2UI follows on the other two
//      platforms.
//   2. For async-sized components (e.g. a WebView whose content height
//      arrives after `onPageEnd`), the author can later call
//      `notifyA2UICustomViewSizeChanged(view, w, h)` on the ArkTS side.
//      That routes through the `a2uiNotifyCustomViewResize` NAPI writer
//      and lands in `A2UIBridgeManager.notifyCustomViewResize`, which
//      only triggers a re-layout when at least one dimension actually
//      changed (0.5 vp tolerance).
//   3. If no `measurer` is registered, A2UI falls back to
//      `width = parent cross-axis constraint` (so stretch-aligned parents
//      still fill the component horizontally) and `height = 0`. Authors
//      can still supply JSON-side `width` / `height` as a hard override
//      at any time — this always wins over both the measurer result and
//      the fallback.
internal actual val isCustomComponentShadowRequired: Boolean = false

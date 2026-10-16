package com.tencent.kuikly.a2ui.bridge

internal actual val isModuleBasedPlatform: Boolean = true

// OHOS resolves A2UIBridgeModule through its ArkTS module registry map
// returned by A2UIKuiklyRenderViewSupport.getCustomRenderModuleCreatorRegisterMap.
// Missing registrations surface as a Kotlin exception the Module dispatcher
// already catches, so no pre-check is needed here.
internal actual fun isA2UIBridgeModuleAvailable(): Boolean = true

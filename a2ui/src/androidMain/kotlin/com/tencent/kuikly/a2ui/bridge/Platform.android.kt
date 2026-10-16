package com.tencent.kuikly.a2ui.bridge

internal actual val isModuleBasedPlatform: Boolean = true

// Android resolves A2UIBridgeModule through its own module registry
// (see A2UIKuiklyBaseViewSupport.wrap). If the host app skipped that
// registration the module call simply logs — never crashes — so no
// pre-check is needed here.
internal actual fun isA2UIBridgeModuleAvailable(): Boolean = true

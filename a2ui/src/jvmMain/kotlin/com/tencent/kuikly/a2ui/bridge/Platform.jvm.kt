package com.tencent.kuikly.a2ui.bridge

internal actual val isModuleBasedPlatform: Boolean = false

// JVM has no A2UI render pod so the Module channel is never available.
internal actual fun isA2UIBridgeModuleAvailable(): Boolean = false

package com.tencent.kuikly.a2ui.demo

internal object A2UIDemoAssetVersion {
    fun validate(versions: List<String?>, directory: String) {
        val allowed = when (directory.substringBefore('/')) {
            "v0_9" -> setOf("v0.9", "v0.9.1")
            "v1_0" -> setOf("1.0", "v1.0")
            else -> return
        }
        require(versions.isNotEmpty() && versions.all { it in allowed }) {
            "样本版本与目录 $directory 不一致，或消息缺少显式 version"
        }
    }

    fun config(directory: String): String = when (directory.substringBefore('/')) {
        "v1_0" -> "{\"debug\":true,\"protocolVersion\":\"1.0\",\"strictV10\":true}"
        "v0_9" -> "{\"debug\":true,\"protocolVersion\":\"0.9\",\"strictV10\":false}"
        else -> "{\"debug\":true}"
    }
}

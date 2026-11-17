package com.tencent.kuikly.a2ui.demo

internal object A2UIDemoAssetPath {
    fun join(vararg parts: String): String {
        val segments = parts.flatMap { it.split('/') }.filter { it.isNotEmpty() }
        require(segments.isNotEmpty() && segments.none { it == "." || it == ".." }) {
            "无效的样本资源路径"
        }
        return segments.joinToString("/")
    }
}

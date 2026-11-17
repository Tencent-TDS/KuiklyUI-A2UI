package com.tencent.kuikly.a2ui.golden

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionDescriptor
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.function.A2UIFunctionInvokeMode
import com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import kotlin.math.round

internal class A2UIFormalGoldenHost(parser: A2UIStreamParser) {
    private val catalog = "custom_functions_catalog_v1_0.json"
    private val types = linkedMapOf(
        "getGreeting" to A2UIFunctionReturnType.STRING,
        "formatPhone" to A2UIFunctionReturnType.STRING,
        "isVip" to A2UIFunctionReturnType.BOOLEAN,
        "calculateDiscount" to A2UIFunctionReturnType.NUMBER,
        "logEvent" to A2UIFunctionReturnType.VOID,
        "buildSummary" to A2UIFunctionReturnType.STRING,
    )
    private val previous = types.keys.associateWith { A2UICustomFunctionRegistry.lookupExact(it, catalog) }

    init {
        types.forEach { (name, type) ->
            A2UICustomFunctionRegistry.register(A2UICustomFunctionDescriptor(
                name, type, A2UIFunctionInvokeMode.SYNC, catalog, "rendererOrAgent",
            ))
        }
        parser.protocolAdapter.customFunctionInvoker = { name, argsJson, _ ->
            val args = JSONObject(argsJson)
            when (name) {
                "getGreeting" -> "Hello, ${args.optString("name", "World")}!"
                "formatPhone" -> {
                    val phone = args.optString("phone", "")
                    if (phone.length == 11) "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}" else phone
                }
                "isVip" -> args.optDouble("level", 0.0) >= 3
                "calculateDiscount" -> round(args.optDouble("price", 0.0) * args.optDouble("rate", 1.0) * 100) / 100.0
                "logEvent" -> null
                "buildSummary" -> "项目「${args.optString("name", "Unknown")}」当前状态: ${args.optString("status", "unknown")}，共 ${args.optInt("count", 0)} 个任务"
                else -> error("Unregistered formal golden function: $name")
            }
        }
    }

    fun restore() {
        types.keys.forEach { name ->
            A2UICustomFunctionRegistry.unregister(name, catalog)
            previous[name]?.let(A2UICustomFunctionRegistry::register)
        }
    }
}

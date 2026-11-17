package com.tencent.kuikly.a2ui.golden

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class A2UIFormalGoldenHostTest {
    @Test
    fun customGoldenRecordsActualResultsInsteadOfMissingHandlerEmptyStrings() {
        val expectations = mapOf(
            "a2ui_custom_functions/01_string_return/1_custom_greeting.json" to "Hello, Kuikly!",
            "a2ui_custom_functions/01_string_return/2_custom_format_phone.json" to "138-0000-1234",
            "a2ui_custom_functions/02_boolean_return/1_custom_is_vip.json" to "当前 userLevel = 5，isVip 返回 true",
            "a2ui_custom_functions/03_number_return/1_custom_calculate_discount.json" to "139.3",
            "a2ui_custom_functions/05_args_passing/1_custom_build_summary.json" to "项目「KuiklyUI」当前状态: active，共 42 个任务",
        )
        for ((path, expected) in expectations) {
            val case = A2UIGoldenCorpus.cases.single { it.id.startsWith("v1_0.formal.") && it.sourcePath == path }
            val snapshot = A2UIGoldenReplay.run(case).snapshot
            assertTrue(snapshot.contains(expected), "$path: missing actual function result '$expected'\n${snapshot.take(3500)}")
        }
    }

    @Test
    fun replayRestoresExistingQualifiedRegistrations() {
        val names = listOf("getGreeting", "formatPhone", "isVip", "calculateDiscount", "logEvent", "buildSummary")
        val catalog = "custom_functions_catalog_v1_0.json"
        val previous = names.associateWith { A2UICustomFunctionRegistry.lookupExact(it, catalog) }
        val case = A2UIGoldenCorpus.cases.first { it.id.startsWith("v1_0.formal.") && it.sourcePath.startsWith("a2ui_custom_functions/") }
        A2UIGoldenReplay.run(case)
        assertEquals(previous, names.associateWith { A2UICustomFunctionRegistry.lookupExact(it, catalog) })
    }
}

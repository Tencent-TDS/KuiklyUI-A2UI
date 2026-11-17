/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.kuikly.a2ui.golden

import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Golden 回归网。
 *
 * 直接覆盖 StreamParser 与 ProtocolAdapter 的消息和状态输出，
 * 不执行 Applier 的 View 操作，也不替代宿主端的 UI 回归。
 *
 * 将生成语料喂进生产用的 [A2UIStreamParser]，即时冻结消息全字段、
 * 最终数据模型与 action 状态，与 [A2UIGoldenBaseline] 逐字比对。
 *
 * 三层断言，各管一件事：
 *  1. [corpus_parsesWithoutError]  —— 不许报错、不许空产出（冒烟）
 *  2. [corpus_matchesBaseline]     —— 消息流逐字节不变（真正的回归网）
 *  3. [corpus_isDeterministic]     —— 同一输入跑两遍结果一致（防快照本身不稳）
 *
 * 基线怎么更新：跑 `ka2ui golden --accept`，它会重新生成
 * [A2UIGoldenBaseline]。**更新前必须人工确认 diff 是预期的行为变更**——
 * 无脑 accept 等于把网拆了。
 */
class A2UIGoldenTest {

    private fun run(case: A2UIGoldenCase) = A2UIGoldenReplay.run(case)

    @Test
    fun corpus_isNotEmpty() {
        // 守卫生成器本身：如果 ka2ui golden 因为筛选条件写错而产出空语料，
        // 下面两个测试会"全部通过"，网却是空的。
        assertTrue(
            A2UIGoldenCorpus.cases.size >= 200,
            "golden 语料只有 ${A2UIGoldenCorpus.cases.size} 条，远少于预期；" +
                "请检查 ka2ui golden 的筛选条件是否被改坏",
        )
    }

    @Test
    fun corpus_parsesWithoutError() {
        val failures = mutableListOf<String>()
        for (case in A2UIGoldenCorpus.cases) {
            val result = run(case)
            if (result.errors.isNotEmpty()) {
                failures += "${case.id}: ${result.errors.joinToString()}"
                continue
            }
            if (result.messageCount == 0 || !result.ended) {
                failures += "${case.id}: 未产出消息或未结束流"
            }
        }
        assertTrue(
            failures.isEmpty(),
            "${failures.size} 条语料解析失败：\n" + failures.joinToString("\n").take(4000),
        )
    }

    @Test
    fun corpus_isDeterministic() {
        for (case in A2UIGoldenCorpus.cases) {
            val first = run(case).snapshot
            val second = run(case).snapshot
            assertEquals(first, second, "${case.id} 的消息流快照不稳定（同一输入两次结果不同）")
        }
    }

    @Test
    fun corpus_chunkedStreamMatchesWholeStream() {
        for (case in A2UIGoldenCorpus.cases) {
            val whole = run(case)
            val chunked = A2UIGoldenReplay.run(case, chunkSize = 17)
            assertEquals(whole, chunked, "${case.id} 分块输入改变了解析结果")
        }
    }

    @Test
    fun corpus_matchesBaseline() {
        val baseline = A2UIGoldenBaseline.snapshots
        val ids = A2UIGoldenCorpus.cases.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "语料 id 重复")
        assertEquals(ids.toSet(), baseline.keys, "语料与基线的 id 集合必须精确一致")
        val drifted = mutableListOf<String>()
        val missing = mutableListOf<String>()

        for (case in A2UIGoldenCorpus.cases) {
            val expected = baseline[case.id]
            if (expected == null) {
                missing += case.id
                continue
            }
            val actual = run(case).snapshot
            if (actual != expected) {
                drifted += buildString {
                    append("\n── ").append(case.id).append(" （").append(case.sourcePath).append("）\n")
                    append(firstDiff(expected, actual))
                }
            }
        }

        assertTrue(
            missing.isEmpty(),
            "${missing.size} 条语料在基线里没有对应快照，请运行 ka2ui golden --accept：\n" +
                missing.take(20).joinToString("\n"),
        )
        assertTrue(
            drifted.isEmpty(),
            "${drifted.size} 条语料的消息流发生变化。如果这是预期的行为变更，" +
                "请人工核对 diff 后运行 ka2ui golden --accept 更新基线：" +
                drifted.joinToString("").take(6000),
        )
    }

    /** 只报第一处差异，避免把整份快照糊到断言消息里 */
    private fun firstDiff(expected: String, actual: String): String {
        val e = expected.lines()
        val a = actual.lines()
        for (i in 0 until maxOf(e.size, a.size)) {
            val el = e.getOrNull(i)
            val al = a.getOrNull(i)
            if (el != al) {
                return "   第 ${i + 1} 行\n   基线: ${el ?: "<缺失>"}\n   实际: ${al ?: "<缺失>"}"
            }
        }
        return "   （逐行相同但整体不等，可能是结尾换行差异）"
    }
}

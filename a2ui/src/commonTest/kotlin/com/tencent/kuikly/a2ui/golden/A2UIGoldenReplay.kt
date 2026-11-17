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

import com.tencent.kuikly.a2ui.parser.A2UIParseError
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion

internal object A2UIGoldenReplay {
    data class Result(
        val snapshot: String,
        val errors: List<String>,
        val messageCount: Int,
        val ended: Boolean,
    )

    fun run(case: A2UIGoldenCase, chunkSize: Int = Int.MAX_VALUE): Result {
        require(chunkSize > 0)
        val parser = A2UIStreamParser()
        val host = if (case.id.startsWith("v1_0.formal.extension.") && case.sourcePath.startsWith("a2ui_custom_functions/")) {
            A2UIFormalGoldenHost(parser)
        } else null
        try {
            return replay(case, chunkSize, parser)
        } finally {
            host?.restore()
        }
    }

    private fun replay(case: A2UIGoldenCase, chunkSize: Int, parser: A2UIStreamParser): Result {
        val snapshot = StringBuilder()
        val errors = mutableListOf<String>()
        var count = 0
        var ended = false
        parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) {
                snapshot.append(A2UIMessageSnapshot.render(listOf(message), count++))
            }
            override fun onError(error: A2UIParseError) { errors += error.message }
            override fun onStreamEnd() { ended = true }
        })
        if (case.protocolVersion == "1.0" || case.protocolVersion == "v1.0") {
            parser.protocolAdapter.setExpectedProtocolVersion(A2UIProtocolVersion.V1_0)
            parser.protocolAdapter.strictV10 = true
        }
        if (case.stream.length <= chunkSize) {
            parser.feed(case.stream)
        } else {
            case.stream.chunked(chunkSize).forEach(parser::feed)
        }
        parser.finish()
        snapshot.append(A2UIMessageSnapshot.renderState(
            "dataModel", parser.protocolAdapter.getDataModelSnapshot(),
        ))
        val actions = parser.protocolAdapter.getActionTags().sorted().associate { tag ->
            tag.toString() to parser.protocolAdapter.getActionForTag(tag)
        }
        snapshot.append(A2UIMessageSnapshot.renderState("actions", actions))
        return Result(snapshot.toString(), errors.toList(), count, ended)
    }
}

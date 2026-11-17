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

import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.protocol.A2UISurfaceConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class A2UIMessageSnapshotTest {
    private fun snapshot(msg: A2UIMessage) = A2UIMessageSnapshot.render(listOf(msg))
    private fun data(value: Any?) = A2UIMessageSnapshot.renderState("data", mapOf("value" to value))

    @Test
    fun everyMessageFieldParticipatesInSnapshot() {
        val base = A2UIMessage(A2UIMessageType.CREATE_COMPONENT, "surface")
        val variants = listOf(
            base.copy(type = A2UIMessageType.UPDATE_COMPONENT),
            base.copy(surfaceId = "another"),
            base.copy(tag = 3),
            base.copy(viewName = "KRView"),
            base.copy(parentTag = 1),
            base.copy(index = 2),
            base.copy(props = mapOf("text" to "changed")),
            base.copy(frame = floatArrayOf(1f, 2f, 3f, 4f)),
            base.copy(bindings = mapOf("text" to "/text")),
            base.copy(method = "click"),
            base.copy(params = "{}"),
            base.copy(messages = listOf(base.copy(props = mapOf("text" to "nested")))),
            base.copy(data = mapOf("value" to 1)),
            base.copy(config = A2UISurfaceConfig()),
        )
        for (variant in variants) assertNotEquals(snapshot(base), snapshot(variant))
        assertEquals(base, base.copy(props = mapOf("text" to "changed")))
        assertNotEquals(snapshot(base.copy(props = emptyMap())), snapshot(base.copy(props = null)))
    }

    @Test
    fun everyConfigFieldParticipatesInSnapshot() {
        val cfg = A2UISurfaceConfig()
        val base = A2UIMessage(A2UIMessageType.CREATE_SURFACE, "surface", config = cfg)
        for (variant in listOf(
            cfg.copy(width = 1f), cfg.copy(height = 1f), cfg.copy(backgroundColor = "null"),
            cfg.copy(incrementalRender = false), cfg.copy(catalogId = "catalog"),
        )) assertNotEquals(snapshot(base), snapshot(base.copy(config = variant)))
    }

    @Test
    fun nestedMapOrderDoesNotAffectSnapshot() {
        assertEquals(
            data(linkedMapOf("b" to 2, "a" to linkedMapOf("d" to 4, "c" to 3))),
            data(linkedMapOf("a" to linkedMapOf("c" to 3, "d" to 4), "b" to 2)),
        )
        assertNotEquals(data(listOf(1, 2)), data(listOf(2, 1)))
    }

    @Test
    fun doublePrecisionAndFloatSignAreNotLost() {
        assertNotEquals(data(1.0000000001), data(1.0000000002))
        assertNotEquals(data(16777216.0), data(16777217.0))
        assertNotEquals(data(-0.0f), data(0.0f))
        assertNotEquals(data(Float.MAX_VALUE), data(Long.MAX_VALUE))
    }

    @Test
    fun stringsAreEscapedWithoutAmbiguity() {
        assertEquals("\"a\\n\\t\\r\\\\\\\"\\u0000\"", A2UIMessageSnapshot.quote("a\n\t\r\\\"\u0000"))
        assertNotEquals(data(listOf("a", "b")), data(listOf("a\", \"b")))
        assertNotEquals(data("null"), data(null))
        assertNotEquals(data("1"), data(1))
    }

    @Test
    fun unknownObjectsCannotSilentlyUseIdentityToString() {
        assertFailsWith<IllegalStateException> { data(Any()) }
        assertFailsWith<IllegalStateException> { data(mapOf(1 to "value")) }
    }
}

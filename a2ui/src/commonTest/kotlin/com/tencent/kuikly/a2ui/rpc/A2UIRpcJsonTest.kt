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

package com.tencent.kuikly.a2ui.rpc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Minimal round-trip tests for [A2UIRpcJson]. The parser side is already
 * exercised by the stream-parser tests upstream; this suite focuses on
 * the writer and on the error-swallowing policy (`null` on any failure).
 */
class A2UIRpcJsonTest {

    @Test
    fun stringify_null_returnsNull() {
        assertNull(A2UIRpcJson.stringify(null))
    }

    @Test
    fun stringify_primitives_emitsCanonicalJson() {
        assertEquals("true", A2UIRpcJson.stringify(true))
        assertEquals("42", A2UIRpcJson.stringify(42))
        assertEquals("3.5", A2UIRpcJson.stringify(3.5))
        assertEquals("\"hi\"", A2UIRpcJson.stringify("hi"))
    }

    @Test
    fun stringify_escapesSpecialChars() {
        val out = A2UIRpcJson.stringify("a\"b\\c\nd\te")
        // Backslash + quote + backslash + newline + tab are all escaped.
        assertEquals("\"a\\\"b\\\\c\\nd\\te\"", out)
    }

    @Test
    fun stringify_mapWithNestedList_preservesOrderAndShape() {
        val input = linkedMapOf(
            "k1" to "v",
            "k2" to listOf(1, 2, 3),
            "k3" to null,
        )
        val out = A2UIRpcJson.stringify(input)
        assertNotNull(out)
        assertTrue(out.startsWith("{"))
        assertTrue(out.endsWith("}"))
        // Order is preserved (LinkedHashMap).
        assertTrue(out.indexOf("k1") < out.indexOf("k2"))
        assertTrue(out.indexOf("k2") < out.indexOf("k3"))
        // null is encoded literally.
        assertTrue(out.contains("\"k3\":null"))
        // Nested list is correctly encoded.
        assertTrue(out.contains("[1,2,3]"))
    }

    @Test
    fun parseObject_emptyInput_returnsNull() {
        assertNull(A2UIRpcJson.parseObject(null))
        assertNull(A2UIRpcJson.parseObject(""))
    }

    @Test
    fun parseObject_wellFormed_roundTripsSimpleShape() {
        val json = "{\"name\":\"alice\",\"age\":30,\"tags\":[\"a\",\"b\"]}"
        val parsed = A2UIRpcJson.parseObject(json)
        assertNotNull(parsed)
        assertEquals("alice", parsed["name"])
        assertEquals(30, parsed["age"])
        @Suppress("UNCHECKED_CAST")
        assertEquals(listOf("a", "b"), parsed["tags"] as List<Any?>)
    }
}

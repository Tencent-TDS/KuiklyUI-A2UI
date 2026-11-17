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

package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class A2UIProjectionArchitectureTest {
    private class Fixture {
        val model = A2UISurfaceModel()
        val decoder = A2UIProtocolDecoder { model.surfaceId }
        val values = A2UIValueEngine(decoder)
        val reactive = A2UIReactiveEngine()
        val state = A2UIInteractionState()
        val projection = A2UIComponentProjection(model, decoder, values, reactive, state)
    }

    @Test
    fun propertyPlanningDoesNotCommitFactsActionsOrSubscriptions() {
        val f = Fixture()
        f.model.write("name", "Alice", A2UIUpdateOrigin.WIRE)
        f.projection.idToTag["text"] = 1
        val definition = mapOf<String, Any?>(
            "id" to "text", "component" to "Text", "text" to mapOf("path" to "/name"),
            "action" to mapOf("name" to "tap"),
        )
        val plan = f.projection.planProperties("Text", definition)
        assertEquals("Alice", plan.props["text"])
        assertTrue(f.model.definitions.isEmpty())
        assertTrue(f.reactive.pathDependents("name").isEmpty())
        assertTrue(f.state.actionTags().isEmpty())
        assertNull(f.projection.pendingProperties)
        f.projection.commitProperties(plan)
        assertEquals(definition, f.model.definitions["text"])
        assertEquals(setOf("text"), f.reactive.pathDependents("name"))
        assertEquals(setOf(1), f.state.actionTags())
    }

    @Test
    fun inputInitializationAndChecksArePlannedTogether() {
        val f = Fixture()
        f.projection.idToTag["input"] = 1
        val plan = f.projection.planProperties("TextField", mapOf(
            "id" to "input", "component" to "TextField", "value" to "bad", "validationRegexp" to "[0-9]+",
        ))
        assertEquals("bad", plan.initialValues["__vr_input"])
        assertTrue(f.model.data.isEmpty())
        assertFalse(f.reactive.hasChecks("input"))
        assertEquals("Input does not match the required format", plan.props["__checkError"])
        f.projection.commitProperties(plan)
        assertEquals("bad", f.model.read("__vr_input"))
        assertTrue(f.reactive.hasChecks("input"))
    }

    @Test
    fun propertyCommitNeverInvokesHostFunctionsAgain() {
        val f = Fixture()
        val name = "architectureLabel"
        assertTrue(A2UICustomFunctionRegistry.register(name, "string|sync"))
        try {
            var calls = 0
            f.values.customFunctionInvoker = { _, _, _ -> calls++; "result" }
            f.projection.idToTag["text"] = 1
            val plan = f.projection.planProperties("Text", mapOf(
                "id" to "text", "component" to "Text", "text" to mapOf("call" to name, "args" to emptyMap<String, Any?>()),
            ))
            assertEquals("result", plan.props["text"])
            assertEquals(1, calls)
            f.projection.commitProperties(plan)
            assertEquals(1, calls)
        } finally {
            A2UICustomFunctionRegistry.unregister(name)
        }
    }

    @Test
    fun definitionAllocatedIdentityAndMaterializationAreDistinct() {
        val f = Fixture()
        f.model.rememberDefinition("text", mapOf("component" to "Text"))
        f.projection.idToTag["text"] = 1
        assertFalse(f.projection.isMaterialized("text"))
        val create = A2UIMessage(type = A2UIMessageType.CREATE_COMPONENT, surfaceId = "s", tag = 1, parentTag = 7)
        f.projection.commitBatch(A2UIProjectionBatch(listOf(create)))
        assertTrue(f.projection.isMaterialized("text"))
        assertEquals(7, f.projection.materialization.parentOf(1))
        f.projection.commitBatch(A2UIProjectionBatch(listOf(create.copy(type = A2UIMessageType.REMOVE_COMPONENT))))
        assertFalse(f.projection.isMaterialized("text"))
        assertEquals(1, f.projection.idToTag["text"])
        assertTrue(f.model.definitions.containsKey("text"))
    }

    @Test
    fun evaluationContextsPreserveScopedMissingValueAndDependencyDifferences() {
        val f = Fixture()
        val binding = mapOf("path" to "/name")
        val ordinary = f.values.evaluate(binding, A2UIEvaluationContext(emptyMap()), true)
        assertEquals("", ordinary.value)
        assertEquals(setOf("name"), ordinary.paths)
        val scoped = f.values.evaluate(binding, A2UIEvaluationContext(emptyMap(), item = mapOf("name" to "item")), true)
        assertEquals("item", scoped.value)
        assertTrue(scoped.paths.isEmpty())
        val unsupported = mapOf("call" to "unknown", "args" to emptyMap<String, Any?>())
        assertEquals("", f.values.evaluate(unsupported, A2UIEvaluationContext(emptyMap()), false).value)
        assertNull(f.values.function(unsupported, A2UIEvaluationContext(emptyMap())))
        assertEquals(unsupported, f.values.action(unsupported, A2UIEvaluationContext(emptyMap())))
    }

    @Test
    fun indexDiagnosticsAreLocalToEachEvaluation() {
        val f = Fixture()
        f.decoder.decode(mapOf("version" to "v1.0"))
        val expression = mapOf("@call" to "@index")
        val missing = f.values.evaluate(expression, A2UIEvaluationContext(emptyMap()), false)
        assertEquals("", missing.value)
        assertEquals(1, missing.diagnostics.size)
        val scoped = f.values.evaluate(expression, A2UIEvaluationContext(emptyMap(), index = 2), false)
        assertEquals("2", scoped.value)
        assertTrue(scoped.diagnostics.isEmpty())
    }

    @Test
    fun hostReentryAndCallbackReplacementDoNotLeakPropertyDrafts() {
        val adapter = A2UIProtocolAdapter()
        val name = "architectureReentrantLabel"
        assertTrue(A2UICustomFunctionRegistry.register(name, "string|sync"))
        try {
            adapter.convertMessage(mapOf("createSurface" to mapOf("surfaceId" to "s")))
            adapter.updateDataModelValue("other", "before")
            var calls = 0
            adapter.customFunctionInvoker = { _, _, _ ->
                calls++
                adapter.convertMessage(mapOf("updateComponents" to mapOf("components" to listOf(
                    mapOf("id" to "nested", "component" to "Text", "text" to mapOf("path" to "/other")),
                ))))
                "first"
            }
            adapter.convertMessage(mapOf("updateComponents" to mapOf("components" to listOf(
                mapOf("id" to "outer", "component" to "Text", "text" to mapOf(
                    "call" to name, "args" to mapOf("value" to mapOf("path" to "/name")),
                )),
            ))))
            assertEquals(1, calls)
            val nestedUpdates = adapter.updateDataModelValue("other", "after")
            assertTrue(nestedUpdates.any { it.props?.get("text") == "after" })
            adapter.customFunctionInvoker = { _, _, _ -> "second" }
            val outerUpdates = adapter.updateDataModelValue("name", "updated")
            assertTrue(outerUpdates.any { it.props?.get("text") == "second" })
        } finally {
            A2UICustomFunctionRegistry.unregister(name)
        }
    }

    @Test
    fun decoderPlansRpcBeforeSurfaceWithoutMutatingTheModel() {
        val f = Fixture()
        val envelope = f.decoder.decode(mapOf(
            "version" to "v1.0", "callRendererFunction" to mapOf("name" to "test"),
            "createSurface" to mapOf("surfaceId" to "s", "catalogId" to "catalog"),
            "updateDataModel" to mapOf("value" to mapOf("name" to "Alice")),
        ))
        assertEquals(listOf(A2UICommandKind.CALL_RENDERER, A2UICommandKind.CREATE, A2UICommandKind.DATA), envelope?.commands?.map { it.kind })
        assertEquals("", f.model.surfaceId)
        assertTrue(f.model.data.isEmpty())
    }
}

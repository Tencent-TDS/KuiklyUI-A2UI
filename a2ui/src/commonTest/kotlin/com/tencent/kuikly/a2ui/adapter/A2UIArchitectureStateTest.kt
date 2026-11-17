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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class A2UIArchitectureStateTest {
    @Test
    fun modelAndSubscriptionsHaveIndependentOwnership() {
        val model = A2UISurfaceModel()
        val reactive = A2UIReactiveEngine()
        reactive.registerPath("text", "person")
        val change = model.write("person/name", "Alice", A2UIUpdateOrigin.BINDING)
        assertEquals("Alice", model.read("person/name"))
        assertEquals(setOf("text"), reactive.impact(change).components)
        assertTrue(model.definitions.isEmpty())
        reactive.release("text")
        assertEquals("Alice", model.read("person/name"))
        assertTrue(reactive.impact(change).components.isEmpty())
    }

    @Test
    fun refreshOriginsPreserveLegacyLookupAndCheckBehavior() {
        val reactive = A2UIReactiveEngine()
        reactive.registerPath("rootDependent", "person")
        reactive.registerFunction("exactDependent", "person/name")
        reactive.registerPath("__childlist__list", "person")
        reactive.registerChecks("checkOnly", listOf(mapOf("condition" to true)), setOf("/person/name"))
        val binding = reactive.impact(A2UIDataChange(setOf("person/name"), A2UIUpdateOrigin.BINDING))
        assertEquals(linkedSetOf("exactDependent", "rootDependent"), binding.components)
        assertEquals(setOf("checkOnly"), binding.checks)
        val wire = reactive.impact(A2UIDataChange(setOf("person/name"), A2UIUpdateOrigin.WIRE))
        assertEquals(linkedSetOf("exactDependent", "rootDependent"), wire.components)
        val choice = reactive.impact(A2UIDataChange(setOf("person"), A2UIUpdateOrigin.CHOICE))
        assertEquals(setOf("rootDependent"), choice.components)
        assertTrue(choice.checks.isEmpty())
    }

    @Test
    fun modelBatchQueriesFollowReplacementWithoutCapturingOldMap() {
        val model = A2UISurfaceModel()
        model.replaceBatchParents(mapOf("child" to "first"))
        assertEquals("first", model.currentParents["child"])
        model.replaceBatchParents(mapOf("child" to "second"))
        assertEquals("second", model.currentParents["child"])
        model.rememberParent("child", "persistent")
        model.beginComponentBatch()
        assertTrue(model.currentParents.isEmpty())
        assertEquals("persistent", model.originalParents["child"])
    }

    @Test
    fun resetRetainsOnlyTheExistingCompatibilityScope() {
        val model = A2UISurfaceModel()
        model.rememberDefinition("text", mapOf("component" to "Text"))
        model.rememberCatalog("text", "catalog")
        model.surfaceId = "surface"
        model.primaryColor = "#123456"
        model.write("value", 42, A2UIUpdateOrigin.WIRE)
        model.reset()
        assertTrue(model.data.isEmpty())
        assertTrue(model.componentCatalogs.isEmpty())
        assertEquals("", model.surfaceId)
        assertFalse(model.sendDataModel)
        assertEquals("#123456", model.primaryColor)
        assertTrue(model.definitions.containsKey("text"))
    }
}

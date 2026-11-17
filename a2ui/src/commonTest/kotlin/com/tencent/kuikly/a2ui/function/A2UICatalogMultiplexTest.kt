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

package com.tencent.kuikly.a2ui.function

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for PR-8 multi-catalog support in
 * [A2UICustomFunctionRegistry] and [A2UICatalogDescriptorRegistry].
 *
 * These registries are the Kotlin-only scaffolding that lets
 * [com.tencent.kuikly.a2ui.adapter.A2UIProtocolAdapter] resolve
 * `@catalogId` per component and `funcCall["catalogId"]` per
 * FunctionCall site WITHOUT dragging a JSON-schema parser into
 * commonMain. Keeping the tests at the registry level means we can
 * exercise the lookup fall-back rules deterministically, without
 * standing up a full adapter / surface.
 */
class A2UICatalogMultiplexTest {

    @BeforeTest
    fun setup() {
        A2UICustomFunctionRegistry.clearAll()
        A2UICatalogDescriptorRegistry.clearAll()
    }

    @AfterTest
    fun teardown() {
        A2UICustomFunctionRegistry.clearAll()
        A2UICatalogDescriptorRegistry.clearAll()
    }

    // ------------------------------------------------------------------
    //  A2UICustomFunctionRegistry \u2014 catalogId-aware lookup
    // ------------------------------------------------------------------

    @Test
    fun customFunction_catalogQualifiedLookupTakesPrecedence() {
        // Register the SAME function name under two different catalogs.
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "formatPhone",
                returnType = A2UIFunctionReturnType.STRING,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = "custom_functions_catalog_v1_0.json"
            )
        )
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "formatPhone",
                returnType = A2UIFunctionReturnType.BOOLEAN,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = "legacy_catalog.json"
            )
        )

        // Exact catalog-qualified hit returns the matching descriptor.
        val hit1 = A2UICustomFunctionRegistry.lookup(
            "formatPhone", "custom_functions_catalog_v1_0.json"
        )
        assertEquals(A2UIFunctionReturnType.STRING, hit1?.returnType)

        val hit2 = A2UICustomFunctionRegistry.lookup(
            "formatPhone", "legacy_catalog.json"
        )
        assertEquals(A2UIFunctionReturnType.BOOLEAN, hit2?.returnType)
    }

    @Test
    fun customFunction_unqualifiedLookupFallsBackToGlobal() {
        // Global descriptor (catalogId == null) + a catalog-qualified one
        // for the same name. A lookup WITHOUT a catalogId MUST reach the
        // global descriptor (not the catalog-qualified one).
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "logEvent",
                returnType = A2UIFunctionReturnType.VOID,
                invokeMode = A2UIFunctionInvokeMode.ASYNC,
                catalogId = null
            )
        )
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "logEvent",
                returnType = A2UIFunctionReturnType.STRING,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = "custom_functions_catalog_v1_0.json"
            )
        )

        val globalHit = A2UICustomFunctionRegistry.lookup("logEvent")
        assertEquals(A2UIFunctionReturnType.VOID, globalHit?.returnType)
    }

    @Test
    fun customFunction_qualifiedLookupFallsBackToGlobalWhenCatalogMisses() {
        // Only a global descriptor is registered.
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "getGreeting",
                returnType = A2UIFunctionReturnType.STRING,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = null
            )
        )

        // Looking up with a catalogId that has nothing registered must
        // still find the global descriptor (fallback rule).
        val hit = A2UICustomFunctionRegistry.lookup(
            "getGreeting", "some_other_catalog.json"
        )
        assertEquals(A2UIFunctionReturnType.STRING, hit?.returnType)
    }

    @Test
    fun customFunction_wireSpecRegisterOverloadAcceptsCatalogId() {
        assertTrue(
            A2UICustomFunctionRegistry.register(
                name = "calculateDiscount",
                spec = "number|sync",
                catalogId = "custom_functions_catalog_v1_0.json"
            )
        )
        val hit = A2UICustomFunctionRegistry.lookup(
            "calculateDiscount", "custom_functions_catalog_v1_0.json"
        )
        assertEquals(A2UIFunctionReturnType.NUMBER, hit?.returnType)
        assertEquals("custom_functions_catalog_v1_0.json", hit?.catalogId)
    }

    @Test
    fun customFunction_reservedNamesStillIgnoredRegardlessOfCatalog() {
        // Reserved names are protected at the global level AND must not
        // be bypassable by attaching a catalogId.
        assertFalse(
            A2UICustomFunctionRegistry.register(
                name = "openUrl",
                spec = "void|sync",
                catalogId = "some_catalog.json"
            )
        )
        assertNull(A2UICustomFunctionRegistry.lookup("openUrl", "some_catalog.json"))
    }

    @Test
    fun customFunction_unregisterRespectsCatalogQualifier() {
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "formatPhone",
                returnType = A2UIFunctionReturnType.STRING,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = null
            )
        )
        A2UICustomFunctionRegistry.register(
            A2UICustomFunctionDescriptor(
                name = "formatPhone",
                returnType = A2UIFunctionReturnType.BOOLEAN,
                invokeMode = A2UIFunctionInvokeMode.SYNC,
                catalogId = "custom_functions_catalog_v1_0.json"
            )
        )

        // Unregister ONLY the catalog-qualified one.
        A2UICustomFunctionRegistry.unregister(
            "formatPhone", "custom_functions_catalog_v1_0.json"
        )

        // Qualified lookup now falls back to the global descriptor.
        val hit = A2UICustomFunctionRegistry.lookup(
            "formatPhone", "custom_functions_catalog_v1_0.json"
        )
        assertEquals(A2UIFunctionReturnType.STRING, hit?.returnType)
        // Qualified unregister must NOT remove the global one.
        assertEquals(
            A2UIFunctionReturnType.STRING,
            A2UICustomFunctionRegistry.lookup("formatPhone")?.returnType
        )
    }

    // ------------------------------------------------------------------
    //  A2UICatalogDescriptorRegistry \u2014 allowedParents / allowedChildren
    // ------------------------------------------------------------------

    @Test
    fun componentConstraint_normalisationDropsEmptyLists() {
        val raw = A2UIComponentConstraint(
            allowedParents = emptyList(),
            allowedChildren = listOf("Button")
        )
        val norm = raw.normalised()
        assertNull(norm.allowedParents)
        assertEquals(listOf("Button"), norm.allowedChildren)
    }

    @Test
    fun catalogDescriptor_lookupPrefersCatalogQualifiedEntry() {
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(
                allowedParents = listOf("Surface", "Row", "Column", "Card", "Modal")
            ),
            catalogId = "custom_components_catalog_v1_0.json"
        )
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(
                allowedParents = listOf("Surface") // global stricter default
            ),
            catalogId = null
        )

        val qualified = A2UICatalogDescriptorRegistry.lookup(
            "A2UIWeb", "custom_components_catalog_v1_0.json"
        )
        assertEquals(
            listOf("Surface", "Row", "Column", "Card", "Modal"),
            qualified?.allowedParents
        )

        val global = A2UICatalogDescriptorRegistry.lookup("A2UIWeb")
        assertEquals(listOf("Surface"), global?.allowedParents)
    }

    @Test
    fun catalogDescriptor_lookupFallsBackToGlobalWhenCatalogMisses() {
        A2UICatalogDescriptorRegistry.register(
            componentType = "MyA2UIView",
            constraint = A2UIComponentConstraint(allowedChildren = listOf("Text")),
            catalogId = null
        )
        val hit = A2UICatalogDescriptorRegistry.lookup(
            "MyA2UIView", "unknown_catalog.json"
        )
        assertEquals(listOf("Text"), hit?.allowedChildren)
    }

    @Test
    fun catalogDescriptor_registerEmptyConstraintRemovesExistingEntry() {
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(allowedParents = listOf("Surface")),
            catalogId = null
        )
        // Overwriting with an empty constraint is treated as "no constraint".
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(),
            catalogId = null
        )
        assertNull(A2UICatalogDescriptorRegistry.lookup("A2UIWeb"))
    }

    @Test
    fun catalogDescriptor_unregisterIsIsolatedAcrossCatalogs() {
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(allowedParents = listOf("Row")),
            catalogId = "custom_components_catalog_v1_0.json"
        )
        A2UICatalogDescriptorRegistry.register(
            componentType = "A2UIWeb",
            constraint = A2UIComponentConstraint(allowedParents = listOf("Surface")),
            catalogId = null
        )

        A2UICatalogDescriptorRegistry.unregister(
            "A2UIWeb", "custom_components_catalog_v1_0.json"
        )

        // Catalog-qualified lookup now falls through to the global entry.
        assertEquals(
            listOf("Surface"),
            A2UICatalogDescriptorRegistry.lookup(
                "A2UIWeb", "custom_components_catalog_v1_0.json"
            )?.allowedParents
        )
        // Global entry untouched.
        assertEquals(
            listOf("Surface"),
            A2UICatalogDescriptorRegistry.lookup("A2UIWeb")?.allowedParents
        )
    }
}

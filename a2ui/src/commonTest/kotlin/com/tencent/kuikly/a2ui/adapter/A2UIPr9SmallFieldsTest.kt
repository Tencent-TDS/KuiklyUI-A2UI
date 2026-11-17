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

import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for PR-9 "small-field" catalog additions:
 *
 *  * `Video.posterUrl`    (DynamicString -> native `posterUrl` prop)
 *  * `TextField.placeholder` (DynamicString -> Kuikly `placeholder` attr)
 *  * `Slider.steps`       (Int -> consumed by Applier's snap logic)
 *
 * Plus an end-to-end check that v1.0 `action.event.userMessage`
 * DynamicStrings are resolved by [A2UIProtocolAdapter.getActionForTag]
 * without any PR-9 code change (recursive resolveActionValue already
 * handles arbitrarily nested DynamicValues).
 */
class A2UIPr9SmallFieldsTest {

    // ------------------------------------------------------------------
    //  Component mapper — new v1.0 small fields
    // ------------------------------------------------------------------

    @Test
    fun mapVideoProp_posterUrl_literalIsForwarded() {
        val out = A2UIComponentMapper.mapProp(
            "Video", "posterUrl", "https://cdn/x.jpg"
        )
        assertNotNull(out)
        assertEquals("posterUrl", out.first)
        // resolveTextValue wraps literals in A2UIValue.Literal so that the
        // adapter can tell "a plain string" apart from "a path binding";
        // the raw string only reappears after resolveValue at buildProps time.
        assertEquals(A2UIValue.Literal("https://cdn/x.jpg"), out.second)
    }

    @Test
    fun mapVideoProp_posterUrl_dynamicBindingStaysAsDynamicValue() {
        val binding = mapOf("path" to "/video/cover")
        val out = A2UIComponentMapper.mapProp("Video", "posterUrl", binding)
        assertNotNull(out)
        assertEquals("posterUrl", out.first)
        // resolveTextValue packages PathBinding references; the Adapter
        // resolves them at buildProps time via resolveValue, so here we
        // only assert the mapper didn't silently drop the binding form.
        assertNotNull(out.second)
    }

    @Test
    fun mapTextFieldProp_placeholder_literalIsForwarded() {
        val out = A2UIComponentMapper.mapProp(
            "TextField", "placeholder", "Search ..."
        )
        assertNotNull(out)
        assertEquals("placeholder", out.first)
        assertEquals(A2UIValue.Literal("Search ..."), out.second)
    }

    @Test
    fun mapTextFieldProp_label_stillNotForwarded() {
        // Regression guard: `label` must keep producing "" to null so it
        // is rendered as a sibling Text (per A2UI reference renderer),
        // NOT accidentally routed to `placeholder` by the PR-9 wiring.
        val out = A2UIComponentMapper.mapProp(
            "TextField", "label", "First name"
        )
        assertNotNull(out)
        assertEquals("", out.first)
        assertNull(out.second)
    }

    @Test
    fun mapSliderProp_steps_isForwardedVerbatim() {
        val out = A2UIComponentMapper.mapProp("Slider", "steps", 10)
        assertNotNull(out)
        assertEquals("steps", out.first)
        assertEquals(10, out.second)
    }

    @Test
    fun mapSliderProp_knownFieldsStillWork() {
        // Regression guard around the PR-9 addition: the pre-existing
        // Slider mappings (value/min/max/colors) MUST continue to work.
        assertEquals("min" to 0, A2UIComponentMapper.mapProp("Slider", "min", 0))
        assertEquals("max" to 100, A2UIComponentMapper.mapProp("Slider", "max", 100))
        assertEquals(
            "progressColor" to "#FF0000",
            A2UIComponentMapper.mapProp("Slider", "progressColor", "#FF0000")
        )
    }

    // ------------------------------------------------------------------
    //  Adapter — v1.0 action.event.userMessage transparent resolve
    // ------------------------------------------------------------------

    /**
     * v1.0 protocol: `action.event.userMessage` is a DynamicString carried
     * inside the action payload. The Adapter's `resolveActionValue` walks
     * the action map recursively and flattens DynamicValues, so the host
     * receives the resolved literal string. This test guards that the
     * recursive walk really reaches arbitrarily nested keys, and that
     * PR-9's broader changes did not regress the path.
     *
     * The adapter is pinned to v1.0 strict mode via
     * [A2UIProtocolAdapter.setExpectedProtocolVersion] to match the
     * scope's "severity v1.0 strict" clause.
     */
    @Test
    fun actionUserMessage_isResolvedInline() {
        val adapter = A2UIProtocolAdapter()
        adapter.setExpectedProtocolVersion(A2UIProtocolVersion.V1_0)
        adapter.strictV10 = true

        // Seed a surface so component updates pass surfaceId validation.
        adapter.convertMessage(
            mapOf(
                "version" to "1.0",
                "createSurface" to mapOf(
                    "surfaceId" to "s1",
                    "catalogId" to "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json",
                )
            )
        )
        // Preload a data-model value that the DynamicString binding
        // below points at. The envelope key is `updateDataModel` and its
        // payload field is `value` — NOT `setDataModel`/`data`.
        adapter.convertMessage(
            mapOf(
                "version" to "1.0",
                "updateDataModel" to mapOf(
                    "surfaceId" to "s1",
                    "value" to mapOf("user" to mapOf("name" to "Alice"))
                )
            )
        )

        // Emit a button with a v1.0-style action whose `event.userMessage`
        // is a DynamicString path binding. Under v1.0 the mixin key is
        // `@path`, and Button requires a `child` rather than a `label`.
        adapter.convertMessage(
            mapOf(
                "version" to "1.0",
                "updateComponents" to mapOf(
                    "surfaceId" to "s1",
                    "components" to listOf(
                        mapOf(
                            "id" to "btn-1",
                            "component" to "Button",
                            "child" to "btn-1-text",
                            "action" to mapOf(
                                "event" to mapOf(
                                    "name" to "greet",
                                    // DynamicString: resolves to "Alice" from the data model.
                                    "userMessage" to mapOf("@path" to "/user/name")
                                )
                            )
                        ),
                        mapOf(
                            "id" to "btn-1-text",
                            "component" to "Text",
                            "text" to "Say hi"
                        )
                    )
                )
            )
        )

        // Grab the action for `btn-1` and inspect the resolved JSON.
        // getActionForTag is keyed by the internal tag the adapter mints;
        // we iterate through getActionTags to find it without scraping
        // protocol-tag internals.
        val tags = adapter.getActionTags()
        assertTrue(tags.isNotEmpty(), "Adapter must have recorded at least one action")
        val actionJson = adapter.getActionForTag(tags.first())
        assertNotNull(actionJson)
        // The DynamicString must have been flattened to the literal "Alice",
        // not left as a nested {"path":"/user/name"} map.
        assertTrue(
            actionJson.contains("\"userMessage\":\"Alice\""),
            "action JSON should contain resolved userMessage, got: $actionJson"
        )
        assertTrue(
            !actionJson.contains("\"path\":\"/user/name\""),
            "action JSON should NOT retain the raw path binding, got: $actionJson"
        )
    }
}

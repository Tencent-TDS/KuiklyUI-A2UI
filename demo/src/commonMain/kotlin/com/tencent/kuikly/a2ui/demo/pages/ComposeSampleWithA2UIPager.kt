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

package com.tencent.kuikly.a2ui.demo.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.a2ui.dsl.embed.A2UISurfaceDSLView
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.extension.MakeKuiklyComposeNode
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.MeasurePolicy
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page

/**
 * ComposeSampleWithA2UIPager — a Kuikly Compose page (built on top of
 * [ComposeContainer]) that hosts an [A2UISurfaceDSLView] as one of its
 * children via the standard Compose ↔ Kuikly interop bridge
 * ([MakeKuiklyComposeNode]).
 *
 * Purpose: shows off the *Compose Page integration* path for A2UI —
 * i.e. business code that already renders its screen with Kuikly Compose
 * (`:compose`) can drop an A2UI surface into its Composable tree without
 * switching to hand-written Kuikly DSL.
 *
 * Rendering layout:
 *  1. A Compose header (Text) describing the sample.
 *  2. An [A2UISurfaceDSLView] streamed with a hand-crafted A2UI v0.9
 *     "weather card" payload. The payload is built inline (no assets
 *     dependency) so this page compiles in isolation.
 *
 * The A2UI JSON is intentionally kept small and self-contained — it uses
 * only the semantic components Text / Column / Row / Card so it renders
 * out-of-the-box on any A2UI runtime.
 */
@Page("ComposeSampleWithA2UI")
class ComposeSampleWithA2UIPager : ComposeContainer() {

    override fun willInit() {
        super.willInit()
        // Snapshot the payload to a local so the `viewInit` lambda below
        // (whose implicit receiver is A2UISurfaceDSLView, not this Pager)
        // doesn't need to reach through the outer class receiver.
        val envelopes = weatherCardEnvelopes()
        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Compose 页面接入 A2UI 示例",
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "宿主：Kuikly Compose（ComposeContainer + setContent）\n嵌入：A2UISurfaceDSLView（通过 MakeKuiklyComposeNode 桥接）",
                    color = Color(0xFF666666),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                A2UISurfaceComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5)),
                    viewInit = {
                        // Feed the weather-card envelopes right after mount.
                        // A2UISurfaceDSLView buffers pre-didInit() writes so
                        // ordering here is safe.
                        for (envelope in envelopes) {
                            writeStream(envelope)
                        }
                        finishStream()
                    }
                )
            }
        }
    }

    /**
     * Hand-crafted A2UI v0.9 "weather card" envelopes.
     *
     * Follows the same shape as the shared demo sample in
     * [A2UISurfaceDemoSample.kt] — see that file's KDoc for the protocol
     * rationale (top-level envelopes; `catalogId` on `createSurface`;
     * `components` array with string-id references; `Card.child` is a *single*
     * string; `Row/Column` use only `align`/`children`; `variant` values
     * limited to those defined by the basic catalog).
     */
    private fun weatherCardEnvelopes(): List<String> {
        val surfaceId = "compose-weather-surface"
        val createSurface = """
            {
              "version": "v0.9",
              "createSurface": {
                "surfaceId": "$surfaceId",
                "catalogId": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"
              }
            }
        """.trimIndent()
        val updateComponents = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "$surfaceId",
                "components": [
                  { "id": "root", "component": "Card", "child": "outer" },
                  {
                    "id": "outer", "component": "Column",
                    "children": ["title", "hero", "detail"]
                  },
                  {
                    "id": "title", "component": "Text",
                    "text": "深圳 · 今日天气", "variant": "h4"
                  },
                  {
                    "id": "hero", "component": "Row",
                    "align": "center", "children": ["temp", "cond"]
                  },
                  {
                    "id": "temp", "component": "Text",
                    "text": "27°C", "variant": "h4"
                  },
                  {
                    "id": "cond", "component": "Text",
                    "text": "多云转晴", "variant": "body"
                  },
                  {
                    "id": "detail", "component": "Column",
                    "children": ["hum", "wind", "aqi"]
                  },
                  { "id": "hum", "component": "Text", "text": "湿度 68%", "variant": "caption" },
                  { "id": "wind", "component": "Text", "text": "东南风 3 级", "variant": "caption" },
                  { "id": "aqi", "component": "Text", "text": "空气 良 (58)", "variant": "caption" }
                ]
              }
            }
        """.trimIndent()
        return listOf(createSurface, updateComponents)
    }
}

// ===================================================================
// A2UISurfaceComposable — a local Compose wrapper around
// A2UISurfaceDSLView that makes the surface height wrap its A2UI
// content when the parent gives it an unbounded height constraint.
//
// This is a *demo-scoped* helper (方案 C-3): the wrap-content behaviour
// is intentionally not baked into the a2ui library to avoid introducing
// a Compose dependency there. If more Compose pages need this, promote
// it into a proper a2ui-compose module.
// ===================================================================

/**
 * A Composable wrapper around [A2UISurfaceDSLView] that always sizes the
 * surface to its A2UI-natural content height (wrap-content on the vertical
 * axis), clamped to the parent's `[minHeight, maxHeight]` constraint.
 *
 * ### Behaviour
 *
 *  - **Width**: taken from the parent's `constraints.maxWidth` when the
 *    parent is bounded on the horizontal axis; falls back to `0` otherwise.
 *    Callers should always give A2UI a bounded width (e.g. via
 *    `Modifier.fillMaxWidth()`).
 *  - **Height**: every measure pass asks
 *    [A2UISurfaceDSLView.measureContentHeight] for the current natural
 *    height of the A2UI subtree at the offered width, then clamps it to
 *    `constraints.minHeight..constraints.maxHeight`. A local
 *    `mutableIntStateOf` — driven by
 *    [A2UISurfaceDSLView.onContentSizeChanged] — is read inside the
 *    measure block so the Compose runtime remeasures whenever the A2UI
 *    stream mutates the tree.
 *
 * Consequences for the caller:
 *
 *  - Unbounded parent (`Column { A2UISurface(...) }`) → surface grows to
 *    exactly its content height, no wasted whitespace.
 *  - Bounded but oversized parent (`Column(fillMaxSize) { A2UISurface(...) }`)
 *    → surface still collapses to its content height instead of stretching.
 *    If you *want* the surface to fill the leftover space, wrap it in a
 *    `Box` / `Column(weight = 1f)` and give it a `.height(...)` /
 *    `Modifier.weight(...)` — the clamp will make it grow to at least
 *    the parent's min height.
 *  - Streamed A2UI payloads visually grow in stages (each stream chunk
 *    can cause the surface to expand). This matches the streaming
 *    nature of A2UI and is the accepted tradeoff of the wrap-content
 *    strategy.
 */
@Composable
private fun A2UISurfaceComposable(
    modifier: Modifier = Modifier,
    viewInit: A2UISurfaceDSLView.() -> Unit = {},
    viewUpdate: (A2UISurfaceDSLView) -> Unit = {}
) {
    // Bumped each time A2UISurfaceDSLView.performLayoutAndReportHeight()
    // finishes, forcing Compose to re-run the measure block below and
    // pick up the new content height.
    var contentSizeTick by remember { mutableIntStateOf(0) }

    // Captures the surface view instance for the measurePolicy below.
    // Using a MutableState so the (remembered) measurePolicy's closure
    // always sees the current view, even across recompositions.
    val surfaceHolder = remember { mutableStateOf<A2UISurfaceDSLView?>(null) }

    val measurePolicy = remember {
        MeasurePolicy { _, constraints ->
            // Read the tick inside measure so any A2UI content-size change
            // triggers a Compose remeasure.
            @Suppress("UNUSED_VARIABLE")
            val tick = contentSizeTick

            // Width: honour the parent's bounded width; fall back to 0 when
            // the parent is fully unbounded (callers are expected to give
            // A2UI a bounded width via e.g. `.fillMaxWidth()`).
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0

            // Height: always wrap A2UI content, then clamp to the parent's
            // [minHeight, maxHeight]. This lets a plain `Column { A2UI(...) }`
            // (unbounded height) size the surface to its natural content, and
            // also collapses a `Column(fillMaxSize) { A2UI(...) }` (bounded
            // but oversized parent) down to the same natural content height
            // instead of letting the surface stretch to fill the leftover.
            val availableWidthDp = width.toDp().value
            val view = surfaceHolder.value
            val measuredDp = view?.measureContentHeight(availableWidthDp) ?: 0f
            val measuredPx = measuredDp.dp.roundToPx()
            val height = measuredPx.coerceIn(constraints.minHeight, constraints.maxHeight)

            layout(width, height) {
                // Nothing to place — the surface's own children are laid out
                // by Kuikly Core inside the native view, not by Compose.
            }
        }
    }

    MakeKuiklyComposeNode<A2UISurfaceDSLView>(
        factory = { A2UISurfaceDSLView() },
        modifier = modifier,
        viewInit = {
            surfaceHolder.value = this
            onContentSizeChanged = {
                // Cheap remeasure trigger: bump a state read by the
                // measurePolicy above.
                contentSizeTick += 1
            }
            viewInit()
        },
        viewUpdate = {
            viewUpdate(it)
        },
        measurePolicy = measurePolicy
    )
}

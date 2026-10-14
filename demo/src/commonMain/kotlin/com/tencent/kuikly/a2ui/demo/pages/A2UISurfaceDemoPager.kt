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

import com.tencent.kuikly.a2ui.dsl.embed.A2UISurface
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.ScrollerView
import com.tencent.kuikly.core.views.compose.Button

/**
 * A2UISurfaceDemoPager — the reference DSL page that hosts A2UI surfaces
 * inside a hand-written Kuikly page.
 *
 * ### Demo scenario
 * A single header (`RichText`) shows the last A2UI action, followed by a
 * vertical [Scroller] filled with A2UI cards. Tapping the button at the
 * bottom appends a **new** [A2UISurface] to the scroller — mirroring the
 * flow where an AI backend streams a fresh card into a conversation:
 *
 *     ┌──────────────────────────────┐
 *     │ Last A2UI action: (...)      │  ← RichText
 *     ├──────────────────────────────┤
 *     │  ┌──────── AI Card #1 ────┐  │
 *     │  │  Card > Column > Text  │  │  ← Scroller
 *     │  └────────────────────────┘  │      each item = one A2UISurface
 *     │  ┌──────── AI Card #2 ────┐  │      (its own instanceId + surfaceId)
 *     │  │  ...                   │  │
 *     │  └────────────────────────┘  │
 *     ├──────────────────────────────┤
 *     │  [ + Add new AI card ]       │  ← Button
 *     └──────────────────────────────┘
 *
 * ### Runtime pipeline
 * 1. `:demo` (this module) is compiled with `core-ksp`, which discovers
 *    the [Page] annotation and registers the string `"A2UISurfaceDemo"` in
 *    the generated `PagerRoute`.
 * 2. `:androidAppForKuiklyPage/A2UISurfaceDSLDemoActivity` creates a `KuiklyBaseView`
 *    and calls `onAttach("", "A2UISurfaceDemo", ...)` — the page loader
 *    resolves the string to this class and instantiates it.
 * 3. Everything inside `body()` runs as a normal Kuikly page. Each
 *    [A2UISurface] child is a plain `A2UISurfaceDSLView` living in the same
 *    Flex tree as its siblings — no inner Pager, no wrapper native view.
 *
 * ### Adding more DSL example pages
 * Just drop another `@Page("XXX") class ... : Pager()` file in this package
 * and add a corresponding entry to `A2UIDemoActivity` in the androidApp.
 */
@Page("A2UISurfaceDemo")
class A2UISurfaceDemoPager : Pager() {

    /**
     * Backing list of *card kinds*. Each element stores which of the 20 A2UI
     * card layouts to mount (see [sampleCardEnvelopes]); the element's
     * *position* in the list is used as the surfaceId suffix
     * (`native-*-\$seq` inside the envelope templates). Using
     * [observableList] lets [vforIndex] incrementally mount a new child when
     * we [MutableList.add] a new kind, without rebuilding the whole
     * scroller.
     *
     * Seeded in [init] with the first [INITIAL_CARD_COUNT] kinds so the
     * page is never empty on first render.
     */
    private val cardKinds by observableList<Int>()

    init {
        // Pre-populate the scroller so the page is not empty on entry.
        // Runs before body() is called, so vforIndex picks all N up in the
        // very first render pass instead of mounting them one-by-one after
        // initial layout.
        repeat(INITIAL_CARD_COUNT) { i -> cardKinds.add(i) }
    }

    companion object {
        /** How many cards are visible on first entry. */
        private const val INITIAL_CARD_COUNT: Int = 3
    }

    /** Last action tapped by the user (rendered above the scroller). */
    private var lastAction: String by observable("(none)")

    /**
     * Reference to the Scroller so the "+" button can programmatically scroll
     * it to the bottom after appending a new card. Set once during the first
     * body() invocation and stable for the lifetime of the pager.
     */
    private var scroller: ScrollerView<*, *>? = null

    /**
     * How many upcoming `contentSizeChanged` events should still trigger an
     * "auto scroll to bottom". Bumped up on every "+" tap and decremented
     * inside `contentSizeChanged`. A single tap arms multiple ticks because
     * A2UI card layout typically stabilizes across a few frames (initial
     * shell → text measured → image loaded etc.); consuming several ticks
     * keeps the viewport pinned to the bottom until the card truly stops
     * growing, without permanently overriding the user's manual scrolls.
     */
    private var pendingScrollToBottomTicks: Int = 0

    override fun body(): ViewBuilder {
        // Capture the Pager so nested DSL lambdas can reach observable state
        // without needing to spell out `this@A2UISurfaceDemoPager` each time.
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
                flexDirectionColumn()
                padding(16f)
            }

            // 1. Header — echoes the last A2UI action that fired.
            RichText {
                attr {
                    text("Last A2UI action: ${ctx.lastAction}")
                    fontSize(14f)
                    color(Color.BLACK)
                    marginBottom(12f)
                }
            }

            // 2. Scroller filled with dynamically-appended A2UI cards.
            //    `vforIndex({ ctx.cardKinds })` re-runs its item builder only
            //    for newly-added entries, so each existing surface keeps its
            //    own state / bridge / rendered subtree intact when we append.
            //    The lambda receives (kind, index, total) — we pass `index`
            //    into the sample builders to derive a unique surfaceId, and
            //    `kind` to pick which of the 3 sample trees to mount.
            Scroller {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5.toInt()))
                    borderRadius(8f)
                }
                // Capture the Scroller instance so the button below can drive
                // its content offset. `this@Scroller` inside the init lambda
                // is the actual ScrollerView.
                ctx.scroller = this@Scroller

                event {
                    // Fires whenever the internal contentView's size changes,
                    // i.e. every time an A2UI card finishes a layout frame.
                    // If a button tap recently armed us, animate to the
                    // bottom so the newest card is fully visible.
                    contentSizeChanged { _, contentHeight ->
                        if (ctx.pendingScrollToBottomTicks > 0) {
                            ctx.pendingScrollToBottomTicks -= 1
                            this@Scroller.scrollToBottom(contentHeight, animated = true)
                        }
                    }
                }

                vforIndex({ ctx.cardKinds }) { kind, index, _ ->
                    // One A2UISurface per entry. writeStream() is safe to call
                    // inside this init lambda — the surface view buffers pre-
                    // didInit() writes internally (see A2UISurfaceDSLView).
                    A2UISurface {
                        attr {
                            marginBottom(12f)
                            // Give each card a bit of visible bounds while
                            // A2UI-generated content lays itself out.
                            backgroundColor(Color.WHITE)
                            borderRadius(6f)
                        }
                        event {
                            action { _, tag, actionJson ->
                                ctx.lastAction = formatActionForHeader(index, tag, actionJson)
                            }
                            openUrl { url ->
                                ctx.lastAction = "card #${index + 1} openUrl: $url"
                            }
                        }
                        // Feed this surface's two envelopes right away —
                        // A2UISurfaceDSLView will flush them on didInit().
                        val (createEnvelope, updateEnvelope) =
                            sampleCardEnvelopes(index, kind)
                        writeStream(createEnvelope)
                        writeStream(updateEnvelope)
                        finishStream()
                    }
                }
            }

            // 3. "Add a new AI card" button — cycles through the 3 card
            //    kinds in a fixed order (text → list → image+row+column →
            //    text → …) and appends the next one to cardKinds, which
            //    triggers vforIndex to mount a fresh A2UISurface at the
            //    bottom of the scroller. Simulates "AI streams a new card
            //    into the conversation" with predictable, easy-to-demo
            //    ordering.
            Button {
                attr {
                    height(44f)
                    marginTop(12f)
                    backgroundColor(Color(0xFF1A73E8L))
                    borderRadius(8f)
                    titleAttr {
                        text("+ Add new AI card")
                        color(Color.WHITE)
                        fontSize(15f)
                    }
                }
                event {
                    click {
                        // 20 kinds cycled in a fixed order — 0..19, then wrap
                        // back to 0. Using the current list size as the round-
                        // robin index keeps the sequence deterministic and
                        // matches the Native Activity demo one-for-one.
                        val nextKind = ctx.cardKinds.size % SAMPLE_CARD_KIND_COUNT
                        ctx.cardKinds.add(nextKind)
                        // Arm the Scroller so the next few contentSizeChanged
                        // callbacks pin the viewport to the bottom while the
                        // freshly-appended A2UI card is laying itself out.
                        // 3 ticks is empirically enough for text/list/image
                        // card kinds; anything after that we leave to the user.
                        ctx.pendingScrollToBottomTicks = 3
                    }
                }
            }
        }
    }
}

/**
 * Scroll this scroller so its very bottom becomes visible.
 *
 * There is no built-in `scrollToBottom()` on Kuikly's [ScrollerView] — the
 * only offset primitive is [ScrollerView.setContentOffset]. This helper
 * fills that gap for the demo, and importantly clamps the offset to
 * `contentHeight - viewportHeight` before forwarding.
 *
 * ### Why the clamp matters (Android only)
 * `KRRecyclerView` guards its immediate scroll with:
 *
 *     offsetY <= contentView.height - viewportHeight
 *
 * If a caller passes the raw `contentHeight` (i.e. an offset past the end),
 * the scroll is *deferred* to "next layout" via `pendingSetContentOffsetStr`.
 * In our stable-layout scenario that next layout never arrives, so the
 * scroll would silently no-op. Clamping here makes the guard pass and the
 * scroll fires on the current frame.
 *
 * @param contentHeight  Latest known height of the scroller's content view,
 *   typically the value delivered by the `contentSizeChanged` callback.
 * @param animated       Whether to animate the offset change.
 */
private fun ScrollerView<*, *>.scrollToBottom(contentHeight: Float, animated: Boolean) {
    val viewportHeight = flexNode.layoutFrame.height
    val maxOffsetY = (contentHeight - viewportHeight).coerceAtLeast(0f)
    setContentOffset(0f, maxOffsetY, animated)
}

/**
 * Turn a raw A2UI `action` JSON delivered by the DSL surface into a compact
 * one-line header string.
 *
 * For the demo-only `event.name == "toast"` convention we surface the
 * `context.title` / `context.message` verbatim (as if they were a native
 * alert dialog). Everything else falls back to the raw JSON so users can
 * still inspect payload shapes for openUrl / custom / etc.
 *
 * In a real Kuikly-page host you would show a proper Kuikly `ModalView`
 * here; this demo intentionally keeps things minimal.
 */
private fun formatActionForHeader(cardIndex: Int, tag: Int, actionJson: String): String {
    val cardLabel = "card #${cardIndex + 1}"
    val event = try {
        JSONObject(actionJson).optJSONObject("event")
    } catch (e: Exception) {
        null
    }
    if (event != null && event.optString("name") == "toast") {
        val context = event.optJSONObject("context")
        val title = context?.optString("title").orEmpty()
        val message = context?.optString("message").orEmpty()
        val body = when {
            title.isNotEmpty() && message.isNotEmpty() -> "$title — $message"
            title.isNotEmpty() -> title
            else -> message
        }
        return "$cardLabel toast: $body"
    }
    return "$cardLabel tag=$tag action=$actionJson"
}

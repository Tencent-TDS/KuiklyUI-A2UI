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
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.layout.Column

/**
 * A2UICaseListPager — a generic Kuikly Page that renders a scrollable list of
 * A2UI test cases, one [A2UISurface] per case.
 *
 * Consumed by the KuiklyPage integration app's component / custom-function /
 * custom-component / custom-icon detail screens. The host Activity reads
 * every `<caseName>.json` in an assets folder and forwards them to this
 * Pager as a JSON array via `pageData.params["cases"]` — no per-case
 * hand-written Pager needed.
 *
 * ### pageData contract
 *  - `title` (String, optional): shown at the top of the page.
 *  - `cases` (JSON array string, required): an array of case entries, each of
 *    which is a JSON object with the following fields:
 *      * `name` (String, optional): shown above the surface as the case label.
 *      * `description` (String, optional): shown just below the case name.
 *      * `messages` (JSON array | JSON object, required): the A2UI envelope
 *        stream for this case, using the same demo-only wrapper accepted by
 *        [A2UIJsonHostPager] and Android's `A2UIDemoStreamUtil`. Cases whose
 *        `messages` is absent or empty are skipped.
 *
 * The `cases` array is passed as a *string* rather than a real JSON array so
 * that the same `Map<String, Any>` `pageData` map works uniformly across
 * platforms (Android/iOS/OHOS onAttach signatures all serialise to string).
 */
@Page("A2UICaseList")
class A2UICaseListPager : Pager() {

    private data class CaseEntry(
        val name: String,
        val description: String,
        val messagesJson: String
    )

    private val cases by observableList<CaseEntry>()
    private var lastAction: String by observable("(none)")

    override fun willInit() {
        super.willInit()
        parseCases()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        val title = pageData.params.optString("title", "")
        return {
            attr {
                backgroundColor(Color.WHITE)
                flexDirectionColumn()
                padding(12f)
            }

            if (title.isNotEmpty()) {
                RichText {
                    attr {
                        text(title)
                        fontSize(16f)
                        color(Color.BLACK)
                        marginBottom(4f)
                    }
                }
            }
            RichText {
                attr {
                    text("Last A2UI action: ${ctx.lastAction}")
                    fontSize(12f)
                    color(Color(0xFF888888L))
                    marginBottom(8f)
                }
            }

            Scroller {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5L))
                    borderRadius(6f)
                    padding(8f)
                }

                vforIndex({ ctx.cases }) { case, index, _ ->
                    // vfor's creator lambda must produce exactly ONE top-level
                    // child. We wrap the name label, description and the
                    // A2UISurface inside a single Column so Kuikly's directive
                    // sees a lone root per iteration.
                    Column {
                        attr {
                            flexDirectionColumn()
                            marginTop(if (index == 0) 0f else 10f)
                        }

                        // Case name label
                        if (case.name.isNotEmpty()) {
                            RichText {
                                attr {
                                    text("${index + 1}. ${case.name}")
                                    fontSize(13f)
                                    color(Color(0xFF333333L))
                                }
                            }
                        }
                        if (case.description.isNotEmpty()) {
                            RichText {
                                attr {
                                    text(case.description)
                                    fontSize(11f)
                                    color(Color(0xFF888888L))
                                    marginTop(2f)
                                    marginBottom(4f)
                                }
                            }
                        }

                        A2UISurface {
                            attr {
                                marginBottom(4f)
                                backgroundColor(Color.WHITE)
                                borderRadius(6f)
                            }
                            event {
                                action { _, tag, actionJson ->
                                    ctx.lastAction =
                                        "case #${index + 1} tag=$tag action=$actionJson"
                                }
                                openUrl { url ->
                                    ctx.lastAction = "case #${index + 1} openUrl: $url"
                                }
                            }
                            ctx.feedEnvelopes(this, case.messagesJson)
                            finishStream()
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse `pageData.params["cases"]` into [cases]. Cases missing or with
     * an empty `messages` field are dropped silently — the host activity is
     * responsible for producing a well-formed array.
     */
    private fun parseCases() {
        val casesString = pageData.params.optString("cases", "")
        if (casesString.isEmpty()) return
        val arr = try {
            JSONArray(casesString)
        } catch (_: Throwable) {
            return
        }
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val messagesJson = extractMessages(obj)
            if (messagesJson.isEmpty()) continue
            cases.add(
                CaseEntry(
                    name = obj.optString("name", ""),
                    description = obj.optString("description", ""),
                    messagesJson = messagesJson
                )
            )
        }
    }

    /**
     * Pull the "messages" field out of a case object. Supports both:
     *  - array form: `"messages": [ {envelope}, ... ]`
     *  - object form: `"messages": {envelope}`
     * Returns an empty string when the field is absent so the caller can
     * skip the case.
     */
    private fun extractMessages(caseObj: JSONObject): String {
        val arr = caseObj.optJSONArray("messages")
        if (arr != null) return arr.toString()
        val obj = caseObj.optJSONObject("messages")
        if (obj != null) return obj.toString()
        return ""
    }

    /**
     * Split the supplied `messagesJson` into individual envelopes and feed
     * each into [surface] via [A2UISurface.writeStream]. Accepts either a
     * top-level array or a single object; anything else is dropped.
     */
    private fun feedEnvelopes(
        surface: com.tencent.kuikly.a2ui.dsl.embed.A2UISurfaceDSLView,
        messagesJson: String
    ) {
        val trimmed = messagesJson.trimStart()
        if (trimmed.isEmpty()) return
        when (trimmed.first()) {
            '[' -> {
                val array = try {
                    JSONArray(messagesJson)
                } catch (_: Throwable) {
                    return
                }
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    surface.writeStream(obj.toString())
                }
            }
            '{' -> {
                try {
                    JSONObject(messagesJson)
                } catch (_: Throwable) {
                    return
                }
                surface.writeStream(messagesJson)
            }
            else -> Unit
        }
    }
}

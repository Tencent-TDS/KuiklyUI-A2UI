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

package com.tencent.kuikly.a2ui.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Size
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UIKuiklyBaseViewSupport
import com.tencent.kuikly.core.render.android.expand.KuiklyBaseView

/**
 * A2UISurfaceDSLDemoActivity — showcases the *DSL / Compose embedding* path:
 * a hand-written Kuikly page (loaded here as pageName `"A2UISurfaceDemo"`,
 * defined in `:demo`) hosts an [A2UISurface] child that is fed with A2UI
 * protocol chunks and rendered as part of the surrounding page's Flex tree —
 * *without* any wrapper native view.
 *
 * ### Coexistence with the native-View path
 * This app also ships [CardDemoActivity] et al., which use the classic
 * `A2UISurfaceView` native-View integration. Both paths live in the same APK,
 * pull the same `:a2ui` KMP runtime, and register their bridges under
 * disjoint pagerIds — there is no runtime conflict. See the "Coexistence"
 * section in `a2ui/README.md` for details.
 *
 * ### Wiring
 * The KuiklyBaseView + `onAttach(pageName=…)` combo below is intentionally
 * minimal (compare with the fuller lifecycle plumbing in
 * `androidApp/KuiklyPageView.kt`). If you later need onResume/onPause forwarding
 * — e.g. to pause videos while the Activity is backgrounded — swap to the same
 * lifecycle pattern used by the top-level Kuikly demo.
 */
class A2UISurfaceDSLDemoActivity : AppCompatActivity() {

    /** The single KuiklyBaseView hosting the DSL page. */
    private var renderView: KuiklyBaseView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        root.addView(buildTitleBar())

        // Container filling the remaining vertical space so the Kuikly page
        // has a positive size when we call onAttach() below.
        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        root.addView(container)
        setContentView(root)

        // Post-attach so `container` has been measured — we pass an explicit
        // Size so the KuiklyRenderView delegator doesn't have to wait for its
        // first onLayout pass. Using displayMetrics is a safe upper bound
        // (the delegator honours real frame dimensions once available).
        container.post {
            // Wrap with A2UIKuiklyBaseViewSupport so any A2UI-registered
            // custom render view (e.g. from A2UICustomViewDemo) is installed
            // onto this Kuikly page's IKuiklyRenderExport too.
            val view = KuiklyBaseView(this, A2UIKuiklyBaseViewSupport.wrap())
            container.addView(
                view,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            renderView = view
            view.onAttach(
                contextCode = "",
                // Must match the value on `@Page("A2UISurfaceDemo")` declared
                // in :demo/…/A2UISurfaceDemoPager.kt. KSP registers this
                // string into the generated PagerRoute at compile time.
                pageName = PAGE_NAME,
                pageData = emptyMap(),
                size = Size(container.width, container.height),
                assetsPath = null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        renderView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        renderView?.onPause()
    }

    override fun onDestroy() {
        renderView?.onDetach()
        renderView = null
        super.onDestroy()
    }

    // ─────────────────────────────────────────────────────────────────────
    private fun buildTitleBar(): FrameLayout {
        val bar = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#6200EE"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }
        val title = TextView(this).apply {
            text = "[DSL] A2UI in Kuikly Page"
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, 0, 0)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        bar.addView(title)
        return bar
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    companion object {
        /** Must equal the `name` value on `@Page` in :demo. */
        private const val PAGE_NAME = "A2UISurfaceDemo"
    }
}

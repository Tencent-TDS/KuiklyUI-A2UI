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
 * A2UIRpcDemoActivity — hosts the v1.0 bidirectional-RPC demo page (see
 * `:demo`'s `A2UIRpcDemoPager`). The Kuikly page itself implements:
 *
 *  - an inbound-RPC handler (`callRendererFunction("openPage", ...)` →
 *    local handler → `rendererFunctionResponse`)
 *  - an outbound-RPC trigger (`callAgentFunction("fetchUser", ...)` →
 *    mock agent reply → `agentFunctionResponse` → `onComplete`).
 *
 * This Activity only performs the usual Kuikly-page plumbing (KuiklyBaseView
 * + onAttach). No Activity-side RPC wiring is required because the demo
 * uses `A2UIBridgeManager`'s common APIs directly from the page.
 */
class A2UIRpcDemoActivity : AppCompatActivity() {

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

        container.post {
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

    private fun buildTitleBar(): FrameLayout {
        val bar = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#1A73E8"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }
        val title = TextView(this).apply {
            text = "[DSL] A2UI v1.0 RPC Demo"
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
        /** Must equal the `name` value on `@Page` in `:demo`. */
        private const val PAGE_NAME = "A2UIRpcDemo"
    }
}

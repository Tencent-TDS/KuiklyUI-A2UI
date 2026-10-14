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
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UIKuiklyBaseViewSupport
import com.tencent.kuikly.core.render.android.expand.KuiklyBaseView

/**
 * Compose Page Demo Activity.
 *
 * Hosts the Kuikly Compose page `ComposeSampleWithA2UI` (see
 * `:demo/…/ComposeSampleWithA2UIPager`) inside a plain Android
 * Activity via `KuiklyBaseView.onAttach(pageName = ...)`. The Kuikly
 * Compose page in turn embeds an `A2UISurfaceDSLView` through
 * `MakeKuiklyComposeNode`, giving us a proper *Compose Page → A2UI*
 * integration example.
 *
 * This is the only screen in the KuiklyPage app that goes through
 * `:compose`.
 */
class ComposePageDemoActivity : AppCompatActivity() {

    companion object {
        /** Must match the `@Page` name on `ComposeSampleWithA2UIPager`. */
        private const val PAGE_NAME = "ComposeSampleWithA2UI"
    }

    private var renderView: KuiklyBaseView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val density = resources.displayMetrics.density
        fun dp(value: Int) = (value * density).toInt()

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Native header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#009688"))
            setPadding(dp(24), dp(48), dp(24), dp(16))
        }
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val backArrow = TextView(this).apply {
            text = "←"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            setPadding(0, 0, dp(12), 0)
            setOnClickListener { finish() }
        }
        val headerTitle = TextView(this).apply {
            text = "Compose 页面接入示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "宿主：Kuikly Compose（:compose 模块 · ComposeContainer + setContent）"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#B2DFDB"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

        // Kuikly page container
        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        rootLayout.addView(container)

        container.post { attachKuiklyPage(container) }
        setContentView(rootLayout)
    }

    private fun attachKuiklyPage(container: ViewGroup) {
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
}

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

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * A2UI KuiklyPage Demo main Activity.
 *
 * All examples here host A2UI through the *Kuikly Page* integration path:
 * a hand-written Kuikly `Pager` (in `:demo`) embeds `A2UISurface` as
 * a child of its own Flex tree, and each Activity below spins up a
 * `KuiklyBaseView.onAttach(pageName = …)` to load the corresponding page.
 *
 * Group 1 — Kuikly 页面接入示例：
 *   卡片场景接入示例 / 自定义函数示例 / 自定义组件示例 / 自定义 Icon 图片 / Compose 页面接入示例
 * Group 2 — 组件展示：v0.9 协议的 18 个语义化组件
 */
class A2UIDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Title bar
        val titleBar = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#3F51B5"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }
        val titleText = TextView(this).apply {
            text = "A2UI · Kuikly Page"
            setTextColor(Color.WHITE)
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, 0, 0)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        titleBar.addView(titleText)
        rootLayout.addView(titleBar)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(16))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // ===== Group 1: Kuikly 页面接入示例 =====
        contentLayout.addView(createSectionHeader("Kuikly 页面接入示例"))

        contentLayout.addView(createDemoEntry(
            title = "卡片场景接入示例",
            description = "在 Kuikly DSL 页面里嵌入 A2UISurface：单 Pager、单 FlexNode 树，点击按钮追加 A2UI 卡片",
            iconText = "🧬",
            color = "#3F51B5"
        ) {
            startActivity(Intent(this, A2UISurfaceDSLDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "自定义函数示例",
            description = "注册 string/boolean/number/void 返回类型的自定义函数，验证表达式求值、check 条件、action 触发",
            iconText = "⚡",
            color = "#00897B"
        ) {
            startActivity(Intent(this, CustomFunctionDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "自定义组件示例",
            description = "注册 A2UIWeb (WebView) 和 MyA2UIView (简单视图) 自定义组件，覆盖 setProp/Shadow 完整链路",
            iconText = "🧩",
            color = "#6200EE"
        ) {
            startActivity(Intent(this, CustomComponentDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "自定义 Icon 图片",
            description = "通过 A2UIIconRegistry 注册业务侧 Icon 图片，替换协议内建的 emoji / unicode 字形",
            iconText = "🖼️",
            color = "#D81B60"
        ) {
            startActivity(Intent(this, CustomIconDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "Compose 页面接入示例",
            description = "在 Kuikly Compose 页面（:compose 模块）里承载 A2UI，展示 Compose 世界的 A2UI 集成",
            iconText = "🧵",
            color = "#009688"
        ) {
            startActivity(Intent(this, ComposePageDemoActivity::class.java))
        })

        // ===== Group 2: 组件展示 =====
        contentLayout.addView(createSectionHeader("组件展示"))

        contentLayout.addView(createDemoEntry(
            title = "组件展示",
            description = "展示 A2UI v0.9 协议语义化组件：Text、Row、Column、Card 等 18 个组件",
            iconText = "🔧",
            color = "#F39C12"
        ) {
            startActivity(Intent(this, ComponentDemoActivity::class.java))
        })

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        setContentView(rootLayout)
    }

    private fun createSectionHeader(title: String): View {
        return TextView(this).apply {
            text = title
            setTextColor(Color.parseColor("#555555"))
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dp(16)
            params.bottomMargin = dp(8)
            layoutParams = params
        }
    }

    private fun createDemoEntry(
        title: String,
        description: String,
        iconText: String,
        color: String,
        onClick: () -> Unit
    ): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            val bg = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(12).toFloat()
            }
            background = bg
            elevation = dp(2).toFloat()
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(12)
            layoutParams = params
            setOnClickListener { onClick() }
        }

        val iconView = TextView(this).apply {
            text = iconText
            textSize = 28f
            gravity = Gravity.CENTER
            val iconBg = GradientDrawable().apply {
                setColor(Color.parseColor(color))
                cornerRadius = dp(24).toFloat()
                alpha = 40
            }
            background = iconBg
            val size = dp(48)
            layoutParams = LinearLayout.LayoutParams(size, size)
        }
        card.addView(iconView)

        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val titleView = TextView(this).apply {
            text = title
            setTextColor(Color.parseColor("#333333"))
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val descView = TextView(this).apply {
            text = description
            setTextColor(Color.parseColor("#888888"))
            textSize = 13f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dp(4)
            layoutParams = params
        }
        textLayout.addView(titleView)
        textLayout.addView(descView)
        card.addView(textLayout)

        val arrow = TextView(this).apply {
            text = "›"
            setTextColor(Color.parseColor("#CCCCCC"))
            textSize = 24f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        card.addView(arrow)

        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

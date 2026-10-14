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
 * A2UI Test main Activity — the "kitchen-sink" test harness.
 *
 * Groups exposed here (kept out of the user-facing KuiklyPage/NativePage/Both apps
 * on purpose — those apps only ship polished integration examples):
 *  1. 函数测试 / 数据绑定测试 / 主题测试
 *  2. A2UI v0.9 协议官方示例
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
            setBackgroundColor(Color.parseColor("#6200EE"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }
        val titleText = TextView(this).apply {
            text = "A2UI Test"
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

        // Scrollable content
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

        // ===== Group 1: 函数 / 数据绑定 / 主题测试 =====
        contentLayout.addView(createSectionHeader("函数 / 数据绑定 / 主题测试"))

        contentLayout.addView(createDemoEntry(
            title = "函数测试",
            description = "展示 A2UI v0.9 basic catalog 内建 14 个函数：required / regex / formatDate / pluralize / and / or 等",
            iconText = "ƒ",
            color = "#00B894"
        ) {
            startActivity(Intent(this, FunctionDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "数据绑定测试",
            description = "展示 A2UI v0.9 数据绑定 11 项能力：绝对/相对路径、ChildList 模板、双向绑定、formatString 插值、Action 上下文等",
            iconText = "🔗",
            color = "#0984E3"
        ) {
            startActivity(Intent(this, DataBindingDemoActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "主题测试",
            description = "展示 A2UI v0.9 主题字段能力：createSurface.theme.primaryColor / iconUrl / agentDisplayName、非法值 fallback、默认主题等",
            iconText = "🎨",
            color = "#E91E63"
        ) {
            startActivity(Intent(this, ThemeDemoActivity::class.java))
        })

        // ===== Group 2: A2UI v0.9 协议官方示例 =====
        contentLayout.addView(createSectionHeader("A2UI v0.9 协议官方示例"))

        contentLayout.addView(createDemoEntry(
            title = "A2UI v0.9 协议官方示例",
            description = "展示 A2UI v0.9 协议规范中的 36 个官方示例卡片（启动一次性加载了36个json文件渲染UI卡片，待优化）",
            iconText = "📋",
            color = "#9B59B6"
        ) {
            startActivity(Intent(this, OfficialExampleActivity::class.java))
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

        // Icon circle
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

        // Text content
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

        // Arrow
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

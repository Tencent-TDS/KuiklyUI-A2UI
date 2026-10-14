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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Both Pages Demo main Activity.
 *
 * Shows both A2UI integration paths side by side in the same App so users
 * can compare them without switching APKs. Each entry loads a minimal
 * single-card example:
 *   1. Kuikly 页面接入示例 → `KuiklyPageCardActivity`
 *   2. Native 页面接入示例 → `NativePageCardActivity`
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

        val titleBar = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#7B1FA2"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }
        val titleText = TextView(this).apply {
            text = "A2UI · Both Pages"
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

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        contentLayout.addView(createDemoEntry(
            title = "Kuikly 页面接入示例",
            description = "Kuikly Page（KuiklyBaseView + @Page）承载一张 A2UI v0.9 天气卡片",
            iconText = "🧬",
            color = "#3F51B5"
        ) {
startActivity(Intent(this, KuiklyPageCardActivity::class.java))
        })

        contentLayout.addView(createDemoEntry(
            title = "Native 页面接入示例",
            description = "Native Activity + A2UISurfaceView 直接承载一张 A2UI v0.9 天气卡片",
            iconText = "🧱",
            color = "#455A64"
        ) {
startActivity(Intent(this, NativePageCardActivity::class.java))
        })

        rootLayout.addView(contentLayout)
        setContentView(rootLayout)
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
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(12).toFloat()
            }
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
            background = GradientDrawable().apply {
                setColor(Color.parseColor(color))
                cornerRadius = dp(24).toFloat()
                alpha = 40
            }
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
        }
        val descView = TextView(this).apply {
            text = description
            setTextColor(Color.parseColor("#888888"))
            textSize = 13f
        }
        textLayout.addView(titleView)
        textLayout.addView(descView)
        card.addView(textLayout)

        val arrow = TextView(this).apply {
            text = "›"
            setTextColor(Color.parseColor("#CCCCCC"))
            textSize = 24f
        }
        card.addView(arrow)

        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

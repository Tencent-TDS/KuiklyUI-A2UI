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
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Theme Test Activity.
 * Displays the A2UI v0.9 protocol theme capabilities defined in `createSurface.theme`,
 * grouped by category. Selecting a capability opens [A2UIComponentTestActivity]
 * with the corresponding asset directory under
 * `assets/a2ui_theme_tests/<capabilityDir>/`.
 *
 * The 8 capability groups (mirroring the v0.9 protocol theme spec + regression edges):
 *  1. primaryColor basic染色                     -> 01_primary_color_basic
 *  2. Agent identity: iconUrl / agentDisplayName -> 02_agent_identity
 *  3. Full theme + additionalProperties extras   -> 03_theme_all_fields
 *  4. Invalid primaryColor fallback              -> 04_invalid_color_fallback
 *  5. Default theme (createSurface without theme) -> 05_default_no_theme
 *  6. Extreme colors (black / white primaryColor) -> 06_extreme_colors
 *  7. Button variant isolation (regression)      -> 07_button_variant_isolation
 *  8. Agent identity edge cases                  -> 08_agent_identity_edge
 */
class ThemeDemoActivity : AppCompatActivity() {

    companion object {
        private const val ROOT_DIR = "a2ui_theme_tests"
        private const val TITLE_SUFFIX = "主题"
    }

    /** Single theme capability entry. */
    private data class ThemeEntry(
        val dir: String,
        val name: String,
        val desc: String,
        val color: String
    )

    /** A group of related capability entries. */
    private data class ThemeGroup(val title: String, val entries: List<ThemeEntry>)

    private val groups: List<ThemeGroup> = listOf(
        ThemeGroup("主题色渲染（primaryColor）", listOf(
            ThemeEntry(
                "01_primary_color_basic",
                "primaryColor 一次性染色多组件",
                "Button primary / Slider progressColor / Tabs 选中态 / CheckBox / ChoicePicker 等均应统一为主题色",
                "#E91E63"
            )
        )),
        ThemeGroup("身份归因（Identity & Attribution）", listOf(
            ThemeEntry(
                "02_agent_identity",
                "iconUrl + agentDisplayName",
                "theme.iconUrl (URI) 与 agentDisplayName (String) 用于向用户展示 agent 身份",
                "#6C5CE7"
            )
        )),
        ThemeGroup("完整主题（All Fields）", listOf(
            ThemeEntry(
                "03_theme_all_fields",
                "三字段全填 + 自定义扩展",
                "同时携带 primaryColor / iconUrl / agentDisplayName，并附加规范未定义的自定义键（additionalProperties=true 允许扩展）",
                "#F39C12"
            )
        )),
        ThemeGroup("非法值 fallback（Invalid Fallback）", listOf(
            ThemeEntry(
                "04_invalid_color_fallback",
                "primaryColor 非法值应回退默认",
                "非 hex / 3 位 / 8 位 / 缺 # / 非 hex 字符 / 空串 均应回退 #00BFFF，大写 6 位 hex 作为正向对照",
                "#E74C3C"
            )
        )),
        ThemeGroup("默认主题（Default）", listOf(
            ThemeEntry(
                "05_default_no_theme",
                "createSurface 不带 theme",
                "theme 属可选。未携带时，primaryColor 走协议默认值 #00BFFF（浅蓝）",
                "#00BFFF"
            )
        )),
        ThemeGroup("极端色（Extreme Colors）", listOf(
            ThemeEntry(
                "06_extreme_colors",
                "primaryColor 极端色可读性",
                "纯黑 / 纯白 primaryColor 目视验证，暴露主题色对比度不当时的观感（不代表实现缺陷）",
                "#000000"
            )
        )),
        ThemeGroup("Button variant 隔离（Regression）", listOf(
            ThemeEntry(
                "07_button_variant_isolation",
                "仅 primary variant 应被染色",
                "同一主题下 primary / default / borderless 并列，验证仅 primary 背景应用主题色，其他 variant 不被误染",
                "#E91E63"
            )
        )),
        ThemeGroup("Agent 身份边界（Identity Edge）", listOf(
            ThemeEntry(
                "08_agent_identity_edge",
                "超长名称 / emoji / data URI",
                "agentDisplayName 超长中英混排 + emoji，iconUrl 使用 data:image/png;base64,... 内联图片",
                "#009688"
            )
        ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildLayout())
    }

    private fun buildLayout(): View {
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

        // Header (magenta bar to distinguish from DataBindingDemo's blue)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#E91E63"))
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
            text = "主题测试"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI v0.9 协议 createSurface.theme 主题字段能力"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#FFE0EC"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

        // Scrollable list
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(16))
        }

        for (group in groups) {
            listContainer.addView(createSectionHeader(group.title, ::dp))
            for (entry in group.entries) {
                listContainer.addView(createThemeCard(entry, ::dp))
            }
        }

        scrollView.addView(listContainer)
        rootLayout.addView(scrollView)
        return rootLayout
    }

    private fun createSectionHeader(title: String, dp: (Int) -> Int): View {
        return TextView(this).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.parseColor("#555555"))
            typeface = Typeface.DEFAULT_BOLD
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dp(16)
            params.bottomMargin = dp(6)
            layoutParams = params
        }
    }

    private fun createThemeCard(entry: ThemeEntry, dp: (Int) -> Int): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(8).toFloat()
            }
            elevation = dp(1).toFloat()
            setPadding(dp(12), dp(14), dp(12), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
            }
            isClickable = true
            isFocusable = true
            setOnClickListener { openThemeTest(entry) }
        }
        val indicator = View(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.parseColor(entry.color))
                cornerRadius = dp(3).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(4), dp(40)).apply {
                marginEnd = dp(12)
            }
        }
        card.addView(indicator)
        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val nameText = TextView(this).apply {
            text = entry.name
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTextColor(Color.parseColor("#333333"))
            typeface = Typeface.DEFAULT_BOLD
        }
        val descText = TextView(this).apply {
            text = entry.desc
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, dp(3), 0, 0)
        }
        textColumn.addView(nameText)
        textColumn.addView(descText)
        card.addView(textColumn)
        val arrow = TextView(this).apply {
            text = "›"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(dp(8), 0, 0, 0)
        }
        card.addView(arrow)
        return card
    }

    private fun openThemeTest(entry: ThemeEntry) {
        val available = try {
            assets.list("$ROOT_DIR/${entry.dir}")?.any { it.endsWith(".json") } == true
        } catch (e: Exception) {
            false
        }
        if (!available) {
            Toast.makeText(this, "${entry.name} 暂未提供测试用例", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, entry.dir)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, entry.name)
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, ROOT_DIR)
            putExtra(A2UIComponentTestActivity.EXTRA_TITLE_SUFFIX, TITLE_SUFFIX)
        }
        startActivity(intent)
    }
}

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
 * Component Demo Activity.
 * Displays a list of Kuikly native components supported by A2UI protocol.
 * This page is built entirely with native Android Views.
 */
class ComponentDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildNativeLayout())
    }

    private fun buildNativeLayout(): View {
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

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#6C5CE7"))
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
            text = "组件展示"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI v0.9 标准协议支持的 18 个组件"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#DDDDFF"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

        // Scrollable component list
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

        // Component data: (name, description, color)
        val components = listOf(
            Triple("Text", "文本组件 - 支持文本样式、variant、数据绑定", "#E74C3C"),
            Triple("Image", "图片组件 - 支持网络图片、fit模式", "#27AE60"),
            Triple("Icon", "图标组件 - 支持标准图标名称和SVG路径", "#9B59B6"),
            Triple("Video", "视频组件 - 视频播放能力", "#16A085"),
            Triple("AudioPlayer", "音频播放组件 - 音频播放能力", "#2C3E50"),
            Triple("Row", "水平布局 - 子组件水平排列，支持justify/align", "#4A90D9"),
            Triple("Column", "垂直布局 - 子组件垂直排列，支持justify/align", "#3498DB"),
            Triple("List", "列表组件 - 可滚动列表，支持水平/垂直方向", "#F39C12"),
            Triple("Card", "卡片组件 - 带圆角和阴影的容器", "#E67E22"),
            Triple("Tabs", "标签页组件 - 多标签切换", "#34495E"),
            Triple("Modal", "弹窗组件 - 模态弹窗展示", "#2C3E50"),
            Triple("Divider", "分割线组件 - 水平/垂直分割线", "#95A5A6"),
            Triple("Button", "按钮组件 - 可点击，支持action", "#6C5CE7"),
            Triple("TextField", "输入框组件 - 文本输入，支持label/value", "#1ABC9C"),
            Triple("CheckBox", "复选框组件 - 布尔值选择", "#8E44AD"),
            Triple("ChoicePicker", "选择器组件 - 单选/多选列表", "#D35400"),
            Triple("Slider", "滑块组件 - 数值范围选择", "#27AE60"),
            Triple("DateTimeInput", "日期时间组件 - 日期/时间选择", "#C0392B")
        )

        for ((name, desc, color) in components) {
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
                    topMargin = dp(8)
                }
                isClickable = true
                isFocusable = true
                setOnClickListener { openComponentTest(name) }
            }

            // Color indicator bar
            val indicator = View(this).apply {
                background = GradientDrawable().apply {
                    setColor(Color.parseColor(color))
                    cornerRadius = dp(3).toFloat()
                }
                layoutParams = LinearLayout.LayoutParams(dp(4), dp(36)).apply {
                    marginEnd = dp(12)
                }
            }
            card.addView(indicator)

            // Text column
            val textColumn = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val nameText = TextView(this).apply {
                text = name
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColor(Color.parseColor("#333333"))
                typeface = Typeface.DEFAULT_BOLD
            }
            val descText = TextView(this).apply {
                text = desc
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, dp(3), 0, 0)
            }
            textColumn.addView(nameText)
            textColumn.addView(descText)
            card.addView(textColumn)

            // Arrow indicator
            val arrow = TextView(this).apply {
                text = "›"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTextColor(Color.parseColor("#CCCCCC"))
                setPadding(dp(8), 0, 0, 0)
            }
            card.addView(arrow)

            listContainer.addView(card)
        }

        scrollView.addView(listContainer)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    /**
     * Map a component display name (e.g. "Text", "AudioPlayer") to its asset directory
     * under `assets/a2ui_components/`. The mapping is the lower-cased component name.
     */
    private fun componentDirOf(componentName: String): String = componentName.lowercase()

    private fun openComponentTest(componentName: String) {
        val dir = componentDirOf(componentName)
        val available = try {
            assets.list("a2ui_components/$dir")
                ?.any { it.endsWith(".json") } == true
        } catch (e: Exception) {
            false
        }
        if (!available) {
            Toast.makeText(
                this,
                "$componentName 暂未提供测试用例",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val intent = Intent(this, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, dir)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, componentName)
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, "a2ui_components")
        }
        startActivity(intent)
    }
}

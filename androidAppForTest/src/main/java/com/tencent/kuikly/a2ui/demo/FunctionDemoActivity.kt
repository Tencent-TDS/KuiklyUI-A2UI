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
 * Function Demo Activity.
 * Displays the 14 built-in A2UI v0.9 catalog functions grouped by category.
 * Selecting a function opens [A2UIComponentTestActivity] with the function's
 * asset directory under `assets/a2ui_function_tests/<funcName>/`.
 */
class FunctionDemoActivity : AppCompatActivity() {

    companion object {
        private const val ROOT_DIR = "a2ui_function_tests"
        private const val TITLE_SUFFIX = "函数测试"
    }

    /** Category → list of (funcName, description, color). */
    private data class FunctionEntry(val name: String, val desc: String, val color: String)
    private data class FunctionGroup(val title: String, val entries: List<FunctionEntry>)

    private val groups: List<FunctionGroup> = listOf(
        FunctionGroup("校验函数（Validation, returns Boolean）", listOf(
            FunctionEntry("required", "非空校验：值不为 null / undefined / 空串", "#E74C3C"),
            FunctionEntry("regex", "正则匹配：value 必须匹配指定 pattern", "#C0392B"),
            FunctionEntry("length", "长度约束：字符串长度落在 [min,max]", "#D35400"),
            FunctionEntry("numeric", "数值范围：数字落在 [min,max]", "#E67E22"),
            FunctionEntry("email", "邮箱格式：value 必须是合法 email", "#F39C12")
        )),
        FunctionGroup("格式化函数（Formatting, returns String）", listOf(
            FunctionEntry("formatString", "字符串插值：${'$'}{path} / ${'$'}{fn(...)}", "#4A90D9"),
            FunctionEntry("formatNumber", "数值格式化：小数位、千位分隔", "#3498DB"),
            FunctionEntry("formatCurrency", "货币格式化：ISO4217 currency", "#2C3E50"),
            FunctionEntry("formatDate", "日期格式化：Unicode TR35 pattern", "#16A085"),
            FunctionEntry("pluralize", "复数选择：CLDR zero/one/two/few/many/other", "#27AE60")
        )),
        FunctionGroup("副作用函数（Side-effect, returns Void）", listOf(
            FunctionEntry("openUrl", "打开外链：仅用于 Action.functionCall", "#8E44AD")
        )),
        FunctionGroup("逻辑函数（Logic, returns Boolean）", listOf(
            FunctionEntry("and", "逻辑与：所有子条件全为 true", "#6C5CE7"),
            FunctionEntry("or", "逻辑或：至少一个子条件为 true", "#9B59B6"),
            FunctionEntry("not", "逻辑非：对 boolean 取反", "#34495E")
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

        // Header (purple bar matching component demo style)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#00B894"))
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
            text = "函数测试"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI v0.9 basic catalog 内建 14 个函数"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#DDFFF5"))
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
                listContainer.addView(createFunctionCard(entry, ::dp))
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

    private fun createFunctionCard(entry: FunctionEntry, dp: (Int) -> Int): View {
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
            setOnClickListener { openFunctionTest(entry.name) }
        }
        val indicator = View(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.parseColor(entry.color))
                cornerRadius = dp(3).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(4), dp(36)).apply {
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

    private fun openFunctionTest(funcName: String) {
        val available = try {
            assets.list("$ROOT_DIR/$funcName")?.any { it.endsWith(".json") } == true
        } catch (e: Exception) {
            false
        }
        if (!available) {
            Toast.makeText(this, "$funcName 暂未提供测试用例", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, funcName)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, funcName)
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, ROOT_DIR)
            putExtra(A2UIComponentTestActivity.EXTRA_TITLE_SUFFIX, TITLE_SUFFIX)
        }
        startActivity(intent)
    }
}

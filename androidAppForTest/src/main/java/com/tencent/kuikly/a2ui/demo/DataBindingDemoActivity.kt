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
 * Data Binding Test Activity.
 * Displays the data-binding capabilities defined by the A2UI v0.9 protocol,
 * grouped by category. Selecting a capability opens [A2UIComponentTestActivity]
 * with the corresponding asset directory under
 * `assets/a2ui_databinding_tests/<capabilityDir>/`.
 *
 * The 12 capabilities (mirroring the v0.9 protocol analysis):
 *  1. Absolute path binding                       -> 01_absolute_path
 *  2. Data Model upsert / delete / replace        -> 02_data_model_upsert
 *  3. ChildList template (list rendering)         -> 03_child_list_template
 *  4. Relative path in child scope                -> 04_relative_path
 *  5. Mixed absolute + relative in template scope -> 05_mixed_scope
 *  6. Two-way binding (inputs)                    -> 06_two_way_binding
 *  7. FunctionCall as DynamicValue (nested)       -> 07_function_binding
 *  8. String interpolation (formatString)         -> 08_string_interpolation
 *  9. Check binding (checks + logic ops)          -> 09_check_binding
 * 10. Action context binding                      -> 10_action_context
 * 11. Accessibility attributes binding            -> 11_accessibility
 * 12. Progressive rendering (undefined fallback)  -> 12_progressive_rendering
 */
class DataBindingDemoActivity : AppCompatActivity() {

    companion object {
        private const val ROOT_DIR = "a2ui_databinding_tests"
        private const val TITLE_SUFFIX = "数据绑定"
    }

    /** Single data-binding capability entry. */
    private data class BindingEntry(
        val dir: String,       // asset sub-directory
        val name: String,      // display name in the card
        val desc: String,      // one-line description
        val color: String      // left color bar
    )

    /** A group of related capability entries. */
    private data class BindingGroup(val title: String, val entries: List<BindingEntry>)

    private val groups: List<BindingGroup> = listOf(
        BindingGroup("路径绑定（Path Binding）", listOf(
            BindingEntry(
                "01_absolute_path",
                "绝对路径",
                "{path:\"/x\"} 绑定 DynamicString / Number / Boolean 等各种类型",
                "#0984E3"
            ),
            BindingEntry(
                "02_data_model_upsert",
                "Data Model 增量同步",
                "updateDataModel 的 upsert / 删除 / 整体替换语义",
                "#2980B9"
            )
        )),
        BindingGroup("列表模板（ChildList Template）", listOf(
            BindingEntry(
                "03_child_list_template",
                "ChildList 模板遍历",
                "children:{componentId,path} 基于数组渲染动态列表",
                "#8E44AD"
            ),
            BindingEntry(
                "04_relative_path",
                "模板内相对路径",
                "子作用域内使用 \"fieldName\" 绑定当前迭代项",
                "#9B59B6"
            ),
            BindingEntry(
                "05_mixed_scope",
                "混合作用域",
                "模板子树内绝对路径与相对路径共存",
                "#6C5CE7"
            )
        )),
        BindingGroup("双向绑定（Two-Way Binding）", listOf(
            BindingEntry(
                "06_two_way_binding",
                "输入组件回写 + 反应式联动",
                "TextField / Slider / CheckBox 输入自动写回 Data Model 并触发同路径重渲染",
                "#00B894"
            )
        )),
        BindingGroup("函数与插值（Function & Interpolation）", listOf(
            BindingEntry(
                "07_function_binding",
                "FunctionCall 作为 Dynamic*",
                "以 FunctionCall 作为属性值（可嵌套 args）",
                "#16A085"
            ),
            BindingEntry(
                "08_string_interpolation",
                "formatString 字符串插值",
                "\${path} / \${func(args...)} / 转义 / 类型转换",
                "#27AE60"
            )
        )),
        BindingGroup("校验绑定（Check Binding）", listOf(
            BindingEntry(
                "09_check_binding",
                "checks 表达式",
                "条件基于路径与逻辑函数，未通过时 Button 自动 disable",
                "#E74C3C"
            )
        )),
        BindingGroup("动作与无障碍（Action & A11y）", listOf(
            BindingEntry(
                "10_action_context",
                "Action.context 绑定",
                "点击事件把 Data Model 值随 context 一起下发",
                "#E67E22"
            ),
            BindingEntry(
                "11_accessibility",
                "无障碍字段动态绑定",
                "accessibility.label / description 支持 DynamicString",
                "#F39C12"
            )
        )),
        BindingGroup("渐进渲染（Progressive Rendering）", listOf(
            BindingEntry(
                "12_progressive_rendering",
                "缺数据 / 数据后到达降级",
                "path 未定义时 UI 优雅降级，updateDataModel 到达后自动填充重渲染",
                "#7F8C8D"
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

        // Header (blue bar to distinguish from FunctionDemo's green)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0984E3"))
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
            text = "数据绑定测试"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI v0.9 协议 12 项数据绑定能力"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#DDECFF"))
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
                listContainer.addView(createBindingCard(entry, ::dp))
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

    private fun createBindingCard(entry: BindingEntry, dp: (Int) -> Int): View {
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
            setOnClickListener { openBindingTest(entry) }
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

    private fun openBindingTest(entry: BindingEntry) {
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

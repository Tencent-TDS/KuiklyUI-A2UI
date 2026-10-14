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
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView

/**
 * Custom Function Demo Activity.
 *
 * Demonstrates the A2UI custom function extension capability:
 *  1. Registers several custom functions globally via [A2UISurfaceView.registerCustomFunction]
 *  2. Provides navigation to test cases grouped by return type
 *
 * The registered functions are:
 *  - getGreeting(name) → String : returns "Hello, {name}!"
 *  - formatPhone(phone) → String : formats "13800001234" → "138-0000-1234"
 *  - isVip(level) → Boolean : returns level >= 3
 *  - calculateDiscount(price, rate) → Number : returns price * rate
 *  - logEvent(event, data) → Void : logs to console
 *  - buildSummary(name, count, status) → String : builds a summary sentence
 */
class CustomFunctionDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CustomFunctionDemo"
        private const val ROOT_DIR = "a2ui_custom_functions"
        private const val TITLE_SUFFIX = "自定义函数"
    }

    private data class FunctionEntry(
        val dir: String,
        val name: String,
        val desc: String,
        val color: String
    )

    private data class FunctionGroup(val title: String, val entries: List<FunctionEntry>)

    private val groups: List<FunctionGroup> = listOf(
        FunctionGroup("返回 String 的自定义函数", listOf(
            FunctionEntry(
                "01_string_return",
                "getGreeting / formatPhone → String",
                "自定义函数返回字符串，绑定到 Text.text 属性。验证表达式求值路径的同步调用和返回值传递。",
                "#6C5CE7"
            )
        )),
        FunctionGroup("返回 Boolean 的自定义函数", listOf(
            FunctionEntry(
                "02_boolean_return",
                "isVip(level) → Boolean",
                "自定义函数返回布尔值，用于 check 条件控制组件显隐。验证 evaluateCheckFunction 兜底路径。",
                "#00B894"
            )
        )),
        FunctionGroup("返回 Number 的自定义函数", listOf(
            FunctionEntry(
                "03_number_return",
                "calculateDiscount(price, rate) → Number",
                "自定义函数返回数值，绑定到 Text.text。验证数值类型的同步返回和 coercion。",
                "#F39C12"
            )
        )),
        FunctionGroup("Void 函数作为 Action", listOf(
            FunctionEntry(
                "04_void_action",
                "logEvent(event, data) → Void",
                "自定义 void 函数作为 Button 的 action.functionCall。点击按钮触发，验证 executeFunctionCallIfNeeded 拦截。",
                "#E74C3C"
            )
        )),
        FunctionGroup("多参数 + 数据绑定", listOf(
            FunctionEntry(
                "05_args_passing",
                "buildSummary(name, count, status) → String",
                "多参数自定义函数，参数来自 dataModel 深层路径绑定。验证 resolveDynamicArg 对自定义函数参数的处理。",
                "#0984E3"
            )
        ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ===== Register custom functions globally =====
        registerCustomFunctions()

        setContentView(buildLayout())
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister to keep the demo clean (optional in production)
        unregisterCustomFunctions()
    }

    /**
     * Register all demo custom functions.
     * This demonstrates the public API: [A2UISurfaceView.registerCustomFunction].
     */
    private fun registerCustomFunctions() {
        // 1. getGreeting(name) → String
        A2UISurfaceView.registerCustomFunction(
            name = "getGreeting",
            returnType = A2UISurfaceView.RETURN_TYPE_STRING,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val name = args.optString("name", "World")
            "Hello, $name!"
        }

        // 2. formatPhone(phone) → String
        A2UISurfaceView.registerCustomFunction(
            name = "formatPhone",
            returnType = A2UISurfaceView.RETURN_TYPE_STRING,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val phone = args.optString("phone", "")
            if (phone.length == 11) {
                "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}"
            } else {
                phone
            }
        }

        // 3. isVip(level) → Boolean
        A2UISurfaceView.registerCustomFunction(
            name = "isVip",
            returnType = A2UISurfaceView.RETURN_TYPE_BOOLEAN,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val level = args.optInt("level", 0)
            level >= 3
        }

        // 4. calculateDiscount(price, rate) → Number
        A2UISurfaceView.registerCustomFunction(
            name = "calculateDiscount",
            returnType = A2UISurfaceView.RETURN_TYPE_NUMBER,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val price = args.optDouble("price", 0.0)
            val rate = args.optDouble("rate", 1.0)
            Math.round(price * rate * 100) / 100.0
        }

        // 5. logEvent(event, data) → Void (sync)
        A2UISurfaceView.registerCustomFunction(
            name = "logEvent",
            returnType = A2UISurfaceView.RETURN_TYPE_VOID,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val event = args.optString("event", "")
            val data = args.optString("data", "")
            Log.i(TAG, "📊 logEvent: event=$event, data=$data")
            Toast.makeText(this, "logEvent: $event ($data)", Toast.LENGTH_SHORT).show()
            null
        }

        // 6. buildSummary(name, count, status) → String
        A2UISurfaceView.registerCustomFunction(
            name = "buildSummary",
            returnType = A2UISurfaceView.RETURN_TYPE_STRING,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val name = args.optString("name", "Unknown")
            val count = args.optInt("count", 0)
            val status = args.optString("status", "unknown")
            "项目「$name」当前状态: $status，共 $count 个任务"
        }

        Log.i(TAG, "✅ All custom functions registered")
    }

    private fun unregisterCustomFunctions() {
        A2UISurfaceView.unregisterCustomFunction("getGreeting")
        A2UISurfaceView.unregisterCustomFunction("formatPhone")
        A2UISurfaceView.unregisterCustomFunction("isVip")
        A2UISurfaceView.unregisterCustomFunction("calculateDiscount")
        A2UISurfaceView.unregisterCustomFunction("logEvent")
        A2UISurfaceView.unregisterCustomFunction("buildSummary")
        Log.i(TAG, "🗑️ All custom functions unregistered")
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

        // Header (teal bar)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#00897B"))
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
            text = "自定义函数示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI 自定义函数扩展能力：注册 → 调用 → 返回值"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#B2DFDB"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)

        // Registration info banner
        val infoBanner = TextView(this).apply {
            text = "✅ 已注册 6 个自定义函数: getGreeting, formatPhone, isVip, calculateDiscount, logEvent, buildSummary"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#004D40"))
            setBackgroundColor(Color.parseColor("#E0F2F1"))
            setPadding(dp(16), dp(8), dp(16), dp(8))
        }
        header.addView(infoBanner)
        rootLayout.addView(header)

        // Scrollable list
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(16))
        }

        for (group in groups) {
            listContainer.addView(createSectionHeader(group.title, ::dp))
            for (entry in group.entries) {
                listContainer.addView(createEntryCard(entry, ::dp))
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

    private fun createEntryCard(entry: FunctionEntry, dp: (Int) -> Int): View {
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
            ).apply { topMargin = dp(6) }
            isClickable = true
            isFocusable = true
            setOnClickListener { openTest(entry) }
        }
        val indicator = View(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.parseColor(entry.color))
                cornerRadius = dp(3).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(4), dp(40)).apply { marginEnd = dp(12) }
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

    private fun openTest(entry: FunctionEntry) {
        val available = try {
            assets.list("$ROOT_DIR/${entry.dir}")?.any { it.endsWith(".json") } == true
        } catch (e: Exception) { false }
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

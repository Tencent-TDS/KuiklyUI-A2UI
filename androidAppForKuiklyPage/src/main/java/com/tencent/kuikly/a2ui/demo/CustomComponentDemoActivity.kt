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

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderShadowExport
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport

/**
 * Custom Component Demo Activity.
 *
 * Demonstrates the A2UI custom component extension capability:
 *  1. Registers two custom render views globally via [A2UISurfaceView.registerCustomRenderView]
 *  2. Provides navigation to test cases for each custom component
 *
 * The registered custom components are:
 *  - A2UIWeb: A WebView component that supports a "source" property to load URLs
 *  - MyA2UIView: A simple custom view that supports "text" and "textColor" properties
 */
class CustomComponentDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CustomComponentDemo"
        private const val ROOT_DIR = "a2ui_custom_components"
        private const val TITLE_SUFFIX = "自定义组件"
    }

    private data class ComponentEntry(
        val dir: String,
        val name: String,
        val desc: String,
        val color: String
    )

    private data class ComponentGroup(val title: String, val entries: List<ComponentEntry>)

    private val groups: List<ComponentGroup> = listOf(
        ComponentGroup("WebView 自定义组件", listOf(
            ComponentEntry(
                "01_web_view",
                "A2UIWeb → WebView",
                "自定义 WebView 组件，支持 source 属性加载网页。演示通过 registerCustomRenderView 注册自定义组件。",
                "#E91E63"
            )
        )),
        ComponentGroup("简单自定义组件", listOf(
            ComponentEntry(
                "02_my_view",
                "MyA2UIView → TextView",
                "最简单的自定义组件，支持 text 和 textColor 属性。演示自定义属性处理。",
                "#6200EE"
            )
        ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register custom components globally
        registerCustomComponents()

        setContentView(buildLayout())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterCustomComponents()
    }

    /**
     * Register all demo custom components.
     * This demonstrates the public API: [A2UISurfaceView.registerCustomRenderView].
     */
    private fun registerCustomComponents() {
        // 1. A2UIWeb: A WebView component with "source" property
        //    Shadow reports intrinsic height = 240dp so the flex engine can size the card.
        A2UISurfaceView.registerCustomRenderView(
            viewName = "A2UIWeb",
            creator = { context -> A2UIWebView(context) },
            shadowCreator = { A2UIWebShadow() }
        )

        // 2. MyA2UIView: A simple custom view with "text" and "textColor" properties
        //    Shadow reports intrinsic height = 200dp.
        A2UISurfaceView.registerCustomRenderView(
            viewName = "MyA2UIView",
            creator = { context -> MyA2UIView(context) },
            shadowCreator = { MyA2UIViewShadow() }
        )

        Log.i(TAG, "✅ All custom components registered: A2UIWeb, MyA2UIView")
    }

    private fun unregisterCustomComponents() {
        A2UISurfaceView.unregisterCustomRenderView("A2UIWeb")
        A2UISurfaceView.unregisterCustomRenderView("MyA2UIView")
        Log.i(TAG, "🗑️ All custom components unregistered")
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

        // Header (purple bar)
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#6200EE"))
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
            text = "自定义组件示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "A2UI 自定义组件扩展能力：注册 → 创建 → 属性设置"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#CE93D8"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)

        // Registration info banner
        val infoBanner = TextView(this).apply {
            text = "✅ 已注册 2 个自定义组件: A2UIWeb (WebView), MyA2UIView (TextView)"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#4A148C"))
            setBackgroundColor(Color.parseColor("#EDE7F6"))
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

    private fun createEntryCard(entry: ComponentEntry, dp: (Int) -> Int): View {
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

    private fun openTest(entry: ComponentEntry) {
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

// ============================================================
// Custom Component Implementations
// ============================================================

/**
 * A2UIWeb: A custom WebView component for A2UI.
 * Supports the "source" property to load a URL.
 *
 * This class implements [IKuiklyRenderViewExport] by extending [FrameLayout]
 * and embedding a [WebView] inside.
 */
class A2UIWebView(context: Context) : FrameLayout(context), IKuiklyRenderViewExport {

    private val webView: WebView = WebView(context).apply {
        webViewClient = WebViewClient()
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        // Prevent the parent Kuikly `Scroller` (or any ancestor ViewGroup) from
        // intercepting vertical drags while the user is interacting with the
        // WebView. Without this, the outer scroller wins the touch fight in
        // `onInterceptTouchEvent` and the WebView never receives ACTION_MOVE,
        // so the embedded page cannot be scrolled inside the card. Passing the
        // request up through every parent (walk via getParent()) is safer than
        // relying on the immediate FrameLayout to forward it, because the
        // Kuikly render tree may inject additional ViewGroups between the
        // WebView and the Scroller.
        setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE ->
                    disallowParentInterception(true)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL ->
                    disallowParentInterception(false)
            }
            // Return false so the WebView's own gesture handling still runs.
            false
        }
    }

    /**
     * Walk up the entire view hierarchy and toggle `disallowIntercept` on
     * every ancestor. The `true` case guarantees no ancestor `ScrollerView`
     * (Kuikly's Android impl of the Kuikly `Scroller`) can intercept the
     * gesture; the `false` case releases control so subsequent gestures
     * outside the WebView are free to scroll the outer list again.
     */
    private fun disallowParentInterception(disallow: Boolean) {
        var p: ViewParent? = parent
        while (p != null) {
            p.requestDisallowInterceptTouchEvent(disallow)
            p = p.parent
        }
    }

    init {
        addView(webView)
    }

    override fun setProp(propKey: String, propValue: Any): Boolean {
        when (propKey) {
            "source" -> {
                val url = propValue.toString()
                if (url.isNotEmpty()) {
                    webView.loadUrl(url)
                }
                return true
            }
        }
        return super.setProp(propKey, propValue)
    }

    override fun resetProp(propKey: String): Boolean {
        when (propKey) {
            "source" -> {
                webView.loadUrl("about:blank")
                return true
            }
        }
        return super.resetProp(propKey)
    }
}

/**
 * MyA2UIView: A simple custom view for A2UI.
 * Supports "text" and "textColor" properties.
 *
 * This class implements [IKuiklyRenderViewExport] by extending [FrameLayout]
 * and embedding a [TextView] inside.
 */
class MyA2UIView(context: Context) : FrameLayout(context), IKuiklyRenderViewExport {

    private val textView: TextView = TextView(context).apply {
        textSize = 16f
        gravity = Gravity.CENTER
        setTextColor(Color.parseColor("#333333"))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        setBackgroundColor(Color.parseColor("#E8F5E9"))
        addView(textView)
    }

    override fun setProp(propKey: String, propValue: Any): Boolean {
        when (propKey) {
            "text" -> {
                textView.text = propValue.toString()
                return true
            }
            "textColor" -> {
                try {
                    textView.setTextColor(Color.parseColor(propValue.toString()))
                } catch (e: Exception) {
                    // Ignore invalid color
                }
                return true
            }
        }
        return super.setProp(propKey, propValue)
    }

    override fun resetProp(propKey: String): Boolean {
        when (propKey) {
            "text" -> {
                textView.text = ""
                return true
            }
            "textColor" -> {
                textView.setTextColor(Color.parseColor("#333333"))
                return true
            }
        }
        return super.resetProp(propKey)
    }
}

// ============================================================
// Shadow Implementations (for intrinsic size measurement)
// ============================================================

/**
 * Shadow for A2UIWebView.
 * Reports intrinsic size: width = constraint width (fill parent), height = 240dp (in px).
 * This allows the Flex engine to correctly size the card without specifying
 * height in the JSON protocol.
 *
 * Note: IKuiklyRenderShadowExport.calculateRenderViewSize receives constraintSize in px
 * and must return the result in px. The framework converts px -> dp automatically.
 */
class A2UIWebShadow : IKuiklyRenderShadowExport {

    override fun setProp(propKey: String, propValue: Any) {
        // No-op: A2UIWebView handles props directly
    }

    override fun calculateRenderViewSize(constraintSize: SizeF): SizeF {
        // 240dp converted to px
        val heightPx = 240f * Resources.getSystem().displayMetrics.density
        return SizeF(constraintSize.width, heightPx)
    }
}

/**
 * Shadow for MyA2UIView.
 * Reports intrinsic size: width = constraint width (fill parent), height = 200dp (in px).
 *
 * Note: IKuiklyRenderShadowExport.calculateRenderViewSize receives constraintSize in px
 * and must return the result in px. The framework converts px -> dp automatically.
 */
class MyA2UIViewShadow : IKuiklyRenderShadowExport {

    override fun setProp(propKey: String, propValue: Any) {
        // No-op: MyA2UIView handles props directly
    }

    override fun calculateRenderViewSize(constraintSize: SizeF): SizeF {
        // 200dp converted to px
        val heightPx = 200f * Resources.getSystem().displayMetrics.density
        return SizeF(constraintSize.width, heightPx)
    }
}

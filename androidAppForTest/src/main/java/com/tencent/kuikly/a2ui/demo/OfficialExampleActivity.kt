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
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener
import org.json.JSONObject

/**
 * Official Example Activity.
 * Displays all 36 A2UI v0.9 official example cards from the specification.
 * Each card renders the JSON content from the examples directory without modification.
 */
class OfficialExampleActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "OfficialExampleActivity"
    }

    private val surfaceViews = mutableListOf<A2UISurfaceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw edge-to-edge so the activity content frame extends under the system bars.
        // KRModalView attaches to this content frame via addContentView(MATCH_PARENT), so
        // without this the modal scrim cannot reach under the navigation bar and leaves a
        // blank strip at the bottom. Mirrors KuiklyRenderActivity.setupImmersiveMode().
        setupEdgeToEdge()
        setContentView(buildLayout())
    }

    private fun setupEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor =
            if (Build.VERSION.SDK_INT >= 26) Color.TRANSPARENT else 0x66000000
        if (Build.VERSION.SDK_INT >= 28) {
            val attrs = window.attributes
            attrs.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attrs
        }
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

        // Title bar
        val titleBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(48), dp(16), dp(12))
        }
        val backArrow = TextView(this).apply {
            text = "←"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.parseColor("#333333"))
            setOnClickListener { finish() }
            setPadding(0, 0, dp(8), 0)
        }
        val titleText = TextView(this).apply {
            text = "A2UI v0.9 协议官方示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#333333"))
            typeface = Typeface.DEFAULT_BOLD
        }
        titleBar.addView(backArrow)
        titleBar.addView(titleText)
        rootLayout.addView(titleBar)

        // Subtitle
        val subtitle = TextView(this).apply {
            text = "展示 A2UI v0.9 协议规范中的 36 个官方示例，JSON 内容无任何修改"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.parseColor("#888888"))
            setPadding(dp(16), 0, dp(16), dp(12))
        }
        rootLayout.addView(subtitle)

        // Scrollable content
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        val scrollContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, dp(12), dp(16))
        }

        // Load and render all 36 examples
        val exampleFiles = assets.list("a2ui_examples")?.sorted() ?: emptyList()
        var exampleIndex = 0
        for (fileName in exampleFiles) {
            if (!fileName.endsWith(".json")) continue
            exampleIndex++

            //if (exampleIndex != 37 && exampleIndex != 38) continue // For quick testing, only load the 24th example. Remove this line to load all examples.

            try {
                val jsonStr = assets.open("a2ui_examples/$fileName").bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(jsonStr)
                val name = jsonObj.optString("name", fileName)
                val description = jsonObj.optString("description", "")
                val messages = jsonObj.getJSONArray("messages").toString()

                // Card label
                val label = TextView(this).apply {
                    text = "$exampleIndex. $name"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    setTextColor(Color.parseColor("#333333"))
                    typeface = Typeface.DEFAULT_BOLD
                    setPadding(dp(4), dp(12), 0, dp(4))
                }
                scrollContent.addView(label)

                if (description.isNotEmpty()) {
                    val descView = TextView(this).apply {
                        text = description
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                        setTextColor(Color.parseColor("#888888"))
                        setPadding(dp(4), 0, 0, dp(6))
                    }
                    scrollContent.addView(descView)
                }

                // Card container - transparent layout wrapper, no extra styling
                // (the inner Card component handles its own styling per v0.9 protocol)
                val cardContainer = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = dp(4)
                    }
                    setBackgroundColor(Color.TRANSPARENT)
                }

                // A2UI surface view - WRAP_CONTENT so it sizes to its content
                val surfaceView = A2UISurfaceView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                cardContainer.addView(surfaceView)
                scrollContent.addView(cardContainer)

                surfaceView.init("{\"debug\": true}")
                surfaceView.setListener(object : A2UISurfaceViewListener {
                    override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                        Log.d(TAG, "[Example: $name] Action received: surfaceId=$surfaceId, tag=$tag, action=$action")
                        Toast.makeText(this@OfficialExampleActivity, "Action from '$name': $action", Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataModelChanged(dataModelJson: String, changedKey: String) {
                        Log.d(TAG, "[Example: $name] DataModel changed: key=$changedKey, model=$dataModelJson")
                    }
                    override fun onOpenUrl(url: String) {
                        Log.d(TAG, "[Example: $name] Open URL: $url")
                        openUrl(url)
                    }
                })
                // A2UI v0.9 server-to-client is a stream of JSON envelope OBJECTS.
                // Our demo asset files bundle multiple envelopes into a JSON ARRAY
                // for convenience (not part of the protocol), so we unpack it here
                // on the caller side and feed each envelope through writeStream().
                A2UIDemoStreamUtil.writeMessages(surfaceView, messages)
                surfaceView.finishStream()
                surfaceViews.add(surfaceView)
            } catch (e: Throwable) {
                // Do not crash the whole page for a single malformed example.
                // Log the error and render an inline error placeholder so other examples still show.
                Log.e(TAG, "Failed to load example '$fileName': ${e.message}", e)

                val errorLabel = TextView(this).apply {
                    text = "$exampleIndex. $fileName (加载失败)"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    setTextColor(Color.parseColor("#CC0000"))
                    typeface = Typeface.DEFAULT_BOLD
                    setPadding(dp(4), dp(12), 0, dp(4))
                }
                scrollContent.addView(errorLabel)

                val errorDetail = TextView(this).apply {
                    text = "${e.javaClass.simpleName}: ${e.message ?: "unknown error"}"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setTextColor(Color.parseColor("#CC0000"))
                    setPadding(dp(8), dp(6), dp(8), dp(6))
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#FFF3F3"))
                        cornerRadius = dp(8).toFloat()
                    }
                }
                val errorContainer = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = dp(4)
                    }
                    addView(errorDetail)
                }
                scrollContent.addView(errorContainer)
            }
        }

        scrollView.addView(scrollContent)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        surfaceViews.forEach { it.destroy() }
        surfaceViews.clear()
    }

    /** Open a Markdown link URL in the system browser. */
    private fun openUrl(url: String) {
        try {
            val uri = android.net.Uri.parse(url)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open URL: $url", e)
            Toast.makeText(this, "无法打开链接: $url", Toast.LENGTH_SHORT).show()
        }
    }
}

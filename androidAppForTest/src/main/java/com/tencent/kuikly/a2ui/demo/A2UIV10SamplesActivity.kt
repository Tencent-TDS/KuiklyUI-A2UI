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
 * A2UI v1.0 Samples Activity — PR-10 regression gallery (真机可跑).
 *
 * Mirrors [OfficialExampleActivity] but loads the 5 v1.0 strict-mode samples
 * living under `assets/a2ui_v10_tests/` (produced in PR-10). Each sample is
 * self-contained and demonstrates one of:
 *
 *  1. PR-2 Mixin keys (@path / @call / @accessibility)
 *  2. PR-3 createSurface.dataModel + surface catalogId + @index
 *  3. PR-6 Bidirectional RPC (callRendererFunction + agentFunctionResponse)
 *  4. PR-8 Multi-Catalog (@catalogId + @call.catalogId)
 *  5. PR-9 Small fields (Video.posterUrl / TextField.placeholder / Slider.steps / action.userMessage)
 *
 * The README.md inside the directory is skipped by the `.endsWith(".json")`
 * gate — no need to filter it out explicitly.
 */
class A2UIV10SamplesActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "A2UIV10SamplesActivity"
        private const val ASSETS_DIR = "v1_0/a2ui_v10_tests"
    }

    private val surfaceViews = mutableListOf<A2UISurfaceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            text = "A2UI v1.0 回归样本"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#333333"))
            typeface = Typeface.DEFAULT_BOLD
        }
        titleBar.addView(backArrow)
        titleBar.addView(titleText)
        rootLayout.addView(titleBar)

        // Subtitle
        val subtitle = TextView(this).apply {
            text = "展示 PR-10 产出的 5 份 v1.0 严格模式样本 —— 覆盖 Mixin keys / 内联 dataModel / 双向 RPC / 多 Catalog / 小字段"
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

        // Load and render all v1.0 samples under assets/a2ui_v10_tests/
        val sampleFiles = assets.list(ASSETS_DIR)?.sorted() ?: emptyList()
        var sampleIndex = 0
        for (fileName in sampleFiles) {
            if (!fileName.endsWith(".json")) continue
            sampleIndex++

            try {
                val jsonStr = assets.open("$ASSETS_DIR/$fileName").bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(jsonStr)
                val name = jsonObj.optString("name", fileName)
                val description = jsonObj.optString("description", "")
                val envelopes = jsonObj.getJSONArray("messages")
                A2UIDemoAssetVersion.validate((0 until envelopes.length()).map {
                    envelopes.optJSONObject(it)?.optString("version")
                }, ASSETS_DIR)
                val messages = envelopes.toString()

                // Card label
                val label = TextView(this).apply {
                    text = "$sampleIndex. $name"
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

                // Card container
                val cardContainer = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = dp(4)
                    }
                    setBackgroundColor(Color.TRANSPARENT)
                }

                // A2UI surface view
                val surfaceView = A2UISurfaceView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                cardContainer.addView(surfaceView)
                scrollContent.addView(cardContainer)

                surfaceView.init(A2UIDemoAssetVersion.config(ASSETS_DIR))
                surfaceView.setListener(object : A2UISurfaceViewListener {
                    override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                        Log.d(TAG, "[Sample: $name] Action: surfaceId=$surfaceId, tag=$tag, action=$action, params=$params")
                        Toast.makeText(
                            this@A2UIV10SamplesActivity,
                            "Action from '$name': $action",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    override fun onDataModelChanged(dataModelJson: String, changedKey: String) {
                        Log.d(TAG, "[Sample: $name] DataModel changed: key=$changedKey, model=$dataModelJson")
                    }
                    override fun onOpenUrl(url: String) {
                        Log.d(TAG, "[Sample: $name] Open URL: $url")
                        openUrl(url)
                    }
                })
                A2UIDemoStreamUtil.writeMessages(surfaceView, messages)
                surfaceView.finishStream()
                surfaceViews.add(surfaceView)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to load v1.0 sample '$fileName': ${e.message}", e)

                val errorLabel = TextView(this).apply {
                    text = "$sampleIndex. $fileName (加载失败)"
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

        if (sampleIndex == 0) {
            val emptyHint = TextView(this).apply {
                text = "assets/$ASSETS_DIR 下未发现任何 .json 样本文件。"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#CC0000"))
                setPadding(dp(16), dp(24), dp(16), dp(24))
            }
            scrollContent.addView(emptyHint)
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

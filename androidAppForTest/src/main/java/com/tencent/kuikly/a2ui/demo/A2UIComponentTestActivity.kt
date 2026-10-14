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
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener
import org.json.JSONObject

/**
 * Component Test Activity.
 * Renders all test-case JSON files under assets/<rootDir>/<componentDir>/ for
 * a single A2UI component. Used to verify per-property rendering correctness.
 *
 * Launched from [ComponentDemoActivity]. The caller passes:
 *  - [EXTRA_COMPONENT_DIR] : sub-directory name under <rootDir>/ (e.g. "01_string_return")
 *  - [EXTRA_COMPONENT_NAME]: display name shown in the title bar (e.g. "Text")
 */
class A2UIComponentTestActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COMPONENT_DIR = "extra_component_dir"
        const val EXTRA_COMPONENT_NAME = "extra_component_name"
        /** Optional. Asset root dir override. Callers should always pass this via EXTRA_ROOT_DIR. */
        const val EXTRA_ROOT_DIR = "extra_root_dir"
        /** Optional. Title suffix. Defaults to "组件测试" (component test). */
        const val EXTRA_TITLE_SUFFIX = "extra_title_suffix"
        private const val DEFAULT_ROOT_DIR = "a2ui_function_tests"
        private const val DEFAULT_TITLE_SUFFIX = "组件测试"
        private const val TAG = "A2UIComponentTest"
    }

    private val surfaceViews = mutableListOf<A2UISurfaceView>()
    private lateinit var componentDir: String
    private lateinit var componentName: String
    private lateinit var rootDir: String
    private lateinit var titleSuffix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        componentDir = intent.getStringExtra(EXTRA_COMPONENT_DIR).orEmpty()
        componentName = intent.getStringExtra(EXTRA_COMPONENT_NAME).orEmpty()
        if (componentName.isEmpty()) componentName = componentDir
        rootDir = intent.getStringExtra(EXTRA_ROOT_DIR)?.takeIf { it.isNotEmpty() } ?: DEFAULT_ROOT_DIR
        titleSuffix = intent.getStringExtra(EXTRA_TITLE_SUFFIX)?.takeIf { it.isNotEmpty() } ?: DEFAULT_TITLE_SUFFIX
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

        // Header (purple bar matching ComponentDemoActivity style)
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
            text = "$componentName $titleSuffix"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "测试用例目录：$rootDir/$componentDir/"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#DDDDFF"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

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
            setPadding(dp(12), dp(8), dp(12), dp(16))
        }

        val dirPath = "$rootDir/$componentDir"
        val files = try {
            assets.list(dirPath)?.filter { it.endsWith(".json") }?.sorted() ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to list assets at $dirPath: ${e.message}")
            emptyList()
        }

        if (files.isEmpty()) {
            val empty = TextView(this).apply {
                text = "暂无测试用例\n($dirPath 为空或不存在)"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.parseColor("#999999"))
                gravity = Gravity.CENTER
                setPadding(dp(24), dp(48), dp(24), dp(48))
            }
            scrollContent.addView(empty)
        } else {
            renderCases(scrollContent, dirPath, files, ::dp)
        }

        scrollView.addView(scrollContent)
        rootLayout.addView(scrollView)
        return rootLayout
    }

    private fun renderCases(
        container: LinearLayout,
        dirPath: String,
        files: List<String>,
        dp: (Int) -> Int
    ) {
        var index = 0
        for (fileName in files) {
            index++
            val jsonStr = try {
                assets.open("$dirPath/$fileName").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read $dirPath/$fileName: ${e.message}")
                continue
            }
            val jsonObj = try {
                JSONObject(jsonStr)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid JSON in $fileName: ${e.message}")
                continue
            }
            val name = jsonObj.optString("name", fileName)
            val description = jsonObj.optString("description", "")
            val messages = jsonObj.optJSONArray("messages")?.toString().orEmpty()
            if (messages.isEmpty()) {
                Log.w(TAG, "$fileName has no 'messages' field, skip")
                continue
            }

            // Title row: index + name + filename badge
            val titleRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(4), dp(12), dp(4), dp(4))
            }
            val titleText = TextView(this).apply {
                text = "$index. $name"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#333333"))
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            }
            val fileBadge = TextView(this).apply {
                text = fileName
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                setTextColor(Color.parseColor("#6C5CE7"))
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#EEE9FF"))
                    cornerRadius = dp(4).toFloat()
                }
                setPadding(dp(6), dp(2), dp(6), dp(2))
            }
            titleRow.addView(titleText)
            titleRow.addView(fileBadge)
            container.addView(titleRow)

            if (description.isNotEmpty()) {
                val descView = TextView(this).apply {
                    text = description
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setTextColor(Color.parseColor("#888888"))
                    setPadding(dp(4), 0, 0, dp(6))
                }
                container.addView(descView)
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
            val surfaceView = A2UISurfaceView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }
            cardContainer.addView(surfaceView)
            container.addView(cardContainer)

            surfaceView.init("{\"debug\": true}")
            surfaceView.setListener(object : A2UISurfaceViewListener {
                override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                    Log.d(TAG, "[$name] Action: surfaceId=$surfaceId, tag=$tag, action=$action, params=$params")
                    showActionDialog(name, action, params)
                }
                override fun onDataModelChanged(dataModelJson: String, changedKey: String) {
                    Log.d(TAG, "[$name] DataModel changed: key=$changedKey")
                }
                override fun onOpenUrl(url: String) {
                    Log.d(TAG, "[$name] Open URL: $url")
                    openUrl(url)
                }
            })
            A2UIDemoStreamUtil.writeMessages(surfaceView, messages)
            surfaceView.finishStream()
            surfaceViews.add(surfaceView)
        }
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
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open URL: $url", e)
            Toast.makeText(this, "无法打开链接: $url", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Show a dialog with the fired action and (optionally pretty-printed) params.
     * Used by A2UISurfaceViewListener.onAction to make debug output easier to read
     * than a Toast, especially when params carry JSON payloads.
     */
    private fun showActionDialog(caseName: String, action: String, params: String?) {
        val prettyParams = params?.takeIf { it.isNotBlank() }?.let { raw ->
            try {
                // Try JSONObject first, fall back to JSONArray, otherwise keep raw text.
                JSONObject(raw).toString(2)
            } catch (_: Exception) {
                try {
                    org.json.JSONArray(raw).toString(2)
                } catch (_: Exception) {
                    raw
                }
            }
        } ?: "(no params)"

        val message = "action: $action\n\nparams:\n$prettyParams"
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Action · $caseName")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

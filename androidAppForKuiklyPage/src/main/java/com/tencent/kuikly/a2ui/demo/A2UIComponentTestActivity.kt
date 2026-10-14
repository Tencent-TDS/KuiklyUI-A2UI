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
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UIKuiklyBaseViewSupport
import com.tencent.kuikly.core.render.android.expand.KuiklyBaseView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Component Test Activity (KuiklyPage variant).
 *
 * Reads all `<caseName>.json` files under `assets/<rootDir>/<componentDir>/`
 * and forwards them to the KMP page `A2UICaseList` (see
 * `:demo/…/A2UICaseListPager`) via `KuiklyBaseView.onAttach`. The Kuikly
 * page then renders every case as a stand-alone A2UISurface, giving us the
 * "many-cards-per-screen" UX with a single KMP page instance.
 *
 * Intent extras (all optional aside from EXTRA_COMPONENT_DIR):
 *  - [EXTRA_COMPONENT_DIR]: sub-directory name (e.g. "text", "01_string_return")
 *  - [EXTRA_COMPONENT_NAME]: display name shown in the header
 *  - [EXTRA_ROOT_DIR]: parent asset dir (defaults to "a2ui_components")
 *  - [EXTRA_TITLE_SUFFIX]: title suffix (defaults to "组件测试")
 */
class A2UIComponentTestActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COMPONENT_DIR = "extra_component_dir"
        const val EXTRA_COMPONENT_NAME = "extra_component_name"
        const val EXTRA_ROOT_DIR = "extra_root_dir"
        const val EXTRA_TITLE_SUFFIX = "extra_title_suffix"
        private const val DEFAULT_ROOT_DIR = "a2ui_components"
        private const val DEFAULT_TITLE_SUFFIX = "组件测试"
        private const val TAG = "A2UIComponentTest"
        /** Must match the `@Page` name on `A2UICaseListPager` in :demo. */
        private const val PAGE_NAME = "A2UICaseList"
    }

    private var renderView: KuiklyBaseView? = null
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

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#3F51B5"))
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
            text = "宿主：Kuikly Page（KuiklyBaseView + @Page A2UICaseList）\n资源：$rootDir/$componentDir/"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#DDDDFF"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

        // Kuikly page container — fills the remaining vertical space so the
        // Kuikly render view has a positive size before onAttach() runs.
        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        rootLayout.addView(container)

        container.post {
            attachKuiklyPage(container)
        }

        return rootLayout
    }

    /**
     * Aggregate every JSON test case under the target assets directory into a
     * single `cases` JSON array string, then attach a KuiklyBaseView bound to
     * the `A2UICaseList` KMP page and forward the array via `pageData`.
     */
    private fun attachKuiklyPage(container: ViewGroup) {
        val dirPath = "$rootDir/$componentDir"
        val files = try {
            assets.list(dirPath)?.filter { it.endsWith(".json") }?.sorted() ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to list assets at $dirPath: ${e.message}")
            emptyList()
        }

        val casesArr = JSONArray()
        for (fileName in files) {
            val jsonStr = try {
                assets.open("$dirPath/$fileName").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read $dirPath/$fileName: ${e.message}")
                continue
            }
            val obj = try {
                JSONObject(jsonStr)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid JSON in $fileName: ${e.message}")
                continue
            }
            val messages = obj.optJSONArray("messages") ?: continue
            val caseObj = JSONObject().apply {
                put("name", obj.optString("name", fileName))
                put("description", obj.optString("description", ""))
                put("messages", messages)
            }
            casesArr.put(caseObj)
        }

        val view = KuiklyBaseView(this, A2UIKuiklyBaseViewSupport.wrap())
        container.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        renderView = view
        view.onAttach(
            contextCode = "",
            pageName = PAGE_NAME,
            pageData = mapOf(
                "title" to "$componentName $titleSuffix",
                "cases" to casesArr.toString()
            ),
            size = Size(container.width, container.height),
            assetsPath = null
        )
    }

    override fun onResume() {
        super.onResume()
        renderView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        renderView?.onPause()
    }

    override fun onDestroy() {
        renderView?.onDetach()
        renderView = null
        super.onDestroy()
    }
}

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
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener

/**
 * Custom Icon Demo Activity (NativePage integration).
 *
 * Mirrors the KuiklyPage variant: registers a small set of icons through
 * [A2UIIconRegistry] on create, renders a pair of A2UI cards displaying
 * both registered and unregistered icon names, then unregisters on destroy
 * so other screens keep the built-in unicode/emoji glyphs.
 *
 * The rendering path here is *Native View* (an [A2UISurfaceView] per card)
 * rather than the KuiklyPage's KuiklyBaseView + Kuikly Compose/DSL page.
 *
 * The two A2UI cards are hand-written as literal JSON strings (no runtime
 * JSONObject/JSONArray assembly), one pair of create/update envelopes each.
 */
class CustomIconDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CustomIconDemo"
        private val REGISTERED_ICONS = mapOf(
            "star" to "assets://a2ui_icons/star.png",
            "favorite" to "assets://a2ui_icons/favorite.png",
            "home" to "assets://a2ui_icons/home.png",
            "settings" to "assets://a2ui_icons/settings.png",
            "search" to "assets://a2ui_icons/search.png",
            A2UIIconRegistry.FALLBACK_KEY to "assets://a2ui_icons/_fallback.png"
        )

        // ==== Card A: 5 registered icons — hit KRImageView through REGISTERED_ICONS ====
        private const val REGISTERED_CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "native-icon-card-registered" }
            }
        """
        private const val REGISTERED_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-icon-card-registered",
                "components": [
                  { "id": "root",    "component": "Card", "child": "row" },
                  { "id": "row",     "component": "Row",  "justify": "spaceAround", "align": "center",
                    "children": ["icon-0", "icon-1", "icon-2", "icon-3", "icon-4"] },
                  { "id": "icon-0",  "component": "Icon", "name": "star" },
                  { "id": "icon-1",  "component": "Icon", "name": "favorite" },
                  { "id": "icon-2",  "component": "Icon", "name": "home" },
                  { "id": "icon-3",  "component": "Icon", "name": "settings" },
                  { "id": "icon-4",  "component": "Icon", "name": "search" }
                ]
              }
            }
        """

        // ==== Card B: 3 unregistered names — all fall back to _fallback.png ====
        private const val UNREGISTERED_CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "native-icon-card-unregistered" }
            }
        """
        private const val UNREGISTERED_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-icon-card-unregistered",
                "components": [
                  { "id": "root",    "component": "Card", "child": "row" },
                  { "id": "row",     "component": "Row",  "justify": "spaceAround", "align": "center",
                    "children": ["icon-0", "icon-1", "icon-2"] },
                  { "id": "icon-0",  "component": "Icon", "name": "camera" },
                  { "id": "icon-1",  "component": "Icon", "name": "delete" },
                  { "id": "icon-2",  "component": "Icon", "name": "edit" }
                ]
              }
            }
        """
    }

    private val surfaceViews = mutableListOf<A2UISurfaceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        A2UIIconRegistry.registerIcons(REGISTERED_ICONS)
        setContentView(buildLayout())
    }

    private fun buildLayout(): View {
        val density = resources.displayMetrics.density
        fun dp(value: Int) = (value * density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#D81B60"))
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
            text = "自定义 Icon 图片"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)
        val headerSubtitle = TextView(this).apply {
            text = "宿主：Native Activity + A2UISurfaceView\n已注册 ${REGISTERED_ICONS.size - 1} 个 Icon + _fallback，未注册的名称均落到 _fallback"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#FCE4EC"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        root.addView(header)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setPadding(dp(12), dp(8), dp(12), dp(12))
        }
        val cardsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(cardsContainer)
        root.addView(scrollView)

        addIconCard(
            cardsContainer,
            title = "已注册 Icon（走 KRImageView）",
            description = "star / favorite / home / settings / search 均命中 REGISTERED_ICONS",
            createJson = REGISTERED_CREATE_SURFACE_JSON,
            updateJson = REGISTERED_UPDATE_COMPONENTS_JSON,
            dp = ::dp
        )
        addIconCard(
            cardsContainer,
            title = "未注册 Icon（走 _fallback）",
            description = "camera / delete / edit 三个名称未注册，均渲染为 _fallback.png",
            createJson = UNREGISTERED_CREATE_SURFACE_JSON,
            updateJson = UNREGISTERED_UPDATE_COMPONENTS_JSON,
            dp = ::dp
        )

        return root
    }

    private fun addIconCard(
        parent: LinearLayout,
        title: String,
        description: String,
        createJson: String,
        updateJson: String,
        dp: (Int) -> Int
    ) {
        val titleView = TextView(this).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#333333"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(4), dp(10), dp(4), dp(2))
        }
        val descView = TextView(this).apply {
            text = description
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#888888"))
            setPadding(dp(4), 0, 0, dp(6))
        }
        parent.addView(titleView)
        parent.addView(descView)

        val cardWrap = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(8).toFloat()
            }
        }
        val surface = A2UISurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        cardWrap.addView(surface)
        parent.addView(cardWrap)

        surface.init("{}")
        surface.setListener(object : A2UISurfaceViewListener {
            override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                Log.d(TAG, "onAction $title tag=$tag action=$action")
            }
            override fun onDataModelChanged(dataModelJson: String, changedKey: String) = Unit
            override fun onOpenUrl(url: String) {
                Log.d(TAG, "openUrl $url")
            }
        })
        surface.writeStream(createJson)
        surface.writeStream(updateJson)
        surface.finishStream()
        surfaceViews.add(surface)
    }

    override fun onDestroy() {
        surfaceViews.forEach { it.destroy() }
        surfaceViews.clear()
        REGISTERED_ICONS.keys.forEach { A2UIIconRegistry.unregisterIcon(it) }
        super.onDestroy()
    }
}

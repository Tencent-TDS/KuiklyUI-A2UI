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
import android.util.Size
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.render.android.A2UIKuiklyBaseViewSupport
import com.tencent.kuikly.core.render.android.expand.KuiklyBaseView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Custom Icon Demo Activity.
 *
 * Demonstrates how a business can override A2UI's built-in unicode/emoji
 * Icon rendering with its own PNG assets:
 *  1. On create: registers a curated set of icons into [A2UIIconRegistry]
 *     — plus the reserved `_fallback` entry — so every subsequent A2UI Icon
 *     component in this process renders via `KRImageView` instead of the
 *     framework's built-in glyph.
 *  2. Hosts the visualisation inside a Kuikly Page ([A2UICaseListPager])
 *     containing a few A2UI Icon cards that stress-test the registered names
 *     as well as an *unknown* name (should fall back to `_fallback`).
 *  3. On destroy: undoes the registration so other screens keep rendering
 *     the built-in glyphs.
 *
 * This is the *only* activity in the KuiklyPage app that touches
 * [A2UIIconRegistry]; other screens deliberately keep the default rendering
 * to prove that icon customisation is opt-in.
 */
class CustomIconDemoActivity : AppCompatActivity() {

    companion object {
        /** Must match the `@Page` name on `A2UICaseListPager` in :demo. */
        private const val PAGE_NAME = "A2UICaseList"
        /**
         * Only a handful of icons are registered here — enough to prove the
         * override works without shipping the full ICON_MAP. Icon names not
         * in this list still land on `_fallback`.
         */
        private val REGISTERED_ICONS = mapOf(
            "star" to "assets://a2ui_icons/star.png",
            "favorite" to "assets://a2ui_icons/favorite.png",
            "home" to "assets://a2ui_icons/home.png",
            "settings" to "assets://a2ui_icons/settings.png",
            "search" to "assets://a2ui_icons/search.png",
            A2UIIconRegistry.FALLBACK_KEY to "assets://a2ui_icons/_fallback.png"
        )
    }

    private var renderView: KuiklyBaseView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Register our PNGs before the Kuikly page mounts so the very
        //    first Icon layout resolves against them.
        A2UIIconRegistry.registerIcons(REGISTERED_ICONS)

        setContentView(buildLayout())
    }

    private fun buildLayout(): android.view.View {
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
            text = "宿主：Kuikly Page（A2UICaseList）\n已注册 ${REGISTERED_ICONS.size - 1} 个 Icon + _fallback，其它 Icon 名称均落到 _fallback"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#FCE4EC"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        rootLayout.addView(header)

        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        rootLayout.addView(container)

        container.post { attachKuiklyPage(container) }
        return rootLayout
    }

    private fun attachKuiklyPage(container: ViewGroup) {
        val casesArr = JSONArray().apply {
            put(buildIconGridCase(
                name = "已注册 Icon（走 KRImageView）",
                description = "star / favorite / home / settings / search 均命中 REGISTERED_ICONS",
                iconNames = listOf("star", "favorite", "home", "settings", "search")
            ))
            put(buildIconGridCase(
                name = "未注册 Icon（走 _fallback）",
                description = "camera / delete / edit 三个名称未注册，均渲染为 _fallback.png",
                iconNames = listOf("camera", "delete", "edit")
            ))
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
                "title" to "自定义 Icon 图片",
                "cases" to casesArr.toString()
            ),
            size = Size(container.width, container.height),
            assetsPath = null
        )
    }

    /**
     * Build one "case" object for [A2UICaseListPager] whose surface contains a
     * horizontal Row of A2UI Icon components. Each icon references [iconNames]
     * so the Icon rendering path exercises the freshly-registered PNGs.
     */
    private fun buildIconGridCase(
        name: String,
        description: String,
        iconNames: List<String>
    ): JSONObject {
        val surfaceId = "icon-case-${name.hashCode()}"
        val iconChildren = JSONArray()
        val components = JSONArray()

        // Root Card > Row > Icons
        components.put(JSONObject().apply {
            put("id", "root")
            put("component", "Card")
            put("children", JSONArray().apply { put("row") })
        })
        val row = JSONObject().apply {
            put("id", "row")
            put("component", "Row")
            put("children", iconChildren)
            put("justifyContent", "spaceAround")
            put("alignItems", "center")
            put("padding", 16)
        }
        components.put(row)

        for ((index, iconName) in iconNames.withIndex()) {
            val id = "icon-$index"
            iconChildren.put(id)
            components.put(JSONObject().apply {
                put("id", id)
                put("component", "Icon")
                put("name", iconName)
                put("size", 48)
            })
        }

        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("version", "v0.9")
                put("createSurface", JSONObject().apply {
                    put("surfaceId", surfaceId)
                })
            })
            put(JSONObject().apply {
                put("version", "v0.9")
                put("updateComponents", JSONObject().apply {
                    put("surfaceId", surfaceId)
                    put("components", components)
                })
            })
        }

        return JSONObject().apply {
            put("name", name)
            put("description", description)
            put("messages", messages)
        }
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
        // Undo the registration so the rest of the app keeps rendering the
        // built-in unicode/emoji glyphs for Icon components. This mirrors the
        // register/unregister pattern used by CustomFunctionDemoActivity.
        REGISTERED_ICONS.keys.forEach { A2UIIconRegistry.unregisterIcon(it) }
        super.onDestroy()
    }
}

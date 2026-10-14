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
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener

/**
 * Native Page single-card sample.
 *
 * Embeds an [A2UISurfaceView] directly inside a plain Android view tree
 * and pushes one hand-crafted A2UI v0.9 weather-card payload through
 * `writeStream()` / `finishStream()`. No Kuikly Pager involved.
 */
class NativePageCardActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NativePageOneCard"

        /**
         * Hand-crafted A2UI v0.9 weather-card envelopes. Kept identical (in
         * intent) to the weather card used by the Kuikly Page's Compose demo,
         * so users can eyeball that both integration paths render the same
         * DSL. Split into two envelopes: createSurface + updateComponents.
         */
        private const val CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "both-native-weather" }
            }
        """

        private const val UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "both-native-weather",
                "components": [
                  {
                    "id": "root", "component": "Card", "child": "outer",
                    "styles": {
                      "padding": 10,
                      "backgroundLinearGradient": {
                        "direction": "to-bottom",
                        "colorStops": [
                          { "color": "#3A6DF0", "stop": 0.0 },
                          { "color": "#5B4BE1", "stop": 0.55 },
                          { "color": "#7A3FD8", "stop": 1.0 }
                        ]
                      }
                    }
                  },
                  {
                    "id": "outer", "component": "Column", "align": "stretch",
                    "children": ["title", "subtitle", "hero", "detail"]
                  },

                  {
                    "id": "title", "component": "Text", "text": "深圳 · 今日天气", "variant": "h3",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 15,
                      "fontWeight": "medium",
                      "letterSpacing": 1,
                      "opacity": 0.85,
                      "textAlign": "center",
                      "marginTop": 8,
                      "marginBottom": 0
                    }
                  },
                  {
                    "id": "subtitle", "component": "Text", "text": "周一 · 9月8日 · 体感 29°", "variant": "caption",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 11,
                      "fontWeight": "normal",
                      "letterSpacing": 0.5,
                      "opacity": 0.6,
                      "textAlign": "center",
                      "marginTop": 0,
                      "marginBottom": 2,
                      "textShadow": {
                        "offsetX": 0, "offsetY": 1, "radius": 2, "color": "#40000000"
                      }
                    }
                  },

                  {
                    "id": "hero", "component": "Row", "align": "center",
                    "children": ["temp", "cond"],
                    "styles": {
                      "marginTop": 0,
                      "marginBottom": 4,
                      "paddingLeft": 4,
                      "paddingRight": 4
                    }
                  },
                  {
                    "id": "temp", "component": "Text", "text": "27°", "variant": "h1",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 84,
                      "fontWeight": "light",
                      "letterSpacing": -2,
                      "flexGrow": 1,
                      "marginTop": 0,
                      "marginBottom": 0,
                      "transform": {
                        "scale": 1.02,
                        "translate": [0, -0.02],
                        "anchor": [0, 0.5]
                      },
                      "textShadow": {
                        "offsetX": 0, "offsetY": 2, "radius": 12, "color": "#66000000"
                      }
                    }
                  },
                  {
                    "id": "cond", "component": "Text", "text": "多云转晴", "variant": "body",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 17,
                      "fontWeight": "medium",
                      "opacity": 0.9,
                      "textAlign": "right",
                      "flexGrow": 1,
                      "marginTop": 0,
                      "marginBottom": 0,
                      "textShadow": {
                        "offsetX": 0, "offsetY": 1, "radius": 4, "color": "#4D000000"
                      }
                    }
                  },

                  {
                    "id": "detail", "component": "Row", "justify": "spaceBetween",
                    "children": ["hum", "wind", "aqi"],
                    "styles": {
                      "marginTop": 4,
                      "paddingTop": 0
                    }
                  },

                  {
                    "id": "hum", "component": "Row", "align": "center",
                    "children": ["humText"],
                    "styles": {
                      "backgroundColor": "#22FFFFFF",
                      "borderRadius": 8,
                      "paddingLeft": 10,
                      "paddingRight": 10,
                      "paddingTop": 3,
                      "paddingBottom": 3,
                      "marginLeft": 8,
                      "marginTop": 0,
                      "marginBottom": 8,
                      "boxShadow": {
                        "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
                      }
                    }
                  },
                  {
                    "id": "humText", "component": "Text", "text": "湿度 68%", "variant": "caption",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 12,
                      "fontWeight": "medium",
                      "opacity": 0.9,
                      "marginTop": 0,
                      "marginBottom": 0
                    }
                  },

                  {
                    "id": "wind", "component": "Row", "align": "center",
                    "children": ["windText"],
                    "styles": {
                      "backgroundColor": "#22FFFFFF",
                      "borderRadius": 8,
                      "paddingLeft": 10,
                      "paddingRight": 10,
                      "paddingTop": 3,
                      "paddingBottom": 3,
                      "marginTop": 0,
                      "marginBottom": 8,
                      "boxShadow": {
                        "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
                      }
                    }
                  },
                  {
                    "id": "windText", "component": "Text", "text": "东南风 3 级", "variant": "caption",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 12,
                      "fontWeight": "medium",
                      "opacity": 0.9,
                      "marginTop": 0,
                      "marginBottom": 0
                    }
                  },

                  {
                    "id": "aqi", "component": "Row", "align": "center",
                    "children": ["aqiText"],
                    "styles": {
                      "backgroundColor": "#22FFFFFF",
                      "borderRadius": 8,
                      "paddingLeft": 10,
                      "paddingRight": 10,
                      "paddingTop": 3,
                      "paddingBottom": 3,
                      "marginRight": 8,
                      "marginTop": 0,
                      "marginBottom": 8,
                      "boxShadow": {
                        "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
                      }
                    }
                  },
                  {
                    "id": "aqiText", "component": "Text", "text": "空气 良 · 58", "variant": "caption",
                    "styles": {
                      "color": "#FFFFFF",
                      "fontSize": 12,
                      "fontWeight": "medium",
                      "opacity": 0.9,
                      "marginTop": 0,
                      "marginBottom": 0
                    }
                  }
                ]              }
            }
        """
    }

    private var surfaceView: A2UISurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val density = resources.displayMetrics.density
        fun dp(value: Int) = (value * density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#455A64"))
            setPadding(dp(24), dp(48), dp(24), dp(16))
        }
        val headerRow = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        headerRow.addView(TextView(this).apply {
            text = "←"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            setPadding(0, 0, dp(12), 0)
            setOnClickListener { finish() }
        })
        headerRow.addView(TextView(this).apply {
            text = "Native 页面接入示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        })
        header.addView(headerRow)
        header.addView(TextView(this).apply {
            text = "宿主：Native Activity + A2UISurfaceView"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#CFD8DC"))
            setPadding(0, dp(4), 0, 0)
        })
        root.addView(header)

        val cardWrap = FrameLayout(this).apply {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dp(12), dp(12), dp(12), dp(12))
            layoutParams = params
            // No opaque native background — the A2UI card owns the visual
            // (gradient + rounded corners) via `styles`.
        }
        val surface = A2UISurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        cardWrap.addView(surface)
        root.addView(cardWrap)

        surface.init("{}")
        surface.setListener(object : A2UISurfaceViewListener {
            override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                Log.d(TAG, "onAction tag=$tag action=$action")
            }
            override fun onDataModelChanged(dataModelJson: String, changedKey: String) = Unit
            override fun onOpenUrl(url: String) {
                Log.d(TAG, "openUrl $url")
            }
        })
        // Feed the two pre-built v0.9 envelopes directly. Each writeStream()
        // call must be a single JSON object per the A2UI protocol.
        surface.writeStream(CREATE_SURFACE_JSON)
        surface.writeStream(UPDATE_COMPONENTS_JSON)
        surface.finishStream()
        surfaceView = surface

        setContentView(root)
    }

    override fun onDestroy() {
        surfaceView?.destroy()
        surfaceView = null
        super.onDestroy()
    }
}

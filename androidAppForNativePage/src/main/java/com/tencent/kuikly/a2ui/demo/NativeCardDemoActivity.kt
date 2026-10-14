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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener
import org.json.JSONObject

/**
 * Native Card Demo Activity (NativePage integration).
 *
 * Mirrors the "card scene" example of the KuiklyPage app but stays on the
 * *Native View* integration path: no Kuikly Pager, no `KuiklyBaseView` —
 * everything sits inside a hand-written Android view tree. Each tap on the
 * top button appends one A2UI card to the scroll view below, cycling through
 * three card kinds in a fixed order:
 *   0 → plain text card   (Card > Column > Text)
 *   1 → list card         (Card > Column > Title + List(Row * n))
 *   2 → image+row+column  (Card > Row > Image + Column(Title + subtitle))
 *
 * Every card uses only A2UI v0.9 standard components — no Kuikly-specific
 * views — so the assets match the KuiklyPage variant one-for-one.
 *
 * All A2UI messages are hand-written as literal JSON strings (no runtime
 * JSONObject/JSONArray assembly) with a single `%SEQ%` placeholder for the
 * card sequence number.
 */
class NativeCardDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NativeCardDemo"

        // ==== Card kind 0 · 航班状态 (adapted from 01_flight-status.json) ====
        //
        // 白底 + 顶部淡蓝渐变条 + 航班号大字 + 状态 pill + 出发/到达大时间.
        // 关键差异 (vs. 源 JSON):
        //   - 移除 formatDate / path 引用, 数据直写便于 native 端复用.
        //   - Icon 组件换成 emoji "✈️" (Text), 避免依赖 catalog icon 字体.
        //   - 状态用带底色的 pill (Row 套 Text) 表达 "准点/延误".
        //   - 大写字母 label 换成中文并降 opacity 做次级信息.
        private const val CARD0_CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "native-flight-card-%SEQ%" }
            }
        """
        private const val CARD0_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-flight-card-%SEQ%",
                "components": [
                  {
                    "id": "root", "component": "Card", "child": "main",
                    "styles": {
                      "padding": 0,
                      "backgroundColor": "#FFFFFF",
                      "borderRadius": 14
                    }
                  },
                  {
                    "id": "main", "component": "Column", "align": "stretch",
                    "children": ["banner", "content"]
                  },

                  {
                    "id": "banner", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["banner-left", "status-pill"],
                    "styles": {
                      "paddingLeft": 18, "paddingRight": 14,
                      "paddingTop": 10,   "paddingBottom": 10,
                      "backgroundLinearGradient": {
                        "direction": "to-right",
                        "colorStops": [
                          { "color": "#EAF2FF", "stop": 0.0 },
                          { "color": "#DCE7FE", "stop": 1.0 }
                        ]
                      }
                    }
                  },
                  {
                    "id": "banner-left", "component": "Row", "align": "center",
                    "children": ["plane-emoji", "flight-no"]
                  },
                  {
                    "id": "plane-emoji", "component": "Text", "text": "✈️",
                    "styles": { "fontSize": 20, "marginTop": 0, "marginBottom": 0, "marginRight": 8 }
                  },
                  {
                    "id": "flight-no", "component": "Text", "text": "CA1836 · 国航",
                    "styles": {
                      "color": "#1E3A8A",
                      "fontSize": 16,
                      "fontWeight": "bold",
                      "letterSpacing": 0.5,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "status-pill", "component": "Row", "align": "center",
                    "children": ["status-text"],
                    "styles": {
                      "backgroundColor": "#DCFCE7",
                      "borderRadius": 10,
                      "paddingLeft": 10, "paddingRight": 10,
                      "paddingTop": 3,   "paddingBottom": 3
                    }
                  },
                  {
                    "id": "status-text", "component": "Text", "text": "· 准点",
                    "styles": {
                      "color": "#166534",
                      "fontSize": 12,
                      "fontWeight": "bold",
                      "letterSpacing": 0.3,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },

                  {
                    "id": "content", "component": "Column", "align": "stretch",
                    "children": ["route", "route-code", "time-row", "footer"],
                    "styles": {
                      "paddingLeft": 18, "paddingRight": 18,
                      "paddingTop": 10, "paddingBottom": 10
                    }
                  },
                  {
                    "id": "route", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["origin", "arrow", "dest"]
                  },
                  {
                    "id": "origin", "component": "Text", "text": "深圳",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 26,
                      "fontWeight": "bold",
                      "letterSpacing": 1,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "arrow", "component": "Text", "text": "→",
                    "styles": {
                      "color": "#94A3B8",
                      "fontSize": 22,
                      "fontWeight": "normal",
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "dest", "component": "Text", "text": "北京",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 26,
                      "fontWeight": "bold",
                      "letterSpacing": 1,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "route-code", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["origin-code", "dur-text", "dest-code"],
                    "styles": { "marginTop": 0, "marginBottom": 4 }
                  },
                  {
                    "id": "origin-code", "component": "Text", "text": "SZX · T3",
                    "styles": { "color": "#64748B", "fontSize": 11, "letterSpacing": 0.5, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "dur-text", "component": "Text", "text": "3 小时 05 分",
                    "styles": { "color": "#64748B", "fontSize": 11, "letterSpacing": 0.3, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "dest-code", "component": "Text", "text": "PEK · T2",
                    "styles": { "color": "#64748B", "fontSize": 11, "letterSpacing": 0.5, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "time-row", "component": "Row", "align": "start", "justify": "spaceBetween",
                    "children": ["dep-col", "arr-col"],
                    "styles": {
                      "marginTop": 6
                    }
                  },
                  {
                    "id": "dep-col", "component": "Column", "align": "start",
                    "children": ["dep-label", "dep-time"]
                  },
                  {
                    "id": "dep-label", "component": "Text", "text": "起飞",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "letterSpacing": 1, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "dep-time", "component": "Text", "text": "10:15",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 20,
                      "fontWeight": "bold",
                      "letterSpacing": 0.5,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "arr-col", "component": "Column", "align": "end",
                    "children": ["arr-label", "arr-time"]
                  },
                  {
                    "id": "arr-label", "component": "Text", "text": "到达",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "letterSpacing": 1, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "arr-time", "component": "Text", "text": "13:20",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 20,
                      "fontWeight": "bold",
                      "letterSpacing": 0.5,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },

                  {
                    "id": "footer", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["gate-pill", "seat-pill"],
                    "styles": { "marginTop": 6 }
                  },
                  {
                    "id": "gate-pill", "component": "Row", "align": "center",
                    "children": ["gate-text"],
                    "styles": {
                      "backgroundColor": "#F1F5F9",
                      "borderRadius": 6,
                      "paddingLeft": 10, "paddingRight": 10,
                      "paddingTop": 3,   "paddingBottom": 3
                    }
                  },
                  {
                    "id": "gate-text", "component": "Text", "text": "登机口 B24",
                    "styles": { "color": "#334155", "fontSize": 12, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "seat-pill", "component": "Row", "align": "center",
                    "children": ["seat-text"],
                    "styles": {
                      "backgroundColor": "#EFF6FF",
                      "borderRadius": 6,
                      "paddingLeft": 10, "paddingRight": 10,
                      "paddingTop": 3,   "paddingBottom": 3
                    }
                  },
                  {
                    "id": "seat-text", "component": "Text", "text": "座位 32A · 靠窗",
                    "styles": { "color": "#1D4ED8", "fontSize": 12, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  }
                ]
              }
            }
        """

        // ==== Card kind 1 · 今日日程 (adapted from 03_calendar-day.json) ====
        //
        // 白底 + 左侧品牌色日期块 + 3 条事件 (色标+标题+时间) + 底部两枚按钮.
        // 关键差异 (vs. 源 JSON):
        //   - 去掉动态模板, 事件手写 3 条便于 native 端演示.
        //   - 日期改成 "12月28日 周日" 中文竖排, 左侧添加彩色 accent 竖条.
        //   - 每条事件左侧一个 8x8 圆角色标区分类型 (会议 / 沟通 / 生活).
        //   - 按钮做主次层次 (主按钮蓝底白字, 次按钮描边灰字), 但受制于
        //     A2UI Button 组件内部样式, 这里通过 Button 外自定义 label 文案.
        private const val CARD1_CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "native-calendar-card-%SEQ%" }
            }
        """
        private const val CARD1_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-calendar-card-%SEQ%",
                "components": [
                  {
                    "id": "root", "component": "Card", "child": "main",
                    "styles": {
                      "padding": 12,
                      "backgroundColor": "#FFFFFF",
                      "borderRadius": 14
                    }
                  },
                  {
                    "id": "main", "component": "Column", "align": "stretch",
                    "children": ["header", "divider", "actions"]
                  },

                  {
                    "id": "header", "component": "Row", "align": "start",
                    "children": ["date-block", "events-col"]
                  },
                  {
                    "id": "date-block", "component": "Row", "align": "stretch",
                    "children": ["accent-divider", "date-col"],
                    "styles": { "marginRight": 14 }
                  },
                  {
                    "id": "accent-divider", "component": "Divider", "axis": "vertical",
                    "styles": {
                      "backgroundColor": "#2563EB",
                      "width": 3,
                      "borderRadius": 2,
                      "marginLeft": 0,
                      "marginRight": 10,
                      "marginTop": 2,
                      "marginBottom": 2
                    }
                  },
                  {
                    "id": "date-col", "component": "Column", "align": "start",
                    "children": ["day-name", "day-number", "day-lunar"]
                  },
                  {
                    "id": "day-name", "component": "Text", "text": "周日",
                    "styles": {
                      "color": "#2563EB",
                      "fontSize": 12,
                      "fontWeight": "bold",
                      "letterSpacing": 1,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "day-number", "component": "Text", "text": "28",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 38,
                      "fontWeight": "bold",
                      "letterSpacing": -1,
                      "marginTop": 0,
                      "marginBottom": 0
                    }
                  },
                  {
                    "id": "day-lunar", "component": "Text", "text": "腊月初八",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "letterSpacing": 0.5, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "events-col", "component": "Column", "align": "stretch",
                    "children": ["event-1", "event-2", "event-3"],
                    "styles": { "flexGrow": 1 }
                  },
                  {
                    "id": "event-1", "component": "Row", "align": "center",
                    "children": ["dot-1", "event-1-col"],
                    "styles": { "marginBottom": 6 }
                  },
                  {
                    "id": "dot-1", "component": "Column", "weight": 0, "children": [],
                    "styles": {
                      "width": 8, "height": 8,
                      "backgroundColor": "#F59E0B",
                      "borderRadius": 4,
                      "marginRight": 10
                    }
                  },
                  {
                    "id": "event-1-col", "component": "Column", "align": "start",
                    "children": ["event-1-title", "event-1-time"]
                  },
                  {
                    "id": "event-1-title", "component": "Text", "text": "早餐 · 与设计对齐",
                    "styles": { "color": "#0F172A", "fontSize": 14, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "event-1-time", "component": "Text", "text": "08:30 – 09:15",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "event-2", "component": "Row", "align": "center",
                    "children": ["dot-2", "event-2-col"],
                    "styles": { "marginBottom": 6 }
                  },
                  {
                    "id": "dot-2", "component": "Column", "weight": 0, "children": [],
                    "styles": {
                      "width": 8, "height": 8,
                      "backgroundColor": "#2563EB",
                      "borderRadius": 4,
                      "marginRight": 10
                    }
                  },
                  {
                    "id": "event-2-col", "component": "Column", "align": "start",
                    "children": ["event-2-title", "event-2-time"]
                  },
                  {
                    "id": "event-2-title", "component": "Text", "text": "Q1 路线图评审",
                    "styles": { "color": "#0F172A", "fontSize": 14, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "event-2-time", "component": "Text", "text": "13:00 – 14:00 · 3 号会议室",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "event-3", "component": "Row", "align": "center",
                    "children": ["dot-3", "event-3-col"]
                  },
                  {
                    "id": "dot-3", "component": "Column", "weight": 0, "children": [],
                    "styles": {
                      "width": 8, "height": 8,
                      "backgroundColor": "#10B981",
                      "borderRadius": 4,
                      "marginRight": 10
                    }
                  },
                  {
                    "id": "event-3-col", "component": "Column", "align": "start",
                    "children": ["event-3-title", "event-3-time"]
                  },
                  {
                    "id": "event-3-title", "component": "Text", "text": "团队站会",
                    "styles": { "color": "#0F172A", "fontSize": 14, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "event-3-time", "component": "Text", "text": "15:30 – 16:00",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "divider", "component": "Divider",
                    "styles": { "marginTop": 8, "marginBottom": 6 }
                  },

                  {
                    "id": "actions", "component": "Row", "align": "center", "justify": "end",
                    "children": ["discard-btn", "add-btn"],
                    "styles": { "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "discard-btn-text", "component": "Text", "text": "忽略",
                    "styles": { "color": "#64748B", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "discard-btn", "component": "Button", "child": "discard-btn-text",
                    "action": {
                      "event": {
                        "name": "toast",
                        "context": {
                          "title": "已忽略",
                          "message": "该日程不会加入日历。"
                        }
                      }
                    },
                    "styles": {
                      "backgroundColor": "#F1F5F9",
                      "borderRadius": 8,
                      "paddingLeft": 14, "paddingRight": 14,
                      "paddingTop": 4, "paddingBottom": 4,
                      "marginTop": 0, "marginBottom": 0,
                      "marginRight": 8
                    }
                  },
                  {
                    "id": "add-btn-text", "component": "Text", "text": "添加到日历",
                    "styles": { "color": "#FFFFFF", "fontSize": 13, "fontWeight": "bold", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "add-btn", "component": "Button", "child": "add-btn-text",
                    "action": {
                      "event": {
                        "name": "toast",
                        "context": {
                          "title": "已添加",
                          "message": "3 项日程已同步到你的日历。"
                        }
                      }
                    },
                    "styles": {
                      "backgroundColor": "#2563EB",
                      "borderRadius": 8,
                      "paddingLeft": 14, "paddingRight": 14,
                      "paddingTop": 4, "paddingBottom": 4,
                      "marginTop": 0, "marginBottom": 0
                    }
                  }
                ]
              }
            }
        """

        // ==== Card kind 2 · 天气 (adapted from 04_weather-current.json) ====
        //
        // 白底 + 顶部主温度大字 + 副信息 + 5 天预报一行 (今日列高亮).
        // 关键差异 (vs. 源 JSON):
        //   - 去掉 formatString/formatDate, 数据手写便于 native 端演示.
        //   - 主温度 15° 与最低温组合, 用不同 fontSize + 竖直基线错位模拟"°"上标.
        //   - 5 天预报: 今日列做浅蓝底 + 圆角强调; 其余列透明底.
        //   - 位置和描述用清亮小字, 避免和主温度抢重点.
        private const val CARD2_CREATE_SURFACE_JSON = """
            {
              "version": "v0.9",
              "createSurface": { "surfaceId": "native-weather-card-%SEQ%" }
            }
        """
        private const val CARD2_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-weather-card-%SEQ%",
                "components": [
                  {
                    "id": "root", "component": "Card", "child": "main",
                    "styles": {
                      "padding": 12,
                      "backgroundColor": "#FFFFFF",
                      "borderRadius": 14
                    }
                  },
                  {
                    "id": "main", "component": "Column", "align": "stretch",
                    "children": ["header-row", "temp-row", "desc-row", "forecast-row"]
                  },

                  {
                    "id": "header-row", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["loc-col", "sun-emoji"]
                  },
                  {
                    "id": "loc-col", "component": "Column", "align": "start",
                    "children": ["location", "location-sub"]
                  },
                  {
                    "id": "location", "component": "Text", "text": "深圳 · 南山区",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 16,
                      "fontWeight": "bold",
                      "letterSpacing": 0.5,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "location-sub", "component": "Text", "text": "更新于 10 分钟前",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "marginTop": 2, "marginBottom": 0 }
                  },
                  {
                    "id": "sun-emoji", "component": "Text", "text": "🌤️",
                    "styles": { "fontSize": 32, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "temp-row", "component": "Row", "align": "start",
                    "children": ["temp-main", "temp-unit", "temp-low-wrap"],
                    "styles": { "marginTop": 2 }
                  },
                  {
                    "id": "temp-main", "component": "Text", "text": "15",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 64,
                      "fontWeight": "light",
                      "letterSpacing": -2,
                      "marginTop": 0, "marginBottom": 0
                    }
                  },
                  {
                    "id": "temp-unit", "component": "Text", "text": "°",
                    "styles": {
                      "color": "#0F172A",
                      "fontSize": 40,
                      "fontWeight": "light",
                      "marginTop": 6, "marginBottom": 0
                    }
                  },
                  {
                    "id": "temp-low-wrap", "component": "Column", "align": "start",
                    "children": ["temp-low-label", "temp-low-value"],
                    "styles": { "marginLeft": 14, "marginTop": 10 }
                  },
                  {
                    "id": "temp-low-label", "component": "Text", "text": "最低",
                    "styles": { "color": "#94A3B8", "fontSize": 11, "letterSpacing": 1, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "temp-low-value", "component": "Text", "text": "8°",
                    "styles": {
                      "color": "#334155",
                      "fontSize": 18,
                      "fontWeight": "medium",
                      "marginTop": 0, "marginBottom": 0
                    }
                  },

                  {
                    "id": "desc-row", "component": "Row", "align": "center",
                    "children": ["desc-text", "desc-sep", "wind-text"],
                    "styles": { "marginTop": 2, "marginBottom": 8 }
                  },
                  {
                    "id": "desc-text", "component": "Text", "text": "多云转晴",
                    "styles": { "color": "#334155", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "desc-sep", "component": "Text", "text": " · ",
                    "styles": { "color": "#CBD5E1", "fontSize": 13, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "wind-text", "component": "Text", "text": "东南风 3 级",
                    "styles": { "color": "#64748B", "fontSize": 13, "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "forecast-row", "component": "Row", "align": "center", "justify": "spaceBetween",
                    "children": ["day-today", "day-tue", "day-wed", "day-thu", "day-fri"],
                    "styles": {
                      "marginTop": 6
                    }
                  },

                  {
                    "id": "day-today", "component": "Column", "align": "center",
                    "children": ["today-name", "today-icon", "today-temp"],
                    "styles": {
                      "backgroundColor": "#EFF6FF",
                      "borderRadius": 10,
                      "paddingLeft": 8, "paddingRight": 8,
                      "paddingTop": 4,  "paddingBottom": 4
                    }
                  },
                  {
                    "id": "today-name", "component": "Text", "text": "今日",
                    "styles": { "color": "#1D4ED8", "fontSize": 11, "fontWeight": "bold", "letterSpacing": 0.5, "marginTop": 0, "marginBottom": 0 }
                  },
                  {
                    "id": "today-icon", "component": "Text", "text": "🌤️",
                    "styles": { "fontSize": 20, "marginTop": 2, "marginBottom": 2 }
                  },
                  {
                    "id": "today-temp", "component": "Text", "text": "15°",
                    "styles": { "color": "#1D4ED8", "fontSize": 13, "fontWeight": "bold", "marginTop": 0, "marginBottom": 0 }
                  },

                  {
                    "id": "day-tue", "component": "Column", "align": "center",
                    "children": ["tue-name", "tue-icon", "tue-temp"]
                  },
                  { "id": "tue-name", "component": "Text", "text": "周二", "styles": { "color": "#64748B", "fontSize": 11, "marginTop": 0, "marginBottom": 0 } },
                  { "id": "tue-icon", "component": "Text", "text": "☀️",   "styles": { "fontSize": 20, "marginTop": 2, "marginBottom": 2 } },
                  { "id": "tue-temp", "component": "Text", "text": "17°", "styles": { "color": "#0F172A", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 } },

                  {
                    "id": "day-wed", "component": "Column", "align": "center",
                    "children": ["wed-name", "wed-icon", "wed-temp"]
                  },
                  { "id": "wed-name", "component": "Text", "text": "周三", "styles": { "color": "#64748B", "fontSize": 11, "marginTop": 0, "marginBottom": 0 } },
                  { "id": "wed-icon", "component": "Text", "text": "🌧️",   "styles": { "fontSize": 20, "marginTop": 2, "marginBottom": 2 } },
                  { "id": "wed-temp", "component": "Text", "text": "12°", "styles": { "color": "#0F172A", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 } },

                  {
                    "id": "day-thu", "component": "Column", "align": "center",
                    "children": ["thu-name", "thu-icon", "thu-temp"]
                  },
                  { "id": "thu-name", "component": "Text", "text": "周四", "styles": { "color": "#64748B", "fontSize": 11, "marginTop": 0, "marginBottom": 0 } },
                  { "id": "thu-icon", "component": "Text", "text": "⛅",   "styles": { "fontSize": 20, "marginTop": 2, "marginBottom": 2 } },
                  { "id": "thu-temp", "component": "Text", "text": "14°", "styles": { "color": "#0F172A", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 } },

                  {
                    "id": "day-fri", "component": "Column", "align": "center",
                    "children": ["fri-name", "fri-icon", "fri-temp"]
                  },
                  { "id": "fri-name", "component": "Text", "text": "周五", "styles": { "color": "#64748B", "fontSize": 11, "marginTop": 0, "marginBottom": 0 } },
                  { "id": "fri-icon", "component": "Text", "text": "☀️",   "styles": { "fontSize": 20, "marginTop": 2, "marginBottom": 2 } },
                  { "id": "fri-temp", "component": "Text", "text": "18°", "styles": { "color": "#0F172A", "fontSize": 13, "fontWeight": "medium", "marginTop": 0, "marginBottom": 0 } }
                ]
              }
            }
        """

        // ==== Card kind 3 · 商品 (adapted from 05_product-card.json) ====
        //
        // 白底 + 顶部彩色渐变图占位 + 商品名 + 价格/库存 + 加入购物车按钮.
        private const val CARD3_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-product-card-%SEQ%"}}
        """
        private const val CARD3_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-product-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":0,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["cover","content"] },
                  { "id":"cover","component":"Row","align":"center","justify":"center","children":["cover-emoji"],
                    "styles":{"height":110,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#FDE68A","stop":0.0},{"color":"#F59E0B","stop":1.0}]}} },
                  { "id":"cover-emoji","component":"Text","text":"🎧","styles":{"fontSize":56,"marginTop":0,"marginBottom":0} },
                  { "id":"content","component":"Column","align":"stretch","children":["tag-row","title","subtitle","price-row","cart-btn"],
                    "styles":{"paddingLeft":14,"paddingRight":14,"paddingTop":10,"paddingBottom":12} },
                  { "id":"tag-row","component":"Row","align":"center","children":["tag-new","tag-hot"] },
                  { "id":"tag-new","component":"Text","text":"新品",
                    "styles":{"color":"#166534","fontSize":11,"fontWeight":"bold","backgroundColor":"#DCFCE7","borderRadius":4,"paddingLeft":6,"paddingRight":6,"paddingTop":2,"paddingBottom":2,"marginRight":6,"marginTop":0,"marginBottom":0} },
                  { "id":"tag-hot","component":"Text","text":"畅销",
                    "styles":{"color":"#9A3412","fontSize":11,"fontWeight":"bold","backgroundColor":"#FFEDD5","borderRadius":4,"paddingLeft":6,"paddingRight":6,"paddingTop":2,"paddingBottom":2,"marginTop":0,"marginBottom":0} },
                  { "id":"title","component":"Text","text":"XxxWave Pro 无线降噪耳机",
                    "styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","letterSpacing":0.3,"marginTop":6,"marginBottom":0} },
                  { "id":"subtitle","component":"Text","text":"40 小时续航 · 空间音频 · 极致降噪",
                    "styles":{"color":"#64748B","fontSize":12,"marginTop":2,"marginBottom":0} },
                  { "id":"price-row","component":"Row","align":"center","justify":"spaceBetween","children":["price-col","stock-text"],
                    "styles":{"marginTop":8,"marginBottom":8} },
                  { "id":"price-col","component":"Row","align":"end","children":["price-cur","price-num","price-old"] },
                  { "id":"price-cur","component":"Text","text":"¥","styles":{"color":"#DC2626","fontSize":14,"fontWeight":"bold","marginTop":0,"marginBottom":3,"marginRight":2} },
                  { "id":"price-num","component":"Text","text":"899","styles":{"color":"#DC2626","fontSize":24,"fontWeight":"bold","letterSpacing":-0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"price-old","component":"Text","text":"¥1299","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":4,"marginLeft":8,"textDecoration":"line-through"} },
                  { "id":"stock-text","component":"Text","text":"仅剩 12 件","styles":{"color":"#EA580C","fontSize":12,"fontWeight":"medium","marginTop":0,"marginBottom":0} },
                  { "id":"cart-btn-text","component":"Text","text":"加入购物车","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"cart-btn","component":"Button","child":"cart-btn-text",
"action":{"event":{"name":"toast","context":{"title":"已加入购物车","message":"XxxWave Pro 无线降噪耳机 × 1"}}},
                    "styles":{"backgroundColor":"#DC2626","borderRadius":10,"paddingLeft":14,"paddingRight":14,"paddingTop":8,"paddingBottom":8,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 4 · 音乐播放 (adapted from 06_music-player.json) ====
        private const val CARD4_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-music-card-%SEQ%"}}
        """
        private const val CARD4_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-music-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#0F172A","borderRadius":14} },
                  { "id":"main","component":"Row","align":"center","children":["cover","info-col"] },
                  { "id":"cover","component":"Row","align":"center","justify":"center","children":["cover-emoji"],
                    "styles":{"width":72,"height":72,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#7C3AED","stop":0.0},{"color":"#EC4899","stop":1.0}]},"borderRadius":10,"marginRight":12} },
                  { "id":"cover-emoji","component":"Text","text":"🎵","styles":{"fontSize":32,"marginTop":0,"marginBottom":0} },
                  { "id":"info-col","component":"Column","align":"stretch","children":["title","artist","progress-row","controls-row"],"styles":{"flexGrow":1} },
                  { "id":"title","component":"Text","text":"夜航之歌","styles":{"color":"#F8FAFC","fontSize":16,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"artist","component":"Text","text":"星野 · 单曲","styles":{"color":"#94A3B8","fontSize":12,"marginTop":2,"marginBottom":0} },
                  { "id":"progress-row","component":"Row","align":"center","children":["cur-time","progress-bar","total-time"],"styles":{"marginTop":8,"marginBottom":0} },
                  { "id":"cur-time","component":"Text","text":"01:23","styles":{"color":"#94A3B8","fontSize":10,"marginTop":0,"marginBottom":0,"marginRight":8} },
                  { "id":"progress-bar","component":"Row","align":"center","children":["progress-fill","progress-track"],"styles":{"height":3,"backgroundColor":"#1E293B","borderRadius":2,"flexGrow":1} },
                  { "id":"progress-fill","component":"Column","weight":0,"children":[],"styles":{"width":90,"height":3,"backgroundColor":"#EC4899","borderRadius":2} },
                  { "id":"progress-track","component":"Column","weight":0,"children":[],"styles":{"width":0,"height":3} },
                  { "id":"total-time","component":"Text","text":"03:42","styles":{"color":"#94A3B8","fontSize":10,"marginTop":0,"marginBottom":0,"marginLeft":8} },
                  { "id":"controls-row","component":"Row","align":"center","justify":"spaceBetween","children":["btn-prev","btn-play","btn-next"],"styles":{"marginTop":10,"marginBottom":0} },
                  { "id":"btn-prev-text","component":"Text","text":"⏮","styles":{"color":"#F8FAFC","fontSize":18,"marginTop":0,"marginBottom":0} },
                  { "id":"btn-prev","component":"Button","child":"btn-prev-text",
"action":{"event":{"name":"toast","context":{"title":"上一曲","message":"已切换到上一曲。"}}},
                    "styles":{"backgroundColor":"#00000000","paddingLeft":4,"paddingRight":4,"paddingTop":2,"paddingBottom":2,"marginTop":0,"marginBottom":0} },
                  { "id":"btn-play-text","component":"Text","text":"▶","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"btn-play","component":"Button","child":"btn-play-text",
"action":{"event":{"name":"toast","context":{"title":"播放中","message":"夜航之歌 · 星野"}}},
                    "styles":{"backgroundColor":"#F8FAFC","borderRadius":100,"paddingLeft":14,"paddingRight":14,"paddingTop":6,"paddingBottom":6,"marginTop":0,"marginBottom":0} },
                  { "id":"btn-next-text","component":"Text","text":"⏭","styles":{"color":"#F8FAFC","fontSize":18,"marginTop":0,"marginBottom":0} },
                  { "id":"btn-next","component":"Button","child":"btn-next-text",
"action":{"event":{"name":"toast","context":{"title":"下一曲","message":"已切换到下一曲。"}}},
                    "styles":{"backgroundColor":"#00000000","paddingLeft":4,"paddingRight":4,"paddingTop":2,"paddingBottom":2,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 5 · 任务 (adapted from 07_task-card.json) ====
        private const val CARD5_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-task-card-%SEQ%"}}
        """
        private const val CARD5_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-task-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":12,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Row","align":"center","children":["check-box","content-col","priority-pill"] },
                  { "id":"check-box","component":"Row","align":"center","justify":"center","weight":0,"children":["check-mark"],
                    "styles":{"width":22,"height":22,"borderRadius":6,"backgroundColor":"#2563EB","marginRight":10} },
                  { "id":"check-mark","component":"Text","text":"✓","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"content-col","component":"Column","align":"start","children":["task-title","task-meta"],"styles":{"flexGrow":1} },
                  { "id":"task-title","component":"Text","text":"完成 Q1 季度汇报 PPT","styles":{"color":"#0F172A","fontSize":15,"fontWeight":"medium","marginTop":0,"marginBottom":0} },
                  { "id":"task-meta","component":"Text","text":"截止 · 明天 18:00 · 项目组","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"priority-pill","component":"Row","weight":0,"align":"center","children":["priority-text"],
                    "styles":{"backgroundColor":"#FEE2E2","borderRadius":8,"paddingLeft":8,"paddingRight":8,"paddingTop":3,"paddingBottom":3,"marginLeft":8} },
                  { "id":"priority-text","component":"Text","text":"高优","styles":{"color":"#B91C1C","fontSize":11,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 6 · 个人资料 (adapted from 08_user-profile.json) ====
        private const val CARD6_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-profile-card-%SEQ%"}}
        """
        private const val CARD6_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-profile-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":0,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["banner","body"] },
                  { "id":"banner","component":"Row","align":"center","justify":"center","children":["avatar-wrap"],
                    "styles":{"height":78,"backgroundLinearGradient":{"direction":"to-right","colorStops":[{"color":"#6366F1","stop":0.0},{"color":"#8B5CF6","stop":1.0}]}} },
                  { "id":"avatar-wrap","component":"Row","align":"center","justify":"center","children":["avatar-text"],
                    "styles":{"width":64,"height":64,"backgroundColor":"#FFFFFF","borderRadius":100,"marginTop":36} },
                  { "id":"avatar-text","component":"Text","text":"🐼","styles":{"fontSize":32,"marginTop":0,"marginBottom":0} },
                  { "id":"body","component":"Column","align":"center","children":["name","handle","bio","stats-row","follow-btn"],
                    "styles":{"paddingLeft":16,"paddingRight":16,"paddingTop":38,"paddingBottom":14} },
                  { "id":"name","component":"Text","text":"林明明","styles":{"color":"#0F172A","fontSize":18,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"handle","component":"Text","text":"@mingming · 产品设计师","styles":{"color":"#64748B","fontSize":12,"marginTop":2,"marginBottom":0} },
                  { "id":"bio","component":"Text","text":"专注移动端交互体验，探索 AI 与工具的边界。","styles":{"color":"#475569","fontSize":12,"marginTop":8,"marginBottom":0,"textAlign":"center"} },
                  { "id":"stats-row","component":"Row","align":"center","justify":"spaceBetween","children":["stat-1","stat-2","stat-3"],"styles":{"marginTop":12,"marginBottom":10} },
                  { "id":"stat-1","component":"Column","align":"center","children":["stat-1-num","stat-1-lbl"] },
                  { "id":"stat-1-num","component":"Text","text":"1.2k","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"stat-1-lbl","component":"Text","text":"作品","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"stat-2","component":"Column","align":"center","children":["stat-2-num","stat-2-lbl"] },
                  { "id":"stat-2-num","component":"Text","text":"38.5k","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"stat-2-lbl","component":"Text","text":"粉丝","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"stat-3","component":"Column","align":"center","children":["stat-3-num","stat-3-lbl"] },
                  { "id":"stat-3-num","component":"Text","text":"286","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"stat-3-lbl","component":"Text","text":"关注","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"follow-btn-text","component":"Text","text":"+ 关注","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"follow-btn","component":"Button","child":"follow-btn-text",
"action":{"event":{"name":"toast","context":{"title":"已关注","message":"你已关注 @mingming。"}}},
                    "styles":{"backgroundColor":"#6366F1","borderRadius":100,"paddingLeft":22,"paddingRight":22,"paddingTop":7,"paddingBottom":7,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 7 · 订单完成 (adapted from 11_purchase-complete.json) ====
        private const val CARD7_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-purchase-card-%SEQ%"}}
        """
        private const val CARD7_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-purchase-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":16,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"center","children":["icon-wrap","title","subtitle","summary","view-btn"] },
                  { "id":"icon-wrap","component":"Row","align":"center","justify":"center","children":["icon-check"],
                    "styles":{"width":52,"height":52,"backgroundColor":"#DCFCE7","borderRadius":100,"marginTop":0,"marginBottom":10} },
                  { "id":"icon-check","component":"Text","text":"✓","styles":{"color":"#16A34A","fontSize":30,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"title","component":"Text","text":"支付成功","styles":{"color":"#0F172A","fontSize":19,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"subtitle","component":"Text","text":"感谢您的购买，订单已提交","styles":{"color":"#64748B","fontSize":12,"marginTop":4,"marginBottom":10,"textAlign":"center"} },
                  { "id":"summary","component":"Column","align":"stretch","children":["row-amount","row-order","row-time"],
                    "styles":{"backgroundColor":"#F8FAFC","borderRadius":10,"paddingLeft":12,"paddingRight":12,"paddingTop":10,"paddingBottom":10,"marginTop":0,"marginBottom":12} },
                  { "id":"row-amount","component":"Row","align":"center","justify":"spaceBetween","children":["lbl-amount","val-amount"] },
                  { "id":"lbl-amount","component":"Text","text":"支付金额","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"val-amount","component":"Text","text":"¥ 328.00","styles":{"color":"#0F172A","fontSize":14,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"row-order","component":"Row","align":"center","justify":"spaceBetween","children":["lbl-order","val-order"],"styles":{"marginTop":6,"marginBottom":0} },
                  { "id":"lbl-order","component":"Text","text":"订单号","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"val-order","component":"Text","text":"20260909·A17F92","styles":{"color":"#334155","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"row-time","component":"Row","align":"center","justify":"spaceBetween","children":["lbl-time","val-time"],"styles":{"marginTop":6,"marginBottom":0} },
                  { "id":"lbl-time","component":"Text","text":"支付时间","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"val-time","component":"Text","text":"09-09 12:18","styles":{"color":"#334155","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"view-btn-text","component":"Text","text":"查看订单详情","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"view-btn","component":"Button","child":"view-btn-text",
"action":{"event":{"name":"toast","context":{"title":"订单详情","message":"20260909·A17F92 · ¥ 328.00"}}},
                    "styles":{"backgroundColor":"#16A34A","borderRadius":10,"paddingLeft":22,"paddingRight":22,"paddingTop":8,"paddingBottom":8,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 8 · 聊天消息 (adapted from 12_chat-message.json) ====
        private const val CARD8_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-chat-card-%SEQ%"}}
        """
        private const val CARD8_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-chat-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":12,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["msg-in","msg-out","typing-row"] },
                  { "id":"msg-in","component":"Row","align":"start","children":["avatar-in","bubble-in-wrap"],"styles":{"marginTop":0,"marginBottom":8} },
                  { "id":"avatar-in","component":"Row","weight":0,"align":"center","justify":"center","children":["avatar-in-text"],
                    "styles":{"width":28,"height":28,"backgroundColor":"#F1F5F9","borderRadius":100,"marginRight":8} },
                  { "id":"avatar-in-text","component":"Text","text":"🧑‍💻","styles":{"fontSize":14,"marginTop":0,"marginBottom":0} },
                  { "id":"bubble-in-wrap","component":"Column","align":"start","children":["name-in","bubble-in","time-in"] },
                  { "id":"name-in","component":"Text","text":"陈文 · 产品","styles":{"color":"#64748B","fontSize":11,"marginTop":0,"marginBottom":2} },
                  { "id":"bubble-in","component":"Row","align":"center","children":["bubble-in-text"],
                    "styles":{"backgroundColor":"#F1F5F9","borderRadius":12,"paddingLeft":10,"paddingRight":10,"paddingTop":6,"paddingBottom":6} },
                  { "id":"bubble-in-text","component":"Text","text":"新版本原型评审时间调整到明天 10 点，方便吗？","styles":{"color":"#0F172A","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"time-in","component":"Text","text":"12:16","styles":{"color":"#94A3B8","fontSize":10,"marginTop":2,"marginBottom":0} },
                  { "id":"msg-out","component":"Row","align":"start","justify":"end","children":["bubble-out-wrap"],"styles":{"marginTop":0,"marginBottom":6} },
                  { "id":"bubble-out-wrap","component":"Column","align":"end","children":["bubble-out","time-out"] },
                  { "id":"bubble-out","component":"Row","align":"center","children":["bubble-out-text"],
                    "styles":{"backgroundColor":"#2563EB","borderRadius":12,"paddingLeft":10,"paddingRight":10,"paddingTop":6,"paddingBottom":6} },
                  { "id":"bubble-out-text","component":"Text","text":"可以，我把评审材料提前发到群里。","styles":{"color":"#FFFFFF","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"time-out","component":"Text","text":"12:17 · 已读","styles":{"color":"#94A3B8","fontSize":10,"marginTop":2,"marginBottom":0} },
                  { "id":"typing-row","component":"Row","align":"center","children":["typing-dot","typing-text"],"styles":{"marginTop":2,"marginBottom":0} },
                  { "id":"typing-dot","component":"Column","weight":0,"children":[],"styles":{"width":6,"height":6,"borderRadius":3,"backgroundColor":"#10B981","marginRight":6} },
                  { "id":"typing-text","component":"Text","text":"陈文正在输入…","styles":{"color":"#64748B","fontSize":11,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 9 · 咖啡订单 (adapted from 13_coffee-order.json) ====
        private const val CARD9_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-coffee-card-%SEQ%"}}
        """
        private const val CARD9_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-coffee-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["header","items","divider","totals","pay-btn"] },
                  { "id":"header","component":"Row","align":"center","children":["shop-emoji","shop-col"] },
                  { "id":"shop-emoji","component":"Text","text":"☕","styles":{"fontSize":22,"marginTop":0,"marginBottom":0,"marginRight":10} },
                  { "id":"shop-col","component":"Column","align":"start","children":["shop-name","shop-meta"] },
                  { "id":"shop-name","component":"Text","text":"北山咖啡 · 外带","styles":{"color":"#0F172A","fontSize":15,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"shop-meta","component":"Text","text":"订单 #2451 · 预计 12:30 完成","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"items","component":"Column","align":"stretch","children":["item-1","item-2","item-3"],"styles":{"marginTop":10,"marginBottom":0} },
                  { "id":"item-1","component":"Row","align":"center","justify":"spaceBetween","children":["item-1-name","item-1-price"],"styles":{"marginTop":0,"marginBottom":4} },
                  { "id":"item-1-name","component":"Text","text":"燕麦拿铁 (大) × 1","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"item-1-price","component":"Text","text":"¥ 32","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"item-2","component":"Row","align":"center","justify":"spaceBetween","children":["item-2-name","item-2-price"],"styles":{"marginTop":0,"marginBottom":4} },
                  { "id":"item-2-name","component":"Text","text":"美式 (中·冰) × 1","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"item-2-price","component":"Text","text":"¥ 22","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"item-3","component":"Row","align":"center","justify":"spaceBetween","children":["item-3-name","item-3-price"],"styles":{"marginTop":0,"marginBottom":0} },
                  { "id":"item-3-name","component":"Text","text":"手工可颂 × 2","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"item-3-price","component":"Text","text":"¥ 28","styles":{"color":"#334155","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"divider","component":"Divider","styles":{"marginTop":8,"marginBottom":8} },
                  { "id":"totals","component":"Row","align":"center","justify":"spaceBetween","children":["total-lbl","total-val"],"styles":{"marginTop":0,"marginBottom":10} },
                  { "id":"total-lbl","component":"Text","text":"合计","styles":{"color":"#64748B","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"total-val","component":"Text","text":"¥ 82.00","styles":{"color":"#0F172A","fontSize":17,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"pay-btn-text","component":"Text","text":"立即支付","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"pay-btn","component":"Button","child":"pay-btn-text",
"action":{"event":{"name":"toast","context":{"title":"支付成功","message":"订单 #2451 · ¥ 82.00 已支付"}}},
                    "styles":{"backgroundColor":"#78350F","borderRadius":10,"paddingLeft":14,"paddingRight":14,"paddingTop":8,"paddingBottom":8,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 10 · 球员数据 (adapted from 14_sports-player.json) ====
        private const val CARD10_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-player-card-%SEQ%"}}
        """
        private const val CARD10_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-player-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":0,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["hero","stats"] },
                  { "id":"hero","component":"Row","align":"center","children":["num-block","name-col"],
                    "styles":{"paddingLeft":16,"paddingRight":16,"paddingTop":12,"paddingBottom":12,"backgroundLinearGradient":{"direction":"to-right","colorStops":[{"color":"#1E3A8A","stop":0.0},{"color":"#3730A3","stop":1.0}]}} },
                  { "id":"num-block","component":"Column","weight":0,"align":"center","children":["num-text"],
                    "styles":{"width":56,"height":56,"backgroundColor":"#FBBF24","borderRadius":12,"marginRight":12} },
                  { "id":"num-text","component":"Text","text":"23","styles":{"color":"#1E3A8A","fontSize":30,"fontWeight":"bold","letterSpacing":-1,"marginTop":10,"marginBottom":0} },
                  { "id":"name-col","component":"Column","align":"start","children":["player-name","player-meta"] },
                  { "id":"player-name","component":"Text","text":"叶小明","styles":{"color":"#FFFFFF","fontSize":20,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"player-meta","component":"Text","text":"小前锋 · 星辰队","styles":{"color":"#C7D2FE","fontSize":12,"marginTop":2,"marginBottom":0} },
                  { "id":"stats","component":"Row","align":"center","justify":"spaceBetween","children":["s1","s2","s3","s4"],
                    "styles":{"paddingLeft":12,"paddingRight":12,"paddingTop":12,"paddingBottom":12} },
                  { "id":"s1","component":"Column","align":"center","children":["s1-num","s1-lbl"] },
                  { "id":"s1-num","component":"Text","text":"28.4","styles":{"color":"#0F172A","fontSize":19,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"s1-lbl","component":"Text","text":"得分","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":0.5,"marginTop":2,"marginBottom":0} },
                  { "id":"s2","component":"Column","align":"center","children":["s2-num","s2-lbl"] },
                  { "id":"s2-num","component":"Text","text":"7.8","styles":{"color":"#0F172A","fontSize":19,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"s2-lbl","component":"Text","text":"篮板","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":0.5,"marginTop":2,"marginBottom":0} },
                  { "id":"s3","component":"Column","align":"center","children":["s3-num","s3-lbl"] },
                  { "id":"s3-num","component":"Text","text":"6.2","styles":{"color":"#0F172A","fontSize":19,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"s3-lbl","component":"Text","text":"助攻","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":0.5,"marginTop":2,"marginBottom":0} },
                  { "id":"s4","component":"Column","align":"center","children":["s4-num","s4-lbl"] },
                  { "id":"s4-num","component":"Text","text":"48.2%","styles":{"color":"#0F172A","fontSize":19,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"s4-lbl","component":"Text","text":"命中率","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":0.5,"marginTop":2,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 11 · 账户余额 (adapted from 15_account-balance.json) ====
        private const val CARD11_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-balance-card-%SEQ%"}}
        """
        private const val CARD11_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-balance-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#0F172A","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["header","balance-row","updated","actions"] },
                  { "id":"header","component":"Row","align":"center","justify":"spaceBetween","children":["header-lbl","header-eye"] },
                  { "id":"header-lbl","component":"Text","text":"钱包总资产","styles":{"color":"#94A3B8","fontSize":12,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"header-eye","component":"Text","text":"👁","styles":{"color":"#94A3B8","fontSize":14,"marginTop":0,"marginBottom":0} },
                  { "id":"balance-row","component":"Row","align":"end","children":["balance-cur","balance-num","balance-decimal"],"styles":{"marginTop":8,"marginBottom":0} },
                  { "id":"balance-cur","component":"Text","text":"¥","styles":{"color":"#F8FAFC","fontSize":18,"fontWeight":"bold","marginTop":0,"marginBottom":6,"marginRight":4} },
                  { "id":"balance-num","component":"Text","text":"12,486","styles":{"color":"#F8FAFC","fontSize":34,"fontWeight":"bold","letterSpacing":-1,"marginTop":0,"marginBottom":0} },
                  { "id":"balance-decimal","component":"Text","text":".52","styles":{"color":"#94A3B8","fontSize":18,"fontWeight":"medium","marginTop":0,"marginBottom":5,"marginLeft":2} },
                  { "id":"updated","component":"Text","text":"较昨日 +¥ 84.30 · 更新于 5 分钟前","styles":{"color":"#10B981","fontSize":11,"marginTop":4,"marginBottom":10} },
                  { "id":"actions","component":"Row","align":"center","justify":"spaceBetween","children":["btn-in","btn-out"] },
                  { "id":"btn-in-text","component":"Text","text":"充值","styles":{"color":"#0F172A","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"btn-in","component":"Button","child":"btn-in-text",
"action":{"event":{"name":"toast","context":{"title":"充值","message":"请选择充值金额。"}}},
                    "styles":{"backgroundColor":"#FBBF24","borderRadius":10,"paddingLeft":22,"paddingRight":22,"paddingTop":7,"paddingBottom":7,"marginTop":0,"marginBottom":0,"marginRight":8,"flexGrow":1} },
                  { "id":"btn-out-text","component":"Text","text":"提现","styles":{"color":"#F8FAFC","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"btn-out","component":"Button","child":"btn-out-text",
"action":{"event":{"name":"toast","context":{"title":"提现","message":"提现将在 1-3 个工作日到账。"}}},
                    "styles":{"backgroundColor":"#1E293B","borderRadius":10,"paddingLeft":22,"paddingRight":22,"paddingTop":7,"paddingBottom":7,"marginTop":0,"marginBottom":0,"flexGrow":1} }
                ]
              }
            }
        """

        // ==== Card kind 12 · 运动总结 (adapted from 16_workout-summary.json) ====
        private const val CARD12_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-workout-card-%SEQ%"}}
        """
        private const val CARD12_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-workout-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["header","divider","metrics","date-text"] },
                  { "id":"header","component":"Row","align":"center","children":["head-emoji","head-col"] },
                  { "id":"head-emoji","component":"Text","text":"🏃","styles":{"fontSize":26,"marginTop":0,"marginBottom":0,"marginRight":10} },
                  { "id":"head-col","component":"Column","align":"start","children":["head-title","head-sub"] },
                  { "id":"head-title","component":"Text","text":"晨间跑步","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"head-sub","component":"Text","text":"沙河绿道 · 心率区间 3","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"divider","component":"Divider","styles":{"marginTop":10,"marginBottom":10} },
                  { "id":"metrics","component":"Row","align":"center","justify":"spaceBetween","children":["m1","m2","m3"] },
                  { "id":"m1","component":"Column","align":"center","children":["m1-num","m1-lbl"] },
                  { "id":"m1-num","component":"Text","text":"6.28","styles":{"color":"#10B981","fontSize":22,"fontWeight":"bold","letterSpacing":-0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"m1-lbl","component":"Text","text":"公里","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"m2","component":"Column","align":"center","children":["m2-num","m2-lbl"] },
                  { "id":"m2-num","component":"Text","text":"38'12″","styles":{"color":"#10B981","fontSize":22,"fontWeight":"bold","letterSpacing":-0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"m2-lbl","component":"Text","text":"时长","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"m3","component":"Column","align":"center","children":["m3-num","m3-lbl"] },
                  { "id":"m3-num","component":"Text","text":"482","styles":{"color":"#10B981","fontSize":22,"fontWeight":"bold","letterSpacing":-0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"m3-lbl","component":"Text","text":"千卡","styles":{"color":"#94A3B8","fontSize":11,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"date-text","component":"Text","text":"09 月 09 日 06:18 · 天气晴 · 22°","styles":{"color":"#64748B","fontSize":11,"marginTop":10,"marginBottom":0,"textAlign":"center"} }
                ]
              }
            }
        """

        // ==== Card kind 13 · 餐厅 (adapted from 20_restaurant-card.json) ====
        private const val CARD13_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-restaurant-card-%SEQ%"}}
        """
        private const val CARD13_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-restaurant-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":0,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["cover","body"] },
                  { "id":"cover","component":"Row","align":"end","justify":"end","children":["open-pill"],
                    "styles":{"height":86,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#FCA5A5","stop":0.0},{"color":"#DC2626","stop":1.0}]},"paddingLeft":10,"paddingRight":10,"paddingTop":10,"paddingBottom":10} },
                  { "id":"open-pill","component":"Row","align":"center","children":["open-text"],
                    "styles":{"backgroundColor":"#FFFFFF","borderRadius":100,"paddingLeft":8,"paddingRight":8,"paddingTop":2,"paddingBottom":2} },
                  { "id":"open-text","component":"Text","text":"营业中","styles":{"color":"#16A34A","fontSize":11,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"body","component":"Column","align":"stretch","children":["rest-name","rest-cat","meta-row","addr-text"],
                    "styles":{"paddingLeft":14,"paddingRight":14,"paddingTop":10,"paddingBottom":12} },
                  { "id":"rest-name","component":"Text","text":"好吃火锅店","styles":{"color":"#0F172A","fontSize":17,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"rest-cat","component":"Text","text":"中餐 · 火锅 · ¥¥¥","styles":{"color":"#64748B","fontSize":12,"marginTop":2,"marginBottom":0} },
                  { "id":"meta-row","component":"Row","align":"center","children":["star-text","rate-text","dot-sep","dist-text"],"styles":{"marginTop":8,"marginBottom":0} },
                  { "id":"star-text","component":"Text","text":"★","styles":{"color":"#F59E0B","fontSize":13,"marginTop":0,"marginBottom":0,"marginRight":2} },
                  { "id":"rate-text","component":"Text","text":"4.7","styles":{"color":"#0F172A","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"dot-sep","component":"Text","text":" · ","styles":{"color":"#CBD5E1","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"dist-text","component":"Text","text":"1.2 km · 步行 15 分钟","styles":{"color":"#64748B","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"addr-text","component":"Text","text":"📍 沙河路 128 号 · 山水人家 3 楼","styles":{"color":"#94A3B8","fontSize":11,"marginTop":6,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 14 · 物流 (adapted from 21_shipping-status.json) ====
        private const val CARD14_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-shipping-card-%SEQ%"}}
        """
        private const val CARD14_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-shipping-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"stretch","children":["header","tracking","divider","steps","eta"] },
                  { "id":"header","component":"Row","align":"center","justify":"spaceBetween","children":["header-lbl","stage-pill"] },
                  { "id":"header-lbl","component":"Text","text":"📦 快鸟速运","styles":{"color":"#0F172A","fontSize":15,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"stage-pill","component":"Row","align":"center","children":["stage-text"],
                    "styles":{"backgroundColor":"#DBEAFE","borderRadius":10,"paddingLeft":8,"paddingRight":8,"paddingTop":2,"paddingBottom":2} },
                  { "id":"stage-text","component":"Text","text":"派送中","styles":{"color":"#1D4ED8","fontSize":11,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"tracking","component":"Text","text":"运单 SF1082849017GD","styles":{"color":"#64748B","fontSize":12,"letterSpacing":0.5,"marginTop":6,"marginBottom":0} },
                  { "id":"divider","component":"Divider","styles":{"marginTop":10,"marginBottom":8} },
                  { "id":"steps","component":"Column","align":"stretch","children":["step-1","step-2","step-3"] },
                  { "id":"step-1","component":"Row","align":"center","children":["step-1-dot","step-1-col"],"styles":{"marginTop":0,"marginBottom":6} },
                  { "id":"step-1-dot","component":"Column","weight":0,"children":[],"styles":{"width":8,"height":8,"borderRadius":4,"backgroundColor":"#2563EB","marginRight":10} },
                  { "id":"step-1-col","component":"Column","align":"start","children":["step-1-title","step-1-time"] },
                  { "id":"step-1-title","component":"Text","text":"快递员正在派送中","styles":{"color":"#0F172A","fontSize":13,"fontWeight":"medium","marginTop":0,"marginBottom":0} },
                  { "id":"step-1-time","component":"Text","text":"09-09 11:52 · 联系电话 138****2314","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"step-2","component":"Row","align":"center","children":["step-2-dot","step-2-col"],"styles":{"marginTop":0,"marginBottom":6} },
                  { "id":"step-2-dot","component":"Column","weight":0,"children":[],"styles":{"width":8,"height":8,"borderRadius":4,"backgroundColor":"#94A3B8","marginRight":10} },
                  { "id":"step-2-col","component":"Column","align":"start","children":["step-2-title","step-2-time"] },
                  { "id":"step-2-title","component":"Text","text":"到达苏州区分拣中心","styles":{"color":"#64748B","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"step-2-time","component":"Text","text":"09-09 06:14","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"step-3","component":"Row","align":"center","children":["step-3-dot","step-3-col"] },
                  { "id":"step-3-dot","component":"Column","weight":0,"children":[],"styles":{"width":8,"height":8,"borderRadius":4,"backgroundColor":"#94A3B8","marginRight":10} },
                  { "id":"step-3-col","component":"Column","align":"start","children":["step-3-title","step-3-time"] },
                  { "id":"step-3-title","component":"Text","text":"上海转运中心已发出","styles":{"color":"#64748B","fontSize":13,"marginTop":0,"marginBottom":0} },
                  { "id":"step-3-time","component":"Text","text":"09-08 21:37","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"eta","component":"Text","text":"预计今日 13:30 前送达","styles":{"color":"#1D4ED8","fontSize":12,"fontWeight":"medium","marginTop":8,"marginBottom":0,"textAlign":"center"} }
                ]
              }
            }
        """

        // ==== Card kind 15 · 银行卡 (adapted from 22_credit-card.json) ====
        private const val CARD15_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-credit-card-%SEQ%"}}
        """
        private const val CARD15_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-credit-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main",
                    "styles":{"padding":18,"borderRadius":14,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#312E81","stop":0.0},{"color":"#6D28D9","stop":1.0}]}} },
                  { "id":"main","component":"Column","align":"stretch","children":["brand-row","chip-row","card-no","detail-row"] },
                  { "id":"brand-row","component":"Row","align":"center","justify":"spaceBetween","children":["brand-lbl","brand-name"] },
                  { "id":"brand-lbl","component":"Text","text":"星辰银行","styles":{"color":"#E9D5FF","fontSize":12,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"brand-name","component":"Text","text":"VISA","styles":{"color":"#FFFFFF","fontSize":16,"fontWeight":"bold","letterSpacing":2,"marginTop":0,"marginBottom":0} },
                  { "id":"chip-row","component":"Row","align":"center","children":["chip-block","chip-wave"],"styles":{"marginTop":16,"marginBottom":0} },
                  { "id":"chip-block","component":"Column","weight":0,"children":[],"styles":{"width":36,"height":26,"backgroundColor":"#FCD34D","borderRadius":5,"marginRight":10} },
                  { "id":"chip-wave","component":"Text","text":"))))","styles":{"color":"#FFFFFF","fontSize":14,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"card-no","component":"Text","text":"4823  8901  ****  2314","styles":{"color":"#FFFFFF","fontSize":19,"fontWeight":"medium","letterSpacing":3,"marginTop":16,"marginBottom":16} },
                  { "id":"detail-row","component":"Row","align":"center","justify":"spaceBetween","children":["holder-col","expire-col"] },
                  { "id":"holder-col","component":"Column","align":"start","children":["holder-lbl","holder-val"] },
                  { "id":"holder-lbl","component":"Text","text":"持卡人","styles":{"color":"#C4B5FD","fontSize":10,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"holder-val","component":"Text","text":"LIN WEN WEN","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"medium","letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"expire-col","component":"Column","align":"end","children":["expire-lbl","expire-val"] },
                  { "id":"expire-lbl","component":"Text","text":"有效期","styles":{"color":"#C4B5FD","fontSize":10,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"expire-val","component":"Text","text":"08 / 28","styles":{"color":"#FFFFFF","fontSize":13,"fontWeight":"medium","letterSpacing":1,"marginTop":2,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 16 · 步数 (adapted from 23_step-counter.json) ====
        private const val CARD16_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-step-card-%SEQ%"}}
        """
        private const val CARD16_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-step-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":16,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"center","children":["header","big-num","goal-row","progress-track","stats-row"] },
                  { "id":"header","component":"Text","text":"👟 今日步数","styles":{"color":"#64748B","fontSize":12,"fontWeight":"medium","letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"big-num","component":"Text","text":"8,342","styles":{"color":"#0F172A","fontSize":42,"fontWeight":"bold","letterSpacing":-1,"marginTop":6,"marginBottom":0} },
                  { "id":"goal-row","component":"Row","align":"center","children":["goal-text","goal-pct"],"styles":{"marginTop":2,"marginBottom":8} },
                  { "id":"goal-text","component":"Text","text":"目标 10,000 步 · ","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":0} },
                  { "id":"goal-pct","component":"Text","text":"83%","styles":{"color":"#10B981","fontSize":12,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"progress-track","component":"Row","align":"start","children":["progress-fill"],
                    "styles":{"height":8,"backgroundColor":"#F1F5F9","borderRadius":4,"marginTop":0,"marginBottom":12} },
                  { "id":"progress-fill","component":"Column","weight":0,"children":[],"styles":{"width":210,"height":8,"backgroundColor":"#10B981","borderRadius":4} },
                  { "id":"stats-row","component":"Row","align":"center","justify":"spaceBetween","children":["st1","st2","st3"] },
                  { "id":"st1","component":"Column","align":"center","children":["st1-num","st1-lbl"] },
                  { "id":"st1-num","component":"Text","text":"5.8","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"st1-lbl","component":"Text","text":"公里","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"st2","component":"Column","align":"center","children":["st2-num","st2-lbl"] },
                  { "id":"st2-num","component":"Text","text":"312","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"st2-lbl","component":"Text","text":"千卡","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"st3","component":"Column","align":"center","children":["st3-num","st3-lbl"] },
                  { "id":"st3-num","component":"Text","text":"68","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"st3-lbl","component":"Text","text":"活跃分钟","styles":{"color":"#94A3B8","fontSize":11,"marginTop":2,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 17 · 数据统计 (adapted from 27_stats-card.json) ====
        private const val CARD17_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-stats-card-%SEQ%"}}
        """
        private const val CARD17_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-stats-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":14,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Column","align":"start","children":["title","value-row","trend-row"] },
                  { "id":"title","component":"Text","text":"月度活跃用户","styles":{"color":"#64748B","fontSize":12,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"value-row","component":"Row","align":"end","children":["val-num","val-unit"],"styles":{"marginTop":6,"marginBottom":0} },
                  { "id":"val-num","component":"Text","text":"128.4","styles":{"color":"#0F172A","fontSize":30,"fontWeight":"bold","letterSpacing":-0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"val-unit","component":"Text","text":"万","styles":{"color":"#64748B","fontSize":15,"fontWeight":"medium","marginTop":0,"marginBottom":4,"marginLeft":4} },
                  { "id":"trend-row","component":"Row","align":"center","children":["arrow-text","trend-num","trend-desc"],"styles":{"marginTop":6,"marginBottom":0} },
                  { "id":"arrow-text","component":"Text","text":"▲","styles":{"color":"#16A34A","fontSize":11,"marginTop":0,"marginBottom":0,"marginRight":2} },
                  { "id":"trend-num","component":"Text","text":"12.4%","styles":{"color":"#16A34A","fontSize":13,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"trend-desc","component":"Text","text":"  较上月","styles":{"color":"#94A3B8","fontSize":12,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 18 · 倒计时 (adapted from 28_countdown-timer.json) ====
        private const val CARD18_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-countdown-card-%SEQ%"}}
        """
        private const val CARD18_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-countdown-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main",
                    "styles":{"padding":16,"borderRadius":14,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#0891B2","stop":0.0},{"color":"#0E7490","stop":1.0}]}} },
                  { "id":"main","component":"Column","align":"center","children":["title","time-row","desc"] },
                  { "id":"title","component":"Text","text":"距发布会开始还有","styles":{"color":"#CFFAFE","fontSize":12,"letterSpacing":1,"marginTop":0,"marginBottom":0} },
                  { "id":"time-row","component":"Row","align":"center","justify":"spaceBetween","children":["b-day","b-hour","b-min","b-sec"],"styles":{"marginTop":10,"marginBottom":10} },
                  { "id":"b-day","component":"Column","align":"center","children":["b-day-n","b-day-l"],
                    "styles":{"backgroundColor":"#0C4A6E","borderRadius":10,"paddingLeft":12,"paddingRight":12,"paddingTop":8,"paddingBottom":6,"marginRight":6} },
                  { "id":"b-day-n","component":"Text","text":"07","styles":{"color":"#FFFFFF","fontSize":24,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"b-day-l","component":"Text","text":"天","styles":{"color":"#FFFFFF","fontSize":10,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"b-hour","component":"Column","align":"center","children":["b-hour-n","b-hour-l"],
                    "styles":{"backgroundColor":"#0C4A6E","borderRadius":10,"paddingLeft":12,"paddingRight":12,"paddingTop":8,"paddingBottom":6,"marginRight":6} },
                  { "id":"b-hour-n","component":"Text","text":"12","styles":{"color":"#FFFFFF","fontSize":24,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"b-hour-l","component":"Text","text":"时","styles":{"color":"#FFFFFF","fontSize":10,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"b-min","component":"Column","align":"center","children":["b-min-n","b-min-l"],
                    "styles":{"backgroundColor":"#0C4A6E","borderRadius":10,"paddingLeft":12,"paddingRight":12,"paddingTop":8,"paddingBottom":6,"marginRight":6} },
                  { "id":"b-min-n","component":"Text","text":"34","styles":{"color":"#FFFFFF","fontSize":24,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"b-min-l","component":"Text","text":"分","styles":{"color":"#FFFFFF","fontSize":10,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"b-sec","component":"Column","align":"center","children":["b-sec-n","b-sec-l"],
                    "styles":{"backgroundColor":"#0C4A6E","borderRadius":10,"paddingLeft":12,"paddingRight":12,"paddingTop":8,"paddingBottom":6} },
                  { "id":"b-sec-n","component":"Text","text":"52","styles":{"color":"#FFFFFF","fontSize":24,"fontWeight":"bold","letterSpacing":0.5,"marginTop":0,"marginBottom":0} },
                  { "id":"b-sec-l","component":"Text","text":"秒","styles":{"color":"#FFFFFF","fontSize":10,"letterSpacing":1,"marginTop":2,"marginBottom":0} },
                  { "id":"desc","component":"Text","text":"09 月 16 日 20:00 · 山上礼堂","styles":{"color":"#E0F2FE","fontSize":12,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        // ==== Card kind 19 · 电影 (adapted from 29_movie-card.json) ====
        private const val CARD19_CREATE_SURFACE_JSON = """
            {"version":"v0.9","createSurface":{"surfaceId":"native-movie-card-%SEQ%"}}
        """
        private const val CARD19_UPDATE_COMPONENTS_JSON = """
            {
              "version": "v0.9",
              "updateComponents": {
                "surfaceId": "native-movie-card-%SEQ%",
                "components": [
                  { "id":"root","component":"Card","child":"main","styles":{"padding":12,"backgroundColor":"#FFFFFF","borderRadius":14} },
                  { "id":"main","component":"Row","align":"start","children":["poster","info-col"] },
                  { "id":"poster","component":"Row","weight":0,"align":"center","justify":"center","children":["poster-emoji"],
                    "styles":{"width":72,"height":100,"borderRadius":8,"backgroundLinearGradient":{"direction":"to-bottom-right","colorStops":[{"color":"#F472B6","stop":0.0},{"color":"#7C3AED","stop":1.0}]},"marginRight":12} },
                  { "id":"poster-emoji","component":"Text","text":"🎬","styles":{"fontSize":32,"marginTop":0,"marginBottom":0} },
                  { "id":"info-col","component":"Column","align":"start","children":["movie-title","movie-meta","rating-row","desc-text","tag-row"],"styles":{"flexGrow":1} },
                  { "id":"movie-title","component":"Text","text":"星海之上","styles":{"color":"#0F172A","fontSize":16,"fontWeight":"bold","letterSpacing":0.3,"marginTop":0,"marginBottom":0} },
                  { "id":"movie-meta","component":"Text","text":"科幻 · 冒险 · 2026 · 128 分钟","styles":{"color":"#64748B","fontSize":11,"marginTop":2,"marginBottom":0} },
                  { "id":"rating-row","component":"Row","align":"center","children":["star","rate","rate-total"],"styles":{"marginTop":6,"marginBottom":0} },
                  { "id":"star","component":"Text","text":"★","styles":{"color":"#F59E0B","fontSize":14,"marginTop":0,"marginBottom":0,"marginRight":2} },
                  { "id":"rate","component":"Text","text":"8.9","styles":{"color":"#F59E0B","fontSize":15,"fontWeight":"bold","marginTop":0,"marginBottom":0} },
                  { "id":"rate-total","component":"Text","text":"  / 12.4 万人评价","styles":{"color":"#94A3B8","fontSize":11,"marginTop":0,"marginBottom":0} },
                  { "id":"desc-text","component":"Text","text":"孤船启航，星河深处，一次关于回家的告别。","styles":{"color":"#475569","fontSize":12,"marginTop":6,"marginBottom":0} },
                  { "id":"tag-row","component":"Row","align":"center","children":["tag-imax","tag-hot"],"styles":{"marginTop":8,"marginBottom":0} },
                  { "id":"tag-imax","component":"Text","text":"IMAX",
                    "styles":{"color":"#1D4ED8","fontSize":10,"fontWeight":"bold","backgroundColor":"#DBEAFE","borderRadius":4,"paddingLeft":6,"paddingRight":6,"paddingTop":2,"paddingBottom":2,"marginRight":6,"marginTop":0,"marginBottom":0} },
                  { "id":"tag-hot","component":"Text","text":"🔥 热映",
                    "styles":{"color":"#B91C1C","fontSize":10,"fontWeight":"bold","backgroundColor":"#FEE2E2","borderRadius":4,"paddingLeft":6,"paddingRight":6,"paddingTop":2,"paddingBottom":2,"marginTop":0,"marginBottom":0} }
                ]
              }
            }
        """

        /**
         * All (createSurface, updateComponents) pairs for the 20 card kinds,
         * indexed by kind number. `appendNextCard` picks the pair by
         * `appended % 20` and injects the sequence number via `%SEQ%`.
         */
        private val CARD_JSON_PAIRS: Array<Pair<String, String>> = arrayOf(
            CARD0_CREATE_SURFACE_JSON to CARD0_UPDATE_COMPONENTS_JSON,
            CARD1_CREATE_SURFACE_JSON to CARD1_UPDATE_COMPONENTS_JSON,
            CARD2_CREATE_SURFACE_JSON to CARD2_UPDATE_COMPONENTS_JSON,
            CARD3_CREATE_SURFACE_JSON to CARD3_UPDATE_COMPONENTS_JSON,
            CARD4_CREATE_SURFACE_JSON to CARD4_UPDATE_COMPONENTS_JSON,
            CARD5_CREATE_SURFACE_JSON to CARD5_UPDATE_COMPONENTS_JSON,
            CARD6_CREATE_SURFACE_JSON to CARD6_UPDATE_COMPONENTS_JSON,
            CARD7_CREATE_SURFACE_JSON to CARD7_UPDATE_COMPONENTS_JSON,
            CARD8_CREATE_SURFACE_JSON to CARD8_UPDATE_COMPONENTS_JSON,
            CARD9_CREATE_SURFACE_JSON to CARD9_UPDATE_COMPONENTS_JSON,
            CARD10_CREATE_SURFACE_JSON to CARD10_UPDATE_COMPONENTS_JSON,
            CARD11_CREATE_SURFACE_JSON to CARD11_UPDATE_COMPONENTS_JSON,
            CARD12_CREATE_SURFACE_JSON to CARD12_UPDATE_COMPONENTS_JSON,
            CARD13_CREATE_SURFACE_JSON to CARD13_UPDATE_COMPONENTS_JSON,
            CARD14_CREATE_SURFACE_JSON to CARD14_UPDATE_COMPONENTS_JSON,
            CARD15_CREATE_SURFACE_JSON to CARD15_UPDATE_COMPONENTS_JSON,
            CARD16_CREATE_SURFACE_JSON to CARD16_UPDATE_COMPONENTS_JSON,
            CARD17_CREATE_SURFACE_JSON to CARD17_UPDATE_COMPONENTS_JSON,
            CARD18_CREATE_SURFACE_JSON to CARD18_UPDATE_COMPONENTS_JSON,
            CARD19_CREATE_SURFACE_JSON to CARD19_UPDATE_COMPONENTS_JSON
        )

        /** Number of cards shown on first load. */
        private const val INITIAL_CARD_COUNT = 3
    }

    private lateinit var cardsContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private val surfaceViews = mutableListOf<A2UISurfaceView>()
    /** How many cards were appended so far — drives kind cycling. */
    private var appended: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildLayout())
        // Pre-populate the scroll host with the first N cards so the page
        // is not empty on launch. Each tap on the append button will keep
        // adding one more card, cycling through all 20 kinds indefinitely.
        repeat(INITIAL_CARD_COUNT) { appendNextCard() }
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

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#455A64"))
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
            text = "卡片场景接入示例"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        headerRow.addView(backArrow)
        headerRow.addView(headerTitle)
        header.addView(headerRow)

        val headerSubtitle = TextView(this).apply {
            text = "宿主：Native Activity + A2UISurfaceView\n默认展示前 4 张，点击按钮依次添加第 5 → 20 张卡片，20 张后从头循环"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#CFD8DC"))
            setPadding(0, dp(4), 0, 0)
        }
        header.addView(headerSubtitle)
        root.addView(header)

        // Scroll host — occupies the remaining vertical space, so the append
        // button below can be anchored to the bottom of the screen.
        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setPadding(dp(12), dp(8), dp(12), dp(12))
        }
        cardsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(cardsContainer)
        root.addView(scrollView)

        // Append button — pinned to the bottom of the screen.
        val addButton = Button(this).apply {
            text = "点击追加 A2UI 卡片"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dp(12), dp(8), dp(12), dp(12))
            layoutParams = params
            setOnClickListener { appendNextCard() }
        }
        root.addView(addButton)
        return root
    }

    /**
     * Append one A2UI card of the next kind in the cycle. Each card gets its
     * own [A2UISurfaceView] so its stream lifecycle stays independent — the
     * KuiklyPage variant does the same on the Compose/Flex tree.
     */
    private fun appendNextCard() {
        val kind = appended % CARD_JSON_PAIRS.size
        appended += 1

        val density = resources.displayMetrics.density
        fun dp(value: Int) = (value * density).toInt()

        val cardWrap = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (appended == 1) 0 else dp(10)
            }
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
        cardsContainer.addView(cardWrap)

        surface.init("{}")
        surface.setListener(object : A2UISurfaceViewListener {
            override fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {
                Log.d(TAG, "onAction card#$appended surfaceId=$surfaceId tag=$tag action=$action params=$params")
                handleActionEvent(action)
            }
            override fun onDataModelChanged(dataModelJson: String, changedKey: String) {
                Log.d(TAG, "onDataModelChanged card#$appended key=$changedKey")
            }
            override fun onOpenUrl(url: String) {
                Log.d(TAG, "onOpenUrl card#$appended url=$url")
            }
        })

        // Pick the pre-built JSON templates and just inject the sequence number.
        val (createJson, updateJson) = CARD_JSON_PAIRS[kind]
        val seq = appended.toString()
        surface.writeStream(createJson.replace("%SEQ%", seq))
        surface.writeStream(updateJson.replace("%SEQ%", seq))
        surface.finishStream()
        surfaceViews.add(surface)

        // Scrolling to the *real* bottom is trickier than it looks: A2UISurfaceView
        // renders asynchronously (JSON → context thread → batched UI ops), so the
        // moment appendNextCard returns, cardWrap is still 0-height. A single
        // `scrollView.post { fullScroll(FOCUS_DOWN) }` would scroll to the old
        // bottom (i.e. the *previous* card's tail).
        //
        // We solve this by listening to `cardWrap`'s own layout changes: every
        // time A2UI produces new UI ops and remeasures the surface view, the
        // wrap's bottom moves and we re-scroll. We keep listening for a few
        // seconds to cover multi-pass layouts (e.g. images resolving after
        // network load), then detach.
        val listenerRef = arrayOfNulls<View.OnLayoutChangeListener>(1)
        val detachAt = System.currentTimeMillis() + 3_000L
        val layoutListener = View.OnLayoutChangeListener { v, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom != oldBottom) {
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
            if (System.currentTimeMillis() >= detachAt) {
                listenerRef[0]?.let { v.removeOnLayoutChangeListener(it) }
            }
        }
        listenerRef[0] = layoutListener
        cardWrap.addOnLayoutChangeListener(layoutListener)
        // Immediate best-effort scroll so text-only cards (which finish layout
        // in the very next frame) also visibly stick to the bottom.
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    override fun onDestroy() {
        surfaceViews.forEach { it.destroy() }
        surfaceViews.clear()
        super.onDestroy()
    }

    /**
     * Interpret an A2UI `action` JSON delivered via
     * [A2UISurfaceViewListener.onAction] and, for the demo-only convention
     * of `event.name == "toast"`, pop a native `AlertDialog` using the
     * `title` / `message` fields from `event.context`.
     *
     * Any other event name is left to be handled by the app-specific
     * business logic (here we just log it above). This mirrors what
     * ohos / iOS host demos do — the platform side no longer treats
     * `showAlert` as a built-in `functionCall`; instead the demo JSON
     * emits a plain protocol event and the host decides how to render it.
     */
    private fun handleActionEvent(actionJson: String) {
        val eventObj = try {
            JSONObject(actionJson).optJSONObject("event") ?: return
        } catch (e: Exception) {
            Log.w(TAG, "handleActionEvent: malformed action JSON: ${e.message}")
            return
        }
        val name = eventObj.optString("name")
        if (name != "toast") return
        val ctx = eventObj.optJSONObject("context")
        val title = ctx?.optString("title").orEmpty()
        val message = ctx?.optString("message").orEmpty()
        AlertDialog.Builder(this)
            .apply { if (title.isNotEmpty()) setTitle(title) }
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

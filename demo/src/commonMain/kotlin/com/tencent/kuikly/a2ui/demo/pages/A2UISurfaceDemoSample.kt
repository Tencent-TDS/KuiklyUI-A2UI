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

package com.tencent.kuikly.a2ui.demo.pages

/**
 * Sample A2UI 1.0 protocol envelope builders used by [A2UISurfaceDemoPager].
 *
 * ### Protocol note
 * These A2UI 1.0 envelopes declare `kuikly_a2ui_catalog_v1_0.json` to preserve
 * the Kuikly styles extension. The official A2UI protocol defines the
 * server-to-client channel as a stream of JSON *objects* (envelopes), one per
 * message. Each string constant below is a self-contained top-level `{...}`
 * envelope, ready to be fed to
 * [com.tencent.kuikly.a2ui.dsl.embed.A2UISurfaceDSLView.writeStream]
 * one-at-a-time. Feeding a JSON *array* is NOT standard and won't be parsed.
 *
 * ### Card kinds
 * 20 hand-authored card layouts are provided (indices 0..19). They mirror
 * [NativeCardDemoActivity]'s catalogue one-for-one — the same JSON strings,
 * so any visual tweak made on the Native path is picked up here verbatim.
 * Callers pass the `kind` (0..19) into [sampleCardEnvelopes] to select which
 * tree to mount.
 *
 * The `index` parameter is used to derive a unique `surfaceId` suffix
 * (`%SEQ%` → `(index + 1)`) so multiple A2UISurface instances rendered
 * side-by-side don't collide.
 *
 * ### Protocol note on `children`
 * `Row` / `Column` / `List` all accept `children` as an array of component
 * IDs (strings). Components can NOT be inlined; every component referenced by
 * an ID must appear as its own entry in the top-level `components` array.
 *
 * See `a2ui/androidApp{KuiklyPage,NativePage,BothPages}/src/main/assets/a2ui_examples/`
 * for the original upstream JSON files these card kinds were adapted from.
 */

/** Number of card kinds available. Callers wrap with `% SAMPLE_CARD_KIND_COUNT`. */
internal const val SAMPLE_CARD_KIND_COUNT: Int = 20

// ---------------------------------------------------------------------------
// Card kind 0 · 航班状态 (adapted from 01_flight-status.json)
// 白底 + 顶部淡蓝渐变条 + 航班号大字 + 状态 pill + 出发/到达大时间.
// ---------------------------------------------------------------------------
private const val CARD0_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-flight-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD0_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-flight-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 0,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "banner",
              "content"
            ]
          },
          {
            "id": "banner",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "banner_left",
              "status_pill"
            ],
            "styles": {
              "paddingLeft": 18,
              "paddingRight": 14,
              "paddingTop": 10,
              "paddingBottom": 10,
              "backgroundLinearGradient": {
                "direction": "to-right",
                "colorStops": [
                  {
                    "color": "#EAF2FF",
                    "stop": 0.0
                  },
                  {
                    "color": "#DCE7FE",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "banner_left",
            "component": "Row",
            "align": "center",
            "children": [
              "plane_emoji",
              "flight_no"
            ]
          },
          {
            "id": "plane_emoji",
            "component": "Text",
            "text": "✈️",
            "styles": {
              "fontSize": 20,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 8
            }
          },
          {
            "id": "flight_no",
            "component": "Text",
            "text": "CA1836 · 国航",
            "styles": {
              "color": "#1E3A8A",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "status_pill",
            "component": "Row",
            "align": "center",
            "children": [
              "status_text"
            ],
            "styles": {
              "backgroundColor": "#DCFCE7",
              "borderRadius": 10,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3
            }
          },
          {
            "id": "status_text",
            "component": "Text",
            "text": "· 准点",
            "styles": {
              "color": "#166534",
              "fontSize": 12,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "content",
            "component": "Column",
            "align": "stretch",
            "children": [
              "route",
              "route_code",
              "time_row",
              "footer"
            ],
            "styles": {
              "paddingLeft": 18,
              "paddingRight": 18,
              "paddingTop": 10,
              "paddingBottom": 10
            }
          },
          {
            "id": "route",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "origin",
              "arrow",
              "dest"
            ]
          },
          {
            "id": "origin",
            "component": "Text",
            "text": "深圳",
            "styles": {
              "color": "#0F172A",
              "fontSize": 26,
              "fontWeight": "bold",
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "arrow",
            "component": "Text",
            "text": "→",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 22,
              "fontWeight": "normal",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dest",
            "component": "Text",
            "text": "北京",
            "styles": {
              "color": "#0F172A",
              "fontSize": 26,
              "fontWeight": "bold",
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "route_code",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "origin_code",
              "dur_text",
              "dest_code"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 4
            }
          },
          {
            "id": "origin_code",
            "component": "Text",
            "text": "SZX · T3",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dur_text",
            "component": "Text",
            "text": "3 小时 05 分",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dest_code",
            "component": "Text",
            "text": "PEK · T2",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "time_row",
            "component": "Row",
            "align": "start",
            "justify": "spaceBetween",
            "children": [
              "dep_col",
              "arr_col"
            ],
            "styles": {
              "marginTop": 6
            }
          },
          {
            "id": "dep_col",
            "component": "Column",
            "align": "start",
            "children": [
              "dep_label",
              "dep_time"
            ]
          },
          {
            "id": "dep_label",
            "component": "Text",
            "text": "起飞",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dep_time",
            "component": "Text",
            "text": "10:15",
            "styles": {
              "color": "#0F172A",
              "fontSize": 20,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "arr_col",
            "component": "Column",
            "align": "end",
            "children": [
              "arr_label",
              "arr_time"
            ]
          },
          {
            "id": "arr_label",
            "component": "Text",
            "text": "到达",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "arr_time",
            "component": "Text",
            "text": "13:20",
            "styles": {
              "color": "#0F172A",
              "fontSize": 20,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "footer",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "gate_pill",
              "seat_pill"
            ],
            "styles": {
              "marginTop": 6
            }
          },
          {
            "id": "gate_pill",
            "component": "Row",
            "align": "center",
            "children": [
              "gate_text"
            ],
            "styles": {
              "backgroundColor": "#F1F5F9",
              "borderRadius": 6,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3
            }
          },
          {
            "id": "gate_text",
            "component": "Text",
            "text": "登机口 B24",
            "styles": {
              "color": "#334155",
              "fontSize": 12,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "seat_pill",
            "component": "Row",
            "align": "center",
            "children": [
              "seat_text"
            ],
            "styles": {
              "backgroundColor": "#EFF6FF",
              "borderRadius": 6,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3
            }
          },
          {
            "id": "seat_text",
            "component": "Text",
            "text": "座位 32A · 靠窗",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 12,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 1 · 今日日程 (adapted from 03_calendar-day.json)
// ---------------------------------------------------------------------------
private const val CARD1_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-calendar-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD1_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-calendar-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 12,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header",
              "divider",
              "actions"
            ]
          },
          {
            "id": "header",
            "component": "Row",
            "align": "start",
            "children": [
              "date_block",
              "events_col"
            ]
          },
          {
            "id": "date_block",
            "component": "Row",
            "align": "stretch",
            "children": [
              "accent_divider",
              "date_col"
            ],
            "styles": {
              "marginRight": 14
            }
          },
          {
            "id": "accent_divider",
            "component": "Divider",
            "axis": "vertical",
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
            "id": "date_col",
            "component": "Column",
            "align": "start",
            "children": [
              "day_name",
              "day_number",
              "day_lunar"
            ]
          },
          {
            "id": "day_name",
            "component": "Text",
            "text": "周日",
            "styles": {
              "color": "#2563EB",
              "fontSize": 12,
              "fontWeight": "bold",
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "day_number",
            "component": "Text",
            "text": "28",
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
            "id": "day_lunar",
            "component": "Text",
            "text": "腊月初八",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "events_col",
            "component": "Column",
            "align": "stretch",
            "children": [
              "event_1",
              "event_2",
              "event_3"
            ],
            "styles": {
              "flexGrow": 1
            }
          },
          {
            "id": "event_1",
            "component": "Row",
            "align": "center",
            "children": [
              "dot_1",
              "event_1_col"
            ],
            "styles": {
              "marginBottom": 6
            }
          },
          {
            "id": "dot_1",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "backgroundColor": "#F59E0B",
              "borderRadius": 4,
              "marginRight": 10
            }
          },
          {
            "id": "event_1_col",
            "component": "Column",
            "align": "start",
            "children": [
              "event_1_title",
              "event_1_time"
            ]
          },
          {
            "id": "event_1_title",
            "component": "Text",
            "text": "早餐 · 与设计对齐",
            "styles": {
              "color": "#0F172A",
              "fontSize": 14,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "event_1_time",
            "component": "Text",
            "text": "08:30 – 09:15",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "event_2",
            "component": "Row",
            "align": "center",
            "children": [
              "dot_2",
              "event_2_col"
            ],
            "styles": {
              "marginBottom": 6
            }
          },
          {
            "id": "dot_2",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "backgroundColor": "#2563EB",
              "borderRadius": 4,
              "marginRight": 10
            }
          },
          {
            "id": "event_2_col",
            "component": "Column",
            "align": "start",
            "children": [
              "event_2_title",
              "event_2_time"
            ]
          },
          {
            "id": "event_2_title",
            "component": "Text",
            "text": "Q1 路线图评审",
            "styles": {
              "color": "#0F172A",
              "fontSize": 14,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "event_2_time",
            "component": "Text",
            "text": "13:00 – 14:00 · 3 号会议室",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "event_3",
            "component": "Row",
            "align": "center",
            "children": [
              "dot_3",
              "event_3_col"
            ]
          },
          {
            "id": "dot_3",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "backgroundColor": "#10B981",
              "borderRadius": 4,
              "marginRight": 10
            }
          },
          {
            "id": "event_3_col",
            "component": "Column",
            "align": "start",
            "children": [
              "event_3_title",
              "event_3_time"
            ]
          },
          {
            "id": "event_3_title",
            "component": "Text",
            "text": "团队站会",
            "styles": {
              "color": "#0F172A",
              "fontSize": 14,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "event_3_time",
            "component": "Text",
            "text": "15:30 – 16:00",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "divider",
            "component": "Divider",
            "styles": {
              "marginTop": 8,
              "marginBottom": 6
            }
          },
          {
            "id": "actions",
            "component": "Row",
            "align": "center",
            "justify": "end",
            "children": [
              "discard_btn",
              "add_btn"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "discard_btn_text",
            "component": "Text",
            "text": "忽略",
            "styles": {
              "color": "#64748B",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "discard_btn",
            "component": "Button",
            "child": "discard_btn_text",
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
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 4,
              "paddingBottom": 4,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 8
            }
          },
          {
            "id": "add_btn_text",
            "component": "Text",
            "text": "添加到日历",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "add_btn",
            "component": "Button",
            "child": "add_btn_text",
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
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 4,
              "paddingBottom": 4,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 2 · 天气 (adapted from 04_weather-current.json)
// ---------------------------------------------------------------------------
private const val CARD2_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-weather-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD2_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-weather-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 12,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header_row",
              "temp_row",
              "desc_row",
              "forecast_row"
            ]
          },
          {
            "id": "header_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "loc_col",
              "sun_emoji"
            ]
          },
          {
            "id": "loc_col",
            "component": "Column",
            "align": "start",
            "children": [
              "location",
              "location_sub"
            ]
          },
          {
            "id": "location",
            "component": "Text",
            "text": "深圳 · 南山区",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "location_sub",
            "component": "Text",
            "text": "更新于 10 分钟前",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "sun_emoji",
            "component": "Text",
            "text": "🌤️",
            "styles": {
              "fontSize": 32,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "temp_row",
            "component": "Row",
            "align": "start",
            "children": [
              "temp_main",
              "temp_unit",
              "temp_low_wrap"
            ],
            "styles": {
              "marginTop": 2
            }
          },
          {
            "id": "temp_main",
            "component": "Text",
            "text": "15",
            "styles": {
              "color": "#0F172A",
              "fontSize": 64,
              "fontWeight": "light",
              "letterSpacing": -2,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "temp_unit",
            "component": "Text",
            "text": "°",
            "styles": {
              "color": "#0F172A",
              "fontSize": 40,
              "fontWeight": "light",
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "temp_low_wrap",
            "component": "Column",
            "align": "start",
            "children": [
              "temp_low_label",
              "temp_low_value"
            ],
            "styles": {
              "marginLeft": 14,
              "marginTop": 10
            }
          },
          {
            "id": "temp_low_label",
            "component": "Text",
            "text": "最低",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "temp_low_value",
            "component": "Text",
            "text": "8°",
            "styles": {
              "color": "#334155",
              "fontSize": 18,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "desc_row",
            "component": "Row",
            "align": "center",
            "children": [
              "desc_text",
              "desc_sep",
              "wind_text"
            ],
            "styles": {
              "marginTop": 2,
              "marginBottom": 8
            }
          },
          {
            "id": "desc_text",
            "component": "Text",
            "text": "多云转晴",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "desc_sep",
            "component": "Text",
            "text": " · ",
            "styles": {
              "color": "#CBD5E1",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "wind_text",
            "component": "Text",
            "text": "东南风 3 级",
            "styles": {
              "color": "#64748B",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "forecast_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "day_today",
              "day_tue",
              "day_wed",
              "day_thu",
              "day_fri"
            ],
            "styles": {
              "marginTop": 6
            }
          },
          {
            "id": "day_today",
            "component": "Column",
            "align": "center",
            "children": [
              "today_name",
              "today_icon",
              "today_temp"
            ],
            "styles": {
              "backgroundColor": "#EFF6FF",
              "borderRadius": 10,
              "paddingLeft": 8,
              "paddingRight": 8,
              "paddingTop": 4,
              "paddingBottom": 4
            }
          },
          {
            "id": "today_name",
            "component": "Text",
            "text": "今日",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 11,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "today_icon",
            "component": "Text",
            "text": "🌤️",
            "styles": {
              "fontSize": 20,
              "marginTop": 2,
              "marginBottom": 2
            }
          },
          {
            "id": "today_temp",
            "component": "Text",
            "text": "15°",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "day_tue",
            "component": "Column",
            "align": "center",
            "children": [
              "tue_name",
              "tue_icon",
              "tue_temp"
            ]
          },
          {
            "id": "tue_name",
            "component": "Text",
            "text": "周二",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "tue_icon",
            "component": "Text",
            "text": "☀️",
            "styles": {
              "fontSize": 20,
              "marginTop": 2,
              "marginBottom": 2
            }
          },
          {
            "id": "tue_temp",
            "component": "Text",
            "text": "17°",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "day_wed",
            "component": "Column",
            "align": "center",
            "children": [
              "wed_name",
              "wed_icon",
              "wed_temp"
            ]
          },
          {
            "id": "wed_name",
            "component": "Text",
            "text": "周三",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "wed_icon",
            "component": "Text",
            "text": "🌧️",
            "styles": {
              "fontSize": 20,
              "marginTop": 2,
              "marginBottom": 2
            }
          },
          {
            "id": "wed_temp",
            "component": "Text",
            "text": "12°",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "day_thu",
            "component": "Column",
            "align": "center",
            "children": [
              "thu_name",
              "thu_icon",
              "thu_temp"
            ]
          },
          {
            "id": "thu_name",
            "component": "Text",
            "text": "周四",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "thu_icon",
            "component": "Text",
            "text": "⛅",
            "styles": {
              "fontSize": 20,
              "marginTop": 2,
              "marginBottom": 2
            }
          },
          {
            "id": "thu_temp",
            "component": "Text",
            "text": "14°",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "day_fri",
            "component": "Column",
            "align": "center",
            "children": [
              "fri_name",
              "fri_icon",
              "fri_temp"
            ]
          },
          {
            "id": "fri_name",
            "component": "Text",
            "text": "周五",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "fri_icon",
            "component": "Text",
            "text": "☀️",
            "styles": {
              "fontSize": 20,
              "marginTop": 2,
              "marginBottom": 2
            }
          },
          {
            "id": "fri_temp",
            "component": "Text",
            "text": "18°",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 3 · 商品 (adapted from 05_product-card.json)
// ---------------------------------------------------------------------------
private const val CARD3_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-product-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD3_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-product-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 0,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "cover",
              "content"
            ]
          },
          {
            "id": "cover",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "children": [
              "cover_emoji"
            ],
            "styles": {
              "height": 110,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#FDE68A",
                    "stop": 0.0
                  },
                  {
                    "color": "#F59E0B",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "cover_emoji",
            "component": "Text",
            "text": "🎧",
            "styles": {
              "fontSize": 56,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "content",
            "component": "Column",
            "align": "stretch",
            "children": [
              "tag_row",
              "title",
              "subtitle",
              "price_row",
              "cart_btn"
            ],
            "styles": {
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 10,
              "paddingBottom": 12
            }
          },
          {
            "id": "tag_row",
            "component": "Row",
            "align": "center",
            "children": [
              "tag_new",
              "tag_hot"
            ]
          },
          {
            "id": "tag_new",
            "component": "Text",
            "text": "新品",
            "styles": {
              "color": "#166534",
              "fontSize": 11,
              "fontWeight": "bold",
              "backgroundColor": "#DCFCE7",
              "borderRadius": 4,
              "paddingLeft": 6,
              "paddingRight": 6,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginRight": 6,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "tag_hot",
            "component": "Text",
            "text": "畅销",
            "styles": {
              "color": "#9A3412",
              "fontSize": 11,
              "fontWeight": "bold",
              "backgroundColor": "#FFEDD5",
              "borderRadius": 4,
              "paddingLeft": 6,
              "paddingRight": 6,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "title",
            "component": "Text",
            "text": "XxxWave Pro 无线降噪耳机",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "subtitle",
            "component": "Text",
            "text": "40 小时续航 · 空间音频 · 极致降噪",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "price_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "price_col",
              "stock_text"
            ],
            "styles": {
              "marginTop": 8,
              "marginBottom": 8
            }
          },
          {
            "id": "price_col",
            "component": "Row",
            "align": "end",
            "children": [
              "price_cur",
              "price_num",
              "price_old"
            ]
          },
          {
            "id": "price_cur",
            "component": "Text",
            "text": "¥",
            "styles": {
              "color": "#DC2626",
              "fontSize": 14,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 3,
              "marginRight": 2
            }
          },
          {
            "id": "price_num",
            "component": "Text",
            "text": "899",
            "styles": {
              "color": "#DC2626",
              "fontSize": 24,
              "fontWeight": "bold",
              "letterSpacing": -0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "price_old",
            "component": "Text",
            "text": "¥1299",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 4,
              "marginLeft": 8,
              "textDecoration": "line-through"
            }
          },
          {
            "id": "stock_text",
            "component": "Text",
            "text": "仅剩 12 件",
            "styles": {
              "color": "#EA580C",
              "fontSize": 12,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "cart_btn_text",
            "component": "Text",
            "text": "加入购物车",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "cart_btn",
            "component": "Button",
            "child": "cart_btn_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "已加入购物车",
                  "message": "XxxWave Pro 无线降噪耳机 × 1"
                }
              }
            },
            "styles": {
              "backgroundColor": "#DC2626",
              "borderRadius": 10,
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 8,
              "paddingBottom": 8,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 4 · 音乐播放 (adapted from 06_music-player.json)
// ---------------------------------------------------------------------------
private const val CARD4_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-music-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD4_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-music-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#0F172A",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Row",
            "align": "center",
            "children": [
              "cover",
              "info_col"
            ]
          },
          {
            "id": "cover",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "children": [
              "cover_emoji"
            ],
            "styles": {
              "width": 72,
              "height": 72,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#7C3AED",
                    "stop": 0.0
                  },
                  {
                    "color": "#EC4899",
                    "stop": 1.0
                  }
                ]
              },
              "borderRadius": 10,
              "marginRight": 12
            }
          },
          {
            "id": "cover_emoji",
            "component": "Text",
            "text": "🎵",
            "styles": {
              "fontSize": 32,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "info_col",
            "component": "Column",
            "align": "stretch",
            "children": [
              "title",
              "artist",
              "progress_row",
              "controls_row"
            ],
            "styles": {
              "flexGrow": 1
            }
          },
          {
            "id": "title",
            "component": "Text",
            "text": "夜航之歌",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "artist",
            "component": "Text",
            "text": "星野 · 单曲",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "progress_row",
            "component": "Row",
            "align": "center",
            "children": [
              "cur_time",
              "progress_bar",
              "total_time"
            ],
            "styles": {
              "marginTop": 8,
              "marginBottom": 0
            }
          },
          {
            "id": "cur_time",
            "component": "Text",
            "text": "01:23",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 10,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 8
            }
          },
          {
            "id": "progress_bar",
            "component": "Row",
            "align": "center",
            "children": [
              "progress_fill",
              "progress_track"
            ],
            "styles": {
              "height": 3,
              "backgroundColor": "#1E293B",
              "borderRadius": 2,
              "flexGrow": 1
            }
          },
          {
            "id": "progress_fill",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 90,
              "height": 3,
              "backgroundColor": "#EC4899",
              "borderRadius": 2
            }
          },
          {
            "id": "progress_track",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 0,
              "height": 3
            }
          },
          {
            "id": "total_time",
            "component": "Text",
            "text": "03:42",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 10,
              "marginTop": 0,
              "marginBottom": 0,
              "marginLeft": 8
            }
          },
          {
            "id": "controls_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "btn_prev",
              "btn_play",
              "btn_next"
            ],
            "styles": {
              "marginTop": 10,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_prev_text",
            "component": "Text",
            "text": "⏮",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 18,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_prev",
            "component": "Button",
            "child": "btn_prev_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "上一曲",
                  "message": "已切换到上一曲。"
                }
              }
            },
            "styles": {
              "backgroundColor": "#00000000",
              "paddingLeft": 4,
              "paddingRight": 4,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_play_text",
            "component": "Text",
            "text": "▶",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_play",
            "component": "Button",
            "child": "btn_play_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "播放中",
                  "message": "夜航之歌 · 星野"
                }
              }
            },
            "styles": {
              "backgroundColor": "#F8FAFC",
              "borderRadius": 100,
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 6,
              "paddingBottom": 6,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_next_text",
            "component": "Text",
            "text": "⏭",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 18,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_next",
            "component": "Button",
            "child": "btn_next_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "下一曲",
                  "message": "已切换到下一曲。"
                }
              }
            },
            "styles": {
              "backgroundColor": "#00000000",
              "paddingLeft": 4,
              "paddingRight": 4,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 5 · 任务 (adapted from 07_task-card.json)
// ---------------------------------------------------------------------------
private const val CARD5_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-task-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD5_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-task-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 12,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Row",
            "align": "center",
            "children": [
              "check_box",
              "content_col",
              "priority_pill"
            ]
          },
          {
            "id": "check_box",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "weight": 0,
            "children": [
              "check_mark"
            ],
            "styles": {
              "width": 22,
              "height": 22,
              "borderRadius": 6,
              "backgroundColor": "#2563EB",
              "marginRight": 10
            }
          },
          {
            "id": "check_mark",
            "component": "Text",
            "text": "✓",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "content_col",
            "component": "Column",
            "align": "start",
            "children": [
              "task_title",
              "task_meta"
            ],
            "styles": {
              "flexGrow": 1
            }
          },
          {
            "id": "task_title",
            "component": "Text",
            "text": "完成 Q1 季度汇报 PPT",
            "styles": {
              "color": "#0F172A",
              "fontSize": 15,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "task_meta",
            "component": "Text",
            "text": "截止 · 明天 18:00 · 项目组",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "priority_pill",
            "component": "Row",
            "weight": 0,
            "align": "center",
            "children": [
              "priority_text"
            ],
            "styles": {
              "backgroundColor": "#FEE2E2",
              "borderRadius": 8,
              "paddingLeft": 8,
              "paddingRight": 8,
              "paddingTop": 3,
              "paddingBottom": 3,
              "marginLeft": 8
            }
          },
          {
            "id": "priority_text",
            "component": "Text",
            "text": "高优",
            "styles": {
              "color": "#B91C1C",
              "fontSize": 11,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 6 · 个人资料 (adapted from 08_user-profile.json)
// ---------------------------------------------------------------------------
private const val CARD6_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-profile-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD6_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-profile-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 0,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "banner",
              "body"
            ]
          },
          {
            "id": "banner",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "children": [
              "avatar_wrap"
            ],
            "styles": {
              "height": 78,
              "backgroundLinearGradient": {
                "direction": "to-right",
                "colorStops": [
                  {
                    "color": "#6366F1",
                    "stop": 0.0
                  },
                  {
                    "color": "#8B5CF6",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "avatar_wrap",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "children": [
              "avatar_text"
            ],
            "styles": {
              "width": 64,
              "height": 64,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 100,
              "marginTop": 36
            }
          },
          {
            "id": "avatar_text",
            "component": "Text",
            "text": "🐼",
            "styles": {
              "fontSize": 32,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "body",
            "component": "Column",
            "align": "center",
            "children": [
              "name",
              "handle",
              "bio",
              "stats_row",
              "follow_btn"
            ],
            "styles": {
              "paddingLeft": 16,
              "paddingRight": 16,
              "paddingTop": 38,
              "paddingBottom": 14
            }
          },
          {
            "id": "name",
            "component": "Text",
            "text": "林明明",
            "styles": {
              "color": "#0F172A",
              "fontSize": 18,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "handle",
            "component": "Text",
            "text": "@mingming · 产品设计师",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "bio",
            "component": "Text",
            "text": "专注移动端交互体验，探索 AI 与工具的边界。",
            "styles": {
              "color": "#475569",
              "fontSize": 12,
              "marginTop": 8,
              "marginBottom": 0,
              "textAlign": "center"
            }
          },
          {
            "id": "stats_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "stat_1",
              "stat_2",
              "stat_3"
            ],
            "styles": {
              "marginTop": 12,
              "marginBottom": 10
            }
          },
          {
            "id": "stat_1",
            "component": "Column",
            "align": "center",
            "children": [
              "stat_1_num",
              "stat_1_lbl"
            ]
          },
          {
            "id": "stat_1_num",
            "component": "Text",
            "text": "1.2k",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stat_1_lbl",
            "component": "Text",
            "text": "作品",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stat_2",
            "component": "Column",
            "align": "center",
            "children": [
              "stat_2_num",
              "stat_2_lbl"
            ]
          },
          {
            "id": "stat_2_num",
            "component": "Text",
            "text": "38.5k",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stat_2_lbl",
            "component": "Text",
            "text": "粉丝",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stat_3",
            "component": "Column",
            "align": "center",
            "children": [
              "stat_3_num",
              "stat_3_lbl"
            ]
          },
          {
            "id": "stat_3_num",
            "component": "Text",
            "text": "286",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stat_3_lbl",
            "component": "Text",
            "text": "关注",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "follow_btn_text",
            "component": "Text",
            "text": "+ 关注",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "follow_btn",
            "component": "Button",
            "child": "follow_btn_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "已关注",
                  "message": "你已关注 @mingming。"
                }
              }
            },
            "styles": {
              "backgroundColor": "#6366F1",
              "borderRadius": 100,
              "paddingLeft": 22,
              "paddingRight": 22,
              "paddingTop": 7,
              "paddingBottom": 7,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 7 · 订单完成 (adapted from 11_purchase-complete.json)
// ---------------------------------------------------------------------------
private const val CARD7_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-purchase-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD7_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-purchase-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 16,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "center",
            "children": [
              "icon_wrap",
              "title",
              "subtitle",
              "summary",
              "view_btn"
            ]
          },
          {
            "id": "icon_wrap",
            "component": "Row",
            "align": "center",
            "justify": "center",
            "children": [
              "icon_check"
            ],
            "styles": {
              "width": 52,
              "height": 52,
              "backgroundColor": "#DCFCE7",
              "borderRadius": 100,
              "marginTop": 0,
              "marginBottom": 10
            }
          },
          {
            "id": "icon_check",
            "component": "Text",
            "text": "✓",
            "styles": {
              "color": "#16A34A",
              "fontSize": 30,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "title",
            "component": "Text",
            "text": "支付成功",
            "styles": {
              "color": "#0F172A",
              "fontSize": 19,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "subtitle",
            "component": "Text",
            "text": "感谢您的购买，订单已提交",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "marginTop": 4,
              "marginBottom": 10,
              "textAlign": "center"
            }
          },
          {
            "id": "summary",
            "component": "Column",
            "align": "stretch",
            "children": [
              "row_amount",
              "row_order",
              "row_time"
            ],
            "styles": {
              "backgroundColor": "#F8FAFC",
              "borderRadius": 10,
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 10,
              "paddingBottom": 10,
              "marginTop": 0,
              "marginBottom": 12
            }
          },
          {
            "id": "row_amount",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "lbl_amount",
              "val_amount"
            ]
          },
          {
            "id": "lbl_amount",
            "component": "Text",
            "text": "支付金额",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "val_amount",
            "component": "Text",
            "text": "¥ 328.00",
            "styles": {
              "color": "#0F172A",
              "fontSize": 14,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "row_order",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "lbl_order",
              "val_order"
            ],
            "styles": {
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "lbl_order",
            "component": "Text",
            "text": "订单号",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "val_order",
            "component": "Text",
            "text": "20260909·A17F92",
            "styles": {
              "color": "#334155",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "row_time",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "lbl_time",
              "val_time"
            ],
            "styles": {
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "lbl_time",
            "component": "Text",
            "text": "支付时间",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "val_time",
            "component": "Text",
            "text": "09-09 12:18",
            "styles": {
              "color": "#334155",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "view_btn_text",
            "component": "Text",
            "text": "查看订单详情",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "view_btn",
            "component": "Button",
            "child": "view_btn_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "订单详情",
                  "message": "20260909·A17F92 · ¥ 328.00"
                }
              }
            },
            "styles": {
              "backgroundColor": "#16A34A",
              "borderRadius": 10,
              "paddingLeft": 22,
              "paddingRight": 22,
              "paddingTop": 8,
              "paddingBottom": 8,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 8 · 聊天消息 (adapted from 12_chat-message.json)
// ---------------------------------------------------------------------------
private const val CARD8_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-chat-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD8_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-chat-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 12,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "msg_in",
              "msg_out",
              "typing_row"
            ]
          },
          {
            "id": "msg_in",
            "component": "Row",
            "align": "start",
            "children": [
              "avatar_in",
              "bubble_in_wrap"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 8
            }
          },
          {
            "id": "avatar_in",
            "component": "Row",
            "weight": 0,
            "align": "center",
            "justify": "center",
            "children": [
              "avatar_in_text"
            ],
            "styles": {
              "width": 28,
              "height": 28,
              "backgroundColor": "#F1F5F9",
              "borderRadius": 100,
              "marginRight": 8
            }
          },
          {
            "id": "avatar_in_text",
            "component": "Text",
            "text": "🧑‍💻",
            "styles": {
              "fontSize": 14,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "bubble_in_wrap",
            "component": "Column",
            "align": "start",
            "children": [
              "name_in",
              "bubble_in",
              "time_in"
            ]
          },
          {
            "id": "name_in",
            "component": "Text",
            "text": "陈文 · 产品",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 2
            }
          },
          {
            "id": "bubble_in",
            "component": "Row",
            "align": "center",
            "children": [
              "bubble_in_text"
            ],
            "styles": {
              "backgroundColor": "#F1F5F9",
              "borderRadius": 12,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 6,
              "paddingBottom": 6
            }
          },
          {
            "id": "bubble_in_text",
            "component": "Text",
            "text": "新版本原型评审时间调整到明天 10 点，方便吗？",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "time_in",
            "component": "Text",
            "text": "12:16",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 10,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "msg_out",
            "component": "Row",
            "align": "start",
            "justify": "end",
            "children": [
              "bubble_out_wrap"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 6
            }
          },
          {
            "id": "bubble_out_wrap",
            "component": "Column",
            "align": "end",
            "children": [
              "bubble_out",
              "time_out"
            ]
          },
          {
            "id": "bubble_out",
            "component": "Row",
            "align": "center",
            "children": [
              "bubble_out_text"
            ],
            "styles": {
              "backgroundColor": "#2563EB",
              "borderRadius": 12,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 6,
              "paddingBottom": 6
            }
          },
          {
            "id": "bubble_out_text",
            "component": "Text",
            "text": "可以，我把评审材料提前发到群里。",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "time_out",
            "component": "Text",
            "text": "12:17 · 已读",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 10,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "typing_row",
            "component": "Row",
            "align": "center",
            "children": [
              "typing_dot",
              "typing_text"
            ],
            "styles": {
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "typing_dot",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 6,
              "height": 6,
              "borderRadius": 3,
              "backgroundColor": "#10B981",
              "marginRight": 6
            }
          },
          {
            "id": "typing_text",
            "component": "Text",
            "text": "陈文正在输入…",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 9 · 咖啡订单 (adapted from 13_coffee-order.json)
// ---------------------------------------------------------------------------
private const val CARD9_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-coffee-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD9_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-coffee-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header",
              "items",
              "divider",
              "totals",
              "pay_btn"
            ]
          },
          {
            "id": "header",
            "component": "Row",
            "align": "center",
            "children": [
              "shop_emoji",
              "shop_col"
            ]
          },
          {
            "id": "shop_emoji",
            "component": "Text",
            "text": "☕",
            "styles": {
              "fontSize": 22,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 10
            }
          },
          {
            "id": "shop_col",
            "component": "Column",
            "align": "start",
            "children": [
              "shop_name",
              "shop_meta"
            ]
          },
          {
            "id": "shop_name",
            "component": "Text",
            "text": "北山咖啡 · 外带",
            "styles": {
              "color": "#0F172A",
              "fontSize": 15,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "shop_meta",
            "component": "Text",
            "text": "订单 #2451 · 预计 12:30 完成",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "items",
            "component": "Column",
            "align": "stretch",
            "children": [
              "item_1",
              "item_2",
              "item_3"
            ],
            "styles": {
              "marginTop": 10,
              "marginBottom": 0
            }
          },
          {
            "id": "item_1",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "item_1_name",
              "item_1_price"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 4
            }
          },
          {
            "id": "item_1_name",
            "component": "Text",
            "text": "燕麦拿铁 (大) × 1",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_1_price",
            "component": "Text",
            "text": "¥ 32",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_2",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "item_2_name",
              "item_2_price"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 4
            }
          },
          {
            "id": "item_2_name",
            "component": "Text",
            "text": "美式 (中·冰) × 1",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_2_price",
            "component": "Text",
            "text": "¥ 22",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_3",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "item_3_name",
              "item_3_price"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_3_name",
            "component": "Text",
            "text": "手工可颂 × 2",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "item_3_price",
            "component": "Text",
            "text": "¥ 28",
            "styles": {
              "color": "#334155",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "divider",
            "component": "Divider",
            "styles": {
              "marginTop": 8,
              "marginBottom": 8
            }
          },
          {
            "id": "totals",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "total_lbl",
              "total_val"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 10
            }
          },
          {
            "id": "total_lbl",
            "component": "Text",
            "text": "合计",
            "styles": {
              "color": "#64748B",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "total_val",
            "component": "Text",
            "text": "¥ 82.00",
            "styles": {
              "color": "#0F172A",
              "fontSize": 17,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "pay_btn_text",
            "component": "Text",
            "text": "立即支付",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "pay_btn",
            "component": "Button",
            "child": "pay_btn_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "支付成功",
                  "message": "订单 #2451 · ¥ 82.00 已支付"
                }
              }
            },
            "styles": {
              "backgroundColor": "#78350F",
              "borderRadius": 10,
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 8,
              "paddingBottom": 8,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 10 · 球员数据 (adapted from 14_sports-player.json)
// ---------------------------------------------------------------------------
private const val CARD10_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-player-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD10_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-player-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 0,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "hero",
              "stats"
            ]
          },
          {
            "id": "hero",
            "component": "Row",
            "align": "center",
            "children": [
              "num_block",
              "name_col"
            ],
            "styles": {
              "paddingLeft": 16,
              "paddingRight": 16,
              "paddingTop": 12,
              "paddingBottom": 12,
              "backgroundLinearGradient": {
                "direction": "to-right",
                "colorStops": [
                  {
                    "color": "#1E3A8A",
                    "stop": 0.0
                  },
                  {
                    "color": "#3730A3",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "num_block",
            "component": "Column",
            "weight": 0,
            "align": "center",
            "children": [
              "num_text"
            ],
            "styles": {
              "width": 56,
              "height": 56,
              "backgroundColor": "#FBBF24",
              "borderRadius": 12,
              "marginRight": 12
            }
          },
          {
            "id": "num_text",
            "component": "Text",
            "text": "23",
            "styles": {
              "color": "#1E3A8A",
              "fontSize": 30,
              "fontWeight": "bold",
              "letterSpacing": -1,
              "marginTop": 10,
              "marginBottom": 0
            }
          },
          {
            "id": "name_col",
            "component": "Column",
            "align": "start",
            "children": [
              "player_name",
              "player_meta"
            ]
          },
          {
            "id": "player_name",
            "component": "Text",
            "text": "叶小明",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 20,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "player_meta",
            "component": "Text",
            "text": "小前锋 · 星辰队",
            "styles": {
              "color": "#C7D2FE",
              "fontSize": 12,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "stats",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "s1",
              "s2",
              "s3",
              "s4"
            ],
            "styles": {
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 12,
              "paddingBottom": 12
            }
          },
          {
            "id": "s1",
            "component": "Column",
            "align": "center",
            "children": [
              "s1_num",
              "s1_lbl"
            ]
          },
          {
            "id": "s1_num",
            "component": "Text",
            "text": "28.4",
            "styles": {
              "color": "#0F172A",
              "fontSize": 19,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "s1_lbl",
            "component": "Text",
            "text": "得分",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "s2",
            "component": "Column",
            "align": "center",
            "children": [
              "s2_num",
              "s2_lbl"
            ]
          },
          {
            "id": "s2_num",
            "component": "Text",
            "text": "7.8",
            "styles": {
              "color": "#0F172A",
              "fontSize": 19,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "s2_lbl",
            "component": "Text",
            "text": "篮板",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "s3",
            "component": "Column",
            "align": "center",
            "children": [
              "s3_num",
              "s3_lbl"
            ]
          },
          {
            "id": "s3_num",
            "component": "Text",
            "text": "6.2",
            "styles": {
              "color": "#0F172A",
              "fontSize": 19,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "s3_lbl",
            "component": "Text",
            "text": "助攻",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "s4",
            "component": "Column",
            "align": "center",
            "children": [
              "s4_num",
              "s4_lbl"
            ]
          },
          {
            "id": "s4_num",
            "component": "Text",
            "text": "48.2%",
            "styles": {
              "color": "#0F172A",
              "fontSize": 19,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "s4_lbl",
            "component": "Text",
            "text": "命中率",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 0.5,
              "marginTop": 2,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 11 · 账户余额 (adapted from 15_account-balance.json)
// ---------------------------------------------------------------------------
private const val CARD11_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-balance-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD11_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-balance-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#0F172A",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header",
              "balance_row",
              "updated",
              "actions"
            ]
          },
          {
            "id": "header",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "header_lbl",
              "header_eye"
            ]
          },
          {
            "id": "header_lbl",
            "component": "Text",
            "text": "钱包总资产",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "header_eye",
            "component": "Text",
            "text": "👁",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 14,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "balance_row",
            "component": "Row",
            "align": "end",
            "children": [
              "balance_cur",
              "balance_num",
              "balance_decimal"
            ],
            "styles": {
              "marginTop": 8,
              "marginBottom": 0
            }
          },
          {
            "id": "balance_cur",
            "component": "Text",
            "text": "¥",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 18,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 6,
              "marginRight": 4
            }
          },
          {
            "id": "balance_num",
            "component": "Text",
            "text": "12,486",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 34,
              "fontWeight": "bold",
              "letterSpacing": -1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "balance_decimal",
            "component": "Text",
            "text": ".52",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 18,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 5,
              "marginLeft": 2
            }
          },
          {
            "id": "updated",
            "component": "Text",
            "text": "较昨日 +¥ 84.30 · 更新于 5 分钟前",
            "styles": {
              "color": "#10B981",
              "fontSize": 11,
              "marginTop": 4,
              "marginBottom": 10
            }
          },
          {
            "id": "actions",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "btn_in",
              "btn_out"
            ]
          },
          {
            "id": "btn_in_text",
            "component": "Text",
            "text": "充值",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_in",
            "component": "Button",
            "child": "btn_in_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "充值",
                  "message": "请选择充值金额。"
                }
              }
            },
            "styles": {
              "backgroundColor": "#FBBF24",
              "borderRadius": 10,
              "paddingLeft": 22,
              "paddingRight": 22,
              "paddingTop": 7,
              "paddingBottom": 7,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 8,
              "flexGrow": 1
            }
          },
          {
            "id": "btn_out_text",
            "component": "Text",
            "text": "提现",
            "styles": {
              "color": "#F8FAFC",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "btn_out",
            "component": "Button",
            "child": "btn_out_text",
            "action": {
              "event": {
                "name": "toast",
                "context": {
                  "title": "提现",
                  "message": "提现将在 1-3 个工作日到账。"
                }
              }
            },
            "styles": {
              "backgroundColor": "#1E293B",
              "borderRadius": 10,
              "paddingLeft": 22,
              "paddingRight": 22,
              "paddingTop": 7,
              "paddingBottom": 7,
              "marginTop": 0,
              "marginBottom": 0,
              "flexGrow": 1
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 12 · 运动总结 (adapted from 16_workout-summary.json)
// ---------------------------------------------------------------------------
private const val CARD12_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-workout-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD12_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-workout-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header",
              "divider",
              "metrics",
              "date_text"
            ]
          },
          {
            "id": "header",
            "component": "Row",
            "align": "center",
            "children": [
              "head_emoji",
              "head_col"
            ]
          },
          {
            "id": "head_emoji",
            "component": "Text",
            "text": "🏃",
            "styles": {
              "fontSize": 26,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 10
            }
          },
          {
            "id": "head_col",
            "component": "Column",
            "align": "start",
            "children": [
              "head_title",
              "head_sub"
            ]
          },
          {
            "id": "head_title",
            "component": "Text",
            "text": "晨间跑步",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "head_sub",
            "component": "Text",
            "text": "沙河绿道 · 心率区间 3",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "divider",
            "component": "Divider",
            "styles": {
              "marginTop": 10,
              "marginBottom": 10
            }
          },
          {
            "id": "metrics",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "m1",
              "m2",
              "m3"
            ]
          },
          {
            "id": "m1",
            "component": "Column",
            "align": "center",
            "children": [
              "m1_num",
              "m1_lbl"
            ]
          },
          {
            "id": "m1_num",
            "component": "Text",
            "text": "6.28",
            "styles": {
              "color": "#10B981",
              "fontSize": 22,
              "fontWeight": "bold",
              "letterSpacing": -0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "m1_lbl",
            "component": "Text",
            "text": "公里",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "m2",
            "component": "Column",
            "align": "center",
            "children": [
              "m2_num",
              "m2_lbl"
            ]
          },
          {
            "id": "m2_num",
            "component": "Text",
            "text": "38'12″",
            "styles": {
              "color": "#10B981",
              "fontSize": 22,
              "fontWeight": "bold",
              "letterSpacing": -0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "m2_lbl",
            "component": "Text",
            "text": "时长",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "m3",
            "component": "Column",
            "align": "center",
            "children": [
              "m3_num",
              "m3_lbl"
            ]
          },
          {
            "id": "m3_num",
            "component": "Text",
            "text": "482",
            "styles": {
              "color": "#10B981",
              "fontSize": 22,
              "fontWeight": "bold",
              "letterSpacing": -0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "m3_lbl",
            "component": "Text",
            "text": "千卡",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "date_text",
            "component": "Text",
            "text": "09 月 09 日 06:18 · 天气晴 · 22°",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 10,
              "marginBottom": 0,
              "textAlign": "center"
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 13 · 餐厅 (adapted from 20_restaurant-card.json)
// ---------------------------------------------------------------------------
private const val CARD13_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-restaurant-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD13_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-restaurant-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 0,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "cover",
              "body"
            ]
          },
          {
            "id": "cover",
            "component": "Row",
            "align": "end",
            "justify": "end",
            "children": [
              "open_pill"
            ],
            "styles": {
              "height": 86,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#FCA5A5",
                    "stop": 0.0
                  },
                  {
                    "color": "#DC2626",
                    "stop": 1.0
                  }
                ]
              },
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 10,
              "paddingBottom": 10
            }
          },
          {
            "id": "open_pill",
            "component": "Row",
            "align": "center",
            "children": [
              "open_text"
            ],
            "styles": {
              "backgroundColor": "#FFFFFF",
              "borderRadius": 100,
              "paddingLeft": 8,
              "paddingRight": 8,
              "paddingTop": 2,
              "paddingBottom": 2
            }
          },
          {
            "id": "open_text",
            "component": "Text",
            "text": "营业中",
            "styles": {
              "color": "#16A34A",
              "fontSize": 11,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "body",
            "component": "Column",
            "align": "stretch",
            "children": [
              "rest_name",
              "rest_cat",
              "meta_row",
              "addr_text"
            ],
            "styles": {
              "paddingLeft": 14,
              "paddingRight": 14,
              "paddingTop": 10,
              "paddingBottom": 12
            }
          },
          {
            "id": "rest_name",
            "component": "Text",
            "text": "好吃火锅店",
            "styles": {
              "color": "#0F172A",
              "fontSize": 17,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "rest_cat",
            "component": "Text",
            "text": "中餐 · 火锅 · ¥¥¥",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "meta_row",
            "component": "Row",
            "align": "center",
            "children": [
              "star_text",
              "rate_text",
              "dot_sep",
              "dist_text"
            ],
            "styles": {
              "marginTop": 8,
              "marginBottom": 0
            }
          },
          {
            "id": "star_text",
            "component": "Text",
            "text": "★",
            "styles": {
              "color": "#F59E0B",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 2
            }
          },
          {
            "id": "rate_text",
            "component": "Text",
            "text": "4.7",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dot_sep",
            "component": "Text",
            "text": " · ",
            "styles": {
              "color": "#CBD5E1",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "dist_text",
            "component": "Text",
            "text": "1.2 km · 步行 15 分钟",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "addr_text",
            "component": "Text",
            "text": "📍 沙河路 128 号 · 山水人家 3 楼",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 6,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 14 · 物流 (adapted from 21_shipping-status.json)
// ---------------------------------------------------------------------------
private const val CARD14_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-shipping-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD14_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-shipping-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "header",
              "tracking",
              "divider",
              "steps",
              "eta"
            ]
          },
          {
            "id": "header",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "header_lbl",
              "stage_pill"
            ]
          },
          {
            "id": "header_lbl",
            "component": "Text",
            "text": "📦 快鸟速运",
            "styles": {
              "color": "#0F172A",
              "fontSize": 15,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "stage_pill",
            "component": "Row",
            "align": "center",
            "children": [
              "stage_text"
            ],
            "styles": {
              "backgroundColor": "#DBEAFE",
              "borderRadius": 10,
              "paddingLeft": 8,
              "paddingRight": 8,
              "paddingTop": 2,
              "paddingBottom": 2
            }
          },
          {
            "id": "stage_text",
            "component": "Text",
            "text": "派送中",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 11,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "tracking",
            "component": "Text",
            "text": "运单 SF1082849017GD",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "letterSpacing": 0.5,
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "divider",
            "component": "Divider",
            "styles": {
              "marginTop": 10,
              "marginBottom": 8
            }
          },
          {
            "id": "steps",
            "component": "Column",
            "align": "stretch",
            "children": [
              "step_1",
              "step_2",
              "step_3"
            ]
          },
          {
            "id": "step_1",
            "component": "Row",
            "align": "center",
            "children": [
              "step_1_dot",
              "step_1_col"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 6
            }
          },
          {
            "id": "step_1_dot",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "borderRadius": 4,
              "backgroundColor": "#2563EB",
              "marginRight": 10
            }
          },
          {
            "id": "step_1_col",
            "component": "Column",
            "align": "start",
            "children": [
              "step_1_title",
              "step_1_time"
            ]
          },
          {
            "id": "step_1_title",
            "component": "Text",
            "text": "快递员正在派送中",
            "styles": {
              "color": "#0F172A",
              "fontSize": 13,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "step_1_time",
            "component": "Text",
            "text": "09-09 11:52 · 联系电话 138****2314",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "step_2",
            "component": "Row",
            "align": "center",
            "children": [
              "step_2_dot",
              "step_2_col"
            ],
            "styles": {
              "marginTop": 0,
              "marginBottom": 6
            }
          },
          {
            "id": "step_2_dot",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "borderRadius": 4,
              "backgroundColor": "#94A3B8",
              "marginRight": 10
            }
          },
          {
            "id": "step_2_col",
            "component": "Column",
            "align": "start",
            "children": [
              "step_2_title",
              "step_2_time"
            ]
          },
          {
            "id": "step_2_title",
            "component": "Text",
            "text": "到达苏州区分拣中心",
            "styles": {
              "color": "#64748B",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "step_2_time",
            "component": "Text",
            "text": "09-09 06:14",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "step_3",
            "component": "Row",
            "align": "center",
            "children": [
              "step_3_dot",
              "step_3_col"
            ]
          },
          {
            "id": "step_3_dot",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 8,
              "height": 8,
              "borderRadius": 4,
              "backgroundColor": "#94A3B8",
              "marginRight": 10
            }
          },
          {
            "id": "step_3_col",
            "component": "Column",
            "align": "start",
            "children": [
              "step_3_title",
              "step_3_time"
            ]
          },
          {
            "id": "step_3_title",
            "component": "Text",
            "text": "上海转运中心已发出",
            "styles": {
              "color": "#64748B",
              "fontSize": 13,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "step_3_time",
            "component": "Text",
            "text": "09-08 21:37",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "eta",
            "component": "Text",
            "text": "预计今日 13:30 前送达",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 12,
              "fontWeight": "medium",
              "marginTop": 8,
              "marginBottom": 0,
              "textAlign": "center"
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 15 · 银行卡 (adapted from 22_credit-card.json)
// ---------------------------------------------------------------------------
private const val CARD15_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-credit-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD15_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-credit-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 18,
              "borderRadius": 14,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#312E81",
                    "stop": 0.0
                  },
                  {
                    "color": "#6D28D9",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "stretch",
            "children": [
              "brand_row",
              "chip_row",
              "card_no",
              "detail_row"
            ]
          },
          {
            "id": "brand_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "brand_lbl",
              "brand_name"
            ]
          },
          {
            "id": "brand_lbl",
            "component": "Text",
            "text": "星辰银行",
            "styles": {
              "color": "#E9D5FF",
              "fontSize": 12,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "brand_name",
            "component": "Text",
            "text": "VISA",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 2,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "chip_row",
            "component": "Row",
            "align": "center",
            "children": [
              "chip_block",
              "chip_wave"
            ],
            "styles": {
              "marginTop": 16,
              "marginBottom": 0
            }
          },
          {
            "id": "chip_block",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 36,
              "height": 26,
              "backgroundColor": "#FCD34D",
              "borderRadius": 5,
              "marginRight": 10
            }
          },
          {
            "id": "chip_wave",
            "component": "Text",
            "text": "))))",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 14,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "card_no",
            "component": "Text",
            "text": "4823  8901  ****  2314",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 19,
              "fontWeight": "medium",
              "letterSpacing": 3,
              "marginTop": 16,
              "marginBottom": 16
            }
          },
          {
            "id": "detail_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "holder_col",
              "expire_col"
            ]
          },
          {
            "id": "holder_col",
            "component": "Column",
            "align": "start",
            "children": [
              "holder_lbl",
              "holder_val"
            ]
          },
          {
            "id": "holder_lbl",
            "component": "Text",
            "text": "持卡人",
            "styles": {
              "color": "#C4B5FD",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "holder_val",
            "component": "Text",
            "text": "LIN WEN WEN",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "medium",
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "expire_col",
            "component": "Column",
            "align": "end",
            "children": [
              "expire_lbl",
              "expire_val"
            ]
          },
          {
            "id": "expire_lbl",
            "component": "Text",
            "text": "有效期",
            "styles": {
              "color": "#C4B5FD",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "expire_val",
            "component": "Text",
            "text": "08 / 28",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 13,
              "fontWeight": "medium",
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 16 · 步数 (adapted from 23_step-counter.json)
// ---------------------------------------------------------------------------
private const val CARD16_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-step-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD16_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-step-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 16,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "center",
            "children": [
              "header",
              "big_num",
              "goal_row",
              "progress_track",
              "stats_row"
            ]
          },
          {
            "id": "header",
            "component": "Text",
            "text": "👟 今日步数",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "fontWeight": "medium",
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "big_num",
            "component": "Text",
            "text": "8,342",
            "styles": {
              "color": "#0F172A",
              "fontSize": 42,
              "fontWeight": "bold",
              "letterSpacing": -1,
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "goal_row",
            "component": "Row",
            "align": "center",
            "children": [
              "goal_text",
              "goal_pct"
            ],
            "styles": {
              "marginTop": 2,
              "marginBottom": 8
            }
          },
          {
            "id": "goal_text",
            "component": "Text",
            "text": "目标 10,000 步 · ",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "goal_pct",
            "component": "Text",
            "text": "83%",
            "styles": {
              "color": "#10B981",
              "fontSize": 12,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "progress_track",
            "component": "Row",
            "align": "start",
            "children": [
              "progress_fill"
            ],
            "styles": {
              "height": 8,
              "backgroundColor": "#F1F5F9",
              "borderRadius": 4,
              "marginTop": 0,
              "marginBottom": 12
            }
          },
          {
            "id": "progress_fill",
            "component": "Column",
            "weight": 0,
            "children": [],
            "styles": {
              "width": 210,
              "height": 8,
              "backgroundColor": "#10B981",
              "borderRadius": 4
            }
          },
          {
            "id": "stats_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "st1",
              "st2",
              "st3"
            ]
          },
          {
            "id": "st1",
            "component": "Column",
            "align": "center",
            "children": [
              "st1_num",
              "st1_lbl"
            ]
          },
          {
            "id": "st1_num",
            "component": "Text",
            "text": "5.8",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "st1_lbl",
            "component": "Text",
            "text": "公里",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "st2",
            "component": "Column",
            "align": "center",
            "children": [
              "st2_num",
              "st2_lbl"
            ]
          },
          {
            "id": "st2_num",
            "component": "Text",
            "text": "312",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "st2_lbl",
            "component": "Text",
            "text": "千卡",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "st3",
            "component": "Column",
            "align": "center",
            "children": [
              "st3_num",
              "st3_lbl"
            ]
          },
          {
            "id": "st3_num",
            "component": "Text",
            "text": "68",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "st3_lbl",
            "component": "Text",
            "text": "活跃分钟",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 17 · 数据统计 (adapted from 27_stats-card.json)
// ---------------------------------------------------------------------------
private const val CARD17_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-stats-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD17_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-stats-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 14,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "start",
            "children": [
              "title",
              "value_row",
              "trend_row"
            ]
          },
          {
            "id": "title",
            "component": "Text",
            "text": "月度活跃用户",
            "styles": {
              "color": "#64748B",
              "fontSize": 12,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "value_row",
            "component": "Row",
            "align": "end",
            "children": [
              "val_num",
              "val_unit"
            ],
            "styles": {
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "val_num",
            "component": "Text",
            "text": "128.4",
            "styles": {
              "color": "#0F172A",
              "fontSize": 30,
              "fontWeight": "bold",
              "letterSpacing": -0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "val_unit",
            "component": "Text",
            "text": "万",
            "styles": {
              "color": "#64748B",
              "fontSize": 15,
              "fontWeight": "medium",
              "marginTop": 0,
              "marginBottom": 4,
              "marginLeft": 4
            }
          },
          {
            "id": "trend_row",
            "component": "Row",
            "align": "center",
            "children": [
              "arrow_text",
              "trend_num",
              "trend_desc"
            ],
            "styles": {
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "arrow_text",
            "component": "Text",
            "text": "▲",
            "styles": {
              "color": "#16A34A",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 2
            }
          },
          {
            "id": "trend_num",
            "component": "Text",
            "text": "12.4%",
            "styles": {
              "color": "#16A34A",
              "fontSize": 13,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "trend_desc",
            "component": "Text",
            "text": "  较上月",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 18 · 倒计时 (adapted from 28_countdown-timer.json)
// ---------------------------------------------------------------------------
private const val CARD18_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-countdown-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD18_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-countdown-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 16,
              "borderRadius": 14,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#0891B2",
                    "stop": 0.0
                  },
                  {
                    "color": "#0E7490",
                    "stop": 1.0
                  }
                ]
              }
            }
          },
          {
            "id": "main",
            "component": "Column",
            "align": "center",
            "children": [
              "title",
              "time_row",
              "desc"
            ]
          },
          {
            "id": "title",
            "component": "Text",
            "text": "距发布会开始还有",
            "styles": {
              "color": "#CFFAFE",
              "fontSize": 12,
              "letterSpacing": 1,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "time_row",
            "component": "Row",
            "align": "center",
            "justify": "spaceBetween",
            "children": [
              "b_day",
              "b_hour",
              "b_min",
              "b_sec"
            ],
            "styles": {
              "marginTop": 10,
              "marginBottom": 10
            }
          },
          {
            "id": "b_day",
            "component": "Column",
            "align": "center",
            "children": [
              "b_day_n",
              "b_day_l"
            ],
            "styles": {
              "backgroundColor": "#0C4A6E",
              "borderRadius": 10,
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 8,
              "paddingBottom": 6,
              "marginRight": 6
            }
          },
          {
            "id": "b_day_n",
            "component": "Text",
            "text": "07",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 24,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "b_day_l",
            "component": "Text",
            "text": "天",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "b_hour",
            "component": "Column",
            "align": "center",
            "children": [
              "b_hour_n",
              "b_hour_l"
            ],
            "styles": {
              "backgroundColor": "#0C4A6E",
              "borderRadius": 10,
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 8,
              "paddingBottom": 6,
              "marginRight": 6
            }
          },
          {
            "id": "b_hour_n",
            "component": "Text",
            "text": "12",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 24,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "b_hour_l",
            "component": "Text",
            "text": "时",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "b_min",
            "component": "Column",
            "align": "center",
            "children": [
              "b_min_n",
              "b_min_l"
            ],
            "styles": {
              "backgroundColor": "#0C4A6E",
              "borderRadius": 10,
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 8,
              "paddingBottom": 6,
              "marginRight": 6
            }
          },
          {
            "id": "b_min_n",
            "component": "Text",
            "text": "34",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 24,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "b_min_l",
            "component": "Text",
            "text": "分",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "b_sec",
            "component": "Column",
            "align": "center",
            "children": [
              "b_sec_n",
              "b_sec_l"
            ],
            "styles": {
              "backgroundColor": "#0C4A6E",
              "borderRadius": 10,
              "paddingLeft": 12,
              "paddingRight": 12,
              "paddingTop": 8,
              "paddingBottom": 6
            }
          },
          {
            "id": "b_sec_n",
            "component": "Text",
            "text": "52",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 24,
              "fontWeight": "bold",
              "letterSpacing": 0.5,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "b_sec_l",
            "component": "Text",
            "text": "秒",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 10,
              "letterSpacing": 1,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "desc",
            "component": "Text",
            "text": "09 月 16 日 20:00 · 山上礼堂",
            "styles": {
              "color": "#E0F2FE",
              "fontSize": 12,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

// ---------------------------------------------------------------------------
// Card kind 19 · 电影 (adapted from 29_movie-card.json)
// ---------------------------------------------------------------------------
private const val CARD19_CREATE_SURFACE_JSON = """
    {
      "version": "1.0",
      "createSurface": {
        "surfaceId": "native-movie-card-%SEQ%",
        "catalogId": "kuikly_a2ui_catalog_v1_0.json"
      }
    }
"""
private const val CARD19_UPDATE_COMPONENTS_JSON = """
    {
      "version": "1.0",
      "updateComponents": {
        "surfaceId": "native-movie-card-%SEQ%",
        "components": [
          {
            "id": "root",
            "component": "Card",
            "child": "main",
            "styles": {
              "padding": 12,
              "backgroundColor": "#FFFFFF",
              "borderRadius": 14
            }
          },
          {
            "id": "main",
            "component": "Row",
            "align": "start",
            "children": [
              "poster",
              "info_col"
            ]
          },
          {
            "id": "poster",
            "component": "Row",
            "weight": 0,
            "align": "center",
            "justify": "center",
            "children": [
              "poster_emoji"
            ],
            "styles": {
              "width": 72,
              "height": 100,
              "borderRadius": 8,
              "backgroundLinearGradient": {
                "direction": "to-bottom-right",
                "colorStops": [
                  {
                    "color": "#F472B6",
                    "stop": 0.0
                  },
                  {
                    "color": "#7C3AED",
                    "stop": 1.0
                  }
                ]
              },
              "marginRight": 12
            }
          },
          {
            "id": "poster_emoji",
            "component": "Text",
            "text": "🎬",
            "styles": {
              "fontSize": 32,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "info_col",
            "component": "Column",
            "align": "start",
            "children": [
              "movie_title",
              "movie_meta",
              "rating_row",
              "desc_text",
              "tag_row"
            ],
            "styles": {
              "flexGrow": 1
            }
          },
          {
            "id": "movie_title",
            "component": "Text",
            "text": "星海之上",
            "styles": {
              "color": "#0F172A",
              "fontSize": 16,
              "fontWeight": "bold",
              "letterSpacing": 0.3,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "movie_meta",
            "component": "Text",
            "text": "科幻 · 冒险 · 2026 · 128 分钟",
            "styles": {
              "color": "#64748B",
              "fontSize": 11,
              "marginTop": 2,
              "marginBottom": 0
            }
          },
          {
            "id": "rating_row",
            "component": "Row",
            "align": "center",
            "children": [
              "star",
              "rate",
              "rate_total"
            ],
            "styles": {
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "star",
            "component": "Text",
            "text": "★",
            "styles": {
              "color": "#F59E0B",
              "fontSize": 14,
              "marginTop": 0,
              "marginBottom": 0,
              "marginRight": 2
            }
          },
          {
            "id": "rate",
            "component": "Text",
            "text": "8.9",
            "styles": {
              "color": "#F59E0B",
              "fontSize": 15,
              "fontWeight": "bold",
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "rate_total",
            "component": "Text",
            "text": "  / 12.4 万人评价",
            "styles": {
              "color": "#94A3B8",
              "fontSize": 11,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "desc_text",
            "component": "Text",
            "text": "孤船启航，星河深处，一次关于回家的告别。",
            "styles": {
              "color": "#475569",
              "fontSize": 12,
              "marginTop": 6,
              "marginBottom": 0
            }
          },
          {
            "id": "tag_row",
            "component": "Row",
            "align": "center",
            "children": [
              "tag_imax",
              "tag_hot"
            ],
            "styles": {
              "marginTop": 8,
              "marginBottom": 0
            }
          },
          {
            "id": "tag_imax",
            "component": "Text",
            "text": "IMAX",
            "styles": {
              "color": "#1D4ED8",
              "fontSize": 10,
              "fontWeight": "bold",
              "backgroundColor": "#DBEAFE",
              "borderRadius": 4,
              "paddingLeft": 6,
              "paddingRight": 6,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginRight": 6,
              "marginTop": 0,
              "marginBottom": 0
            }
          },
          {
            "id": "tag_hot",
            "component": "Text",
            "text": "🔥 热映",
            "styles": {
              "color": "#B91C1C",
              "fontSize": 10,
              "fontWeight": "bold",
              "backgroundColor": "#FEE2E2",
              "borderRadius": 4,
              "paddingLeft": 6,
              "paddingRight": 6,
              "paddingTop": 2,
              "paddingBottom": 2,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
"""

/**
 * All 20 (createSurface, updateComponents) template pairs, indexed by kind.
 * Callers use [sampleCardEnvelopes] instead of touching this array directly.
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

/**
 * Build the pair of envelope strings for a given [kind] (0..19) with a
 * unique surfaceId derived from [index] (1-based sequence number). Both
 * envelopes are `%SEQ%`-substituted templates copied verbatim from the
 * NativeCardDemoActivity catalogue.
 *
 * @return `(createSurfaceJson, updateComponentsJson)` — feed them to the
 *   host A2UISurface via `writeStream()` in order.
 */
internal fun sampleCardEnvelopes(index: Int, kind: Int): Pair<String, String> {
    val safeKind = ((kind % SAMPLE_CARD_KIND_COUNT) + SAMPLE_CARD_KIND_COUNT) % SAMPLE_CARD_KIND_COUNT
    val (createTemplate, updateTemplate) = CARD_JSON_PAIRS[safeKind]
    val seq = (index + 1).toString()
    return createTemplate.replace("%SEQ%", seq) to updateTemplate.replace("%SEQ%", seq)
}

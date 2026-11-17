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

package com.tencent.kuikly.a2ui.golden

/**
 * 由 ka2ui golden --accept 生成，共 370 条全字段快照。
 * 记录既有行为，不代表规范正确性；更新前必须核对行为差异。
 */
internal object A2UIGoldenBaseline {
    val snapshots: Map<String, String> = mapOf(
        """v0_9.binding.01_absolute_path.1_absolute_path_string""" to """#0 createSurface surface="db-ap-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ap-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ap-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ap-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "绑定 /user/name → Text"
#4 createComponent surface="db-ap-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 updateData surface="db-ap-1"
  data:
    "user" = {"name": "Alice"}
#6 updateComponent surface="db-ap-1" tag=4 view="KRRichTextView"
  props:
    "text" = "Alice"
#state dataModel "{\"user\":{\"name\":\"Alice\"}}"
#state actions {}
""",
        """v0_9.binding.01_absolute_path.2_absolute_path_number""" to """#0 createSurface surface="db-ap-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ap-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ap-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ap-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "音量（绑定 /settings/volume，number）"
#4 createComponent surface="db-ap-2" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="db-ap-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "音量"
#6 createComponent surface="db-ap-2" tag=4 parent=6 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "settings/volume"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#7 createComponent surface="db-ap-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前音量："
#8 updateData surface="db-ap-2"
  data:
    "settings" = {"volume": 65}
#9 updateComponent surface="db-ap-2" tag=5 view="KRRichTextView"
  props:
    "text" = "当前音量：65"
#10 updateComponent surface="db-ap-2" tag=4 view="KRSliderView"
  props:
    "value" = 65
#11 updateComponent surface="db-ap-2" tag=7 view="KRRichTextView"
  props:
    "text" = "音量"
#state dataModel "{\"settings\":{\"volume\":65}}"
#state actions {}
""",
        """v0_9.binding.01_absolute_path.3_absolute_path_boolean""" to """#0 createSurface surface="db-ap-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ap-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ap-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ap-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "订阅（绑定 /prefs/newsletter，boolean）"
#4 createComponent surface="db-ap-3" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#5 createComponent surface="db-ap-3" tag=4 parent=6 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "prefs/newsletter"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#6 createComponent surface="db-ap-3" tag=7 parent=6 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "接收营销邮件"
#7 createComponent surface="db-ap-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前值："
#8 updateData surface="db-ap-3"
  data:
    "prefs" = {"newsletter": true}
#9 updateComponent surface="db-ap-3" tag=5 view="KRRichTextView"
  props:
    "text" = "当前值：true"
#10 updateComponent surface="db-ap-3" tag=4 view="KRCheckBoxView"
  props:
    "checked" = true
#state dataModel "{\"prefs\":{\"newsletter\":true}}"
#state actions {}
""",
        """v0_9.binding.01_absolute_path.4_absolute_path_deep_json_pointer""" to """#0 createSurface surface="db-ap-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ap-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ap-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ap-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "深层 JSON Pointer"
#4 createComponent surface="db-ap-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "客户："
#5 createComponent surface="db-ap-4" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "首件价格："
#6 updateData surface="db-ap-4"
  data:
    "order" = {"customer": {"name": "王小明", "vip": true}, "items": [{"price": 19.99, "sku": "A-1"}, {"price": 5.5, "sku": "A-2"}]}
#7 updateComponent surface="db-ap-4" tag=4 view="KRRichTextView"
  props:
    "text" = "客户：王小明"
#8 updateComponent surface="db-ap-4" tag=5 view="KRRichTextView"
  props:
    "text" = "首件价格：${'$'}19.99"
#state dataModel "{\"order\":{\"customer\":{\"name\":\"王小明\",\"vip\":true},\"items\":[{\"sku\":\"A-1\",\"price\":19.99},{\"sku\":\"A-2\",\"price\":5.5}]}}"
#state actions {}
""",
        """v0_9.binding.02_data_model_upsert.1_upsert_partial""" to """#0 createSurface surface="db-du-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-du-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-du-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-du-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "upsert：只更新 /user/city"
#4 createComponent surface="db-du-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "姓名："
#5 createComponent surface="db-du-1" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "城市："
#6 updateData surface="db-du-1"
  data:
    "user" = {"city": "深圳", "name": "Alice"}
#7 updateComponent surface="db-du-1" tag=4 view="KRRichTextView"
  props:
    "text" = "姓名：Alice"
#8 updateComponent surface="db-du-1" tag=5 view="KRRichTextView"
  props:
    "text" = "城市：深圳"
#9 updateData surface="db-du-1"
  data:
    "user/city" = "上海"
#10 updateComponent surface="db-du-1" tag=5 view="KRRichTextView"
  props:
    "text" = "城市：上海"
#11 updateComponent surface="db-du-1" tag=4 view="KRRichTextView"
  props:
    "text" = "姓名：Alice"
#state dataModel "{\"user\":{\"name\":\"Alice\",\"city\":\"上海\"}}"
#state actions {}
""",
        """v0_9.binding.02_data_model_upsert.2_upsert_create_missing""" to """#0 createSurface surface="db-du-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-du-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-du-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-du-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "upsert：从无到有创建 /profile/email"
#4 createComponent surface="db-du-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "姓名："
#5 createComponent surface="db-du-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "邮箱："
#6 updateData surface="db-du-2"
  data:
    "profile" = {"name": "Bob"}
#7 updateComponent surface="db-du-2" tag=4 view="KRRichTextView"
  props:
    "text" = "姓名：Bob"
#8 updateComponent surface="db-du-2" tag=5 view="KRRichTextView"
  props:
    "text" = "邮箱："
#9 updateData surface="db-du-2"
  data:
    "profile/email" = "bob@example.com"
#10 updateComponent surface="db-du-2" tag=5 view="KRRichTextView"
  props:
    "text" = "邮箱：bob@example.com"
#11 updateComponent surface="db-du-2" tag=4 view="KRRichTextView"
  props:
    "text" = "姓名：Bob"
#state dataModel "{\"profile\":{\"name\":\"Bob\",\"email\":\"bob@example.com\"}}"
#state actions {}
""",
        """v0_9.binding.02_data_model_upsert.3_replace_entire_model""" to """#0 createSurface surface="db-du-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-du-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-du-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-du-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "整体替换 Data Model"
#4 createComponent surface="db-du-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前展示："
#5 createComponent surface="db-du-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "备注："
#6 updateData surface="db-du-3"
  data:
    "current" = {"label": "第 1 版", "note": "初始版本"}
    "extraField" = "legacy"
#7 updateComponent surface="db-du-3" tag=4 view="KRRichTextView"
  props:
    "text" = "当前展示：第 1 版"
#8 updateComponent surface="db-du-3" tag=5 view="KRRichTextView"
  props:
    "text" = "备注：初始版本"
#9 updateData surface="db-du-3"
  data:
    "current" = {"label": "第 2 版", "note": "整体替换后 extraField 被清除"}
#10 updateComponent surface="db-du-3" tag=4 view="KRRichTextView"
  props:
    "text" = "当前展示：第 2 版"
#11 updateComponent surface="db-du-3" tag=5 view="KRRichTextView"
  props:
    "text" = "备注：整体替换后 extraField 被清除"
#state dataModel "{\"current\":{\"label\":\"第 2 版\",\"note\":\"整体替换后 extraField 被清除\"}}"
#state actions {}
""",
        """v0_9.binding.02_data_model_upsert.4_remove_field""" to """#0 createSurface surface="db-upsert-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-upsert-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-upsert-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-upsert-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "path 无 value 表示 remove（规范条款）"
#4 createComponent surface="db-upsert-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "/a （未删）："
#5 createComponent surface="db-upsert-4" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[]"
#6 createComponent surface="db-upsert-4" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "/b （规范要求移除后应显示空）："
#7 createComponent surface="db-upsert-4" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[]"
#8 createComponent surface="db-upsert-4" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "/c （未删）："
#9 createComponent surface="db-upsert-4" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[]"
#10 createComponent surface="db-upsert-4" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "规范期望：[A] [] [C]。若显示 [A] [B] [C] 则 remove 未生效；若显示 [A] [null] [C] 或 [A] [B] [C] 均属未完全对齐规范。"
#11 updateData surface="db-upsert-4"
  data:
    "a" = "A"
    "b" = "B"
    "c" = "C"
#12 updateComponent surface="db-upsert-4" tag=5 view="KRRichTextView"
  props:
    "text" = "[A]"
#13 updateComponent surface="db-upsert-4" tag=7 view="KRRichTextView"
  props:
    "text" = "[B]"
#14 updateComponent surface="db-upsert-4" tag=9 view="KRRichTextView"
  props:
    "text" = "[C]"
#15 updateComponent surface="db-upsert-4" tag=7 view="KRRichTextView"
  props:
    "text" = "[]"
#state dataModel "{\"a\":\"A\",\"c\":\"C\"}"
#state actions {}
""",
        """v0_9.binding.03_child_list_template.1_column_template""" to """#0 createSurface surface="db-cl-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cl-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cl-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cl-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "今日 TODO"
#4 createComponent surface="db-cl-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-cl-1"
  data:
    "todos" = [{"done": true, "title": "阅读 A2UI 协议规范"}, {"done": false, "title": "补齐数据绑定测试用例"}, {"done": false, "title": "跑通 Android / iOS 双端"}]
#6 createComponent surface="db-cl-1" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 阅读 A2UI 协议规范"
#7 createComponent surface="db-cl-1" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 补齐数据绑定测试用例"
#8 createComponent surface="db-cl-1" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 跑通 Android / iOS 双端"
#state dataModel "{\"todos\":[{\"title\":\"阅读 A2UI 协议规范\",\"done\":true},{\"title\":\"补齐数据绑定测试用例\",\"done\":false},{\"title\":\"跑通 Android / iOS 双端\",\"done\":false}]}"
#state actions {}
""",
        """v0_9.binding.03_child_list_template.2_row_template""" to """#0 createSurface surface="db-cl-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cl-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cl-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cl-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "热门标签"
#4 createComponent surface="db-cl-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#5 updateData surface="db-cl-2"
  data:
    "tags" = [{"label": "#Kotlin"}, {"label": "#Multiplatform"}, {"label": "#A2UI"}, {"label": "#DSL"}]
#6 createComponent surface="db-cl-2" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "#Kotlin"
#7 createComponent surface="db-cl-2" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "#Multiplatform"
#8 createComponent surface="db-cl-2" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "#A2UI"
#9 createComponent surface="db-cl-2" tag=9 parent=4 index=3 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "#DSL"
#state dataModel "{\"tags\":[{\"label\":\"#Kotlin\"},{\"label\":\"#Multiplatform\"},{\"label\":\"#A2UI\"},{\"label\":\"#DSL\"}]}"
#state actions {}
""",
        """v0_9.binding.03_child_list_template.3_template_append_update""" to """#0 createSurface surface="db-cl-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cl-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cl-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cl-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "追加数据触发模板重展开"
#4 createComponent surface="db-cl-3" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-cl-3"
  data:
    "list" = [{"index": 1, "name": "First"}, {"index": 2, "name": "Second"}]
#6 createComponent surface="db-cl-3" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1. First"
#7 createComponent surface="db-cl-3" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2. Second"
#8 updateData surface="db-cl-3"
  data:
    "list" = [{"index": 1, "name": "First"}, {"index": 2, "name": "Second"}, {"index": 3, "name": "Third（新增）"}, {"index": 4, "name": "Fourth（新增）"}]
#9 createComponent surface="db-cl-3" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3. Third（新增）"
#10 createComponent surface="db-cl-3" tag=9 parent=4 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "4. Fourth（新增）"
#state dataModel "{\"list\":[{\"index\":1,\"name\":\"First\"},{\"index\":2,\"name\":\"Second\"},{\"index\":3,\"name\":\"Third（新增）\"},{\"index\":4,\"name\":\"Fourth（新增）\"}]}"
#state actions {}
""",
        """v0_9.binding.03_child_list_template.4_edge_empty_undefined_nonarray""" to """#0 createSurface surface="db-cl-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cl-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cl-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cl-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "ChildList 模板 · 数据边界"
#4 createComponent surface="db-cl-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "① 空数组 /items_empty = []（期望 0 实例）"
#5 createComponent surface="db-cl-4" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#6 createComponent surface="db-cl-4" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "② 未定义 path /items_missing（期望 0 实例）"
#7 createComponent surface="db-cl-4" tag=7 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#8 createComponent surface="db-cl-4" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "③ 非数组值 /items_nonarray = 'not_a_list'（期望 0 实例，不 crash）"
#9 createComponent surface="db-cl-4" tag=9 parent=2 index=6 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#10 createComponent surface="db-cl-4" tag=11 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "三行 Column 都应为空。任何 'should_not_render' 出现即失败。"
#11 updateData surface="db-cl-4"
  data:
    "items_empty" = []
    "items_nonarray" = "not_a_list"
#state dataModel "{\"items_empty\":[],\"items_nonarray\":\"not_a_list\"}"
#state actions {}
""",
        """v0_9.binding.04_relative_path.1_relative_path_binding""" to """#0 createSurface surface="db-rp-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-rp-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-rp-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-rp-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "相对 path 绑定：${'$'}{name}"
#4 createComponent surface="db-rp-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-rp-1"
  data:
    "friends" = [{"name": "Alice"}, {"name": "Bob"}, {"name": "Carol"}]
#6 createComponent surface="db-rp-1" tag=8 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="db-rp-1" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "👤"
#8 createComponent surface="db-rp-1" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Alice"
#9 createComponent surface="db-rp-1" tag=11 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="db-rp-1" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "👤"
#11 createComponent surface="db-rp-1" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Bob"
#12 createComponent surface="db-rp-1" tag=14 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="db-rp-1" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "👤"
#14 createComponent surface="db-rp-1" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Carol"
#state dataModel "{\"friends\":[{\"name\":\"Alice\"},{\"name\":\"Bob\"},{\"name\":\"Carol\"}]}"
#state actions {}
""",
        """v0_9.binding.04_relative_path.2_relative_multi_fields""" to """#0 createSurface surface="db-rp-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-rp-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-rp-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-rp-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "参会人员"
#4 createComponent surface="db-rp-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-rp-2"
  data:
    "people" = [{"age": 28, "firstName": "小明", "lastName": "王"}, {"age": 32, "firstName": "小红", "lastName": "李"}, {"age": 24, "firstName": "小刚", "lastName": "赵"}]
#6 createComponent surface="db-rp-2" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "小明 王（28 岁）"
#7 createComponent surface="db-rp-2" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "小红 李（32 岁）"
#8 createComponent surface="db-rp-2" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "小刚 赵（24 岁）"
#state dataModel "{\"people\":[{\"firstName\":\"小明\",\"lastName\":\"王\",\"age\":28},{\"firstName\":\"小红\",\"lastName\":\"李\",\"age\":32},{\"firstName\":\"小刚\",\"lastName\":\"赵\",\"age\":24}]}"
#state actions {}
""",
        """v0_9.binding.04_relative_path.3_relative_in_function""" to """#0 createSurface surface="db-rp-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-rp-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-rp-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-rp-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "订单明细"
#4 createComponent surface="db-rp-3" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-rp-3"
  data:
    "lines" = [{"price": 49.9, "qty": 2, "sku": "SKU-001"}, {"price": 199, "qty": 1, "sku": "SKU-002"}, {"price": 12.5, "qty": 5, "sku": "SKU-003"}]
#6 createComponent surface="db-rp-3" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "SKU-001：2 件 × ¥49.90"
#7 createComponent surface="db-rp-3" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "SKU-002：1 件 × ¥199.00"
#8 createComponent surface="db-rp-3" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "SKU-003：5 件 × ¥12.50"
#state dataModel "{\"lines\":[{\"sku\":\"SKU-001\",\"qty\":2,\"price\":49.9},{\"sku\":\"SKU-002\",\"qty\":1,\"price\":199},{\"sku\":\"SKU-003\",\"qty\":5,\"price\":12.5}]}"
#state actions {}
""",
        """v0_9.binding.05_mixed_scope.1_absolute_and_relative_mixed""" to """#0 createSurface surface="db-ms-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ms-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ms-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ms-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="db-ms-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-ms-1"
  data:
    "header" = {"tag": "Core", "title": "团队成员"}
    "rows" = [{"joinedYear": 2019, "name": "Alice"}, {"joinedYear": 2021, "name": "Bob"}, {"joinedYear": 2024, "name": "Carol"}]
#6 createComponent surface="db-ms-1" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[Core] Alice 加入于 2019"
#7 createComponent surface="db-ms-1" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[Core] Bob 加入于 2021"
#8 createComponent surface="db-ms-1" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[Core] Carol 加入于 2024"
#9 updateComponent surface="db-ms-1" tag=3 view="KRRichTextView"
  props:
    "text" = "团队成员"
#state dataModel "{\"header\":{\"title\":\"团队成员\",\"tag\":\"Core\"},\"rows\":[{\"name\":\"Alice\",\"joinedYear\":2019},{\"name\":\"Bob\",\"joinedYear\":2021},{\"name\":\"Carol\",\"joinedYear\":2024}]}"
#state actions {}
""",
        """v0_9.binding.05_mixed_scope.2_nested_child_list""" to """#0 createSurface surface="db-ms-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ms-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ms-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 updateData surface="db-ms-2"
  data:
    "sections" = [{"items": [{"label": "苹果"}, {"label": "香蕉"}, {"label": "橙子"}], "title": "水果"}, {"items": [{"label": "西红柿"}, {"label": "黄瓜"}], "title": "蔬菜"}]
#4 createComponent surface="db-ms-2" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="db-ms-2" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "水果"
#6 createComponent surface="db-ms-2" tag=9 parent=7 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#7 createComponent surface="db-ms-2" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "  • 苹果"
#8 createComponent surface="db-ms-2" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "  • 香蕉"
#9 createComponent surface="db-ms-2" tag=12 parent=9 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "  • 橙子"
#10 createComponent surface="db-ms-2" tag=13 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#11 createComponent surface="db-ms-2" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "蔬菜"
#12 createComponent surface="db-ms-2" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#13 createComponent surface="db-ms-2" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "  • 西红柿"
#14 createComponent surface="db-ms-2" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "  • 黄瓜"
#state dataModel "{\"sections\":[{\"title\":\"水果\",\"items\":[{\"label\":\"苹果\"},{\"label\":\"香蕉\"},{\"label\":\"橙子\"}]},{\"title\":\"蔬菜\",\"items\":[{\"label\":\"西红柿\"},{\"label\":\"黄瓜\"}]}]}"
#state actions {}
""",
        """v0_9.binding.06_two_way_binding.1_textfield_two_way""" to """#0 createSurface surface="db-tw-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-tw-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-tw-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-tw-1" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="db-tw-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "用户名"
#5 createComponent surface="db-tw-1" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/username"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="db-tw-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Hello, !"
#7 updateData surface="db-tw-1"
  data:
    "form" = {"username": ""}
#8 updateComponent surface="db-tw-1" tag=4 view="KRRichTextView"
  props:
    "text" = "Hello, !"
#9 updateComponent surface="db-tw-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#10 updateComponent surface="db-tw-1" tag=6 view="KRRichTextView"
  props:
    "text" = "用户名"
#state dataModel "{\"form\":{\"username\":\"\"}}"
#state actions {}
""",
        """v0_9.binding.06_two_way_binding.2_slider_two_way""" to """#0 createSurface surface="db-tw-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-tw-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-tw-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-tw-2" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="db-tw-2" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "评分"
#5 createComponent surface="db-tw-2" tag=3 parent=5 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/rating"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 10
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#6 createComponent surface="db-tw-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前评分： / 10"
#7 updateData surface="db-tw-2"
  data:
    "form" = {"rating": 5}
#8 updateComponent surface="db-tw-2" tag=4 view="KRRichTextView"
  props:
    "text" = "当前评分：5.0 / 10"
#9 updateComponent surface="db-tw-2" tag=3 view="KRSliderView"
  props:
    "value" = 5
#10 updateComponent surface="db-tw-2" tag=6 view="KRRichTextView"
  props:
    "text" = "评分"
#state dataModel "{\"form\":{\"rating\":5}}"
#state actions {}
""",
        """v0_9.binding.06_two_way_binding.3_checkbox_two_way""" to """#0 createSurface surface="db-tw-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-tw-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-tw-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-tw-3" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="db-tw-3" tag=3 parent=7 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "prefs/showEmail"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="db-tw-3" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "显示我的邮箱"
#6 createComponent surface="db-tw-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "showEmail = "
#7 createComponent surface="db-tw-3" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="db-tw-3" tag=5 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#9 createComponent surface="db-tw-3" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "请先勾选\"显示我的邮箱\""
#10 createComponent surface="db-tw-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "确认"
#11 updateData surface="db-tw-3"
  data:
    "prefs" = {"showEmail": false}
#12 updateComponent surface="db-tw-3" tag=4 view="KRRichTextView"
  props:
    "text" = "showEmail = false"
#13 updateComponent surface="db-tw-3" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"prefs\":{\"showEmail\":false}}"
#state actions {"5": null}
""",
        """v0_9.binding.06_two_way_binding.4_multi_fields_reactive""" to """#0 createSurface surface="db-tw-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-tw-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-tw-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-tw-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "qty/unit 由客户端双向绑定，total 由服务端派生并下推"
#4 createComponent surface="db-tw-4" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="db-tw-4" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "数量"
#6 createComponent surface="db-tw-4" tag=4 parent=7 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "order/qty"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 20
    "min" = 1
    "progressColor" = "4278239231"
    "value" = ""
#7 createComponent surface="db-tw-4" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="db-tw-4" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "单价 (元)"
#9 createComponent surface="db-tw-4" tag=5 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "order/unit"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#10 createComponent surface="db-tw-4" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "小计：（数量  × 单价 ）"
#11 updateData surface="db-tw-4"
  data:
    "order" = {"qty": 3, "total": 76.5, "unit": 25.5}
#12 updateComponent surface="db-tw-4" tag=6 view="KRRichTextView"
  props:
    "text" = "小计：¥76.50（数量 3 × 单价 25.5）"
#13 updateComponent surface="db-tw-4" tag=4 view="KRSliderView"
  props:
    "value" = 3
#14 updateComponent surface="db-tw-4" tag=8 view="KRRichTextView"
  props:
    "text" = "数量"
#15 updateComponent surface="db-tw-4" tag=5 view="KRTextFieldView"
  props:
    "text" = 25.5
#16 updateComponent surface="db-tw-4" tag=10 view="KRRichTextView"
  props:
    "text" = "单价 (元)"
#state dataModel "{\"order\":{\"qty\":3,\"unit\":25.5,\"total\":76.5}}"
#state actions {}
""",
        """v0_9.binding.07_function_binding.1_function_as_dynamic_string""" to """#0 createSurface surface="db-fb-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-fb-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-fb-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-fb-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Text.text ← formatDate"
#4 createComponent surface="db-fb-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 updateData surface="db-fb-1"
  data:
    "timestamp" = "2026-01-15T08:30:00Z"
#6 updateComponent surface="db-fb-1" tag=4 view="KRRichTextView"
  props:
    "text" = "2026 年 1 月 15 日 Thursday"
#state dataModel "{\"timestamp\":\"2026-01-15T08:30:00Z\"}"
#state actions {}
""",
        """v0_9.binding.07_function_binding.2_function_nested""" to """#0 createSurface surface="db-fb-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-fb-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-fb-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-fb-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "嵌套 FunctionCall：pluralize + formatNumber"
#4 createComponent surface="db-fb-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 updateData surface="db-fb-2"
  data:
    "count" = 12345
#6 updateComponent surface="db-fb-2" tag=4 view="KRRichTextView"
  props:
    "text" = "共 12,345 条新消息"
#state dataModel "{\"count\":12345}"
#state actions {}
""",
        """v0_9.binding.07_function_binding.3_function_dynamic_args""" to """#0 createSurface surface="db-fb-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-fb-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-fb-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-fb-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "FunctionCall 参数本身也是 Dynamic*"
#4 createComponent surface="db-fb-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 createComponent surface="db-fb-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 updateData surface="db-fb-3"
  data:
    "price" = 88.88
    "region" = {"currency": "CNY"}
#7 updateComponent surface="db-fb-3" tag=4 view="KRRichTextView"
  props:
    "text" = "${'$'}88.88"
#8 updateComponent surface="db-fb-3" tag=5 view="KRRichTextView"
  props:
    "text" = "¥88.88"
#state dataModel "{\"price\":88.88,\"region\":{\"currency\":\"CNY\"}}"
#state actions {}
""",
        """v0_9.binding.08_string_interpolation.1_multi_paths_and_nested""" to """#0 createSurface surface="db-si-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-si-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-si-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-si-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "多路径 + 嵌套函数"
#4 createComponent surface="db-si-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "尊敬的 ，您的账户余额为 （截至 ）"
#5 updateData surface="db-si-1"
  data:
    "asOf" = "2026-07-08T00:00:00Z"
    "user" = {"balance": 12345.67, "name": "Alice"}
#6 updateComponent surface="db-si-1" tag=4 view="KRRichTextView"
  props:
    "text" = "尊敬的 Alice，您的账户余额为 ${'$'}12,345.67（截至 2026-07-08）"
#state dataModel "{\"user\":{\"name\":\"Alice\",\"balance\":12345.67},\"asOf\":\"2026-07-08T00:00:00Z\"}"
#state actions {}
""",
        """v0_9.binding.08_string_interpolation.2_type_coercion""" to """#0 createSurface surface="db-si-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-si-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-si-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-si-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "boolean / number / null 三种类型自动转字符串"
#4 createComponent surface="db-si-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "boolean → "
#5 createComponent surface="db-si-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "number → （不加格式化）"
#6 createComponent surface="db-si-2" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "missing path → 「」（应为空）"
#7 updateData surface="db-si-2"
  data:
    "flags" = {"enabled": true}
    "stats" = {"count": 42}
#8 updateComponent surface="db-si-2" tag=4 view="KRRichTextView"
  props:
    "text" = "boolean → true"
#9 updateComponent surface="db-si-2" tag=5 view="KRRichTextView"
  props:
    "text" = "number → 42（不加格式化）"
#state dataModel "{\"flags\":{\"enabled\":true},\"stats\":{\"count\":42}}"
#state actions {}
""",
        """v0_9.binding.08_string_interpolation.3_escape_and_adjacent""" to """#0 createSurface surface="db-si-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-si-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-si-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-si-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "${'$'}{var} 转义示例 · 当前用户："
#4 createComponent surface="db-si-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "文档中写法：${'$'}{username} → 实际值 "
#5 createComponent surface="db-si-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "（三段无空格拼接）"
#6 updateData surface="db-si-3"
  data:
    "left" = "AAA"
    "mid" = "BBB"
    "right" = "CCC"
    "user" = {"name": "Bob"}
#7 updateComponent surface="db-si-3" tag=3 view="KRRichTextView"
  props:
    "text" = "${'$'}{var} 转义示例 · 当前用户：Bob"
#8 updateComponent surface="db-si-3" tag=4 view="KRRichTextView"
  props:
    "text" = "文档中写法：${'$'}{username} → 实际值 Bob"
#9 updateComponent surface="db-si-3" tag=5 view="KRRichTextView"
  props:
    "text" = "AAABBBCCC（三段无空格拼接）"
#state dataModel "{\"user\":{\"name\":\"Bob\"},\"left\":\"AAA\",\"mid\":\"BBB\",\"right\":\"CCC\"}"
#state actions {}
""",
        """v0_9.binding.09_check_binding.1_single_check""" to """#0 createSurface surface="db-cb-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cb-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cb-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cb-1" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="db-cb-1" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="db-cb-1" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="db-cb-1" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="db-cb-1" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="db-cb-1" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#9 createComponent surface="db-cb-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#10 updateData surface="db-cb-1"
  data:
    "form" = {"email": ""}
#11 updateComponent surface="db-cb-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="db-cb-1" tag=7 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"form\":{\"email\":\"\"}}"
#state actions {"4": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.binding.09_check_binding.2_and_combined""" to """#0 createSurface surface="db-cb-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cb-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cb-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cb-2" tag=8 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="db-cb-2" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "姓名"
#5 createComponent surface="db-cb-2" tag=3 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="db-cb-2" tag=10 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="db-cb-2" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#8 createComponent surface="db-cb-2" tag=4 parent=10 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="db-cb-2" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="db-cb-2" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "密码"
#11 createComponent surface="db-cb-2" tag=5 parent=12 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/password"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#12 createComponent surface="db-cb-2" tag=14 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="db-cb-2" tag=6 parent=14 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#14 createComponent surface="db-cb-2" tag=15 parent=14 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "请填写姓名、有效邮箱，密码至少 8 位"
#15 createComponent surface="db-cb-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "注册"
#16 updateData surface="db-cb-2"
  data:
    "form" = {"email": "", "name": "", "password": ""}
#17 updateComponent surface="db-cb-2" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#18 updateComponent surface="db-cb-2" tag=9 view="KRRichTextView"
  props:
    "text" = "姓名"
#19 updateComponent surface="db-cb-2" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#20 updateComponent surface="db-cb-2" tag=11 view="KRRichTextView"
  props:
    "text" = "邮箱"
#21 updateComponent surface="db-cb-2" tag=5 view="KRTextFieldView"
  props:
    "text" = ""
#22 updateComponent surface="db-cb-2" tag=13 view="KRRichTextView"
  props:
    "text" = "密码"
#state dataModel "{\"form\":{\"name\":\"\",\"email\":\"\",\"password\":\"\"}}"
#state actions {"6": null}
""",
        """v0_9.binding.09_check_binding.3_not_and_exclusive""" to """#0 createSurface surface="db-cb-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-cb-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cb-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cb-3" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="db-cb-3" tag=3 parent=7 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "f/a"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="db-cb-3" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意 A 协议"
#6 createComponent surface="db-cb-3" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#7 createComponent surface="db-cb-3" tag=4 parent=9 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "f/b"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#8 createComponent surface="db-cb-3" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意 B 协议（与 A 互斥）"
#9 createComponent surface="db-cb-3" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="db-cb-3" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="db-cb-3" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#12 createComponent surface="db-cb-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "继续"
#13 updateData surface="db-cb-3"
  data:
    "f" = {"a": false, "b": false}
#14 updateComponent surface="db-cb-3" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#15 updateComponent surface="db-cb-3" tag=4 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"f\":{\"a\":false,\"b\":false}}"
#state actions {"5": "{\"event\":{\"name\":\"continue\"}}"}
""",
        """v0_9.binding.10_action_context.1_event_context_form_data""" to """#0 createSurface surface="db-ac-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ac-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ac-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ac-1" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="db-ac-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "备注"
#5 createComponent surface="db-ac-1" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/note"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="db-ac-1" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="db-ac-1" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "满意度"
#8 createComponent surface="db-ac-1" tag=4 parent=9 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/score"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 5
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#9 createComponent surface="db-ac-1" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#10 createComponent surface="db-ac-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交反馈"
#11 updateData surface="db-ac-1"
  data:
    "form" = {"note": "", "score": 3}
#12 updateComponent surface="db-ac-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#13 updateComponent surface="db-ac-1" tag=8 view="KRRichTextView"
  props:
    "text" = "备注"
#14 updateComponent surface="db-ac-1" tag=4 view="KRSliderView"
  props:
    "value" = 3
#15 updateComponent surface="db-ac-1" tag=10 view="KRRichTextView"
  props:
    "text" = "满意度"
#state dataModel "{\"form\":{\"note\":\"\",\"score\":3}}"
#state actions {"5": "{\"event\":{\"name\":\"submit_feedback\",\"context\":{\"note\":\"\",\"score\":3}}}"}
""",
        """v0_9.binding.10_action_context.2_event_context_template_row""" to """#0 createSurface surface="db-ac-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ac-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ac-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ac-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "点击任意行 → context 中携带该行 id/label"
#4 createComponent surface="db-ac-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-ac-2"
  data:
    "rows" = [{"id": "r-1", "label": "选项一"}, {"id": "r-2", "label": "选项二"}, {"id": "r-3", "label": "选项三"}]
#6 createComponent surface="db-ac-2" tag=9 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="db-ac-2" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "选项一"
#8 createComponent surface="db-ac-2" tag=11 parent=9 index=1 view="KRView"
  props:
    "touchEnable" = 1
#9 createComponent surface="db-ac-2" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#10 createComponent surface="db-ac-2" tag=13 parent=9 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#11 createComponent surface="db-ac-2" tag=14 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#12 createComponent surface="db-ac-2" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "选项二"
#13 createComponent surface="db-ac-2" tag=16 parent=14 index=1 view="KRView"
  props:
    "touchEnable" = 1
#14 createComponent surface="db-ac-2" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#15 createComponent surface="db-ac-2" tag=18 parent=14 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#16 createComponent surface="db-ac-2" tag=19 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#17 createComponent surface="db-ac-2" tag=20 parent=19 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "选项三"
#18 createComponent surface="db-ac-2" tag=21 parent=19 index=1 view="KRView"
  props:
    "touchEnable" = 1
#19 createComponent surface="db-ac-2" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#20 createComponent surface="db-ac-2" tag=23 parent=19 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "选择"
#state dataModel "{\"rows\":[{\"id\":\"r-1\",\"label\":\"选项一\"},{\"id\":\"r-2\",\"label\":\"选项二\"},{\"id\":\"r-3\",\"label\":\"选项三\"}]}"
#state actions {"11": "{\"event\":{\"name\":\"row_selected\",\"context\":{\"id\":null,\"label\":null}}}", "16": "{\"event\":{\"name\":\"row_selected\",\"context\":{\"id\":null,\"label\":null}}}", "21": "{\"event\":{\"name\":\"row_selected\",\"context\":{\"id\":null,\"label\":null}}}"}
""",
        """v0_9.binding.10_action_context.3_context_mixed_dynamic_value""" to """#0 createSurface surface="db-ac-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-ac-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-ac-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-ac-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "点击「提交」后，onAction 会展示混合类型的 context 值"
#4 createComponent surface="db-ac-3" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="db-ac-3" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "姓名"
#6 createComponent surface="db-ac-3" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="db-ac-3" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="db-ac-3" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "金额"
#9 createComponent surface="db-ac-3" tag=5 parent=11 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/amount"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 1000
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#10 createComponent surface="db-ac-3" tag=13 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#11 createComponent surface="db-ac-3" tag=6 parent=13 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "form/agree"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#12 createComponent surface="db-ac-3" tag=14 parent=13 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意条款"
#13 createComponent surface="db-ac-3" tag=7 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#14 createComponent surface="db-ac-3" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#15 updateData surface="db-ac-3"
  data:
    "form" = {"agree": true, "amount": 199.5, "name": "Alice"}
#16 updateComponent surface="db-ac-3" tag=4 view="KRTextFieldView"
  props:
    "text" = "Alice"
#17 updateComponent surface="db-ac-3" tag=10 view="KRRichTextView"
  props:
    "text" = "姓名"
#18 updateComponent surface="db-ac-3" tag=5 view="KRSliderView"
  props:
    "value" = 199.5
#19 updateComponent surface="db-ac-3" tag=12 view="KRRichTextView"
  props:
    "text" = "金额"
#20 updateComponent surface="db-ac-3" tag=6 view="KRCheckBoxView"
  props:
    "checked" = true
#state dataModel "{\"form\":{\"name\":\"Alice\",\"amount\":199.5,\"agree\":true}}"
#state actions {"7": "{\"event\":{\"name\":\"submit_mixed_context\",\"context\":{\"literalStr\":\"hello\",\"literalNum\":42,\"literalBool\":true,\"literalArr\":[\"a\",\"b\",\"c\"],\"boundName\":\"Alice\",\"formattedAmount\":\"${'$'}199.50\"}}}"}
""",
        """v0_9.binding.11_accessibility.1_accessibility_label_dynamic""" to """#0 createSurface surface="db-a11y-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-a11y-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-a11y-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-a11y-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "按钮的 accessibility.label 会随 /user/name 动态更新"
#4 createComponent surface="db-a11y-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "__accessibility" = "点两下以关注该用户"
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="db-a11y-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "关注 "
#6 updateData surface="db-a11y-1"
  data:
    "user" = {"name": "Alice"}
#7 updateComponent surface="db-a11y-1" tag=5 view="KRRichTextView"
  props:
    "text" = "关注 Alice"
#state dataModel "{\"user\":{\"name\":\"Alice\"}}"
#state actions {"4": "{\"event\":{\"name\":\"follow\"}}"}
""",
        """v0_9.binding.11_accessibility.2_accessibility_in_template""" to """#0 createSurface surface="db-a11y-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-a11y-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-a11y-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-a11y-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "每行按钮的 accessibility.label 独立绑定 name"
#4 createComponent surface="db-a11y-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-a11y-2"
  data:
    "friends" = [{"name": "Alice"}, {"name": "Bob"}, {"name": "Carol"}]
#6 createComponent surface="db-a11y-2" tag=9 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="db-a11y-2" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Alice"
#8 createComponent surface="db-a11y-2" tag=11 parent=9 index=1 view="KRView"
  props:
    "touchEnable" = 1
#9 createComponent surface="db-a11y-2" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#10 createComponent surface="db-a11y-2" tag=13 parent=9 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#11 createComponent surface="db-a11y-2" tag=14 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#12 createComponent surface="db-a11y-2" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Bob"
#13 createComponent surface="db-a11y-2" tag=16 parent=14 index=1 view="KRView"
  props:
    "touchEnable" = 1
#14 createComponent surface="db-a11y-2" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#15 createComponent surface="db-a11y-2" tag=18 parent=14 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#16 createComponent surface="db-a11y-2" tag=19 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#17 createComponent surface="db-a11y-2" tag=20 parent=19 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Carol"
#18 createComponent surface="db-a11y-2" tag=21 parent=19 index=1 view="KRView"
  props:
    "touchEnable" = 1
#19 createComponent surface="db-a11y-2" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#20 createComponent surface="db-a11y-2" tag=23 parent=19 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发消息"
#state dataModel "{\"friends\":[{\"name\":\"Alice\"},{\"name\":\"Bob\"},{\"name\":\"Carol\"}]}"
#state actions {"11": "{\"event\":{\"name\":\"message\",\"context\":{\"to\":null}}}", "16": "{\"event\":{\"name\":\"message\",\"context\":{\"to\":null}}}", "21": "{\"event\":{\"name\":\"message\",\"context\":{\"to\":null}}}"}
""",
        """v0_9.binding.12_progressive_rendering.1_no_data_model""" to """#0 createSurface surface="db-pr-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-pr-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-pr-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-pr-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "渐进渲染：未提供 dataModel"
#4 createComponent surface="db-pr-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "以下所有绑定路径都指向未定义的数据；渲染层应优雅降级，不显示 'undefined' 字面量、不 crash。"
#5 createComponent surface="db-pr-1" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="db-pr-1" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Hello , score="
#7 createComponent surface="db-pr-1" tag=10 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="db-pr-1" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "备注（path 未定义）"
#9 createComponent surface="db-pr-1" tag=7 parent=10 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/note"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#10 createComponent surface="db-pr-1" tag=12 parent=2 index=5 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#11 createComponent surface="db-pr-1" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "分值（path 未定义）"
#12 createComponent surface="db-pr-1" tag=8 parent=12 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/score"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 10
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#13 createComponent surface="db-pr-1" tag=14 parent=2 index=6 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#14 createComponent surface="db-pr-1" tag=9 parent=14 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "form/agree"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#15 createComponent surface="db-pr-1" tag=15 parent=14 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "开关（path 未定义）"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.binding.12_progressive_rendering.2_data_arrives_later""" to """#0 createSurface surface="db-pr-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-pr-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-pr-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-pr-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "渐进渲染：dataModel 后到达"
#4 createComponent surface="db-pr-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 createComponent surface="db-pr-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Hello , score="
#6 createComponent surface="db-pr-2" tag=11 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="db-pr-2" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "备注"
#8 createComponent surface="db-pr-2" tag=6 parent=11 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/note"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="db-pr-2" tag=13 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="db-pr-2" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "分值"
#11 createComponent surface="db-pr-2" tag=7 parent=13 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/score"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#12 createComponent surface="db-pr-2" tag=15 parent=2 index=5 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#13 createComponent surface="db-pr-2" tag=8 parent=15 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "form/agree"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#14 createComponent surface="db-pr-2" tag=16 parent=15 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意条款"
#15 createComponent surface="db-pr-2" tag=9 parent=2 index=6 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#16 updateData surface="db-pr-2"
  data:
    "form" = {"agree": true, "note": "已备注", "score": 42}
    "items" = [{"index": 1, "name": "First"}, {"index": 2, "name": "Second"}, {"index": 3, "name": "Third"}]
    "user" = {"name": "Alice", "score": 88}
#17 createComponent surface="db-pr-2" tag=17 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1. First"
#18 createComponent surface="db-pr-2" tag=18 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2. Second"
#19 createComponent surface="db-pr-2" tag=19 parent=9 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3. Third"
#20 updateComponent surface="db-pr-2" tag=5 view="KRRichTextView"
  props:
    "text" = "Hello Alice, score=88"
#21 updateComponent surface="db-pr-2" tag=4 view="KRRichTextView"
  props:
    "text" = "Alice"
#22 updateComponent surface="db-pr-2" tag=6 view="KRTextFieldView"
  props:
    "text" = "已备注"
#23 updateComponent surface="db-pr-2" tag=12 view="KRRichTextView"
  props:
    "text" = "备注"
#24 updateComponent surface="db-pr-2" tag=7 view="KRSliderView"
  props:
    "value" = 42
#25 updateComponent surface="db-pr-2" tag=14 view="KRRichTextView"
  props:
    "text" = "分值"
#26 updateComponent surface="db-pr-2" tag=8 view="KRCheckBoxView"
  props:
    "checked" = true
#state dataModel "{\"user\":{\"name\":\"Alice\",\"score\":88},\"form\":{\"note\":\"已备注\",\"score\":42,\"agree\":true},\"items\":[{\"index\":1,\"name\":\"First\"},{\"index\":2,\"name\":\"Second\"},{\"index\":3,\"name\":\"Third\"}]}"
#state actions {}
""",
        """v0_9.binding.12_progressive_rendering.3_root_scope_relative_path_fallback""" to """#0 createSurface surface="db-pr-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-pr-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-pr-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-pr-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Root Scope 相对路径：规范未定义行为，容错基线"
#4 createComponent surface="db-pr-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "规范只在 ChildList 模板 Child Scope 里定义相对路径。Root Scope 下使用相对路径属未定义行为，实现可选择：(a) fallback 到根同名 key；(b) 返回空串。两者皆合规，但绝不能 crash。"
#5 createComponent surface="db-pr-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "① 直接 PathBinding {path:'name'}："
#6 createComponent surface="db-pr-3" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="db-pr-3" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "② formatString 插值 ${'$'}{name}："
#8 createComponent surface="db-pr-3" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "value=[]"
#9 createComponent surface="db-pr-3" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "③ FunctionCall 参数携带相对 PathBinding："
#10 createComponent surface="db-pr-3" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="db-pr-3" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "本引擎期望三行都显示 'Root-Scope Fallback OK'（fallback 到 dataModel 顶层 name）；严格实现下应显示空串或 [] 占位。"
#12 updateData surface="db-pr-3"
  data:
    "name" = "Root-Scope Fallback OK"
#13 updateComponent surface="db-pr-3" tag=8 view="KRRichTextView"
  props:
    "text" = "value=[Root-Scope Fallback OK]"
#14 updateComponent surface="db-pr-3" tag=10 view="KRRichTextView"
  props:
    "text" = "Root-Scope Fallback OK"
#15 updateComponent surface="db-pr-3" tag=6 view="KRRichTextView"
  props:
    "text" = "Root-Scope Fallback OK"
#state dataModel "{\"name\":\"Root-Scope Fallback OK\"}"
#state actions {}
""",
        """v0_9.binding.12_progressive_rendering.4_undefined_path_placeholder""" to """#0 createSurface surface="db-pr-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-pr-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-pr-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-pr-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "绝对路径指向不存在数据：规范要求 placeholder 降级"
#4 createComponent surface="db-pr-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "① 存在路径 /present："
#5 createComponent surface="db-pr-4" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="db-pr-4" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "② 顶层不存在 /nowhere："
#7 createComponent surface="db-pr-4" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="db-pr-4" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "③ 深层不存在 /missing/user/name："
#9 createComponent surface="db-pr-4" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#10 createComponent surface="db-pr-4" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "④ formatString 混合已存在与不存在："
#11 createComponent surface="db-pr-4" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "present=[] missing=[] deep=[]"
#12 createComponent surface="db-pr-4" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "期望：① 显示 HELLO；②③ 空占位；④ 显示 present=[HELLO] missing=[] deep=[]。绝不能出现 'undefined'/'null' 字面量。"
#13 updateData surface="db-pr-4"
  data:
    "present" = "HELLO"
#14 updateComponent surface="db-pr-4" tag=11 view="KRRichTextView"
  props:
    "text" = "present=[HELLO] missing=[] deep=[]"
#15 updateComponent surface="db-pr-4" tag=5 view="KRRichTextView"
  props:
    "text" = "HELLO"
#state dataModel "{\"present\":\"HELLO\"}"
#state actions {}
""",
        """v0_9.binding.12_progressive_rendering.5_undefined_treated_as_empty""" to """#0 createSurface surface="db-pr-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="db-pr-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-pr-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-pr-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "formatString: undefined 段应替换为空串"
#4 createComponent surface="db-pr-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "① 缺失夹在中间：'[${'$'}{/a}][${'$'}{/gap}][${'$'}{/b}]'"
#5 createComponent surface="db-pr-5" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[][][]"
#6 createComponent surface="db-pr-5" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "② 首尾都缺失：'[${'$'}{/lead}]-body-[${'$'}{/trail}]'"
#7 createComponent surface="db-pr-5" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "[]-body-[]"
#8 createComponent surface="db-pr-5" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "③ 相邻缺失：'x${'$'}{/m1}${'$'}{/m2}y'"
#9 createComponent surface="db-pr-5" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "xy"
#10 createComponent surface="db-pr-5" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "④ 函数参数里的缺失：'up=${'$'}{formatString(value:\"pre-${'$'}{/none}-post\")}'"
#11 createComponent surface="db-pr-5" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "up=pre--post"
#12 createComponent surface="db-pr-5" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "期望：① '[foo][][bar]'；② '[]-body-[]'；③ 'xy'；④ 'up=pre--post'。任何 undefined 字面量出现即失败。"
#13 updateData surface="db-pr-5"
  data:
    "a" = "foo"
    "b" = "bar"
#14 updateComponent surface="db-pr-5" tag=5 view="KRRichTextView"
  props:
    "text" = "[foo][][bar]"
#state dataModel "{\"a\":\"foo\",\"b\":\"bar\"}"
#state actions {}
""",
        """v0_9.component.audioplayer.1_audioplayer_literal_url""" to """#0 createSurface surface="test-audio-literal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-audio-literal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-literal" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-audio-literal" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Sample audio track"
#4 createComponent surface="test-audio-literal" tag=2 parent=3 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.audioplayer.2_audioplayer_url_binding""" to """#0 createSurface surface="test-audio-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-audio-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-binding" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-audio-binding" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = ""
#4 createComponent surface="test-audio-binding" tag=2 parent=3 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = ""
#5 updateData surface="test-audio-binding"
  data:
    "audio" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    "title" = "Bound Track"
#6 updateComponent surface="test-audio-binding" tag=2 view="KRAudioView"
  props:
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#7 updateComponent surface="test-audio-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Bound Track"
#state dataModel "{\"audio\":\"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3\",\"title\":\"Bound Track\"}"
#state actions {}
""",
        """v0_9.component.audioplayer.3_audioplayer_in_column""" to """#0 createSurface surface="test-audio-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-audio-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-audio-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Now Playing"
#4 createComponent surface="test-audio-column" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "SoundHelix Song 1"
#5 createComponent surface="test-audio-column" tag=6 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="test-audio-column" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "SoundHelix Song 1"
#7 createComponent surface="test-audio-column" tag=5 parent=6 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.button.1_button_variant_default""" to """#0 createSurface surface="test-btn-default"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-btn-default" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-default" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-default" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Default Button"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"defaultClicked\"}}"}
""",
        """v0_9.component.button.2_button_variant_primary""" to """#0 createSurface surface="test-btn-primary"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-btn-primary" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-primary" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-primary" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Action"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"primaryClicked\"}}"}
""",
        """v0_9.component.button.3_button_variant_borderless""" to """#0 createSurface surface="test-btn-borderless"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-btn-borderless" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-borderless" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-borderless" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Link Style"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"linkClicked\"}}"}
""",
        """v0_9.component.button.4_button_icon_only""" to """#0 createSurface surface="test-btn-icon"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-btn-icon" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-icon" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-btn-icon" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#4 createComponent surface="test-btn-icon" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔗"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="test-btn-icon" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="test-btn-icon" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "❤"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-btn-icon" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="test-btn-icon" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🗑"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {"3": "{\"event\":{\"name\":\"shareClicked\"}}", "5": "{\"event\":{\"name\":\"favClicked\"}}", "7": "{\"event\":{\"name\":\"deleteClicked\"}}"}
""",
        """v0_9.component.button.5_button_action_with_context""" to """#0 createSurface surface="test-btn-context"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-btn-context" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-context" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-context" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Buy Now"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"buyItem\",\"context\":{\"itemId\":\"SKU-001\",\"quantity\":2}}}"}
""",
        """v0_9.component.card.1_card_single_text_child""" to """#0 createSurface surface="test-card-single-text"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-card-single-text" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-single-text" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Card with a single Text child"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.card.2_card_with_column""" to """#0 createSurface surface="test-card-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-card-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-card-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Card title"
#4 createComponent surface="test-card-column" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Card body. Use Column to host multiple children."
#5 createComponent surface="test-card-column" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "footer"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.card.3_card_with_row""" to """#0 createSurface surface="test-card-row"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-card-row" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-row" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="test-card-row" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#4 createComponent surface="test-card-row" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#5 createComponent surface="test-card-row" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "John Doe"
#6 createComponent surface="test-card-row" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Software Engineer"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.card.4_card_nested_cards""" to """#0 createSurface surface="test-card-nested"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-card-nested" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-nested" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-card-nested" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Outer Card"
#4 createComponent surface="test-card-nested" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#5 createComponent surface="test-card-nested" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Inner Card A"
#6 createComponent surface="test-card-nested" tag=6 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#7 createComponent surface="test-card-nested" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Inner Card B"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.checkbox.1_checkbox_value_true""" to """#0 createSurface surface="test-cb-true"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cb-true" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-true" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#3 createComponent surface="test-cb-true" tag=2 parent=3 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#4 createComponent surface="test-cb-true" tag=4 parent=3 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "I agree (initially checked)"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.checkbox.2_checkbox_value_false""" to """#0 createSurface surface="test-cb-false"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cb-false" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-false" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#3 createComponent surface="test-cb-false" tag=2 parent=3 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "false"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#4 createComponent surface="test-cb-false" tag=4 parent=3 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Subscribe to newsletter"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.checkbox.3_checkbox_value_binding""" to """#0 createSurface surface="test-cb-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cb-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cb-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="test-cb-binding" tag=3 parent=5 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "dark"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="test-cb-binding" tag=6 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Enable dark mode"
#6 createComponent surface="test-cb-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Dark mode is: "
#7 updateData surface="test-cb-binding"
  data:
    "dark" = false
#8 updateComponent surface="test-cb-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Dark mode is: false"
#9 updateComponent surface="test-cb-binding" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"dark\":false}"
#state actions {}
""",
        """v0_9.component.checkbox.4_checkbox_multiple""" to """#0 createSurface surface="test-cb-multi"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cb-multi" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-multi" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cb-multi" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Preferences"
#4 createComponent surface="test-cb-multi" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#5 createComponent surface="test-cb-multi" tag=4 parent=7 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#6 createComponent surface="test-cb-multi" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive email"
#7 createComponent surface="test-cb-multi" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#8 createComponent surface="test-cb-multi" tag=5 parent=9 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "false"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#9 createComponent surface="test-cb-multi" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive SMS"
#10 createComponent surface="test-cb-multi" tag=11 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#11 createComponent surface="test-cb-multi" tag=6 parent=11 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#12 createComponent surface="test-cb-multi" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive push"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.1_choicepicker_mutually_exclusive_checkbox""" to """#0 createSurface surface="test-cp-mut-cb"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-mut-cb" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-mut-cb" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-mut-cb" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Size"
#4 createComponent surface="test-cp-mut-cb" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "flexDirection" = "column"
    "label" = "Size"
    "options" = [{"label": "Small", "value": "S"}, {"label": "Medium", "value": "M"}, {"label": "Large", "value": "L"}]
    "value" = ["M"]
    "variant" = "mutuallyExclusive"
#5 createComponent surface="test-cp-mut-cb" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "S"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-mut-cb" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#7 createComponent surface="test-cp-mut-cb" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Small"
#8 createComponent surface="test-cp-mut-cb" tag=8 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "M"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-mut-cb" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "●"
#10 createComponent surface="test-cp-mut-cb" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Medium"
#11 createComponent surface="test-cp-mut-cb" tag=11 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "L"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-mut-cb" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#13 createComponent surface="test-cp-mut-cb" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Large"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.2_choicepicker_multiple_selection_checkbox""" to """#0 createSurface surface="test-cp-multi-cb"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-multi-cb" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-multi-cb" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-multi-cb" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Languages I speak"
#4 createComponent surface="test-cp-multi-cb" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "flexDirection" = "column"
    "label" = "Languages I speak"
    "options" = [{"label": "English", "value": "en"}, {"label": "中文", "value": "zh"}, {"label": "Français", "value": "fr"}, {"label": "Deutsch", "value": "de"}, {"label": "Español", "value": "es"}]
    "value" = ["en", "zh"]
    "variant" = "multipleSelection"
#5 createComponent surface="test-cp-multi-cb" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "en"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-multi-cb" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "☑"
#7 createComponent surface="test-cp-multi-cb" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "English"
#8 createComponent surface="test-cp-multi-cb" tag=8 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "zh"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-multi-cb" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "☑"
#10 createComponent surface="test-cp-multi-cb" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "中文"
#11 createComponent surface="test-cp-multi-cb" tag=11 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "fr"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-multi-cb" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#13 createComponent surface="test-cp-multi-cb" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Français"
#14 createComponent surface="test-cp-multi-cb" tag=14 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "de"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#15 createComponent surface="test-cp-multi-cb" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#16 createComponent surface="test-cp-multi-cb" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Deutsch"
#17 createComponent surface="test-cp-multi-cb" tag=17 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "es"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#18 createComponent surface="test-cp-multi-cb" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#19 createComponent surface="test-cp-multi-cb" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Español"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.3_choicepicker_mutually_exclusive_chips""" to """#0 createSurface surface="test-cp-mut-chips"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-mut-chips" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-mut-chips" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-mut-chips" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Plan"
#4 createComponent surface="test-cp-mut-chips" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Plan"
    "options" = [{"label": "Free", "value": "free"}, {"label": "Pro", "value": "pro"}, {"label": "Team", "value": "team"}]
    "value" = ["pro"]
    "variant" = "mutuallyExclusive"
#5 createComponent surface="test-cp-mut-chips" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "free"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-mut-chips" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Free"
#7 createComponent surface="test-cp-mut-chips" tag=7 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "pro"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#8 createComponent surface="test-cp-mut-chips" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Pro"
#9 createComponent surface="test-cp-mut-chips" tag=9 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "team"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-mut-chips" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Team"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.4_choicepicker_multiple_selection_chips""" to """#0 createSurface surface="test-cp-multi-chips"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-multi-chips" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-multi-chips" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-multi-chips" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Tags"
#4 createComponent surface="test-cp-multi-chips" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Tags"
    "options" = [{"label": "Kotlin", "value": "kotlin"}, {"label": "Swift", "value": "swift"}, {"label": "ArkTS", "value": "arkts"}, {"label": "JS", "value": "js"}, {"label": "Compose", "value": "compose"}]
    "value" = ["kotlin", "compose"]
    "variant" = "multipleSelection"
#5 createComponent surface="test-cp-multi-chips" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "kotlin"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-multi-chips" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Kotlin"
#7 createComponent surface="test-cp-multi-chips" tag=7 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "swift"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#8 createComponent surface="test-cp-multi-chips" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Swift"
#9 createComponent surface="test-cp-multi-chips" tag=9 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "arkts"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-multi-chips" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "ArkTS"
#11 createComponent surface="test-cp-multi-chips" tag=11 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "js"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-multi-chips" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "JS"
#13 createComponent surface="test-cp-multi-chips" tag=13 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "compose"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#14 createComponent surface="test-cp-multi-chips" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Compose"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.5_choicepicker_filterable""" to """#0 createSurface surface="test-cp-filterable"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-filterable" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-filterable" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-filterable" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Country"
#4 createComponent surface="test-cp-filterable" tag=5 parent=3 index=1 view="KRTextFieldView"
  props:
    "__choicePickerFilterId" = "cp"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = 8.0
    "fontSize" = 14.0
    "height" = 36.0
    "marginBottom" = 8.0
    "paddingLeft" = 10.0
    "paddingRight" = 10.0
    "placeholder" = ""
#5 createComponent surface="test-cp-filterable" tag=2 parent=3 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "filterable" = true
    "flexDirection" = "column"
    "label" = "Country"
    "options" = [{"label": "China", "value": "china"}, {"label": "United States", "value": "united states"}, {"label": "Japan", "value": "japan"}, {"label": "France", "value": "france"}, {"label": "Germany", "value": "germany"}, {"label": "India", "value": "india"}, {"label": "Brazil", "value": "brazil"}, {"label": "Australia", "value": "australia"}]
    "value" = ["china"]
    "variant" = "mutuallyExclusive"
#6 createComponent surface="test-cp-filterable" tag=6 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "china"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#7 createComponent surface="test-cp-filterable" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "●"
#8 createComponent surface="test-cp-filterable" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "China"
#9 createComponent surface="test-cp-filterable" tag=9 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "united states"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-filterable" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#11 createComponent surface="test-cp-filterable" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "United States"
#12 createComponent surface="test-cp-filterable" tag=12 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "japan"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#13 createComponent surface="test-cp-filterable" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#14 createComponent surface="test-cp-filterable" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Japan"
#15 createComponent surface="test-cp-filterable" tag=15 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "france"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#16 createComponent surface="test-cp-filterable" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#17 createComponent surface="test-cp-filterable" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "France"
#18 createComponent surface="test-cp-filterable" tag=18 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "germany"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#19 createComponent surface="test-cp-filterable" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#20 createComponent surface="test-cp-filterable" tag=20 parent=18 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Germany"
#21 createComponent surface="test-cp-filterable" tag=21 parent=2 index=5 view="KRView"
  props:
    "__choiceOptionValue" = "india"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#22 createComponent surface="test-cp-filterable" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#23 createComponent surface="test-cp-filterable" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "India"
#24 createComponent surface="test-cp-filterable" tag=24 parent=2 index=6 view="KRView"
  props:
    "__choiceOptionValue" = "brazil"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#25 createComponent surface="test-cp-filterable" tag=25 parent=24 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#26 createComponent surface="test-cp-filterable" tag=26 parent=24 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Brazil"
#27 createComponent surface="test-cp-filterable" tag=27 parent=2 index=7 view="KRView"
  props:
    "__choiceOptionValue" = "australia"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#28 createComponent surface="test-cp-filterable" tag=28 parent=27 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#29 createComponent surface="test-cp-filterable" tag=29 parent=27 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Australia"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.choicepicker.6_choicepicker_value_binding""" to """#0 createSurface surface="test-cp-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-cp-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cp-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-cp-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Topics"
#5 createComponent surface="test-cp-binding" tag=3 parent=5 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Topics"
    "options" = [{"label": "Sports", "value": "sports"}, {"label": "News", "value": "news"}, {"label": "Tech", "value": "tech"}, {"label": "Movies", "value": "movies"}]
    "value" = ""
    "variant" = "multipleSelection"
#6 createComponent surface="test-cp-binding" tag=7 parent=3 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "sports"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#7 createComponent surface="test-cp-binding" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Sports"
#8 createComponent surface="test-cp-binding" tag=9 parent=3 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "news"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-binding" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "News"
#10 createComponent surface="test-cp-binding" tag=11 parent=3 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "tech"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#11 createComponent surface="test-cp-binding" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Tech"
#12 createComponent surface="test-cp-binding" tag=13 parent=3 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "movies"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#13 createComponent surface="test-cp-binding" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Movies"
#14 createComponent surface="test-cp-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Selected: "
#15 updateData surface="test-cp-binding"
  data:
    "picked" = ["tech", "movies"]
#16 updateComponent surface="test-cp-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Selected: [\"tech\",\"movies\"]"
#17 updateComponent surface="test-cp-binding" tag=7 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#18 updateComponent surface="test-cp-binding" tag=8 view="KRRichTextView"
  props:
    "color" = "#333333"
#19 updateComponent surface="test-cp-binding" tag=9 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#20 updateComponent surface="test-cp-binding" tag=10 view="KRRichTextView"
  props:
    "color" = "#333333"
#21 updateComponent surface="test-cp-binding" tag=11 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#22 updateComponent surface="test-cp-binding" tag=12 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#23 updateComponent surface="test-cp-binding" tag=13 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#24 updateComponent surface="test-cp-binding" tag=14 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#state dataModel "{\"picked\":[\"tech\",\"movies\"]}"
#state actions {}
""",
        """v0_9.component.column.1_column_justify_start""" to """#0 createSurface surface="test-col-just-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-just-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "flex-start"
#3 createComponent surface="test-col-just-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.2_column_justify_center""" to """#0 createSurface surface="test-col-just-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-just-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "center"
#3 createComponent surface="test-col-just-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.3_column_justify_end""" to """#0 createSurface surface="test-col-just-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-just-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "flex-end"
#3 createComponent surface="test-col-just-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.4_column_justify_spaceBetween""" to """#0 createSurface surface="test-col-just-between"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-just-between" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-between" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "space-between"
#3 createComponent surface="test-col-just-between" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-between" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-between" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.5_column_align_start""" to """#0 createSurface surface="test-col-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.6_column_align_center""" to """#0 createSurface surface="test-col-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.7_column_align_end""" to """#0 createSurface surface="test-col-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.column.8_column_template_children""" to """#0 createSurface surface="test-col-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-col-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-template" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 updateData surface="test-col-template"
  data:
    "items" = [{"text": "1. First item from /items"}, {"text": "2. Second item from /items"}, {"text": "3. Third item from /items"}, {"text": "4. Fourth item from /items"}]
#4 createComponent surface="test-col-template" tag=4 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1. First item from /items"
#5 createComponent surface="test-col-template" tag=5 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2. Second item from /items"
#6 createComponent surface="test-col-template" tag=6 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3. Third item from /items"
#7 createComponent surface="test-col-template" tag=7 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "4. Fourth item from /items"
#state dataModel "{\"items\":[{\"text\":\"1. First item from /items\"},{\"text\":\"2. Second item from /items\"},{\"text\":\"3. Third item from /items\"},{\"text\":\"4. Fourth item from /items\"}]}"
#state actions {}
""",
        """v0_9.component.datetimeinput.1_datetimeinput_date_only""" to """#0 createSurface surface="test-dt-date"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-date" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-date" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-date" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Birthday"
#4 createComponent surface="test-dt-date" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Birthday"
    "value" = "1990-01-15"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.datetimeinput.2_datetimeinput_time_only""" to """#0 createSurface surface="test-dt-time"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-time" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-time" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-time" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Reminder time"
#4 createComponent surface="test-dt-time" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = false
    "enableTime" = true
    "label" = "Reminder time"
    "value" = "09:00:00"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.datetimeinput.3_datetimeinput_date_time""" to """#0 createSurface surface="test-dt-datetime"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-datetime" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-datetime" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-datetime" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Meeting"
#4 createComponent surface="test-dt-datetime" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Meeting"
    "value" = "2026-01-15T14:30:00"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.datetimeinput.4_datetimeinput_min_max_constraints""" to """#0 createSurface surface="test-dt-range"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-range" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-range" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-range" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Booking date"
#4 createComponent surface="test-dt-range" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Booking date"
    "max" = "2026-12-31"
    "min" = "2026-06-01"
    "value" = "2026-06-20"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.datetimeinput.5_datetimeinput_empty_value""" to """#0 createSurface surface="test-dt-empty"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-empty" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-empty" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-empty" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Pick a date"
#4 createComponent surface="test-dt-empty" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Pick a date"
    "value" = ""
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.datetimeinput.6_datetimeinput_value_binding""" to """#0 createSurface surface="test-dt-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-dt-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-dt-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-dt-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Departure"
#5 createComponent surface="test-dt-binding" tag=3 parent=5 index=1 view="KRDateTimeInputView"
  props:
    "__dateTimeBindingPath" = "departure"
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Departure"
    "value" = ""
#6 createComponent surface="test-dt-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "ISO: "
#7 updateData surface="test-dt-binding"
  data:
    "departure" = "2026-12-24T08:00:00"
#8 updateComponent surface="test-dt-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "ISO: 2026-12-24T08:00:00"
#9 updateComponent surface="test-dt-binding" tag=3 view="KRDateTimeInputView"
  props:
    "value" = "2026-12-24T08:00:00"
#10 updateComponent surface="test-dt-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Departure"
#state dataModel "{\"departure\":\"2026-12-24T08:00:00\"}"
#state actions {}
""",
        """v0_9.component.divider.1_divider_horizontal_default""" to """#0 createSurface surface="test-divider-h"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-divider-h" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-h" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-divider-h" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Above the line"
#4 createComponent surface="test-divider-h" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#5 createComponent surface="test-divider-h" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Below the line"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.divider.2_divider_vertical_in_row""" to """#0 createSurface surface="test-divider-v"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-divider-v" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-v" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="test-divider-v" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Left"
#4 createComponent surface="test-divider-v" tag=4 parent=2 index=1 view="KRView"
  props:
    "__verticalDividerFollowParent" = true
    "backgroundColor" = "4292927712"
    "height" = 40
    "marginLeft" = 8
    "marginRight" = 8
    "width" = 1
#5 createComponent surface="test-divider-v" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Right"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.divider.3_divider_multiple_in_column""" to """#0 createSurface surface="test-divider-multi"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-divider-multi" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-multi" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-divider-multi" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Section 1"
#4 createComponent surface="test-divider-multi" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#5 createComponent surface="test-divider-multi" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Section 2"
#6 createComponent surface="test-divider-multi" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="test-divider-multi" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Section 3"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.1_icon_common_set_1""" to """#0 createSurface surface="test-icon-set1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-set1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-set1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-set1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: home, search, settings, favorite, star"
#4 createComponent surface="test-icon-set1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-set1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🏠"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-set1" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔍"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-set1" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⚙"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-set1" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "❤"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-set1" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "★"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.2_icon_common_set_2""" to """#0 createSurface surface="test-icon-set2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-set2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-set2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-set2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: add, close, check, menu, share"
#4 createComponent surface="test-icon-set2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-set2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "+"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-set2" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✕"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-set2" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-set2" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "☰"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-set2" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔗"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.3_icon_media_icons""" to """#0 createSurface surface="test-icon-media"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-media" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-media" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-media" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: play, pause, stop, skipNext, skipPrevious, volumeUp, volumeMute"
#4 createComponent surface="test-icon-media" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-media" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "▶"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-media" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏸"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-media" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏹"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-media" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏭"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-media" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏮"
    "textAlign" = "center"
    "width" = 24
#10 createComponent surface="test-icon-media" tag=10 parent=4 index=5 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔊"
    "textAlign" = "center"
    "width" = 24
#11 createComponent surface="test-icon-media" tag=11 parent=4 index=6 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔇"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.4_icon_communication_icons""" to """#0 createSurface surface="test-icon-comm"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-comm" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-comm" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-comm" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: mail, phone, call, send, notifications"
#4 createComponent surface="test-icon-comm" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-comm" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✉"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-comm" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📱"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-comm" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📞"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-comm" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✈"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-comm" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔔"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.5_icon_svg_path""" to """#0 createSurface surface="test-icon-svg"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-svg" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-svg" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-svg" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Custom SVG path icons (heart, lightning, smile)"
#4 createComponent surface="test-icon-svg" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#5 createComponent surface="test-icon-svg" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-svg" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-svg" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.icon.6_icon_name_binding""" to """#0 createSurface surface="test-icon-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-icon-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-icon-binding" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#4 createComponent surface="test-icon-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="test-icon-binding" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#6 updateData surface="test-icon-binding"
  data:
    "icon1" = "lockOpen"
    "icon2" = "visibility"
    "icon3" = "favorite"
#7 updateComponent surface="test-icon-binding" tag=3 view="KRRichTextView"
  props:
    "text" = "🔓"
#8 updateComponent surface="test-icon-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "👁"
#9 updateComponent surface="test-icon-binding" tag=5 view="KRRichTextView"
  props:
    "text" = "❤"
#state dataModel "{\"icon1\":\"lockOpen\",\"icon2\":\"visibility\",\"icon3\":\"favorite\"}"
#state actions {}
""",
        """v0_9.component.image.1_image_variant_icon""" to """#0 createSurface surface="test-image-icon"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-icon" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-icon" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "height" = 24
    "margin" = 0
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.2_image_variant_avatar""" to """#0 createSurface surface="test-image-avatar"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-avatar" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-avatar" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.3_image_variant_smallFeature""" to """#0 createSurface surface="test-image-small"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-small" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-small" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.4_image_variant_mediumFeature""" to """#0 createSurface surface="test-image-medium"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-medium" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-medium" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.5_image_variant_largeFeature""" to """#0 createSurface surface="test-image-large"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-large" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-large" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 400
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.6_image_variant_header""" to """#0 createSurface surface="test-image-header"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-header" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-header" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeHeader" = true
    "__featureAutoSizeMaxHeightDp" = 200
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.7_image_fit_modes""" to """#0 createSurface surface="test-image-fit"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-fit" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-fit" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-image-fit" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=contain"
#4 createComponent surface="test-image-fit" tag=4 parent=2 index=1 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#5 createComponent surface="test-image-fit" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=cover"
#6 createComponent surface="test-image-fit" tag=6 parent=2 index=3 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#7 createComponent surface="test-image-fit" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=fill (default)"
#8 createComponent surface="test-image-fit" tag=8 parent=2 index=5 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "stretch"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#9 createComponent surface="test-image-fit" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=none"
#10 createComponent surface="test-image-fit" tag=10 parent=2 index=7 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#11 createComponent surface="test-image-fit" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=scaleDown"
#12 createComponent surface="test-image-fit" tag=12 parent=2 index=9 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.image.8_image_url_path_binding""" to """#0 createSurface surface="test-image-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-image-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-binding" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__accessibility" = ""
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#3 updateData surface="test-image-binding"
  data:
    "alt" = "Bound image alt"
    "photoUrl" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#4 updateComponent surface="test-image-binding" tag=2 view="KRImageView"
  props:
    "__accessibility" = "Bound image alt"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{\"photoUrl\":\"https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop\",\"alt\":\"Bound image alt\"}"
#state actions {}
""",
        """v0_9.component.list.1_list_direction_vertical""" to """#0 createSurface surface="test-list-vertical"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-vertical" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-vertical" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-vertical" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 1"
#4 createComponent surface="test-list-vertical" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 2"
#5 createComponent surface="test-list-vertical" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 3"
#6 createComponent surface="test-list-vertical" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 4"
#7 createComponent surface="test-list-vertical" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 5"
#8 createComponent surface="test-list-vertical" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 6"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.2_list_direction_horizontal""" to """#0 createSurface surface="test-list-horizontal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-horizontal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-horizontal" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "row"
#3 createComponent surface="test-list-horizontal" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#1"
#4 createComponent surface="test-list-horizontal" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#2"
#5 createComponent surface="test-list-horizontal" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#3"
#6 createComponent surface="test-list-horizontal" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#4"
#7 createComponent surface="test-list-horizontal" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#5"
#8 createComponent surface="test-list-horizontal" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#6"
#9 createComponent surface="test-list-horizontal" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#7"
#10 createComponent surface="test-list-horizontal" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#8"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.3_list_direction_vertical_long""" to """#0 createSurface surface="test-list-vertical"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-vertical" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-vertical" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-vertical" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 1"
#4 createComponent surface="test-list-vertical" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 2"
#5 createComponent surface="test-list-vertical" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 3"
#6 createComponent surface="test-list-vertical" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 4"
#7 createComponent surface="test-list-vertical" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 5"
#8 createComponent surface="test-list-vertical" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 6"
#9 createComponent surface="test-list-vertical" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 7"
#10 createComponent surface="test-list-vertical" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 8"
#11 createComponent surface="test-list-vertical" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 9"
#12 createComponent surface="test-list-vertical" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 10"
#13 createComponent surface="test-list-vertical" tag=13 parent=2 index=10 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 11"
#14 createComponent surface="test-list-vertical" tag=14 parent=2 index=11 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 12"
#15 createComponent surface="test-list-vertical" tag=15 parent=2 index=12 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 13"
#16 createComponent surface="test-list-vertical" tag=16 parent=2 index=13 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 14"
#17 createComponent surface="test-list-vertical" tag=17 parent=2 index=14 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 15"
#18 createComponent surface="test-list-vertical" tag=18 parent=2 index=15 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 16"
#19 createComponent surface="test-list-vertical" tag=19 parent=2 index=16 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 17"
#20 createComponent surface="test-list-vertical" tag=20 parent=2 index=17 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 18"
#21 createComponent surface="test-list-vertical" tag=21 parent=2 index=18 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 19"
#22 createComponent surface="test-list-vertical" tag=22 parent=2 index=19 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 20"
#23 createComponent surface="test-list-vertical" tag=23 parent=2 index=20 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 21"
#24 createComponent surface="test-list-vertical" tag=24 parent=2 index=21 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 22"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.4_list_direction_horizontal_long""" to """#0 createSurface surface="test-list-horizontal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-horizontal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-horizontal" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "row"
#3 createComponent surface="test-list-horizontal" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#1"
#4 createComponent surface="test-list-horizontal" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#2"
#5 createComponent surface="test-list-horizontal" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#3"
#6 createComponent surface="test-list-horizontal" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#4"
#7 createComponent surface="test-list-horizontal" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#5"
#8 createComponent surface="test-list-horizontal" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#6"
#9 createComponent surface="test-list-horizontal" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#7"
#10 createComponent surface="test-list-horizontal" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#8"
#11 createComponent surface="test-list-horizontal" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#9"
#12 createComponent surface="test-list-horizontal" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#10"
#13 createComponent surface="test-list-horizontal" tag=13 parent=2 index=10 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#11"
#14 createComponent surface="test-list-horizontal" tag=14 parent=2 index=11 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#12"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.5_list_align_start""" to """#0 createSurface surface="test-list-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-start" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.6_list_align_center""" to """#0 createSurface surface="test-list-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-center" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.7_list_align_end""" to """#0 createSurface surface="test-list-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-end" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.8_list_align_stretch""" to """#0 createSurface surface="test-list-align-stretch"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-align-stretch" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-stretch" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-stretch" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-stretch" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-stretch" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.list.9_list_template_children""" to """#0 createSurface surface="test-list-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-list-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-template" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 updateData surface="test-list-template"
  data:
    "people" = [{"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Alice", "role": "Engineer"}, {"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Bob", "role": "Designer"}, {"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Carol", "role": "Manager"}]
#4 createComponent surface="test-list-template" tag=8 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="test-list-template" tag=9 parent=8 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#6 createComponent surface="test-list-template" tag=10 parent=8 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#7 createComponent surface="test-list-template" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Alice"
#8 createComponent surface="test-list-template" tag=12 parent=10 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Engineer"
#9 createComponent surface="test-list-template" tag=13 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="test-list-template" tag=14 parent=13 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#11 createComponent surface="test-list-template" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="test-list-template" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Bob"
#13 createComponent surface="test-list-template" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Designer"
#14 createComponent surface="test-list-template" tag=18 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#15 createComponent surface="test-list-template" tag=19 parent=18 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#16 createComponent surface="test-list-template" tag=20 parent=18 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#17 createComponent surface="test-list-template" tag=21 parent=20 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Carol"
#18 createComponent surface="test-list-template" tag=22 parent=20 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Manager"
#state dataModel "{\"people\":[{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Alice\",\"role\":\"Engineer\"},{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Bob\",\"role\":\"Designer\"},{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Carol\",\"role\":\"Manager\"}]}"
#state actions {}
""",
        """v0_9.component.modal.1_modal_basic""" to """#0 createSurface surface="test-modal-basic"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-modal-basic" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-modal-basic" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-modal-basic" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Click 'Open' to show the modal"
#4 createComponent surface="test-modal-basic" tag=5 parent=2 index=1 view="KRView"
  props:
    "__modalTriggerId" = "modal"
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="test-modal-basic" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Open"
#state dataModel "{}"
#state actions {"5": "{\"event\":{\"name\":\"openModal\"}}"}
""",
        """v0_9.component.modal.2_modal_with_form""" to """#0 createSurface surface="test-modal-form"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-modal-form" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-modal-form" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-modal-form" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Open settings modal"
#4 createComponent surface="test-modal-form" tag=5 parent=2 index=1 view="KRView"
  props:
    "__modalTriggerId" = "modal"
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="test-modal-form" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Settings"
#state dataModel "{}"
#state actions {"5": "{\"event\":{\"name\":\"openSettings\"}}"}
""",
        """v0_9.component.row.1_row_justify_start""" to """#0 createSurface surface="test-row-justify-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-start"
#3 createComponent surface="test-row-justify-start" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-start" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-start" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-start" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-start" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-start" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.10_row_template_children""" to """#0 createSurface surface="test-row-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-template" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-start"
#3 updateData surface="test-row-template"
  data:
    "tags" = [{"label": "Kotlin"}, {"label": "Compose"}, {"label": "iOS"}, {"label": "A2UI"}]
#4 createComponent surface="test-row-template" tag=4 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Kotlin"
#5 createComponent surface="test-row-template" tag=5 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Compose"
#6 createComponent surface="test-row-template" tag=6 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "iOS"
#7 createComponent surface="test-row-template" tag=7 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A2UI"
#state dataModel "{\"tags\":[{\"label\":\"Kotlin\"},{\"label\":\"Compose\"},{\"label\":\"iOS\"},{\"label\":\"A2UI\"}]}"
#state actions {}
""",
        """v0_9.component.row.2_row_justify_center""" to """#0 createSurface surface="test-row-justify-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "center"
#3 createComponent surface="test-row-justify-center" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-center" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-center" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-center" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-center" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-center" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.3_row_justify_end""" to """#0 createSurface surface="test-row-justify-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-end"
#3 createComponent surface="test-row-justify-end" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-end" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-end" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-end" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-end" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-end" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.4_row_justify_spaceBetween""" to """#0 createSurface surface="test-row-justify-between"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-between" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-between" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#3 createComponent surface="test-row-justify-between" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-between" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-between" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-between" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-between" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-between" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.5_row_justify_spaceAround""" to """#0 createSurface surface="test-row-justify-around"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-around" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-around" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#3 createComponent surface="test-row-justify-around" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-around" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-around" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-around" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-around" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-around" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.6_row_justify_spaceEvenly""" to """#0 createSurface surface="test-row-justify-evenly"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-justify-evenly" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-evenly" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-justify-evenly" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-evenly" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-evenly" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-evenly" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-evenly" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-evenly" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.7_row_align_start""" to """#0 createSurface surface="test-row-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Tall\nText\nA"
#4 createComponent surface="test-row-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.8_row_align_center""" to """#0 createSurface surface="test-row-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Tall\nText\nA"
#4 createComponent surface="test-row-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.row.9_row_align_end""" to """#0 createSurface surface="test-row-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-row-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Tall\nText\nA"
#4 createComponent surface="test-row-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.slider.1_slider_default_min_max""" to """#0 createSurface surface="test-sl-default"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-sl-default" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-default" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-default" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Volume"
#4 createComponent surface="test-sl-default" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = 30
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.slider.2_slider_min_omitted""" to """#0 createSurface surface="test-sl-omit-min"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-sl-omit-min" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-omit-min" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-omit-min" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Brightness"
#4 createComponent surface="test-sl-omit-min" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 10
    "progressColor" = "4278239231"
    "value" = 7
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.slider.3_slider_negative_range""" to """#0 createSurface surface="test-sl-neg"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-sl-neg" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-neg" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-neg" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Temperature (°C)"
#4 createComponent surface="test-sl-neg" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 40
    "min" = -20
    "progressColor" = "4278239231"
    "value" = 22
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.slider.4_slider_value_binding""" to """#0 createSurface surface="test-sl-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-sl-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-sl-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-sl-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Progress"
#5 createComponent surface="test-sl-binding" tag=3 parent=5 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "progress"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#6 createComponent surface="test-sl-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Progress: %"
#7 updateData surface="test-sl-binding"
  data:
    "progress" = 45
#8 updateComponent surface="test-sl-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Progress: 45%"
#9 updateComponent surface="test-sl-binding" tag=3 view="KRSliderView"
  props:
    "value" = 45
#10 updateComponent surface="test-sl-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Progress"
#state dataModel "{\"progress\":45}"
#state actions {}
""",
        """v0_9.component.slider.5_slider_no_label""" to """#0 createSurface surface="test-sl-nolabel"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-sl-nolabel" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-nolabel" tag=2 parent=1 index=0 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 1
    "min" = 0
    "progressColor" = "4278239231"
    "value" = 0.5
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.tabs.1_tabs_two_tabs""" to """#0 createSurface surface="test-tabs-two"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tabs-two" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-two" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-two" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-two" tag=6 parent=5 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-two" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Overview"
#6 createComponent surface="test-tabs-two" tag=8 parent=6 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-two" tag=9 parent=5 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-two" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Details"
#9 createComponent surface="test-tabs-two" tag=11 parent=9 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-two" tag=12 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-two" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-two" tag=3 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Tab 1: Overview content. Lorem ipsum..."
#13 createComponent surface="test-tabs-two" tag=4 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Tab 2: Details content. Different from tab 1."
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.tabs.2_tabs_three_tabs""" to """#0 createSurface surface="test-tabs-three"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tabs-three" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-three" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-three" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-three" tag=7 parent=6 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-three" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Photos"
#6 createComponent surface="test-tabs-three" tag=9 parent=7 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-three" tag=10 parent=6 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-three" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Videos"
#9 createComponent surface="test-tabs-three" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-three" tag=13 parent=6 index=2 view="KRView"
  props:
    "__tabIndex" = 2
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#11 createComponent surface="test-tabs-three" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Audio"
#12 createComponent surface="test-tabs-three" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#13 createComponent surface="test-tabs-three" tag=16 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#14 createComponent surface="test-tabs-three" tag=17 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#15 createComponent surface="test-tabs-three" tag=18 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#16 createComponent surface="test-tabs-three" tag=3 parent=16 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#17 createComponent surface="test-tabs-three" tag=4 parent=17 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#18 createComponent surface="test-tabs-three" tag=19 parent=18 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#19 createComponent surface="test-tabs-three" tag=20 parent=19 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Audio tab"
#20 createComponent surface="test-tabs-three" tag=5 parent=19 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.tabs.3_tabs_complex_children""" to """#0 createSurface surface="test-tabs-complex"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tabs-complex" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-complex" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-complex" tag=9 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-complex" tag=10 parent=9 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-complex" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Profile"
#6 createComponent surface="test-tabs-complex" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-complex" tag=13 parent=9 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-complex" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Settings"
#9 createComponent surface="test-tabs-complex" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-complex" tag=16 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-complex" tag=17 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-complex" tag=3 parent=16 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#13 createComponent surface="test-tabs-complex" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Name: Alex Chen"
#14 createComponent surface="test-tabs-complex" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Role: Senior Engineer"
#15 createComponent surface="test-tabs-complex" tag=6 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#16 createComponent surface="test-tabs-complex" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Theme: Dark"
#17 createComponent surface="test-tabs-complex" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Language: English"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.tabs.4_tabs_title_binding""" to """#0 createSurface surface="test-tabs-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tabs-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-binding" tag=6 parent=5 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-binding" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Tab 1"
#6 createComponent surface="test-tabs-binding" tag=8 parent=6 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-binding" tag=9 parent=5 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-binding" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Tab 2"
#9 createComponent surface="test-tabs-binding" tag=11 parent=9 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-binding" tag=12 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-binding" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-binding" tag=3 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Content for tab 1"
#13 createComponent surface="test-tabs-binding" tag=4 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Content for tab 2"
#14 updateData surface="test-tabs-binding"
  data:
    "tab1" = "Bound A"
    "tab2" = "Bound B"
#15 updateComponent surface="test-tabs-binding" tag=7 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Bound A"
#16 updateComponent surface="test-tabs-binding" tag=10 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Bound B"
#state dataModel "{\"tab1\":\"Bound A\",\"tab2\":\"Bound B\"}"
#state actions {}
""",
        """v0_9.component.text.1_text_h1""" to """#0 createSurface surface="test-text-h1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-h1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h1" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Heading 1 - Largest Title"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.text.2_text_h2""" to """#0 createSurface surface="test-text-h2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-h2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h2" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Heading 2 - Section Title"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.text.3_text_h3_h4_h5""" to """#0 createSurface surface="test-text-h345"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-h345" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h345" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-h345" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Heading 3 (28dp)"
#4 createComponent surface="test-text-h345" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Heading 4 (24dp)"
#5 createComponent surface="test-text-h345" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Heading 5 (20dp)"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.text.4_text_body""" to """#0 createSurface surface="test-text-body"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-body" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-body" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-body" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "This is body text with variant=\"body\" (16dp)."
#4 createComponent surface="test-text-body" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "This is text WITHOUT variant set (should default to body)."
#5 createComponent surface="test-text-body" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Long body text to verify wrapping. The quick brown fox jumps over the lazy dog. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.text.5_text_caption""" to """#0 createSurface surface="test-text-caption"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-caption" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-caption" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-caption" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "POSTED"
#4 createComponent surface="test-text-caption" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Article Title"
#5 createComponent surface="test-text-caption" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "by John Doe • 5 min read"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.text.6_text_path_binding""" to """#0 createSurface surface="test-text-path"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-path" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-path" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-path" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="test-text-path" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#5 updateData surface="test-text-path"
  data:
    "subtitle" = "This caption text is also bound via /subtitle"
    "title" = "Bound Title from Data Model"
#6 updateComponent surface="test-text-path" tag=3 view="KRRichTextView"
  props:
    "text" = "Bound Title from Data Model"
#7 updateComponent surface="test-text-path" tag=4 view="KRRichTextView"
  props:
    "text" = "This caption text is also bound via /subtitle"
#state dataModel "{\"title\":\"Bound Title from Data Model\",\"subtitle\":\"This caption text is also bound via /subtitle\"}"
#state actions {}
""",
        """v0_9.component.text.7_text_function_formatString""" to """#0 createSurface surface="test-text-format-string"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-format-string" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-format-string" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-format-string" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Hello,  !"
#4 createComponent surface="test-text-format-string" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "You have  unread messages and  new notifications."
#5 updateData surface="test-text-format-string"
  data:
    "firstName" = "Alex"
    "lastName" = "Chen"
    "notifications" = 12
    "unread" = 5
#6 updateComponent surface="test-text-format-string" tag=3 view="KRRichTextView"
  props:
    "text" = "Hello, Alex Chen!"
#7 updateComponent surface="test-text-format-string" tag=4 view="KRRichTextView"
  props:
    "text" = "You have 5 unread messages and 12 new notifications."
#state dataModel "{\"firstName\":\"Alex\",\"lastName\":\"Chen\",\"unread\":5,\"notifications\":12}"
#state actions {}
""",
        """v0_9.component.text.8_text_function_formatDate""" to """#0 createSurface surface="test-text-format-date"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-text-format-date" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-format-date" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-format-date" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "PUBLISHED"
#4 createComponent surface="test-text-format-date" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="test-text-format-date" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 updateData surface="test-text-format-date"
  data:
    "publishedAt" = "2025-12-15T17:30:00Z"
#7 updateComponent surface="test-text-format-date" tag=4 view="KRRichTextView"
  props:
    "text" = "Dec 15, 2025"
#8 updateComponent surface="test-text-format-date" tag=5 view="KRRichTextView"
  props:
    "text" = "2025/12/15"
#state dataModel "{\"publishedAt\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.component.textfield.1_textfield_variant_shortText""" to """#0 createSurface surface="test-tf-short"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-short" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-short" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-short" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Username"
#4 createComponent surface="test-tf-short" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.textfield.2_textfield_variant_longText""" to """#0 createSurface surface="test-tf-long"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-long" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-long" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-long" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Bio"
#4 createComponent surface="test-tf-long" tag=2 parent=3 index=1 view="KRTextAreaView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "minHeight" = 88
    "padding" = 8
    "text" = ""
    "variant" = "longText"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.textfield.3_textfield_variant_number""" to """#0 createSurface surface="test-tf-number"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-number" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-number" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-number" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Age"
#4 createComponent surface="test-tf-number" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "number"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.textfield.4_textfield_variant_obscured""" to """#0 createSurface surface="test-tf-obscured"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-obscured" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-obscured" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-obscured" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Password"
#4 createComponent surface="test-tf-obscured" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "obscured"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.textfield.5_textfield_value_binding""" to """#0 createSurface surface="test-tf-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tf-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-tf-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Your name"
#5 createComponent surface="test-tf-binding" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="test-tf-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Hello, !"
#7 updateData surface="test-tf-binding"
  data:
    "name" = "World"
#8 updateComponent surface="test-tf-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Hello, World!"
#9 updateComponent surface="test-tf-binding" tag=3 view="KRTextFieldView"
  props:
    "text" = "World"
#10 updateComponent surface="test-tf-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Your name"
#state dataModel "{\"name\":\"World\"}"
#state actions {}
""",
        """v0_9.component.textfield.6_textfield_email_checks_vs_legacy_regexp""" to """#0 createSurface surface="test-tf-regexp"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-tf-regexp" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-regexp" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tf-regexp" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-tf-regexp" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (checks - preferred)"
#5 createComponent surface="test-tf-regexp" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#6 createComponent surface="test-tf-regexp" tag=7 parent=5 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#7 createComponent surface="test-tf-regexp" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="test-tf-regexp" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (validationRegexp - legacy)"
#9 createComponent surface="test-tf-regexp" tag=4 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "__vr_tf"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#10 createComponent surface="test-tf-regexp" tag=10 parent=8 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#11 updateData surface="test-tf-regexp"
  data:
    "email" = ""
#12 updateComponent surface="test-tf-regexp" tag=3 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#13 updateComponent surface="test-tf-regexp" tag=6 view="KRRichTextView"
  props:
    "text" = "Email (checks - preferred)"
#14 updateComponent surface="test-tf-regexp" tag=7 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#state dataModel "{\"email\":\"\"}"
#state actions {}
""",
        """v0_9.component.video.1_video_literal_url""" to """#0 createSurface surface="test-video-literal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-video-literal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-literal" tag=2 parent=1 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.component.video.2_video_url_binding""" to """#0 createSurface surface="test-video-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-video-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-binding" tag=2 parent=1 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = ""
#3 updateData surface="test-video-binding"
  data:
    "videoUrl" = "https://www.w3schools.com/html/mov_bbb.mp4"
#4 updateComponent surface="test-video-binding" tag=2 view="KRVideoView"
  props:
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#state dataModel "{\"videoUrl\":\"https://www.w3schools.com/html/mov_bbb.mp4\"}"
#state actions {}
""",
        """v0_9.component.video.3_video_in_column""" to """#0 createSurface surface="test-video-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="test-video-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-video-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Sample Video"
#4 createComponent surface="test-video-column" tag=4 parent=2 index=1 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#5 createComponent surface="test-video-column" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Big Buck Bunny - test clip"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.examples.01_flight-status""" to """#0 createSurface surface="gallery-flight-status"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-flight-status" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-flight-status" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-flight-status" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#4 createComponent surface="gallery-flight-status" tag=4 parent=3 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="gallery-flight-status" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✈"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="gallery-flight-status" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-flight-status" tag=7 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-flight-status" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#9 createComponent surface="gallery-flight-status" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-flight-status" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "→"
#11 createComponent surface="gallery-flight-status" tag=11 parent=8 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-flight-status" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#13 createComponent surface="gallery-flight-status" tag=13 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#14 createComponent surface="gallery-flight-status" tag=14 parent=13 index=0 view="KRView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#15 createComponent surface="gallery-flight-status" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Departs"
#16 createComponent surface="gallery-flight-status" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#17 createComponent surface="gallery-flight-status" tag=17 parent=13 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#18 createComponent surface="gallery-flight-status" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Status"
#19 createComponent surface="gallery-flight-status" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#20 createComponent surface="gallery-flight-status" tag=20 parent=13 index=2 view="KRView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#21 createComponent surface="gallery-flight-status" tag=21 parent=20 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Arrives"
#22 createComponent surface="gallery-flight-status" tag=22 parent=20 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#23 updateData surface="gallery-flight-status"
  data:
    "arrivalTime" = "2025-12-15T14:30:00Z"
    "date" = "2025-12-15"
    "departureTime" = "2025-12-15T10:15:00Z"
    "destination" = "New York"
    "flightNumber" = "OS 87"
    "origin" = "Vienna"
    "status" = "On Time"
#24 updateComponent surface="gallery-flight-status" tag=6 view="KRRichTextView"
  props:
    "text" = "OS 87"
#25 updateComponent surface="gallery-flight-status" tag=7 view="KRRichTextView"
  props:
    "text" = "Mon, Dec 15"
#26 updateComponent surface="gallery-flight-status" tag=9 view="KRRichTextView"
  props:
    "text" = "Vienna"
#27 updateComponent surface="gallery-flight-status" tag=11 view="KRRichTextView"
  props:
    "text" = "New York"
#28 updateComponent surface="gallery-flight-status" tag=16 view="KRRichTextView"
  props:
    "text" = "10:15 AM"
#29 updateComponent surface="gallery-flight-status" tag=19 view="KRRichTextView"
  props:
    "text" = "On Time"
#30 updateComponent surface="gallery-flight-status" tag=22 view="KRRichTextView"
  props:
    "text" = "2:30 PM"
#state dataModel "{\"flightNumber\":\"OS 87\",\"date\":\"2025-12-15\",\"origin\":\"Vienna\",\"destination\":\"New York\",\"departureTime\":\"2025-12-15T10:15:00Z\",\"status\":\"On Time\",\"arrivalTime\":\"2025-12-15T14:30:00Z\"}"
#state actions {}
""",
        """v0_9.examples.02_email-compose""" to """#0 createSurface surface="gallery-email-compose"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-email-compose" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-email-compose" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-email-compose" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-email-compose" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "FROM"
#5 createComponent surface="gallery-email-compose" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-email-compose" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="gallery-email-compose" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "TO"
#8 createComponent surface="gallery-email-compose" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-email-compose" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-email-compose" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "SUBJECT"
#11 createComponent surface="gallery-email-compose" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-email-compose" tag=12 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#13 createComponent surface="gallery-email-compose" tag=13 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#14 createComponent surface="gallery-email-compose" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-email-compose" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#16 createComponent surface="gallery-email-compose" tag=16 parent=13 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#17 createComponent surface="gallery-email-compose" tag=17 parent=13 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#18 createComponent surface="gallery-email-compose" tag=18 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#19 createComponent surface="gallery-email-compose" tag=20 parent=18 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#20 createComponent surface="gallery-email-compose" tag=19 parent=20 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Send email"
#21 createComponent surface="gallery-email-compose" tag=22 parent=18 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#22 createComponent surface="gallery-email-compose" tag=21 parent=22 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Discard"
#23 updateData surface="gallery-email-compose"
  data:
    "body" = "Following up on our call. Please review the attached Q4 forecast and let me know if you have questions before the board meeting."
    "closing" = "Best,"
    "from" = "alex@acme.com"
    "greeting" = "Hi Jordan,"
    "signature" = "Alex"
    "subject" = "Q4 Revenue Forecast"
    "to" = "jordan@acme.com"
#24 updateComponent surface="gallery-email-compose" tag=5 view="KRRichTextView"
  props:
    "text" = "alex@acme.com"
#25 updateComponent surface="gallery-email-compose" tag=8 view="KRRichTextView"
  props:
    "text" = "jordan@acme.com"
#26 updateComponent surface="gallery-email-compose" tag=11 view="KRRichTextView"
  props:
    "text" = "Q4 Revenue Forecast"
#27 updateComponent surface="gallery-email-compose" tag=14 view="KRRichTextView"
  props:
    "text" = "Hi Jordan,"
#28 updateComponent surface="gallery-email-compose" tag=15 view="KRRichTextView"
  props:
    "text" = "Following up on our call. Please review the attached Q4 forecast and let me know if you have questions before the board meeting."
#29 updateComponent surface="gallery-email-compose" tag=16 view="KRRichTextView"
  props:
    "text" = "Best,"
#30 updateComponent surface="gallery-email-compose" tag=17 view="KRRichTextView"
  props:
    "text" = "Alex"
#state dataModel "{\"from\":\"alex@acme.com\",\"to\":\"jordan@acme.com\",\"subject\":\"Q4 Revenue Forecast\",\"greeting\":\"Hi Jordan,\",\"body\":\"Following up on our call. Please review the attached Q4 forecast and let me know if you have questions before the board meeting.\",\"closing\":\"Best,\",\"signature\":\"Alex\"}"
#state actions {"20": "{\"event\":{\"name\":\"send\",\"context\":{}}}", "22": "{\"event\":{\"name\":\"discard\",\"context\":{}}}"}
""",
        """v0_9.examples.03_calendar-day""" to """#0 createSurface surface="gallery-calendar-day"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-calendar-day" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-calendar-day" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-calendar-day" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-calendar-day" tag=4 parent=3 index=0 view="KRView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#5 createComponent surface="gallery-calendar-day" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-calendar-day" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-calendar-day" tag=7 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#8 createComponent surface="gallery-calendar-day" tag=11 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#9 createComponent surface="gallery-calendar-day" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-calendar-day" tag=14 parent=12 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="gallery-calendar-day" tag=13 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Add to calendar"
#12 createComponent surface="gallery-calendar-day" tag=16 parent=12 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#13 createComponent surface="gallery-calendar-day" tag=15 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Discard"
#14 updateData surface="gallery-calendar-day"
  data:
    "date" = "2025-12-28"
    "events" = [{"time": "12:00 - 12:45 PM", "title": "Lunch"}, {"time": "1:00 - 2:00 PM", "title": "Q1 roadmap review"}, {"time": "3:30 - 4:00 PM", "title": "Team standup"}]
#15 createComponent surface="gallery-calendar-day" tag=17 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#16 createComponent surface="gallery-calendar-day" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Lunch"
#17 createComponent surface="gallery-calendar-day" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "12:00 - 12:45 PM"
#18 createComponent surface="gallery-calendar-day" tag=20 parent=7 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#19 createComponent surface="gallery-calendar-day" tag=21 parent=20 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Q1 roadmap review"
#20 createComponent surface="gallery-calendar-day" tag=22 parent=20 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "1:00 - 2:00 PM"
#21 createComponent surface="gallery-calendar-day" tag=23 parent=7 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#22 createComponent surface="gallery-calendar-day" tag=24 parent=23 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Team standup"
#23 createComponent surface="gallery-calendar-day" tag=25 parent=23 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "3:30 - 4:00 PM"
#24 updateComponent surface="gallery-calendar-day" tag=5 view="KRRichTextView"
  props:
    "text" = "Sunday"
#25 updateComponent surface="gallery-calendar-day" tag=6 view="KRRichTextView"
  props:
    "text" = "28"
#state dataModel "{\"date\":\"2025-12-28\",\"events\":[{\"title\":\"Lunch\",\"time\":\"12:00 - 12:45 PM\"},{\"title\":\"Q1 roadmap review\",\"time\":\"1:00 - 2:00 PM\"},{\"title\":\"Team standup\",\"time\":\"3:30 - 4:00 PM\"}]}"
#state actions {"14": "{\"event\":{\"name\":\"add\",\"context\":{}}}", "16": "{\"event\":{\"name\":\"discard\",\"context\":{}}}"}
""",
        """v0_9.examples.04_weather-current""" to """#0 createSurface surface="gallery-weather-current"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-weather-current" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-weather-current" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-weather-current" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-weather-current" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "°"
#5 createComponent surface="gallery-weather-current" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "°"
#6 createComponent surface="gallery-weather-current" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-weather-current" tag=7 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-weather-current" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#9 updateData surface="gallery-weather-current"
  data:
    "description" = "Clear skies with light breeze"
    "forecast" = [{"date": "2025-12-16", "icon": "☀️", "temp": 74}, {"date": "2025-12-17", "icon": "☀️", "temp": 76}, {"date": "2025-12-18", "icon": "⛅", "temp": 71}, {"date": "2025-12-19", "icon": "☀️", "temp": 73}, {"date": "2025-12-20", "icon": "☀️", "temp": 75}]
    "location" = "Austin, TX"
    "tempHigh" = 72
    "tempLow" = 58
#10 createComponent surface="gallery-weather-current" tag=13 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#11 createComponent surface="gallery-weather-current" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Tue"
#12 createComponent surface="gallery-weather-current" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "☀️"
#13 createComponent surface="gallery-weather-current" tag=16 parent=13 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "74°"
#14 createComponent surface="gallery-weather-current" tag=17 parent=8 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#15 createComponent surface="gallery-weather-current" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Wed"
#16 createComponent surface="gallery-weather-current" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "☀️"
#17 createComponent surface="gallery-weather-current" tag=20 parent=17 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "76°"
#18 createComponent surface="gallery-weather-current" tag=21 parent=8 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#19 createComponent surface="gallery-weather-current" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Thu"
#20 createComponent surface="gallery-weather-current" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "⛅"
#21 createComponent surface="gallery-weather-current" tag=24 parent=21 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "71°"
#22 createComponent surface="gallery-weather-current" tag=25 parent=8 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#23 createComponent surface="gallery-weather-current" tag=26 parent=25 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Fri"
#24 createComponent surface="gallery-weather-current" tag=27 parent=25 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "☀️"
#25 createComponent surface="gallery-weather-current" tag=28 parent=25 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "73°"
#26 createComponent surface="gallery-weather-current" tag=29 parent=8 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#27 createComponent surface="gallery-weather-current" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Sat"
#28 createComponent surface="gallery-weather-current" tag=31 parent=29 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "☀️"
#29 createComponent surface="gallery-weather-current" tag=32 parent=29 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "75°"
#30 updateComponent surface="gallery-weather-current" tag=4 view="KRRichTextView"
  props:
    "text" = "72°"
#31 updateComponent surface="gallery-weather-current" tag=5 view="KRRichTextView"
  props:
    "text" = "58°"
#32 updateComponent surface="gallery-weather-current" tag=6 view="KRRichTextView"
  props:
    "text" = "Austin, TX"
#33 updateComponent surface="gallery-weather-current" tag=7 view="KRRichTextView"
  props:
    "text" = "Clear skies with light breeze"
#state dataModel "{\"tempHigh\":72,\"tempLow\":58,\"location\":\"Austin, TX\",\"description\":\"Clear skies with light breeze\",\"forecast\":[{\"date\":\"2025-12-16\",\"icon\":\"☀️\",\"temp\":74},{\"date\":\"2025-12-17\",\"icon\":\"☀️\",\"temp\":76},{\"date\":\"2025-12-18\",\"icon\":\"⛅\",\"temp\":71},{\"date\":\"2025-12-19\",\"icon\":\"☀️\",\"temp\":73},{\"date\":\"2025-12-20\",\"icon\":\"☀️\",\"temp\":75}]}"
#state actions {}
""",
        """v0_9.examples.05_product-card""" to """#0 createSurface surface="gallery-product-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-product-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-product-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-product-card" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#4 createComponent surface="gallery-product-card" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-product-card" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-product-card" tag=6 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="gallery-product-card" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-product-card" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "( )"
#9 createComponent surface="gallery-product-card" tag=9 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-product-card" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-product-card" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-product-card" tag=12 parent=4 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="gallery-product-card" tag=14 parent=12 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#14 createComponent surface="gallery-product-card" tag=13 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Add to Cart"
#15 updateData surface="gallery-product-card"
  data:
    "imageUrl" = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=200&fit=crop"
    "name" = "Wireless Headphones Pro"
    "originalPrice" = 249.99
    "price" = 199.99
    "reviewCount" = 2847
    "stars" = "★★★★★"
#16 updateComponent surface="gallery-product-card" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=200&fit=crop"
#17 updateComponent surface="gallery-product-card" tag=5 view="KRRichTextView"
  props:
    "text" = "Wireless Headphones Pro"
#18 updateComponent surface="gallery-product-card" tag=7 view="KRRichTextView"
  props:
    "text" = "★★★★★"
#19 updateComponent surface="gallery-product-card" tag=8 view="KRRichTextView"
  props:
    "text" = "(2,847 reviews)"
#20 updateComponent surface="gallery-product-card" tag=10 view="KRRichTextView"
  props:
    "text" = "${'$'}199.99"
#21 updateComponent surface="gallery-product-card" tag=11 view="KRRichTextView"
  props:
    "text" = "${'$'}249.99"
#state dataModel "{\"imageUrl\":\"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=200&fit=crop\",\"name\":\"Wireless Headphones Pro\",\"stars\":\"★★★★★\",\"reviewCount\":2847,\"price\":199.99,\"originalPrice\":249.99}"
#state actions {"14": "{\"event\":{\"name\":\"addToCart\",\"context\":{}}}"}
""",
        """v0_9.examples.06_music-player""" to """#0 createSurface surface="gallery-music-player"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-music-player" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-music-player" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-music-player" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#4 createComponent surface="gallery-music-player" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-music-player" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-music-player" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-music-player" tag=7 parent=2 index=2 view="KRSliderView"
  props:
    "__sliderBindingPath" = "progress"
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 1
    "progressColor" = "4278239231"
    "value" = ""
#8 createComponent surface="gallery-music-player" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#9 createComponent surface="gallery-music-player" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-music-player" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-music-player" tag=11 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "center"
#12 createComponent surface="gallery-music-player" tag=13 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#13 createComponent surface="gallery-music-player" tag=12 parent=13 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏮"
    "textAlign" = "center"
    "width" = 24
#14 createComponent surface="gallery-music-player" tag=15 parent=11 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#15 createComponent surface="gallery-music-player" tag=14 parent=15 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#16 createComponent surface="gallery-music-player" tag=17 parent=11 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#17 createComponent surface="gallery-music-player" tag=16 parent=17 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏭"
    "textAlign" = "center"
    "width" = 24
#18 updateData surface="gallery-music-player"
  data:
    "album" = "After Hours"
    "albumArt" = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop"
    "artist" = "The Weeknd"
    "currentTime" = "1:48"
    "playIcon" = "pause"
    "progress" = 0.45
    "title" = "Blinding Lights"
    "totalTime" = "4:22"
#19 updateComponent surface="gallery-music-player" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop"
#20 updateComponent surface="gallery-music-player" tag=5 view="KRRichTextView"
  props:
    "text" = "Blinding Lights"
#21 updateComponent surface="gallery-music-player" tag=6 view="KRRichTextView"
  props:
    "text" = "The Weeknd"
#22 updateComponent surface="gallery-music-player" tag=7 view="KRSliderView"
  props:
    "value" = 0.45
#23 updateComponent surface="gallery-music-player" tag=9 view="KRRichTextView"
  props:
    "text" = "1:48"
#24 updateComponent surface="gallery-music-player" tag=10 view="KRRichTextView"
  props:
    "text" = "4:22"
#25 updateComponent surface="gallery-music-player" tag=14 view="KRRichTextView"
  props:
    "text" = "⏸"
#state dataModel "{\"albumArt\":\"https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop\",\"title\":\"Blinding Lights\",\"artist\":\"The Weeknd\",\"album\":\"After Hours\",\"progress\":0.45,\"currentTime\":\"1:48\",\"totalTime\":\"4:22\",\"playIcon\":\"pause\"}"
#state actions {"13": "{\"event\":{\"name\":\"previous\",\"context\":{}}}", "15": "{\"event\":{\"name\":\"playPause\",\"context\":{}}}", "17": "{\"event\":{\"name\":\"next\",\"context\":{}}}"}
""",
        """v0_9.examples.07_task-card""" to """#0 createSurface surface="gallery-task-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-task-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-task-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="gallery-task-card" tag=3 parent=2 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "completed"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "margin" = 8
    "width" = 24
#4 createComponent surface="gallery-task-card" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#5 createComponent surface="gallery-task-card" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-task-card" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-task-card" tag=7 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#8 createComponent surface="gallery-task-card" tag=11 parent=7 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#9 createComponent surface="gallery-task-card" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Due"
#10 createComponent surface="gallery-task-card" tag=8 parent=11 index=1 view="KRDateTimeInputView"
  props:
    "__dateTimeBindingPath" = "dueDate"
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Due"
    "value" = ""
#11 createComponent surface="gallery-task-card" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-task-card" tag=10 parent=2 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#13 updateData surface="gallery-task-card"
  data:
    "completed" = false
    "description" = "Review and approve the authentication module changes."
    "dueDate" = "2025-12-15T17:00:00Z"
    "priorityIcon" = "priority_high"
    "project" = "Backend"
    "title" = "Review pull request"
#14 updateComponent surface="gallery-task-card" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#15 updateComponent surface="gallery-task-card" tag=5 view="KRRichTextView"
  props:
    "text" = "Review pull request"
#16 updateComponent surface="gallery-task-card" tag=6 view="KRRichTextView"
  props:
    "text" = "Review and approve the authentication module changes."
#17 updateComponent surface="gallery-task-card" tag=8 view="KRDateTimeInputView"
  props:
    "value" = "2025-12-15T17:00:00Z"
#18 updateComponent surface="gallery-task-card" tag=12 view="KRRichTextView"
  props:
    "text" = "Due"
#19 updateComponent surface="gallery-task-card" tag=9 view="KRRichTextView"
  props:
    "text" = "Backend"
#20 updateComponent surface="gallery-task-card" tag=10 view="KRRichTextView"
  props:
    "text" = "❗"
#state dataModel "{\"completed\":false,\"title\":\"Review pull request\",\"description\":\"Review and approve the authentication module changes.\",\"dueDate\":\"2025-12-15T17:00:00Z\",\"project\":\"Backend\",\"priorityIcon\":\"priority_high\"}"
#state actions {}
""",
        """v0_9.examples.08_user-profile""" to """#0 createSurface surface="gallery-user-profile"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-user-profile" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-user-profile" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-user-profile" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
    "width" = 40
#4 createComponent surface="gallery-user-profile" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-user-profile" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-user-profile" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-user-profile" tag=7 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-user-profile" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#9 createComponent surface="gallery-user-profile" tag=9 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#10 createComponent surface="gallery-user-profile" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-user-profile" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Followers"
#12 createComponent surface="gallery-user-profile" tag=12 parent=8 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#13 createComponent surface="gallery-user-profile" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#14 createComponent surface="gallery-user-profile" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Following"
#15 createComponent surface="gallery-user-profile" tag=15 parent=8 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#16 createComponent surface="gallery-user-profile" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#17 createComponent surface="gallery-user-profile" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Posts"
#18 createComponent surface="gallery-user-profile" tag=19 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#19 createComponent surface="gallery-user-profile" tag=18 parent=19 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = ""
#20 updateData surface="gallery-user-profile"
  data:
    "avatar" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop"
    "bio" = "Product Designer at Tech Co. Creating delightful experiences."
    "followText" = "Follow"
    "followers" = 12400
    "following" = 892
    "name" = "Sarah Chen"
    "posts" = 347
    "username" = "@sarahchen"
#21 updateComponent surface="gallery-user-profile" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop"
#22 updateComponent surface="gallery-user-profile" tag=5 view="KRRichTextView"
  props:
    "text" = "Sarah Chen"
#23 updateComponent surface="gallery-user-profile" tag=6 view="KRRichTextView"
  props:
    "text" = "@sarahchen"
#24 updateComponent surface="gallery-user-profile" tag=7 view="KRRichTextView"
  props:
    "text" = "Product Designer at Tech Co. Creating delightful experiences."
#25 updateComponent surface="gallery-user-profile" tag=10 view="KRRichTextView"
  props:
    "text" = "12,400"
#26 updateComponent surface="gallery-user-profile" tag=13 view="KRRichTextView"
  props:
    "text" = "892"
#27 updateComponent surface="gallery-user-profile" tag=16 view="KRRichTextView"
  props:
    "text" = "347"
#28 updateComponent surface="gallery-user-profile" tag=18 view="KRRichTextView"
  props:
    "text" = "Follow"
#state dataModel "{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop\",\"name\":\"Sarah Chen\",\"username\":\"@sarahchen\",\"bio\":\"Product Designer at Tech Co. Creating delightful experiences.\",\"followers\":12400,\"following\":892,\"posts\":347,\"followText\":\"Follow\"}"
#state actions {"19": "{\"event\":{\"name\":\"follow\",\"context\":{}}}"}
""",
        """v0_9.examples.09_login-form""" to """#0 createSurface surface="gallery-login-form"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-login-form" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-login-form" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-login-form" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#4 createComponent surface="gallery-login-form" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Welcome back"
#5 createComponent surface="gallery-login-form" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Sign in to your account"
#6 createComponent surface="gallery-login-form" tag=15 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="gallery-login-form" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email"
#8 createComponent surface="gallery-login-form" tag=6 parent=15 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="gallery-login-form" tag=17 parent=15 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Email is required"
#10 createComponent surface="gallery-login-form" tag=18 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#11 createComponent surface="gallery-login-form" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Password"
#12 createComponent surface="gallery-login-form" tag=7 parent=18 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "password"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "obscured"
#13 createComponent surface="gallery-login-form" tag=20 parent=18 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Password is required"
#14 createComponent surface="gallery-login-form" tag=21 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#15 createComponent surface="gallery-login-form" tag=9 parent=21 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#16 createComponent surface="gallery-login-form" tag=22 parent=21 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Please fix errors before signing in"
#17 createComponent surface="gallery-login-form" tag=8 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Sign in"
#18 createComponent surface="gallery-login-form" tag=10 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#19 createComponent surface="gallery-login-form" tag=11 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "center"
#20 createComponent surface="gallery-login-form" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Don't have an account?"
#21 createComponent surface="gallery-login-form" tag=14 parent=11 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#22 createComponent surface="gallery-login-form" tag=13 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Sign up"
#23 updateData surface="gallery-login-form"
  data:
    "email" = ""
    "password" = ""
#24 updateComponent surface="gallery-login-form" tag=6 view="KRTextFieldView"
  props:
    "border" = "1 solid 4292030255"
    "text" = ""
#25 updateComponent surface="gallery-login-form" tag=16 view="KRRichTextView"
  props:
    "text" = "Email"
#26 updateComponent surface="gallery-login-form" tag=7 view="KRTextFieldView"
  props:
    "border" = "1 solid 4292030255"
    "text" = ""
#27 updateComponent surface="gallery-login-form" tag=19 view="KRRichTextView"
  props:
    "text" = "Password"
#28 updateComponent surface="gallery-login-form" tag=17 view="KRRichTextView"
  props:
    "height" = NaN
    "opacity" = 1.0
    "text" = "Email is required"
#29 updateComponent surface="gallery-login-form" tag=22 view="KRRichTextView"
  props:
    "height" = NaN
    "opacity" = 1.0
    "text" = "Please fix errors before signing in"
#30 updateComponent surface="gallery-login-form" tag=9 view="KRView"
  props:
    "opacity" = 0.4
    "touchEnable" = 0
#31 updateComponent surface="gallery-login-form" tag=20 view="KRRichTextView"
  props:
    "height" = NaN
    "opacity" = 1.0
    "text" = "Password is required"
#state dataModel "{\"email\":\"\",\"password\":\"\"}"
#state actions {"14": "{\"event\":{\"name\":\"signup\",\"context\":{}}}", "9": null}
""",
        """v0_9.examples.10_notification-permission""" to """#0 createSurface surface="gallery-notification-permission"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-notification-permission" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-notification-permission" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-notification-permission" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#4 createComponent surface="gallery-notification-permission" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="gallery-notification-permission" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-notification-permission" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "center"
#7 createComponent surface="gallery-notification-permission" tag=8 parent=6 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="gallery-notification-permission" tag=7 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Yes"
#9 createComponent surface="gallery-notification-permission" tag=10 parent=6 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#10 createComponent surface="gallery-notification-permission" tag=9 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "No"
#11 updateData surface="gallery-notification-permission"
  data:
    "description" = "Get alerts for order status changes"
    "icon" = "check"
    "title" = "Enable notification"
#12 updateComponent surface="gallery-notification-permission" tag=3 view="KRRichTextView"
  props:
    "text" = "✓"
#13 updateComponent surface="gallery-notification-permission" tag=4 view="KRRichTextView"
  props:
    "text" = "Enable notification"
#14 updateComponent surface="gallery-notification-permission" tag=5 view="KRRichTextView"
  props:
    "text" = "Get alerts for order status changes"
#state dataModel "{\"icon\":\"check\",\"title\":\"Enable notification\",\"description\":\"Get alerts for order status changes\"}"
#state actions {"10": "{\"event\":{\"name\":\"decline\",\"context\":{}}}", "8": "{\"event\":{\"name\":\"accept\",\"context\":{}}}"}
""",
        """v0_9.examples.11_purchase-complete""" to """#0 createSurface surface="gallery-purchase-complete"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-purchase-complete" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-purchase-complete" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-purchase-complete" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#4 createComponent surface="gallery-purchase-complete" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Purchase Complete"
#5 createComponent surface="gallery-purchase-complete" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#6 createComponent surface="gallery-purchase-complete" tag=6 parent=5 index=0 view="KRImageView"
  props:
    "height" = 100
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
    "width" = 100
#7 createComponent surface="gallery-purchase-complete" tag=7 parent=5 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#8 createComponent surface="gallery-purchase-complete" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-purchase-complete" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-purchase-complete" tag=10 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#11 createComponent surface="gallery-purchase-complete" tag=11 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#12 createComponent surface="gallery-purchase-complete" tag=12 parent=11 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="gallery-purchase-complete" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "→"
    "textAlign" = "center"
    "width" = 24
#14 createComponent surface="gallery-purchase-complete" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-purchase-complete" tag=15 parent=11 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#16 createComponent surface="gallery-purchase-complete" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Sold by:"
#17 createComponent surface="gallery-purchase-complete" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#18 createComponent surface="gallery-purchase-complete" tag=19 parent=2 index=5 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#19 createComponent surface="gallery-purchase-complete" tag=18 parent=19 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "View Order Details"
#20 updateData surface="gallery-purchase-complete"
  data:
    "deliveryDate" = "Arrives Dec 18 - Dec 20"
    "price" = 199.99
    "productImage" = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100&h=100&fit=crop"
    "productName" = "Wireless Headphones Pro"
    "seller" = "TechStore Official"
#21 updateComponent surface="gallery-purchase-complete" tag=6 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100&h=100&fit=crop"
#22 updateComponent surface="gallery-purchase-complete" tag=8 view="KRRichTextView"
  props:
    "text" = "Wireless Headphones Pro"
#23 updateComponent surface="gallery-purchase-complete" tag=9 view="KRRichTextView"
  props:
    "text" = "${'$'}199.99"
#24 updateComponent surface="gallery-purchase-complete" tag=14 view="KRRichTextView"
  props:
    "text" = "Arrives Dec 18 - Dec 20"
#25 updateComponent surface="gallery-purchase-complete" tag=17 view="KRRichTextView"
  props:
    "text" = "TechStore Official"
#state dataModel "{\"productImage\":\"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100&h=100&fit=crop\",\"productName\":\"Wireless Headphones Pro\",\"price\":199.99,\"deliveryDate\":\"Arrives Dec 18 - Dec 20\",\"seller\":\"TechStore Official\"}"
#state actions {"19": "{\"event\":{\"name\":\"view_details\",\"context\":{}}}"}
""",
        """v0_9.examples.12_chat-message""" to """#0 createSurface surface="gallery-chat-message"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-chat-message" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-chat-message" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-chat-message" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-chat-message" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "ℹ"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-chat-message" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-chat-message" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="gallery-chat-message" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#8 updateData surface="gallery-chat-message"
  data:
    "channelName" = "project-updates"
    "messages" = [{"avatar": "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop", "text": "Just pushed the new API changes. Ready for review.", "timestamp": "2025-12-15T10:32:00Z", "username": "Mike Chen"}, {"avatar": "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=40&h=40&fit=crop", "text": "Great! I'll take a look after standup.", "timestamp": "2025-12-15T10:45:00Z", "username": "Sarah Kim"}]
#9 createComponent surface="gallery-chat-message" tag=15 parent=7 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-chat-message" tag=16 parent=15 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop"
    "width" = 40
#11 createComponent surface="gallery-chat-message" tag=17 parent=15 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="gallery-chat-message" tag=18 parent=17 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="gallery-chat-message" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Mike Chen"
#14 createComponent surface="gallery-chat-message" tag=20 parent=18 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "10:32 AM"
#15 createComponent surface="gallery-chat-message" tag=21 parent=17 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Just pushed the new API changes. Ready for review."
#16 createComponent surface="gallery-chat-message" tag=22 parent=7 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#17 createComponent surface="gallery-chat-message" tag=23 parent=22 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=40&h=40&fit=crop"
    "width" = 40
#18 createComponent surface="gallery-chat-message" tag=24 parent=22 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#19 createComponent surface="gallery-chat-message" tag=25 parent=24 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#20 createComponent surface="gallery-chat-message" tag=26 parent=25 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Sarah Kim"
#21 createComponent surface="gallery-chat-message" tag=27 parent=25 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "10:45 AM"
#22 createComponent surface="gallery-chat-message" tag=28 parent=24 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Great! I'll take a look after standup."
#23 updateComponent surface="gallery-chat-message" tag=5 view="KRRichTextView"
  props:
    "text" = "project-updates"
#state dataModel "{\"channelName\":\"project-updates\",\"messages\":[{\"avatar\":\"https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop\",\"username\":\"Mike Chen\",\"timestamp\":\"2025-12-15T10:32:00Z\",\"text\":\"Just pushed the new API changes. Ready for review.\"},{\"avatar\":\"https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=40&h=40&fit=crop\",\"username\":\"Sarah Kim\",\"timestamp\":\"2025-12-15T10:45:00Z\",\"text\":\"Great! I'll take a look after standup.\"}]}"
#state actions {}
""",
        """v0_9.examples.13_coffee-order""" to """#0 createSurface surface="gallery-coffee-order"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-coffee-order" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-coffee-order" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-coffee-order" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-coffee-order" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "❤"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-coffee-order" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-coffee-order" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#7 createComponent surface="gallery-coffee-order" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#8 createComponent surface="gallery-coffee-order" tag=13 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#9 createComponent surface="gallery-coffee-order" tag=14 parent=13 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#10 createComponent surface="gallery-coffee-order" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Subtotal"
#11 createComponent surface="gallery-coffee-order" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-coffee-order" tag=17 parent=13 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#13 createComponent surface="gallery-coffee-order" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Tax"
#14 createComponent surface="gallery-coffee-order" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-coffee-order" tag=20 parent=13 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#16 createComponent surface="gallery-coffee-order" tag=21 parent=20 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Total"
#17 createComponent surface="gallery-coffee-order" tag=22 parent=20 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#18 createComponent surface="gallery-coffee-order" tag=23 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#19 createComponent surface="gallery-coffee-order" tag=25 parent=23 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#20 createComponent surface="gallery-coffee-order" tag=24 parent=25 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Purchase"
#21 createComponent surface="gallery-coffee-order" tag=27 parent=23 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#22 createComponent surface="gallery-coffee-order" tag=26 parent=27 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Add to cart"
#23 updateData surface="gallery-coffee-order"
  data:
    "items" = [{"name": "Oat Milk Latte", "price": 6.45, "size": "Grande, Extra Shot"}, {"name": "Chocolate Croissant", "price": 4.25, "size": "Warmed"}]
    "storeName" = "Sunrise Coffee"
    "subtotal" = 10.7
    "tax" = 0.96
    "total" = 11.66
#24 createComponent surface="gallery-coffee-order" tag=28 parent=6 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "spaceBetween"
#25 createComponent surface="gallery-coffee-order" tag=29 parent=28 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#26 createComponent surface="gallery-coffee-order" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Oat Milk Latte"
#27 createComponent surface="gallery-coffee-order" tag=31 parent=29 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Grande, Extra Shot"
#28 createComponent surface="gallery-coffee-order" tag=32 parent=28 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}6.45"
#29 createComponent surface="gallery-coffee-order" tag=33 parent=6 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "spaceBetween"
#30 createComponent surface="gallery-coffee-order" tag=34 parent=33 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#31 createComponent surface="gallery-coffee-order" tag=35 parent=34 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Chocolate Croissant"
#32 createComponent surface="gallery-coffee-order" tag=36 parent=34 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Warmed"
#33 createComponent surface="gallery-coffee-order" tag=37 parent=33 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}4.25"
#34 updateComponent surface="gallery-coffee-order" tag=5 view="KRRichTextView"
  props:
    "text" = "Sunrise Coffee"
#35 updateComponent surface="gallery-coffee-order" tag=16 view="KRRichTextView"
  props:
    "text" = "${'$'}10.70"
#36 updateComponent surface="gallery-coffee-order" tag=19 view="KRRichTextView"
  props:
    "text" = "${'$'}0.96"
#37 updateComponent surface="gallery-coffee-order" tag=22 view="KRRichTextView"
  props:
    "text" = "${'$'}11.66"
#state dataModel "{\"storeName\":\"Sunrise Coffee\",\"items\":[{\"name\":\"Oat Milk Latte\",\"size\":\"Grande, Extra Shot\",\"price\":6.45},{\"name\":\"Chocolate Croissant\",\"size\":\"Warmed\",\"price\":4.25}],\"subtotal\":10.7,\"tax\":0.96,\"total\":11.66}"
#state actions {"25": "{\"event\":{\"name\":\"purchase\",\"context\":{}}}", "27": "{\"event\":{\"name\":\"add_to_cart\",\"context\":{}}}"}
""",
        """v0_9.examples.14_sports-player""" to """#0 createSurface surface="gallery-sports-player"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-sports-player" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-sports-player" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-sports-player" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#4 createComponent surface="gallery-sports-player" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-sports-player" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-sports-player" tag=6 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="gallery-sports-player" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-sports-player" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-sports-player" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#10 createComponent surface="gallery-sports-player" tag=10 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#11 createComponent surface="gallery-sports-player" tag=11 parent=10 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="gallery-sports-player" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#13 createComponent surface="gallery-sports-player" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#14 createComponent surface="gallery-sports-player" tag=14 parent=10 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#15 createComponent surface="gallery-sports-player" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#16 createComponent surface="gallery-sports-player" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#17 createComponent surface="gallery-sports-player" tag=17 parent=10 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#18 createComponent surface="gallery-sports-player" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#19 createComponent surface="gallery-sports-player" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#20 updateData surface="gallery-sports-player"
  data:
    "number" = "#23"
    "playerImage" = "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=200&h=200&fit=crop"
    "playerName" = "Marcus Johnson"
    "stat1" = {"label": "PPG", "value": "28.4"}
    "stat2" = {"label": "RPG", "value": "7.2"}
    "stat3" = {"label": "APG", "value": "6.8"}
    "team" = "LA Lakers"
#21 updateComponent surface="gallery-sports-player" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=200&h=200&fit=crop"
#22 updateComponent surface="gallery-sports-player" tag=5 view="KRRichTextView"
  props:
    "text" = "Marcus Johnson"
#23 updateComponent surface="gallery-sports-player" tag=7 view="KRRichTextView"
  props:
    "text" = "#23"
#24 updateComponent surface="gallery-sports-player" tag=8 view="KRRichTextView"
  props:
    "text" = "LA Lakers"
#25 updateComponent surface="gallery-sports-player" tag=12 view="KRRichTextView"
  props:
    "text" = "28.4"
#26 updateComponent surface="gallery-sports-player" tag=13 view="KRRichTextView"
  props:
    "text" = "PPG"
#27 updateComponent surface="gallery-sports-player" tag=15 view="KRRichTextView"
  props:
    "text" = "7.2"
#28 updateComponent surface="gallery-sports-player" tag=16 view="KRRichTextView"
  props:
    "text" = "RPG"
#29 updateComponent surface="gallery-sports-player" tag=18 view="KRRichTextView"
  props:
    "text" = "6.8"
#30 updateComponent surface="gallery-sports-player" tag=19 view="KRRichTextView"
  props:
    "text" = "APG"
#state dataModel "{\"playerImage\":\"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=200&h=200&fit=crop\",\"playerName\":\"Marcus Johnson\",\"number\":\"#23\",\"team\":\"LA Lakers\",\"stat1\":{\"value\":\"28.4\",\"label\":\"PPG\"},\"stat2\":{\"value\":\"7.2\",\"label\":\"RPG\"},\"stat3\":{\"value\":\"6.8\",\"label\":\"APG\"}}"
#state actions {}
""",
        """v0_9.examples.15_account-balance""" to """#0 createSurface surface="gallery-account-balance"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-account-balance" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-account-balance" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-account-balance" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-account-balance" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "💳"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-account-balance" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-account-balance" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-account-balance" tag=7 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-account-balance" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#9 createComponent surface="gallery-account-balance" tag=9 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-account-balance" tag=11 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="gallery-account-balance" tag=10 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Transfer"
#12 createComponent surface="gallery-account-balance" tag=13 parent=9 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#13 createComponent surface="gallery-account-balance" tag=12 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Pay Bill"
#14 updateData surface="gallery-account-balance"
  data:
    "accountName" = "Primary Checking"
    "balance" = 12458.32
    "lastUpdated" = "Updated just now"
#15 updateComponent surface="gallery-account-balance" tag=5 view="KRRichTextView"
  props:
    "text" = "Primary Checking"
#16 updateComponent surface="gallery-account-balance" tag=6 view="KRRichTextView"
  props:
    "text" = "${'$'}12,458.32"
#17 updateComponent surface="gallery-account-balance" tag=7 view="KRRichTextView"
  props:
    "text" = "Updated just now"
#state dataModel "{\"accountName\":\"Primary Checking\",\"balance\":12458.32,\"lastUpdated\":\"Updated just now\"}"
#state actions {"11": "{\"event\":{\"name\":\"transfer\",\"context\":{}}}", "13": "{\"event\":{\"name\":\"pay_bill\",\"context\":{}}}"}
""",
        """v0_9.examples.16_workout-summary""" to """#0 createSurface surface="gallery-workout-summary"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-workout-summary" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-workout-summary" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-workout-summary" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-workout-summary" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-workout-summary" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Workout Complete"
#6 createComponent surface="gallery-workout-summary" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="gallery-workout-summary" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#8 createComponent surface="gallery-workout-summary" tag=8 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#9 createComponent surface="gallery-workout-summary" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-workout-summary" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Duration"
#11 createComponent surface="gallery-workout-summary" tag=11 parent=7 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="gallery-workout-summary" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#13 createComponent surface="gallery-workout-summary" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Calories"
#14 createComponent surface="gallery-workout-summary" tag=14 parent=7 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#15 createComponent surface="gallery-workout-summary" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = " km"
#16 createComponent surface="gallery-workout-summary" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Distance"
#17 createComponent surface="gallery-workout-summary" tag=17 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#18 updateData surface="gallery-workout-summary"
  data:
    "calories" = 385
    "date" = "2025-12-15T07:30:00Z"
    "distance" = 5.2
    "duration" = "32:15"
    "icon" = "directions_run"
    "workoutType" = "Morning Run"
#19 updateComponent surface="gallery-workout-summary" tag=4 view="KRRichTextView"
  props:
    "text" = "🏃"
#20 updateComponent surface="gallery-workout-summary" tag=9 view="KRRichTextView"
  props:
    "text" = "32:15"
#21 updateComponent surface="gallery-workout-summary" tag=12 view="KRRichTextView"
  props:
    "text" = "385"
#22 updateComponent surface="gallery-workout-summary" tag=15 view="KRRichTextView"
  props:
    "text" = "5.2 km"
#23 updateComponent surface="gallery-workout-summary" tag=17 view="KRRichTextView"
  props:
    "text" = "Monday, Dec 15 at 7:30 AM"
#state dataModel "{\"icon\":\"directions_run\",\"workoutType\":\"Morning Run\",\"duration\":\"32:15\",\"calories\":385,\"distance\":5.2,\"date\":\"2025-12-15T07:30:00Z\"}"
#state actions {}
""",
        """v0_9.examples.17_event-detail""" to """#0 createSurface surface="gallery-event-detail"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-event-detail" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-event-detail" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-event-detail" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="gallery-event-detail" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="gallery-event-detail" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📅"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="gallery-event-detail" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = " •  - "
#7 createComponent surface="gallery-event-detail" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#8 createComponent surface="gallery-event-detail" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📍"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="gallery-event-detail" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-event-detail" tag=10 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-event-detail" tag=11 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#12 createComponent surface="gallery-event-detail" tag=12 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="gallery-event-detail" tag=14 parent=12 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#14 createComponent surface="gallery-event-detail" tag=13 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Accept"
#15 createComponent surface="gallery-event-detail" tag=16 parent=12 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#16 createComponent surface="gallery-event-detail" tag=15 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Decline"
#17 updateData surface="gallery-event-detail"
  data:
    "description" = "Review final product specs and marketing materials before the Q1 launch."
    "end" = "2025-12-19T15:30:00Z"
    "location" = "Conference Room A, Building 2"
    "start" = "2025-12-19T14:00:00Z"
    "title" = "Product Launch Meeting"
#18 updateComponent surface="gallery-event-detail" tag=3 view="KRRichTextView"
  props:
    "text" = "Product Launch Meeting"
#19 updateComponent surface="gallery-event-detail" tag=6 view="KRRichTextView"
  props:
    "text" = "Fri, Dec 19 • 2:00 PM - 3:30 PM"
#20 updateComponent surface="gallery-event-detail" tag=9 view="KRRichTextView"
  props:
    "text" = "Conference Room A, Building 2"
#21 updateComponent surface="gallery-event-detail" tag=10 view="KRRichTextView"
  props:
    "text" = "Review final product specs and marketing materials before the Q1 launch."
#state dataModel "{\"title\":\"Product Launch Meeting\",\"start\":\"2025-12-19T14:00:00Z\",\"end\":\"2025-12-19T15:30:00Z\",\"location\":\"Conference Room A, Building 2\",\"description\":\"Review final product specs and marketing materials before the Q1 launch.\"}"
#state actions {"14": "{\"event\":{\"name\":\"accept\",\"context\":{}}}", "16": "{\"event\":{\"name\":\"decline\",\"context\":{}}}"}
""",
        """v0_9.examples.18_track-list""" to """#0 createSurface surface="gallery-track-list"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-track-list" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-track-list" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-track-list" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-track-list" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "▶"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-track-list" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-track-list" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="gallery-track-list" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#8 updateData surface="gallery-track-list"
  data:
    "playlistName" = "Focus Flow"
    "tracks" = [{"art": "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=50&h=50&fit=crop", "artist": "Marconi Union", "duration": "8:09", "number": 1, "title": "Weightless"}, {"art": "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=50&h=50&fit=crop", "artist": "Debussy", "duration": "5:12", "number": 2, "title": "Clair de Lune"}, {"art": "https://images.unsplash.com/photo-1507838153414-b4b713384a76?w=50&h=50&fit=crop", "artist": "Brian Eno", "duration": "6:45", "number": 3, "title": "Ambient Light"}]
#9 createComponent surface="gallery-track-list" tag=15 parent=7 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-track-list" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "1"
#11 createComponent surface="gallery-track-list" tag=17 parent=15 index=1 view="KRImageView"
  props:
    "height" = 100
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=50&h=50&fit=crop"
    "width" = 100
#12 createComponent surface="gallery-track-list" tag=18 parent=15 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#13 createComponent surface="gallery-track-list" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Weightless"
#14 createComponent surface="gallery-track-list" tag=20 parent=18 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Marconi Union"
#15 createComponent surface="gallery-track-list" tag=21 parent=15 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "8:09"
#16 createComponent surface="gallery-track-list" tag=22 parent=7 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#17 createComponent surface="gallery-track-list" tag=23 parent=22 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "2"
#18 createComponent surface="gallery-track-list" tag=24 parent=22 index=1 view="KRImageView"
  props:
    "height" = 100
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=50&h=50&fit=crop"
    "width" = 100
#19 createComponent surface="gallery-track-list" tag=25 parent=22 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#20 createComponent surface="gallery-track-list" tag=26 parent=25 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Clair de Lune"
#21 createComponent surface="gallery-track-list" tag=27 parent=25 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Debussy"
#22 createComponent surface="gallery-track-list" tag=28 parent=22 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "5:12"
#23 createComponent surface="gallery-track-list" tag=29 parent=7 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#24 createComponent surface="gallery-track-list" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "3"
#25 createComponent surface="gallery-track-list" tag=31 parent=29 index=1 view="KRImageView"
  props:
    "height" = 100
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1507838153414-b4b713384a76?w=50&h=50&fit=crop"
    "width" = 100
#26 createComponent surface="gallery-track-list" tag=32 parent=29 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#27 createComponent surface="gallery-track-list" tag=33 parent=32 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Ambient Light"
#28 createComponent surface="gallery-track-list" tag=34 parent=32 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Brian Eno"
#29 createComponent surface="gallery-track-list" tag=35 parent=29 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "6:45"
#30 updateComponent surface="gallery-track-list" tag=5 view="KRRichTextView"
  props:
    "text" = "Focus Flow"
#state dataModel "{\"playlistName\":\"Focus Flow\",\"tracks\":[{\"number\":1,\"art\":\"https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=50&h=50&fit=crop\",\"title\":\"Weightless\",\"artist\":\"Marconi Union\",\"duration\":\"8:09\"},{\"number\":2,\"art\":\"https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=50&h=50&fit=crop\",\"title\":\"Clair de Lune\",\"artist\":\"Debussy\",\"duration\":\"5:12\"},{\"number\":3,\"art\":\"https://images.unsplash.com/photo-1507838153414-b4b713384a76?w=50&h=50&fit=crop\",\"title\":\"Ambient Light\",\"artist\":\"Brian Eno\",\"duration\":\"6:45\"}]}"
#state actions {}
""",
        """v0_9.examples.19_software-purchase""" to """#0 createSurface surface="gallery-software-purchase"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-software-purchase" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-software-purchase" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-software-purchase" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Purchase License"
#4 createComponent surface="gallery-software-purchase" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="gallery-software-purchase" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#6 createComponent surface="gallery-software-purchase" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#7 createComponent surface="gallery-software-purchase" tag=7 parent=6 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#8 createComponent surface="gallery-software-purchase" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Number of seats"
#9 createComponent surface="gallery-software-purchase" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-software-purchase" tag=10 parent=6 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#11 createComponent surface="gallery-software-purchase" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Billing period"
#12 createComponent surface="gallery-software-purchase" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "margin" = 8
    "options" = [{"label": "Annual", "value": "annual"}, {"label": "Monthly", "value": "monthly"}]
    "value" = ""
    "variant" = "mutuallyExclusive"
#13 createComponent surface="gallery-software-purchase" tag=22 parent=12 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "annual"
    "__choicePickerParentId" = "period-picker"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#14 createComponent surface="gallery-software-purchase" tag=23 parent=22 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Annual"
#15 createComponent surface="gallery-software-purchase" tag=24 parent=12 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "monthly"
    "__choicePickerParentId" = "period-picker"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#16 createComponent surface="gallery-software-purchase" tag=25 parent=24 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Monthly"
#17 createComponent surface="gallery-software-purchase" tag=13 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#18 createComponent surface="gallery-software-purchase" tag=14 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#19 createComponent surface="gallery-software-purchase" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Total"
#20 createComponent surface="gallery-software-purchase" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "/year"
#21 createComponent surface="gallery-software-purchase" tag=17 parent=2 index=6 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#22 createComponent surface="gallery-software-purchase" tag=19 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#23 createComponent surface="gallery-software-purchase" tag=18 parent=19 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Confirm Purchase"
#24 createComponent surface="gallery-software-purchase" tag=21 parent=17 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#25 createComponent surface="gallery-software-purchase" tag=20 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Cancel"
#26 updateData surface="gallery-software-purchase"
  data:
    "billingPeriod" = ["annual"]
    "productName" = "Design Suite Pro"
    "seats" = "10 seats"
    "total" = 1188
#27 updateComponent surface="gallery-software-purchase" tag=4 view="KRRichTextView"
  props:
    "text" = "Design Suite Pro"
#28 updateComponent surface="gallery-software-purchase" tag=9 view="KRRichTextView"
  props:
    "text" = "10 seats"
#29 updateComponent surface="gallery-software-purchase" tag=22 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#30 updateComponent surface="gallery-software-purchase" tag=23 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#31 updateComponent surface="gallery-software-purchase" tag=24 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#32 updateComponent surface="gallery-software-purchase" tag=25 view="KRRichTextView"
  props:
    "color" = "#333333"
#33 updateComponent surface="gallery-software-purchase" tag=16 view="KRRichTextView"
  props:
    "text" = "${'$'}1,188.00/year"
#state dataModel "{\"productName\":\"Design Suite Pro\",\"seats\":\"10 seats\",\"billingPeriod\":[\"annual\"],\"total\":1188}"
#state actions {"19": "{\"event\":{\"name\":\"confirm\",\"context\":{}}}", "21": "{\"event\":{\"name\":\"cancel\",\"context\":{}}}"}
""",
        """v0_9.examples.20_restaurant-card""" to """#0 createSurface surface="gallery-restaurant-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-restaurant-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-restaurant-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-restaurant-card" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#4 createComponent surface="gallery-restaurant-card" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-restaurant-card" tag=5 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#6 createComponent surface="gallery-restaurant-card" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-restaurant-card" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-restaurant-card" tag=8 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-restaurant-card" tag=9 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-restaurant-card" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "★"
    "textAlign" = "center"
    "width" = 24
#11 createComponent surface="gallery-restaurant-card" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-restaurant-card" tag=12 parent=9 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#13 createComponent surface="gallery-restaurant-card" tag=13 parent=4 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#14 createComponent surface="gallery-restaurant-card" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-restaurant-card" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#16 updateData surface="gallery-restaurant-card"
  data:
    "cuisine" = "Italian • Pasta • Wine Bar"
    "deliveryTime" = "25-35 min"
    "distance" = "0.8 mi"
    "image" = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=300&h=150&fit=crop"
    "name" = "The Italian Kitchen"
    "priceRange" = "${'$'}${'$'}${'$'}"
    "rating" = "4.8"
    "reviewCount" = "(2,847 reviews)"
#17 updateComponent surface="gallery-restaurant-card" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=300&h=150&fit=crop"
#18 updateComponent surface="gallery-restaurant-card" tag=6 view="KRRichTextView"
  props:
    "text" = "The Italian Kitchen"
#19 updateComponent surface="gallery-restaurant-card" tag=7 view="KRRichTextView"
  props:
    "text" = "${'$'}${'$'}${'$'}"
#20 updateComponent surface="gallery-restaurant-card" tag=8 view="KRRichTextView"
  props:
    "text" = "Italian • Pasta • Wine Bar"
#21 updateComponent surface="gallery-restaurant-card" tag=11 view="KRRichTextView"
  props:
    "text" = "4.8"
#22 updateComponent surface="gallery-restaurant-card" tag=12 view="KRRichTextView"
  props:
    "text" = "(2,847 reviews)"
#23 updateComponent surface="gallery-restaurant-card" tag=14 view="KRRichTextView"
  props:
    "text" = "0.8 mi"
#24 updateComponent surface="gallery-restaurant-card" tag=15 view="KRRichTextView"
  props:
    "text" = "25-35 min"
#state dataModel "{\"image\":\"https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=300&h=150&fit=crop\",\"name\":\"The Italian Kitchen\",\"priceRange\":\"${'$'}${'$'}${'$'}\",\"cuisine\":\"Italian • Pasta • Wine Bar\",\"rating\":\"4.8\",\"reviewCount\":\"(2,847 reviews)\",\"distance\":\"0.8 mi\",\"deliveryTime\":\"25-35 min\"}"
#state actions {}
""",
        """v0_9.examples.21_shipping-status""" to """#0 createSurface surface="gallery-shipping-status"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-shipping-status" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-shipping-status" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-shipping-status" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-shipping-status" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "ℹ"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-shipping-status" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Package Status"
#6 createComponent surface="gallery-shipping-status" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-shipping-status" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#8 createComponent surface="gallery-shipping-status" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#9 createComponent surface="gallery-shipping-status" tag=12 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-shipping-status" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📅"
    "textAlign" = "center"
    "width" = 24
#11 createComponent surface="gallery-shipping-status" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#12 updateData surface="gallery-shipping-status"
  data:
    "eta" = "Estimated delivery: Today by 8 PM"
    "steps" = [{"icon": "check", "label": "Order Placed"}, {"icon": "check", "label": "Shipped"}, {"icon": "send", "label": "Out for Delivery"}, {"icon": "check", "label": "Delivered"}]
    "trackingNumber" = "Tracking: 1Z999AA10123456784"
#13 createComponent surface="gallery-shipping-status" tag=15 parent=8 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#14 createComponent surface="gallery-shipping-status" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#15 createComponent surface="gallery-shipping-status" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Order Placed"
#16 createComponent surface="gallery-shipping-status" tag=18 parent=8 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#17 createComponent surface="gallery-shipping-status" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#18 createComponent surface="gallery-shipping-status" tag=20 parent=18 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Shipped"
#19 createComponent surface="gallery-shipping-status" tag=21 parent=8 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#20 createComponent surface="gallery-shipping-status" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✈"
    "textAlign" = "center"
    "width" = 24
#21 createComponent surface="gallery-shipping-status" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Out for Delivery"
#22 createComponent surface="gallery-shipping-status" tag=24 parent=8 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#23 createComponent surface="gallery-shipping-status" tag=25 parent=24 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#24 createComponent surface="gallery-shipping-status" tag=26 parent=24 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Delivered"
#25 updateComponent surface="gallery-shipping-status" tag=6 view="KRRichTextView"
  props:
    "text" = "Tracking: 1Z999AA10123456784"
#26 updateComponent surface="gallery-shipping-status" tag=14 view="KRRichTextView"
  props:
    "text" = "Estimated delivery: Today by 8 PM"
#state dataModel "{\"trackingNumber\":\"Tracking: 1Z999AA10123456784\",\"steps\":[{\"icon\":\"check\",\"label\":\"Order Placed\"},{\"icon\":\"check\",\"label\":\"Shipped\"},{\"icon\":\"send\",\"label\":\"Out for Delivery\"},{\"icon\":\"check\",\"label\":\"Delivered\"}],\"eta\":\"Estimated delivery: Today by 8 PM\"}"
#state actions {}
""",
        """v0_9.examples.22_credit-card""" to """#0 createSurface surface="gallery-credit-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-credit-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-credit-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-credit-card" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#4 createComponent surface="gallery-credit-card" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "💳"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-credit-card" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-credit-card" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-credit-card" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#8 createComponent surface="gallery-credit-card" tag=8 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#9 createComponent surface="gallery-credit-card" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "CARD HOLDER"
#10 createComponent surface="gallery-credit-card" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-credit-card" tag=11 parent=7 index=1 view="KRView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="gallery-credit-card" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "EXPIRES"
#13 createComponent surface="gallery-credit-card" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#14 updateData surface="gallery-credit-card"
  data:
    "cardNumber" = "•••• •••• •••• 4242"
    "cardType" = "VISA"
    "expiryDate" = "09/27"
    "holderName" = "SARAH JOHNSON"
#15 updateComponent surface="gallery-credit-card" tag=5 view="KRRichTextView"
  props:
    "text" = "VISA"
#16 updateComponent surface="gallery-credit-card" tag=6 view="KRRichTextView"
  props:
    "text" = "•••• •••• •••• 4242"
#17 updateComponent surface="gallery-credit-card" tag=10 view="KRRichTextView"
  props:
    "text" = "SARAH JOHNSON"
#18 updateComponent surface="gallery-credit-card" tag=13 view="KRRichTextView"
  props:
    "text" = "09/27"
#state dataModel "{\"cardType\":\"VISA\",\"cardNumber\":\"•••• •••• •••• 4242\",\"holderName\":\"SARAH JOHNSON\",\"expiryDate\":\"09/27\"}"
#state actions {}
""",
        """v0_9.examples.23_step-counter""" to """#0 createSurface surface="gallery-step-counter"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-step-counter" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-step-counter" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-step-counter" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-step-counter" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "👨"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-step-counter" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Today's Steps"
#6 createComponent surface="gallery-step-counter" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-step-counter" tag=7 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "% of  goal"
#8 createComponent surface="gallery-step-counter" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#9 createComponent surface="gallery-step-counter" tag=9 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#10 createComponent surface="gallery-step-counter" tag=10 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#11 createComponent surface="gallery-step-counter" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = " mi"
#12 createComponent surface="gallery-step-counter" tag=12 parent=10 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Distance"
#13 createComponent surface="gallery-step-counter" tag=13 parent=9 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#14 createComponent surface="gallery-step-counter" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-step-counter" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Calories"
#16 updateData surface="gallery-step-counter"
  data:
    "calories" = 312
    "distance" = 3.8
    "goal" = 10000
    "progress" = 84
    "steps" = 8432
#17 updateComponent surface="gallery-step-counter" tag=6 view="KRRichTextView"
  props:
    "text" = "8,432"
#18 updateComponent surface="gallery-step-counter" tag=7 view="KRRichTextView"
  props:
    "text" = "84% of 10,000 goal"
#19 updateComponent surface="gallery-step-counter" tag=11 view="KRRichTextView"
  props:
    "text" = "3.8 mi"
#20 updateComponent surface="gallery-step-counter" tag=14 view="KRRichTextView"
  props:
    "text" = "312"
#state dataModel "{\"steps\":8432,\"goal\":10000,\"progress\":84,\"distance\":3.8,\"calories\":312}"
#state actions {}
""",
        """v0_9.examples.24_recipe-card""" to """#0 createSurface surface="gallery-recipe-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-recipe-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-recipe-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-recipe-card" tag=22 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="gallery-recipe-card" tag=23 parent=22 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs-container"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="gallery-recipe-card" tag=24 parent=23 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Overview"
#6 createComponent surface="gallery-recipe-card" tag=25 parent=23 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="gallery-recipe-card" tag=26 parent=22 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs-container"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="gallery-recipe-card" tag=27 parent=26 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Ingredients"
#9 createComponent surface="gallery-recipe-card" tag=28 parent=26 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="gallery-recipe-card" tag=29 parent=22 index=2 view="KRView"
  props:
    "__tabIndex" = 2
    "__tabsParentId" = "tabs-container"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#11 createComponent surface="gallery-recipe-card" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Instructions"
#12 createComponent surface="gallery-recipe-card" tag=31 parent=29 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#13 createComponent surface="gallery-recipe-card" tag=32 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#14 createComponent surface="gallery-recipe-card" tag=33 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#15 createComponent surface="gallery-recipe-card" tag=34 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#16 createComponent surface="gallery-recipe-card" tag=3 parent=32 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#17 createComponent surface="gallery-recipe-card" tag=4 parent=3 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#18 createComponent surface="gallery-recipe-card" tag=5 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#19 createComponent surface="gallery-recipe-card" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#20 createComponent surface="gallery-recipe-card" tag=7 parent=5 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#21 createComponent surface="gallery-recipe-card" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "★"
    "textAlign" = "center"
    "width" = 24
#22 createComponent surface="gallery-recipe-card" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#23 createComponent surface="gallery-recipe-card" tag=10 parent=7 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "( )"
#24 createComponent surface="gallery-recipe-card" tag=11 parent=5 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#25 createComponent surface="gallery-recipe-card" tag=12 parent=11 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#26 createComponent surface="gallery-recipe-card" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📅"
    "textAlign" = "center"
    "width" = 24
#27 createComponent surface="gallery-recipe-card" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#28 createComponent surface="gallery-recipe-card" tag=15 parent=11 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#29 createComponent surface="gallery-recipe-card" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⚠"
    "textAlign" = "center"
    "width" = 24
#30 createComponent surface="gallery-recipe-card" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#31 createComponent surface="gallery-recipe-card" tag=18 parent=5 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#32 createComponent surface="gallery-recipe-card" tag=19 parent=33 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#33 createComponent surface="gallery-recipe-card" tag=20 parent=34 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#34 updateData surface="gallery-recipe-card"
  data:
    "cookTime" = "20 min cook"
    "image" = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=180&fit=crop"
    "ingredients" = [{"text": "1 cup quinoa"}, {"text": "2 cups water"}, {"text": "1 cucumber, diced"}, {"text": "1 cup cherry tomatoes, halved"}]
    "instructions" = [{"text": "1. Rinse quinoa and bring to a boil in water."}, {"text": "2. Reduce heat and simmer for 15 minutes."}, {"text": "3. Fluff with a fork and let cool."}, {"text": "4. Mix with diced vegetables."}]
    "prepTime" = "15 min prep"
    "rating" = "4.9"
    "reviewCount" = 1247
    "servings" = "Serves 4"
    "title" = "Mediterranean Quinoa Bowl"
#35 createComponent surface="gallery-recipe-card" tag=35 parent=19 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1 cup quinoa"
#36 createComponent surface="gallery-recipe-card" tag=36 parent=19 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2 cups water"
#37 createComponent surface="gallery-recipe-card" tag=37 parent=19 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1 cucumber, diced"
#38 createComponent surface="gallery-recipe-card" tag=38 parent=19 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1 cup cherry tomatoes, halved"
#39 createComponent surface="gallery-recipe-card" tag=35 parent=20 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1. Rinse quinoa and bring to a boil in water."
#40 createComponent surface="gallery-recipe-card" tag=36 parent=20 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2. Reduce heat and simmer for 15 minutes."
#41 createComponent surface="gallery-recipe-card" tag=37 parent=20 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3. Fluff with a fork and let cool."
#42 createComponent surface="gallery-recipe-card" tag=38 parent=20 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "4. Mix with diced vegetables."
#43 updateComponent surface="gallery-recipe-card" tag=4 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=180&fit=crop"
#44 updateComponent surface="gallery-recipe-card" tag=6 view="KRRichTextView"
  props:
    "text" = "Mediterranean Quinoa Bowl"
#45 updateComponent surface="gallery-recipe-card" tag=9 view="KRRichTextView"
  props:
    "text" = "4.9"
#46 updateComponent surface="gallery-recipe-card" tag=10 view="KRRichTextView"
  props:
    "text" = "(1,247 reviews)"
#47 updateComponent surface="gallery-recipe-card" tag=14 view="KRRichTextView"
  props:
    "text" = "15 min prep"
#48 updateComponent surface="gallery-recipe-card" tag=17 view="KRRichTextView"
  props:
    "text" = "20 min cook"
#49 updateComponent surface="gallery-recipe-card" tag=18 view="KRRichTextView"
  props:
    "text" = "Serves 4"
#state dataModel "{\"image\":\"https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=180&fit=crop\",\"title\":\"Mediterranean Quinoa Bowl\",\"rating\":\"4.9\",\"reviewCount\":1247,\"prepTime\":\"15 min prep\",\"cookTime\":\"20 min cook\",\"servings\":\"Serves 4\",\"ingredients\":[{\"text\":\"1 cup quinoa\"},{\"text\":\"2 cups water\"},{\"text\":\"1 cucumber, diced\"},{\"text\":\"1 cup cherry tomatoes, halved\"}],\"instructions\":[{\"text\":\"1. Rinse quinoa and bring to a boil in water.\"},{\"text\":\"2. Reduce heat and simmer for 15 minutes.\"},{\"text\":\"3. Fluff with a fork and let cool.\"},{\"text\":\"4. Mix with diced vegetables.\"}]}"
#state actions {}
""",
        """v0_9.examples.25_contact-card""" to """#0 createSurface surface="gallery-contact-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-contact-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-contact-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-contact-card" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
    "width" = 40
#4 createComponent surface="gallery-contact-card" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="gallery-contact-card" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-contact-card" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="gallery-contact-card" tag=7 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#8 createComponent surface="gallery-contact-card" tag=8 parent=7 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#9 createComponent surface="gallery-contact-card" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📱"
    "textAlign" = "center"
    "width" = 24
#10 createComponent surface="gallery-contact-card" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-contact-card" tag=11 parent=7 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#12 createComponent surface="gallery-contact-card" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✉"
    "textAlign" = "center"
    "width" = 24
#13 createComponent surface="gallery-contact-card" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#14 createComponent surface="gallery-contact-card" tag=14 parent=7 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#15 createComponent surface="gallery-contact-card" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📍"
    "textAlign" = "center"
    "width" = 24
#16 createComponent surface="gallery-contact-card" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#17 createComponent surface="gallery-contact-card" tag=17 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#18 createComponent surface="gallery-contact-card" tag=19 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#19 createComponent surface="gallery-contact-card" tag=18 parent=19 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Call"
#20 createComponent surface="gallery-contact-card" tag=21 parent=17 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#21 createComponent surface="gallery-contact-card" tag=20 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Message"
#22 updateData surface="gallery-contact-card"
  data:
    "avatar" = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"
    "email" = "david.park@company.com"
    "location" = "San Francisco, CA"
    "name" = "David Park"
    "phone" = "+1 (555) 234-5678"
    "title" = "Engineering Manager"
#23 updateComponent surface="gallery-contact-card" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"
#24 updateComponent surface="gallery-contact-card" tag=4 view="KRRichTextView"
  props:
    "text" = "David Park"
#25 updateComponent surface="gallery-contact-card" tag=5 view="KRRichTextView"
  props:
    "text" = "Engineering Manager"
#26 updateComponent surface="gallery-contact-card" tag=10 view="KRRichTextView"
  props:
    "text" = "+1 (555) 234-5678"
#27 updateComponent surface="gallery-contact-card" tag=13 view="KRRichTextView"
  props:
    "text" = "david.park@company.com"
#28 updateComponent surface="gallery-contact-card" tag=16 view="KRRichTextView"
  props:
    "text" = "San Francisco, CA"
#state dataModel "{\"avatar\":\"https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop\",\"name\":\"David Park\",\"title\":\"Engineering Manager\",\"phone\":\"+1 (555) 234-5678\",\"email\":\"david.park@company.com\",\"location\":\"San Francisco, CA\"}"
#state actions {"19": "{\"event\":{\"name\":\"call\",\"context\":{}}}", "21": "{\"event\":{\"name\":\"message\",\"context\":{}}}"}
""",
        """v0_9.examples.26_podcast-episode""" to """#0 createSurface surface="gallery-podcast-episode"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-podcast-episode" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-podcast-episode" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="gallery-podcast-episode" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "height" = 100
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
    "width" = 100
#4 createComponent surface="gallery-podcast-episode" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#5 createComponent surface="gallery-podcast-episode" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-podcast-episode" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-podcast-episode" tag=7 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#8 createComponent surface="gallery-podcast-episode" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-podcast-episode" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-podcast-episode" tag=10 parent=4 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#11 createComponent surface="gallery-podcast-episode" tag=12 parent=4 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#12 createComponent surface="gallery-podcast-episode" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = ""
#13 createComponent surface="gallery-podcast-episode" tag=11 parent=12 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = ""
#14 updateData surface="gallery-podcast-episode"
  data:
    "artwork" = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=100&h=100&fit=crop"
    "audioUrl" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    "date" = "2024-12-15"
    "description" = "How AI is transforming the way we design and build products."
    "duration" = "45 min"
    "episodeTitle" = "The Future of AI in Product Design"
    "showName" = "Tech Talk Daily"
#15 updateComponent surface="gallery-podcast-episode" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=100&h=100&fit=crop"
#16 updateComponent surface="gallery-podcast-episode" tag=5 view="KRRichTextView"
  props:
    "text" = "Tech Talk Daily"
#17 updateComponent surface="gallery-podcast-episode" tag=6 view="KRRichTextView"
  props:
    "text" = "The Future of AI in Product Design"
#18 updateComponent surface="gallery-podcast-episode" tag=11 view="KRAudioView"
  props:
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#19 updateComponent surface="gallery-podcast-episode" tag=13 view="KRRichTextView"
  props:
    "text" = "The Future of AI in Product Design"
#20 updateComponent surface="gallery-podcast-episode" tag=8 view="KRRichTextView"
  props:
    "text" = "45 min"
#21 updateComponent surface="gallery-podcast-episode" tag=9 view="KRRichTextView"
  props:
    "text" = "Dec 15, 2024"
#22 updateComponent surface="gallery-podcast-episode" tag=10 view="KRRichTextView"
  props:
    "text" = "How AI is transforming the way we design and build products."
#state dataModel "{\"artwork\":\"https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=100&h=100&fit=crop\",\"showName\":\"Tech Talk Daily\",\"episodeTitle\":\"The Future of AI in Product Design\",\"duration\":\"45 min\",\"date\":\"2024-12-15\",\"description\":\"How AI is transforming the way we design and build products.\",\"audioUrl\":\"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3\"}"
#state actions {}
""",
        """v0_9.examples.27_stats-card""" to """#0 createSurface surface="gallery-stats-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-stats-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-stats-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-stats-card" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-stats-card" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="gallery-stats-card" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="gallery-stats-card" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-stats-card" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#8 createComponent surface="gallery-stats-card" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="gallery-stats-card" tag=9 parent=7 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "+% from last month"
#10 updateData surface="gallery-stats-card"
  data:
    "icon" = "trending_up"
    "metricName" = "Monthly Revenue"
    "trendIcon" = "arrow_upward"
    "trendPercent" = 12.5
    "value" = 48294
#11 updateComponent surface="gallery-stats-card" tag=4 view="KRRichTextView"
  props:
    "text" = "📈"
#12 updateComponent surface="gallery-stats-card" tag=5 view="KRRichTextView"
  props:
    "text" = "Monthly Revenue"
#13 updateComponent surface="gallery-stats-card" tag=6 view="KRRichTextView"
  props:
    "text" = "${'$'}48,294.00"
#14 updateComponent surface="gallery-stats-card" tag=8 view="KRRichTextView"
  props:
    "text" = "↑"
#15 updateComponent surface="gallery-stats-card" tag=9 view="KRRichTextView"
  props:
    "text" = "+12.5% from last month"
#state dataModel "{\"icon\":\"trending_up\",\"metricName\":\"Monthly Revenue\",\"value\":48294,\"trendIcon\":\"arrow_upward\",\"trendPercent\":12.5}"
#state actions {}
""",
        """v0_9.examples.28_countdown-timer""" to """#0 createSurface surface="gallery-countdown-timer"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-countdown-timer" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-countdown-timer" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-countdown-timer" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="gallery-countdown-timer" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#5 createComponent surface="gallery-countdown-timer" tag=5 parent=4 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#6 createComponent surface="gallery-countdown-timer" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-countdown-timer" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Days"
#8 createComponent surface="gallery-countdown-timer" tag=8 parent=4 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#9 createComponent surface="gallery-countdown-timer" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#10 createComponent surface="gallery-countdown-timer" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Hours"
#11 createComponent surface="gallery-countdown-timer" tag=11 parent=4 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="gallery-countdown-timer" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#13 createComponent surface="gallery-countdown-timer" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Minutes"
#14 createComponent surface="gallery-countdown-timer" tag=14 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#15 updateData surface="gallery-countdown-timer"
  data:
    "days" = "14"
    "eventName" = "Product Launch"
    "hours" = "08"
    "minutes" = "32"
    "targetDate" = "2025-01-15"
#16 updateComponent surface="gallery-countdown-timer" tag=3 view="KRRichTextView"
  props:
    "text" = "Product Launch"
#17 updateComponent surface="gallery-countdown-timer" tag=6 view="KRRichTextView"
  props:
    "text" = "14"
#18 updateComponent surface="gallery-countdown-timer" tag=9 view="KRRichTextView"
  props:
    "text" = "08"
#19 updateComponent surface="gallery-countdown-timer" tag=12 view="KRRichTextView"
  props:
    "text" = "32"
#20 updateComponent surface="gallery-countdown-timer" tag=14 view="KRRichTextView"
  props:
    "text" = "January 15, 2025"
#state dataModel "{\"eventName\":\"Product Launch\",\"days\":\"14\",\"hours\":\"08\",\"minutes\":\"32\",\"targetDate\":\"2025-01-15\"}"
#state actions {}
""",
        """v0_9.examples.29_movie-card""" to """#0 createSurface surface="gallery-movie-card"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-movie-card" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-movie-card" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-movie-card" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#4 createComponent surface="gallery-movie-card" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="gallery-movie-card" tag=5 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#6 createComponent surface="gallery-movie-card" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 createComponent surface="gallery-movie-card" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-movie-card" tag=8 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="gallery-movie-card" tag=9 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="gallery-movie-card" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "★"
    "textAlign" = "center"
    "width" = 24
#11 createComponent surface="gallery-movie-card" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#12 createComponent surface="gallery-movie-card" tag=12 parent=4 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#13 createComponent surface="gallery-movie-card" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📅"
    "textAlign" = "center"
    "width" = 24
#14 createComponent surface="gallery-movie-card" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#15 createComponent surface="gallery-movie-card" tag=16 parent=4 index=4 view="KRView"
  props:
    "__modalTriggerId" = "trailer-modal"
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#16 createComponent surface="gallery-movie-card" tag=15 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Watch Trailer"
#17 updateData surface="gallery-movie-card"
  data:
    "genre" = "Sci-Fi • Adventure • Drama"
    "poster" = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=200&h=300&fit=crop"
    "rating" = "8.7/10"
    "runtime" = "2h 49min"
    "title" = "Interstellar"
    "trailerUrl" = "https://www.w3schools.com/html/mov_bbb.mp4"
    "year" = "(2014)"
#18 updateComponent surface="gallery-movie-card" tag=3 view="KRImageView"
  props:
    "src" = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=200&h=300&fit=crop"
#19 updateComponent surface="gallery-movie-card" tag=6 view="KRRichTextView"
  props:
    "text" = "Interstellar"
#20 updateComponent surface="gallery-movie-card" tag=7 view="KRRichTextView"
  props:
    "text" = "(2014)"
#21 updateComponent surface="gallery-movie-card" tag=8 view="KRRichTextView"
  props:
    "text" = "Sci-Fi • Adventure • Drama"
#22 updateComponent surface="gallery-movie-card" tag=11 view="KRRichTextView"
  props:
    "text" = "8.7/10"
#23 updateComponent surface="gallery-movie-card" tag=14 view="KRRichTextView"
  props:
    "text" = "2h 49min"
#state dataModel "{\"poster\":\"https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=200&h=300&fit=crop\",\"title\":\"Interstellar\",\"year\":\"(2014)\",\"genre\":\"Sci-Fi • Adventure • Drama\",\"rating\":\"8.7/10\",\"runtime\":\"2h 49min\",\"trailerUrl\":\"https://www.w3schools.com/html/mov_bbb.mp4\"}"
#state actions {"16": "{\"event\":{\"name\":\"open_trailer\"}}"}
""",
        """v0_9.examples.30_live-invitation-builder""" to """#0 createSurface surface="gallery-invitation-builder"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-invitation-builder" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#2 createComponent surface="gallery-invitation-builder" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 40.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "# Invitation Builder"
#3 createComponent surface="gallery-invitation-builder" tag=3 parent=1 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-invitation-builder" tag=4 parent=3 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#5 createComponent surface="gallery-invitation-builder" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Customize your invitation"
#6 createComponent surface="gallery-invitation-builder" tag=21 parent=4 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="gallery-invitation-builder" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Event Name"
#8 createComponent surface="gallery-invitation-builder" tag=6 parent=21 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "event/name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="gallery-invitation-builder" tag=23 parent=4 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="gallery-invitation-builder" tag=24 parent=23 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Guest of Honor"
#11 createComponent surface="gallery-invitation-builder" tag=7 parent=23 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "event/guest"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#12 createComponent surface="gallery-invitation-builder" tag=25 parent=4 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="gallery-invitation-builder" tag=26 parent=25 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Event Date & Time"
#14 createComponent surface="gallery-invitation-builder" tag=8 parent=25 index=1 view="KRDateTimeInputView"
  props:
    "__dateTimeBindingPath" = "event/date"
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Event Date & Time"
    "value" = ""
#15 createComponent surface="gallery-invitation-builder" tag=27 parent=4 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#16 createComponent surface="gallery-invitation-builder" tag=28 parent=27 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Location"
#17 createComponent surface="gallery-invitation-builder" tag=9 parent=27 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Location"
    "options" = [{"label": "Grand Ballroom", "value": "ballroom"}, {"label": "Sunset Terrace", "value": "terrace"}, {"label": "Garden Pavillion", "value": "garden"}]
    "value" = ""
    "variant" = "mutuallyExclusive"
#18 createComponent surface="gallery-invitation-builder" tag=29 parent=9 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "ballroom"
    "__choicePickerParentId" = "location-picker"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#19 createComponent surface="gallery-invitation-builder" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Grand Ballroom"
#20 createComponent surface="gallery-invitation-builder" tag=31 parent=9 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "terrace"
    "__choicePickerParentId" = "location-picker"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#21 createComponent surface="gallery-invitation-builder" tag=32 parent=31 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Sunset Terrace"
#22 createComponent surface="gallery-invitation-builder" tag=33 parent=9 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "garden"
    "__choicePickerParentId" = "location-picker"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#23 createComponent surface="gallery-invitation-builder" tag=34 parent=33 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Garden Pavillion"
#24 createComponent surface="gallery-invitation-builder" tag=10 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#25 createComponent surface="gallery-invitation-builder" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Live Preview"
#26 createComponent surface="gallery-invitation-builder" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#27 createComponent surface="gallery-invitation-builder" tag=13 parent=12 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#28 createComponent surface="gallery-invitation-builder" tag=14 parent=13 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=400&h=200&fit=crop"
#29 createComponent surface="gallery-invitation-builder" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#30 createComponent surface="gallery-invitation-builder" tag=16 parent=13 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#31 createComponent surface="gallery-invitation-builder" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Celebrating"
#32 createComponent surface="gallery-invitation-builder" tag=18 parent=16 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#33 createComponent surface="gallery-invitation-builder" tag=19 parent=13 index=3 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#34 createComponent surface="gallery-invitation-builder" tag=20 parent=13 index=4 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Location: "
#35 updateData surface="gallery-invitation-builder"
  data:
    "event" = {"date": "2025-07-15T19:00:00Z", "guest": "Alex Johnson", "location": ["terrace"], "name": "Summer Gala"}
#36 updateComponent surface="gallery-invitation-builder" tag=19 view="KRRichTextView"
  props:
    "text" = "Tuesday, July 15, 2025 at 7:00 PM"
#37 updateComponent surface="gallery-invitation-builder" tag=20 view="KRRichTextView"
  props:
    "text" = "Location: terrace"
#38 updateComponent surface="gallery-invitation-builder" tag=6 view="KRTextFieldView"
  props:
    "text" = "Summer Gala"
#39 updateComponent surface="gallery-invitation-builder" tag=22 view="KRRichTextView"
  props:
    "text" = "Event Name"
#40 updateComponent surface="gallery-invitation-builder" tag=7 view="KRTextFieldView"
  props:
    "text" = "Alex Johnson"
#41 updateComponent surface="gallery-invitation-builder" tag=24 view="KRRichTextView"
  props:
    "text" = "Guest of Honor"
#42 updateComponent surface="gallery-invitation-builder" tag=8 view="KRDateTimeInputView"
  props:
    "value" = "2025-07-15T19:00:00Z"
#43 updateComponent surface="gallery-invitation-builder" tag=26 view="KRRichTextView"
  props:
    "text" = "Event Date & Time"
#44 updateComponent surface="gallery-invitation-builder" tag=29 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#45 updateComponent surface="gallery-invitation-builder" tag=30 view="KRRichTextView"
  props:
    "color" = "#333333"
#46 updateComponent surface="gallery-invitation-builder" tag=31 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#47 updateComponent surface="gallery-invitation-builder" tag=32 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#48 updateComponent surface="gallery-invitation-builder" tag=33 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#49 updateComponent surface="gallery-invitation-builder" tag=34 view="KRRichTextView"
  props:
    "color" = "#333333"
#50 updateComponent surface="gallery-invitation-builder" tag=15 view="KRRichTextView"
  props:
    "text" = "Summer Gala"
#51 updateComponent surface="gallery-invitation-builder" tag=18 view="KRRichTextView"
  props:
    "text" = "Alex Johnson"
#state dataModel "{\"event\":{\"name\":\"Summer Gala\",\"guest\":\"Alex Johnson\",\"date\":\"2025-07-15T19:00:00Z\",\"location\":[\"terrace\"]}}"
#state actions {}
""",
        """v0_9.examples.31_incremental-dashboard""" to """#0 createSurface surface="gallery-incremental-dashboard"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-incremental-dashboard" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#2 createComponent surface="gallery-incremental-dashboard" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "System Dashboard"
#3 createComponent surface="gallery-incremental-dashboard" tag=3 parent=1 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-incremental-dashboard" tag=4 parent=3 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#5 createComponent surface="gallery-incremental-dashboard" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Loading analytics..."
#6 createComponent surface="gallery-incremental-dashboard" tag=5 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#7 createComponent surface="gallery-incremental-dashboard" tag=7 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Loading logs..."
#8 removeComponent surface="gallery-incremental-dashboard" tag=6
#9 updateComponent surface="gallery-incremental-dashboard" tag=4 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#10 createComponent surface="gallery-incremental-dashboard" tag=8 parent=4 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#11 createComponent surface="gallery-incremental-dashboard" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Analytics are ready."
#12 removeComponent surface="gallery-incremental-dashboard" tag=7
#13 updateComponent surface="gallery-incremental-dashboard" tag=5 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "weight" = 1
#14 createComponent surface="gallery-incremental-dashboard" tag=10 parent=5 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#15 updateData surface="gallery-incremental-dashboard"
  data:
    "logs" = [{"message": "System boot complete."}, {"message": "All services healthy."}, {"message": "Waiting for user input."}]
#16 createComponent surface="gallery-incremental-dashboard" tag=12 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "System boot complete."
#17 createComponent surface="gallery-incremental-dashboard" tag=13 parent=10 index=1 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "All services healthy."
#18 createComponent surface="gallery-incremental-dashboard" tag=14 parent=10 index=2 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Waiting for user input."
#state dataModel "{\"logs\":[{\"message\":\"System boot complete.\"},{\"message\":\"All services healthy.\"},{\"message\":\"Waiting for user input.\"}]}"
#state actions {}
""",
        """v0_9.examples.32_advanced-form-validator""" to """#0 createSurface surface="gallery-advanced-validator"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-advanced-validator" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-advanced-validator" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-advanced-validator" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Hello! Today is ."
#4 createComponent surface="gallery-advanced-validator" tag=10 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="gallery-advanced-validator" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email Address"
#6 createComponent surface="gallery-advanced-validator" tag=4 parent=10 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "formData/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="gallery-advanced-validator" tag=12 parent=10 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#8 createComponent surface="gallery-advanced-validator" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#9 createComponent surface="gallery-advanced-validator" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Phone Number"
#10 createComponent surface="gallery-advanced-validator" tag=5 parent=13 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "formData/phone"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#11 createComponent surface="gallery-advanced-validator" tag=15 parent=13 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Invalid phone format"
#12 createComponent surface="gallery-advanced-validator" tag=16 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="gallery-advanced-validator" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Zip Code"
#14 createComponent surface="gallery-advanced-validator" tag=6 parent=16 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "formData/zip"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#15 createComponent surface="gallery-advanced-validator" tag=18 parent=16 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Must be exactly 5 digits"
#16 createComponent surface="gallery-advanced-validator" tag=19 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#17 createComponent surface="gallery-advanced-validator" tag=7 parent=19 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "formData/agree"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#18 createComponent surface="gallery-advanced-validator" tag=20 parent=19 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "I agree to the terms and conditions"
#19 createComponent surface="gallery-advanced-validator" tag=21 parent=2 index=5 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#20 createComponent surface="gallery-advanced-validator" tag=9 parent=21 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#21 createComponent surface="gallery-advanced-validator" tag=22 parent=21 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "You must agree to terms AND provide either Email or Phone, plus a Zip code."
#22 createComponent surface="gallery-advanced-validator" tag=8 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Submit Registration"
#23 updateData surface="gallery-advanced-validator"
  data:
    "formData" = {"agree": false, "email": "", "phone": "", "zip": ""}
    "now" = "2025-12-15T12:00:00Z"
#24 updateComponent surface="gallery-advanced-validator" tag=3 view="KRRichTextView"
  props:
    "text" = "Hello! Today is Monday, December 15."
#25 updateComponent surface="gallery-advanced-validator" tag=4 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#26 updateComponent surface="gallery-advanced-validator" tag=11 view="KRRichTextView"
  props:
    "text" = "Email Address"
#27 updateComponent surface="gallery-advanced-validator" tag=5 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#28 updateComponent surface="gallery-advanced-validator" tag=14 view="KRRichTextView"
  props:
    "text" = "Phone Number"
#29 updateComponent surface="gallery-advanced-validator" tag=6 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#30 updateComponent surface="gallery-advanced-validator" tag=17 view="KRRichTextView"
  props:
    "text" = "Zip Code"
#31 updateComponent surface="gallery-advanced-validator" tag=7 view="KRCheckBoxView"
  props:
    "checked" = false
#32 updateComponent surface="gallery-advanced-validator" tag=12 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#33 updateComponent surface="gallery-advanced-validator" tag=15 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#34 updateComponent surface="gallery-advanced-validator" tag=18 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#state dataModel "{\"now\":\"2025-12-15T12:00:00Z\",\"formData\":{\"email\":\"\",\"phone\":\"\",\"zip\":\"\",\"agree\":false}}"
#state actions {"9": null}
""",
        """v0_9.examples.33_financial-data-grid""" to """#0 createSurface surface="gallery-financial-grid"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-financial-grid" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-financial-grid" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-financial-grid" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-financial-grid" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "flex" = 2
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Asset"
    "weight" = 2
#5 createComponent surface="gallery-financial-grid" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "flex" = 1
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Price"
    "weight" = 1
#6 createComponent surface="gallery-financial-grid" tag=6 parent=3 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "flex" = 1
    "fontSize" = 13.0
    "margin" = 8
    "text" = "24h Change"
    "weight" = 1
#7 createComponent surface="gallery-financial-grid" tag=7 parent=3 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "flex" = 1.5
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Market Cap"
    "weight" = 1.5
#8 createComponent surface="gallery-financial-grid" tag=8 parent=2 index=1 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#9 updateData surface="gallery-financial-grid"
  data:
    "assets" = [{"change": 1.2, "marketCap": 850000000000, "name": "Bitcoin", "price": 43500.25, "symbol": "BTC"}, {"change": -0.5, "marketCap": 270000000000, "name": "Ethereum", "price": 2250.5, "symbol": "ETH"}, {"change": 5.4, "marketCap": 40000000000, "name": "Solana", "price": 95.8, "symbol": "SOL"}]
#10 createComponent surface="gallery-financial-grid" tag=18 parent=8 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#11 createComponent surface="gallery-financial-grid" tag=19 parent=18 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 2
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "weight" = 2
#12 createComponent surface="gallery-financial-grid" tag=20 parent=19 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "💳"
    "textAlign" = "center"
    "width" = 24
#13 createComponent surface="gallery-financial-grid" tag=21 parent=19 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#14 createComponent surface="gallery-financial-grid" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Bitcoin"
#15 createComponent surface="gallery-financial-grid" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "BTC"
#16 createComponent surface="gallery-financial-grid" tag=24 parent=18 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}43,500.25"
    "weight" = 1
#17 createComponent surface="gallery-financial-grid" tag=25 parent=18 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1.2%"
    "weight" = 1
#18 createComponent surface="gallery-financial-grid" tag=26 parent=18 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1.5
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}850,000,000,000.00"
    "weight" = 1.5
#19 createComponent surface="gallery-financial-grid" tag=27 parent=8 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#20 createComponent surface="gallery-financial-grid" tag=28 parent=27 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 2
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "weight" = 2
#21 createComponent surface="gallery-financial-grid" tag=29 parent=28 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "💳"
    "textAlign" = "center"
    "width" = 24
#22 createComponent surface="gallery-financial-grid" tag=30 parent=28 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#23 createComponent surface="gallery-financial-grid" tag=31 parent=30 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Ethereum"
#24 createComponent surface="gallery-financial-grid" tag=32 parent=30 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "ETH"
#25 createComponent surface="gallery-financial-grid" tag=33 parent=27 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}2,250.50"
    "weight" = 1
#26 createComponent surface="gallery-financial-grid" tag=34 parent=27 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "-0.5%"
    "weight" = 1
#27 createComponent surface="gallery-financial-grid" tag=35 parent=27 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1.5
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}270,000,000,000.00"
    "weight" = 1.5
#28 createComponent surface="gallery-financial-grid" tag=36 parent=8 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#29 createComponent surface="gallery-financial-grid" tag=37 parent=36 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flex" = 2
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "weight" = 2
#30 createComponent surface="gallery-financial-grid" tag=38 parent=37 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "💳"
    "textAlign" = "center"
    "width" = 24
#31 createComponent surface="gallery-financial-grid" tag=39 parent=37 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#32 createComponent surface="gallery-financial-grid" tag=40 parent=39 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Solana"
#33 createComponent surface="gallery-financial-grid" tag=41 parent=39 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "SOL"
#34 createComponent surface="gallery-financial-grid" tag=42 parent=36 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}95.80"
    "weight" = 1
#35 createComponent surface="gallery-financial-grid" tag=43 parent=36 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1
    "fontSize" = 16.0
    "margin" = 8
    "text" = "5.4%"
    "weight" = 1
#36 createComponent surface="gallery-financial-grid" tag=44 parent=36 index=3 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "flex" = 1.5
    "fontSize" = 16.0
    "margin" = 8
    "text" = "${'$'}40,000,000,000.00"
    "weight" = 1.5
#state dataModel "{\"assets\":[{\"name\":\"Bitcoin\",\"symbol\":\"BTC\",\"price\":43500.25,\"change\":1.2,\"marketCap\":850000000000},{\"name\":\"Ethereum\",\"symbol\":\"ETH\",\"price\":2250.5,\"change\":-0.5,\"marketCap\":270000000000},{\"name\":\"Solana\",\"symbol\":\"SOL\",\"price\":95.8,\"change\":5.4,\"marketCap\":40000000000}]}"
#state actions {}
""",
        """v0_9.examples.34_child-list-template""" to """#0 createSurface surface="gallery-child-list-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-child-list-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-child-list-template" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-child-list-template" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Dynamic Item List"
#4 createComponent surface="gallery-child-list-template" tag=4 parent=2 index=1 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="gallery-child-list-template"
  data:
    "items" = [{"name": "Apple", "quantity": 10}, {"name": "Banana", "quantity": 5}, {"name": "Cherry", "quantity": 20}]
#6 createComponent surface="gallery-child-list-template" tag=9 parent=4 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#7 createComponent surface="gallery-child-list-template" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Apple"
#8 createComponent surface="gallery-child-list-template" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = " - Qty: "
#9 createComponent surface="gallery-child-list-template" tag=12 parent=9 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 10
#10 createComponent surface="gallery-child-list-template" tag=13 parent=4 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#11 createComponent surface="gallery-child-list-template" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Banana"
#12 createComponent surface="gallery-child-list-template" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = " - Qty: "
#13 createComponent surface="gallery-child-list-template" tag=16 parent=13 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 5
#14 createComponent surface="gallery-child-list-template" tag=17 parent=4 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#15 createComponent surface="gallery-child-list-template" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Cherry"
#16 createComponent surface="gallery-child-list-template" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = " - Qty: "
#17 createComponent surface="gallery-child-list-template" tag=20 parent=17 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 20
#state dataModel "{\"items\":[{\"name\":\"Apple\",\"quantity\":10},{\"name\":\"Banana\",\"quantity\":5},{\"name\":\"Cherry\",\"quantity\":20}]}"
#state actions {}
""",
        """v0_9.examples.35_markdown-text""" to """#0 createSurface surface="gallery-markdown-text"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="gallery-markdown-text" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-markdown-text" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-markdown-text" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Markdown Rendering"
#4 createComponent surface="gallery-markdown-text" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "# Heading 1\n\nThis is **bold** text and *italic* text.\n\n- List item 1\n- List item 2\n\n[Link to Google](https://google.com)"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.examples.36_modal""" to """#0 createSurface surface="modal-sample-surface"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="modal-sample-surface" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#2 createComponent surface="modal-sample-surface" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Modal Component Sample"
#3 createComponent surface="modal-sample-surface" tag=5 parent=1 index=1 view="KRView"
  props:
    "__modalTriggerId" = "modal-comp"
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#4 createComponent surface="modal-sample-surface" tag=4 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Open Modal"
#state dataModel "{}"
#state actions {"5": "{\"event\":{\"name\":\"openModalEvent\",\"context\":{}}}"}
""",
        """v0_9.extension.03_number_return.1_custom_calculate_discount""" to """#0 createSurface surface="cf-num-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="cf-num-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-num-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-num-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "calculateDiscount(price, rate) → Number"
#4 createComponent surface="cf-num-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="cf-num-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "折后价: ¥"
#6 createComponent surface="cf-num-1" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 28.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#7 updateData surface="cf-num-1"
  data:
    "discountRate" = 0.7
    "originalPrice" = 199
#8 updateComponent surface="cf-num-1" tag=6 view="KRRichTextView"
  props:
    "text" = ""
#state dataModel "{\"originalPrice\":199,\"discountRate\":0.7}"
#state actions {}
""",
        """v0_9.function.and.1_and_two_required""" to """#0 createSurface surface="fn-and-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-and-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-and-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-and-1" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-and-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "姓"
#5 createComponent surface="fn-and-1" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/last"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-and-1" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-and-1" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "名"
#8 createComponent surface="fn-and-1" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/first"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-and-1" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-and-1" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#11 createComponent surface="fn-and-1" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "姓和名都不能为空"
#12 createComponent surface="fn-and-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "登记"
#13 updateData surface="fn-and-1"
  data:
    "f" = {"first": "", "last": ""}
#14 updateComponent surface="fn-and-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-and-1" tag=8 view="KRRichTextView"
  props:
    "text" = "姓"
#16 updateComponent surface="fn-and-1" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#17 updateComponent surface="fn-and-1" tag=10 view="KRRichTextView"
  props:
    "text" = "名"
#state dataModel "{\"f\":{\"last\":\"\",\"first\":\"\"}}"
#state actions {"5": null}
""",
        """v0_9.function.and.2_and_three_values""" to """#0 createSurface surface="fn-and-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-and-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-and-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-and-2" tag=8 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="fn-and-2" tag=3 parent=8 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "agree/tos"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="fn-and-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意用户协议"
#6 createComponent surface="fn-and-2" tag=10 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#7 createComponent surface="fn-and-2" tag=4 parent=10 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "agree/privacy"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#8 createComponent surface="fn-and-2" tag=11 parent=10 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意隐私政策"
#9 createComponent surface="fn-and-2" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#10 createComponent surface="fn-and-2" tag=5 parent=12 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "agree/marketing"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#11 createComponent surface="fn-and-2" tag=13 parent=12 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "同意接收邮件"
#12 createComponent surface="fn-and-2" tag=14 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="fn-and-2" tag=6 parent=14 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#14 createComponent surface="fn-and-2" tag=15 parent=14 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "必须全部勾选"
#15 createComponent surface="fn-and-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "开始使用"
#16 updateData surface="fn-and-2"
  data:
    "agree" = {"marketing": false, "privacy": false, "tos": false}
#17 updateComponent surface="fn-and-2" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#18 updateComponent surface="fn-and-2" tag=4 view="KRCheckBoxView"
  props:
    "checked" = false
#19 updateComponent surface="fn-and-2" tag=5 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"agree\":{\"tos\":false,\"privacy\":false,\"marketing\":false}}"
#state actions {"6": null}
""",
        """v0_9.function.and.3_and_nested_email_length""" to """#0 createSurface surface="fn-and-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-and-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-and-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-and-3" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-and-3" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-and-3" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-and-3" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-and-3" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-and-3" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "邮箱格式必须正确且不超过 30 字符"
#9 createComponent surface="fn-and-3" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "订阅"
#10 updateData surface="fn-and-3"
  data:
    "f" = {"email": ""}
#11 updateComponent surface="fn-and-3" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-and-3" tag=7 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"f\":{\"email\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.and.4_and_or_literal_bool_values""" to """#0 createSurface surface="fn-and-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-and-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-and-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-and-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "字面量 boolean 组合"
#4 createComponent surface="fn-and-4" tag=12 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-and-4" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱（前两个按钮的启用条件之一）"
#6 createComponent surface="fn-and-4" tag=4 parent=12 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-and-4" tag=14 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="fn-and-4" tag=5 parent=14 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#9 createComponent surface="fn-and-4" tag=15 parent=14 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#10 createComponent surface="fn-and-4" tag=9 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "and(true, email(...))"
#11 createComponent surface="fn-and-4" tag=16 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#12 createComponent surface="fn-and-4" tag=6 parent=16 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#13 createComponent surface="fn-and-4" tag=17 parent=16 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "and 中 literal false 使整体永远为 false（feature flag 关闭）"
#14 createComponent surface="fn-and-4" tag=10 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "and(false, email(...))  → 永远禁用"
#15 createComponent surface="fn-and-4" tag=18 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#16 createComponent surface="fn-and-4" tag=7 parent=18 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#17 createComponent surface="fn-and-4" tag=19 parent=18 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#18 createComponent surface="fn-and-4" tag=11 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "or(false, not(false))  → 永远启用"
#19 createComponent surface="fn-and-4" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "预期：\n · 按钮1：邮箱合法时启用（and 中 true 是恒真项）\n · 按钮2：无论邮箱如何都禁用（and 中 false 使整体为 false）\n · 按钮3：始终启用（or 中 not(false)=true）"
#20 updateData surface="fn-and-4"
  data:
    "email" = ""
#21 updateComponent surface="fn-and-4" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#22 updateComponent surface="fn-and-4" tag=13 view="KRRichTextView"
  props:
    "text" = "邮箱（前两个按钮的启用条件之一）"
#23 updateComponent surface="fn-and-4" tag=15 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#24 updateComponent surface="fn-and-4" tag=5 view="KRView"
  props:
    "opacity" = 1.0
    "touchEnable" = 1
#25 updateComponent surface="fn-and-4" tag=17 view="KRRichTextView"
  props:
    "height" = NaN
    "opacity" = 1.0
    "text" = "and 中 literal false 使整体永远为 false（feature flag 关闭）"
#26 updateComponent surface="fn-and-4" tag=6 view="KRView"
  props:
    "opacity" = 0.4
    "touchEnable" = 0
#state dataModel "{\"email\":\"\"}"
#state actions {"5": "{\"event\":{\"name\":\"submit1\"}}", "6": null, "7": "{\"event\":{\"name\":\"submit3\"}}"}
""",
        """v0_9.function.and.5_and_of_two_ors""" to """#0 createSurface surface="fn-and-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-and-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-and-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-and-5" tag=9 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-and-5" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-and-5" tag=3 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-and-5" tag=11 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-and-5" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "手机号"
#8 createComponent surface="fn-and-5" tag=4 parent=11 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/phone"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-and-5" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-and-5" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "短信验证码"
#11 createComponent surface="fn-and-5" tag=5 parent=13 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/smsCode"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#12 createComponent surface="fn-and-5" tag=15 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="fn-and-5" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱验证码"
#14 createComponent surface="fn-and-5" tag=6 parent=15 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/emailCode"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#15 createComponent surface="fn-and-5" tag=17 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#16 createComponent surface="fn-and-5" tag=7 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#17 createComponent surface="fn-and-5" tag=18 parent=17 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "身份与验证码各需填一项"
#18 createComponent surface="fn-and-5" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "登录"
#19 updateData surface="fn-and-5"
  data:
    "f" = {"email": "", "emailCode": "", "phone": "", "smsCode": ""}
#20 updateComponent surface="fn-and-5" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#21 updateComponent surface="fn-and-5" tag=10 view="KRRichTextView"
  props:
    "text" = "邮箱"
#22 updateComponent surface="fn-and-5" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#23 updateComponent surface="fn-and-5" tag=12 view="KRRichTextView"
  props:
    "text" = "手机号"
#24 updateComponent surface="fn-and-5" tag=5 view="KRTextFieldView"
  props:
    "text" = ""
#25 updateComponent surface="fn-and-5" tag=14 view="KRRichTextView"
  props:
    "text" = "短信验证码"
#26 updateComponent surface="fn-and-5" tag=6 view="KRTextFieldView"
  props:
    "text" = ""
#27 updateComponent surface="fn-and-5" tag=16 view="KRRichTextView"
  props:
    "text" = "邮箱验证码"
#state dataModel "{\"f\":{\"email\":\"\",\"phone\":\"\",\"smsCode\":\"\",\"emailCode\":\"\"}}"
#state actions {"7": null}
""",
        """v0_9.function.email.1_email_basic""" to """#0 createSurface surface="fn-email-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-email-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-email-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-email-1" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-email-1" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-email-1" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-email-1" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-email-1" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="fn-email-1" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#9 createComponent surface="fn-email-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发送"
#10 updateData surface="fn-email-1"
  data:
    "form" = {"email": ""}
#11 updateComponent surface="fn-email-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-email-1" tag=7 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"form\":{\"email\":\"\"}}"
#state actions {"4": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.email.2_email_with_required""" to """#0 createSurface surface="fn-email-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-email-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-email-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-email-2" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-email-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-email-2" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-email-2" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-email-2" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-email-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "邮箱不能为空"
#9 createComponent surface="fn-email-2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "订阅"
#10 updateData surface="fn-email-2"
  data:
    "form" = {"email": ""}
#11 updateComponent surface="fn-email-2" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-email-2" tag=7 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"form\":{\"email\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.email.3_email_prefilled_valid""" to """#0 createSurface surface="fn-email-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-email-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-email-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-email-3" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-email-3" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-email-3" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-email-3" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-email-3" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="fn-email-3" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#9 createComponent surface="fn-email-3" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "订阅"
#10 updateData surface="fn-email-3"
  data:
    "form" = {"email": "alex@example.com"}
#11 updateComponent surface="fn-email-3" tag=3 view="KRTextFieldView"
  props:
    "text" = "alex@example.com"
#12 updateComponent surface="fn-email-3" tag=7 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"form\":{\"email\":\"alex@example.com\"}}"
#state actions {"4": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.formatCurrency.1_formatCurrency_usd""" to """#0 createSurface surface="fn-fc-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fc-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fc-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fc-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fc-1"
  data:
    "price" = 1299.5
#5 updateComponent surface="fn-fc-1" tag=3 view="KRRichTextView"
  props:
    "text" = "${'$'}1,299.50"
#state dataModel "{\"price\":1299.5}"
#state actions {}
""",
        """v0_9.function.formatCurrency.2_formatCurrency_eur_and_jpy""" to """#0 createSurface surface="fn-fc-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fc-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fc-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fc-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="fn-fc-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="fn-fc-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#6 updateData surface="fn-fc-2"
  data:
    "price" = 1299.5
#7 updateComponent surface="fn-fc-2" tag=3 view="KRRichTextView"
  props:
    "text" = "${'$'}1,299.50"
#8 updateComponent surface="fn-fc-2" tag=4 view="KRRichTextView"
  props:
    "text" = "€1,299.50"
#9 updateComponent surface="fn-fc-2" tag=5 view="KRRichTextView"
  props:
    "text" = "¥1,299.50"
#state dataModel "{\"price\":1299.5}"
#state actions {}
""",
        """v0_9.function.formatCurrency.3_formatCurrency_cny_decimals""" to """#0 createSurface surface="fn-fc-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fc-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fc-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fc-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fc-3"
  data:
    "price" = 68888
#5 updateComponent surface="fn-fc-3" tag=3 view="KRRichTextView"
  props:
    "text" = "¥68,888.00"
#state dataModel "{\"price\":68888}"
#state actions {}
""",
        """v0_9.function.formatCurrency.4_formatCurrency_no_grouping""" to """#0 createSurface surface="fn-fc-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fc-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fc-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fc-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fc-4"
  data:
    "price" = 1234567.89
#5 updateComponent surface="fn-fc-4" tag=3 view="KRRichTextView"
  props:
    "text" = "${'$'}1,234,567.89"
#state dataModel "{\"price\":1234567.89}"
#state actions {}
""",
        """v0_9.function.formatCurrency.5_formatCurrency_dynamic_currency""" to """#0 createSurface surface="fn-fc-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fc-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fc-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fc-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "动态币种：修改下方 ISO 4217 代码看金额变化"
#4 createComponent surface="fn-fc-5" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-fc-5" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "币种代码（USD / EUR / JPY / CNY / GBP …）"
#6 createComponent surface="fn-fc-5" tag=4 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "currency"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-fc-5" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#8 createComponent surface="fn-fc-5" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "price = 12345.678，decimals = 2。切换 currency：\n · USD → ${'$'}12,345.68\n · EUR → €12,345.68 (locale 相关)\n · JPY → ¥12,346（日元通常无小数）\n · CNY → CN¥12,345.68 或 ¥12,345.68"
#9 updateData surface="fn-fc-5"
  data:
    "currency" = "USD"
    "decimals" = 2
    "price" = 12345.678
#10 updateComponent surface="fn-fc-5" tag=5 view="KRRichTextView"
  props:
    "text" = "${'$'}12,345.68"
#11 updateComponent surface="fn-fc-5" tag=4 view="KRTextFieldView"
  props:
    "text" = "USD"
#12 updateComponent surface="fn-fc-5" tag=8 view="KRRichTextView"
  props:
    "text" = "币种代码（USD / EUR / JPY / CNY / GBP …）"
#state dataModel "{\"price\":12345.678,\"currency\":\"USD\",\"decimals\":2}"
#state actions {}
""",
        """v0_9.function.formatDate.1_formatDate_long_month_day_year""" to """#0 createSurface surface="fn-fd-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fd-1"
  data:
    "at" = "2025-12-15T17:30:00Z"
#5 updateComponent surface="fn-fd-1" tag=3 view="KRRichTextView"
  props:
    "text" = "Dec 15, 2025"
#state dataModel "{\"at\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatDate.2_formatDate_iso_dt""" to """#0 createSurface surface="fn-fd-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fd-2"
  data:
    "at" = "2025-12-15T17:30:00Z"
#5 updateComponent surface="fn-fd-2" tag=3 view="KRRichTextView"
  props:
    "text" = "2025-12-15 17:30"
#state dataModel "{\"at\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatDate.3_formatDate_weekday_variants""" to """#0 createSurface surface="fn-fd-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="fn-fd-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 updateData surface="fn-fd-3"
  data:
    "at" = "2025-12-15T17:30:00Z"
#6 updateComponent surface="fn-fd-3" tag=3 view="KRRichTextView"
  props:
    "text" = "Mon"
#7 updateComponent surface="fn-fd-3" tag=4 view="KRRichTextView"
  props:
    "text" = "Monday, 15 December"
#state dataModel "{\"at\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatDate.4_formatDate_am_pm""" to """#0 createSurface surface="fn-fd-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 createComponent surface="fn-fd-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 updateData surface="fn-fd-4"
  data:
    "at" = "2025-12-15T17:30:00Z"
#6 updateComponent surface="fn-fd-4" tag=3 view="KRRichTextView"
  props:
    "text" = "5:30 PM"
#7 updateComponent surface="fn-fd-4" tag=4 view="KRRichTextView"
  props:
    "text" = "17:30"
#state dataModel "{\"at\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatDate.5_formatDate_multi_locales_pattern""" to """#0 createSurface surface="fn-fd-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#4 createComponent surface="fn-fd-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 createComponent surface="fn-fd-5" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="fn-fd-5" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="fn-fd-5" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="fn-fd-5" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#9 updateData surface="fn-fd-5"
  data:
    "at" = "2025-12-15T17:30:00Z"
    "at2" = "2026-02-02T15:17:00Z"
    "at3" = "2027-01-01T10:00:00Z"
#10 updateComponent surface="fn-fd-5" tag=3 view="KRRichTextView"
  props:
    "text" = "2025/12/15"
#11 updateComponent surface="fn-fd-5" tag=4 view="KRRichTextView"
  props:
    "text" = "15/12/2025"
#12 updateComponent surface="fn-fd-5" tag=5 view="KRRichTextView"
  props:
    "text" = "December 15, 2025"
#13 updateComponent surface="fn-fd-5" tag=6 view="KRRichTextView"
  props:
    "text" = "25-12-15"
#14 updateComponent surface="fn-fd-5" tag=7 view="KRRichTextView"
  props:
    "text" = "Mon Feb 2, 2026 3:17 PM"
#15 updateComponent surface="fn-fd-5" tag=8 view="KRRichTextView"
  props:
    "text" = "Fri Jan 1, 2026-W53 (2027)"
#state dataModel "{\"at\":\"2025-12-15T17:30:00Z\",\"at2\":\"2026-02-02T15:17:00Z\",\"at3\":\"2027-01-01T10:00:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatDate.6_formatDate_in_action_event_context""" to """#0 createSurface surface="fn-fd-6"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-6" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-6" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-6" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Action.event.context 内嵌 FunctionCall"
#4 createComponent surface="fn-fd-6" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "点击提交后，服务器/宿主收到的 event 会包含：\n · formId：字面量\n · clientTime：formatDate(/nowIso, 'E MMM d, YYYY h:mm a') 结果\n · isSubscribed：路径 /form/subscribe 的值"
#5 createComponent surface="fn-fd-6" tag=8 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#6 createComponent surface="fn-fd-6" tag=5 parent=8 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "form/subscribe"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#7 createComponent surface="fn-fd-6" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "订阅邮件通知"
#8 createComponent surface="fn-fd-6" tag=7 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#9 createComponent surface="fn-fd-6" tag=6 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交表单"
#10 updateData surface="fn-fd-6"
  data:
    "form" = {"subscribe": true}
    "nowIso" = "2026-02-02T15:17:00Z"
#11 updateComponent surface="fn-fd-6" tag=5 view="KRCheckBoxView"
  props:
    "checked" = true
#state dataModel "{\"nowIso\":\"2026-02-02T15:17:00Z\",\"form\":{\"subscribe\":true}}"
#state actions {"7": "{\"event\":{\"name\":\"submitContactForm\",\"context\":{\"formId\":\"contact_form_1\",\"clientTime\":\"Mon Feb 2, 2026 3:17 PM\",\"isSubscribed\":true}}}"}
""",
        """v0_9.function.formatDate.7_formatDate_seconds_and_timezone""" to """#0 createSurface surface="fn-fd-7"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fd-7" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fd-7" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fd-7" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "秒 token & 时区差异"
#4 createComponent surface="fn-fd-7" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "含秒（HH:mm:ss）: "
#5 createComponent surface="fn-fd-7" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "UTC 输入 → 本地: "
#6 createComponent surface="fn-fd-7" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "+08:00 输入 → 本地: "
#7 createComponent surface="fn-fd-7" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "两条时间戳表示的是同一 UTC 瞬间（15:17:42Z ≡ 23:17:42+08:00）。若客户端时区一致，t2 与 t3 应渲染出相同的本地时间字符串；若不一致则可观察到 renderer 时区解析实现差异。"
#8 updateData surface="fn-fd-7"
  data:
    "plus8" = "2026-02-02T23:17:42+08:00"
    "utc" = "2026-02-02T15:17:42Z"
#9 updateComponent surface="fn-fd-7" tag=4 view="KRRichTextView"
  props:
    "text" = "含秒（HH:mm:ss）: 15:17:42"
#10 updateComponent surface="fn-fd-7" tag=5 view="KRRichTextView"
  props:
    "text" = "UTC 输入 → 本地: 2026-02-02 15:17:42"
#11 updateComponent surface="fn-fd-7" tag=6 view="KRRichTextView"
  props:
    "text" = "+08:00 输入 → 本地: 2026-02-02 23:17:00"
#state dataModel "{\"utc\":\"2026-02-02T15:17:42Z\",\"plus8\":\"2026-02-02T23:17:42+08:00\"}"
#state actions {}
""",
        """v0_9.function.formatNumber.1_formatNumber_default""" to """#0 createSurface surface="fn-fn-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fn-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fn-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fn-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fn-1"
  data:
    "n" = 1234567.89
#5 updateComponent surface="fn-fn-1" tag=3 view="KRRichTextView"
  props:
    "text" = "1,234,567.89"
#state dataModel "{\"n\":1234567.89}"
#state actions {}
""",
        """v0_9.function.formatNumber.2_formatNumber_decimals""" to """#0 createSurface surface="fn-fn-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fn-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fn-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fn-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-fn-2"
  data:
    "pi" = 3.14159265358
#5 updateComponent surface="fn-fn-2" tag=3 view="KRRichTextView"
  props:
    "text" = "3.142"
#state dataModel "{\"pi\":3.14159265358}"
#state actions {}
""",
        """v0_9.function.formatNumber.3_formatNumber_no_grouping""" to """#0 createSurface surface="fn-fn-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fn-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fn-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fn-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "默认（grouping=true）: "
#4 createComponent surface="fn-fn-3" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "grouping=false: "
#5 updateData surface="fn-fn-3"
  data:
    "n" = 1234567
#6 updateComponent surface="fn-fn-3" tag=3 view="KRRichTextView"
  props:
    "text" = "默认（grouping=true）: 1,234,567"
#7 updateComponent surface="fn-fn-3" tag=4 view="KRRichTextView"
  props:
    "text" = "grouping=false: 1234567"
#state dataModel "{\"n\":1234567}"
#state actions {}
""",
        """v0_9.function.formatNumber.4_formatNumber_literal_number""" to """#0 createSurface surface="fn-fn-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fn-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fn-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fn-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "43"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.function.formatNumber.5_formatNumber_negative_and_bindings""" to """#0 createSurface surface="fn-fn-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fn-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fn-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fn-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "负数 & 动态 decimals/grouping"
#4 createComponent surface="fn-fn-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "负数默认: "
#5 createComponent surface="fn-fn-5" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "零值: "
#6 createComponent surface="fn-fn-5" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "decimals 绑定 (/prec=): "
#7 createComponent surface="fn-fn-5" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "grouping 绑定: "
#8 createComponent surface="fn-fn-5" tag=8 parent=2 index=5 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#9 createComponent surface="fn-fn-5" tag=11 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#10 createComponent surface="fn-fn-5" tag=9 parent=11 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "useGrouping"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#11 createComponent surface="fn-fn-5" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "启用千位分隔（切换观察 t4 变化）"
#12 createComponent surface="fn-fn-5" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "prec=3 且 pi=3.14159 时，t3 应输出 3.142（保留 3 位）；勾选/取消 CheckBox 后 t4 应在 1,234,567 与 1234567 间切换。"
#13 updateData surface="fn-fn-5"
  data:
    "big" = 1234567
    "neg" = -1234567.89
    "pi" = 3.14159
    "prec" = 3
    "useGrouping" = true
    "zero" = 0
#14 updateComponent surface="fn-fn-5" tag=4 view="KRRichTextView"
  props:
    "text" = "负数默认: -1,234,567.89"
#15 updateComponent surface="fn-fn-5" tag=5 view="KRRichTextView"
  props:
    "text" = "零值: 0.00"
#16 updateComponent surface="fn-fn-5" tag=6 view="KRRichTextView"
  props:
    "text" = "decimals 绑定 (/prec=3): 3.142"
#17 updateComponent surface="fn-fn-5" tag=7 view="KRRichTextView"
  props:
    "text" = "grouping 绑定: 1,234,567"
#18 updateComponent surface="fn-fn-5" tag=9 view="KRCheckBoxView"
  props:
    "checked" = true
#state dataModel "{\"neg\":-1234567.89,\"zero\":0,\"pi\":3.14159,\"prec\":3,\"big\":1234567,\"useGrouping\":true}"
#state actions {}
""",
        """v0_9.function.formatString.1_formatString_single_path""" to """#0 createSurface surface="fn-fs-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "Hello, !"
#4 updateData surface="fn-fs-1"
  data:
    "name" = "Alex"
#5 updateComponent surface="fn-fs-1" tag=3 view="KRRichTextView"
  props:
    "text" = "Hello, Alex!"
#state dataModel "{\"name\":\"Alex\"}"
#state actions {}
""",
        """v0_9.function.formatString.2_formatString_multi_paths""" to """#0 createSurface surface="fn-fs-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = " "
#4 createComponent surface="fn-fs-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "你有  条消息， 条通知"
#5 updateData surface="fn-fs-2"
  data:
    "stats" = {"msgs": 5, "notifs": 12}
    "user" = {"first": "Alex", "last": "Chen"}
#6 updateComponent surface="fn-fs-2" tag=3 view="KRRichTextView"
  props:
    "text" = "Alex Chen"
#7 updateComponent surface="fn-fs-2" tag=4 view="KRRichTextView"
  props:
    "text" = "你有 5 条消息，12 条通知"
#state dataModel "{\"user\":{\"first\":\"Alex\",\"last\":\"Chen\"},\"stats\":{\"msgs\":5,\"notifs\":12}}"
#state actions {}
""",
        """v0_9.function.formatString.3_formatString_nested_format_date""" to """#0 createSurface surface="fn-fs-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "发布于 "
#4 updateData surface="fn-fs-3"
  data:
    "publishedAt" = "2025-12-15T17:30:00Z"
#5 updateComponent surface="fn-fs-3" tag=3 view="KRRichTextView"
  props:
    "text" = "发布于 Dec 15, 2025"
#state dataModel "{\"publishedAt\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.formatString.4_formatString_nested_format_currency""" to """#0 createSurface surface="fn-fs-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "总价："
#4 updateData surface="fn-fs-4"
  data:
    "price" = 199.99
#5 updateComponent surface="fn-fs-4" tag=3 view="KRRichTextView"
  props:
    "text" = "总价：${'$'}199.99"
#state dataModel "{\"price\":199.99}"
#state actions {}
""",
        """v0_9.function.formatString.5_formatString_relative_path_in_template""" to """#0 createSurface surface="fn-fs-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "商品清单"
#4 createComponent surface="fn-fs-5" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#5 updateData surface="fn-fs-5"
  data:
    "items" = [{"name": "苹果", "qty": 3}, {"name": "香蕉", "qty": 5}, {"name": "橙子", "qty": 2}]
#6 createComponent surface="fn-fs-5" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "苹果 × 3"
#7 createComponent surface="fn-fs-5" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "香蕉 × 5"
#8 createComponent surface="fn-fs-5" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "橙子 × 2"
#state dataModel "{\"items\":[{\"name\":\"苹果\",\"qty\":3},{\"name\":\"香蕉\",\"qty\":5},{\"name\":\"橙子\",\"qty\":2}]}"
#state actions {}
""",
        """v0_9.function.formatString.6_formatString_mixed_absolute_relative_paths""" to """#0 createSurface surface="fn-fs-6"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-6" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-6" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-6" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = " · 员工名录"
#4 createComponent surface="fn-fs-6" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "下方每行的公司名来自绝对路径，姓名/岗位来自相对路径"
#5 createComponent surface="fn-fs-6" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#6 updateData surface="fn-fs-6"
  data:
    "company" = "Tencent"
    "users" = [{"name": "Alex Chen", "role": "Engineer"}, {"name": "Jamie Wu", "role": "Designer"}, {"name": "Robin Li", "role": "PM"}]
#7 createComponent surface="fn-fs-6" tag=7 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• [Tencent] Alex Chen — Engineer"
#8 createComponent surface="fn-fs-6" tag=8 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• [Tencent] Jamie Wu — Designer"
#9 createComponent surface="fn-fs-6" tag=9 parent=5 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• [Tencent] Robin Li — PM"
#10 updateComponent surface="fn-fs-6" tag=3 view="KRRichTextView"
  props:
    "text" = "Tencent · 员工名录"
#state dataModel "{\"company\":\"Tencent\",\"users\":[{\"name\":\"Alex Chen\",\"role\":\"Engineer\"},{\"name\":\"Jamie Wu\",\"role\":\"Designer\"},{\"name\":\"Robin Li\",\"role\":\"PM\"}]}"
#state actions {}
""",
        """v0_9.function.formatString.7_formatString_escape_literal""" to """#0 createSurface surface="fn-fs-7"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-7" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-7" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-7" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "formatString 转义规则演示"
#4 createComponent surface="fn-fs-7" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1) 未转义（会被插值）: hello, "
#5 createComponent surface="fn-fs-7" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2) 已转义（输出字面量）: 模板语法为 ${'$'}{/user/name}"
#6 createComponent surface="fn-fs-7" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3) 混合: ${'$'}{...} 表示  的引用；实际值 = "
#7 updateData surface="fn-fs-7"
  data:
    "user" = {"name": "Alex"}
#8 updateComponent surface="fn-fs-7" tag=4 view="KRRichTextView"
  props:
    "text" = "1) 未转义（会被插值）: hello, Alex"
#9 updateComponent surface="fn-fs-7" tag=5 view="KRRichTextView"
  props:
    "text" = "2) 已转义（输出字面量）: 模板语法为 ${'$'}{/user/name}"
#10 updateComponent surface="fn-fs-7" tag=6 view="KRRichTextView"
  props:
    "text" = "3) 混合: ${'$'}{...} 表示 Alex 的引用；实际值 = Alex"
#state dataModel "{\"user\":{\"name\":\"Alex\"}}"
#state actions {}
""",
        """v0_9.function.formatString.8_formatString_type_conversion""" to """#0 createSurface surface="fn-fs-8"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-8" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-8" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-8" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "非字符串值插值 → 类型转换规则"
#4 createComponent surface="fn-fs-8" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Number → \"\"（期望：42.5）"
#5 createComponent surface="fn-fs-8" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Boolean → \"\"（期望：true）"
#6 createComponent surface="fn-fs-8" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Null → \"\"（期望：空串）"
#7 createComponent surface="fn-fs-8" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Undefined（路径不存在）→ \"\"（期望：空串）"
#8 createComponent surface="fn-fs-8" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Array → （期望：JSON 字符串）"
#9 createComponent surface="fn-fs-8" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Object → （期望：JSON 字符串）"
#10 updateData surface="fn-fs-8"
  data:
    "values" = {"flag": true, "list": [1, 2, 3], "n": 42.5, "nothing": null, "point": {"x": 10, "y": 20}}
#11 updateComponent surface="fn-fs-8" tag=4 view="KRRichTextView"
  props:
    "text" = "Number → \"42.5\"（期望：42.5）"
#12 updateComponent surface="fn-fs-8" tag=5 view="KRRichTextView"
  props:
    "text" = "Boolean → \"true\"（期望：true）"
#13 updateComponent surface="fn-fs-8" tag=6 view="KRRichTextView"
  props:
    "text" = "Null → \"\"（期望：空串）"
#14 updateComponent surface="fn-fs-8" tag=7 view="KRRichTextView"
  props:
    "text" = "Undefined（路径不存在）→ \"\"（期望：空串）"
#15 updateComponent surface="fn-fs-8" tag=8 view="KRRichTextView"
  props:
    "text" = "Array → [1,2,3]（期望：JSON 字符串）"
#16 updateComponent surface="fn-fs-8" tag=9 view="KRRichTextView"
  props:
    "text" = "Object → {\"x\":10,\"y\":20}（期望：JSON 字符串）"
#state dataModel "{\"values\":{\"n\":42.5,\"flag\":true,\"nothing\":null,\"list\":[1,2,3],\"point\":{\"x\":10,\"y\":20}}}"
#state actions {}
""",
        """v0_9.function.formatString.9_nested_dollar_expression""" to """#0 createSurface surface="fn-fs-9"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-fs-9" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-fs-9" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-fs-9" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "formatString 嵌套 ${'$'}{...} 表达式"
#4 createComponent surface="fn-fs-9" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Explicit binding: date = "
#5 createComponent surface="fn-fs-9" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Nested functions: price = "
#6 createComponent surface="fn-fs-9" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Nested string: greeting = hi !"
#7 createComponent surface="fn-fs-9" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Combined:  paid  on "
#8 updateData surface="fn-fs-9"
  data:
    "at" = "2025-12-15T17:30:00Z"
    "price" = 1234.5
    "user" = {"name": "Alice"}
#9 updateComponent surface="fn-fs-9" tag=6 view="KRRichTextView"
  props:
    "text" = "Nested string: greeting = hi Alice!"
#10 updateComponent surface="fn-fs-9" tag=7 view="KRRichTextView"
  props:
    "text" = "Combined: Alice paid ${'$'}1,234.50 on Dec 15, 2025"
#11 updateComponent surface="fn-fs-9" tag=5 view="KRRichTextView"
  props:
    "text" = "Nested functions: price = ${'$'}1,234.50"
#12 updateComponent surface="fn-fs-9" tag=4 view="KRRichTextView"
  props:
    "text" = "Explicit binding: date = 2025-12-15"
#state dataModel "{\"user\":{\"name\":\"Alice\"},\"price\":1234.5,\"at\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v0_9.function.length.1_length_min_only""" to """#0 createSurface surface="fn-length-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-length-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-length-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-length-1" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-length-1" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "密码（≥6）"
#5 createComponent surface="fn-length-1" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/password"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-length-1" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-length-1" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-length-1" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "密码至少 6 位"
#9 createComponent surface="fn-length-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "登录"
#10 updateData surface="fn-length-1"
  data:
    "form" = {"password": ""}
#11 updateComponent surface="fn-length-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-length-1" tag=7 view="KRRichTextView"
  props:
    "text" = "密码（≥6）"
#state dataModel "{\"form\":{\"password\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.length.2_length_max_only""" to """#0 createSurface surface="fn-length-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-length-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-length-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-length-2" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-length-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "昵称（≤10）"
#5 createComponent surface="fn-length-2" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/nickname"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-length-2" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-length-2" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-length-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "昵称最多 10 个字符"
#9 createComponent surface="fn-length-2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "保存"
#10 updateData surface="fn-length-2"
  data:
    "form" = {"nickname": ""}
#11 updateComponent surface="fn-length-2" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-length-2" tag=7 view="KRRichTextView"
  props:
    "text" = "昵称（≤10）"
#state dataModel "{\"form\":{\"nickname\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.length.3_length_min_max_range""" to """#0 createSurface surface="fn-length-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-length-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-length-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-length-3" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-length-3" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "用户名（4-8）"
#5 createComponent surface="fn-length-3" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/username"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-length-3" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-length-3" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-length-3" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "用户名长度必须在 4~8 之间"
#9 createComponent surface="fn-length-3" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "注册"
#10 updateData surface="fn-length-3"
  data:
    "form" = {"username": ""}
#11 updateComponent surface="fn-length-3" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-length-3" tag=7 view="KRRichTextView"
  props:
    "text" = "用户名（4-8）"
#state dataModel "{\"form\":{\"username\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.not.1_not_invert_bool""" to """#0 createSurface surface="fn-not-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-not-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-not-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-not-1" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="fn-not-1" tag=3 parent=6 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "f/disabled"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="fn-not-1" tag=7 parent=6 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "禁用按钮"
#6 createComponent surface="fn-not-1" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-not-1" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="fn-not-1" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#9 createComponent surface="fn-not-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "点击"
#10 updateData surface="fn-not-1"
  data:
    "f" = {"disabled": false}
#11 updateComponent surface="fn-not-1" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"f\":{\"disabled\":false}}"
#state actions {"4": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.not.2_not_not_of_email""" to """#0 createSurface surface="fn-not-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-not-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-not-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-not-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "填入非邮箱的字符串以启用按钮"
#4 createComponent surface="fn-not-2" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-not-2" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "非邮箱"
#6 createComponent surface="fn-not-2" tag=4 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/text"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-not-2" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="fn-not-2" tag=5 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#9 createComponent surface="fn-not-2" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "该值必须不是合法邮箱"
#10 createComponent surface="fn-not-2" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "确认（值不是邮箱）"
#11 updateData surface="fn-not-2"
  data:
    "f" = {"text": "user@example.com"}
#12 updateComponent surface="fn-not-2" tag=4 view="KRTextFieldView"
  props:
    "text" = "user@example.com"
#13 updateComponent surface="fn-not-2" tag=8 view="KRRichTextView"
  props:
    "text" = "非邮箱"
#state dataModel "{\"f\":{\"text\":\"user@example.com\"}}"
#state actions {"5": null}
""",
        """v0_9.function.not.3_not_not_of_and""" to """#0 createSurface surface="fn-not-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-not-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-not-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-not-3" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-not-3" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "字段A"
#5 createComponent surface="fn-not-3" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/a"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-not-3" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-not-3" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "字段B"
#8 createComponent surface="fn-not-3" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/b"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-not-3" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-not-3" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="fn-not-3" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#12 createComponent surface="fn-not-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "任一为空则可点"
#13 updateData surface="fn-not-3"
  data:
    "f" = {"a": "", "b": ""}
#14 updateComponent surface="fn-not-3" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-not-3" tag=8 view="KRRichTextView"
  props:
    "text" = "字段A"
#16 updateComponent surface="fn-not-3" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#17 updateComponent surface="fn-not-3" tag=10 view="KRRichTextView"
  props:
    "text" = "字段B"
#state dataModel "{\"f\":{\"a\":\"\",\"b\":\"\"}}"
#state actions {"5": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.not.4_not_not_of_or""" to """#0 createSurface surface="fn-not-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-not-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-not-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-not-4" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-not-4" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "字段A"
#5 createComponent surface="fn-not-4" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/a"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-not-4" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-not-4" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "字段B"
#8 createComponent surface="fn-not-4" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/b"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-not-4" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-not-4" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="fn-not-4" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#12 createComponent surface="fn-not-4" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "两个都空才可点"
#13 updateData surface="fn-not-4"
  data:
    "f" = {"a": "", "b": ""}
#14 updateComponent surface="fn-not-4" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-not-4" tag=8 view="KRRichTextView"
  props:
    "text" = "字段A"
#16 updateComponent surface="fn-not-4" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#17 updateComponent surface="fn-not-4" tag=10 view="KRRichTextView"
  props:
    "text" = "字段B"
#state dataModel "{\"f\":{\"a\":\"\",\"b\":\"\"}}"
#state actions {"5": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.not.5_and_of_two_nots""" to """#0 createSurface surface="fn-not-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-not-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-not-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-not-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "用户名不能含数字，长度不超过 20 字符"
#4 createComponent surface="fn-not-5" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-not-5" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "用户名"
#6 createComponent surface="fn-not-5" tag=4 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-not-5" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="fn-not-5" tag=5 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#9 createComponent surface="fn-not-5" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#10 createComponent surface="fn-not-5" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#11 updateData surface="fn-not-5"
  data:
    "f" = {"name": ""}
#12 updateComponent surface="fn-not-5" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#13 updateComponent surface="fn-not-5" tag=8 view="KRRichTextView"
  props:
    "text" = "用户名"
#state dataModel "{\"f\":{\"name\":\"\"}}"
#state actions {"5": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.numeric.1_numeric_age_min_18""" to """#0 createSurface surface="fn-numeric-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-numeric-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-numeric-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-numeric-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "当前年龄："
#4 createComponent surface="fn-numeric-1" tag=4 parent=2 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/age"
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 60
    "progressColor" = "4278239231"
    "value" = ""
#5 createComponent surface="fn-numeric-1" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="fn-numeric-1" tag=5 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#7 createComponent surface="fn-numeric-1" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "年龄必须 ≥ 18"
#8 createComponent surface="fn-numeric-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#9 updateData surface="fn-numeric-1"
  data:
    "form" = {"age": 15}
#10 updateComponent surface="fn-numeric-1" tag=3 view="KRRichTextView"
  props:
    "text" = "当前年龄：15"
#11 updateComponent surface="fn-numeric-1" tag=4 view="KRSliderView"
  props:
    "value" = 15
#state dataModel "{\"form\":{\"age\":15}}"
#state actions {"5": null}
""",
        """v0_9.function.numeric.2_numeric_discount_max_100""" to """#0 createSurface surface="fn-numeric-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-numeric-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-numeric-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-numeric-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "当前折扣：%"
#4 createComponent surface="fn-numeric-2" tag=4 parent=2 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/discount"
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 200
    "progressColor" = "4278239231"
    "value" = ""
#5 createComponent surface="fn-numeric-2" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="fn-numeric-2" tag=5 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#7 createComponent surface="fn-numeric-2" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "折扣不能超过 100"
#8 createComponent surface="fn-numeric-2" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "应用"
#9 updateData surface="fn-numeric-2"
  data:
    "form" = {"discount": 30}
#10 updateComponent surface="fn-numeric-2" tag=3 view="KRRichTextView"
  props:
    "text" = "当前折扣：30%"
#11 updateComponent surface="fn-numeric-2" tag=4 view="KRSliderView"
  props:
    "value" = 30
#state dataModel "{\"form\":{\"discount\":30}}"
#state actions {"5": null}
""",
        """v0_9.function.numeric.3_numeric_temperature_range""" to """#0 createSurface surface="fn-numeric-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-numeric-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-numeric-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-numeric-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "当前温度：°C"
#4 createComponent surface="fn-numeric-3" tag=4 parent=2 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "form/temp"
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 40
    "progressColor" = "4278239231"
    "value" = ""
#5 createComponent surface="fn-numeric-3" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="fn-numeric-3" tag=5 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#7 createComponent surface="fn-numeric-3" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "温度必须在 16~30 之间"
#8 createComponent surface="fn-numeric-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "确认"
#9 updateData surface="fn-numeric-3"
  data:
    "form" = {"temp": 10}
#10 updateComponent surface="fn-numeric-3" tag=3 view="KRRichTextView"
  props:
    "text" = "当前温度：10°C"
#11 updateComponent surface="fn-numeric-3" tag=4 view="KRSliderView"
  props:
    "value" = 10
#state dataModel "{\"form\":{\"temp\":10}}"
#state actions {"5": null}
""",
        """v0_9.function.openUrl.1_openUrl_button_click""" to """#0 createSurface surface="fn-url-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-url-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-url-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-url-1" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#4 createComponent surface="fn-url-1" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "打开 A2UI 官网"
#state dataModel "{}"
#state actions {"3": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://a2ui.org/\"},\"returnType\":\"void\"}}"}
""",
        """v0_9.function.openUrl.2_openUrl_multi_buttons_row""" to """#0 createSurface surface="fn-url-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-url-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-url-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-url-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "常用链接"
#4 createComponent surface="fn-url-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#5 createComponent surface="fn-url-2" tag=5 parent=4 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="fn-url-2" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "GitHub"
#7 createComponent surface="fn-url-2" tag=7 parent=4 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="fn-url-2" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "官网"
#9 createComponent surface="fn-url-2" tag=9 parent=4 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#10 createComponent surface="fn-url-2" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "文档"
#state dataModel "{}"
#state actions {"5": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://github.com/\"},\"returnType\":\"void\"}}", "7": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://a2ui.org/\"},\"returnType\":\"void\"}}", "9": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://a2ui.org/specification/v0_9/\"},\"returnType\":\"void\"}}"}
""",
        """v0_9.function.openUrl.3_openUrl_icon_only_button""" to """#0 createSurface surface="fn-url-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-url-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-url-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-url-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "点击图标打开链接"
#4 createComponent surface="fn-url-3" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="fn-url-3" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "→"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {"4": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://a2ui.org/\"},\"returnType\":\"void\"}}"}
""",
        """v0_9.function.openUrl.4_openUrl_uri_schemes""" to """#0 createSurface surface="fn-url-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-url-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-url-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-url-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "多 URI scheme"
#4 createComponent surface="fn-url-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "点击按钮触发系统对应 handler：邮件、拨号、短信、深链。若某 scheme 未注册则可能被吞掉，属于宿主行为。"
#5 createComponent surface="fn-url-4" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#6 createComponent surface="fn-url-4" tag=7 parent=5 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#7 createComponent surface="fn-url-4" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "📧 mailto"
#8 createComponent surface="fn-url-4" tag=9 parent=5 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#9 createComponent surface="fn-url-4" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "📞 tel"
#10 createComponent surface="fn-url-4" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#11 createComponent surface="fn-url-4" tag=11 parent=6 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#12 createComponent surface="fn-url-4" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "💬 sms"
#13 createComponent surface="fn-url-4" tag=13 parent=6 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#14 createComponent surface="fn-url-4" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "🔗 深链"
#state dataModel "{}"
#state actions {"11": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"sms:+861008611?body=Hello%20A2UI\"},\"returnType\":\"void\"}}", "13": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"a2ui-demo://sample/path?x=1\"},\"returnType\":\"void\"}}", "7": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"mailto:hello@a2ui.org?subject=Hi&body=Test%20from%20A2UI\"},\"returnType\":\"void\"}}", "9": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"tel:+861008611\"},\"returnType\":\"void\"}}"}
""",
        """v0_9.function.openUrl.5_scheme_allowlist_reject""" to """#0 createSurface surface="fn-url-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-url-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-url-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-url-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "openUrl allowlist（Mandatory 安全强制项）"
#4 createComponent surface="fn-url-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "规范要求：以下 4 个按钮点击后应被 renderer/host 主动拒绝，不打开任何目标。若浏览器/系统弹起或 Toast 无提示，说明实现未对齐规范。"
#5 createComponent surface="fn-url-5" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="fn-url-5" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "❌ javascript: (XSS)"
#7 createComponent surface="fn-url-5" tag=7 parent=2 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="fn-url-5" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "❌ data:text/html (inline HTML)"
#9 createComponent surface="fn-url-5" tag=9 parent=2 index=6 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#10 createComponent surface="fn-url-5" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "❌ file:// (本地文件泄露)"
#11 createComponent surface="fn-url-5" tag=11 parent=2 index=8 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#12 createComponent surface="fn-url-5" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "❌ ftp:// (非 HTTP)"
#13 createComponent surface="fn-url-5" tag=13 parent=2 index=10 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#14 createComponent surface="fn-url-5" tag=14 parent=2 index=11 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "以下按钮是合规对照（应正常打开）："
#15 createComponent surface="fn-url-5" tag=15 parent=2 index=12 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#16 createComponent surface="fn-url-5" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "✅ https://example.com"
#state dataModel "{}"
#state actions {"11": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"ftp://example.com/secret.txt\"},\"returnType\":\"void\"}}", "15": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"https://example.com/\"},\"returnType\":\"void\"}}", "5": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"javascript:alert('xss-poc')\"},\"returnType\":\"void\"}}", "7": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"data:text/html,<script>alert('xss')</script>\"},\"returnType\":\"void\"}}", "9": "{\"functionCall\":{\"call\":\"openUrl\",\"args\":{\"url\":\"file:///etc/passwd\"},\"returnType\":\"void\"}}"}
""",
        """v0_9.function.or.1_or_email_or_phone""" to """#0 createSurface surface="fn-or-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-or-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-or-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-or-1" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-or-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#5 createComponent surface="fn-or-1" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-or-1" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-or-1" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "手机号"
#8 createComponent surface="fn-or-1" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/phone"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-or-1" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-or-1" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#11 createComponent surface="fn-or-1" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "至少填写邮箱或手机号"
#12 createComponent surface="fn-or-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "找回密码"
#13 updateData surface="fn-or-1"
  data:
    "f" = {"email": "", "phone": ""}
#14 updateComponent surface="fn-or-1" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-or-1" tag=8 view="KRRichTextView"
  props:
    "text" = "邮箱"
#16 updateComponent surface="fn-or-1" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#17 updateComponent surface="fn-or-1" tag=10 view="KRRichTextView"
  props:
    "text" = "手机号"
#state dataModel "{\"f\":{\"email\":\"\",\"phone\":\"\"}}"
#state actions {"5": null}
""",
        """v0_9.function.or.2_or_three_values""" to """#0 createSurface surface="fn-or-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-or-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-or-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-or-2" tag=8 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="fn-or-2" tag=3 parent=8 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "pay/wechat"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="fn-or-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "微信支付"
#6 createComponent surface="fn-or-2" tag=10 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#7 createComponent surface="fn-or-2" tag=4 parent=10 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "pay/alipay"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#8 createComponent surface="fn-or-2" tag=11 parent=10 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "支付宝"
#9 createComponent surface="fn-or-2" tag=12 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#10 createComponent surface="fn-or-2" tag=5 parent=12 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "pay/card"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#11 createComponent surface="fn-or-2" tag=13 parent=12 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "银行卡"
#12 createComponent surface="fn-or-2" tag=14 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="fn-or-2" tag=6 parent=14 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#14 createComponent surface="fn-or-2" tag=15 parent=14 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "请至少选择一种支付方式"
#15 createComponent surface="fn-or-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "支付"
#16 updateData surface="fn-or-2"
  data:
    "pay" = {"alipay": false, "card": false, "wechat": false}
#17 updateComponent surface="fn-or-2" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#18 updateComponent surface="fn-or-2" tag=4 view="KRCheckBoxView"
  props:
    "checked" = false
#19 updateComponent surface="fn-or-2" tag=5 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"pay\":{\"wechat\":false,\"alipay\":false,\"card\":false}}"
#state actions {"6": null}
""",
        """v0_9.function.or.3_or_with_not""" to """#0 createSurface surface="fn-or-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-or-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-or-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-or-3" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="fn-or-3" tag=3 parent=7 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "f/guest"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="fn-or-3" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "游客模式"
#6 createComponent surface="fn-or-3" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-or-3" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱（若非游客）"
#8 createComponent surface="fn-or-3" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-or-3" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-or-3" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#11 createComponent surface="fn-or-3" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#12 createComponent surface="fn-or-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "继续"
#13 updateData surface="fn-or-3"
  data:
    "f" = {"email": "", "guest": false}
#14 updateComponent surface="fn-or-3" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#15 updateComponent surface="fn-or-3" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#16 updateComponent surface="fn-or-3" tag=10 view="KRRichTextView"
  props:
    "text" = "邮箱（若非游客）"
#state dataModel "{\"f\":{\"guest\":false,\"email\":\"\"}}"
#state actions {"5": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.or.4_or_of_two_ands""" to """#0 createSurface surface="fn-or-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-or-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-or-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-or-4" tag=9 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-or-4" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱（方案A）"
#5 createComponent surface="fn-or-4" tag=3 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-or-4" tag=11 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-or-4" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "密码（方案A）"
#8 createComponent surface="fn-or-4" tag=4 parent=11 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/password"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-or-4" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-or-4" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "手机号（方案B）"
#11 createComponent surface="fn-or-4" tag=5 parent=13 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/phone"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#12 createComponent surface="fn-or-4" tag=15 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#13 createComponent surface="fn-or-4" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "短信码（方案B）"
#14 createComponent surface="fn-or-4" tag=6 parent=15 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "f/smsCode"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#15 createComponent surface="fn-or-4" tag=17 parent=2 index=4 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#16 createComponent surface="fn-or-4" tag=7 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#17 createComponent surface="fn-or-4" tag=18 parent=17 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "请完整填写方案A（邮箱+密码）或方案B（手机+短信码）"
#18 createComponent surface="fn-or-4" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "登录"
#19 updateData surface="fn-or-4"
  data:
    "f" = {"email": "", "password": "", "phone": "", "smsCode": ""}
#20 updateComponent surface="fn-or-4" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#21 updateComponent surface="fn-or-4" tag=10 view="KRRichTextView"
  props:
    "text" = "邮箱（方案A）"
#22 updateComponent surface="fn-or-4" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#23 updateComponent surface="fn-or-4" tag=12 view="KRRichTextView"
  props:
    "text" = "密码（方案A）"
#24 updateComponent surface="fn-or-4" tag=5 view="KRTextFieldView"
  props:
    "text" = ""
#25 updateComponent surface="fn-or-4" tag=14 view="KRRichTextView"
  props:
    "text" = "手机号（方案B）"
#26 updateComponent surface="fn-or-4" tag=6 view="KRTextFieldView"
  props:
    "text" = ""
#27 updateComponent surface="fn-or-4" tag=16 view="KRRichTextView"
  props:
    "text" = "短信码（方案B）"
#state dataModel "{\"f\":{\"email\":\"\",\"password\":\"\",\"phone\":\"\",\"smsCode\":\"\"}}"
#state actions {"7": null}
""",
        """v0_9.function.pluralize.1_pluralize_en_one_other""" to """#0 createSurface surface="fn-pl-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "You have  "
#4 updateData surface="fn-pl-1"
  data:
    "count" = 1
#5 updateComponent surface="fn-pl-1" tag=3 view="KRRichTextView"
  props:
    "text" = "You have 1 item"
#state dataModel "{\"count\":1}"
#state actions {}
""",
        """v0_9.function.pluralize.2_pluralize_with_zero""" to """#0 createSurface surface="fn-pl-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "No messages"
#4 createComponent surface="fn-pl-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "1 message"
#5 createComponent surface="fn-pl-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "5 messages"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.function.pluralize.3_pluralize_cldr_categories""" to """#0 createSurface surface="fn-pl-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#4 updateData surface="fn-pl-3"
  data:
    "n" = 5
#5 updateComponent surface="fn-pl-3" tag=3 view="KRRichTextView"
  props:
    "text" = "other"
#state dataModel "{\"n\":5}"
#state actions {}
""",
        """v0_9.function.pluralize.4_pluralize_with_data_binding""" to """#0 createSurface surface="fn-pl-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 24.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "购物车： "
#4 createComponent surface="fn-pl-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "改变 data model /cart/count 观察复数变化"
#5 updateData surface="fn-pl-4"
  data:
    "cart" = {"count": 3}
#6 updateComponent surface="fn-pl-4" tag=3 view="KRRichTextView"
  props:
    "text" = "购物车：3 items"
#state dataModel "{\"cart\":{\"count\":3}}"
#state actions {}
""",
        """v0_9.function.pluralize.5_pluralize_cldr_boundaries""" to """#0 createSurface surface="fn-pl-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "CLDR 边界：0 / 1 / 2 / 100"
#4 createComponent surface="fn-pl-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "n=0 → CLDR 'zero'（若语言支持）"
#5 createComponent surface="fn-pl-5" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "n=1 → CLDR 'one'（英/中都命中）"
#6 createComponent surface="fn-pl-5" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "n=2 → 'other'（英语默认）"
#7 createComponent surface="fn-pl-5" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "n=100 → 'other'（英语默认）"
#8 createComponent surface="fn-pl-5" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "对英语 renderer：t0 应走 other；t1 应走 one；t2/t100 应走 other。若 renderer 未严格遵循 CLDR 而用简化规则（如 n==1 → one，其余 → other），t0/t2/t100 都会走 other，符合预期。若某 renderer 抛错，说明分类实现不完整。"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.function.pluralize.6_fallback_only_other""" to """#0 createSurface surface="fn-pl-6"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-pl-6" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-pl-6" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-pl-6" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "只提供 other 时，CLDR 会回退"
#4 createComponent surface="fn-pl-6" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "%d items"
#5 createComponent surface="fn-pl-6" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "%d items"
#6 createComponent surface="fn-pl-6" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "%d items"
#7 createComponent surface="fn-pl-6" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "三行都应显示 '%d items' 模板输出（fallback 生效）"
#state dataModel "{}"
#state actions {}
""",
        """v0_9.function.regex.1_regex_zipcode_5digits""" to """#0 createSurface surface="fn-regex-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-regex-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-regex-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-regex-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "pattern = ^\\d{5}${'$'}"
#4 createComponent surface="fn-regex-1" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-regex-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮编"
#6 createComponent surface="fn-regex-1" tag=4 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "data/zip"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-regex-1" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="fn-regex-1" tag=5 parent=9 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#9 createComponent surface="fn-regex-1" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "必须是 5 位数字"
#10 createComponent surface="fn-regex-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "验证"
#11 updateData surface="fn-regex-1"
  data:
    "data" = {"zip": ""}
#12 updateComponent surface="fn-regex-1" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#13 updateComponent surface="fn-regex-1" tag=8 view="KRRichTextView"
  props:
    "text" = "邮编"
#state dataModel "{\"data\":{\"zip\":\"\"}}"
#state actions {"5": null}
""",
        """v0_9.function.regex.2_regex_http_url""" to """#0 createSurface surface="fn-regex-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-regex-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-regex-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-regex-2" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-regex-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "网址"
#5 createComponent surface="fn-regex-2" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "data/url"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-regex-2" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-regex-2" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-regex-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "必须以 http:// 或 https:// 开头"
#9 createComponent surface="fn-regex-2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "打开"
#10 updateData surface="fn-regex-2"
  data:
    "data" = {"url": ""}
#11 updateComponent surface="fn-regex-2" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-regex-2" tag=7 view="KRRichTextView"
  props:
    "text" = "网址"
#state dataModel "{\"data\":{\"url\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.regex.3_regex_cn_mobile""" to """#0 createSurface surface="fn-regex-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-regex-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-regex-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-regex-3" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-regex-3" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "手机号"
#5 createComponent surface="fn-regex-3" tag=3 parent=6 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "data/mobile"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-regex-3" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-regex-3" tag=4 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#8 createComponent surface="fn-regex-3" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "手机号格式不正确"
#9 createComponent surface="fn-regex-3" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "发送验证码"
#10 updateData surface="fn-regex-3"
  data:
    "data" = {"mobile": ""}
#11 updateComponent surface="fn-regex-3" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#12 updateComponent surface="fn-regex-3" tag=7 view="KRRichTextView"
  props:
    "text" = "手机号"
#state dataModel "{\"data\":{\"mobile\":\"\"}}"
#state actions {"4": null}
""",
        """v0_9.function.required.1_required_literal_pass""" to """#0 createSurface surface="fn-required-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-required-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-required-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-required-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "checks: required(value:'hello') → 恒为 true"
#4 createComponent surface="fn-required-1" tag=6 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-required-1" tag=4 parent=6 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 1.0
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="fn-required-1" tag=7 parent=6 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#7 createComponent surface="fn-required-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "已就绪（可点击）"
#state dataModel "{}"
#state actions {"4": "{\"event\":{\"name\":\"submit\"}}"}
""",
        """v0_9.function.required.2_required_path_empty_disable""" to """#0 createSurface surface="fn-required-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-required-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-required-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-required-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "在下方输入框填入内容后，按钮才会启用"
#4 createComponent surface="fn-required-2" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="fn-required-2" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "用户名"
#6 createComponent surface="fn-required-2" tag=4 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "formData/username"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#7 createComponent surface="fn-required-2" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "当前值：\"\""
#8 createComponent surface="fn-required-2" tag=10 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#9 createComponent surface="fn-required-2" tag=6 parent=10 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#10 createComponent surface="fn-required-2" tag=11 parent=10 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "用户名不能为空"
#11 createComponent surface="fn-required-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交（需填写）"
#12 updateData surface="fn-required-2"
  data:
    "formData" = {"username": ""}
#13 updateComponent surface="fn-required-2" tag=5 view="KRRichTextView"
  props:
    "text" = "当前值：\"\""
#14 updateComponent surface="fn-required-2" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-required-2" tag=9 view="KRRichTextView"
  props:
    "text" = "用户名"
#state dataModel "{\"formData\":{\"username\":\"\"}}"
#state actions {"6": null}
""",
        """v0_9.function.required.3_required_form_multi_fields""" to """#0 createSurface surface="fn-required-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-required-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-required-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-required-3" tag=7 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="fn-required-3" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "姓名"
#5 createComponent surface="fn-required-3" tag=3 parent=7 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="fn-required-3" tag=9 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="fn-required-3" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "邮箱"
#8 createComponent surface="fn-required-3" tag=4 parent=9 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#9 createComponent surface="fn-required-3" tag=11 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#10 createComponent surface="fn-required-3" tag=5 parent=11 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#11 createComponent surface="fn-required-3" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "姓名和邮箱均不能为空"
#12 createComponent surface="fn-required-3" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#13 updateData surface="fn-required-3"
  data:
    "form" = {"email": "", "name": ""}
#14 updateComponent surface="fn-required-3" tag=3 view="KRTextFieldView"
  props:
    "text" = ""
#15 updateComponent surface="fn-required-3" tag=8 view="KRRichTextView"
  props:
    "text" = "姓名"
#16 updateComponent surface="fn-required-3" tag=4 view="KRTextFieldView"
  props:
    "text" = ""
#17 updateComponent surface="fn-required-3" tag=10 view="KRRichTextView"
  props:
    "text" = "邮箱"
#state dataModel "{\"form\":{\"name\":\"\",\"email\":\"\"}}"
#state actions {"5": null}
""",
        """v0_9.function.required.4_required_null_value""" to """#0 createSurface surface="fn-required-4"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-required-4" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-required-4" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-required-4" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "数据模型：{ \"form\": { \"nickname\": null } }。当值为 null 时 required 返回 false，按钮禁用。"
#4 createComponent surface="fn-required-4" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前值：\"\""
#5 createComponent surface="fn-required-4" tag=8 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="fn-required-4" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "在此输入以覆盖 null 值"
#7 createComponent surface="fn-required-4" tag=5 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "form/nickname"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#8 createComponent surface="fn-required-4" tag=10 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#9 createComponent surface="fn-required-4" tag=6 parent=10 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#10 createComponent surface="fn-required-4" tag=11 parent=10 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "昵称不能为 null"
#11 createComponent surface="fn-required-4" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交（值不能为 null）"
#12 updateData surface="fn-required-4"
  data:
    "form" = {"nickname": null}
#13 updateComponent surface="fn-required-4" tag=4 view="KRRichTextView"
  props:
    "text" = "当前值：\"\""
#14 updateComponent surface="fn-required-4" tag=9 view="KRRichTextView"
  props:
    "text" = "在此输入以覆盖 null 值"
#state dataModel "{\"form\":{\"nickname\":null}}"
#state actions {"6": null}
""",
        """v0_9.function.required.5_required_undefined_path""" to """#0 createSurface surface="fn-required-5"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="fn-required-5" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="fn-required-5" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="fn-required-5" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "required 对 undefined / null / 空串 的判定"
#4 createComponent surface="fn-required-5" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "三种空场景 required 均返回 false，按钮全部禁用。"
#5 createComponent surface="fn-required-5" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#6 createComponent surface="fn-required-5" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1. undefined（数据模型无该 key）"
#7 createComponent surface="fn-required-5" tag=17 parent=5 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="fn-required-5" tag=7 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#9 createComponent surface="fn-required-5" tag=18 parent=17 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "path 未定义 → required 失败"
#10 createComponent surface="fn-required-5" tag=14 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#11 createComponent surface="fn-required-5" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#12 createComponent surface="fn-required-5" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2. null（显式赋值为 null）"
#13 createComponent surface="fn-required-5" tag=19 parent=8 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#14 createComponent surface="fn-required-5" tag=10 parent=19 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#15 createComponent surface="fn-required-5" tag=20 parent=19 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "值为 null → required 失败"
#16 createComponent surface="fn-required-5" tag=15 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#17 createComponent surface="fn-required-5" tag=11 parent=2 index=4 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#18 createComponent surface="fn-required-5" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3. 空串 \"\""
#19 createComponent surface="fn-required-5" tag=21 parent=11 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#20 createComponent surface="fn-required-5" tag=13 parent=21 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#21 createComponent surface="fn-required-5" tag=22 parent=21 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "值为空串 → required 失败"
#22 createComponent surface="fn-required-5" tag=16 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "提交"
#23 updateData surface="fn-required-5"
  data:
    "form" = {"emptyStr": "", "nullField": null}
#state dataModel "{\"form\":{\"nullField\":null,\"emptyStr\":\"\"}}"
#state actions {"10": null, "13": null, "7": null}
""",
        """v0_9.theme.01_primary_color_basic.1_primary_color_basic""" to """#0 createSurface surface="theme-pc-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-pc-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-pc-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-pc-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "本页面 theme.primaryColor = #E91E63（品红），下列元素背景/进度/选中态应统一使用该色。"
#4 createComponent surface="theme-pc-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4293467747"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-pc-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（背景应为品红）"
#6 createComponent surface="theme-pc-1" tag=8 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="theme-pc-1" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Slider progressColor 应为品红"
#8 createComponent surface="theme-pc-1" tag=6 parent=8 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "level"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4293467747"
    "value" = ""
#9 createComponent surface="theme-pc-1" tag=7 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#10 createComponent surface="theme-pc-1" tag=10 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#11 createComponent surface="theme-pc-1" tag=11 parent=10 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#12 createComponent surface="theme-pc-1" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4293467747"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "首页"
#13 createComponent surface="theme-pc-1" tag=13 parent=11 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4293467747"
    "height" = 2.5
    "marginTop" = 8.0
#14 createComponent surface="theme-pc-1" tag=14 parent=10 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#15 createComponent surface="theme-pc-1" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "发现"
#16 createComponent surface="theme-pc-1" tag=16 parent=14 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#17 createComponent surface="theme-pc-1" tag=17 parent=10 index=2 view="KRView"
  props:
    "__tabIndex" = 2
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#18 createComponent surface="theme-pc-1" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "我的"
#19 createComponent surface="theme-pc-1" tag=19 parent=17 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#20 createComponent surface="theme-pc-1" tag=20 parent=7 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#21 createComponent surface="theme-pc-1" tag=21 parent=7 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#22 createComponent surface="theme-pc-1" tag=22 parent=7 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#23 updateData surface="theme-pc-1"
  data:
    "level" = 60
    "tab" = "discover"
#24 updateComponent surface="theme-pc-1" tag=6 view="KRSliderView"
  props:
    "value" = 60
#25 updateComponent surface="theme-pc-1" tag=9 view="KRRichTextView"
  props:
    "text" = "Slider progressColor 应为品红"
#state dataModel "{\"level\":60,\"tab\":\"discover\"}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.01_primary_color_basic.2_primary_color_brand_green""" to """#0 createSurface surface="theme-pc-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-pc-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-pc-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-pc-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "本页面 theme.primaryColor = #2ECC71（品牌绿），下列选中态与主按钮应统一为绿色。"
#4 createComponent surface="theme-pc-2" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#5 createComponent surface="theme-pc-2" tag=4 parent=8 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "agree"
    "__primaryColor" = "4281257073"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#6 createComponent surface="theme-pc-2" tag=9 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "我同意条款（勾应为绿色）"
#7 createComponent surface="theme-pc-2" tag=10 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="theme-pc-2" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "选择套餐（选中项应为绿色）"
#9 createComponent surface="theme-pc-2" tag=5 parent=10 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "选择套餐（选中项应为绿色）"
    "options" = [{"label": "基础版", "value": "basic"}, {"label": "专业版", "value": "pro"}, {"label": "企业版", "value": "enterprise"}]
    "value" = ""
#10 createComponent surface="theme-pc-2" tag=12 parent=5 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "basic"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#11 createComponent surface="theme-pc-2" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "基础版"
#12 createComponent surface="theme-pc-2" tag=14 parent=5 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "pro"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#13 createComponent surface="theme-pc-2" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "专业版"
#14 createComponent surface="theme-pc-2" tag=16 parent=5 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "enterprise"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#15 createComponent surface="theme-pc-2" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "企业版"
#16 createComponent surface="theme-pc-2" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4281257073"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#17 createComponent surface="theme-pc-2" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "确认购买（背景应为绿色）"
#18 updateData surface="theme-pc-2"
  data:
    "agree" = true
    "plan" = "pro"
#19 updateComponent surface="theme-pc-2" tag=4 view="KRCheckBoxView"
  props:
    "checked" = true
#20 updateComponent surface="theme-pc-2" tag=12 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#21 updateComponent surface="theme-pc-2" tag=13 view="KRRichTextView"
  props:
    "color" = "#333333"
#22 updateComponent surface="theme-pc-2" tag=14 view="KRView"
  props:
    "backgroundColor" = "#2ECC71"
    "border" = "0 solid 0"
#23 updateComponent surface="theme-pc-2" tag=15 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#24 updateComponent surface="theme-pc-2" tag=16 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#25 updateComponent surface="theme-pc-2" tag=17 view="KRRichTextView"
  props:
    "color" = "#333333"
#state dataModel "{\"agree\":true,\"plan\":\"pro\"}"
#state actions {"6": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.02_agent_identity.1_icon_and_display_name""" to """#0 createSurface surface="theme-id-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-id-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-id-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-id-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.iconUrl / agentDisplayName 属规范身份归因字段。下列图标+名称的值来自 Data Model 镜像，用于人工核对。"
#4 createComponent surface="theme-id-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="theme-id-1" tag=5 parent=4 index=0 view="KRImageView"
  props:
    "height" = 24
    "margin" = 0
    "src" = ""
    "width" = 24
#6 createComponent surface="theme-id-1" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 20.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = "  "
#7 createComponent surface="theme-id-1" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4285291751"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="theme-id-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "primaryColor=#6C5CE7 主按钮"
#9 updateData surface="theme-id-1"
  data:
    "mirror" = {"agentDisplayName": "Weather Bot", "iconUrl": "https://a2ui.org/logo.png"}
#10 updateComponent surface="theme-id-1" tag=6 view="KRRichTextView"
  props:
    "text" = "  Weather Bot"
#11 updateComponent surface="theme-id-1" tag=5 view="KRImageView"
  props:
    "src" = "https://a2ui.org/logo.png"
#state dataModel "{\"mirror\":{\"iconUrl\":\"https://a2ui.org/logo.png\",\"agentDisplayName\":\"Weather Bot\"}}"
#state actions {"7": "{\"event\":{\"name\":\"openProfile\"}}"}
""",
        """v0_9.theme.03_theme_all_fields.1_full_theme_with_extras""" to """#0 createSurface surface="theme-all-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-all-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-all-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-all-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "本用例的 theme 同时携带三项标准字段 + 两项自定义字段（brandName/cornerRadius）。自定义字段被 basic catalog 忽略但通过 schema 校验。"
#4 createComponent surface="theme-all-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Agent: \n主题色: \n扩展 brandName: \n扩展 cornerRadius: "
#5 createComponent surface="theme-all-1" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294155282"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="theme-all-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary 按钮（背景应为橙 #F39C12）"
#7 updateData surface="theme-all-1"
  data:
    "mirror" = {"agentDisplayName": "Finance Assistant", "brandName": "Kuikly A2UI", "cornerRadius": 12, "iconUrl": "https://a2ui.org/agents/finance.png", "primaryColor": "#F39C12"}
#8 updateComponent surface="theme-all-1" tag=4 view="KRRichTextView"
  props:
    "text" = "Agent: Finance Assistant\n主题色: #F39C12\n扩展 brandName: Kuikly A2UI\n扩展 cornerRadius: 12"
#state dataModel "{\"mirror\":{\"primaryColor\":\"#F39C12\",\"iconUrl\":\"https://a2ui.org/agents/finance.png\",\"agentDisplayName\":\"Finance Assistant\",\"brandName\":\"Kuikly A2UI\",\"cornerRadius\":12}}"
#state actions {"5": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.04_invalid_color_fallback.7_uppercase_hex_accepted""" to """#0 createSurface surface="theme-inv-7"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-inv-7" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-inv-7" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-inv-7" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.primaryColor='#E91E63' 是合法 6 位大写 hex，应被接受。下方 Primary Button 背景应为品红 #E91E63，而不是默认浅蓝。"
#4 createComponent surface="theme-inv-7" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4293467747"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-inv-7" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（背景应为品红 #E91E63）"
#state dataModel "{}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.05_default_no_theme.1_no_theme_uses_default""" to """#0 createSurface surface="theme-dft-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-dft-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-dft-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-dft-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "createSurface 未携带 theme。primaryColor 应走协议默认值 #00BFFF（浅蓝），Primary 按钮与 Slider 进度色应为浅蓝。"
#4 createComponent surface="theme-dft-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-dft-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（默认浅蓝 #00BFFF）"
#6 createComponent surface="theme-dft-1" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="theme-dft-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Slider 进度色应为浅蓝"
#8 createComponent surface="theme-dft-1" tag=6 parent=7 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "level"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#9 updateData surface="theme-dft-1"
  data:
    "level" = 45
#10 updateComponent surface="theme-dft-1" tag=6 view="KRSliderView"
  props:
    "value" = 45
#11 updateComponent surface="theme-dft-1" tag=8 view="KRRichTextView"
  props:
    "text" = "Slider 进度色应为浅蓝"
#state dataModel "{\"level\":45}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.06_extreme_colors.1_black_primary""" to """#0 createSurface surface="theme-ext-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-ext-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-ext-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-ext-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.primaryColor=#000000（纯黑）。目视验证：Primary Button 背景黑、文字仍可读；Slider progressColor 黑色，与浅色轨道对比清晰。"
#4 createComponent surface="theme-ext-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278190080"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-ext-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（黑底白字应可读）"
#6 createComponent surface="theme-ext-1" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="theme-ext-1" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Slider 进度色应为纯黑"
#8 createComponent surface="theme-ext-1" tag=6 parent=7 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "level"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278190080"
    "value" = ""
#9 updateData surface="theme-ext-1"
  data:
    "level" = 60
#10 updateComponent surface="theme-ext-1" tag=6 view="KRSliderView"
  props:
    "value" = 60
#11 updateComponent surface="theme-ext-1" tag=8 view="KRRichTextView"
  props:
    "text" = "Slider 进度色应为纯黑"
#state dataModel "{\"level\":60}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.06_extreme_colors.2_white_primary""" to """#0 createSurface surface="theme-ext-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-ext-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-ext-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-ext-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.primaryColor=#FFFFFF（纯白）——极端边界。Primary Button 白底白字几乎不可读、Slider 进度色难辨。此用例用于让业务侧感知主题色对比度不当的后果，不代表实现缺陷。"
#4 createComponent surface="theme-ext-2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294967295"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-ext-2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（白底 · 对比度极低）"
#6 createComponent surface="theme-ext-2" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#7 createComponent surface="theme-ext-2" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Slider 进度色应为纯白"
#8 createComponent surface="theme-ext-2" tag=6 parent=7 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "level"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4294967295"
    "value" = ""
#9 updateData surface="theme-ext-2"
  data:
    "level" = 60
#10 updateComponent surface="theme-ext-2" tag=6 view="KRSliderView"
  props:
    "value" = 60
#11 updateComponent surface="theme-ext-2" tag=8 view="KRRichTextView"
  props:
    "text" = "Slider 进度色应为纯白"
#state dataModel "{\"level\":60}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.07_button_variant_isolation.1_default_borderless_not_tinted""" to """#0 createSurface surface="theme-vari-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-vari-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-vari-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-vari-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.primaryColor=#E91E63。仅 variant=primary 的按钮背景应为品红，default 与 borderless 应保持中性外观，不被主题色误染。"
#4 createComponent surface="theme-vari-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4293467747"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="theme-vari-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "primary（背景应为品红）"
#6 createComponent surface="theme-vari-1" tag=6 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#7 createComponent surface="theme-vari-1" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "default（不应被主题色染背景）"
#8 createComponent surface="theme-vari-1" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#9 createComponent surface="theme-vari-1" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "borderless（不应被主题色染背景）"
#state dataModel "{}"
#state actions {"4": "{\"event\":{\"name\":\"themeTestClick\"}}", "6": "{\"event\":{\"name\":\"themeTestClick\"}}", "8": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.08_agent_identity_edge.1_long_display_name""" to """#0 createSurface surface="theme-agent-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-agent-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-agent-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-agent-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Agent 名称包含 emoji + 中英混排 + 超长文本。引擎未内置消费 agentDisplayName，此处通过 Data Model 镜像到 Text 显示，用于目视验证。"
#4 createComponent surface="theme-agent-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 32.0
    "fontWeight" = "bold"
    "margin" = 8
    "text" = ""
#5 createComponent surface="theme-agent-2" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4284955319"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="theme-agent-2" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（背景应为紫 #673AB7）"
#7 updateData surface="theme-agent-2"
  data:
    "mirror" = {"agentDisplayName": "🤖 Kuikly Long-Name Assistant · 长名助手 · very-long-display-name-for-edge-case-🚀🌟✨"}
#8 updateComponent surface="theme-agent-2" tag=4 view="KRRichTextView"
  props:
    "text" = "🤖 Kuikly Long-Name Assistant · 长名助手 · very-long-display-name-for-edge-case-🚀🌟✨"
#state dataModel "{\"mirror\":{\"agentDisplayName\":\"🤖 Kuikly Long-Name Assistant · 长名助手 · very-long-display-name-for-edge-case-🚀🌟✨\"}}"
#state actions {"5": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v0_9.theme.08_agent_identity_edge.2_data_uri_icon""" to """#0 createSurface surface="theme-agent-3"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId=null
#1 createComponent surface="theme-agent-3" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="theme-agent-3" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="theme-agent-3" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "theme.iconUrl 使用 data:image/png;base64,... 内联图片（1×1 蓝色像素）。Image 组件应能渲染，且不因非 http(s) URL 崩溃。"
#4 createComponent surface="theme-agent-3" tag=4 parent=2 index=1 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = ""
    "width" = 40
#5 createComponent surface="theme-agent-3" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="theme-agent-3" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278228616"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#7 createComponent surface="theme-agent-3" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Button（背景应为青 #009688）"
#8 updateData surface="theme-agent-3"
  data:
    "mirror" = {"agentDisplayName": "Data URI Icon Agent", "iconUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="}
#9 updateComponent surface="theme-agent-3" tag=4 view="KRImageView"
  props:
    "src" = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
#10 updateComponent surface="theme-agent-3" tag=5 view="KRRichTextView"
  props:
    "text" = "Data URI Icon Agent"
#state dataModel "{\"mirror\":{\"iconUrl\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=\",\"agentDisplayName\":\"Data URI Icon Agent\"}}"
#state actions {"6": "{\"event\":{\"name\":\"themeTestClick\"}}"}
""",
        """v1_0.binding.03_child_list_template.1_column_template""" to """#0 createSurface surface="db-cl-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="db-cl-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="db-cl-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="db-cl-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### 今日 TODO"
#4 createComponent surface="db-cl-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 updateData surface="db-cl-1"
  data:
    "todos" = [{"done": true, "title": "阅读 A2UI 协议规范"}, {"done": false, "title": "补齐数据绑定测试用例"}, {"done": false, "title": "跑通 Android / iOS 双端"}]
#6 createComponent surface="db-cl-1" tag=6 parent=4 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 阅读 A2UI 协议规范"
#7 createComponent surface="db-cl-1" tag=7 parent=4 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 补齐数据绑定测试用例"
#8 createComponent surface="db-cl-1" tag=8 parent=4 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "• 跑通 Android / iOS 双端"
#state dataModel "{\"todos\":[{\"title\":\"阅读 A2UI 协议规范\",\"done\":true},{\"title\":\"补齐数据绑定测试用例\",\"done\":false},{\"title\":\"跑通 Android / iOS 双端\",\"done\":false}]}"
#state actions {}
""",
        """v1_0.component.list.1_list_direction_vertical""" to """#0 createSurface surface="test-list-vertical"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-vertical" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-vertical" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-vertical" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 1"
#4 createComponent surface="test-list-vertical" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 2"
#5 createComponent surface="test-list-vertical" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 3"
#6 createComponent surface="test-list-vertical" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 4"
#7 createComponent surface="test-list-vertical" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 5"
#8 createComponent surface="test-list-vertical" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 6"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.component.text.4_text_body""" to """#0 createSurface surface="test-text-body"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-body" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-body" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-body" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "This is body text with variant=\"body\" (16dp)."
#4 createComponent surface="test-text-body" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "This is text WITHOUT variant set (should default to body)."
#5 createComponent surface="test-text-body" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Long body text to verify wrapping. The quick brown fox jumps over the lazy dog. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
#state dataModel "{}"
#state actions {}
""",
        """v1_0.component.text.6_text_path_binding""" to """#0 createSurface surface="test-text-path"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-path" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-path" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-path" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### "
#4 createComponent surface="test-text-path" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#5 updateData surface="test-text-path"
  data:
    "subtitle" = "This caption text is also bound via /subtitle"
    "title" = "Bound Title from Data Model"
#6 updateComponent surface="test-text-path" tag=3 view="KRRichTextView"
  props:
    "text" = "### Bound Title from Data Model"
#7 updateComponent surface="test-text-path" tag=4 view="KRRichTextView"
  props:
    "text" = "This caption text is also bound via /subtitle"
#state dataModel "{\"title\":\"Bound Title from Data Model\",\"subtitle\":\"This caption text is also bound via /subtitle\"}"
#state actions {}
""",
        """v1_0.component.textfield.6_textfield_email_checks_vs_legacy_regexp""" to """#0 createSurface surface="test-tf-regexp"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-regexp" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-regexp" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tf-regexp" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-tf-regexp" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (email via @checks)"
#5 createComponent surface="test-tf-regexp" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#6 createComponent surface="test-tf-regexp" tag=7 parent=5 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#7 createComponent surface="test-tf-regexp" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="test-tf-regexp" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (regex via @checks)"
#9 createComponent surface="test-tf-regexp" tag=4 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "emailRegexp"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#10 createComponent surface="test-tf-regexp" tag=10 parent=8 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Please enter an address matching the original email pattern"
#11 updateData surface="test-tf-regexp"
  data:
    "email" = ""
    "emailRegexp" = ""
#12 updateComponent surface="test-tf-regexp" tag=3 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#13 updateComponent surface="test-tf-regexp" tag=6 view="KRRichTextView"
  props:
    "text" = "Email (email via @checks)"
#14 updateComponent surface="test-tf-regexp" tag=4 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#15 updateComponent surface="test-tf-regexp" tag=9 view="KRRichTextView"
  props:
    "text" = "Email (regex via @checks)"
#16 updateComponent surface="test-tf-regexp" tag=7 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#17 updateComponent surface="test-tf-regexp" tag=10 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#state dataModel "{\"email\":\"\",\"emailRegexp\":\"\"}"
#state actions {}
""",
        """v1_0.examples.04_weather-current""" to """#0 createSurface surface="gallery-weather-current"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="gallery-weather-current" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="gallery-weather-current" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="gallery-weather-current" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#4 createComponent surface="gallery-weather-current" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "# °"
#5 createComponent surface="gallery-weather-current" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "## °"
#6 createComponent surface="gallery-weather-current" tag=6 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### "
#7 createComponent surface="gallery-weather-current" tag=7 parent=2 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#8 createComponent surface="gallery-weather-current" tag=8 parent=2 index=3 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#9 updateData surface="gallery-weather-current"
  data:
    "description" = "Clear skies with light breeze"
    "forecast" = [{"date": "2025-12-16", "icon": "☀️", "temp": 74}, {"date": "2025-12-17", "icon": "☀️", "temp": 76}, {"date": "2025-12-18", "icon": "⛅", "temp": 71}, {"date": "2025-12-19", "icon": "☀️", "temp": 73}, {"date": "2025-12-20", "icon": "☀️", "temp": 75}]
    "location" = "Austin, TX"
    "tempHigh" = 72
    "tempLow" = 58
#10 createComponent surface="gallery-weather-current" tag=13 parent=8 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#11 createComponent surface="gallery-weather-current" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Tue"
#12 createComponent surface="gallery-weather-current" tag=15 parent=13 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### ☀️"
#13 createComponent surface="gallery-weather-current" tag=16 parent=13 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "74°"
#14 createComponent surface="gallery-weather-current" tag=17 parent=8 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#15 createComponent surface="gallery-weather-current" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Wed"
#16 createComponent surface="gallery-weather-current" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### ☀️"
#17 createComponent surface="gallery-weather-current" tag=20 parent=17 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "76°"
#18 createComponent surface="gallery-weather-current" tag=21 parent=8 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#19 createComponent surface="gallery-weather-current" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Thu"
#20 createComponent surface="gallery-weather-current" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### ⛅"
#21 createComponent surface="gallery-weather-current" tag=24 parent=21 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "71°"
#22 createComponent surface="gallery-weather-current" tag=25 parent=8 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#23 createComponent surface="gallery-weather-current" tag=26 parent=25 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Fri"
#24 createComponent surface="gallery-weather-current" tag=27 parent=25 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### ☀️"
#25 createComponent surface="gallery-weather-current" tag=28 parent=25 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "73°"
#26 createComponent surface="gallery-weather-current" tag=29 parent=8 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#27 createComponent surface="gallery-weather-current" tag=30 parent=29 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Sat"
#28 createComponent surface="gallery-weather-current" tag=31 parent=29 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### ☀️"
#29 createComponent surface="gallery-weather-current" tag=32 parent=29 index=2 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "75°"
#30 updateComponent surface="gallery-weather-current" tag=4 view="KRRichTextView"
  props:
    "text" = "# 72°"
#31 updateComponent surface="gallery-weather-current" tag=5 view="KRRichTextView"
  props:
    "text" = "## 58°"
#32 updateComponent surface="gallery-weather-current" tag=6 view="KRRichTextView"
  props:
    "text" = "### Austin, TX"
#33 updateComponent surface="gallery-weather-current" tag=7 view="KRRichTextView"
  props:
    "text" = "Clear skies with light breeze"
#state dataModel "{\"tempHigh\":72,\"tempLow\":58,\"location\":\"Austin, TX\",\"description\":\"Clear skies with light breeze\",\"forecast\":[{\"date\":\"2025-12-16\",\"icon\":\"☀️\",\"temp\":74},{\"date\":\"2025-12-17\",\"icon\":\"☀️\",\"temp\":76},{\"date\":\"2025-12-18\",\"icon\":\"⛅\",\"temp\":71},{\"date\":\"2025-12-19\",\"icon\":\"☀️\",\"temp\":73},{\"date\":\"2025-12-20\",\"icon\":\"☀️\",\"temp\":75}]}"
#state actions {}
""",
        """v1_0.formal.component.audioplayer.1_audioplayer_literal_url""" to """#0 createSurface surface="test-audio-literal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-audio-literal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-literal" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-audio-literal" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Sample audio track"
#4 createComponent surface="test-audio-literal" tag=2 parent=3 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.audioplayer.2_audioplayer_url_binding""" to """#0 createSurface surface="test-audio-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-audio-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-binding" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-audio-binding" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = ""
#4 createComponent surface="test-audio-binding" tag=2 parent=3 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = ""
#5 updateData surface="test-audio-binding"
  data:
    "audio" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    "title" = "Bound Track"
#6 updateComponent surface="test-audio-binding" tag=2 view="KRAudioView"
  props:
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#7 updateComponent surface="test-audio-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Bound Track"
#state dataModel "{\"audio\":\"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3\",\"title\":\"Bound Track\"}"
#state actions {}
""",
        """v1_0.formal.component.audioplayer.3_audioplayer_in_column""" to """#0 createSurface surface="test-audio-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-audio-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-audio-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-audio-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Now Playing"
#4 createComponent surface="test-audio-column" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### SoundHelix Song 1"
#5 createComponent surface="test-audio-column" tag=6 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="test-audio-column" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "SoundHelix Song 1"
#7 createComponent surface="test-audio-column" tag=5 parent=6 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.button.1_button_variant_default""" to """#0 createSurface surface="test-btn-default"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-btn-default" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-default" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-default" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Default Button"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"defaultClicked\"}}"}
""",
        """v1_0.formal.component.button.2_button_variant_primary""" to """#0 createSurface surface="test-btn-primary"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-btn-primary" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-primary" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-primary" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Primary Action"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"primaryClicked\"}}"}
""",
        """v1_0.formal.component.button.3_button_variant_borderless""" to """#0 createSurface surface="test-btn-borderless"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-btn-borderless" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-borderless" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-borderless" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Link Style"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"linkClicked\"}}"}
""",
        """v1_0.formal.component.button.4_button_icon_only""" to """#0 createSurface surface="test-btn-icon"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-btn-icon" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-icon" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-btn-icon" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#4 createComponent surface="test-btn-icon" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔗"
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="test-btn-icon" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="test-btn-icon" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "❤"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-btn-icon" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#8 createComponent surface="test-btn-icon" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🗑"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {"3": "{\"event\":{\"name\":\"shareClicked\"}}", "5": "{\"event\":{\"name\":\"favClicked\"}}", "7": "{\"event\":{\"name\":\"deleteClicked\"}}"}
""",
        """v1_0.formal.component.button.5_button_action_with_context""" to """#0 createSurface surface="test-btn-context"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-btn-context" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-btn-context" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#3 createComponent surface="test-btn-context" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Buy Now"
#state dataModel "{}"
#state actions {"2": "{\"event\":{\"name\":\"buyItem\",\"context\":{\"itemId\":\"SKU-001\",\"quantity\":2}}}"}
""",
        """v1_0.formal.component.card.1_card_single_text_child""" to """#0 createSurface surface="test-card-single-text"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-card-single-text" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-single-text" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Card with a single Text child"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.card.2_card_with_column""" to """#0 createSurface surface="test-card-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-card-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-card-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Card title"
#4 createComponent surface="test-card-column" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Card body. Use Column to host multiple children."
#5 createComponent surface="test-card-column" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "footer"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.card.3_card_with_row""" to """#0 createSurface surface="test-card-row"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-card-row" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-row" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="test-card-row" tag=3 parent=2 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#4 createComponent surface="test-card-row" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#5 createComponent surface="test-card-row" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### John Doe"
#6 createComponent surface="test-card-row" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Software Engineer"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.card.4_card_nested_cards""" to """#0 createSurface surface="test-card-nested"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-card-nested" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-card-nested" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-card-nested" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Outer Card"
#4 createComponent surface="test-card-nested" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#5 createComponent surface="test-card-nested" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Inner Card A"
#6 createComponent surface="test-card-nested" tag=6 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#7 createComponent surface="test-card-nested" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Inner Card B"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.checkbox.1_checkbox_value_true""" to """#0 createSurface surface="test-cb-true"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cb-true" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-true" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#3 createComponent surface="test-cb-true" tag=2 parent=3 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#4 createComponent surface="test-cb-true" tag=4 parent=3 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "I agree (initially checked)"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.checkbox.2_checkbox_value_false""" to """#0 createSurface surface="test-cb-false"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cb-false" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-false" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#3 createComponent surface="test-cb-false" tag=2 parent=3 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "false"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#4 createComponent surface="test-cb-false" tag=4 parent=3 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Subscribe to newsletter"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.checkbox.3_checkbox_value_binding""" to """#0 createSurface surface="test-cb-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cb-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cb-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#4 createComponent surface="test-cb-binding" tag=3 parent=5 index=0 view="KRCheckBoxView"
  props:
    "__checkBoxBindingPath" = "dark"
    "__primaryColor" = "4278239231"
    "checked" = ""
    "height" = 24
    "marginRight" = 8
    "width" = 24
#5 createComponent surface="test-cb-binding" tag=6 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Enable dark mode"
#6 createComponent surface="test-cb-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Dark mode is: "
#7 updateData surface="test-cb-binding"
  data:
    "dark" = false
#8 updateComponent surface="test-cb-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Dark mode is: false"
#9 updateComponent surface="test-cb-binding" tag=3 view="KRCheckBoxView"
  props:
    "checked" = false
#state dataModel "{\"dark\":false}"
#state actions {}
""",
        """v1_0.formal.component.checkbox.4_checkbox_multiple""" to """#0 createSurface surface="test-cb-multi"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cb-multi" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cb-multi" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cb-multi" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Preferences"
#4 createComponent surface="test-cb-multi" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#5 createComponent surface="test-cb-multi" tag=4 parent=7 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#6 createComponent surface="test-cb-multi" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive email"
#7 createComponent surface="test-cb-multi" tag=9 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#8 createComponent surface="test-cb-multi" tag=5 parent=9 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "false"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#9 createComponent surface="test-cb-multi" tag=10 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive SMS"
#10 createComponent surface="test-cb-multi" tag=11 parent=2 index=3 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "margin" = 8
#11 createComponent surface="test-cb-multi" tag=6 parent=11 index=0 view="KRCheckBoxView"
  props:
    "__primaryColor" = "4278239231"
    "checked" = "true"
    "height" = 24
    "marginRight" = 8
    "width" = 24
#12 createComponent surface="test-cb-multi" tag=12 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#1A1A1A"
    "flex" = 1
    "fontSize" = 16.0
    "text" = "Receive push"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.1_choicepicker_mutually_exclusive_checkbox""" to """#0 createSurface surface="test-cp-mut-cb"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-mut-cb" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-mut-cb" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-mut-cb" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Size"
#4 createComponent surface="test-cp-mut-cb" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "flexDirection" = "column"
    "label" = "Size"
    "options" = [{"label": "Small", "value": "S"}, {"label": "Medium", "value": "M"}, {"label": "Large", "value": "L"}]
    "value" = ["M"]
    "variant" = "mutuallyExclusive"
#5 createComponent surface="test-cp-mut-cb" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "S"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-mut-cb" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#7 createComponent surface="test-cp-mut-cb" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Small"
#8 createComponent surface="test-cp-mut-cb" tag=8 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "M"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-mut-cb" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "●"
#10 createComponent surface="test-cp-mut-cb" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Medium"
#11 createComponent surface="test-cp-mut-cb" tag=11 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "L"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-mut-cb" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#13 createComponent surface="test-cp-mut-cb" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Large"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.2_choicepicker_multiple_selection_checkbox""" to """#0 createSurface surface="test-cp-multi-cb"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-multi-cb" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-multi-cb" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-multi-cb" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Languages I speak"
#4 createComponent surface="test-cp-multi-cb" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "flexDirection" = "column"
    "label" = "Languages I speak"
    "options" = [{"label": "English", "value": "en"}, {"label": "中文", "value": "zh"}, {"label": "Français", "value": "fr"}, {"label": "Deutsch", "value": "de"}, {"label": "Español", "value": "es"}]
    "value" = ["en", "zh"]
    "variant" = "multipleSelection"
#5 createComponent surface="test-cp-multi-cb" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "en"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-multi-cb" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "☑"
#7 createComponent surface="test-cp-multi-cb" tag=7 parent=5 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "English"
#8 createComponent surface="test-cp-multi-cb" tag=8 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "zh"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-multi-cb" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "☑"
#10 createComponent surface="test-cp-multi-cb" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "中文"
#11 createComponent surface="test-cp-multi-cb" tag=11 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "fr"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-multi-cb" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#13 createComponent surface="test-cp-multi-cb" tag=13 parent=11 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Français"
#14 createComponent surface="test-cp-multi-cb" tag=14 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "de"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#15 createComponent surface="test-cp-multi-cb" tag=15 parent=14 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#16 createComponent surface="test-cp-multi-cb" tag=16 parent=14 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Deutsch"
#17 createComponent surface="test-cp-multi-cb" tag=17 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "es"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#18 createComponent surface="test-cp-multi-cb" tag=18 parent=17 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "□"
#19 createComponent surface="test-cp-multi-cb" tag=19 parent=17 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Español"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.3_choicepicker_mutually_exclusive_chips""" to """#0 createSurface surface="test-cp-mut-chips"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-mut-chips" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-mut-chips" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-mut-chips" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Plan"
#4 createComponent surface="test-cp-mut-chips" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Plan"
    "options" = [{"label": "Free", "value": "free"}, {"label": "Pro", "value": "pro"}, {"label": "Team", "value": "team"}]
    "value" = ["pro"]
    "variant" = "mutuallyExclusive"
#5 createComponent surface="test-cp-mut-chips" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "free"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-mut-chips" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Free"
#7 createComponent surface="test-cp-mut-chips" tag=7 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "pro"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#8 createComponent surface="test-cp-mut-chips" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Pro"
#9 createComponent surface="test-cp-mut-chips" tag=9 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "team"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-mut-chips" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Team"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.4_choicepicker_multiple_selection_chips""" to """#0 createSurface surface="test-cp-multi-chips"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-multi-chips" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-multi-chips" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-multi-chips" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Tags"
#4 createComponent surface="test-cp-multi-chips" tag=2 parent=3 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Tags"
    "options" = [{"label": "Kotlin", "value": "kotlin"}, {"label": "Swift", "value": "swift"}, {"label": "ArkTS", "value": "arkts"}, {"label": "JS", "value": "js"}, {"label": "Compose", "value": "compose"}]
    "value" = ["kotlin", "compose"]
    "variant" = "multipleSelection"
#5 createComponent surface="test-cp-multi-chips" tag=5 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "kotlin"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#6 createComponent surface="test-cp-multi-chips" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Kotlin"
#7 createComponent surface="test-cp-multi-chips" tag=7 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "swift"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#8 createComponent surface="test-cp-multi-chips" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Swift"
#9 createComponent surface="test-cp-multi-chips" tag=9 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "arkts"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-multi-chips" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "ArkTS"
#11 createComponent surface="test-cp-multi-chips" tag=11 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "js"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#12 createComponent surface="test-cp-multi-chips" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "JS"
#13 createComponent surface="test-cp-multi-chips" tag=13 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "compose"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4278239231"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#14 createComponent surface="test-cp-multi-chips" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 14.0
    "text" = "Compose"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.5_choicepicker_filterable""" to """#0 createSurface surface="test-cp-filterable"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-filterable" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-filterable" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-cp-filterable" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Country"
#4 createComponent surface="test-cp-filterable" tag=5 parent=3 index=1 view="KRTextFieldView"
  props:
    "__choicePickerFilterId" = "cp"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = 8.0
    "fontSize" = 14.0
    "height" = 36.0
    "marginBottom" = 8.0
    "paddingLeft" = 10.0
    "paddingRight" = 10.0
    "placeholder" = ""
#5 createComponent surface="test-cp-filterable" tag=2 parent=3 index=2 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "displayStyle" = "checkbox"
    "filterable" = true
    "flexDirection" = "column"
    "label" = "Country"
    "options" = [{"label": "China", "value": "china"}, {"label": "United States", "value": "united states"}, {"label": "Japan", "value": "japan"}, {"label": "France", "value": "france"}, {"label": "Germany", "value": "germany"}, {"label": "India", "value": "india"}, {"label": "Brazil", "value": "brazil"}, {"label": "Australia", "value": "australia"}]
    "value" = ["china"]
    "variant" = "mutuallyExclusive"
#6 createComponent surface="test-cp-filterable" tag=6 parent=2 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "china"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#7 createComponent surface="test-cp-filterable" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "#00BFFF"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "●"
#8 createComponent surface="test-cp-filterable" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "China"
#9 createComponent surface="test-cp-filterable" tag=9 parent=2 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "united states"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#10 createComponent surface="test-cp-filterable" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#11 createComponent surface="test-cp-filterable" tag=11 parent=9 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "United States"
#12 createComponent surface="test-cp-filterable" tag=12 parent=2 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "japan"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#13 createComponent surface="test-cp-filterable" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#14 createComponent surface="test-cp-filterable" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Japan"
#15 createComponent surface="test-cp-filterable" tag=15 parent=2 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "france"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#16 createComponent surface="test-cp-filterable" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#17 createComponent surface="test-cp-filterable" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "France"
#18 createComponent surface="test-cp-filterable" tag=18 parent=2 index=4 view="KRView"
  props:
    "__choiceOptionValue" = "germany"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#19 createComponent surface="test-cp-filterable" tag=19 parent=18 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#20 createComponent surface="test-cp-filterable" tag=20 parent=18 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Germany"
#21 createComponent surface="test-cp-filterable" tag=21 parent=2 index=5 view="KRView"
  props:
    "__choiceOptionValue" = "india"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#22 createComponent surface="test-cp-filterable" tag=22 parent=21 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#23 createComponent surface="test-cp-filterable" tag=23 parent=21 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "India"
#24 createComponent surface="test-cp-filterable" tag=24 parent=2 index=6 view="KRView"
  props:
    "__choiceOptionValue" = "brazil"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#25 createComponent surface="test-cp-filterable" tag=25 parent=24 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#26 createComponent surface="test-cp-filterable" tag=26 parent=24 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Brazil"
#27 createComponent surface="test-cp-filterable" tag=27 parent=2 index=7 view="KRView"
  props:
    "__choiceOptionValue" = "australia"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "mutuallyExclusive"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingBottom" = 8.0
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
    "paddingTop" = 8.0
    "touchEnable" = 1
#28 createComponent surface="test-cp-filterable" tag=28 parent=27 index=0 view="KRRichTextView"
  props:
    "color" = "#999999"
    "fontSize" = 18.0
    "marginRight" = 8.0
    "text" = "○"
#29 createComponent surface="test-cp-filterable" tag=29 parent=27 index=1 view="KRRichTextView"
  props:
    "color" = "#333333"
    "fontSize" = 14.0
    "text" = "Australia"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.choicepicker.6_choicepicker_value_binding""" to """#0 createSurface surface="test-cp-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-cp-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-cp-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-cp-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-cp-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Topics"
#5 createComponent surface="test-cp-binding" tag=3 parent=5 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "displayStyle" = "chips"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "label" = "Topics"
    "options" = [{"label": "Sports", "value": "sports"}, {"label": "News", "value": "news"}, {"label": "Tech", "value": "tech"}, {"label": "Movies", "value": "movies"}]
    "value" = ""
    "variant" = "multipleSelection"
#6 createComponent surface="test-cp-binding" tag=7 parent=3 index=0 view="KRView"
  props:
    "__choiceOptionValue" = "sports"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#7 createComponent surface="test-cp-binding" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Sports"
#8 createComponent surface="test-cp-binding" tag=9 parent=3 index=1 view="KRView"
  props:
    "__choiceOptionValue" = "news"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#9 createComponent surface="test-cp-binding" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "News"
#10 createComponent surface="test-cp-binding" tag=11 parent=3 index=2 view="KRView"
  props:
    "__choiceOptionValue" = "tech"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#11 createComponent surface="test-cp-binding" tag=12 parent=11 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Tech"
#12 createComponent surface="test-cp-binding" tag=13 parent=3 index=3 view="KRView"
  props:
    "__choiceOptionValue" = "movies"
    "__choicePickerParentId" = "cp"
    "__choicePickerVariant" = "multipleSelection"
    "alignItems" = "center"
    "alignSelf" = "flex-start"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4291611852"
    "borderRadius" = 16.0
    "justifyContent" = "center"
    "marginBottom" = 8.0
    "marginRight" = 8.0
    "paddingBottom" = 6.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 6.0
    "touchEnable" = 1
#13 createComponent surface="test-cp-binding" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4281545523"
    "fontSize" = 14.0
    "text" = "Movies"
#14 createComponent surface="test-cp-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Selected: "
#15 updateData surface="test-cp-binding"
  data:
    "picked" = ["tech", "movies"]
#16 updateComponent surface="test-cp-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Selected: [\"tech\",\"movies\"]"
#17 updateComponent surface="test-cp-binding" tag=7 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#18 updateComponent surface="test-cp-binding" tag=8 view="KRRichTextView"
  props:
    "color" = "#333333"
#19 updateComponent surface="test-cp-binding" tag=9 view="KRView"
  props:
    "backgroundColor" = "#FFFFFF"
    "border" = "1 solid 4291611852"
#20 updateComponent surface="test-cp-binding" tag=10 view="KRRichTextView"
  props:
    "color" = "#333333"
#21 updateComponent surface="test-cp-binding" tag=11 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#22 updateComponent surface="test-cp-binding" tag=12 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#23 updateComponent surface="test-cp-binding" tag=13 view="KRView"
  props:
    "backgroundColor" = "#00BFFF"
    "border" = "0 solid 0"
#24 updateComponent surface="test-cp-binding" tag=14 view="KRRichTextView"
  props:
    "color" = "#FFFFFF"
#state dataModel "{\"picked\":[\"tech\",\"movies\"]}"
#state actions {}
""",
        """v1_0.formal.component.column.1_column_justify_start""" to """#0 createSurface surface="test-col-just-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-just-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "flex-start"
#3 createComponent surface="test-col-just-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.2_column_justify_center""" to """#0 createSurface surface="test-col-just-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-just-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "center"
#3 createComponent surface="test-col-just-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.3_column_justify_end""" to """#0 createSurface surface="test-col-just-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-just-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "flex-end"
#3 createComponent surface="test-col-just-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.4_column_justify_spaceBetween""" to """#0 createSurface surface="test-col-just-between"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-just-between" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-just-between" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "justifyContent" = "space-between"
#3 createComponent surface="test-col-just-between" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "A — first item"
#4 createComponent surface="test-col-just-between" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B — second item"
#5 createComponent surface="test-col-just-between" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "C — third item"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.5_column_align_start""" to """#0 createSurface surface="test-col-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.6_column_align_center""" to """#0 createSurface surface="test-col-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "center"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.7_column_align_end""" to """#0 createSurface surface="test-col-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-align-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-col-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-col-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textAlignPostLayout" = "end"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer text in column"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.column.8_column_template_children""" to """#0 createSurface surface="test-col-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-col-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-col-template" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 updateData surface="test-col-template"
  data:
    "items" = [{"text": "1. First item from /items"}, {"text": "2. Second item from /items"}, {"text": "3. Third item from /items"}, {"text": "4. Fourth item from /items"}]
#4 createComponent surface="test-col-template" tag=4 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#5 createComponent surface="test-col-template" tag=5 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="test-col-template" tag=6 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="test-col-template" tag=7 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = ""
#state dataModel "{\"items\":[{\"text\":\"1. First item from /items\"},{\"text\":\"2. Second item from /items\"},{\"text\":\"3. Third item from /items\"},{\"text\":\"4. Fourth item from /items\"}]}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.1_datetimeinput_date_only""" to """#0 createSurface surface="test-dt-date"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-date" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-date" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-date" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Birthday"
#4 createComponent surface="test-dt-date" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Birthday"
    "value" = "1990-01-15"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.2_datetimeinput_time_only""" to """#0 createSurface surface="test-dt-time"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-time" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-time" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-time" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Reminder time"
#4 createComponent surface="test-dt-time" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = false
    "enableTime" = true
    "label" = "Reminder time"
    "value" = "09:00:00"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.3_datetimeinput_date_time""" to """#0 createSurface surface="test-dt-datetime"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-datetime" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-datetime" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-datetime" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Meeting"
#4 createComponent surface="test-dt-datetime" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Meeting"
    "value" = "2026-01-15T14:30:00"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.4_datetimeinput_min_max_constraints""" to """#0 createSurface surface="test-dt-range"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-range" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-range" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-range" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Booking date"
#4 createComponent surface="test-dt-range" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Booking date"
    "max" = "2026-12-31"
    "min" = "2026-06-01"
    "value" = "2026-06-20"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.5_datetimeinput_empty_value""" to """#0 createSurface surface="test-dt-empty"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-empty" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-empty" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-dt-empty" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Pick a date"
#4 createComponent surface="test-dt-empty" tag=2 parent=3 index=1 view="KRDateTimeInputView"
  props:
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = false
    "label" = "Pick a date"
    "value" = ""
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.datetimeinput.6_datetimeinput_value_binding""" to """#0 createSurface surface="test-dt-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-dt-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-dt-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-dt-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-dt-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Departure"
#5 createComponent surface="test-dt-binding" tag=3 parent=5 index=1 view="KRDateTimeInputView"
  props:
    "__dateTimeBindingPath" = "departure"
    "alignSelf" = "stretch"
    "enableDate" = true
    "enableTime" = true
    "label" = "Departure"
    "value" = ""
#6 createComponent surface="test-dt-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "ISO: "
#7 updateData surface="test-dt-binding"
  data:
    "departure" = "2026-12-24T08:00:00"
#8 updateComponent surface="test-dt-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "ISO: 2026-12-24T08:00:00"
#9 updateComponent surface="test-dt-binding" tag=3 view="KRDateTimeInputView"
  props:
    "value" = "2026-12-24T08:00:00"
#10 updateComponent surface="test-dt-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Departure"
#state dataModel "{\"departure\":\"2026-12-24T08:00:00\"}"
#state actions {}
""",
        """v1_0.formal.component.divider.1_divider_horizontal_default""" to """#0 createSurface surface="test-divider-h"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-divider-h" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-h" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-divider-h" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Above the line"
#4 createComponent surface="test-divider-h" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#5 createComponent surface="test-divider-h" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Below the line"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.divider.2_divider_vertical_in_row""" to """#0 createSurface surface="test-divider-v"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-divider-v" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-v" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#3 createComponent surface="test-divider-v" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Left"
#4 createComponent surface="test-divider-v" tag=4 parent=2 index=1 view="KRView"
  props:
    "__verticalDividerFollowParent" = true
    "backgroundColor" = "4292927712"
    "height" = 40
    "marginLeft" = 8
    "marginRight" = 8
    "width" = 1
#5 createComponent surface="test-divider-v" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Right"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.divider.3_divider_multiple_in_column""" to """#0 createSurface surface="test-divider-multi"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-divider-multi" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-divider-multi" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-divider-multi" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Section 1"
#4 createComponent surface="test-divider-multi" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#5 createComponent surface="test-divider-multi" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Section 2"
#6 createComponent surface="test-divider-multi" tag=6 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4292927712"
    "height" = 1
    "marginBottom" = 8
    "marginTop" = 8
#7 createComponent surface="test-divider-multi" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Section 3"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.1_icon_common_set_1""" to """#0 createSurface surface="test-icon-set1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-set1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-set1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-set1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: home, search, settings, favorite, star"
#4 createComponent surface="test-icon-set1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-set1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🏠"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-set1" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔍"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-set1" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⚙"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-set1" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "❤"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-set1" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "★"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.2_icon_common_set_2""" to """#0 createSurface surface="test-icon-set2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-set2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-set2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-set2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: add, close, check, menu, share"
#4 createComponent surface="test-icon-set2" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-set2" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "+"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-set2" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✕"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-set2" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✓"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-set2" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "☰"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-set2" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔗"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.3_icon_media_icons""" to """#0 createSurface surface="test-icon-media"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-media" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-media" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-media" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: play, pause, stop, skipNext, skipPrevious, volumeUp, volumeMute"
#4 createComponent surface="test-icon-media" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-media" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "▶"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-media" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏸"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-media" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏹"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-media" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏭"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-media" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "⏮"
    "textAlign" = "center"
    "width" = 24
#10 createComponent surface="test-icon-media" tag=10 parent=4 index=5 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔊"
    "textAlign" = "center"
    "width" = 24
#11 createComponent surface="test-icon-media" tag=11 parent=4 index=6 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔇"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.4_icon_communication_icons""" to """#0 createSurface surface="test-icon-comm"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-comm" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-comm" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-comm" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Icons: mail, phone, call, send, notifications"
#4 createComponent surface="test-icon-comm" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#5 createComponent surface="test-icon-comm" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✉"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-comm" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📱"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-comm" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "📞"
    "textAlign" = "center"
    "width" = 24
#8 createComponent surface="test-icon-comm" tag=8 parent=4 index=3 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "✈"
    "textAlign" = "center"
    "width" = 24
#9 createComponent surface="test-icon-comm" tag=9 parent=4 index=4 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "🔔"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.5_icon_svg_path""" to """#0 createSurface surface="test-icon-svg"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-svg" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-svg" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-icon-svg" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Custom SVG path icons (heart, lightning, smile)"
#4 createComponent surface="test-icon-svg" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#5 createComponent surface="test-icon-svg" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#6 createComponent surface="test-icon-svg" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#7 createComponent surface="test-icon-svg" tag=7 parent=4 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = "●"
    "textAlign" = "center"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.icon.6_icon_name_binding""" to """#0 createSurface surface="test-icon-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-icon-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-icon-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-icon-binding" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#4 createComponent surface="test-icon-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#5 createComponent surface="test-icon-binding" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "fontSize" = 20.0
    "margin" = 8
    "text" = ""
    "textAlign" = "center"
    "width" = 24
#6 updateData surface="test-icon-binding"
  data:
    "icon1" = "lockOpen"
    "icon2" = "visibility"
    "icon3" = "favorite"
#7 updateComponent surface="test-icon-binding" tag=3 view="KRRichTextView"
  props:
    "text" = "🔓"
#8 updateComponent surface="test-icon-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "👁"
#9 updateComponent surface="test-icon-binding" tag=5 view="KRRichTextView"
  props:
    "text" = "❤"
#state dataModel "{\"icon1\":\"lockOpen\",\"icon2\":\"visibility\",\"icon3\":\"favorite\"}"
#state actions {}
""",
        """v1_0.formal.component.image.1_image_variant_icon""" to """#0 createSurface surface="test-image-icon"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-icon" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-icon" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "height" = 24
    "margin" = 0
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 24
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.2_image_variant_avatar""" to """#0 createSurface surface="test-image-avatar"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-avatar" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-avatar" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop"
    "width" = 40
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.3_image_variant_smallFeature""" to """#0 createSurface surface="test-image-small"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-small" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-small" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.4_image_variant_mediumFeature""" to """#0 createSurface surface="test-image-medium"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-medium" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-medium" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.5_image_variant_largeFeature""" to """#0 createSurface surface="test-image-large"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-large" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-large" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 400
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.6_image_variant_header""" to """#0 createSurface surface="test-image-header"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-header" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-header" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__featureAutoSizeHeader" = true
    "__featureAutoSizeMaxHeightDp" = 200
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.7_image_fit_modes""" to """#0 createSurface surface="test-image-fit"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-fit" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-fit" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-image-fit" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=contain"
#4 createComponent surface="test-image-fit" tag=4 parent=2 index=1 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#5 createComponent surface="test-image-fit" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=cover"
#6 createComponent surface="test-image-fit" tag=6 parent=2 index=3 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#7 createComponent surface="test-image-fit" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=fill (default)"
#8 createComponent surface="test-image-fit" tag=8 parent=2 index=5 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "stretch"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#9 createComponent surface="test-image-fit" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=none"
#10 createComponent surface="test-image-fit" tag=10 parent=2 index=7 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#11 createComponent surface="test-image-fit" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "fit=scaleDown"
#12 createComponent surface="test-image-fit" tag=12 parent=2 index=9 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 100
    "__featureAutoSizeMaxWidthDp" = 100
    "height" = 1
    "margin" = 8
    "resizeMode" = "contain"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
    "width" = 1
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.image.8_image_url_path_binding""" to """#0 createSurface surface="test-image-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-image-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-image-binding" tag=2 parent=1 index=0 view="KRImageView"
  props:
    "__accessibility" = ""
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = ""
#3 updateData surface="test-image-binding"
  data:
    "alt" = "Bound image alt"
    "photoUrl" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#4 updateComponent surface="test-image-binding" tag=2 view="KRImageView"
  props:
    "__accessibility" = "Bound image alt"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#state dataModel "{\"photoUrl\":\"https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop\",\"alt\":\"Bound image alt\"}"
#state actions {}
""",
        """v1_0.formal.component.list.2_list_direction_horizontal""" to """#0 createSurface surface="test-list-horizontal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-horizontal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-horizontal" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "row"
#3 createComponent surface="test-list-horizontal" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#1"
#4 createComponent surface="test-list-horizontal" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#2"
#5 createComponent surface="test-list-horizontal" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#3"
#6 createComponent surface="test-list-horizontal" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#4"
#7 createComponent surface="test-list-horizontal" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#5"
#8 createComponent surface="test-list-horizontal" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#6"
#9 createComponent surface="test-list-horizontal" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#7"
#10 createComponent surface="test-list-horizontal" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#8"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.3_list_direction_vertical_long""" to """#0 createSurface surface="test-list-vertical"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-vertical" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-vertical" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-vertical" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 1"
#4 createComponent surface="test-list-vertical" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 2"
#5 createComponent surface="test-list-vertical" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 3"
#6 createComponent surface="test-list-vertical" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 4"
#7 createComponent surface="test-list-vertical" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 5"
#8 createComponent surface="test-list-vertical" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 6"
#9 createComponent surface="test-list-vertical" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 7"
#10 createComponent surface="test-list-vertical" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 8"
#11 createComponent surface="test-list-vertical" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 9"
#12 createComponent surface="test-list-vertical" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 10"
#13 createComponent surface="test-list-vertical" tag=13 parent=2 index=10 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 11"
#14 createComponent surface="test-list-vertical" tag=14 parent=2 index=11 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 12"
#15 createComponent surface="test-list-vertical" tag=15 parent=2 index=12 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 13"
#16 createComponent surface="test-list-vertical" tag=16 parent=2 index=13 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 14"
#17 createComponent surface="test-list-vertical" tag=17 parent=2 index=14 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 15"
#18 createComponent surface="test-list-vertical" tag=18 parent=2 index=15 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 16"
#19 createComponent surface="test-list-vertical" tag=19 parent=2 index=16 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 17"
#20 createComponent surface="test-list-vertical" tag=20 parent=2 index=17 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 18"
#21 createComponent surface="test-list-vertical" tag=21 parent=2 index=18 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 19"
#22 createComponent surface="test-list-vertical" tag=22 parent=2 index=19 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 20"
#23 createComponent surface="test-list-vertical" tag=23 parent=2 index=20 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 21"
#24 createComponent surface="test-list-vertical" tag=24 parent=2 index=21 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Vertical Item 22"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.4_list_direction_horizontal_long""" to """#0 createSurface surface="test-list-horizontal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-horizontal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-horizontal" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "row"
#3 createComponent surface="test-list-horizontal" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#1"
#4 createComponent surface="test-list-horizontal" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#2"
#5 createComponent surface="test-list-horizontal" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#3"
#6 createComponent surface="test-list-horizontal" tag=6 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#4"
#7 createComponent surface="test-list-horizontal" tag=7 parent=2 index=4 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#5"
#8 createComponent surface="test-list-horizontal" tag=8 parent=2 index=5 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#6"
#9 createComponent surface="test-list-horizontal" tag=9 parent=2 index=6 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#7"
#10 createComponent surface="test-list-horizontal" tag=10 parent=2 index=7 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#8"
#11 createComponent surface="test-list-horizontal" tag=11 parent=2 index=8 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#9"
#12 createComponent surface="test-list-horizontal" tag=12 parent=2 index=9 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#10"
#13 createComponent surface="test-list-horizontal" tag=13 parent=2 index=10 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#11"
#14 createComponent surface="test-list-horizontal" tag=14 parent=2 index=11 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#12"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.5_list_align_start""" to """#0 createSurface surface="test-list-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-start" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.6_list_align_center""" to """#0 createSurface surface="test-list-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-center" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.7_list_align_end""" to """#0 createSurface surface="test-list-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-end" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.8_list_align_stretch""" to """#0 createSurface surface="test-list-align-stretch"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-align-stretch" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-align-stretch" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-list-align-stretch" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Short"
#4 createComponent surface="test-list-align-stretch" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Longer item"
#5 createComponent surface="test-list-align-stretch" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Even longer item text"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.list.9_list_template_children""" to """#0 createSurface surface="test-list-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-list-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-list-template" tag=2 parent=1 index=0 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 updateData surface="test-list-template"
  data:
    "people" = [{"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Alice", "role": "Engineer"}, {"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Bob", "role": "Designer"}, {"avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop", "name": "Carol", "role": "Manager"}]
#4 createComponent surface="test-list-template" tag=8 parent=2 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="test-list-template" tag=9 parent=8 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = ""
    "width" = 40
#6 createComponent surface="test-list-template" tag=10 parent=8 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#7 createComponent surface="test-list-template" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### "
#8 createComponent surface="test-list-template" tag=12 parent=10 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#9 createComponent surface="test-list-template" tag=13 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="test-list-template" tag=14 parent=13 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = ""
    "width" = 40
#11 createComponent surface="test-list-template" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#12 createComponent surface="test-list-template" tag=16 parent=15 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### "
#13 createComponent surface="test-list-template" tag=17 parent=15 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#14 createComponent surface="test-list-template" tag=18 parent=2 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#15 createComponent surface="test-list-template" tag=19 parent=18 index=0 view="KRImageView"
  props:
    "borderRadius" = "20"
    "height" = 40
    "margin" = 8
    "src" = ""
    "width" = 40
#16 createComponent surface="test-list-template" tag=20 parent=18 index=1 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
#17 createComponent surface="test-list-template" tag=21 parent=20 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### "
#18 createComponent surface="test-list-template" tag=22 parent=20 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#state dataModel "{\"people\":[{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Alice\",\"role\":\"Engineer\"},{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Bob\",\"role\":\"Designer\"},{\"avatar\":\"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&h=300&fit=crop\",\"name\":\"Carol\",\"role\":\"Manager\"}]}"
#state actions {}
""",
        """v1_0.formal.component.modal.1_modal_basic""" to """#0 createSurface surface="test-modal-basic"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-modal-basic" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-modal-basic" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-modal-basic" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Click 'Open' to show the modal"
#4 createComponent surface="test-modal-basic" tag=5 parent=2 index=1 view="KRView"
  props:
    "__modalTriggerId" = "modal"
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="test-modal-basic" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Open"
#state dataModel "{}"
#state actions {"5": "{\"event\":{\"name\":\"openModal\"}}"}
""",
        """v1_0.formal.component.modal.2_modal_with_form""" to """#0 createSurface surface="test-modal-form"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-modal-form" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-modal-form" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-modal-form" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Open settings modal"
#4 createComponent surface="test-modal-form" tag=5 parent=2 index=1 view="KRView"
  props:
    "__modalTriggerId" = "modal"
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#5 createComponent surface="test-modal-form" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Settings"
#state dataModel "{}"
#state actions {"5": "{\"event\":{\"name\":\"openSettings\"}}"}
""",
        """v1_0.formal.component.row.1_row_justify_start""" to """#0 createSurface surface="test-row-justify-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-start"
#3 createComponent surface="test-row-justify-start" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-start" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-start" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-start" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-start" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-start" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.10_row_template_children""" to """#0 createSurface surface="test-row-template"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-template" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-template" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-start"
#3 updateData surface="test-row-template"
  data:
    "tags" = [{"label": "Kotlin"}, {"label": "Compose"}, {"label": "iOS"}, {"label": "A2UI"}]
#4 createComponent surface="test-row-template" tag=4 parent=2 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#5 createComponent surface="test-row-template" tag=5 parent=2 index=1 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 createComponent surface="test-row-template" tag=6 parent=2 index=2 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#7 createComponent surface="test-row-template" tag=7 parent=2 index=3 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#state dataModel "{\"tags\":[{\"label\":\"Kotlin\"},{\"label\":\"Compose\"},{\"label\":\"iOS\"},{\"label\":\"A2UI\"}]}"
#state actions {}
""",
        """v1_0.formal.component.row.2_row_justify_center""" to """#0 createSurface surface="test-row-justify-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "center"
#3 createComponent surface="test-row-justify-center" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-center" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-center" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-center" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-center" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-center" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.3_row_justify_end""" to """#0 createSurface surface="test-row-justify-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "flex-end"
#3 createComponent surface="test-row-justify-end" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-end" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-end" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-end" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-end" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-end" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.4_row_justify_spaceBetween""" to """#0 createSurface surface="test-row-justify-between"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-between" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-between" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-between"
#3 createComponent surface="test-row-justify-between" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-between" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-between" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-between" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-between" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-between" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.5_row_justify_spaceAround""" to """#0 createSurface surface="test-row-justify-around"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-around" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-around" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-around"
#3 createComponent surface="test-row-justify-around" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-around" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-around" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-around" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-around" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-around" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.6_row_justify_spaceEvenly""" to """#0 createSurface surface="test-row-justify-evenly"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-justify-evenly" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-justify-evenly" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-justify-evenly" tag=3 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#4 createComponent surface="test-row-justify-evenly" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "A"
#5 createComponent surface="test-row-justify-evenly" tag=5 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#6 createComponent surface="test-row-justify-evenly" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "B"
#7 createComponent surface="test-row-justify-evenly" tag=7 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 8
    "padding" = 16
    "wrapperBoxShadowView" = 1
#8 createComponent surface="test-row-justify-evenly" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.7_row_align_start""" to """#0 createSurface surface="test-row-align-start"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-align-start" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-start" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-start" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Tall\nText\nA"
#4 createComponent surface="test-row-align-start" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-start" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.8_row_align_center""" to """#0 createSurface surface="test-row-align-center"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-align-center" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-center" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-center" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Tall\nText\nA"
#4 createComponent surface="test-row-align-center" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-center" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.row.9_row_align_end""" to """#0 createSurface surface="test-row-align-end"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-row-align-end" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-row-align-end" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignItems" = "flex-end"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
    "justifyContent" = "space-evenly"
#3 createComponent surface="test-row-align-end" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Tall\nText\nA"
#4 createComponent surface="test-row-align-end" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "B"
#5 createComponent surface="test-row-align-end" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "C"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.slider.1_slider_default_min_max""" to """#0 createSurface surface="test-sl-default"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-sl-default" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-default" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-default" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Volume"
#4 createComponent surface="test-sl-default" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = 30
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.slider.2_slider_min_omitted""" to """#0 createSurface surface="test-sl-omit-min"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-sl-omit-min" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-omit-min" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-omit-min" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Brightness"
#4 createComponent surface="test-sl-omit-min" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 10
    "progressColor" = "4278239231"
    "value" = 7
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.slider.3_slider_negative_range""" to """#0 createSurface surface="test-sl-neg"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-sl-neg" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-neg" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-sl-neg" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Temperature (°C)"
#4 createComponent surface="test-sl-neg" tag=2 parent=3 index=1 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 40
    "min" = -20
    "progressColor" = "4278239231"
    "value" = 22
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.slider.4_slider_value_binding""" to """#0 createSurface surface="test-sl-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-sl-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-sl-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-sl-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Progress"
#5 createComponent surface="test-sl-binding" tag=3 parent=5 index=1 view="KRSliderView"
  props:
    "__sliderBindingPath" = "progress"
    "alignSelf" = "stretch"
    "height" = 40
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "value" = ""
#6 createComponent surface="test-sl-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Progress: %"
#7 updateData surface="test-sl-binding"
  data:
    "progress" = 45
#8 updateComponent surface="test-sl-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Progress: 45%"
#9 updateComponent surface="test-sl-binding" tag=3 view="KRSliderView"
  props:
    "value" = 45
#10 updateComponent surface="test-sl-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Progress"
#state dataModel "{\"progress\":45}"
#state actions {}
""",
        """v1_0.formal.component.slider.5_slider_no_label""" to """#0 createSurface surface="test-sl-nolabel"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-sl-nolabel" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-sl-nolabel" tag=2 parent=1 index=0 view="KRSliderView"
  props:
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 1
    "min" = 0
    "progressColor" = "4278239231"
    "value" = 0.5
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.tabs.1_tabs_two_tabs""" to """#0 createSurface surface="test-tabs-two"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tabs-two" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-two" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-two" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-two" tag=6 parent=5 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-two" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Overview"
#6 createComponent surface="test-tabs-two" tag=8 parent=6 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-two" tag=9 parent=5 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-two" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Details"
#9 createComponent surface="test-tabs-two" tag=11 parent=9 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-two" tag=12 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-two" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-two" tag=3 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Tab 1: Overview content. Lorem ipsum..."
#13 createComponent surface="test-tabs-two" tag=4 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Tab 2: Details content. Different from tab 1."
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.tabs.2_tabs_three_tabs""" to """#0 createSurface surface="test-tabs-three"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tabs-three" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-three" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-three" tag=6 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-three" tag=7 parent=6 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-three" tag=8 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Photos"
#6 createComponent surface="test-tabs-three" tag=9 parent=7 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-three" tag=10 parent=6 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-three" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Videos"
#9 createComponent surface="test-tabs-three" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-three" tag=13 parent=6 index=2 view="KRView"
  props:
    "__tabIndex" = 2
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#11 createComponent surface="test-tabs-three" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Audio"
#12 createComponent surface="test-tabs-three" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#13 createComponent surface="test-tabs-three" tag=16 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#14 createComponent surface="test-tabs-three" tag=17 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#15 createComponent surface="test-tabs-three" tag=18 parent=2 index=3 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#16 createComponent surface="test-tabs-three" tag=3 parent=16 index=0 view="KRImageView"
  props:
    "__featureAutoSizeMaxHeightDp" = 200
    "__featureAutoSizeMaxWidthDp" = 300
    "alignSelf" = "stretch"
    "height" = 1
    "margin" = 8
    "resizeMode" = "cover"
    "src" = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=300&fit=crop"
#17 createComponent surface="test-tabs-three" tag=4 parent=17 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#18 createComponent surface="test-tabs-three" tag=19 parent=18 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#19 createComponent surface="test-tabs-three" tag=20 parent=19 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Audio tab"
#20 createComponent surface="test-tabs-three" tag=5 parent=19 index=1 view="KRAudioView"
  props:
    "alignSelf" = "stretch"
    "height" = 64
    "src" = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.tabs.3_tabs_complex_children""" to """#0 createSurface surface="test-tabs-complex"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tabs-complex" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-complex" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-complex" tag=9 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-complex" tag=10 parent=9 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-complex" tag=11 parent=10 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Profile"
#6 createComponent surface="test-tabs-complex" tag=12 parent=10 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-complex" tag=13 parent=9 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-complex" tag=14 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Settings"
#9 createComponent surface="test-tabs-complex" tag=15 parent=13 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-complex" tag=16 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-complex" tag=17 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-complex" tag=3 parent=16 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#13 createComponent surface="test-tabs-complex" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Name: Alex Chen"
#14 createComponent surface="test-tabs-complex" tag=5 parent=3 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Role: Senior Engineer"
#15 createComponent surface="test-tabs-complex" tag=6 parent=17 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#16 createComponent surface="test-tabs-complex" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Theme: Dark"
#17 createComponent surface="test-tabs-complex" tag=8 parent=6 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Language: English"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.tabs.4_tabs_title_binding""" to """#0 createSurface surface="test-tabs-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tabs-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tabs-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tabs-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "paddingLeft" = 4.0
    "paddingRight" = 4.0
#4 createComponent surface="test-tabs-binding" tag=6 parent=5 index=0 view="KRView"
  props:
    "__tabIndex" = 0
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#5 createComponent surface="test-tabs-binding" tag=7 parent=6 index=0 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Tab 1"
#6 createComponent surface="test-tabs-binding" tag=8 parent=6 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4278239231"
    "height" = 2.5
    "marginTop" = 8.0
#7 createComponent surface="test-tabs-binding" tag=9 parent=5 index=1 view="KRView"
  props:
    "__tabIndex" = 1
    "__tabsParentId" = "tabs"
    "alignItems" = "center"
    "flex" = 1
    "justifyContent" = "center"
    "paddingBottom" = 8.0
    "paddingLeft" = 12.0
    "paddingRight" = 12.0
    "paddingTop" = 10.0
    "touchEnable" = 1
#8 createComponent surface="test-tabs-binding" tag=10 parent=9 index=0 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Tab 2"
#9 createComponent surface="test-tabs-binding" tag=11 parent=9 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "0"
    "height" = 2.5
    "marginTop" = 8.0
#10 createComponent surface="test-tabs-binding" tag=12 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flex" = 1
    "flexDirection" = "column"
    "overflow" = "hidden"
#11 createComponent surface="test-tabs-binding" tag=13 parent=2 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "height" = 0.0
    "overflow" = "hidden"
#12 createComponent surface="test-tabs-binding" tag=3 parent=12 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Content for tab 1"
#13 createComponent surface="test-tabs-binding" tag=4 parent=13 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Content for tab 2"
#14 updateData surface="test-tabs-binding"
  data:
    "tab1" = "Bound A"
    "tab2" = "Bound B"
#15 updateComponent surface="test-tabs-binding" tag=7 view="KRRichTextView"
  props:
    "color" = "4278239231"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "text" = "Bound A"
#16 updateComponent surface="test-tabs-binding" tag=10 view="KRRichTextView"
  props:
    "color" = "4284900966"
    "fontSize" = 14.0
    "fontWeight" = "normal"
    "text" = "Bound B"
#state dataModel "{\"tab1\":\"Bound A\",\"tab2\":\"Bound B\"}"
#state actions {}
""",
        """v1_0.formal.component.text.1_text_h1""" to """#0 createSurface surface="test-text-h1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-h1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h1" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "# Heading 1 - Largest Title"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.text.2_text_h2""" to """#0 createSurface surface="test-text-h2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-h2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h2" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "## Heading 2 - Section Title"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.text.3_text_h3_h4_h5""" to """#0 createSurface surface="test-text-h345"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-h345" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-h345" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-h345" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### Heading 3 (28dp)"
#4 createComponent surface="test-text-h345" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Heading 4 (24dp)"
#5 createComponent surface="test-text-h345" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### Heading 5 (20dp)"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.text.5_text_caption""" to """#0 createSurface surface="test-text-caption"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-caption" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-caption" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-caption" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "POSTED"
#4 createComponent surface="test-text-caption" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Article Title"
#5 createComponent surface="test-text-caption" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "by John Doe • 5 min read"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.text.7_text_function_formatString""" to """#0 createSurface surface="test-text-format-string"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-format-string" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-format-string" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-format-string" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Hello,  !"
#4 createComponent surface="test-text-format-string" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "You have  unread messages and  new notifications."
#5 updateData surface="test-text-format-string"
  data:
    "firstName" = "Alex"
    "lastName" = "Chen"
    "notifications" = 12
    "unread" = 5
#6 updateComponent surface="test-text-format-string" tag=3 view="KRRichTextView"
  props:
    "text" = "#### Hello, Alex Chen!"
#7 updateComponent surface="test-text-format-string" tag=4 view="KRRichTextView"
  props:
    "text" = "You have 5 unread messages and 12 new notifications."
#state dataModel "{\"firstName\":\"Alex\",\"lastName\":\"Chen\",\"unread\":5,\"notifications\":12}"
#state actions {}
""",
        """v1_0.formal.component.text.8_text_function_formatDate""" to """#0 createSurface surface="test-text-format-date"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-text-format-date" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-text-format-date" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-text-format-date" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "PUBLISHED"
#4 createComponent surface="test-text-format-date" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "##### "
#5 createComponent surface="test-text-format-date" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = ""
#6 updateData surface="test-text-format-date"
  data:
    "publishedAt" = "2025-12-15T17:30:00Z"
#7 updateComponent surface="test-text-format-date" tag=4 view="KRRichTextView"
  props:
    "text" = "##### Dec 15, 2025"
#8 updateComponent surface="test-text-format-date" tag=5 view="KRRichTextView"
  props:
    "text" = "2025/12/15"
#state dataModel "{\"publishedAt\":\"2025-12-15T17:30:00Z\"}"
#state actions {}
""",
        """v1_0.formal.component.textfield.1_textfield_variant_shortText""" to """#0 createSurface surface="test-tf-short"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-short" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-short" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-short" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Username"
#4 createComponent surface="test-tf-short" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.textfield.2_textfield_variant_longText""" to """#0 createSurface surface="test-tf-long"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-long" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-long" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-long" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Bio"
#4 createComponent surface="test-tf-long" tag=2 parent=3 index=1 view="KRTextAreaView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "minHeight" = 88
    "padding" = 8
    "text" = ""
    "variant" = "longText"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.textfield.3_textfield_variant_number""" to """#0 createSurface surface="test-tf-number"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-number" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-number" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-number" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Age"
#4 createComponent surface="test-tf-number" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "number"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.textfield.4_textfield_variant_obscured""" to """#0 createSurface surface="test-tf-obscured"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-obscured" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-obscured" tag=3 parent=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#3 createComponent surface="test-tf-obscured" tag=4 parent=3 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Password"
#4 createComponent surface="test-tf-obscured" tag=2 parent=3 index=1 view="KRTextFieldView"
  props:
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "obscured"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.textfield.5_textfield_value_binding""" to """#0 createSurface surface="test-tf-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-binding" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tf-binding" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-tf-binding" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Your name"
#5 createComponent surface="test-tf-binding" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "name"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#6 createComponent surface="test-tf-binding" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Hello, !"
#7 updateData surface="test-tf-binding"
  data:
    "name" = "World"
#8 updateComponent surface="test-tf-binding" tag=4 view="KRRichTextView"
  props:
    "text" = "Hello, World!"
#9 updateComponent surface="test-tf-binding" tag=3 view="KRTextFieldView"
  props:
    "text" = "World"
#10 updateComponent surface="test-tf-binding" tag=6 view="KRRichTextView"
  props:
    "text" = "Your name"
#state dataModel "{\"name\":\"World\"}"
#state actions {}
""",
        """v1_0.formal.component.textfield.6_textfield_email_checks_vs_legacy_regexp""" to """#0 createSurface surface="test-tf-regexp"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-tf-regexp" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-tf-regexp" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-tf-regexp" tag=5 parent=2 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#4 createComponent surface="test-tf-regexp" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (email via @checks)"
#5 createComponent surface="test-tf-regexp" tag=3 parent=5 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "email"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#6 createComponent surface="test-tf-regexp" tag=7 parent=5 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "height" = 0.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "opacity" = 0.0
    "text" = ""
#7 createComponent surface="test-tf-regexp" tag=8 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#8 createComponent surface="test-tf-regexp" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Email (regex via @checks)"
#9 createComponent surface="test-tf-regexp" tag=4 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "tf_validation_value"
    "alignSelf" = "stretch"
    "border" = "1 solid 4292030255"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
    "variant" = "shortText"
#10 createComponent surface="test-tf-regexp" tag=10 parent=8 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "Please enter a value matching the required pattern"
#11 updateData surface="test-tf-regexp"
  data:
    "email" = ""
    "tf_validation_value" = ""
#12 updateComponent surface="test-tf-regexp" tag=3 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#13 updateComponent surface="test-tf-regexp" tag=6 view="KRRichTextView"
  props:
    "text" = "Email (email via @checks)"
#14 updateComponent surface="test-tf-regexp" tag=4 view="KRTextFieldView"
  props:
    "border" = "1 solid 4291611852"
    "text" = ""
#15 updateComponent surface="test-tf-regexp" tag=9 view="KRRichTextView"
  props:
    "text" = "Email (regex via @checks)"
#16 updateComponent surface="test-tf-regexp" tag=7 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#17 updateComponent surface="test-tf-regexp" tag=10 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#state dataModel "{\"email\":\"\",\"tf_validation_value\":\"\"}"
#state actions {}
""",
        """v1_0.formal.component.video.1_video_literal_url""" to """#0 createSurface surface="test-video-literal"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-video-literal" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-literal" tag=2 parent=1 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.component.video.2_video_url_binding""" to """#0 createSurface surface="test-video-binding"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-video-binding" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-binding" tag=2 parent=1 index=0 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = ""
#3 updateData surface="test-video-binding"
  data:
    "videoUrl" = "https://www.w3schools.com/html/mov_bbb.mp4"
#4 updateComponent surface="test-video-binding" tag=2 view="KRVideoView"
  props:
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#state dataModel "{\"videoUrl\":\"https://www.w3schools.com/html/mov_bbb.mp4\"}"
#state actions {}
""",
        """v1_0.formal.component.video.3_video_in_column""" to """#0 createSurface surface="test-video-column"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 createComponent surface="test-video-column" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="test-video-column" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="test-video-column" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### Sample Video"
#4 createComponent surface="test-video-column" tag=4 parent=2 index=1 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "src" = "https://www.w3schools.com/html/mov_bbb.mp4"
#5 createComponent surface="test-video-column" tag=5 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "Big Buck Bunny - test clip"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.extension.01_string_return.1_custom_greeting""" to """#0 createSurface surface="cf-str-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-str-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-str-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-str-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "getGreeting(name) → String"
#4 createComponent surface="cf-str-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### Hello, World!"
#5 updateData surface="cf-str-1"
  data:
    "userName" = "Kuikly"
#6 updateComponent surface="cf-str-1" tag=4 view="KRRichTextView"
  props:
    "text" = "### Hello, Kuikly!"
#state dataModel "{\"userName\":\"Kuikly\"}"
#state actions {}
""",
        """v1_0.formal.extension.01_string_return.2_custom_format_phone""" to """#0 createSurface surface="cf-str-2"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-str-2" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-str-2" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-str-2" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "formatPhone(phone) → String"
#4 createComponent surface="cf-str-2" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "#### "
#5 updateData surface="cf-str-2"
  data:
    "rawPhone" = "13800001234"
#6 updateComponent surface="cf-str-2" tag=4 view="KRRichTextView"
  props:
    "text" = "#### 138-0000-1234"
#state dataModel "{\"rawPhone\":\"13800001234\"}"
#state actions {}
""",
        """v1_0.formal.extension.01_web_view.1_web_source""" to """#0 createSurface surface="cc-web-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_components_catalog_v1_0.json"
#1 createComponent surface="cc-web-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cc-web-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cc-web-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### A2UIWeb 组件"
#4 createComponent surface="cc-web-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "通过 source 属性加载网页内容"
#5 createComponent surface="cc-web-1" tag=5 parent=2 index=2 view="A2UIWeb"
  props:
    "source" = "https://framework.tds.qq.com/"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.extension.02_boolean_return.1_custom_is_vip""" to """#0 createSurface surface="cf-bool-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-bool-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-bool-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-bool-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "isVip(level) → Boolean；numeric → ValidationResult"
#4 createComponent surface="cf-bool-1" tag=7 parent=2 index=1 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#5 createComponent surface="cf-bool-1" tag=5 parent=7 index=0 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "opacity" = 0.4
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 0
#6 createComponent surface="cf-bool-1" tag=8 parent=7 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#D32F2F"
    "fontSize" = 12.0
    "marginLeft" = 4.0
    "marginTop" = 4.0
    "text" = "仅 VIP 用户（level >= 3）可进入"
#7 createComponent surface="cf-bool-1" tag=4 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4294967295"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "🌟 进入 VIP 专区"
#8 createComponent surface="cf-bool-1" tag=6 parent=2 index=2 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "当前 userLevel = ，isVip 返回 false"
#9 updateData surface="cf-bool-1"
  data:
    "userLevel" = 5
#10 updateComponent surface="cf-bool-1" tag=6 view="KRRichTextView"
  props:
    "text" = "当前 userLevel = 5，isVip 返回 true"
#11 updateComponent surface="cf-bool-1" tag=8 view="KRRichTextView"
  props:
    "height" = 0.0
    "opacity" = 0.0
    "text" = ""
#12 updateComponent surface="cf-bool-1" tag=5 view="KRView"
  props:
    "opacity" = 1.0
    "touchEnable" = 1
#state dataModel "{\"userLevel\":5}"
#state actions {"5": "{\"event\":{\"name\":\"enterVipZone\",\"context\":{\"level\":5}}}"}
""",
        """v1_0.formal.extension.02_my_view.1_my_view""" to """#0 createSurface surface="cc-my-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_components_catalog_v1_0.json"
#1 createComponent surface="cc-my-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cc-my-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cc-my-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### MyA2UIView 组件"
#4 createComponent surface="cc-my-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "自定义简单视图，支持 text 和 textColor 属性"
#5 createComponent surface="cc-my-1" tag=5 parent=2 index=2 view="MyA2UIView"
  props:
    "text" = "Hello from MyA2UIView! 🎉"
    "textColor" = "4284612846"
#state dataModel "{}"
#state actions {}
""",
        """v1_0.formal.extension.03_number_return.1_custom_calculate_discount""" to """#0 createSurface surface="cf-num-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-num-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-num-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-num-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "calculateDiscount(price, rate) → Number"
#4 createComponent surface="cf-num-1" tag=4 parent=2 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#5 createComponent surface="cf-num-1" tag=5 parent=4 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "折后价: ¥"
#6 createComponent surface="cf-num-1" tag=6 parent=4 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "### 0"
#7 updateData surface="cf-num-1"
  data:
    "discountRate" = 0.7
    "originalPrice" = 199
#8 updateComponent surface="cf-num-1" tag=6 view="KRRichTextView"
  props:
    "text" = "### 139.3"
#state dataModel "{\"originalPrice\":199,\"discountRate\":0.7}"
#state actions {}
""",
        """v1_0.formal.extension.04_void_action.1_custom_log_event""" to """#0 createSurface surface="cf-void-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-void-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-void-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-void-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "logEvent(event, data) → Void"
#4 createComponent surface="cf-void-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "点击按钮触发自定义 void 函数 logEvent"
#5 createComponent surface="cf-void-1" tag=5 parent=2 index=2 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4278239231"
    "borderRadius" = "6"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#6 createComponent surface="cf-void-1" tag=6 parent=5 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "触发 logEvent"
#7 updateData surface="cf-void-1"
  data:
    "eventData" = "custom_function_demo"
#state dataModel "{\"eventData\":\"custom_function_demo\"}"
#state actions {"5": "{\"functionCall\":{\"@call\":\"logEvent\",\"args\":{\"event\":\"button_click\",\"data\":\"custom_function_demo\"}}}"}
""",
        """v1_0.formal.extension.05_args_passing.1_custom_build_summary""" to """#0 createSurface surface="cf-args-1"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="custom_functions_catalog_v1_0.json"
#1 createComponent surface="cf-args-1" tag=1 index=0 view="KRView"
  props:
    "alignSelf" = "stretch"
    "backgroundColor" = "4294967295"
    "border" = "1 solid 4292927712"
    "borderRadius" = "12"
    "boxShadow" = "0 1 2 436207616 0"
    "flexDirection" = "column"
    "margin" = 0
    "padding" = 16
    "wrapperBoxShadowView" = 1
#2 createComponent surface="cf-args-1" tag=2 parent=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="cf-args-1" tag=3 parent=2 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4284900966"
    "fontSize" = 13.0
    "margin" = 8
    "text" = "buildSummary(name, count, status) → String"
#4 createComponent surface="cf-args-1" tag=4 parent=2 index=1 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "项目「Unknown」当前状态: unknown，共 0 个任务"
#5 updateData surface="cf-args-1"
  data:
    "project" = {"name": "KuiklyUI", "status": "active", "taskCount": 42}
#6 updateComponent surface="cf-args-1" tag=4 view="KRRichTextView"
  props:
    "text" = "项目「KuiklyUI」当前状态: active，共 42 个任务"
#state dataModel "{\"project\":{\"name\":\"KuiklyUI\",\"taskCount\":42,\"status\":\"active\"}}"
#state actions {}
""",
        """v1_0.protocol.01_pr2_mixin_keys""" to """#0 createSurface surface="pr2-mixin-keys-surface"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 updateData surface="pr2-mixin-keys-surface"
  data:
    "score" = 87
    "userName" = "Alice"
#2 createComponent surface="pr2-mixin-keys-surface" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="pr2-mixin-keys-surface" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "__accessibility" = "Greeting line shows the resolved user name"
    "__accessibilityLive" = "polite"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Alice"
#4 createComponent surface="pr2-mixin-keys-surface" tag=3 parent=1 index=1 view="KRRichTextView"
  props:
    "__accessibility" = "Current numeric score bound via @path"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 87
#5 createComponent surface="pr2-mixin-keys-surface" tag=4 parent=1 index=2 view="KRRichTextView"
  props:
    "__accessibility" = "Letter grade derived from the score via a host-registered custom function"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "score=87"
#state dataModel "{\"userName\":\"Alice\",\"score\":87}"
#state actions {}
""",
        """v1_0.protocol.02_pr3_createsurface_inline_datamodel""" to """#0 createSurface surface="pr3-inline-datamodel-surface"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 updateData surface="pr3-inline-datamodel-surface"
  data:
    "cart" = {"items": [{"name": "Espresso", "price": 3.5}, {"name": "Croissant", "price": 2.8}, {"name": "Tea latte", "price": 4.2}], "title": "Your cart"}
#2 createComponent surface="pr3-inline-datamodel-surface" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="pr3-inline-datamodel-surface" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Your cart"
#4 createComponent surface="pr3-inline-datamodel-surface" tag=3 parent=1 index=1 view="KRScrollView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#5 createComponent surface="pr3-inline-datamodel-surface" tag=8 parent=3 index=0 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#6 createComponent surface="pr3-inline-datamodel-surface" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "1"
#7 createComponent surface="pr3-inline-datamodel-surface" tag=10 parent=8 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Espresso"
#8 createComponent surface="pr3-inline-datamodel-surface" tag=11 parent=8 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 3.5
#9 createComponent surface="pr3-inline-datamodel-surface" tag=12 parent=3 index=1 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#10 createComponent surface="pr3-inline-datamodel-surface" tag=13 parent=12 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "2"
#11 createComponent surface="pr3-inline-datamodel-surface" tag=14 parent=12 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Croissant"
#12 createComponent surface="pr3-inline-datamodel-surface" tag=15 parent=12 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 2.8
#13 createComponent surface="pr3-inline-datamodel-surface" tag=16 parent=3 index=2 view="KRView"
  props:
    "alignContent" = "flex-start"
    "alignSelf" = "stretch"
    "flexDirection" = "row"
    "flexWrap" = "wrap"
#14 createComponent surface="pr3-inline-datamodel-surface" tag=17 parent=16 index=0 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "3"
#15 createComponent surface="pr3-inline-datamodel-surface" tag=18 parent=16 index=1 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "Tea latte"
#16 createComponent surface="pr3-inline-datamodel-surface" tag=19 parent=16 index=2 view="KRRichTextView"
  props:
    "__textInRowPostLayout" = "true"
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = 4.2
#state dataModel "{\"cart\":{\"title\":\"Your cart\",\"items\":[{\"name\":\"Espresso\",\"price\":3.5},{\"name\":\"Croissant\",\"price\":2.8},{\"name\":\"Tea latte\",\"price\":4.2}]}}"
#state actions {}
""",
        """v1_0.protocol.05_pr9_small_fields_video_poster""" to """#0 createSurface surface="pr9-small-fields-surface"
  config: w=0.0 h=0.0 bg=null incremental=true catalogId="https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
#1 updateData surface="pr9-small-fields-surface"
  data:
    "coverUrl" = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
    "searchQuery" = ""
    "videoUrl" = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    "volume" = 50
#2 createComponent surface="pr9-small-fields-surface" tag=1 index=0 view="KRView"
  props:
    "alignItems" = "stretch"
    "alignSelf" = "stretch"
    "flexDirection" = "column"
#3 createComponent surface="pr9-small-fields-surface" tag=2 parent=1 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 8
    "text" = "PR-9 small fields"
#4 createComponent surface="pr9-small-fields-surface" tag=3 parent=1 index=1 view="KRVideoView"
  props:
    "alignSelf" = "stretch"
    "height" = 200
    "margin" = 8
    "posterUrl" = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
    "src" = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
#5 createComponent surface="pr9-small-fields-surface" tag=8 parent=1 index=2 view="KRView"
  props:
    "alignSelf" = "stretch"
    "flexDirection" = "column"
    "margin" = 8
#6 createComponent surface="pr9-small-fields-surface" tag=9 parent=8 index=0 view="KRRichTextView"
  props:
    "alignSelf" = "stretch"
    "color" = "#333333"
    "fontSize" = 14.0
    "fontWeight" = "bold"
    "marginBottom" = 4.0
    "text" = "Search"
#7 createComponent surface="pr9-small-fields-surface" tag=4 parent=8 index=1 view="KRTextFieldView"
  props:
    "__textFieldBindingPath" = "searchQuery"
    "alignSelf" = "stretch"
    "border" = "1 solid 4291611852"
    "borderRadius" = "4"
    "height" = 44
    "padding" = 8
    "text" = ""
#8 createComponent surface="pr9-small-fields-surface" tag=5 parent=1 index=3 view="KRSliderView"
  props:
    "__sliderBindingPath" = "volume"
    "alignSelf" = "stretch"
    "height" = 40
    "margin" = 8
    "max" = 100
    "min" = 0
    "progressColor" = "4278239231"
    "steps" = 4
    "value" = 50
#9 createComponent surface="pr9-small-fields-surface" tag=7 parent=1 index=4 view="KRView"
  props:
    "alignItems" = "center"
    "backgroundColor" = "4294309365"
    "borderColor" = "4291611852"
    "borderRadius" = "6"
    "borderWidth" = "1"
    "flexDirection" = "row"
    "justifyContent" = "center"
    "margin" = 8
    "padding" = 10
    "paddingLeft" = 16
    "paddingRight" = 16
    "touchEnable" = 1
#10 createComponent surface="pr9-small-fields-surface" tag=6 parent=7 index=0 view="KRRichTextView"
  props:
    "color" = "4279900698"
    "fontSize" = 16.0
    "margin" = 0
    "text" = "Send to agent"
#state dataModel "{\"videoUrl\":\"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4\",\"coverUrl\":\"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg\",\"volume\":50,\"searchQuery\":\"\"}"
#state actions {"7": "{\"event\":{\"name\":\"submitSearchEvent\",\"context\":{\"query\":\"\",\"volume\":50},\"userMessage\":\"\"}}"}
""",
    )
}

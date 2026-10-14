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

import { router } from '@kit.ArkUI';
import { BindingEntry, BindingGroup } from '../a2ui/A2UIDemoAssetUtil';

/**
 * Router params for ComponentTestPage navigation.
 */
class ComponentTestParams {
  componentDir: string = '';
  componentName: string = '';
  rootDir: string = '';
  titleSuffix: string = '';

  constructor(componentDir: string, componentName: string, rootDir: string, titleSuffix: string) {
    this.componentDir = componentDir;
    this.componentName = componentName;
    this.rootDir = rootDir;
    this.titleSuffix = titleSuffix;
  }
}

/**
 * Data Binding Demo Page.
 * Displays the 12 data-binding capabilities defined by the A2UI v0.9 protocol.
 */
@Entry
@Component
struct DataBindingDemoPage {
  private groups: BindingGroup[] = [
    {
      title: '路径绑定（Path Binding）',
      entries: [
        { dir: '01_absolute_path', name: '绝对路径', description: '{path:"/x"} 绑定 DynamicString / Number / Boolean 等各种类型', color: '#0984E3' },
        { dir: '02_data_model_upsert', name: 'Data Model 增量同步', description: 'updateDataModel 的 upsert / 删除 / 整体替换语义', color: '#2980B9' }
      ]
    },
    {
      title: '列表模板（ChildList Template）',
      entries: [
        { dir: '03_child_list_template', name: 'ChildList 模板遍历', description: 'children:{componentId,path} 基于数组渲染动态列表', color: '#8E44AD' },
        { dir: '04_relative_path', name: '模板内相对路径', description: '子作用域内使用 "fieldName" 绑定当前迭代项', color: '#9B59B6' },
        { dir: '05_mixed_scope', name: '混合作用域', description: '模板子树内绝对路径与相对路径共存', color: '#6C5CE7' }
      ]
    },
    {
      title: '双向绑定（Two-Way Binding）',
      entries: [
        { dir: '06_two_way_binding', name: '输入组件回写 + 反应式联动', description: 'TextField / Slider / CheckBox 输入自动写回 Data Model 并触发同路径重渲染', color: '#00B894' }
      ]
    },
    {
      title: '函数与插值（Function & Interpolation）',
      entries: [
        { dir: '07_function_binding', name: 'FunctionCall 作为 Dynamic*', description: '以 FunctionCall 作为属性值（可嵌套 args）', color: '#16A085' },
        { dir: '08_string_interpolation', name: 'formatString 字符串插值', description: '${path} / ${func(args...)} / 转义 / 类型转换', color: '#27AE60' }
      ]
    },
    {
      title: '校验绑定（Check Binding）',
      entries: [
        { dir: '09_check_binding', name: 'checks 表达式', description: '条件基于路径与逻辑函数，未通过时 Button 自动 disable', color: '#E74C3C' }
      ]
    },
    {
      title: '动作与无障碍（Action & A11y）',
      entries: [
        { dir: '10_action_context', name: 'Action.context 绑定', description: '点击事件把 Data Model 值随 context 一起下发', color: '#E67E22' },
        { dir: '11_accessibility', name: '无障碍字段动态绑定', description: 'accessibility.label / description 支持 DynamicString', color: '#F39C12' }
      ]
    },
    {
      title: '渐进渲染（Progressive Rendering）',
      entries: [
        { dir: '12_progressive_rendering', name: '缺数据 / 数据后到达降级', description: 'path 未定义时 UI 优雅降级，updateDataModel 到达后自动填充重渲染', color: '#7F8C8D' }
      ]
    }
  ];

  build() {
    Column() {
      // Header (blue bar)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text('数据绑定测试')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI v0.9 协议 12 项数据绑定能力')
          .fontSize(12)
          .fontColor('#DDECFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#0984E3')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(this.groups, (group: BindingGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6 })

            ForEach(group.entries, (entry: BindingEntry) => {
              Row() {
                // Color indicator
                Column()
                  .width(4)
                  .height(40)
                  .borderRadius(3)
                  .backgroundColor(entry.color)
                  .margin({ right: 12 })

                // Text column
                Column() {
                  Text(entry.name)
                    .fontSize(15)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#333333')
                  Text(entry.description)
                    .fontSize(12)
                    .fontColor('#888888')
                    .margin({ top: 3 })
                }
                .layoutWeight(1)
                .alignItems(HorizontalAlign.Start)

                // Arrow
                Text('›')
                  .fontSize(18)
                  .fontColor('#CCCCCC')
                  .padding({ left: 8 })
              }
              .width('100%')
              .padding({ left: 12, top: 14, right: 12, bottom: 14 })
              .backgroundColor(Color.White)
              .borderRadius(8)
              .shadow({ radius: 2, color: '#1A000000', offsetY: 1 })
              .margin({ top: 6 })
              .onClick(() => {
                router.pushUrl({
                  url: 'pages/ComponentTestPage',
                  params: new ComponentTestParams(entry.dir, entry.name, 'a2ui_databinding_tests', '数据绑定')
                });
              })
            }, (entry: BindingEntry) => entry.dir)
          }, (group: BindingGroup) => group.title)
        }
        .padding({ left: 12, top: 8, right: 12, bottom: 16 })
        .alignItems(HorizontalAlign.Start)
      }
      .layoutWeight(1)
      .align(Alignment.Top)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}

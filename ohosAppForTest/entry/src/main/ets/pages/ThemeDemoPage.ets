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

/**
 * Theme entry data.
 */
interface ThemeEntry {
  dir: string;
  name: string;
  description: string;
  color: string;
}

/**
 * Theme group data.
 */
interface ThemeGroup {
  title: string;
  entries: ThemeEntry[];
}

/**
 * Router params for ComponentTestPage navigation.
 */
class ThemeTestParams {
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
 * Theme Demo Page.
 * Displays the A2UI v0.9 protocol theme capabilities defined in `createSurface.theme`,
 * grouped by category. Selecting a capability opens ComponentTestPage
 * with the corresponding asset directory under `rawfile/a2ui_theme_tests/<capabilityDir>/`.
 *
 * The 8 capability groups:
 *  1. primaryColor basic                          -> 01_primary_color_basic
 *  2. Agent identity: iconUrl / agentDisplayName  -> 02_agent_identity
 *  3. Full theme + additionalProperties extras    -> 03_theme_all_fields
 *  4. Invalid primaryColor fallback               -> 04_invalid_color_fallback
 *  5. Default theme (createSurface without theme) -> 05_default_no_theme
 *  6. Extreme colors (black / white primaryColor) -> 06_extreme_colors
 *  7. Button variant isolation (regression)       -> 07_button_variant_isolation
 *  8. Agent identity edge cases                   -> 08_agent_identity_edge
 */
@Entry
@Component
struct ThemeDemoPage {
  private groups: ThemeGroup[] = [
    {
      title: '主题色渲染（primaryColor）',
      entries: [
        { dir: '01_primary_color_basic', name: 'primaryColor 一次性染色多组件', description: 'Button primary / Slider progressColor / Tabs 选中态 / CheckBox / ChoicePicker 等均应统一为主题色', color: '#E91E63' }
      ]
    },
    {
      title: '身份归因（Identity & Attribution）',
      entries: [
        { dir: '02_agent_identity', name: 'iconUrl + agentDisplayName', description: 'theme.iconUrl (URI) 与 agentDisplayName (String) 用于向用户展示 agent 身份', color: '#6C5CE7' }
      ]
    },
    {
      title: '完整主题（All Fields）',
      entries: [
        { dir: '03_theme_all_fields', name: '三字段全填 + 自定义扩展', description: '同时携带 primaryColor / iconUrl / agentDisplayName，并附加规范未定义的自定义键（additionalProperties=true 允许扩展）', color: '#F39C12' }
      ]
    },
    {
      title: '非法值 fallback（Invalid Fallback）',
      entries: [
        { dir: '04_invalid_color_fallback', name: 'primaryColor 非法值应回退默认', description: '非 hex / 3 位 / 8 位 / 缺 # / 非 hex 字符 / 空串 均应回退 #00BFFF，大写 6 位 hex 作为正向对照', color: '#E74C3C' }
      ]
    },
    {
      title: '默认主题（Default）',
      entries: [
        { dir: '05_default_no_theme', name: 'createSurface 不带 theme', description: 'theme 属可选。未携带时，primaryColor 走协议默认值 #00BFFF（浅蓝）', color: '#00BFFF' }
      ]
    },
    {
      title: '极端色（Extreme Colors）',
      entries: [
        { dir: '06_extreme_colors', name: 'primaryColor 极端色可读性', description: '纯黑 / 纯白 primaryColor 目视验证，暴露主题色对比度不当时的观感（不代表实现缺陷）', color: '#000000' }
      ]
    },
    {
      title: 'Button variant 隔离（Regression）',
      entries: [
        { dir: '07_button_variant_isolation', name: '仅 primary variant 应被染色', description: '同一主题下 primary / default / borderless 并列，验证仅 primary 背景应用主题色，其他 variant 不被误染', color: '#E91E63' }
      ]
    },
    {
      title: 'Agent 身份边界（Identity Edge）',
      entries: [
        { dir: '08_agent_identity_edge', name: '超长名称 / emoji / data URI', description: 'agentDisplayName 超长中英混排 + emoji，iconUrl 使用 data:image/png;base64,... 内联图片', color: '#009688' }
      ]
    }
  ];

  build() {
    Column() {
      // Header (magenta bar to distinguish from DataBindingDemo's blue)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text('主题测试')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI v0.9 协议 createSurface.theme 主题字段能力')
          .fontSize(12)
          .fontColor('#FFE0EC')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#E91E63')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(this.groups, (group: ThemeGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6 })

            ForEach(group.entries, (entry: ThemeEntry) => {
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
                  params: new ThemeTestParams(entry.dir, entry.name, 'a2ui_theme_tests', '主题')
                });
              })
            }, (entry: ThemeEntry) => entry.dir)
          }, (group: ThemeGroup) => group.title)
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

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
import { registerA2UICustomFunction, unregisterA2UICustomFunction } from '@kuikly-a2ui-open/a2ui-render';

/**
 * Custom function entry data.
 */
interface CustomFunctionEntry {
  dir: string;
  name: string;
  description: string;
  color: string;
}

/**
 * Custom function group data.
 */
interface CustomFunctionGroup {
  title: string;
  entries: CustomFunctionEntry[];
}

/**
 * Router params for ComponentTestPage navigation.
 */
class CustomFunctionTestParams {
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
 * Custom Function Demo Page.
 *
 * Demonstrates the A2UI custom function extension capability:
 *  1. Registers several custom functions globally via registerA2UICustomFunction
 *  2. Provides navigation to test cases grouped by return type
 *
 * The registered functions are:
 *  - getGreeting(name) → String
 *  - formatPhone(phone) → String
 *  - isVip(level) → Boolean
 *  - calculateDiscount(price, rate) → Number
 *  - logEvent(event, data) → Void
 *  - buildSummary(name, count, status) → String
 */
@Entry
@Component
struct CustomFunctionDemoPage {
  private groups: CustomFunctionGroup[] = [
    {
      title: '返回 String 的自定义函数',
      entries: [
        { dir: '01_string_return', name: 'getGreeting / formatPhone → String', description: '自定义函数返回字符串，绑定到 Text.text 属性。验证表达式求值路径的同步调用和返回值传递。', color: '#6C5CE7' }
      ]
    },
    {
      title: '返回 Boolean 的自定义函数',
      entries: [
        { dir: '02_boolean_return', name: 'isVip(level) → Boolean', description: '自定义函数返回布尔值，用于 check 条件控制组件显隐。', color: '#00B894' }
      ]
    },
    {
      title: '返回 Number 的自定义函数',
      entries: [
        { dir: '03_number_return', name: 'calculateDiscount(price, rate) → Number', description: '自定义函数返回数值，绑定到 Text.text。', color: '#F39C12' }
      ]
    },
    {
      title: 'Void 函数作为 Action',
      entries: [
        { dir: '04_void_action', name: 'logEvent(event, data) → Void', description: '自定义 void 函数作为 Button 的 action.functionCall。点击按钮触发。', color: '#E74C3C' }
      ]
    },
    {
      title: '多参数 + 数据绑定',
      entries: [
        { dir: '05_args_passing', name: 'buildSummary(name, count, status) → String', description: '多参数自定义函数，参数来自 dataModel 深层路径绑定。', color: '#0984E3' }
      ]
    }
  ];

  aboutToAppear(): void {
    this.registerCustomFunctions();
  }

  aboutToDisappear(): void {
    this.unregisterCustomFunctions();
  }

  /**
   * Register all demo custom functions.
   * This demonstrates the public API: registerA2UICustomFunction.
   */
  private registerCustomFunctions(): void {
    // 1. getGreeting(name) → String
    registerA2UICustomFunction('getGreeting', 'string', 'sync', (args) => {
      const name = (args['name'] as string) ?? 'World';
      return `Hello, ${name}!`;
    });

    // 2. formatPhone(phone) → String
    registerA2UICustomFunction('formatPhone', 'string', 'sync', (args) => {
      const phone = (args['phone'] as string) ?? '';
      if (phone.length === 11) {
        return `${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}`;
      }
      return phone;
    });

    // 3. isVip(level) → Boolean
    registerA2UICustomFunction('isVip', 'boolean', 'sync', (args) => {
      const level = (args['level'] as number) ?? 0;
      return level >= 3;
    });

    // 4. calculateDiscount(price, rate) → Number
    registerA2UICustomFunction('calculateDiscount', 'number', 'sync', (args) => {
      const price = (args['price'] as number) ?? 0;
      const rate = (args['rate'] as number) ?? 1;
      return Math.round(price * rate * 100) / 100;
    });

    // 5. logEvent(event, data) → Void (sync)
    registerA2UICustomFunction('logEvent', 'void', 'sync', (args) => {
      const event = (args['event'] as string) ?? '';
      const data = (args['data'] as string) ?? '';
      console.info(`📊 [CustomFunction] logEvent: event=${event}, data=${data}`);
      return null;
    });

    // 6. buildSummary(name, count, status) → String
    registerA2UICustomFunction('buildSummary', 'string', 'sync', (args) => {
      const name = (args['name'] as string) ?? 'Unknown';
      const count = (args['count'] as number) ?? 0;
      const status = (args['status'] as string) ?? 'unknown';
      return `项目「${name}」当前状态: ${status}，共 ${count} 个任务`;
    });

    console.info('✅ [CustomFunction] All custom functions registered');
  }

  private unregisterCustomFunctions(): void {
    unregisterA2UICustomFunction('getGreeting');
    unregisterA2UICustomFunction('formatPhone');
    unregisterA2UICustomFunction('isVip');
    unregisterA2UICustomFunction('calculateDiscount');
    unregisterA2UICustomFunction('logEvent');
    unregisterA2UICustomFunction('buildSummary');
    console.info('🗑️ [CustomFunction] All custom functions unregistered');
  }

  build() {
    Column() {
      // Header (teal bar)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text('自定义函数示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI 自定义函数扩展能力：注册 → 调用 → 返回值')
          .fontSize(12)
          .fontColor('#B2DFDB')
          .margin({ top: 4 })

        Text('✅ 已注册 6 个自定义函数: getGreeting, formatPhone, isVip, calculateDiscount, logEvent, buildSummary')
          .fontSize(11)
          .fontColor('#004D40')
          .backgroundColor('#E0F2F1')
          .padding(8)
          .margin({ top: 8 })
          .borderRadius(4)
      }
      .width('100%')
      .backgroundColor('#00897B')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(this.groups, (group: CustomFunctionGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6 })

            ForEach(group.entries, (entry: CustomFunctionEntry) => {
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
                params: new CustomFunctionTestParams(entry.dir, entry.name, 'a2ui_custom_functions', '自定义函数')
                });
              })
            }, (entry: CustomFunctionEntry) => entry.dir)
          }, (group: CustomFunctionGroup) => group.title)
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

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

import { router, promptAction } from '@kit.ArkUI';
import { registerA2UICustomFunction, unregisterA2UICustomFunction } from '@kuikly-a2ui-open/a2ui-render';

/**
 * KuiklyPageCustomFunctionDemoPage — custom-function demo list page.
 *
 * Mirrors [CustomFunctionDemoActivity] on Android. On appear, registers
 * 6 custom functions globally through `registerA2UICustomFunction`; on
 * disappear, unregisters them so other pages start clean. Tapping any
 * entry navigates to the shared `A2UIComponentTestPage` with
 * `rootDir=a2ui_custom_functions`, which loads the matching JSON case
 * and renders it via `A2UISurface`. The A2UI runtime dispatches
 * `functionCall` protocol messages to the handlers registered here.
 *
 * Six functions:
 *   getGreeting(name) → String
 *   formatPhone(phone) → String
 *   isVip(level) → Boolean
 *   calculateDiscount(price, rate) → Number
 *   logEvent(event, data) → Void   (side-effect only, shows a toast)
 *   buildSummary(name, count, status) → String
 */

class FunctionEntry {
  dir: string = '';
  name: string = '';
  desc: string = '';
  color: string = '';

  constructor(dir: string, name: string, desc: string, color: string) {
    this.dir = dir;
    this.name = name;
    this.desc = desc;
    this.color = color;
  }
}

class FunctionGroup {
  title: string = '';
  entries: FunctionEntry[] = [];

  constructor(title: string, entries: FunctionEntry[]) {
    this.title = title;
    this.entries = entries;
  }
}

const GROUPS: FunctionGroup[] = [
  new FunctionGroup('返回 String 的自定义函数', [
    new FunctionEntry(
      '01_string_return',
      'getGreeting / formatPhone → String',
      '自定义函数返回字符串，绑定到 Text.text 属性。验证表达式求值路径的同步调用和返回值传递。',
      '#6C5CE7'
    )
  ]),
  new FunctionGroup('返回 Boolean 的自定义函数', [
    new FunctionEntry(
      '02_boolean_return',
      'isVip(level) → Boolean',
      '自定义函数返回布尔值，用于 check 条件控制组件显隐。验证 evaluateCheckFunction 兜底路径。',
      '#00B894'
    )
  ]),
  new FunctionGroup('返回 Number 的自定义函数', [
    new FunctionEntry(
      '03_number_return',
      'calculateDiscount(price, rate) → Number',
      '自定义函数返回数值，绑定到 Text.text。验证数值类型的同步返回和 coercion。',
      '#F39C12'
    )
  ]),
  new FunctionGroup('Void 函数作为 Action', [
    new FunctionEntry(
      '04_void_action',
      'logEvent(event, data) → Void',
      '自定义 void 函数作为 Button 的 action.functionCall。点击按钮触发，验证 executeFunctionCallIfNeeded 拦截。',
      '#E74C3C'
    )
  ]),
  new FunctionGroup('多参数 + 数据绑定', [
    new FunctionEntry(
      '05_args_passing',
      'buildSummary(name, count, status) → String',
      '多参数自定义函数，参数来自 dataModel 深层路径绑定。验证 resolveDynamicArg 对自定义函数参数的处理。',
      '#0984E3'
    )
  ])
];

const CUSTOM_FUNCTION_NAMES: string[] = [
  'getGreeting', 'formatPhone', 'isVip', 'calculateDiscount', 'logEvent', 'buildSummary'
];

@Entry
@Component
struct KuiklyPageCustomFunctionDemoPage {

  aboutToAppear(): void {
    // 1. getGreeting(name) → String
    registerA2UICustomFunction('getGreeting', 'string', 'sync',
      (args: Record<string, Object>): Object | null => {
        const name: string = args['name'] != null ? String(args['name']) : 'World';
        return `Hello, ${name}!`;
      });

    // 2. formatPhone(phone) → String — split "13800001234" → "138-0000-1234"
    registerA2UICustomFunction('formatPhone', 'string', 'sync',
      (args: Record<string, Object>): Object | null => {
        const phone: string = args['phone'] != null ? String(args['phone']) : '';
        if (phone.length === 11) {
          return `${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}`;
        }
        return phone;
      });

    // 3. isVip(level) → Boolean
    registerA2UICustomFunction('isVip', 'boolean', 'sync',
      (args: Record<string, Object>): Object | null => {
        const level: number = args['level'] != null ? Number(args['level']) : 0;
        return level >= 3;
      });

    // 4. calculateDiscount(price, rate) → Number, rounded to 2 decimals
    registerA2UICustomFunction('calculateDiscount', 'number', 'sync',
      (args: Record<string, Object>): Object | null => {
        const price: number = args['price'] != null ? Number(args['price']) : 0;
        const rate: number = args['rate'] != null ? Number(args['rate']) : 1;
        return Math.round(price * rate * 100) / 100;
      });

    // 5. logEvent(event, data) → Void — show a toast to prove the call landed
    registerA2UICustomFunction('logEvent', 'void', 'sync',
      (args: Record<string, Object>): Object | null => {
        const event: string = args['event'] != null ? String(args['event']) : '';
        const data: string = args['data'] != null ? String(args['data']) : '';
        console.info(`[CustomFunctionDemo] logEvent event=${event}, data=${data}`);
        promptAction.showToast({ message: `logEvent: ${event} (${data})` });
        return null;
      });

    // 6. buildSummary(name, count, status) → String
    registerA2UICustomFunction('buildSummary', 'string', 'sync',
      (args: Record<string, Object>): Object | null => {
        const name: string = args['name'] != null ? String(args['name']) : 'Unknown';
        const count: number = args['count'] != null ? Number(args['count']) : 0;
        const status: string = args['status'] != null ? String(args['status']) : 'unknown';
        return `项目「${name}」当前状态: ${status}，共 ${count} 个任务`;
      });
  }

  aboutToDisappear(): void {
    // Clean up so entering other demo pages doesn't inherit these overrides.
    CUSTOM_FUNCTION_NAMES.forEach((name: string): void => {
      unregisterA2UICustomFunction(name);
    });
  }

  private openTest(entry: FunctionEntry): void {
    router.pushUrl({
      url: 'pages/A2UIComponentTestPage',
      params: {
        'componentDir': entry.dir,
        'componentName': entry.name,
        'rootDir': 'a2ui_custom_functions',
        'titleSuffix': '自定义函数'
      }
    }).catch((): void => {
      promptAction.showToast({ message: `${entry.name} 暂未提供测试用例` });
    });
  }

  build() {
    Column() {
      // Header (teal — matches Android #00897B)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('自定义函数示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('A2UI 自定义函数扩展能力：注册 → 调用 → 返回值')
          .fontSize(12)
          .fontColor('#B2DFDB')
          .margin({ top: 4 })

        Text('✅ 已注册 6 个自定义函数: getGreeting, formatPhone, isVip, calculateDiscount, logEvent, buildSummary')
          .fontSize(11)
          .fontColor('#004D40')
          .backgroundColor('#E0F2F1')
          .padding({ left: 16, right: 16, top: 8, bottom: 8 })
          .margin({ top: 8, left: -24, right: -24 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 0 })
      .backgroundColor('#00897B')
      .alignItems(HorizontalAlign.Start)

      // Scrollable groups
      Scroll() {
        Column() {
          ForEach(GROUPS, (group: FunctionGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6, left: 4 })

            ForEach(group.entries, (entry: FunctionEntry) => {
              Row() {
                Column()
                  .width(4)
                  .height(40)
                  .backgroundColor(entry.color)
                  .borderRadius(3)
                  .margin({ right: 12 })

                Column() {
                  Text(entry.name)
                    .fontSize(15)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#333333')
                  Text(entry.desc)
                    .fontSize(12)
                    .fontColor('#888888')
                    .margin({ top: 3 })
                }
                .layoutWeight(1)
                .alignItems(HorizontalAlign.Start)

                Text('›')
                  .fontSize(18)
                  .fontColor('#CCCCCC')
                  .padding({ left: 8 })
              }
              .width('100%')
              .padding({ left: 12, right: 12, top: 14, bottom: 14 })
              .backgroundColor(Color.White)
              .borderRadius(8)
              .margin({ top: 6 })
              .onClick(() => this.openTest(entry))
            }, (entry: FunctionEntry) => entry.dir)
          }, (group: FunctionGroup) => group.title)
        }
        .padding({ left: 12, right: 12, top: 8, bottom: 16 })
        .width('100%')
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

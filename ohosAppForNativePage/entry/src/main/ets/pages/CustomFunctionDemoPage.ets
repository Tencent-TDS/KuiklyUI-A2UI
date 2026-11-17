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
import { registerA2UICustomFunction, registerA2UICatalogFunction, A2UICustomFunctionHandler } from '@kuikly-a2ui-open/render';

export function installCustomFunctionDemo(): void {
  registerDemoFunction('getGreeting', 'string', 'sync', (args) => {
    const name = (args['name'] as string) ?? 'World';
    return `Hello, ${name}!`;
  });
  registerDemoFunction('formatPhone', 'string', 'sync', (args) => {
    const phone = (args['phone'] as string) ?? '';
    if (phone.length === 11) {
      return `${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}`;
    }
    return phone;
  });
  registerDemoFunction('isVip', 'boolean', 'sync', (args) => {
    const level = (args['level'] as number) ?? 0;
    return level >= 3;
  });
  registerDemoFunction('calculateDiscount', 'number', 'sync', (args) => {
    const price = (args['price'] as number) ?? 0;
    const rate = (args['rate'] as number) ?? 1;
    return Math.round(price * rate * 100) / 100;
  });
  registerDemoFunction('logEvent', 'void', 'sync', (args) => {
    const event = (args['event'] as string) ?? '';
    const data = (args['data'] as string) ?? '';
    console.info(`[CustomFunction] logEvent: event=${event}, data=${data}`);
    return null;
  });
  registerDemoFunction('buildSummary', 'string', 'sync', (args) => {
    const name = (args['name'] as string) ?? 'Unknown';
    const count = (args['count'] as number) ?? 0;
    const status = (args['status'] as string) ?? 'unknown';
    return `项目「${name}」当前状态: ${status}，共 ${count} 个任务`;
  });
  registerA2UICatalogFunction(
    'fetchUser',
    'custom_functions_catalog_v1_0.json',
    '{"returnType":"object","invokeMode":"sync","allowedCallers":"agentOnly","args":{"type":"object","properties":{"id":{"type":"number"}},"required":["id"],"additionalProperties":false}}',
    null
  );
}

function registerDemoFunction(name: string, returnType: string, invokeMode: string, handler: A2UICustomFunctionHandler): void {
  let argsSchema: string;
  switch (name) {
    case 'getGreeting':
      argsSchema = '{"type":"object","properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false}';
      break;
    case 'formatPhone':
      argsSchema = '{"type":"object","properties":{"phone":{"type":"string"}},"required":["phone"],"additionalProperties":false}';
      break;
    case 'isVip':
      argsSchema = '{"type":"object","properties":{"level":{"type":"number"}},"required":["level"],"additionalProperties":false}';
      break;
    case 'calculateDiscount':
      argsSchema = '{"type":"object","properties":{"price":{"type":"number"},"rate":{"type":"number"}},"required":["price","rate"],"additionalProperties":false}';
      break;
    case 'logEvent':
      argsSchema = '{"type":"object","properties":{"event":{"type":"string"},"data":{"type":"string"}},"required":["event"],"additionalProperties":false}';
      break;
    case 'buildSummary':
      argsSchema = '{"type":"object","properties":{"name":{"type":"string"},"count":{"type":"number"},"status":{"type":"string"}},"required":["name","count","status"],"additionalProperties":false}';
      break;
    default:
      throw new Error(`Unknown custom demo function: ${name}`);
  }
  registerA2UICustomFunction(name, returnType, invokeMode, handler);
  registerA2UICatalogFunction(
    name,
    'custom_functions_catalog_v1_0.json',
    `{"returnType":"${returnType}","invokeMode":"${invokeMode}","allowedCallers":"rendererOrAgent","requiresUserActivation":false,"args":${argsSchema}}`,
    handler
  );
}

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
        { dir: '02_boolean_return', name: 'isVip(level) → Boolean', description: '展示 isVip Boolean 返回值，按钮以官方 numeric ValidationResult 校验 VIP 阈值。', color: '#00B894' }
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
    installCustomFunctionDemo();
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

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
import { FunctionEntry, FunctionGroup } from '../a2ui/A2UIDemoAssetUtil';

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
 * Function Demo Page.
 * Displays the 14 built-in A2UI v0.9 catalog functions grouped by category.
 */
@Entry
@Component
struct FunctionDemoPage {
  private groups: FunctionGroup[] = [
    {
      title: '校验函数（Validation, returns Boolean）',
      entries: [
        { name: 'required', description: '非空校验：值不为 null / undefined / 空串', color: '#E74C3C' },
        { name: 'regex', description: '正则匹配：value 必须匹配指定 pattern', color: '#C0392B' },
        { name: 'length', description: '长度约束：字符串长度落在 [min,max]', color: '#D35400' },
        { name: 'numeric', description: '数值范围：数字落在 [min,max]', color: '#E67E22' },
        { name: 'email', description: '邮箱格式：value 必须是合法 email', color: '#F39C12' }
      ]
    },
    {
      title: '格式化函数（Formatting, returns String）',
      entries: [
        { name: 'formatString', description: '字符串插值：${path} / ${fn(...)}', color: '#4A90D9' },
        { name: 'formatNumber', description: '数值格式化：小数位、千位分隔', color: '#3498DB' },
        { name: 'formatCurrency', description: '货币格式化：ISO4217 currency', color: '#2C3E50' },
        { name: 'formatDate', description: '日期格式化：Unicode TR35 pattern', color: '#16A085' },
        { name: 'pluralize', description: '复数选择：CLDR zero/one/two/few/many/other', color: '#27AE60' }
      ]
    },
    {
      title: '副作用函数（Side-effect, returns Void）',
      entries: [
        { name: 'openUrl', description: '打开外链：仅用于 Action.functionCall', color: '#8E44AD' }
      ]
    },
    {
      title: '逻辑函数（Logic, returns Boolean）',
      entries: [
        { name: 'and', description: '逻辑与：所有子条件全为 true', color: '#6C5CE7' },
        { name: 'or', description: '逻辑或：至少一个子条件为 true', color: '#9B59B6' },
        { name: 'not', description: '逻辑非：对 boolean 取反', color: '#34495E' }
      ]
    }
  ];

  build() {
    Column() {
      // Header (green bar)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text('函数测试')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI v0.9 basic catalog 内建 14 个函数')
          .fontSize(12)
          .fontColor('#DDFFF5')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#00B894')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(this.groups, (group: FunctionGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6 })

            ForEach(group.entries, (entry: FunctionEntry) => {
              Row() {
                // Color indicator
                Column()
                  .width(4)
                  .height(36)
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
                  params: new ComponentTestParams(entry.name, entry.name, 'a2ui_function_tests', '函数测试')
                });
              })
            }, (entry: FunctionEntry) => entry.name)
          }, (group: FunctionGroup) => group.title)
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

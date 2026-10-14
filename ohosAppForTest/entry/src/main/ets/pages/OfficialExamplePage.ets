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
import { A2UIDemoAssetUtil, TestCaseData } from '../a2ui/A2UIDemoAssetUtil';
import { A2UISurface, A2UISurfaceViewListener } from '../a2ui/A2UISurface';

/**
 * Official Example Page.
 * Displays all 36 A2UI v0.9 official example cards from the specification.
 */
@Entry
@Component
struct OfficialExamplePage {
  @State examples: TestCaseData[] = [];
  private resMgr = getContext(this).resourceManager;

  aboutToAppear(): void {
    this.loadExamples();
  }

  private loadExamples(): void {
    try {
      const manifestStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, 'a2ui_manifest.json');
      const manifest = JSON.parse(manifestStr) as Record<string, Object>;
      const exampleFiles = manifest['examples'] as string[];
      if (!exampleFiles) return;

      for (const fileName of exampleFiles) {
        const jsonStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, `a2ui_examples/${fileName}`);
        if (jsonStr.length === 0) continue;
        const testCase = A2UIDemoAssetUtil.parseTestCase(jsonStr);
        if (testCase) {
          if (testCase.name.length === 0) {
            testCase.name = fileName;
          }
          this.examples.push(testCase);
        }
      }
    } catch (e) {
      console.error(`[OfficialExamplePage] Failed to load examples: ${e}`);
    }
  }

  build() {
    Column() {
      // Title bar
      Row() {
        Text('←')
          .fontSize(20)
          .fontColor('#333333')
          .onClick(() => router.back())
          .padding({ right: 8 })
        Text('A2UI v0.9 协议官方示例')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
      }
      .width('100%')
      .padding({ left: 16, top: 48, right: 16, bottom: 12 })
      .alignItems(VerticalAlign.Center)

      // Subtitle
      Text('展示 A2UI v0.9 协议规范中的 36 个官方示例，JSON 内容无任何修改')
        .fontSize(13)
        .fontColor('#888888')
        .padding({ left: 16, right: 16, bottom: 12 })

      // Scrollable content
      Scroll() {
        Column() {
          ForEach(this.examples, (item: TestCaseData, index: number) => {
            // Card label
            Text(`${index + 1}. ${item.name}`)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#333333')
              .padding({ left: 4, top: 12, bottom: 4 })

            if (item.description.length > 0) {
              Text(item.description)
                .fontSize(11)
                .fontColor('#888888')
                .padding({ left: 4, bottom: 6 })
            }

            // Card container - transparent wrapper
            A2UISurface({
              config: '{"debug": true}',
              messages: item.messages,
              finished: true
            })
              .margin({ bottom: 4 })
          }, (item: TestCaseData, index: number) => `example_${index}`)
        }
        .padding({ left: 12, right: 12, bottom: 16 })
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

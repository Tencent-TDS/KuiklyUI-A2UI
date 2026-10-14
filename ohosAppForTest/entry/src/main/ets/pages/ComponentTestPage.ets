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
 * Component Test Page.
 * Renders all test-case JSON files for a single A2UI component.
 * Receives params: componentDir, componentName, rootDir, titleSuffix.
 */
@Entry
@Component
struct ComponentTestPage {
  @State testCases: TestCaseData[] = [];
  @State componentName: string = '';
  @State titleSuffix: string = '组件测试';
  @State rootDir: string = 'a2ui_components';
  @State componentDir: string = '';
  private resMgr = getContext(this).resourceManager;

  aboutToAppear(): void {
    const params = router.getParams() as Record<string, string>;
    if (params) {
      this.componentDir = params['componentDir'] || '';
      this.componentName = params['componentName'] || this.componentDir;
      this.rootDir = params['rootDir'] || 'a2ui_components';
      this.titleSuffix = params['titleSuffix'] || '组件测试';
    }
    this.loadTestCases();
  }

  private loadTestCases(): void {
    if (this.componentDir.length === 0) return;

    try {
      const manifestStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, 'a2ui_manifest.json');

      // Determine which section of the manifest to use
      // Use JSON string parsing to avoid variable-indexed property access
      let fileList: string[] = [];
      if (this.rootDir === 'a2ui_components') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'components');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_function_tests') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'function_tests');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_databinding_tests') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'databinding_tests');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_theme_tests') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'theme_tests');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_custom_functions') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'custom_functions');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_custom_components') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'custom_components');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      }

      if (fileList.length === 0) {
        console.warn(`[ComponentTestPage] No files found for ${this.rootDir}/${this.componentDir}`);
        return;
      }

      for (const fileName of fileList) {
        const filePath = `${this.rootDir}/${this.componentDir}/${fileName}`;
        const jsonStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, filePath);
        if (jsonStr.length === 0) continue;

        const testCase = A2UIDemoAssetUtil.parseTestCase(jsonStr);
        if (testCase) {
          if (testCase.name.length === 0) {
            testCase.name = fileName;
          }
          this.testCases.push(testCase);
        }
      }
    } catch (e) {
      console.error(`[ComponentTestPage] Failed to load test cases: ${e}`);
    }
  }

  build() {
    Column() {
      // Header (purple bar)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text(`${this.componentName} ${this.titleSuffix}`)
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text(`测试用例目录：${this.rootDir}/${this.componentDir}/`)
          .fontSize(12)
          .fontColor('#DDDDFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#6C5CE7')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable content
      Scroll() {
        Column() {
          if (this.testCases.length === 0) {
            Text(`暂无测试用例\n(${this.rootDir}/${this.componentDir} 为空或不存在)`)
              .fontSize(14)
              .fontColor('#999999')
              .textAlign(TextAlign.Center)
              .padding({ top: 48, bottom: 48 })
          } else {
            ForEach(this.testCases, (item: TestCaseData, index: number) => {
              // Title row
              Row() {
                Text(`${index + 1}. ${item.name}`)
                  .fontSize(13)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#333333')
                  .layoutWeight(1)
              }
              .width('100%')
              .padding({ left: 4, top: 12, right: 4, bottom: 4 })

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
            }, (item: TestCaseData, index: number) => `test_${index}`)
          }
        }
        .padding({ left: 12, top: 8, right: 12, bottom: 16 })
        .width('100%')
        .justifyContent(FlexAlign.Start)
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

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

import { router, ComponentContent } from '@kit.ArkUI';
import { registerA2UICustomRenderView, unregisterA2UICustomRenderView, A2UICustomViewSize } from '@kuikly-a2ui-open/a2ui-render';
import { KuiklyRenderBaseView, KuiklyRenderCallback, KRValue } from '@kuikly-open/render';
import { UIContext } from '@ohos.arkui.UIContext';
import { webview } from '@kit.ArkWeb';

/**
 * Custom component entry data.
 */
interface CustomComponentEntry {
  dir: string;
  name: string;
  description: string;
  color: string;
}

/**
 * Custom component group data.
 */
interface CustomComponentGroup {
  title: string;
  entries: CustomComponentEntry[];
}

/**
 * Router params for ComponentTestPage navigation.
 */
class CustomComponentTestParams {
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
 * Custom Component Demo Page.
 *
 * Demonstrates the A2UI custom component extension capability:
 *  1. Registers two custom render views globally via registerA2UICustomRenderView
 *  2. Provides navigation to test cases for each custom component
 *
 * The registered custom components are:
 *  - A2UIWeb: A Web component that supports a "source" property to load URLs
 *  - MyA2UIView: A simple custom view that supports "text" and "textColor" properties
 */
@Entry
@Component
struct CustomComponentDemoPage {
  private groups: CustomComponentGroup[] = [
    {
      title: 'WebView 自定义组件',
      entries: [
        { dir: '01_web_view', name: 'A2UIWeb → Web', description: '自定义 Web 组件，支持 source 属性加载网页。演示通过 registerA2UICustomRenderView 注册自定义组件。', color: '#E91E63' }
      ]
    },
    {
      title: '简单自定义组件',
      entries: [
        { dir: '02_my_view', name: 'MyA2UIView → Text', description: '最简单的自定义组件，支持 text 和 textColor 属性。演示自定义属性处理。', color: '#6200EE' }
      ]
    }
  ];

  aboutToAppear(): void {
    this.registerCustomComponents();
  }

  aboutToDisappear(): void {
    this.unregisterCustomComponents();
  }

  /**
   * Register all demo custom components.
   * This demonstrates the public API: registerA2UICustomRenderView.
   *
   * Each call ships with a `measurer` so the Flex engine learns the
   * component's intrinsic size on OHOS without needing a hard-coded
   * `height` in the JSON spec. The same JSON files render identically on
   * Android / iOS (their native Shadow mechanisms already return the same
   * numbers from the dedicated Shadow classes).
   */
  private registerCustomComponents(): void {
    // 1. A2UIWeb: A Web component with "source" property.
    //    Intrinsic size: full parent cross-axis width, fixed 240vp height.
    registerA2UICustomRenderView(
      'A2UIWeb',
      (): KuiklyRenderBaseView => new A2UIWebRenderView(),
      (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 240 })
    );

    // 2. MyA2UIView: A simple custom view with "text" and "textColor" properties.
    //    Intrinsic size: full parent cross-axis width, fixed 200vp height.
    registerA2UICustomRenderView(
      'MyA2UIView',
      (): KuiklyRenderBaseView => new MyA2UIRenderView(),
      (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 200 })
    );

    console.info('✅ [CustomComponent] All custom components registered: A2UIWeb, MyA2UIView');
  }

  private unregisterCustomComponents(): void {
    unregisterA2UICustomRenderView('A2UIWeb');
    unregisterA2UICustomRenderView('MyA2UIView');
    console.info('🗑️ [CustomComponent] All custom components unregistered');
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
          Text('自定义组件示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI 自定义组件扩展能力：注册 → 创建 → 属性设置')
          .fontSize(12)
          .fontColor('#CE93D8')
          .margin({ top: 4 })

        Text('✅ 已注册 2 个自定义组件: A2UIWeb (Web), MyA2UIView (Text)')
          .fontSize(11)
          .fontColor('#4A148C')
          .backgroundColor('#EDE7F6')
          .padding(8)
          .margin({ top: 8 })
          .borderRadius(4)
      }
      .width('100%')
      .backgroundColor('#6200EE')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(this.groups, (group: CustomComponentGroup) => {
            // Section header
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6 })

            ForEach(group.entries, (entry: CustomComponentEntry) => {
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
                params: new CustomComponentTestParams(entry.dir, entry.name, 'a2ui_custom_components', '自定义组件')
                });
              })
            }, (entry: CustomComponentEntry) => entry.dir)
          }, (group: CustomComponentGroup) => group.title)
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

// ============================================================
// MyA2UIView Custom Component Implementation
// ============================================================

@Builder
function createMyA2UIView(view: KuiklyRenderBaseView) {
  Column() {
    Text((view as MyA2UIRenderView).text)
      .fontSize(16)
      .fontColor((view as MyA2UIRenderView).textColor)
      .textAlign(TextAlign.Center)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#E8F5E9')
  .justifyContent(FlexAlign.Center)
  .alignItems(HorizontalAlign.Center)
}

/**
 * MyA2UIRenderView: A simple custom render view for A2UI on HarmonyOS.
 * Supports "text" and "textColor" properties.
 * Background color is set to #E8F5E9 (light mint green).
 */
export class MyA2UIRenderView extends KuiklyRenderBaseView {
  text: string = '';
  textColor: string = '#333333';

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'text':
        this.text = propValue as string;
        this.updateArkUI();
        return true;
      case 'textColor':
        this.textColor = propValue as string;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {
    // No methods for this simple demo view
  }

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createMyA2UIView),
      this
    );
  }
}

// ============================================================
// A2UIWebRenderView Custom Component Implementation
// ============================================================

@Builder
function createA2UIWebView(view: KuiklyRenderBaseView) {
  Web({ src: (view as A2UIWebRenderView).source, controller: (view as A2UIWebRenderView).webController })
    .width('100%')
    .height('100%')
}

/**
 * A2UIWebRenderView: A custom render view wrapping Web component for A2UI on HarmonyOS.
 * Supports "source" property to load URLs.
 */
export class A2UIWebRenderView extends KuiklyRenderBaseView {
  source: string = '';
  webController: webview.WebviewController = new webview.WebviewController();

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'source':
        this.source = propValue as string;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {
    // No methods for this demo view
  }

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIWebView),
      this
    );
  }
}

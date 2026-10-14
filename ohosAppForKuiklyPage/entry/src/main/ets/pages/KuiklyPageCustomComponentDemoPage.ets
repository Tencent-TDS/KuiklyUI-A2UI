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
import { ComponentContent } from '@kit.ArkUI';
import { webview } from '@kit.ArkWeb';
import {
  KRAny,
  KuiklyRenderBaseView,
  KuiklyRenderCallback
} from '@kuikly-open/render';
import {
  registerA2UICustomRenderView,
  unregisterA2UICustomRenderView
} from '@kuikly-open/a2ui-render';

/**
 * KuiklyPageCustomComponentDemoPage — custom render-view demo list page.
 *
 * Mirrors [CustomComponentDemoActivity] on Android. Registers two custom
 * render views globally through `registerA2UICustomRenderView` on appear,
 * unregisters on disappear. Tapping an entry navigates to
 * `A2UIComponentTestPage` with `rootDir=a2ui_custom_components`.
 *
 * Registered views:
 *   - `A2UIWeb`      → simplified ohos `Web` component (setProp `source`
 *                      → loadUrl). Skips Android's disallow-intercept
 *                      gesture plumbing — sufficient for the demo card.
 *   - `MyA2UIView`   → simple text panel (setProp `text`, `textColor`).
 *
 * ─── ohos-specific note (Q3=γ) ────────────────────────────────────────
 * On Android the equivalent classes implement `IKuiklyRenderViewExport`
 * on top of `FrameLayout` plus a `Shadow` for intrinsic size. On ohos we
 * extend `KuiklyRenderBaseView` and rely on the parent A2UI Row/Column
 * to size us — the DSL cases we ship in `a2ui_custom_components` already
 * set an explicit `height` on the wrapping container so no Shadow is
 * needed here.
 */

class ComponentEntry {
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

class ComponentGroup {
  title: string = '';
  entries: ComponentEntry[] = [];

  constructor(title: string, entries: ComponentEntry[]) {
    this.title = title;
    this.entries = entries;
  }
}

const GROUPS: ComponentGroup[] = [
  new ComponentGroup('WebView 自定义组件', [
    new ComponentEntry(
      '01_web_view',
      'A2UIWeb → WebView',
      '自定义 WebView 组件，支持 source 属性加载网页。演示通过 registerA2UICustomRenderView 注册自定义组件。',
      '#E91E63'
    )
  ]),
  new ComponentGroup('简单自定义组件', [
    new ComponentEntry(
      '02_my_view',
      'MyA2UIView → TextView',
      '最简单的自定义组件，支持 text 和 textColor 属性。演示自定义属性处理。',
      '#6200EE'
    )
  ])
];

@Entry
@Component
struct KuiklyPageCustomComponentDemoPage {

  aboutToAppear(): void {
    registerA2UICustomRenderView('A2UIWeb', (): KuiklyRenderBaseView => new A2UIWebRenderView());
    registerA2UICustomRenderView('MyA2UIView', (): KuiklyRenderBaseView => new MyA2UIRenderView());
  }

  aboutToDisappear(): void {
    unregisterA2UICustomRenderView('A2UIWeb');
    unregisterA2UICustomRenderView('MyA2UIView');
  }

  private openTest(entry: ComponentEntry): void {
    router.pushUrl({
      url: 'pages/A2UIComponentTestPage',
      params: {
        'componentDir': entry.dir,
        'componentName': entry.name,
        'rootDir': 'a2ui_custom_components',
        'titleSuffix': '自定义组件'
      }
    }).catch((): void => {
      promptAction.showToast({ message: `${entry.name} 暂未提供测试用例` });
    });
  }

  build() {
    Column() {
      // Header (purple bar — matches Android #6200EE)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('自定义组件示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('A2UI 自定义组件扩展能力：注册 → 创建 → 属性设置')
          .fontSize(12)
          .fontColor('#CE93D8')
          .margin({ top: 4 })

        Text('✅ 已注册 2 个自定义组件: A2UIWeb (Web), MyA2UIView (TextView)')
          .fontSize(11)
          .fontColor('#4A148C')
          .backgroundColor('#EDE7F6')
          .padding({ left: 16, right: 16, top: 8, bottom: 8 })
          .margin({ top: 8, left: -24, right: -24 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 0 })
      .backgroundColor('#6200EE')
      .alignItems(HorizontalAlign.Start)

      Scroll() {
        Column() {
          ForEach(GROUPS, (group: ComponentGroup) => {
            Text(group.title)
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
              .fontColor('#555555')
              .margin({ top: 16, bottom: 6, left: 4 })

            ForEach(group.entries, (entry: ComponentEntry) => {
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
            }, (entry: ComponentEntry) => entry.dir)
          }, (group: ComponentGroup) => group.title)
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

// ============================================================
// Custom render-view implementations (registered by aboutToAppear)
// ============================================================

/**
 * A2UIWeb — ohos Web-component-backed custom render view.
 *
 * Simplified from Android's `A2UIWebView` (Q3=γ): we skip the manual
 * disallow-intercept gesture plumbing because ohos Web already claims
 * vertical drags inside its own bounds; A2UI-tested JSON cases wrap us
 * in a Card with an explicit height so the flex engine does not need a
 * Shadow-computed intrinsic size.
 */
@Observed
export class A2UIWebRenderView extends KuiklyRenderBaseView {
  cssSource: string = '';

  call(_method: string, _params: KRAny, _callback: KuiklyRenderCallback | null): void {
    // No imperative methods exposed today.
  }

  setProp(propKey: string, propValue: KRAny | KuiklyRenderCallback): boolean {
    if (propKey === 'source') {
      this.cssSource = (propValue as string) ?? '';
      return true;
    }
    return super.setProp(propKey, propValue);
  }

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    const uiContext = this.getUIContext() as UIContext;
    return new ComponentContent<KuiklyRenderBaseView>(
      uiContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIWebArkUI),
      this
    );
  }
}

@Component
struct A2UIWebArkUI {
  @ObjectLink renderView: A2UIWebRenderView;
  private controller: WebviewController = new webview.WebviewController();

  build() {
    Column() {
      if (this.renderView.cssSource.length > 0) {
        Web({
          src: this.renderView.cssSource,
          controller: this.controller
        })
          .width('100%')
          .height('100%')
          .domStorageAccess(true)
          .javaScriptAccess(true)
      } else {
        Text('A2UIWeb (no source)')
          .fontSize(14)
          .fontColor('#999999')
          .width('100%')
          .height('100%')
          .textAlign(TextAlign.Center)
      }
    }
    .width('100%')
    .height('100%')
  }
}

@Builder
function createA2UIWebArkUI(view: KuiklyRenderBaseView) {
  A2UIWebArkUI({ renderView: view as A2UIWebRenderView });
}

/**
 * MyA2UIView — simple text-panel custom render view.
 * Supports the `text` and `textColor` props expected by the demo JSON
 * cases in `a2ui_custom_components/02_my_view/*.json`.
 */
@Observed
export class MyA2UIRenderView extends KuiklyRenderBaseView {
  cssText: string = '';
  cssTextColor: string = '#333333';

  call(_method: string, _params: KRAny, _callback: KuiklyRenderCallback | null): void {
    // No imperative methods exposed today.
  }

  setProp(propKey: string, propValue: KRAny | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'text':
        this.cssText = (propValue as string) ?? '';
        return true;
      case 'textColor':
        this.cssTextColor = (propValue as string) ?? '#333333';
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    const uiContext = this.getUIContext() as UIContext;
    return new ComponentContent<KuiklyRenderBaseView>(
      uiContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createMyA2UIArkUI),
      this
    );
  }
}

@Component
struct MyA2UIArkUI {
  @ObjectLink renderView: MyA2UIRenderView;

  build() {
    Column() {
      Text(this.renderView.cssText)
        .fontSize(16)
        .fontColor(this.renderView.cssTextColor)
        .textAlign(TextAlign.Center)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#E8F5E9')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
}

@Builder
function createMyA2UIArkUI(view: KuiklyRenderBaseView) {
  MyA2UIArkUI({ renderView: view as MyA2UIRenderView });
}

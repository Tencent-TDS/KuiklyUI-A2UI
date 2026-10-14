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
import { window } from '@kit.ArkUI';
import { common } from '@kit.AbilityKit';

/**
 * Parameters for a demo entry item.
 */
class DemoEntryParams {
  title: string = '';
  description: string = '';
  iconText: string = '';
  color: string = '';
  onClick: () => void = () => {};

  constructor(title: string, description: string, iconText: string, color: string, onClick: () => void) {
    this.title = title;
    this.description = description;
    this.iconText = iconText;
    this.color = color;
    this.onClick = onClick;
  }
}

/**
 * A2UI Kuikly Page Demo entry.
 *
 * Structure mirrors `androidAppForKuiklyPage/A2UIDemoActivity` exactly:
 *   Group 1 — Kuikly 页面接入示例：
 *     卡片场景接入示例 / 自定义函数示例 / 自定义组件示例 /
 *     自定义 Icon 图片 / Compose 页面接入示例
 *   Group 2 — 组件展示：
 *     组件展示
 *
 * All demos here integrate A2UI on the *Kuikly Page* path: the host page
 * is a Kuikly Pager (rendered via KuiklyRenderView), and A2UI content is
 * embedded inside it via `A2UISurface { … }` DSL. Native View-only demos
 * live in the sibling `ohosAppForNativePage`.
 *
 * The five target pages are currently placeholders — actual KuiklyRenderView
 * wiring will land once the ohos Kuikly Render bridge (Phase 1) is finished.
 */
@Entry
@Component
struct Index {
  @State statusBarHeightVp: number = 0;

  aboutToAppear(): void {
    this.resolveStatusBarHeight();
  }

  /**
   * Query the system status-bar height via the current window's avoid-area,
   * then convert the returned px value to vp so it can be plugged into ArkUI
   * padding directly. Falls back silently to 0 on any failure so the page
   * still renders (worst case the title text hugs the status bar again, but
   * background colour still expands via `expandSafeArea`).
   */
  private resolveStatusBarHeight(): void {
    try {
      const ctx = getContext(this) as common.UIAbilityContext;
      window.getLastWindow(ctx).then((win: window.Window) => {
        const area = win.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
        const topPx = area.topRect.height;
        // px → vp
        this.statusBarHeightVp = px2vp(topPx);
      }).catch((): void => {
        // ignore
      });
    } catch (_e) {
      // ignore
    }
  }

  build() {
    Column() {
      // Title bar — background expands into the top safe area, while inner
      // content (title text) is pushed below the status bar via top padding.
      // This mirrors Android's tinted status bar look without overlapping.
      Row() {
        Text('A2UI · Kuikly Page')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(Color.White)
      }
      .width('100%')
      .height(56 + this.statusBarHeightVp)
      .backgroundColor('#3F51B5')
      .padding({ left: 16, top: this.statusBarHeightVp })
      .alignItems(VerticalAlign.Center)
      .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP])

      Scroll() {
        Column() {
          // ===== Group 1: Kuikly 页面接入示例 =====
          this.SectionHeader('Kuikly 页面接入示例')

          this.DemoEntry(new DemoEntryParams(
            '卡片场景接入示例',
            '在 Kuikly DSL 页面里嵌入 A2UISurface：单 Pager、单 FlexNode 树，点击按钮追加 A2UI 卡片',
            '🧬',
            '#3F51B5',
            () => { router.pushUrl({ url: 'pages/KuiklyPageCardDemoPage' }); }
          ))

          this.DemoEntry(new DemoEntryParams(
            '自定义函数示例',
            '注册 string/boolean/number/void 返回类型的自定义函数，验证表达式求值、check 条件、action 触发',
            '⚡',
            '#00897B',
            () => { router.pushUrl({ url: 'pages/KuiklyPageCustomFunctionDemoPage' }); }
          ))

          this.DemoEntry(new DemoEntryParams(
            '自定义组件示例',
            '注册 A2UIWeb (WebView) 和 MyA2UIView (简单视图) 自定义组件，覆盖 setProp/Shadow 完整链路',
            '🧩',
            '#6200EE',
            () => { router.pushUrl({ url: 'pages/KuiklyPageCustomComponentDemoPage' }); }
          ))

          this.DemoEntry(new DemoEntryParams(
            '自定义 Icon 图片',
            '通过 A2UIIconRegistry 注册业务侧 Icon 图片，替换协议内建的 emoji / unicode 字形',
            '🖼️',
            '#D81B60',
            () => { router.pushUrl({ url: 'pages/KuiklyPageCustomIconDemoPage' }); }
          ))

          this.DemoEntry(new DemoEntryParams(
            'Compose 页面接入示例',
            '在 Kuikly Compose 页面（:compose 模块）里承载 A2UI，展示 Compose 世界的 A2UI 集成',
            '🧵',
            '#009688',
            () => { router.pushUrl({ url: 'pages/KuiklyPageComposeDemoPage' }); }
          ))

          // ===== Group 2: 组件展示 =====
          this.SectionHeader('组件展示')

          this.DemoEntry(new DemoEntryParams(
            '组件展示',
            '展示 A2UI v0.9 协议语义化组件：Text、Row、Column、Card 等 18 个组件',
            '🔧',
            '#F39C12',
            () => { router.pushUrl({ url: 'pages/KuiklyPageComponentDemoPage' }); }
          ))
        }
        .padding({ left: 16, right: 16, top: 8, bottom: 16 })
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

  @Builder
  SectionHeader(title: string) {
    Text(title)
      .fontSize(13)
      .fontWeight(FontWeight.Bold)
      .fontColor('#555555')
      .margin({ top: 16, bottom: 8 })
  }

  @Builder
  DemoEntry(params: DemoEntryParams) {
    Row() {
      Text(params.iconText)
        .fontSize(28)
        .textAlign(TextAlign.Center)
        .width(48)
        .height(48)
        .borderRadius(24)
        .backgroundColor(params.color)
        .opacity(0.8)

      Column() {
        Text(params.title)
          .fontSize(17)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333333')
        Text(params.description)
          .fontSize(13)
          .fontColor('#888888')
          .margin({ top: 4 })
      }
      .layoutWeight(1)
      .alignItems(HorizontalAlign.Start)
      .margin({ left: 12 })

      Text('›')
        .fontSize(24)
        .fontColor('#CCCCCC')
    }
    .width('100%')
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .shadow({ radius: 4, color: '#1A000000', offsetY: 2 })
    .margin({ bottom: 12 })
    .onClick(params.onClick)
  }
}

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
 * A2UI BothPages Demo entry.
 *
 * Structure mirrors `androidAppForBothPages/A2UIDemoActivity` exactly:
 * a single ungrouped list of two entries so users can compare the two
 * integration paths side by side without switching apps.
 *
 *   1. Kuikly 页面接入示例 → `KuiklyPageCardDemoPage`
 *      (Kuikly Pager hosts one A2UI weather card via the DSL surface)
 *   2. Native 页面接入示例 → `NativeCardDemoPage`
 *      (Plain ArkUI page hosts one A2UISurfaceView with the same card)
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
      // Mirrors Android's tinted status-bar look (#7B1FA2 from A2UIDemoActivity).
      Row() {
        Text('A2UI · Both Pages')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(Color.White)
      }
      .width('100%')
      .height(56 + this.statusBarHeightVp)
      .backgroundColor('#7B1FA2')
      .padding({ left: 16, top: this.statusBarHeightVp })
      .alignItems(VerticalAlign.Center)
      .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP])

      Scroll() {
        Column() {
          // No section headers on this app — the whole point of Both Pages
          // is a flat side-by-side comparison of the two integration paths.

          this.DemoEntry(new DemoEntryParams(
            'Kuikly 页面接入示例',
            'Kuikly Page（KuiklyRenderView + @Page）承载一张 A2UI v0.9 天气卡片',
            '🧬',
            '#3F51B5',
            () => { router.pushUrl({ url: 'pages/KuiklyPageCardDemoPage' }); }
          ))

          this.DemoEntry(new DemoEntryParams(
            'Native 页面接入示例',
            'Native ArkUI 页面 + A2UISurfaceView 直接承载一张 A2UI v0.9 天气卡片',
            '🧱',
            '#455A64',
            () => { router.pushUrl({ url: 'pages/NativeCardDemoPage' }); }
          ))
        }
        .padding({ left: 16, right: 16, top: 16, bottom: 16 })
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
  DemoEntry(params: DemoEntryParams) {
    Row() {
      Text(params.iconText)
        .fontSize(28)
        .textAlign(TextAlign.Center)
        .width(48)
        .height(48)
        .borderRadius(24)
        .backgroundColor(params.color)
        .opacity(0.4)

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

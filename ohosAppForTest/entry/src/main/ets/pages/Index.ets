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
 * A2UI Test Demo entry.
 *
 * Structure mirrors `iosAppForTest/A2UIDemoListViewController.swift` exactly:
 *   Group 1 — A2UI 协议测试：
 *     函数测试 / 数据绑定测试 / 主题测试
 *   Group 2 — A2UI 协议官方示例：
 *     A2UI v0.9 协议官方示例
 *
 * This app intentionally does not register any A2UI icons — icon protocol
 * samples here render the built-in emoji glyphs as fallback, which is the
 * intended "unregistered" state for testing. Native-integration demos
 * (Native Card / CustomFunction / CustomComponent / CustomIcon / Component
 * showcase) live in the sibling *ohosAppForNativePage* and are explicitly
 * out of scope for this test app.
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
   * still renders.
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
      Row() {
        Text('A2UI Test')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(Color.White)
      }
      .width('100%')
      .height(56 + this.statusBarHeightVp)
      .backgroundColor('#37474F')
      .padding({ left: 16, top: this.statusBarHeightVp })
      .alignItems(VerticalAlign.Center)
      .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP])

      Scroll() {
        Column() {
          this.SectionHeader('按协议版本运行')
          this.DemoEntry(new DemoEntryParams(
            'A2UI 0.9',
            '原样归档的组件、绑定、函数、主题和官方示例，包含预期非法样本',
            '0.9',
            '#607D8B',
            () => { router.pushUrl({ url: 'pages/AssetBrowserPage', params: { directory: 'v0_9' } }); }
          ))
          this.DemoEntry(new DemoEntryParams(
            'A2UI 1.0',
            '独立的 1.0 严格模式样本目录，不混入旧版语料',
            '1.0',
            '#F39C12',
            () => { router.pushUrl({ url: 'pages/AssetBrowserPage', params: { directory: 'v1_0' } }); }
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

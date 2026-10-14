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
import {
  registerA2UIIcons,
  unregisterA2UIIcon,
  A2UI_ICON_FALLBACK_KEY
} from '@kuikly-open/a2ui-render';
import { A2UISurface } from '../a2ui/A2UISurface';

/**
 * CustomIconDemoPage — Native page integration path.
 *
 * Mirrors [CustomIconDemoActivity] on Android and
 * [CustomIconDemoViewController] on iOS: registers a small curated set of
 * icons through `registerA2UIIcons`, then renders two hand-written A2UI
 * cards:
 *
 *  - Card A: 5 registered icon names (star / favorite / home / settings /
 *    search) — each hits KRImageView through the demo-registered map.
 *  - Card B: 3 unregistered icon names (camera / delete / edit) — each
 *    falls back to the shared `_fallback.png`.
 *
 * NOTE on OHOS host wiring: `A2UIDemoAbility.onCreate` already registers
 * ALL 63 built-in icons + `_fallback`. Without any special handling, the
 * "unregistered" card would still show camera/delete/edit PNGs and the
 * demo's fallback semantics would be lost. To reproduce the fallback
 * behavior faithfully, we unregister camera/delete/edit on entry and
 * re-register them (from the same asset paths) on exit so other pages
 * keep their icons intact.
 */

// Icon name → asset URI table shared by register/unregister paths.
// Layout mirrors the Android/iOS demos byte-for-byte.
const REGISTERED_ICON_NAMES: string[] = ['star', 'favorite', 'home', 'settings', 'search'];
const UNREGISTERED_ICON_NAMES: string[] = ['camera', 'delete', 'edit'];

// ==== Card A: 5 registered icons — each hits KRImageView through the demo map.
const REGISTERED_MESSAGES: string =
  '[' +
    '{"version":"v0.9","createSurface":{"surfaceId":"native-icon-card-registered"}},' +
    '{"version":"v0.9","updateComponents":{"surfaceId":"native-icon-card-registered","components":[' +
      '{"id":"root","component":"Card","child":"row"},' +
      '{"id":"row","component":"Row","justify":"spaceAround","align":"center",' +
        '"children":["icon-0","icon-1","icon-2","icon-3","icon-4"]},' +
      '{"id":"icon-0","component":"Icon","name":"star"},' +
      '{"id":"icon-1","component":"Icon","name":"favorite"},' +
      '{"id":"icon-2","component":"Icon","name":"home"},' +
      '{"id":"icon-3","component":"Icon","name":"settings"},' +
      '{"id":"icon-4","component":"Icon","name":"search"}' +
    ']}}' +
  ']';

// ==== Card B: 3 unregistered names — all fall back to _fallback.png.
const UNREGISTERED_MESSAGES: string =
  '[' +
    '{"version":"v0.9","createSurface":{"surfaceId":"native-icon-card-unregistered"}},' +
    '{"version":"v0.9","updateComponents":{"surfaceId":"native-icon-card-unregistered","components":[' +
      '{"id":"root","component":"Card","child":"row"},' +
      '{"id":"row","component":"Row","justify":"spaceAround","align":"center",' +
        '"children":["icon-0","icon-1","icon-2"]},' +
      '{"id":"icon-0","component":"Icon","name":"camera"},' +
      '{"id":"icon-1","component":"Icon","name":"delete"},' +
      '{"id":"icon-2","component":"Icon","name":"edit"}' +
    ']}}' +
  ']';

@Entry
@Component
struct CustomIconDemoPage {
  aboutToAppear(): void {
    // Step 1: unregister the 3 icons whose "not registered → _fallback"
    // behavior we want to demo. The Ability's global registration would
    // otherwise satisfy them and hide the fallback semantics.
    UNREGISTERED_ICON_NAMES.forEach((name: string): void => {
      unregisterA2UIIcon(name);
    });

    // Step 2: (re-)register the 5 demo icons together with `_fallback`.
    // These entries already exist in the Ability-level registration, so
    // this call is functionally a no-op for icon resolution but keeps
    // this file self-contained and matches the Android/iOS demos.
    const icons = new Map<string, string>();
    REGISTERED_ICON_NAMES.forEach((name: string): void => {
      icons.set(name, `assets://a2ui_icons/${name}.png`);
    });
    icons.set(A2UI_ICON_FALLBACK_KEY, 'assets://a2ui_icons/_fallback.png');
    registerA2UIIcons(icons);
  }

  aboutToDisappear(): void {
    REGISTERED_ICON_NAMES.forEach((name: string): void => {
      unregisterA2UIIcon(name);
    });
    unregisterA2UIIcon(A2UI_ICON_FALLBACK_KEY);
  }

  build() {
    Column() {
      // Header (pink bar, matches Android's #D81B60)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('自定义 Icon 图片')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('宿主：Native ArkUI + A2UISurface\n已注册 5 个 Icon + _fallback，未注册的名称均落到 _fallback')
          .fontSize(11)
          .fontColor('#FCE4EC')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 16 })
      .backgroundColor('#D81B60')
      .alignItems(HorizontalAlign.Start)

      // Scrollable card list
      Scroll() {
        Column() {
          // ---------- Card A: registered ----------
          Text('已注册 Icon（走 KRImageView）')
            .fontSize(14)
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')
            .margin({ top: 10, bottom: 2, left: 4 })
          Text('star / favorite / home / settings / search 均命中已注册的图片')
            .fontSize(12)
            .fontColor('#888888')
            .margin({ bottom: 6, left: 4 })
          Column() {
            A2UISurface({
              messages: REGISTERED_MESSAGES,
              finished: true
            })
          }
          .width('100%')
          .backgroundColor(Color.White)
          .borderRadius(8)
          .clip(true)

          // ---------- Card B: unregistered → fallback ----------
          Text('未注册 Icon（走 _fallback）')
            .fontSize(14)
            .fontWeight(FontWeight.Bold)
            .fontColor('#333333')
            .margin({ top: 16, bottom: 2, left: 4 })
          Text('camera / delete / edit 三个名称未注册，均渲染为 _fallback.png')
            .fontSize(12)
            .fontColor('#888888')
            .margin({ bottom: 6, left: 4 })
          Column() {
            A2UISurface({
              messages: UNREGISTERED_MESSAGES,
              finished: true
            })
          }
          .width('100%')
          .backgroundColor(Color.White)
          .borderRadius(8)
          .clip(true)
        }
        .width('100%')
        .alignItems(HorizontalAlign.Start)
      }
      .layoutWeight(1)
      .padding({ left: 12, right: 12, top: 8, bottom: 12 })
      .align(Alignment.Top)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}

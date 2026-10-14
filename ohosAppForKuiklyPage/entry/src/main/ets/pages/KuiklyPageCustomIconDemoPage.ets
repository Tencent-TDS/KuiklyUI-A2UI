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
import { KRNativeRenderController, KRRecord, Kuikly } from '@kuikly-open/render';
import {
  A2UIKuiklyRenderViewSupport,
  a2uiNativeManager,
  registerA2UIIcons,
  unregisterA2UIIcon,
  A2UI_ICON_FALLBACK_KEY
} from '@kuikly-open/a2ui-render';

/**
 * KuiklyPageCustomIconDemoPage — icon-registry demo, KuiklyPage app.
 *
 * Mirrors [CustomIconDemoActivity] on Android: this screen hosts a single
 * Kuikly Page (`@Page("A2UICaseList")`, declared in
 * `:demo/A2UICaseListPager.kt`) which internally renders every case
 * as its own `A2UISurface` inside a KMP `Scroller`. We do **not** spin up
 * a separate native `A2UISurface` per case here — the whole "many-cards"
 * layout lives inside the Kuikly Pager, so this really is a Kuikly-Page
 * integration path (matching Android/iOS).
 *
 * pageData contract (see `A2UICaseListPager`):
 *  - `title`  : header string displayed inside the Kuikly Pager.
 *  - `cases`  : JSON array *string*; each entry has `name` / `description`
 *               / `messages` (a JSON array).
 *
 * Two cases:
 *  - Case A: star / favorite / home / settings / search — hit the
 *            registered PNG assets (KRImageView path).
 *  - Case B: camera / delete / edit — deliberately unregistered so the
 *            protocol falls back to `_fallback.png`.
 *
 * NOTE on state hygiene: the KuiklyPage Ability does *not* pre-register
 * any icons (unlike the NativePage Ability which registers all 63), so
 * we only need to ensure the 5 registered names are wired before the
 * Kuikly Pager mounts and pulled after we leave.
 */

const REGISTERED_ICON_NAMES: string[] = ['star', 'favorite', 'home', 'settings', 'search'];
const UNREGISTERED_ICON_NAMES: string[] = ['camera', 'delete', 'edit'];

@Entry
@Component
struct KuiklyPageCustomIconDemoPage {
  @State ready: boolean = false;
  @State pageData: KRRecord = {};

  private controller: KRNativeRenderController | null = null;
  private delegate = A2UIKuiklyRenderViewSupport.wrap();

  aboutToAppear(): void {
    // 1. Register our PNGs before the Kuikly page mounts so the very
    //    first Icon layout resolves against them.
    //
    //    URIs point at rawfile assets copied under `resfile/a2ui_icons/`
    //    by `sync_demo_assets.sh` — matching the Android side.
    const icons = new Map<string, string>();
    REGISTERED_ICON_NAMES.forEach((name: string): void => {
      icons.set(name, `assets://a2ui_icons/${name}.png`);
    });
    icons.set(A2UI_ICON_FALLBACK_KEY, 'assets://a2ui_icons/_fallback.png');
    registerA2UIIcons(icons);

    // 2. Build the pageData handed off to `A2UICaseListPager`.
    this.pageData = {
      'title': '自定义 Icon 图片',
      'cases': KuiklyPageCustomIconDemoPage.buildCasesJson()
    };
    this.ready = true;
  }

  onBackPress(): boolean | void {
    const consumed = this.controller?.onBackPress();
    return consumed === true;
  }

  aboutToDisappear(): void {
    this.controller = null;
    // Undo everything on exit so sibling demo pages don't inherit the
    // registration (matches Android's onDestroy behaviour). Leaving the
    // fallback in place would silently satisfy every Icon name in every
    // downstream demo — that's not what the demo is meant to show.
    REGISTERED_ICON_NAMES.forEach((name: string): void => {
      unregisterA2UIIcon(name);
    });
    unregisterA2UIIcon(A2UI_ICON_FALLBACK_KEY);
  }

  /**
   * Assemble the `cases` JSON-array string expected by `A2UICaseListPager`.
   * Each case exposes a horizontal Row of A2UI `Icon` components so the
   * Icon rendering path exercises the freshly-registered PNGs.
   *
   * Mirrors Android's `buildIconGridCase(...)`. Kept as a static helper
   * so the concatenation stays readable.
   */
  private static buildCasesJson(): string {
    const caseA: string = KuiklyPageCustomIconDemoPage.buildIconGridCase(
      '已注册 Icon（走 KRImageView）',
      'star / favorite / home / settings / search 均命中已注册的图片',
      REGISTERED_ICON_NAMES
    );
    const caseB: string = KuiklyPageCustomIconDemoPage.buildIconGridCase(
      '未注册 Icon（走 _fallback）',
      'camera / delete / edit 三个名称未注册，均渲染为 _fallback.png',
      UNREGISTERED_ICON_NAMES
    );
    return `[${caseA},${caseB}]`;
  }

  /**
   * Build one case object (as a JSON string) whose surface contains a
   * horizontal Row of A2UI `Icon` components, one per entry in
   * [iconNames].
   */
  private static buildIconGridCase(
    name: string,
    description: string,
    iconNames: string[]
  ): string {
    const surfaceId: string = `icon-case-${KuiklyPageCustomIconDemoPage.hashCode(name)}`;

    // childrenIds for the Row — quoted JSON strings.
    let childrenIds: string = '';
    for (let i = 0; i < iconNames.length; i++) {
      if (i > 0) childrenIds += ',';
      childrenIds += `"icon-${i}"`;
    }

    // components array: root Card → Row → Icons.
    let componentsJson: string =
      '{"id":"root","component":"Card","children":["row"]},' +
      `{"id":"row","component":"Row","children":[${childrenIds}],` +
        '"justifyContent":"spaceAround","alignItems":"center","padding":16}';
    for (let i = 0; i < iconNames.length; i++) {
      componentsJson +=
        `,{"id":"icon-${i}","component":"Icon","name":${JSON.stringify(iconNames[i])},"size":48}`;
    }

    // messages array — `createSurface` + `updateComponents`.
    const messages: string =
      '[' +
        `{"version":"v0.9","createSurface":{"surfaceId":"${surfaceId}"}},` +
        `{"version":"v0.9","updateComponents":{"surfaceId":"${surfaceId}",` +
          `"components":[${componentsJson}]}}` +
      ']';

    return '{' +
      `"name":${JSON.stringify(name)},` +
      `"description":${JSON.stringify(description)},` +
      `"messages":${messages}` +
    '}';
  }

  /**
   * Tiny stable string hash used only to derive a unique surfaceId per
   * case name. Value doesn't need to be secure — just deterministic so
   * cases don't collide in a single `A2UICaseListPager` instance.
   */
  private static hashCode(input: string): number {
    let h: number = 0;
    for (let i = 0; i < input.length; i++) {
      h = ((h << 5) - h + input.charCodeAt(i)) | 0;
    }
    return h;
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

        Text('宿主：Kuikly Page（Kuikly + @Page A2UICaseList）\n已注册 5 个 Icon + _fallback，未注册的名称均落到 _fallback')
          .fontSize(11)
          .fontColor('#FCE4EC')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 16 })
      .backgroundColor('#D81B60')
      .alignItems(HorizontalAlign.Start)

      // Kuikly Pager (`A2UICaseList`) — it iterates over `cases` internally
      // and drops one `A2UISurface { }` per case inside its Flex tree.
      Stack() {
        if (this.ready) {
          Kuikly({
            pageName: 'A2UICaseList',
            pageData: this.pageData,
            delegate: this.delegate,
            nativeManager: a2uiNativeManager,
            onControllerReadyCallback: (controller: KRNativeRenderController): void => {
              this.controller = controller;
              A2UIKuiklyRenderViewSupport.onControllerReadyReplay(controller);
            }
          })
        }
      }
      .layoutWeight(1)
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}

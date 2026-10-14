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
import { KRNativeRenderController, Kuikly } from '@kuikly-open/render';
import {
  A2UIKuiklyRenderViewSupport,
  a2uiNativeManager
} from '@kuikly-open/a2ui-render';

/**
 * KuiklyPageCardDemoPage — the DSL / Compose-embedding integration path.
 *
 * Mirrors [A2UISurfaceDSLDemoActivity] on Android and the iOS
 * `A2UISurfaceDSLDemoViewController`. The host is a Kuikly Page
 * (a hand-written Pager on the KMP side declared as
 * `@Page("A2UISurfaceDemo")` in :demo) whose Flex tree drops an
 * `A2UISurface { ... }` DSL block directly among its siblings.
 *
 * The `Kuikly({...})` component is given the `a2uiNativeManager` (so the
 * embedded A2UI DSL runs its own KMP bridge routing) and a delegate
 * produced by `A2UIKuiklyRenderViewSupport.wrap()`. `wrap()` layers the
 * globally-registered A2UI custom render views and the `A2UIBridgeModule`
 * on top of the caller's own delegate — see
 * `a2ui-render-ohos/A2UIKuiklyRenderViewSupport.ets` for the full
 * contract, including the KMP-registration replay done in
 * `onControllerReadyCallback`.
 */
@Entry
@Component
struct KuiklyPageCardDemoPage {
  @State ready: boolean = true;
  private controller: KRNativeRenderController | null = null;

  /** Wrap a fresh empty delegate; `wrap()` handles A2UI-specific plumbing. */
  private delegate = A2UIKuiklyRenderViewSupport.wrap();

  onBackPress(): boolean | void {
    // Forward hardware back to the Kuikly Pager first; if it doesn't
    // consume, let the router pop as usual. This mirrors what the top
    // level ohosApp Index.ets does.
    const consumed = this.controller?.onBackPress();
    return consumed === true;
  }

  aboutToDisappear(): void {
    this.controller = null;
  }

  build() {
    Column() {
      // Header (deep purple — mirrors Android #6200EE for this path)
      Row() {
        Text('←')
          .fontSize(20)
          .fontColor(Color.White)
          .padding({ right: 12 })
          .onClick(() => router.back())
        Text('[DSL] A2UI in Kuikly Page')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .fontColor(Color.White)
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 48, bottom: 12 })
      .backgroundColor('#6200EE')
      .alignItems(VerticalAlign.Center)

      // Kuikly Pager hosting the KMP `A2UISurfaceDemo` page. The embedded
      // `A2UISurface { }` block inside that Pager renders its A2UI content
      // via KMP's `A2UISurfaceDSLView`, which lives directly in the Flex
      // tree — no wrapper native view, no inner Pager.
      Stack() {
        if (this.ready) {
          Kuikly({
            pageName: 'A2UISurfaceDemo',
            pageData: {},
            delegate: this.delegate,
            nativeManager: a2uiNativeManager,
            onControllerReadyCallback: (controller: KRNativeRenderController): void => {
              this.controller = controller;
              // Seed this fresh KMP core with every globally-registered
              // A2UI custom function / custom view / icon so DSL surfaces
              // inside this Pager see the same registrations that legacy
              // native surfaces would.
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

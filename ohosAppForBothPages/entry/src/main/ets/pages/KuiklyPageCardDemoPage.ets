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
import { KRNativeRenderController, Kuikly } from '@kuikly-open/render';
import {
  A2UIKuiklyRenderViewSupport,
  a2uiNativeManager
} from '@kuikly-a2ui-open/render';

/**
 * KuiklyPageCardDemoPage — Kuikly Page single-card sample.
 *
 * Mirrors `androidAppForBothPages/KuiklyPageCardActivity`: boots a Kuikly
 * Pager with pageName `A2UISurfaceDemo` (declared as `@Page("A2UISurfaceDemo")`
 * on `A2UISurfaceDemoPager` in `:demo`). The Pager's own Flex tree
 * drops an `A2UISurface { ... }` DSL block that renders one A2UI v0.9
 * weather card — same card body used by the Native path so users can
 * eyeball parity between the two integration modes.
 *
 * The `Kuikly({...})` component is given `a2uiNativeManager` (so the
 * embedded A2UI DSL runs its own KMP bridge routing) and the delegate
 * produced by `A2UIKuiklyRenderViewSupport.wrap()` (which layers the
 * globally-registered A2UI custom render views and the `A2UIBridgeModule`
 * on top of an empty base delegate).
 */
@Entry
@Component
struct KuiklyPageCardDemoPage {
  @State ready: boolean = true;
  @State statusBarHeightVp: number = 0;
  private controller: KRNativeRenderController | null = null;

  /** Wrap a fresh empty delegate; `wrap()` handles A2UI-specific plumbing. */
  private delegate = A2UIKuiklyRenderViewSupport.wrap();

  aboutToAppear(): void {
    this.resolveStatusBarHeight();
  }

  aboutToDisappear(): void {
    this.controller = null;
  }

  onBackPress(): boolean | void {
    // Forward hardware back to the Kuikly Pager first; if it doesn't
    // consume the event, let the router pop as usual.
    const consumed = this.controller?.onBackPress();
    return consumed === true;
  }

  private resolveStatusBarHeight(): void {
    try {
      const ctx = getContext(this) as common.UIAbilityContext;
      window.getLastWindow(ctx).then((win: window.Window) => {
        const area = win.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
        this.statusBarHeightVp = px2vp(area.topRect.height);
      }).catch((): void => {
        // ignore
      });
    } catch (_e) {
      // ignore
    }
  }

  build() {
    Column() {
      // Header — indigo band mirroring Android's `#3F51B5` for this path.
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('Kuikly 页面接入示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('宿主：Kuikly Page（KuiklyRenderView + @Page A2UISurfaceDemo）')
          .fontSize(11)
          .fontColor('#DDDDFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: this.statusBarHeightVp + 16, bottom: 16 })
      .backgroundColor('#3F51B5')
      .alignItems(HorizontalAlign.Start)
      .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP])

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

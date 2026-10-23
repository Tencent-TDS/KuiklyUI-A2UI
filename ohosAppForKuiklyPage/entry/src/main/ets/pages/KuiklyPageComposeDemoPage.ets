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
} from '@kuikly-a2ui-open/render';

/**
 * KuiklyPageComposeDemoPage — Compose-hosted A2UI integration path.
 *
 * Mirrors [ComposePageDemoActivity] on Android. The host is a Kuikly
 * *Compose* page (`@Page("ComposeSampleWithA2UI")` declared in the
 * :demo module — that page uses `:compose`'s `ComposeContainer` to
 * render, then embeds `A2UISurfaceDSLView` via `MakeKuiklyComposeNode`).
 *
 * The rendering setup mirrors [KuiklyPageCardDemoPage] exactly — the
 * only difference is the pageName. Both routes come out of the same
 * `libshared.so` (the demo variant that packages `:demo` on top of
 * `:a2ui`), so switching pages is just a Kuikly `pageName` change.
 */
@Entry
@Component
struct KuiklyPageComposeDemoPage {
  @State ready: boolean = true;
  private controller: KRNativeRenderController | null = null;

  private delegate = A2UIKuiklyRenderViewSupport.wrap();

  onBackPress(): boolean | void {
    const consumed = this.controller?.onBackPress();
    return consumed === true;
  }

  aboutToDisappear(): void {
    this.controller = null;
  }

  build() {
    Column() {
      // Header (teal — matches Android #009688 for this path)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('Compose 页面接入示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('宿主：Kuikly Compose（:compose 模块 · ComposeContainer + setContent）')
          .fontSize(11)
          .fontColor('#B2DFDB')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 16 })
      .backgroundColor('#009688')
      .alignItems(HorizontalAlign.Start)

      Stack() {
        if (this.ready) {
          Kuikly({
            pageName: 'ComposeSampleWithA2UI',
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

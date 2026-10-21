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

/**
 * A2UI Render Module for HarmonyOS.
 *
 * This module provides the A2UISurfaceView component that bridges A2UI protocol
 * messages to Kuikly's native rendering engine on HarmonyOS.
 *
 * Architecture (aligned with Android a2ui-render-android and iOS a2ui-render-ios):
 * - A2UISurfaceView: ArkUI component that hosts the rendered content via Kuikly's KRNativeRender
 * - A2UISurfaceViewController: Controls the rendering lifecycle (writeStream, finishStream, destroy)
 * - Uses @kuikly-open/render (core-render-ohos) for native view creation and management
 * - A2UI Kotlin core logic (A2UIBridgeManager, A2UIDSLBridge, A2UIPager) runs via Kotlin/Native
 * - Communication: ArkTS → Kotlin via render.sendEvent(); Kotlin → ArkTS via NativeBridge → C++ → ArkUI C-API
 *
 * Usage:
 * ```ets
 * import { A2UISurfaceView, A2UISurfaceViewController, A2UISurfaceViewListener } from '@kuikly-a2ui-open/a2ui-render';
 *
 * @Component
 * struct MyPage {
 *   private controller: A2UISurfaceViewController | null = null;
 *
 *   build() {
 *     A2UISurfaceView({
 *       listener: { onAction: (surfaceId, tag, action, params) => { ... } },
 *       onReady: (ctrl) => {
 *         this.controller = ctrl;
 *         ctrl.writeStream('{"version":"v0.9","createSurface":{...}}');
 *         ctrl.writeStream('{"version":"v0.9","updateComponents":{...}}');
 *         ctrl.finishStream();
 *       }
 *     })
 *   }
 * }
 * ```
 */

export { A2UISurfaceView, A2UISurfaceViewController, A2UISurfaceViewListener } from './A2UISurfaceView';
export { registerA2UICustomFunction, unregisterA2UICustomFunction } from './A2UISurfaceView';
export {
  registerA2UICustomRenderView,
  unregisterA2UICustomRenderView,
  notifyA2UICustomViewSizeChanged
} from './A2UISurfaceView';
export { registerA2UIIcons, registerA2UIIcon, unregisterA2UIIcon, A2UI_ICON_FALLBACK_KEY } from './A2UISurfaceView';
export type { A2UICustomFunctionHandler } from './A2UISurfaceView';
export type { A2UICustomViewMeasurer, A2UICustomViewSize } from './A2UISurfaceView';
export {
  A2UIKuiklyRenderViewSupport,
  bindA2UIKuiklyPageController,
  unbindA2UIKuiklyPageController
} from './A2UIKuiklyRenderViewSupport';
export { a2uiNativeManager } from './A2UINativeManager';

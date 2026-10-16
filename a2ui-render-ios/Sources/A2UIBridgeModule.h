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

#import <Foundation/Foundation.h>
#import "KRBaseModule.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * `A2UIBridgeModule` — the iOS counterpart of
 * `com.tencent.kuikly.a2ui.render.android.A2UIBridgeModule` on Android and
 * `A2UIBridgeModule` on OHOS.
 *
 * Role
 * ----
 * This is the single native-side entry point for every A2UI request that
 * originates from the KMP core when the surface is rendered in
 * **Kuikly-page-embedded mode** (an `A2UISurfaceDSLView` living inside a
 * host Kuikly page). The KMP side (`A2UIDSLBridge` /
 * `A2UIBridgeManager`) routes those requests through
 * `BridgeManager.callModuleMethod("A2UIBridgeModule", method, params, …)`
 * on all three target platforms, and this class is what
 * `KuiklyRenderLayerHandler.p_moduleHandlerWithModuleName:` instantiates
 * through `NSClassFromString("A2UIBridgeModule")`.
 *
 * Standalone (Native-page) `A2UISurfaceView` instances continue to handle
 * methodId=103/106/107 directly in their own `callNative(…)` switch — this
 * module is NOT exercised there.
 *
 * Wire format (identical across platforms)
 * ----------------------------------------
 * | method                 | params (NSString)                           | sync |
 * |------------------------|---------------------------------------------|------|
 * | contentHeightChanged   | height (NSNumber)                           | 1    |
 * | actionCallback         | "<tag>\n<actionJson>"                       | 1    |
 * | showDateTimePicker     | configJson (contains tag / dslInstanceId)   | 1    |
 * | executeFunctionCall    | "<functionName>\n<argsJson>"                | 1    |
 * | invokeCustomFunction   | "<name>\n<spec>\n<argsJson>"                | 0/1  |
 * | debugLog               | message (NSString)                          | 0    |
 */
@interface A2UIBridgeModule : KRBaseModule
@end

NS_ASSUME_NONNULL_END

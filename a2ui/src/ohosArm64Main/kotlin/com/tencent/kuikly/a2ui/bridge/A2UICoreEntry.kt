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

package com.tencent.kuikly.a2ui.bridge

/**
 * HarmonyOS (OHOS) A2UI Core Entry - Constants.
 *
 * On HarmonyOS, the initialization is handled by [initKuikly] in A2UIKuiklyEntry.kt.
 * Event handling is done by A2UIPager.onReceivePagerEvent() in commonMain.
 *
 * This object provides:
 * - The fixed pageName constant used by A2UISurfaceView.ets and A2UIKuiklyEntry.kt
 *
 * Architecture:
 * 1. ArkTS: KRNativeRender(pageName="a2ui") → C++ creates Pager
 * 2. C++ → Kotlin: BridgeManager.callKotlinMethod(CREATE_INSTANCE, pagerId, "a2ui", pageData)
 * 3. PagerManager.createPager("a2ui") → A2UIOhosPager
 * 4. A2UIOhosPager.onCreatePager() → initializes A2UIBridgeManager
 * 5. ArkTS → Kotlin: render.sendEvent(eventName, data) →
 *    C++ → BridgeManager.callKotlinMethod(UPDATE_INSTANCE) →
 *    A2UIPager.onReceivePagerEvent() → A2UIBridgeManager.callKotlinMethod()
 */
object A2UICoreEntry {

    /** The fixed pageName used for A2UI on OHOS */
    const val PAGE_NAME = "a2ui"
}

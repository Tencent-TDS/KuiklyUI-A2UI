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

import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { KuiklyRenderAdapterManager } from '@kuikly-open/render';
import { A2UIVideoViewAdapter } from '../a2ui/adapters/A2UIVideoViewAdapter';
import { A2UIAudioViewAdapter } from '../a2ui/adapters/A2UIAudioViewAdapter';

const TAG = 'A2UIDemoAbility';

/**
 * Ability for `ohosAppForKuiklyPage`.
 *
 * Registers the video / audio KuiklyRender adapters (identical across the 4
 * demo apps) but intentionally does NOT register any A2UI icons — the
 * KuiklyPage variant only ships the Kuikly-Page card demo which does not
 * exercise icon protocol samples.
 */
export default class A2UIDemoAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, TAG, 'Ability onCreate');

    if (KuiklyRenderAdapterManager.krVideoViewAdapter == null) {
      KuiklyRenderAdapterManager.krVideoViewAdapter = new A2UIVideoViewAdapter();
    }
    if (KuiklyRenderAdapterManager.krAudioViewAdapter == null) {
      KuiklyRenderAdapterManager.krAudioViewAdapter = new A2UIAudioViewAdapter();
    }
  }

  onDestroy(): void {
    hilog.info(0x0000, TAG, 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    hilog.info(0x0000, TAG, 'Ability onWindowStageCreate');
    windowStage.loadContent('pages/Index', (err) => {
      if (err.code) {
        hilog.error(0x0000, TAG, 'Failed to load content: %{public}s', JSON.stringify(err));
        return;
      }
      hilog.info(0x0000, TAG, 'Succeeded in loading content.');
    });
    let mainWindow = windowStage.getMainWindowSync();
    mainWindow.setWindowLayoutFullScreen(true);
  }

  onWindowStageDestroy(): void {
    hilog.info(0x0000, TAG, 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    hilog.info(0x0000, TAG, 'Ability onForeground');
  }

  onBackground(): void {
    hilog.info(0x0000, TAG, 'Ability onBackground');
  }
}

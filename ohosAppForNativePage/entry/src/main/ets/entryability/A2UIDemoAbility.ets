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
import { registerA2UIIcons, A2UI_ICON_FALLBACK_KEY } from '@kuikly-a2ui-open/a2ui-render';

const TAG = 'A2UIDemoAbility';

export default class A2UIDemoAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, TAG, 'Ability onCreate');

    // Register the video adapter so A2UI's "Video" component (KRVideoView) can
    // render an actual native video panel. Mirrors Android A2UIDemoApplication
    // and iOS A2UIVideoViewHandler registration.
    if (KuiklyRenderAdapterManager.krVideoViewAdapter == null) {
      KuiklyRenderAdapterManager.krVideoViewAdapter = new A2UIVideoViewAdapter();
    }

    // Register the audio adapter so A2UI's "AudioPlayer" component (KRAudioView)
    // can render an actual native audio player. Mirrors Android A2UIAudioViewAdapter
    // and iOS A2UIAudioViewHandler registration.
    if (KuiklyRenderAdapterManager.krAudioViewAdapter == null) {
      KuiklyRenderAdapterManager.krAudioViewAdapter = new A2UIAudioViewAdapter();
    }

    // Register business-supplied Icon images. Once at least one icon is
    // registered here, EVERY A2UI Icon component (including those whose name
    // is data-bound at runtime) renders via KRImageView instead of the
    // built-in unicode/emoji glyph. This demo ships one PNG for every entry
    // in the built-in ICON_MAP (63 icons + a `_fallback` placeholder) under
    // `resources/resfile/a2ui_icons/`, so any name declared by the A2UI
    // protocol resolves to a concrete image; unknown names still land on the
    // reserved `_fallback` entry.
    //
    // The `assets://` scheme is understood by the Kuikly OHOS image adapter,
    // which maps it to `getContext().resourceDir` (i.e. the app's resfile
    // directory).
    // this.registerA2UIIconImages();
  }

  private registerA2UIIconImages(): void {
    // Use a Map (not an object literal) because ArkTS strict mode:
    //   1. Forbids untyped object literals (arkts-no-untyped-obj-literals);
    //   2. Forbids computed property names (arkts-identifiers-as-prop-names),
    //      which we would need for the `_fallback` entry keyed by the
    //      A2UI_ICON_FALLBACK_KEY constant.
    // A typed Map neatly sidesteps both restrictions while still matching the
    // upstream `registerA2UIIcons(icons: Map<string, string>)` signature.
    const icons = new Map<string, string>();
    icons.set('accountCircle',    'assets://a2ui_icons/accountCircle.png');
    icons.set('add',              'assets://a2ui_icons/add.png');
    icons.set('arrowBack',        'assets://a2ui_icons/arrowBack.png');
    icons.set('arrowForward',     'assets://a2ui_icons/arrowForward.png');
    icons.set('arrowUpward',      'assets://a2ui_icons/arrowUpward.png');
    icons.set('attachFile',       'assets://a2ui_icons/attachFile.png');
    icons.set('calendarToday',    'assets://a2ui_icons/calendarToday.png');
    icons.set('call',             'assets://a2ui_icons/call.png');
    icons.set('camera',           'assets://a2ui_icons/camera.png');
    icons.set('check',            'assets://a2ui_icons/check.png');
    icons.set('close',            'assets://a2ui_icons/close.png');
    icons.set('delete',           'assets://a2ui_icons/delete.png');
    icons.set('directionsRun',    'assets://a2ui_icons/directionsRun.png');
    icons.set('download',         'assets://a2ui_icons/download.png');
    icons.set('edit',             'assets://a2ui_icons/edit.png');
    icons.set('event',            'assets://a2ui_icons/event.png');
    icons.set('error',            'assets://a2ui_icons/error.png');
    icons.set('fastForward',      'assets://a2ui_icons/fastForward.png');
    icons.set('favorite',         'assets://a2ui_icons/favorite.png');
    icons.set('favoriteOff',      'assets://a2ui_icons/favoriteOff.png');
    icons.set('folder',           'assets://a2ui_icons/folder.png');
    icons.set('help',             'assets://a2ui_icons/help.png');
    icons.set('home',             'assets://a2ui_icons/home.png');
    icons.set('info',             'assets://a2ui_icons/info.png');
    icons.set('locationOn',       'assets://a2ui_icons/locationOn.png');
    icons.set('lock',             'assets://a2ui_icons/lock.png');
    icons.set('lockOpen',         'assets://a2ui_icons/lockOpen.png');
    icons.set('mail',             'assets://a2ui_icons/mail.png');
    icons.set('menu',             'assets://a2ui_icons/menu.png');
    icons.set('moreVert',         'assets://a2ui_icons/moreVert.png');
    icons.set('moreHoriz',        'assets://a2ui_icons/moreHoriz.png');
    icons.set('notificationsOff', 'assets://a2ui_icons/notificationsOff.png');
    icons.set('notifications',    'assets://a2ui_icons/notifications.png');
    icons.set('pause',            'assets://a2ui_icons/pause.png');
    icons.set('payment',          'assets://a2ui_icons/payment.png');
    icons.set('person',           'assets://a2ui_icons/person.png');
    icons.set('priorityHigh',     'assets://a2ui_icons/priorityHigh.png');
    icons.set('phone',            'assets://a2ui_icons/phone.png');
    icons.set('photo',            'assets://a2ui_icons/photo.png');
    icons.set('play',             'assets://a2ui_icons/play.png');
    icons.set('print',            'assets://a2ui_icons/print.png');
    icons.set('refresh',          'assets://a2ui_icons/refresh.png');
    icons.set('rewind',           'assets://a2ui_icons/rewind.png');
    icons.set('search',           'assets://a2ui_icons/search.png');
    icons.set('send',             'assets://a2ui_icons/send.png');
    icons.set('settings',         'assets://a2ui_icons/settings.png');
    icons.set('share',            'assets://a2ui_icons/share.png');
    icons.set('shoppingCart',     'assets://a2ui_icons/shoppingCart.png');
    icons.set('skipNext',         'assets://a2ui_icons/skipNext.png');
    icons.set('skipPrevious',     'assets://a2ui_icons/skipPrevious.png');
    icons.set('star',             'assets://a2ui_icons/star.png');
    icons.set('starHalf',         'assets://a2ui_icons/starHalf.png');
    icons.set('starOff',          'assets://a2ui_icons/starOff.png');
    icons.set('stop',             'assets://a2ui_icons/stop.png');
    icons.set('trendingUp',       'assets://a2ui_icons/trendingUp.png');
    icons.set('upload',           'assets://a2ui_icons/upload.png');
    icons.set('visibility',       'assets://a2ui_icons/visibility.png');
    icons.set('visibilityOff',    'assets://a2ui_icons/visibilityOff.png');
    icons.set('volumeDown',       'assets://a2ui_icons/volumeDown.png');
    icons.set('volumeMute',       'assets://a2ui_icons/volumeMute.png');
    icons.set('volumeOff',        'assets://a2ui_icons/volumeOff.png');
    icons.set('volumeUp',         'assets://a2ui_icons/volumeUp.png');
    icons.set('warning',          'assets://a2ui_icons/warning.png');
    icons.set(A2UI_ICON_FALLBACK_KEY, 'assets://a2ui_icons/_fallback.png');
    registerA2UIIcons(icons);
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

    // Full screen layout
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
